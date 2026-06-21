package model;

public class InHouseLoan extends Loan {
    public static final double DEFAULT_RATE = 9.5;
    public static final double PROCESSING_FEE = 25_000.0;

    public InHouseLoan() {
        setFinanceType("InHouse");
        setAnnualRate(DEFAULT_RATE);
    }

    @Override
    public double computeMonthlyAmortization() { return amortize(); }

    @Override
    public double computeTotalPayable() {
        return computeMonthlyAmortization() * getLoanTermYears() * 12
                + getDownpayment() + PROCESSING_FEE;
    }
}
