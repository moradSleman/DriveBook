package databaseClasses;

import java.util.Date;

public class payment {
    private Date payDate;
    private Double totalPay;
    private boolean accepted;
    public payment() {
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Double getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(Double totalPay) {
        this.totalPay = totalPay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        payment payment = (payment) o;

        return payDate != null ? payDate.equals(payment.payDate) : payment.payDate == null;
    }

    @Override
    public int hashCode() {
        return payDate != null ? payDate.hashCode() : 0;
    }
}
