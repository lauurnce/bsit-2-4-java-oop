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

        // cleanup so reruns stay clean
        bd.delete(added.getId());
        pd.delete(padded.getId());
        System.out.println("DAO CRUD OK");
    }
}
