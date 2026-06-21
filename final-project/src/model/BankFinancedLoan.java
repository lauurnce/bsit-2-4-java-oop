package model;

public class BankFinancedLoan extends Loan {
    public static final double DEFAULT_RATE = 6.5;

    public BankFinancedLoan() {
        setFinanceType("Bank");
        setAnnualRate(DEFAULT_RATE);
    }

    @Override
    public double computeMonthlyAmortization() { return amortize(); }

    @Override
    public double computeTotalPayable() {
        return computeMonthlyAmortization() * getLoanTermYears() * 12 + getDownpayment();
    }
}
