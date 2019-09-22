package com.ustadmobile.lib.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ustadmobile.door.annotation.LastChangedBy
import com.ustadmobile.door.annotation.LocalChangeSeqNum
import com.ustadmobile.door.annotation.MasterChangeSeqNum
import com.ustadmobile.door.annotation.SyncableEntity
import kotlinx.serialization.Serializable

/**
 * This is a 1:1 relationship with Person. It avoids synchronizing login credentials with any other
 * devices in cases where another user has permission to view someone else's profile.
 *
 * There is no foreign key field, personAuthUid simply equals personUid.
 *
 * Note: this entity has change sequence numbers as it may be sync'd with particular, authorized
 * devices to provide a local login service.
 *
 * Currently, as PersonAuthDao does not extend syncable dao, it will not sync
 */
@Entity
@Serializable
class PersonAuth() {

    @PrimaryKey
    var personAuthUid: Long = 0

    var passwordHash: String? = null

    constructor(personAuthUid: Long, passwordHash: String) : this() {
        this.personAuthUid = personAuthUid
        this.passwordHash = passwordHash
    }
}