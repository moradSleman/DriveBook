package databaseClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TimeScheduling implements Parcelable {
    private String schoolUId;
    private Date startTime;
    private Date endTime;
    private Date date;
    private String TeacherUId;
    private String pupilUId;
    private String pupilFullName;
    private boolean test;
    private boolean isEnternalTest;
    private String secondPupilUId;
    private String secondpupilFullName;

    public TimeScheduling() {
        setTest(false);
    }
    public TimeScheduling(boolean test) {
        this.test=test;
    }

    protected TimeScheduling(Parcel in) {
        TeacherUId = in.readString();
        pupilUId = in.readString();
        pupilFullName = in.readString();
        startTime=new Date(in.readLong());
        endTime=new Date(in.readLong());
        date=new Date(in.readLong());
    }

    public boolean isEnternalTest() {
        return isEnternalTest;
    }

    public void setEnternalTest(boolean enternalTest) {
        isEnternalTest = enternalTest;
    }

    public static final Parcelable.Creator<TimeScheduling> CREATOR = new Parcelable.Creator<TimeScheduling>() {
        @Override
        public TimeScheduling createFromParcel(Parcel in) {
            return new TimeScheduling(in);
        }

        @Override
        public TimeScheduling[] newArray(int size) {
            return new TimeScheduling[size];
        }
    };

    public String getSchoolUId() {
        return schoolUId;
    }

    public void setSchoolUId(String schoolUId) {
        this.schoolUId = schoolUId;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getSecondPupilUId() {
        return secondPupilUId;
    }

    public void setSecondPupilUId(String secondPupilUId) {
        this.secondPupilUId = secondPupilUId;
    }

    public String getSecondpupilFullName() {
        return secondpupilFullName;
    }

    public void setSecondpupilFullName(String secondpupilFullName) {
        this.secondpupilFullName = secondpupilFullName;
    }

    public String getPupilFullName() {
        return pupilFullName;
    }

    public void setPupilFullName(String pupilFullName) {
        this.pupilFullName = pupilFullName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTeacherUId() {
        return TeacherUId;
    }

    public void setTeacherUId(String teacherUId) {
        TeacherUId = teacherUId;
    }

    public String getPupilUId() {
        return pupilUId;
    }

    public void setPupilUId(String pupilUId) {
        this.pupilUId = pupilUId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeScheduling that = (TimeScheduling) o;

        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        return endTime != null ? endTime.equals(that.endTime) : that.endTime == null;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTime.getTime());
        dest.writeLong(endTime.getTime());
        dest.writeLong(date.getTime());
        dest.writeString(TeacherUId);
        dest.writeString(pupilUId);
        dest.writeString(pupilFullName);
    }


}
