package com.ustadmobile.port.sharedse.networkmanager

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.dao.DownloadJobItemDao
import com.ustadmobile.core.impl.UMLog
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.networkmanager.DownloadJobItemManager
import com.ustadmobile.lib.db.entities.Container
import com.ustadmobile.lib.db.entities.ContentEntryStatus
import com.ustadmobile.lib.db.entities.DownloadJobItem
import com.ustadmobile.lib.db.entities.DownloadJobItemParentChildJoin
import com.ustadmobile.lib.util.UMUtil

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * This runnable sets up a download job so it's ready to run. It starts from a root content entry uid,
 * and then adds all
 */
class DownloadJobPreparer(private val jobItemManager: DownloadJobItemManager, private val appDatabase: UmAppDatabase,
                          private val appDatabaseRepo: UmAppDatabase) : Runnable {

    override fun run() {
        val startTime = System.currentTimeMillis()
        val downloadJobUid = jobItemManager.downloadJobUid
        val contentEntryUid = jobItemManager.rootContentEntryUid

        UMLog.l(UMLog.DEBUG, 420, "DownloadJobPreparer: start " +
                "entry uid = " + contentEntryUid + " download job uid = " + downloadJobUid)

        var numItemsCreated = 0

        val jobItemDao = appDatabase.downloadJobItemDao
        var childItemsToCreate: List<DownloadJobItemDao.DownloadJobItemToBeCreated2>
        val rootEntryContainer = appDatabaseRepo.containerDao
                .getMostRecentContainerForContentEntry(contentEntryUid)
        val rootDownlaodJobItem = DownloadJobItem(
                jobItemManager.downloadJobUid.toLong(), contentEntryUid,
                rootEntryContainer?.containerUid ?: 0,
                rootEntryContainer?.fileSize ?: 0)
        jobItemManager.insertDownloadJobItemsSync(listOf(rootDownlaodJobItem))

        val contentEntryUidToDjiUidMap = HashMap<Long, Long>()
        val parentUids = ArrayList<Long>()
        parentUids.add(contentEntryUid)
        contentEntryUidToDjiUidMap[contentEntryUid] = rootDownlaodJobItem.djiUid

        val createdJoinCepjUids = HashSet<Long>()

        appDatabase.contentEntryStatusDao.insertOrAbort(listOf(ContentEntryStatus(contentEntryUid,
                rootEntryContainer == null,
                rootEntryContainer?.fileSize ?: 0)))

        val statusList = LinkedList<ContentEntryStatus>()
        do {
            statusList.clear()
            childItemsToCreate = jobItemDao.findByParentContentEntryUuids(parentUids)
            UMLog.l(UMLog.DEBUG, 420, "DownloadJobPreparer: found " +
                    childItemsToCreate.size + " child items on from parents " +
                    UMUtil.debugPrintList(parentUids))

            parentUids.clear()

            for (child in childItemsToCreate) {
                if (!contentEntryUidToDjiUidMap.containsKey(child.contentEntryUid)) {
                    val newItem = DownloadJobItem(downloadJobUid.toLong(),
                            child.contentEntryUid, child.containerUid, child.fileSize)
                    jobItemManager.insertDownloadJobItemsSync(listOf(newItem))
                    numItemsCreated++
                    statusList.add(ContentEntryStatus(child.contentEntryUid,
                            child.fileSize > 0, child.fileSize))

                    contentEntryUidToDjiUidMap[child.contentEntryUid] = newItem.djiUid

                    if (newItem.djiContainerUid == 0L)
                    //this item is a branch, not a leaf if containeruid = 0
                        parentUids.add(child.contentEntryUid)

                    statusList.add(ContentEntryStatus(child.contentEntryUid,
                            child.fileSize > 0, child.fileSize))

                }

                if (!createdJoinCepjUids.contains(child.cepcjUid)) {
                    jobItemManager.insertParentChildJoins(listOf(DownloadJobItemParentChildJoin(
                            contentEntryUidToDjiUidMap[child.parentEntryUid],
                            contentEntryUidToDjiUidMap[child.contentEntryUid],
                            child.cepcjUid)), null)
                    createdJoinCepjUids.add(child.cepcjUid)
                }
            }

            appDatabase.contentEntryStatusDao.insertOrAbort(statusList)
        } while (!parentUids.isEmpty())
        UMLog.l(UMLog.VERBOSE, 420, "Created " + numItemsCreated +
                " items. Time to prepare download job: " +
                (System.currentTimeMillis() - startTime) + "ms")
        val latch = CountDownLatch(1)
        jobItemManager.commit({ aVoid -> latch.countDown() })
        try {
            latch.await(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
        }

    }
}
