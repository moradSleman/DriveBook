package databaseClasses;

import android.graphics.Bitmap;

import java.util.Date;

public class pupilSqlLite {
    private String pupilPrivateId;
    private String pupilMail;
    private String pupilPassword;
    private String pupilLocation;
    private String pupilPhone;
    private String pupilBirthDate;
    private String pupilName;
    private String pupilTheoryEnd;
    private int TimeAlertBeforeLesson;
    private String TokenId;
    private String tokenUserName;
    private String tokenPassword;
    private Bitmap pupilImage;
    private String pupilUId;
    private String teacherUId;
    private String schoolUId;

    public pupilSqlLite() {
    }

    public String getPupilUId() {
        return pupilUId;
    }

    public void setPupilUId(String pupilUId) {
        this.pupilUId = pupilUId;
    }

    public String getTeacherUId() {
        return teacherUId;
    }

    public void setTeacherUId(String teacherUId) {
        this.teacherUId = teacherUId;
    }

    public String getSchoolUId() {
        return schoolUId;
    }

    public void setSchoolUId(String schoolUId) {
        this.schoolUId = schoolUId;
    }

    public Bitmap getPupilImage() {
        return pupilImage;
    }

    public void setPupilImage(Bitmap pupilImage) {
        this.pupilImage = pupilImage;
    }

    public String getPupilPrivateId() {
        return pupilPrivateId;
    }

    public void setPupilPrivateId(String pupilPrivateId) {
        this.pupilPrivateId = pupilPrivateId;
    }

    public String getTokenId() {
        return TokenId;
    }

    public void setTokenId(String tokenId) {
        TokenId = tokenId;
    }

    public String getTokenUserName() {
        return tokenUserName;
    }

    public void setTokenUserName(String tokenUserName) {
        this.tokenUserName = tokenUserName;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }

    public int getTimeAlertBeforeLesson() {
        return TimeAlertBeforeLesson;
    }

    public void setTimeAlertBeforeLesson(int timeAlertBeforeLesson) {
        TimeAlertBeforeLesson = timeAlertBeforeLesson;
    }

    public String getPupilId() {
        return pupilPrivateId;
    }

    public void setPupilId(String pupilId) {
        this.pupilPrivateId = pupilId;
    }

    public String getPupilTheoryEnd() {
        return pupilTheoryEnd;
    }

    public void setPupilTheoryEnd(String pupilTheoryEnd) {
        this.pupilTheoryEnd = pupilTheoryEnd;
    }

    public String getPupilName() {
        return pupilName;
    }

    public void setPupilName(String pupilName) {
        this.pupilName = pupilName;
    }

    public void setPupilPassword(String pupilPassword) {
        this.pupilPassword = pupilPassword;
    }

    public void setPupilLocation(String pupilLocation) {
        this.pupilLocation = pupilLocation;
    }

    public void setPupilPhone(String pupilPhone) {
        this.pupilPhone = pupilPhone;
    }

    public void setPupilBirthDate(String pupilBirthDate) {
        this.pupilBirthDate = pupilBirthDate;
    }

    public String getPupilPassword() {
        return pupilPassword;
    }

    public String getPupilLocation() {
        return pupilLocation;
    }

    public String getPupilPhone() {
        return pupilPhone;
    }

    public String getPupilBirthDate() {
        return pupilBirthDate;
    }

    public String getPupilMail() {
        return pupilMail;
    }

    public void setPupilMail(String pupilMail) {
        this.pupilMail = pupilMail;
    }


}
