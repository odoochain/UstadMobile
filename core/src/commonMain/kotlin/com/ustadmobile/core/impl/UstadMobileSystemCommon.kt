package com.ustadmobile.core.impl

import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UstadMobileConstants.LANGUAGE_NAMES
import com.ustadmobile.core.util.UMFileUtil
import com.ustadmobile.core.view.UstadView
import kotlinx.io.InputStream
import org.kmp.io.KMPSerializerParser
import org.kmp.io.KMPXmlParser
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads

/**
 * Class has all the shared function across all supported platforms
 */
abstract class UstadMobileSystemCommon {

    /**
     * Returns whether or not the init method has already been run
     *
     * @return true if init has been called with a first context used to load certain resources,
     * false otherwise
     */
    private var isInitialized: Boolean = false

    //for testing purpose only
    var networkManager: Any? = null


    /**
     * The currently active locale
     */
    private var locale: String = ""

    internal data class LastGoToDest(val viewName: String, val args: Map<String, String?>)

    /**
     * Options that are used to control navigation
     */
    data class UstadGoOptions(
            /**
             * If not null, this functions the same as popUpTo on Android's NavController. E.g.
             * it will pop any view between the top of the stack and the given view name. If a
             * blank string is provided ( UstadView.CURRENT_DEST ), this means popup off the
             * current destination
             */
            val popUpToViewName: String? = null,

            /**
             * If true, then popup include popUpToViewName.
             */
            val popUpToInclusive: Boolean = false)

    /**
     * The last destination that was called via the go method. This is used for testing purposes.
     */
    internal var lastDestination: LastGoToDest? = null

    /**
     * Do any required startup operations: init will be called on creation
     *
     * This must make the shared content directory if it does not already exist
     */
    open fun init(context: Any) {
        UMLog.l(UMLog.DEBUG, 519, null)
        //We don't need to do init again
        if (isInitialized) {
            return
        }
        isInitialized = true
    }

    /**
     * Wrapper to retrieve preference keys from the system Manifest.
     *
     * On Android: uses meta-data elements on the application element in AndroidManifest.xml
     * On J2ME: uses the jad file
     *
     * @param key The key to lookup
     * @param context System context object
     *
     * @return The value of the manifest preference key if found, null otherwise
     */
    @JsName("getManifestPreference")
    abstract fun getManifestPreference(key: String, context: Any): String?


    /**
     * Return absolute path of the application setup file. Asynchronous.
     *
     * @param context System context
     * @param zip if true, the app setup file should be delivered within a zip.
     * @param callback callback to call when complete or if any error occurs.
     */

    @JsName("getAppSetupFile")
    abstract fun getAppSetupFile(context: Any, zip: Boolean, callback: UmCallback<*>)


    /**
     * Lookup a value from the app runtime configuration. These come from a properties file loaded
     * from the assets folder, the path of which is set by the manifest preference
     * com.sutadmobile.core.appconfig .
     *
     * @param key The config key to lookup
     * @param defaultVal The default value to return if the key is not found
     * @param context Systme context object
     *
     * @return The value of the key if found, if not, the default value provided
     */
    @JsName("getAppConfigString")
    abstract fun getAppConfigString(key: String, defaultVal: String?, context: Any): String?


    /**
     * Wrapper to retrieve preference keys from the system Manifest.
     *
     * On Android: uses meta-data elements on the application element in AndroidManifest.xml
     *
     * @param key The key to lookup
     * @param defaultVal The default value to return if the key is not found
     * @param context System context object
     *
     * @return The value of the manifest preference key if found, otherwise the default value
     */
    open fun getManifestPreference(key: String, defaultVal: String, context: Any): String {
        val `val` = getManifestPreference(key, context)
        return `val` ?: defaultVal
    }


    /**
     * Go to a new view : This is simply a convenience wrapper for go(viewName, args, context):
     * it will parse the a destination into the viewname and arguments, and then build a hashtable
     * to pass on.
     *
     * @param destination Destination name in the form of ViewName?arg1=val1&arg2=val2 etc.
     * @param context System context object
     */
    open fun go(destination: String?, context: Any) {
        val destinationParsed =
        if(destination?.contains(LINK_INTENT_FILTER) == true){
            val destinationIndex : Int? = destination.indexOf("/${LINK_INTENT_FILTER}").plus(10)

            val apiUrl = destination.substring(0, destination.indexOf("/${LINK_INTENT_FILTER}")) + '/'

            var charToAdd = "?"
            val sansApi = destination.substring(destinationIndex?:0+1?:0)?:""
            if(sansApi.contains('?') || sansApi.contains('&')){
                charToAdd = "&"
            }
            destination.substring(destinationIndex?:0) +
                    "${charToAdd}${UstadView.ARG_SERVER_URL}=$apiUrl"

        }else{
            destination
        }
        val destinationQueryPos = destinationParsed!!.indexOf('?')
        if (destinationQueryPos == -1) {
            go(destinationParsed, mapOf(), context)
        } else {
            go(destinationParsed.substring(0, destinationQueryPos), UMFileUtil.parseURLQueryString(
                    destinationParsed), context)
        }
    }

    open fun go(viewName: String, args: Map<String, String?>, context: Any) {
        go(viewName, args, context, 0, UstadGoOptions("", false))
    }

    open fun go(viewName: String, args: Map<String, String?>, context: Any, ustadGoOptions: UstadGoOptions) {
        go(viewName, args, context, 0, ustadGoOptions)
    }

    /**
     * The main method used to go to a new view. This is implemented at the platform level. On
     * Android this involves starting a new activity with the arguments being turned into an
     * Android bundle. On J2ME it creates a new Form and shows it, on iOS it looks up the related
     * UIViewController.
     *
     * @param viewName The name of the view to go to: This should match the view's interface .VIEW_NAME constant
     * @param args (Optional) Hahstable of arguments for the new view (e.g. catalog/container url etc)
     * @param context System context object
     */
    @JsName("go")
    abstract fun go(viewName: String, args: Map<String, String?>, context: Any, flags: Int,
                    ustadGoOptions: UstadGoOptions)

    /**
     * Provides the currently active locale
     *
     * @return The currently active locale code, or a blank "" string meaning the locale is the system default.
     */
    @JsName("getLocale")
    open fun getLocale(context: Any) = getAppPref(PREFKEY_LOCALE, LOCALE_USE_SYSTEM, context)

    @JsName("setLocale")
    fun setLocale(locale: String, context: Any) = setAppPref(PREFKEY_LOCALE, locale, context)


    /**
     * Get a preference for the app
     *
     * @param key preference key as a string
     * @return value of that preference
     */
    @JsName("getAppPref")
    abstract fun getAppPref(key: String, context: Any): String?

    /**
     * Set a preference for the app
     *
     * @param key preference that is being set
     * @param value value to be set
     */
    abstract fun setAppPref(key: String, value: String?, context: Any)

    /**
     * Get a preference for the app.  If not set, return the provided defaultVal
     *
     * @param key preference key as string
     * @param defaultVal default value to return if not set
     * @return value of the preference if set, defaultVal otherwise
     */
    open fun getAppPref(key: String, defaultVal: String, context: Any): String {
        val valFound = getAppPref(key, context)
        return valFound ?: defaultVal
    }

    /**
     * Must provide the system's default locale (e.g. en_US.UTF-8)
     *
     * @return System locale
     */
    @JsName("getSystemLocale")
    abstract fun getSystemLocale(context: Any): String


    /**
     * Provides the language code of the currently active locale. This is different to getLocale. If
     * the locale is currently set to LOCALE_USE_SYSTEM then that language will be resolved and the
     * code returned.
     *
     * @param context
     *
     * @return The locale as the user sees it.
     */
    open fun getDisplayedLocale(context: Any): String {
        var locale = getLocale(context)
        if (locale == LOCALE_USE_SYSTEM)
            locale = getSystemLocale(context)

        return locale.substring(0, 2)
    }

    /**
     * Get a string for use in the UI using a constant int from MessageID
     */
    @JsName("getString")
    abstract fun getString(messageCode: Int, context: Any): String

    /**
     * Get list of all UI supported languages
     */
    @JsName("getAllUiLanguage")
    @Deprecated("Use getAllUiLanguagesList instead")
    open fun getAllUiLanguage(context: Any): Map<String, String> {
        val languagesConfigVal = getAppConfigString(AppConfig.KEY_SUPPORTED_LANGUAGES,
                "", context) ?: throw IllegalStateException("No SUPPORTED LANGUAGES IN APPCONFIG!")
        val languageList = languagesConfigVal.split(",")
        return languageList.map { it to (LANGUAGE_NAMES[it] ?: it) }.toMap()
    }

    /**
     * Get a list of all languages available for the UI. This is a list of pairs in the form of
     * langcode, language display name. The first entry will always be empty constant which
     * tells the app to use the system default language.
     *
     * @param context
     */
    @JsName("getAllUiLanguagesList")
    open fun getAllUiLanguagesList(context: Any): List<Pair<String, String>> {
        val languagesConfigVal = getAppConfigString(AppConfig.KEY_SUPPORTED_LANGUAGES,
                "", context) ?: throw IllegalStateException("No SUPPORTED LANGUAGES IN APPCONFIG!")
        val availableLangs = languagesConfigVal.split(",").sorted()


        return listOf(LOCALE_USE_SYSTEM to getString(MessageID.use_device_language, context)) +
                availableLangs.map { it to (LANGUAGE_NAMES[it] ?: it) }
    }


    /**
     * Make a new instance of an XmlPullParser (e.g. Kxml).  This is added as a
     * method in the implementation instead of using the factory API because
     * it enables the J2ME version to use the minimal jar
     *
     * @return A new default options XmlPullParser
     */

    open fun newPullParser(): KMPXmlParser {
        return KMPXmlParser()
    }

    /**
     * Make a new instance of an XmlSerializer (org.xmlpull.v1.XmlSerializer)
     *
     * @return New instance of an XML Serializer
     */
    open fun newXMLSerializer(): KMPSerializerParser {
        return KMPSerializerParser()
    }

    /**
     * Make a new XmlPullParser from a given inputstream
     * @param in InputStream to read from
     * @param encoding Encoding to be used e.g. UTF-8
     *
     * @return a new XmlPullParser with set with the given inputstream
     */
    @JvmOverloads
    fun newPullParser(`in`: InputStream, encoding: String = UstadMobileConstants.UTF8): KMPXmlParser {
        UMLog.l(UMLog.DEBUG, 523, encoding)
        val xpp = newPullParser()
        xpp.setInput(`in`, encoding)
        return xpp
    }


    @JsName("getStorageDirAsync")
    abstract suspend fun getStorageDirsAsync(context: Any): List<UMStorageDir?>

    /**
     * Return the mime type for the given extension
     *
     * @param extension the extension without the leading .
     *
     * @return The mime type if none; or null if it's not known
     */
    open fun getMimeTypeFromExtension(extension: String): String? {
        return if (MIME_TYPES_REVERSE.containsKey(extension)) MIME_TYPES_REVERSE[extension] else null

    }

    /**
     * Return the extension of the given mime type
     *
     * @param mimeType The mime type
     *
     * @return File extension for the mime type without the leading .
     */
    open fun getExtensionFromMimeType(mimeType: String): String? {
        return if (MIME_TYPES.containsKey(mimeType)) {
            MIME_TYPES[mimeType]
        } else null

    }


    /**
     * Get a boolean from the app configuration. App config is stored as a string, so this is
     * converted to a boolean using Boolean.parseBoolean
     *
     * @param key The preference key to lookup
     * @param defaultVal The default value to return if the key is not found
     * @param context System context object
     * @return The boolean value of the given preference key if found, otherwise the default value
     */
    private fun getAppConfigBoolean(key: String, defaultVal: Boolean, context: Any): Boolean {
        val strVal = getAppConfigString(key, null, context)
        return strVal?.toBoolean() ?: defaultVal
    }

    /**
     * Get a boolean from the app configuration. App config is stored as a string, so this is
     * converted to a boolean using Boolean.parseBoolean
     *
     * @param key The preference key to lookup
     * @param context System viewContext object
     * @return The boolean value of the given preference key if found, otherwise false
     */
    fun getAppConfigBoolean(key: String, context: Any): Boolean {
        return getAppConfigBoolean(key, false, context)
    }


    /**
     * Get an integer from the app configuration.
     *
     * @param key The preference key to lookup
     * @param defaultVal The default value if the preference key is not found
     * @param context System context object
     * @return The integer value of the value if found, otherwise the default value
     */
    open fun getAppConfigInt(key: String, defaultVal: Int, context: Any): Int {
        return getAppConfigString(key, "" + defaultVal, context)!!.toInt()
    }

    /**
     * Determine if the two given locales are the same as far as what the user will see.
     *
     * @param oldLocale
     *
     * @return
     */
    open fun hasDisplayedLocaleChanged(oldLocale: String?, context: Any): Boolean {
        val currentlyDisplayedLocale = getDisplayedLocale(context)
        return !(currentlyDisplayedLocale != null && oldLocale != null
                && oldLocale.substring(0, 2) == currentlyDisplayedLocale.substring(0, 2))
    }

    protected fun getContentDirName(context: Any): String? {
        return getAppConfigString(AppConfig.KEY_CONTENT_DIR_NAME, DEFAULT_CONTENT_DIR_NAME, context)
    }

    fun scheduleChecks(context: Any) {

    }


    companion object {
        private val MIME_TYPES = mapOf("image/jpg" to "jpg", "image/jpg" to "jpg",
                "image/jpeg" to "jpg", "image/png" to "png", "image/gif" to "gif",
                "image/svg" to "svg", "application/epub+zip" to "epub")

        private val MIME_TYPES_REVERSE = MIME_TYPES.entries.associateBy({ it.value }) { it.key }

        /**
         * Suggested name to create for content on Devices
         */
        private const val DEFAULT_CONTENT_DIR_NAME = "ustadmobileContent"

        /**
         * The return value from getLocale when the user has said to use the system's locale
         */
        const val LOCALE_USE_SYSTEM = ""

        /**
         * The preference key where we save a string for the user's locale preference
         */
        @JsName("PREFKEY_LOCALE")
        const val PREFKEY_LOCALE = "locale"


        /**
         * Ported from old CatalogPresenter
         *
         * Save/retrieve resource from user specific directory
         */
        const val USER_RESOURCE = 2


        /**
         * Ported from old CatalogPresenter
         *
         * Save/retrieve resource from shared directory
         */
        const val SHARED_RESOURCE = 4

        const val ARG_REFERRER = "ref"

        /**
         * As per Android Intent.FLAG_ACTIVITY_SINGLE_TOP
         */
        const val GO_FLAG_SINGLE_TOP = 536870912

        /**
         * As per Android Intent.FLAG_CLEAR_TOP
         */
        const val GO_FLAG_CLEAR_TOP = 67108864

        const val TAG_DOWNLOAD_ENABLED = "dlenabled"

        const val TAG_MAIN_COROUTINE_CONTEXT = 16

        const val TAG_DLMGR_SINGLETHREAD_CONTEXT = 32

        const val TAG_LOCAL_HTTP_PORT = 64

        const val LINK_INTENT_FILTER = "umclient"

    }
}