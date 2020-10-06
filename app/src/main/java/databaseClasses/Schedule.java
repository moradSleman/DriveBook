package databaseClasses;

import java.util.Date;

public class Schedule {
    private Date scheduleDate;

    public Schedule() {
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        return scheduleDate != null ? scheduleDate.equals(schedule.scheduleDate) : schedule.scheduleDate == null;
    }

    @Override
    public int hashCode() {
        return scheduleDate != null ? scheduleDate.hashCode() : 0;
    }


}
