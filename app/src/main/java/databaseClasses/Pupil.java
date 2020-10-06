package databaseClasses;

import java.util.Date;

public class Pupil {
            private String schoolId;
            private String pupilMail;
            private String pupilLocation;
            private String pupilPhone;
            private Date pupilBirthDate;
            private String pupilName;
            private Date pupilTheoryEnd;
            private Integer timeAlertBeforeLesson;
            private boolean notfyBeforeLesson,notyFromTeacher,notyBeforeTheoryEnd;
            public Pupil() {
            }

    public Integer getTimeAlertBeforeLesson() {
        return timeAlertBeforeLesson;
    }

    public void setTimeAlertBeforeLesson(Integer timeAlertBeforeLesson) {
        this.timeAlertBeforeLesson = timeAlertBeforeLesson;
    }

    public boolean isNotfyBeforeLesson() {
                return notfyBeforeLesson;
            }

            public void setNotfyBeforeLesson(boolean notfyBeforeLesson) {
                this.notfyBeforeLesson = notfyBeforeLesson;
    }

    public boolean isNotyFromTeacher() {
        return notyFromTeacher;
    }

    public void setNotyFromTeacher(boolean notyFromTeacher) {
        this.notyFromTeacher = notyFromTeacher;
    }

    public boolean isNotyBeforeTheoryEnd() {
        return notyBeforeTheoryEnd;
    }

    public void setNotyBeforeTheoryEnd(boolean notyBeforeTheoryEnd) {
        this.notyBeforeTheoryEnd = notyBeforeTheoryEnd;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public Date getPupilTheoryEnd() {
        return pupilTheoryEnd;
    }

    public void setPupilTheoryEnd(Date pupilTheoryEnd) {
        this.pupilTheoryEnd = pupilTheoryEnd;
    }

    public String getPupilName() {
        return pupilName;
    }

    public void setPupilName(String pupilName) {
        this.pupilName = pupilName;
    }


    public void setPupilLocation(String pupilLocation) {
        this.pupilLocation = pupilLocation;
    }

    public void setPupilPhone(String pupilPhone) {
        this.pupilPhone = pupilPhone;
    }

    public void setPupilBirthDate(Date pupilBirthDate) {
        this.pupilBirthDate = pupilBirthDate;
    }


    public String getPupilLocation() {
        return pupilLocation;
    }

    public String getPupilPhone() {
        return pupilPhone;
    }

    public Date getPupilBirthDate() {
        return pupilBirthDate;
    }

    public String getPupilMail() {
        return pupilMail;
    }

    public void setPupilMail(String pupilMail) {
        this.pupilMail = pupilMail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pupil pupil = (Pupil) o;

        if (pupilMail != null ? !pupilMail.equals(pupil.pupilMail) : pupil.pupilMail != null) return false;
        if (pupilLocation != null ? !pupilLocation.equals(pupil.pupilLocation) : pupil.pupilLocation != null) return false;
        if (pupilPhone != null ? !pupilPhone.equals(pupil.pupilPhone) : pupil.pupilPhone != null) return false;
        return pupilBirthDate != null ? pupilBirthDate.equals(pupil.pupilBirthDate) : pupil.pupilBirthDate == null;
    }

    @Override
    public int hashCode() {
        int result = pupilMail != null ? pupilMail.hashCode() : 0;
        result = 31 * result + (pupilLocation != null ? pupilLocation.hashCode() : 0);
        result = 31 * result + (pupilPhone != null ? pupilPhone.hashCode() : 0);
        result = 31 * result + (pupilBirthDate != null ? pupilBirthDate.hashCode() : 0);
        return result;
    }
}
