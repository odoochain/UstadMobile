package com.ustadmobile.core.util.ext

import com.soywiz.klock.DateTime
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.schedule.localMidnight
import com.ustadmobile.core.schedule.toOffsetByTimezone
import com.ustadmobile.lib.db.entities.*

fun UmAppDatabase.runPreload() {
    preload()
    timeZoneEntityDao.insertSystemTimezones()
    personDetailPresenterFieldDao.preloadCoreFields()
}


/**
 * Insert a new class and
 */
suspend fun UmAppDatabase.createNewClazzAndGroups(clazz: Clazz, impl: UstadMobileSystemImpl, context: Any) {
    clazz.clazzTeachersPersonGroupUid = personGroupDao.insertAsync(
            PersonGroup("${clazz.clazzName} - " +
                    impl.getString(MessageID.teachers_literal, context)))

    clazz.clazzStudentsPersonGroupUid = personGroupDao.insertAsync(PersonGroup("${clazz.clazzName} - " +
            impl.getString(MessageID.students, context)))

    clazz.clazzUid = clazzDao.insertAsync(clazz)

    entityRoleDao.insertAsync(EntityRole(Clazz.TABLE_ID, clazz.clazzUid,
        clazz.clazzTeachersPersonGroupUid, Role.ROLE_TEACHER_UID.toLong()))
    entityRoleDao.insertAsync(EntityRole(Clazz.TABLE_ID, clazz.clazzUid,
        clazz.clazzStudentsPersonGroupUid, Role.ROLE_STUDENT_UID.toLong()))
}

/**
 * Enrol the given person into the given class. The effective date of joining is midnight as per
 * the timezone of the class (e.g. when a teacher adds a student to the system who just joined and
 * wants to mark their attendance for the same day).
 */
suspend fun UmAppDatabase.enrolPersonIntoClazzAtLocalTimezone(personToEnrol: Person, clazzUid: Long,
                                                              role: Int,
                                                              clazzWithSchool: ClazzWithSchool? = null): ClazzMemberWithPerson {
    val clazzWithSchoolVal = clazzWithSchool ?: clazzDao.getClazzWithSchool(clazzUid)
        ?: throw IllegalArgumentException("Class does not exist")

    val clazzTimeZone = clazzWithSchoolVal.effectiveTimeZone()
    val joinTime = DateTime.now().toOffsetByTimezone(clazzTimeZone).localMidnight.utc.unixMillisLong
    val clazzMember = ClazzMemberWithPerson().apply {
        clazzMemberPersonUid = personToEnrol.personUid
        clazzMemberClazzUid = clazzUid
        clazzMemberRole = role
        clazzMemberActive = true
        clazzMemberDateJoined = joinTime
        person = personToEnrol
        clazzMemberUid = clazzMemberDao.insertAsync(this)
    }

    val personGroupUid = when(role) {
        ClazzMember.ROLE_TEACHER -> clazzWithSchoolVal.clazzTeachersPersonGroupUid
        ClazzMember.ROLE_STUDENT -> clazzWithSchoolVal.clazzStudentsPersonGroupUid
        else -> null
    }

    if(personGroupUid != null) {
        val personGroupMember = PersonGroupMember().also {
            it.groupMemberPersonUid = personToEnrol.personUid
            it.groupMemberGroupUid = personGroupUid
            it.groupMemberUid = personGroupMemberDao.insertAsync(it)
        }
    }

    return clazzMember
}

suspend fun UmAppDatabase.enrollPersonToSchool(dateNow: Long, schoolUid: Long,
                                 personUid:Long, role: Int): SchoolMember{

    val school = schoolDao.findByUidAsync(schoolUid)?:
    throw IllegalArgumentException("Class does not exist")

    //Check if relationship already exists
    val matches = schoolMemberDao.findBySchoolAndPersonAndRole(schoolUid, personUid,  role)
    if(matches.isEmpty()) {

        val schoolMember = SchoolMember()
        schoolMember.schoolMemberActive = true
        schoolMember.schoolMemberPersonUid = personUid
        schoolMember.schoolMemberSchoolUid = schoolUid
        schoolMember.schoolMemberRole = role
        schoolMember.schoolMemberCreateDate = dateNow
        schoolMember.schoolMemberJoinDate = dateNow

        schoolMember.schoolMemberUid = schoolMemberDao.insert(schoolMember)

        val personGroupUid = when(role) {
            SchoolMember.SCHOOL_ROLE_TEACHER -> school.schoolTeachersPersonGroupUid
            SchoolMember.SCHOOL_ROLE_STUDENT -> school.schoolStudentsPersonGroupUid
            else -> null
        }

        if(personGroupUid != null) {
            val personGroupMember = PersonGroupMember().also {
                it.groupMemberPersonUid = schoolMember.schoolMemberPersonUid
                it.groupMemberGroupUid = personGroupUid
                it.groupMemberUid = personGroupMemberDao.insertAsync(it)
            }
        }

        return schoolMember
    }else{
        return matches[0]
    }
}