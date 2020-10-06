package databaseClasses;

import java.util.Date;

public class Test {
    private Date testDate;
    private Date testTimeStart;
    private String result;
    private Integer testCost;
    private boolean paidSituation;
    private boolean isInternalTest;

    public Test() {
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public Date getTestTimeStart() {
        return testTimeStart;
    }

    public void setTestTimeStart(Date testTimeStart) {
        this.testTimeStart = testTimeStart;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getTestCost() {
        return testCost;
    }

    public void setTestCost(Integer testCost) {
        this.testCost = testCost;
    }

    public boolean isPaidSituation() {
        return paidSituation;
    }

    public void setPaidSituation(boolean paidSituation) {
        this.paidSituation = paidSituation;
    }

    public boolean isInternalTest() {
        return isInternalTest;
    }

    public void setInternalTest(boolean internalTest) {
        isInternalTest = internalTest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test that = (Test) o;

        return testTimeStart != null ? testTimeStart.equals(that.testTimeStart) : that.testTimeStart == null;
    }

    @Override
    public int hashCode() {
        return testTimeStart != null ? testTimeStart.hashCode() : 0;
    }
}
