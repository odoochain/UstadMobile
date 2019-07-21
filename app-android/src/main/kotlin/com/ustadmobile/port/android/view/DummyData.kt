package com.ustadmobile.port.android.view

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.dao.*
import com.ustadmobile.core.db.dao.PersonAuthDao.Companion.ENCRYPTED_PASS_PREFIX
import com.ustadmobile.lib.db.entities.*
import com.ustadmobile.lib.util.encryptPassword
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException

class DummyData {

    private var personDao: PersonDao? = null
    private var locationDao: LocationDao? = null
    private var personAuthDao: PersonAuthDao? = null
    private var saleProductDao: SaleProductDao? = null
    private var productParentJoinDao: SaleProductParentJoinDao? = null
    private var repo: UmAppDatabase? = null
    private var context:Any ?= null

    constructor(theContext:Any, theRepo:UmAppDatabase){
        this.context = theContext
        this.repo = theRepo
    }

    fun loadInitialData() {

        personDao = repo!!.personDao
        locationDao = repo!!.locationDao
        personAuthDao = repo!!.personAuthDao
        saleProductDao = repo!!.saleProductDao
        productParentJoinDao = repo!!.saleProductParentJoinDao
        
        //Any data goes here.

        //Create Admin
        var adminPerson = personDao!!.findByUsername("admin")
        if (adminPerson == null) {
            adminPerson = Person()
            adminPerson.admin = true
            adminPerson.username = "admin"
            adminPerson.firstNames = "Admin"
            adminPerson.lastName = "Admin"
            adminPerson.active = true

            adminPerson.personUid = personDao!!.insert(adminPerson)

            val adminPersonAuth = PersonAuth(adminPerson.personUid,
                    ENCRYPTED_PASS_PREFIX +
                            encryptPassword("golDoz1"))
            GlobalScope.launch {
                personAuthDao!!.insertAsync(adminPersonAuth)
                //Admin created.
                println("ServletContextClass: Admin created. Continuing..")
                addDummyData()
            }

        } else {
            println("ServletContextClass: Admin Already created. Continuing..")
        }
    }

    private fun addDummyData() {

        //Create a new location
        GlobalScope.launch {
            val result = locationDao!!.findByTitleAsync("Test location")

            val newLocation: Location
            if (result!!.isEmpty()) {
                newLocation = Location()
                newLocation.title = "Test location"
                newLocation.parentLocationUid = 0
                newLocation.locationActive = false
                newLocation.description = "Test location added from Dummy data"
                newLocation.locationUid = locationDao!!.insert(newLocation)

                //Add more locations

                try {
                    //AFGHANISTAN
                    val afg = Location(
                            String("افغانستان".toByteArray(Charsets.UTF_8)),
                            "Afghanistan whole region", true)
                    val afgLocationUid = locationDao!!.insert(afg)

                    //CENTRAL AFGHANISTAN
                    val centralAfg = Location(
                            String("(Central Afghanistan)".toByteArray(Charsets.UTF_8)),
                            "Center region", true, afgLocationUid)
                    val centralAfgLocationUid = locationDao!!.insert(centralAfg)

                    locationDao!!.insert(Location(
                            String("کابل (Kabul)".toByteArray(Charsets.UTF_8)),
                            "Kabul area", true, centralAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("بامیان (Bamyan)".toByteArray(Charsets.UTF_8)),
                            "Bamyan area", true, centralAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Kapisa) كابيسا ".toByteArray(Charsets.UTF_8)),
                            "Kapisa area", true, centralAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Logar) لوغار ".toByteArray(Charsets.UTF_8)),
                            "Logar area", true, centralAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Parwan) پروان ".toByteArray(Charsets.UTF_8)),
                            "Parwan area", true, centralAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Urozgan) اروزكان ".toByteArray(Charsets.UTF_8)),
                            "Urozgan area", true, centralAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Wardak) وردک".toByteArray(Charsets.UTF_8)),
                            "Wardak area", true, centralAfgLocationUid))

                    //EASTERN AFGHANISTAN
                    val easternAfg = Location(
                            String("(Eastern Afghanistan)".toByteArray(Charsets.UTF_8)),
                            "Eastern region", true, afgLocationUid)
                    val easternAfgUid = locationDao!!.insert(easternAfg)
                    locationDao!!.insert(Location(
                            String(" (Laghman) لغمان ".toByteArray(Charsets.UTF_8)),
                            "Laghman area", true, easternAfgUid))
                    locationDao!!.insert(Location(
                            String("(Nangarhar) ننګرهار ".toByteArray(Charsets.UTF_8)),
                            "Nangarhar area", true, easternAfgUid))

                    //NORTH EAST AFGHANISTAN
                    val northEastAfg = Location(
                            String(" (North East Afghanistan)".toByteArray(Charsets.UTF_8)),
                            "Northern region", true, afgLocationUid)
                    val northEastAfgLocationUid = locationDao!!.insert(northEastAfg)
                    locationDao!!.insert(Location(
                            String(" (Badakhshan  Province) بدخشان".toByteArray(Charsets.UTF_8)),
                            "Badakhshan  area", true, northEastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String(" (Baghlan Province) بغلان".toByteArray(Charsets.UTF_8)),
                            "Baghlan area", true, northEastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String(" (Kunar Province) کونړ".toByteArray(Charsets.UTF_8)),
                            "Kunar area", true, northEastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Kunduz Province) کندوز".toByteArray(Charsets.UTF_8)),
                            "Kunduz area", true, northEastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String(" (Nuristan Province) نورستان".toByteArray(Charsets.UTF_8)),
                            "Nuristan area", true, northEastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Panjshir Province) پنجشیر".toByteArray(Charsets.UTF_8)),
                            "Panjshir area", true, northEastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Takhar Province) تخار".toByteArray(Charsets.UTF_8)),
                            "Takhar area", true, northEastAfgLocationUid))

                    //NORTH WEST AFGHANISTAN
                    val northWestAfg = Location(
                            String("(North East Afghanistan)".toByteArray(Charsets.UTF_8)),
                            "Northern region", true, afgLocationUid)
                    val northWestAfgLocationUid = locationDao!!.insert(northWestAfg)
                    locationDao!!.insert(Location(
                            String("(Balkh  Province) بلخ".toByteArray(Charsets.UTF_8)),
                            "Balkh  area", true, northWestAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Faryab  Province) فارياب".toByteArray(Charsets.UTF_8)),
                            "Faryab  area", true, northWestAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Jowzjan  Province) جوزجان".toByteArray(Charsets.UTF_8)),
                            "Jowzjan area", true, northWestAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Samangan  Province) سمنگان".toByteArray(Charsets.UTF_8)),
                            "Samangan  area", true, northWestAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Sar-e Pol  Province) سرپل".toByteArray(Charsets.UTF_8)),
                            "Sar-e Pol  area", true, northWestAfgLocationUid))


                    //SOUTHEAST AFGHANISTAN
                    val southeastAfg = Location(
                            String("(Southeast Afghanistan)".toByteArray(Charsets.UTF_8)),
                            "Southeast region", true, afgLocationUid)
                    val southeastAfgLocationUid = locationDao!!.insert(southeastAfg)
                    locationDao!!.insert(Location(
                            String("(Khost Province) خوست".toByteArray(Charsets.UTF_8)),
                            "Khost area", true, southeastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Paktika Province) پکتیکا".toByteArray(Charsets.UTF_8)),
                            "Paktika area", true, southeastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Ghazni Province) غزنى".toByteArray(Charsets.UTF_8)),
                            "Ghazni area", true, southeastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Kandahar Province) کندهار ".toByteArray(Charsets.UTF_8)),
                            "Kandahar area", true, southeastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Pakika Province) پکتیا".toByteArray(Charsets.UTF_8)),
                            "Pakika area", true, southeastAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Zabul Province) زابل".toByteArray(Charsets.UTF_8)),
                            "Zabul area", true, southeastAfgLocationUid))

                    //SOUTHWEST AFGHANISTAN
                    val southWestAfg = Location(
                            String("(Southwest Afghanistan)".toByteArray(Charsets.UTF_8)),
                            "Southwest region", true, afgLocationUid)
                    val southWestAfgLocationUid = locationDao!!.insert(southWestAfg)
                    locationDao!!.insert(Location(
                            String("(Daykundi Province) دایکندی".toByteArray(Charsets.UTF_8)),
                            "Daykundi area", true, southWestAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Helmand Province) هلمند".toByteArray(Charsets.UTF_8)),
                            "Helmand area", true, southWestAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Nimruz Province) نيمروز".toByteArray(Charsets.UTF_8)),
                            "Nimruz area", true, southWestAfgLocationUid))


                    //WESTERN AFGHANISTAN
                    val westernAfg = Location(
                            "(Western Afghanistan)", "Western region", true, afgLocationUid)
                    val westernAfgLocationUid = locationDao!!.insert(westernAfg)
                    locationDao!!.insert(Location(
                            String("(Herat Province) هرات".toByteArray(Charsets.UTF_8)),
                            "Herat area", true, westernAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Farah Province) فارا".toByteArray(Charsets.UTF_8)),
                            "Farah area", true, westernAfgLocationUid))
                    locationDao!!.insert(Location(
                            String("(Ghor Province) غور".toByteArray(Charsets.UTF_8)),
                            "Ghor area", true, westernAfgLocationUid))

                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                //Also add some Producers (People)

                val person1 = Person("royarahimi", "Roya",
                        "Rahimi")
                val person2 = Person("lailagulzar", "Laila",
                        "Gulzar")
                val person3 = Person("meenahotaki", "Meena",
                        "Hotaki")
                val person4 = Person("nargisyousafzi", "Nargis",
                        "Yousafzi")

                person1.active = true
                person2.active = true
                person3.active = true
                person4.active = true

                personDao!!.insert(person1)
                personDao!!.insert(person2)
                personDao!!.insert(person3)
                personDao!!.insert(person4)

                addSaleProducts()
            }

        }
    }

    private fun addSaleProducts() {

        //Insert products
        val pinkHatName = "Pink Hat"
        GlobalScope.launch{
            var pinkHatProduct = saleProductDao!!.findByNameAsync(pinkHatName)

            if (pinkHatProduct == null) {
                pinkHatProduct = SaleProduct(pinkHatName, "Testing dummy data")
                pinkHatProduct.saleProductUid = saleProductDao!!.insert(pinkHatProduct)
                addPicToProduct(pinkHatProduct,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_pink_hats.png", repo!!)

                //Create other products
                val greenSheets = SaleProduct("Green Sheet", "Testing dummy data")
                greenSheets.saleProductUid = saleProductDao!!.insert(greenSheets)
                addPicToProduct(greenSheets,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_green_sheets.png", repo!!)

                val floralScarves = SaleProduct("Floral Scarf", "Dummy data")
                floralScarves.saleProductUid = saleProductDao!!.insert(floralScarves)
                addPicToProduct(floralScarves,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_floral_scarves.png", repo!!)

                val pinkBedLinen = SaleProduct("Pink Bed Linen", "Dummy data")
                pinkBedLinen.saleProductUid = saleProductDao!!.insert(pinkBedLinen)
                addPicToProduct(pinkBedLinen,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_bed_linen.png", repo!!)

                val redTableLinen = SaleProduct("Red Table Linen", "Dummy data")
                redTableLinen.saleProductUid = saleProductDao!!.insert(redTableLinen)
                addPicToProduct(redTableLinen,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_table_linen.png", repo!!)

                val zariToy = SaleProduct("Zari Bagchesimsim", "Dummy data")
                zariToy.saleProductUid = saleProductDao!!.insert(zariToy)
                addPicToProduct(zariToy,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_toys.png", repo!!)

                val womenSuit = SaleProduct("Women Suit", "Dummy data")
                womenSuit.saleProductUid = saleProductDao!!.insert(womenSuit)
                addPicToProduct(womenSuit,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_women.png", repo!!)

                val mensBelt = SaleProduct("Men Belt", "Dummy data")
                mensBelt.saleProductUid = saleProductDao!!.insert(mensBelt)
                addPicToProduct(mensBelt,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_men.png", repo!!)

                val beigeClutch = SaleProduct("Beige Clutch", "Dummy data")
                beigeClutch.saleProductUid = saleProductDao!!.insert(beigeClutch)
                addPicToProduct(beigeClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_accessories.png", repo!!)


                val brownBag = SaleProduct("Brown Bag", "Dummy data")
                brownBag.saleProductUid = saleProductDao!!.insert(brownBag)
                addPicToProduct(brownBag,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_bag.jpg", repo!!)


                val blueClutch = SaleProduct("Blue Clutch", "Dummy data")
                blueClutch.saleProductUid = saleProductDao!!.insert(blueClutch)
                addPicToProduct(blueClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_blue_clutch.png", repo!!)

                val bowKeychain = SaleProduct("Bow Keychain", "Dummy data")
                bowKeychain.saleProductUid = saleProductDao!!.insert(bowKeychain)
                addPicToProduct(bowKeychain,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_bow_keychain.jpeg", repo!!)

                val keychain = SaleProduct("Keychain", "Dummy data")
                keychain.saleProductUid = saleProductDao!!.insert(keychain)
                addPicToProduct(keychain,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_keychain.jpg", repo!!)

                val keychains = SaleProduct("Keychains", "Dummy data")
                keychains.saleProductUid = saleProductDao!!.insert(keychains)
                addPicToProduct(keychains,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_keychains.jpg", repo!!)

                val lightBlueClutch = SaleProduct("Light Blue Clutch", "Dummy data")
                lightBlueClutch.saleProductUid = saleProductDao!!.insert(lightBlueClutch)
                addPicToProduct(lightBlueClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_light_blue_clutch.png", repo!!)

                val metallicClutch = SaleProduct("Metallic Clutch", "Dummy data")
                metallicClutch.saleProductUid = saleProductDao!!.insert(metallicClutch)
                addPicToProduct(metallicClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_metallic_clutch.png", repo!!)

                val rubyClutch = SaleProduct("Ruby Clutch", "Dummy data")
                rubyClutch.saleProductUid = saleProductDao!!.insert(rubyClutch)
                addPicToProduct(rubyClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_ruby_clutch.png", repo!!)

                val rubyPurse = SaleProduct("Ruby purse", "Dummy data")
                rubyPurse.saleProductUid = saleProductDao!!.insert(rubyPurse)
                addPicToProduct(rubyPurse,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_ruby_purse.png", repo!!)

                val rubyScarf = SaleProduct("Ruby Scarf", "Dummy data")
                rubyScarf.saleProductUid = saleProductDao!!.insert(rubyScarf)
                addPicToProduct(rubyScarf,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_ruby_scarf.jpg", repo!!)

                val laceholderRubyScarf = SaleProduct("Laceholder Ruby Scarf", "Dummy data")
                laceholderRubyScarf.saleProductUid = saleProductDao!!.insert(laceholderRubyScarf)
                addPicToProduct(laceholderRubyScarf,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_scarves.jpg", repo!!)

                val turquoiseClutch = SaleProduct("Turquoise Clutch", "Dummy data")
                turquoiseClutch.saleProductUid = saleProductDao!!.insert(turquoiseClutch)
                addPicToProduct(turquoiseClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_turquoise_clutch.png", repo!!)

                val turquoisePurse = SaleProduct("Turquoise Purse", "Dummy data")
                turquoisePurse.saleProductUid = saleProductDao!!.insert(turquoisePurse)
                addPicToProduct(turquoisePurse,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_turquoise_purse .png", repo!!)

                val yellowClutch = SaleProduct("Yellow Clutch", "Dummy data")
                yellowClutch.saleProductUid = saleProductDao!!.insert(yellowClutch)
                addPicToProduct(yellowClutch,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_yellow_clutch.png", repo!!)

                val yellowPurse = SaleProduct("Yellow Purse", "Dummy data")
                yellowPurse.saleProductUid = saleProductDao!!.insert(yellowPurse)
                addPicToProduct(yellowPurse,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_yellow_purse.png", repo!!)


                //Create categories (Joins) (NEW)

                val bedLinenCategry = SaleProduct("Bed Linen", "Dummy data", true)
                bedLinenCategry.saleProductUid = saleProductDao!!.insert(bedLinenCategry)
                addPicToProduct(bedLinenCategry,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_bed_linen.png", repo!!)

                val toysCategory = SaleProduct("Toys", "Dummy data", true)
                toysCategory.saleProductUid = saleProductDao!!.insert(toysCategory)
                addPicToProduct(toysCategory,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_toys.png", repo!!)

                val accessoriesCategory = SaleProduct("Accessories", "Dummy data", true)
                accessoriesCategory.saleProductUid = saleProductDao!!.insert(accessoriesCategory)
                addPicToProduct(accessoriesCategory,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_accessories.png", repo!!)


                //Create joins to categories (new)
                val bedLinenJoin = SaleProductParentJoin(pinkBedLinen.saleProductUid,
                        bedLinenCategry.saleProductUid, true)
                bedLinenJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(bedLinenJoin)

                val accessoriesJoin = SaleProductParentJoin(beigeClutch.saleProductUid,
                        accessoriesCategory.saleProductUid, true)
                accessoriesJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(accessoriesJoin)

                val toysJoin = SaleProductParentJoin(zariToy.saleProductUid,
                        toysCategory.saleProductUid, true)
                toysJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(toysJoin)

                //two level categories
                val categoryclutches = SaleProduct("Clutches", "All clutches", true)
                categoryclutches.saleProductUid = saleProductDao!!.insert(categoryclutches)
                addPicToProduct(categoryclutches, "/WEB-INF/goldozi/goldozi_product_placeholder_ruby_clutch.png", repo!!)

                val pursesCategory = SaleProduct("Purses", "All purses", true)
                pursesCategory.saleProductUid = saleProductDao!!.insert(pursesCategory)
                addPicToProduct(pursesCategory, "/WEB-INF/goldozi/goldozi_product_placeholder_ruby_purse.png", repo!!)

                val scarvesCategory = SaleProduct("Scarves", "All scarves", true)
                scarvesCategory.saleProductUid = saleProductDao!!.insert(scarvesCategory)
                addPicToProduct(scarvesCategory, "/WEB-INF/goldozi/goldozi_product_placeholder_ruby_scarf.jpg", repo!!)

                val clutchAccessoriesJoin = SaleProductParentJoin(categoryclutches.saleProductUid,
                        accessoriesCategory.saleProductUid, true)
                clutchAccessoriesJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(clutchAccessoriesJoin)

                val pursesAccessoriesJoin = SaleProductParentJoin(pursesCategory.saleProductUid,
                        accessoriesCategory.saleProductUid, true)
                pursesAccessoriesJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(pursesAccessoriesJoin)

                val scarfAccessoriesJoin = SaleProductParentJoin(scarvesCategory.saleProductUid,
                        accessoriesCategory.saleProductUid, true)
                scarfAccessoriesJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(scarfAccessoriesJoin)

                //Create collections new
                val womenCategory = SaleProduct("Women", "All women products", true)
                womenCategory.saleProductUid = saleProductDao!!.insert(womenCategory)
                addPicToProduct(womenCategory, "/WEB-INF/goldozi/goldozi_product_placeholder_women.png", repo!!)

                val menCategory = SaleProduct("Men", "All men products", true)
                menCategory.saleProductUid = saleProductDao!!.insert(menCategory)
                addPicToProduct(menCategory, "/WEB-INF/goldozi/goldozi_product_placeholder_men.png", repo!!)

                val suitWomenJoin = SaleProductParentJoin(womenSuit.saleProductUid,
                        womenCategory.saleProductUid, true)
                suitWomenJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(suitWomenJoin)

                val beltMenJoin = SaleProductParentJoin(mensBelt.saleProductUid,
                        menCategory.saleProductUid, true)
                beltMenJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(beltMenJoin)


                //Create collection (new)
                val collectionCategory = SaleProduct("Collection",
                        "All collections", true)
                collectionCategory.saleProductUid = saleProductDao!!.insert(collectionCategory)
                addPicToProduct(collectionCategory,
                        "/WEB-INF/goldozi/goldozi_product_placeholder_accessories.png", repo!!)

                val womenCategoryCollectionJoin = SaleProductParentJoin(womenCategory.saleProductUid,
                        collectionCategory.saleProductUid, true)
                womenCategoryCollectionJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(womenCategoryCollectionJoin)

                val menCategoryCollectionJoin = SaleProductParentJoin(menCategory.saleProductUid,
                        collectionCategory.saleProductUid, true)
                menCategoryCollectionJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(menCategoryCollectionJoin)

                val accessoriesCategoryCollectionJoin = SaleProductParentJoin(accessoriesCategory.saleProductUid,
                        collectionCategory.saleProductUid, true)
                accessoriesCategoryCollectionJoin.saleProductParentJoinUid = productParentJoinDao!!.insert(accessoriesCategoryCollectionJoin)

            }
        }

    }

    private fun addPicToProduct(product: SaleProduct, assetPath: String, repo: UmAppDatabase) {
        //Create picture entry
        val pictureDao = repo.saleProductPictureDao
        val productPicture = SaleProductPicture()
        productPicture.saleProductPictureSaleProductUid = product.saleProductUid
        productPicture.saleProductPictureTimestamp = System.currentTimeMillis()

        GlobalScope.launch {
            val productPictureUid = pictureDao!!.insertAsync(productPicture)
            val assetIS: InputStream?
            try {

                //TODO: KMP
//                assetIS = context.getResourceAsStream(assetPath)
//                pictureDao!!.setAttachment(productPictureUid, assetIS)
//                if (assetIS == null) {
//                    println("Null Image stream. Check url")
//                } else {
//                    println("Added SaleProductPic OK.")
//                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (ee: Exception) {
                println("Unable to process: " + product.saleProductName!!)
                ee.printStackTrace()
            }
        }


    }


}