package db;

import dao.BuyerDao;
import dao.LoanDao;
import dao.PropertyDao;
import model.BankFinancedLoan;
import model.Buyer;
import model.InHouseLoan;
import model.Property;

/**
 * One-time demo data seeder. Run with:
 *   java -cp "bin:lib/*" db.SeedSampleData
 * Only seeds when the tables are empty, so it is safe to re-run.
 */
public class SeedSampleData {
    public static void main(String[] args) throws Exception {
        Database.init();
        BuyerDao bd = new BuyerDao();
        PropertyDao pd = new PropertyDao();
        LoanDao ld = new LoanDao();

        if (!bd.getAll().isEmpty()) {
            System.out.println("Sample data already present — nothing seeded.");
            return;
        }

        bd.add(new Buyer(0, "James Velasco", "1995-05-03", "P123456789", "M", "M",
                "#16 Mapayapa St., Cebu City", 85000, "09171234567", "james@example.com"));
        bd.add(new Buyer(0, "Ana Santos", "1993-10-02", "P222333444", "F", "S",
                "#45 Aurora Blvd., QC", 74000, "09192345678", "ana@example.com"));
        bd.add(new Buyer(0, "Mark Reyes", "1988-11-20", "P555666777", "M", "M",
                "#78 Rizal St., Manila", 60000, "09175678901", "mark@example.com"));

        pd.add(new Property(0, "Bria Homes Lot 12", "Cavite", "House & Lot", 2_500_000, "Available"));
        pd.add(new Property(0, "Camella Townhouse 4B", "Laguna", "Townhouse", 3_200_000, "Available"));
        pd.add(new Property(0, "Profriends Condo 9F", "Pasig", "Condo", 4_100_000, "Available"));

        int b1 = bd.getAll().get(0).getId();
        int b2 = bd.getAll().get(1).getId();
        int p1 = pd.getAll().get(0).getId();
        int p2 = pd.getAll().get(1).getId();

        BankFinancedLoan bank = new BankFinancedLoan();
        bank.setBuyerId(b1); bank.setPropertyId(p1);
        bank.setLoanAmount(2_500_000); bank.setDownpayment(500_000);
        bank.setAnnualRate(6.5); bank.setLoanTermYears(15); bank.setDateBooked("2026-01-15");
        ld.add(bank);

        InHouseLoan inhouse = new InHouseLoan();
        inhouse.setBuyerId(b2); inhouse.setPropertyId(p2);
        inhouse.setLoanAmount(3_200_000); inhouse.setDownpayment(640_000);
        inhouse.setAnnualRate(9.5); inhouse.setLoanTermYears(10); inhouse.setDateBooked("2026-02-20");
        ld.add(inhouse);

        System.out.println("Seeded 3 buyers, 3 properties, 2 loans.");
    }
}
