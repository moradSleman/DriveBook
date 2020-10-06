package databaseClasses;

import java.util.Date;

public class lessonPayment {
    private Date lessonDate;
    private Date lessonStartingDate;
    private Date lessonEndingDate;
    private Integer lessonCost;
    private boolean lessonPaidSituation;
    private Date lessonPaidDate;

    public lessonPayment() {
    }

    public Date getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(Date lessonDate) {
        this.lessonDate = lessonDate;
    }

    public Date getLessonStartingDate() {
        return lessonStartingDate;
    }

    public void setLessonStartingDate(Date lessonStartingDate) {
        this.lessonStartingDate = lessonStartingDate;
    }

    public Date getLessonEndingDate() {
        return lessonEndingDate;
    }

    public void setLessonEndingDate(Date lessonEndingDate) {
        this.lessonEndingDate = lessonEndingDate;
    }

    public Integer getLessonCost() {
        return lessonCost;
    }

    public void setLessonCost(Integer lessonCost) {
        this.lessonCost = lessonCost;
    }

    public boolean isLessonPaidSituation() {
        return lessonPaidSituation;
    }

    public void setLessonPaidSituation(boolean lessonPaidSituation) {
        this.lessonPaidSituation = lessonPaidSituation;
    }

    public Date getLessonPaidDate() {
        return lessonPaidDate;
    }

    public void setLessonPaidDate(Date lessonPaidDate) {
        this.lessonPaidDate = lessonPaidDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        lessonPayment that = (lessonPayment) o;

        return lessonStartingDate != null ? lessonStartingDate.equals(that.lessonStartingDate) : that.lessonStartingDate == null;
    }

    @Override
    public int hashCode() {
        return lessonStartingDate != null ? lessonStartingDate.hashCode() : 0;
    }
}
