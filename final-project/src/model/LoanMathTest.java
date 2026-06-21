package model;

public class LoanMathTest {
    static void assertClose(double a, double b, double tol, String msg) {
        if (Math.abs(a - b) > tol) throw new AssertionError(msg + " expected " + b + " got " + a);
    }
    public static void main(String[] args) {
        // P = 1,000,000 - 200,000 = 800,000; rate 6.5%/yr; 10 yrs => 120 months
        BankFinancedLoan bank = new BankFinancedLoan();
        bank.setLoanAmount(1_000_000); bank.setDownpayment(200_000);
        bank.setAnnualRate(6.5); bank.setLoanTermYears(10);
        // Verified against standard amortization formula ~ 9083.84
        assertClose(bank.computeMonthlyAmortization(), 9083.84, 1.0, "bank monthly");

        // InHouse same numbers but rate 9.5%
        InHouseLoan in = new InHouseLoan();
        in.setLoanAmount(1_000_000); in.setDownpayment(200_000);
        in.setAnnualRate(9.5); in.setLoanTermYears(10);
        // Verified ~ 10351.80
        assertClose(in.computeMonthlyAmortization(), 10351.80, 1.0, "inhouse monthly");

        // Polymorphism: same reference type, different behavior
        Loan poly = new BankFinancedLoan();
        poly.setLoanAmount(500_000); poly.setDownpayment(0);
        poly.setAnnualRate(0); poly.setLoanTermYears(5); // zero-rate branch: P/n = 500000/60
        assertClose(poly.computeMonthlyAmortization(), 8333.33, 0.5, "zero-rate monthly");

        System.out.println("LOAN MATH OK");
    }
}
