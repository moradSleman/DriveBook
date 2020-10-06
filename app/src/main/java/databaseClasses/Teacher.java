package databaseClasses;

import java.util.Date;

public class Teacher {
    private String teacherName;
    private String teacherMail;
    private String teacherPhone;
    private Date teacherBirthdate;
    private String teacherLocation;
    private Integer lessonCost;
    private Integer hoursBedoreLockScheduling;
    private Integer teacherLessonIntervalTime;
    private boolean notificationsOk;

    public Teacher() {
    }

    public boolean isNotificationsOk() {
        return notificationsOk;
    }

    public void setNotificationsOk(boolean notificationsOk) {
        this.notificationsOk = notificationsOk;
    }

    public Integer getHoursBedoreLockScheduling() {
        return hoursBedoreLockScheduling;
    }

    public void setHoursBedoreLockScheduling(Integer hoursBedoreLockScheduling) {
        this.hoursBedoreLockScheduling = hoursBedoreLockScheduling;
    }

    public Integer getLessonCost() {
        return lessonCost;
    }

    public void setLessonCost(Integer lessonCost) {
        this.lessonCost = lessonCost;
    }

    public Integer getTeacherLessonIntervalTime() {
        return teacherLessonIntervalTime;
    }

    public void setTeacherLessonIntervalTime(Integer teacherLessonIntervalTime) {
        this.teacherLessonIntervalTime = teacherLessonIntervalTime;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherMail() {
        return teacherMail;
    }

    public void setTeacherMail(String teacherMail) {
        this.teacherMail = teacherMail;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public Date getTeacherBirthdate() {
        return teacherBirthdate;
    }

    public void setTeacherBirthdate(Date teacherBirthdate) {
        this.teacherBirthdate = teacherBirthdate;
    }

    public String getTeacherLocation() {
        return teacherLocation;
    }

    public void setTeacherLocation(String teacherLocation) {
        this.teacherLocation = teacherLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Teacher teacher = (Teacher) o;

        if (teacherName != null ? !teacherName.equals(teacher.teacherName) : teacher.teacherName != null)
            return false;
        return teacherPhone != null ? teacherPhone.equals(teacher.teacherPhone) : teacher.teacherPhone == null;
    }

    @Override
    public int hashCode() {
        int result = teacherName != null ? teacherName.hashCode() : 0;
        result = 31 * result + (teacherPhone != null ? teacherPhone.hashCode() : 0);
        return result;
    }
}