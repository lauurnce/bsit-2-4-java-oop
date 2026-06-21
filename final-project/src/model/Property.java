package model;

public class Property {
    private int id;
    private String unitName, location, unitType, status;
    private double sellPrice;

    public Property() {}

    public Property(int id, String unitName, String location, String unitType,
                    double sellPrice, String status) {
        this.id = id; this.unitName = unitName; this.location = location;
        this.unitType = unitType; this.sellPrice = sellPrice; this.status = status;
    }

    public int getId() { return id; }
    public void setId(int v) { id = v; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String v) { unitName = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { location = v; }
    public String getUnitType() { return unitType; }
    public void setUnitType(String v) { unitType = v; }
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double v) { sellPrice = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
}
