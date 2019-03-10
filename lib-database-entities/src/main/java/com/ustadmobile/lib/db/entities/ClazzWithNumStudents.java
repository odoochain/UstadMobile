package com.ustadmobile.lib.db.entities;

public class ClazzWithNumStudents extends Clazz {

    private int numStudents;

    private int numTeachers;

    private String teacherNames;

    private long lastRecorded;

    public int getNumTeachers() {
        return numTeachers;
    }

    public void setNumTeachers(int numTeachers) {
        this.numTeachers = numTeachers;
    }

    public String getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        this.teacherNames = teacherNames;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public long getLastRecorded() {
        return lastRecorded;
    }

    public void setLastRecorded(long lastRecorded) {
        this.lastRecorded = lastRecorded;
    }
}
