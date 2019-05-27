package com.ustadmobile.lib.db.entities

import android.arch.persistence.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ustadmobile.lib.database.annotation.UmEntity
import com.ustadmobile.lib.database.annotation.UmIndexField
import com.ustadmobile.lib.database.annotation.UmPrimaryKey

@UmEntity
@Entity
open class ContainerEntry() {

    @UmPrimaryKey(autoIncrement = true)
    @PrimaryKey(autoGenerate = true)
    var ceUid: Long = 0

    @UmIndexField
    @ColumnInfo(index = true)
    var ceContainerUid: Long = 0

    var cePath: String? = null

    var ceCefUid: Long = 0

    constructor(cePath: String, container: Container, entryFile: ContainerEntryFile) : this() {
        this.cePath = cePath
        this.ceCefUid = entryFile.cefUid
        this.ceContainerUid = container.containerUid
    }
}
