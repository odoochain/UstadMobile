package com.ustadmobile.sharedse.network

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.GROUP_ALERT_SUMMARY
import androidx.core.app.NotificationManagerCompat
import com.ustadmobile.core.db.JobStatus
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.networkmanager.OnDownloadJobItemChangeListener
import com.ustadmobile.core.util.UMFileUtil
import com.ustadmobile.lib.db.entities.DownloadJobItemStatus
import com.ustadmobile.lib.util.copyOnWriteListOf
import com.ustadmobile.port.sharedse.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * This services monitors the download job statuses and act accordingly
 */
class DownloadNotificationService : Service(), OnDownloadJobItemChangeListener {

    private val mNetworkServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mNetworkServiceBound.set(true)

            networkManagerBle = (service as NetworkManagerBleAndroidService.LocalServiceBinder)
                    .service.networkManagerBle
            val channelNetworkManager = networkManagerBle!!
            networkManagerBle?.addDownloadChangeListener(this@DownloadNotificationService)
            val activeDownloadManagers = networkManagerBle?.activeDownloadJobItemManagers!!
            for (manager in activeDownloadManagers) {
                if (manager.rootItemStatus != null && manager.rootContentEntryUid == manager.rootItemStatus!!.contentEntryUid) {
                    onDownloadJobItemChange(manager.rootItemStatus, manager.downloadJobUid)
                }
            }


            GlobalScope.launch {
                for(jobNotifier in downloadJobPreparerChannel) {
                    val jobItemManager = channelNetworkManager.openDownloadJobItemManager(
                            jobNotifier.downloadJobUid)
                    if(jobItemManager != null) {
                        jobItemManager.awaitLoaded()
                        DownloadJobPreparer().prepare(jobItemManager, umAppDatabase, umAppDatabaseRepo)
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mNetworkServiceBound.set(false)
            if (networkManagerBle != null) {
                networkManagerBle = null
            }
        }
    }

    private val mNetworkServiceBound = AtomicBoolean(false)

    private var networkManagerBle: NetworkManagerBle? = null

    private var totalBytesToBeDownloaded: Long = 0

    private var totalBytesDownloadedSoFar: Long = 0

    private lateinit var mNotificationManager: NotificationManagerCompat

    private val notificationIdRef = AtomicInteger(9)

    private var timeLastUpdate: Long = 0

    private val MAX_UPDATE_TIME_DELAY = TimeUnit.SECONDS.toMillis(2)

    private lateinit var umAppDatabase: UmAppDatabase

    private lateinit var umAppDatabaseRepo: UmAppDatabase

    private lateinit var impl: UstadMobileSystemImpl

    private val serviceInstanceId = SERVICE_ID_COUNTER.incrementAndGet()

    private val stopped = AtomicBoolean(false)

    private var foregroundActive: Boolean = false

    private lateinit var downloadJobPreparerChannel: Channel<DownloadJobPreparerNotificationHolder>

    private var summaryNotificationHolder: SummaryNotificationHolder? = null

    private val activeDownloadJobNotifications: MutableList<DownloadJobNotificationHolder> = copyOnWriteListOf()

    open inner class NotificationHolder2(var contentTitle: String, var contentText: String,
                                         val notificationId : Int = notificationIdRef.incrementAndGet()) {

        val builder: NotificationCompat.Builder

        init {
            builder = createNotificationBuilder()
        }

        /**
         * Setup the notificationcompat.builde rwith common options required for all notifications we are using
         */
        private fun createNotificationBuilder(): NotificationCompat.Builder {
            val intent = Intent()
            val mNotificationPendingIntent = PendingIntent.getActivity(
                    this@DownloadNotificationService, 0, intent, 0)
            val builder = NotificationCompat.Builder(this@DownloadNotificationService,
                    NOTIFICATION_CHANNEL_ID)
            builder.setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .setWhen(System.currentTimeMillis())
                    //TODO: set the color
                    //.setColor(ContextCompat.getColor(this, R.color.primary))
                    .setOngoing(true)
                    .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                    .setAutoCancel(true)
                    .setContentIntent(mNotificationPendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setGroup(NOTIFICATION_GROUP_KEY)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.ic_file_download_white_24dp)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            }

            return builder
        }


        internal fun build(): Notification {
            val notification = builder.build()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notification.defaults = 0
                notification.sound = null
            }

            return notification
        }

        internal fun doNotify() {
            mNotificationManager.notify(notificationId, build())
        }

        /**
         * Create action buttons
         * @param downloadJobId Identifies the job on which buttons will appear
         * @param actionTag action tags for the prnding intent
         * @param actionLabel button label text
         * @return constructed action button
         */
        internal fun createAction(downloadJobId: Int, actionTag: String,
                                 actionLabel: String): NotificationCompat.Action {
            val actionIntent = Intent(this@DownloadNotificationService,
                    DownloadNotificationService::class.java)
            actionIntent.putExtra(EXTRA_DOWNLOADJOBUID, downloadJobId)
            actionIntent.action = actionTag
            val actionPendingIntent = PendingIntent.getService(this@DownloadNotificationService,
                    0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            return NotificationCompat.Action(0, actionLabel, actionPendingIntent)
        }
    }

    inner class DownloadJobNotificationHolder(val downloadJobUid: Int, notifyAfterInit: Boolean = true): NotificationHolder2(
            impl.getString(MessageID.loading, applicationContext),
            impl.getString(MessageID.waiting, applicationContext)) {

        var bytesSoFar: Long = 0
        var totalBytes: Long = 0

        init {
            builder.setProgress(MAX_PROGRESS_VALUE, 0, false)
                .addAction(createAction(downloadJobUid,
                        ACTION_CANCEL_DOWNLOAD, impl.getString(MessageID.download_cancel_label,
                        applicationContext)))
                .addAction(createAction(downloadJobUid,
                        ACTION_PAUSE_DOWNLOAD, impl.getString(MessageID.download_pause_download,
                        applicationContext)))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
            GlobalScope.launch {
                val downloadJobTitleInDb = umAppDatabase.downloadJobDao.getEntryTitleByJobUidAsync(downloadJobUid) ?: ""
                builder.setContentTitle(downloadJobTitleInDb)
                contentTitle = downloadJobTitleInDb
                if(notifyAfterInit)
                    doNotify()

            }
        }

        internal fun updateFromStatus(status: DownloadJobItemStatus?, doNotifyAfter: Boolean = true) {
            if(status == null)
                return

            if(status.status >= JobStatus.COMPLETE_MIN) {
                activeDownloadJobNotifications.remove(this)
                mNotificationManager.cancel(notificationId)
                GlobalScope.launch {
                    delay(2000)
                    if(isEmpty())
                        stopForegroundService()
                }
            }else {
                bytesSoFar = status.bytesSoFar
                totalBytes = status.totalBytes

                val progress = (status.bytesSoFar.toDouble() / status.totalBytes * 100).toInt()
                builder.setProgress(MAX_PROGRESS_VALUE, progress, false)
                builder.setContentText(String.format(impl.getString(
                        MessageID.download_downloading_placeholder, this@DownloadNotificationService),
                        UMFileUtil.formatFileSize(bytesSoFar),
                        UMFileUtil.formatFileSize(totalBytes)))

                if(doNotifyAfter)
                    doNotify()
            }
        }
    }


    inner class DownloadJobPreparerNotificationHolder(val downloadJobUid: Int): NotificationHolder2("Preparing", "Downloading Preparing") {
        init {
            //TODO
//            builder.setProgressAndTitles(this@DownloadNotificationService, contentTitle,
//                    contentText, downloadJobUid)
        }
    }


    inner class SummaryNotificationHolder(): NotificationHolder2(
            impl.getString(MessageID.downloading, applicationContext),
            impl.getString(MessageID.downloading, applicationContext)) {
        init {
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(contentTitle)
                .setSummaryText(contentText)
            builder.setGroupSummary(true)
                    .setStyle(inboxStyle)
        }

        fun updateSummary() {
            val summaryLabel = impl.getString(MessageID.download_downloading_placeholder,
                    applicationContext)

            val totalBytes = activeDownloadJobNotifications.fold(0L, {count, jobNotification ->
                count + jobNotification.totalBytes
            })
            val bytesSoFar = activeDownloadJobNotifications.fold(0L, {count, jobNotification ->
                count + jobNotification.bytesSoFar
            })

            contentTitle = String.format(impl.getString(MessageID.download_summary_title,
                    applicationContext), activeDownloadJobNotifications.size)
            contentText = String.format(summaryLabel,
                    UMFileUtil.formatFileSize(bytesSoFar),
                    UMFileUtil.formatFileSize(totalBytes))

            builder.setSubText(contentTitle)
            doNotify()
        }

    }

    override fun onCreate() {
        super.onCreate()
        mNotificationManager = NotificationManagerCompat.from(this)
        createChannel()

        umAppDatabase = UmAppDatabase.getInstance(this)
        umAppDatabaseRepo = UmAccountManager.getRepositoryForActiveAccount(this)
        //bind to network service
        val networkServiceIntent = Intent(applicationContext,
                NetworkManagerBleAndroidService::class.java)
        bindService(networkServiceIntent, mNetworkServiceConnection, Context.BIND_AUTO_CREATE)

        impl = UstadMobileSystemImpl.instance

        downloadJobPreparerChannel = Channel(capacity = Channel.UNLIMITED)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val intentAction = intent.action
        val intentExtras = intent.extras

        if(intentAction == null)
            return START_STICKY

        //TODO: for pre-N (non grouped) notifications: should we call startForeground more than once?
        if(intentAction in listOf(ACTION_DOWNLOADJOBITEM_STARTED, ACTION_PREPARE_DOWNLOAD) && !foregroundActive) {
            if(canCreateGroupedNotification()) {
                summaryNotificationHolder = SummaryNotificationHolder().also {
                    startForeground(it.notificationId, it.build())
                }

                foregroundActive = true
            }
        }

        when(intentAction) {
            ACTION_PREPARE_DOWNLOAD -> {
                val downloadJobUid = intentExtras?.getInt(EXTRA_DOWNLOADJOBUID) ?: 0
                val downloadJobPreparationHolder = DownloadJobPreparerNotificationHolder(downloadJobUid)
                downloadJobPreparerChannel.offer(downloadJobPreparationHolder)
            }

            ACTION_DOWNLOADJOBITEM_STARTED -> {
                val downloadJobUid = intentExtras?.getInt(EXTRA_DOWNLOADJOBUID) ?: -1
                var downloadJobNotificationHolder = activeDownloadJobNotifications
                        .firstOrNull { it.downloadJobUid == downloadJobUid }
                if(downloadJobNotificationHolder == null) {
                    downloadJobNotificationHolder = DownloadJobNotificationHolder(downloadJobUid)
                    activeDownloadJobNotifications.add(downloadJobNotificationHolder)
                    downloadJobNotificationHolder.doNotify()
                }
            }
        }

        return START_STICKY
    }

    private fun isEmpty() : Boolean = activeDownloadJobNotifications.isEmpty()

    @Synchronized
    override fun onDownloadJobItemChange(status: DownloadJobItemStatus?, downloadJobUid: Int) {
        activeDownloadJobNotifications.filter { it.downloadJobUid == downloadJobUid }.forEach {
            val downloadJobItemManager = networkManagerBle?.getDownloadJobItemManager(downloadJobUid)
            it.updateFromStatus(downloadJobItemManager?.rootItemStatus)
        }
        summaryNotificationHolder?.updateSummary()
    }

    /**
     * Create a channel for the notification
     */
    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val mNotificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
        mNotificationChannel.vibrationPattern = longArrayOf(0)
        mNotificationChannel.enableVibration(true)
        mNotificationChannel.setSound(null, null)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(mNotificationChannel)
    }


    /**
     * Stop foreground service
     */
    private fun stopForegroundService() {
        if (!stopped.getAndSet(true)) {
            foregroundActive = false
            networkManagerBle?.removeDownloadChangeListener(this)

            //downloadJobIdToNotificationMap.clear()
            stopForeground(true)
            stopSelf()
        }
    }


    @ExperimentalCoroutinesApi
    override fun onDestroy() {
        super.onDestroy()
        if (mNetworkServiceBound.get())
            unbindService(mNetworkServiceConnection)

        if(!downloadJobPreparerChannel.isClosedForSend) {
            downloadJobPreparerChannel.close()
        }
    }

    private fun canCreateGroupedNotification(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    companion object {

        const val ACTION_PAUSE_DOWNLOAD = "ACTION_PAUSE_DOWNLOAD"

        const val ACTION_CANCEL_DOWNLOAD = "ACTION_CANCEL_DOWNLOAD"

        const val ACTION_PREPARE_DOWNLOAD = "ACTION_PREPARE_DOWNLOAD"

        const val ACTION_DOWNLOADJOBITEM_STARTED = "ACTION_DOWNLOADJOBITEM_STARTED"

        const val EXTRA_DOWNLOADJOBUID = "EXTRA_DOWNLOADJOBUID"

        const val EXTRA_DOWNLOADJOBITEMUID = "EXTRA_DOWNLOADJOBITEMUID"

        const val NOTIFICATION_CHANNEL_ID = "UM_NOTIFICATION_CHANNEL_ID"

        const val NOTIFICATION_GROUP_KEY = "com.android.example.UstadMobile"

        const val MAX_PROGRESS_VALUE = 100

        private val SERVICE_ID_COUNTER = AtomicInteger(0)
    }
}
