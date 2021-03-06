package com.ustadmobile.core.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.ustadmobile.door.annotation.Repository
import com.ustadmobile.lib.db.entities.Role
import com.ustadmobile.lib.db.entities.School
import com.ustadmobile.lib.db.entities.SchoolWithHolidayCalendar
import com.ustadmobile.lib.db.entities.SchoolWithMemberCountAndLocation

@Repository
@Dao
abstract class SchoolDao : BaseDao<School> {

    @Query("SELECT * FROM School WHERE schoolUid = :schoolUid AND CAST(schoolActive AS INTEGER) = 1")
    abstract suspend fun findByUidAsync(schoolUid: Long): School?

    @Query("""SELECT School.*, HolidayCalendar.* FROM School 
            LEFT JOIN HolidayCalendar ON School.schoolHolidayCalendarUid = HolidayCalendar.umCalendarUid
            WHERE School.schoolUid = :uid""")
    abstract suspend fun findByUidWithHolidayCalendarAsync(uid: Long): SchoolWithHolidayCalendar?


    @Query("SELECT * FROM School WHERE schoolCode = :code")
    abstract suspend fun findBySchoolCode(code: String): School?


    /** Check if a permission is present on a specific entity e.g. updateState/modify etc */
    @Query("SELECT EXISTS(SELECT 1 FROM School WHERE " +
            "School.schoolUid = :schoolUid AND :accountPersonUid IN " +
            "(${ENTITY_PERSONS_WITH_PERMISSION}))")
    abstract suspend fun personHasPermissionWithSchool(accountPersonUid: Long,
                                                       schoolUid: Long,
                                                      permission: Long) : Boolean


    @Query("""
        SELECT School.*, 
            (SELECT COUNT(*) FROM SchoolMember WHERE SchoolMember.schoolMemberSchoolUid = School.schoolUid AND 
            CAST(SchoolMember.schoolMemberActive AS INTEGER) = 1 
            AND SchoolMember.schoolMemberRole = ${Role.ROLE_SCHOOL_STUDENT_UID}) as numStudents,
            (SELECT COUNT(*) FROM SchoolMember WHERE SchoolMember.schoolMemberSchoolUid = School.schoolUid AND 
            CAST(SchoolMember.schoolMemberActive AS INTEGER) = 1 
            AND SchoolMember.schoolMemberRole = ${Role.ROLE_SCHOOL_STAFF_UID}) as numTeachers, 
            '' as locationName,
            (SELECT COUNT(*) FROM Clazz WHERE Clazz.clazzSchoolUid = School.schoolUid AND CAST(Clazz.clazzUid AS INTEGER) = 1 ) as clazzCount
        FROM 
            PersonGroupMember
            LEFT JOIN EntityRole ON EntityRole.erGroupUid = PersonGroupMember.groupMemberGroupUid
            LEFT JOIN Role ON EntityRole.erRoleUid = Role.roleUid AND (Role.rolePermissions & :permission) > 0
            LEFT JOIN School ON 
                CAST((SELECT admin FROM Person Person_Admin WHERE Person_Admin.personUid = :personUid) AS INTEGER) = 1
                OR (EntityRole.erTableId = ${School.TABLE_ID} AND EntityRole.erEntityUid = School.schoolUid)
        WHERE
            PersonGroupMember.groupMemberPersonUid = :personUid
            AND CAST(schoolActive AS INTEGER) = 1
            AND schoolName LIKE :searchBit
        GROUP BY School.schoolUid
        ORDER BY CASE(:sortOrder)
            WHEN $SORT_NAME_ASC THEN School.schoolName
            ELSE ''
        END ASC,
        CASE(:sortOrder)
            WHEN $SORT_NAME_DESC THEN School.schoolName
            ELSE ''
        END DESC""")
    abstract fun findAllActiveSchoolWithMemberCountAndLocationName(searchBit: String,
                    personUid: Long, permission: Long, sortOrder: Int)
            : DataSource.Factory<Int, SchoolWithMemberCountAndLocation>


    @Update
    abstract suspend fun updateAsync(entity: School): Int

    companion object {

        const val SORT_NAME_ASC = 1

        const val SORT_NAME_DESC = 2

        const val ENTITY_PERSONS_WITH_PERMISSION_PT1 = """
            SELECT DISTINCT Person.PersonUid FROM Person
            LEFT JOIN PersonGroupMember ON Person.personUid = PersonGroupMember.groupMemberPersonUid
            LEFT JOIN EntityRole ON EntityRole.erGroupUid = PersonGroupMember.groupMemberGroupUid
            LEFT JOIN Role ON EntityRole.erRoleUid = Role.roleUid
            WHERE 
            CAST(Person.admin AS INTEGER) = 1
            OR 
            (EntityRole.ertableId = ${School.TABLE_ID} AND 
            EntityRole.erEntityUid = School.schoolUid AND
            (Role.rolePermissions &  
        """

        const val ENTITY_PERSONS_WITH_PERMISSION_PT2 = ") > 0)"

        const val ENTITY_PERSONS_WITH_PERMISSION = "${ENTITY_PERSONS_WITH_PERMISSION_PT1} " +
                ":permission ${ENTITY_PERSONS_WITH_PERMISSION_PT2}"


    }

}
