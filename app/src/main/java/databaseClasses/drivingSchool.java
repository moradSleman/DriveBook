package databaseClasses;

        import java.util.ArrayList;
        import java.util.List;

public class drivingSchool {
    private String schoolName;
    private String schoolEmail;
    private String schoolLocation;
    private String schoolPhone;
    private boolean notificationsOk;
    public drivingSchool(){ }

    public boolean isNotificationsOk() {
        return notificationsOk;
    }

    public void setNotificationsOk(boolean notificationsOk) {
        this.notificationsOk = notificationsOk;
    }

    public String getSchoolPhone() {
        return schoolPhone;
    }

    public void setSchoolPhone(String schoolPhone) {
        this.schoolPhone = schoolPhone;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public String getSchoolLocation() {
        return schoolLocation;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }

    public void setSchoolLocation(String schoolLocation) {
        this.schoolLocation = schoolLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        drivingSchool that = (drivingSchool) o;

        if (schoolName != null ? !schoolName.equals(that.schoolName) : that.schoolName != null)
            return false;
        if (schoolEmail != null ? !schoolEmail.equals(that.schoolEmail) : that.schoolEmail != null)
            return false;
        if (schoolLocation != null ? !schoolLocation.equals(that.schoolLocation) : that.schoolLocation != null)
            return false;
        return schoolPhone != null ? schoolPhone.equals(that.schoolPhone) : that.schoolPhone == null;
    }

    @Override
    public int hashCode() {
        int result = schoolName != null ? schoolName.hashCode() : 0;
        result = 31 * result + (schoolEmail != null ? schoolEmail.hashCode() : 0);
        result = 31 * result + (schoolLocation != null ? schoolLocation.hashCode() : 0);
        result = 31 * result + (schoolPhone != null ? schoolPhone.hashCode() : 0);
        return result;
    }
}

