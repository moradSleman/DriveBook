package databaseClasses;

import java.util.Date;

public class notification {
    private String title;
    private String body;
    private Date timeNoty;
    private Double totalPaymentAdded;
    private String schoolIdToAdd;
    private boolean read;

    public notification() {
    }

    public String getSchoolIdToAdd() {
        return schoolIdToAdd;
    }

    public void setSchoolIdToAdd(String schoolIdToAdd) {
        this.schoolIdToAdd = schoolIdToAdd;
    }

    public Double getTotalPaymentAdded() {
        return totalPaymentAdded;
    }

    public void setTotalPaymentAdded(Double totalPaymentAdded) {
        this.totalPaymentAdded = totalPaymentAdded;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimeNoty() {
        return timeNoty;
    }

    public void setTimeNoty(Date timeNoty) {
        this.timeNoty = timeNoty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        notification that = (notification) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        return timeNoty != null ? timeNoty.equals(that.timeNoty) : that.timeNoty == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (timeNoty != null ? timeNoty.hashCode() : 0);
        return result;
    }
}
