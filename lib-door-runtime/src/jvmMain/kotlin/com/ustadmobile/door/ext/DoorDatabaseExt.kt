package com.ustadmobile.door.ext

import com.ustadmobile.door.DoorDatabase

actual fun DoorDatabase.dbType(): Int = this.jdbcDbType