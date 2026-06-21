package dao;

import model.Buyer;
import model.Property;
import java.util.List;

public class DaoCrudTest {
    public static void main(String[] a) throws Exception {
        db.Database.init();
        BuyerDao bd = new BuyerDao();
        Buyer b = new Buyer(0, "Juan Cruz", "1990-01-01", "GID-1", "M", "S",
                "Manila", 50000, "09171234567", "juan@example.com");
        bd.add(b);
        List<Buyer> all = bd.getAll();
        if (all.isEmpty()) throw new AssertionError("buyer not added");
        Buyer added = all.get(all.size() - 1);
        added.setFullName("Juan Updated");
        bd.update(added);
        if (bd.findById(added.getId()) == null
            || !bd.findById(added.getId()).getFullName().equals("Juan Updated"))
            throw new AssertionError("update failed");
        if (bd.search("Updated").isEmpty()) throw new AssertionError("search failed");

        PropertyDao pd = new PropertyDao();
        Property p = new Property(0, "Unit A", "Cavite", "Townhouse", 2_500_000, "Available");
        pd.add(p);
        Property padded = pd.getAll().get(pd.getAll().size() - 1);
        if (pd.findById(padded.getId()) == null) throw new AssertionError("property findById failed");

        // --- Loan polymorphism via DAO ---
        // need valid FK rows; re-add a buyer and property
        Buyer fb = new Buyer(0, "Loan Buyer", null, null, null, null, null, 60000, null, null);
        bd.add(fb);
        int fbId = bd.getAll().get(bd.getAll().size() - 1).getId();
        Property fp = new Property(0, "Loan Unit", "QC", "Condo", 3_000_000, "Available");
        pd.add(fp);
        int fpId = pd.getAll().get(pd.getAll().size() - 1).getId();

        LoanDao ld = new LoanDao();
        model.InHouseLoan loan = new model.InHouseLoan();
        loan.setBuyerId(fbId); loan.setPropertyId(fpId);
        loan.setLoanAmount(3_000_000); loan.setDownpayment(600_000);
        loan.setAnnualRate(9.5); loan.setLoanTermYears(15); loan.setDateBooked("2026-06-21");
        ld.add(loan);
        model.Loan fetched = ld.getAll().get(ld.getAll().size() - 1);
        if (!(fetched instanceof model.InHouseLoan))
            throw new AssertionError("DAO did not rebuild InHouseLoan subtype");
        if (fetched.computeMonthlyAmortization() <= 0)
            throw new AssertionError("amortization not computed");

        ld.delete(fetched.getLoanId());
        bd.delete(fbId);
        pd.delete(fpId);

        // original cleanup
        bd.delete(added.getId());
        pd.delete(padded.getId());
        System.out.println("DAO CRUD OK");
    }
}
