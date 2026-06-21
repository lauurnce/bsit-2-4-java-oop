package model;

public abstract class Loan {
    private int loanId, buyerId, propertyId, loanTermYears;
    private double loanAmount, downpayment, annualRate;
    private String dateBooked, financeType;

    public Loan() {}

    public int getLoanId() { return loanId; }
    public void setLoanId(int v) { loanId = v; }
    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int v) { buyerId = v; }
    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int v) { propertyId = v; }
    public int getLoanTermYears() { return loanTermYears; }
    public void setLoanTermYears(int v) { loanTermYears = v; }
    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double v) { loanAmount = v; }
    public double getDownpayment() { return downpayment; }
    public void setDownpayment(double v) { downpayment = v; }
    public double getAnnualRate() { return annualRate; }
    public void setAnnualRate(double v) { annualRate = v; }
    public String getDateBooked() { return dateBooked; }
    public void setDateBooked(String v) { dateBooked = v; }
    public String getFinanceType() { return financeType; }
    public void setFinanceType(String v) { financeType = v; }

    /** Amount actually financed. */
    public double principal() { return loanAmount - downpayment; }

    /** Shared amortization formula used by subclasses. */
    protected double amortize() {
        double p = principal();
        int n = loanTermYears * 12;
        if (n <= 0) return 0;
        double r = annualRate / 12.0 / 100.0;
        if (r <= 0) return p / n;
        return p * r / (1 - Math.pow(1 + r, -n));
    }

    public abstract double computeMonthlyAmortization();
    public abstract double computeTotalPayable();
}
