package com.ustadmobile.lib.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ustadmobile.door.annotation.LastChangedBy
import com.ustadmobile.door.annotation.LocalChangeSeqNum
import com.ustadmobile.door.annotation.MasterChangeSeqNum
import com.ustadmobile.door.annotation.SyncableEntity
import kotlinx.serialization.Serializable

@Entity
@SyncableEntity(tableId = 208)
@Serializable
open class Comments() {

    @PrimaryKey(autoGenerate = true)
    var commentsUid: Long = 0

    var commentsText: String? = null

    //Table name
    var commentsEntityType : Int = 0

    var commentsEntityUid : Long = 0

    var commentsPublic: Boolean = false

    var commentsStatus: Int = COMMENTS_STATUS_APPROVED

    var commentsPersonUid : Long = 0

    var commentsFlagged : Boolean = false

    var commentsInActive : Boolean = false

    var commentsDateTimeAdded : Long = 0

    var commentsDateTimeUpdated: Long = 0

    @MasterChangeSeqNum
    var commentsMCSN: Long = 0

    @LocalChangeSeqNum
    var commentsLCSN: Long = 0

    @LastChangedBy
    var commentsLCB: Int = 0

    constructor(table: Int, uid: Long, personUid: Long, now: Long, comment: String, isPublic: Boolean) : this() {
        commentsText = comment
        commentsEntityType = table
        commentsEntityUid = uid
        commentsPublic = isPublic
        commentsPersonUid = personUid
        commentsDateTimeAdded = now

    }

    companion object {
        const val COMMENTS_STATUS_APPROVED = 0
        const val COMMENTS_STATUS_PENDING = 1
        const val COMMENTS_STATUS_REJECTED = 2
        const val COMMENTS_STATUS_INAPPROPRIATE_REPORTED = 4
    }


}