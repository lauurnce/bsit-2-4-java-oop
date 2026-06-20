package model;

public class Buyer extends Person {
    private String birthdate, govId, gender, civilStatus, address, mobileNum, email;
    private double monthlyIncome;

    public Buyer() { super(); }

    public Buyer(int id, String fullName, String birthdate, String govId, String gender,
                 String civilStatus, String address, double monthlyIncome,
                 String mobileNum, String email) {
        super(id, fullName);
        this.birthdate = birthdate; this.govId = govId; this.gender = gender;
        this.civilStatus = civilStatus; this.address = address;
        this.monthlyIncome = monthlyIncome; this.mobileNum = mobileNum; this.email = email;
    }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String v) { birthdate = v; }
    public String getGovId() { return govId; }
    public void setGovId(String v) { govId = v; }
    public String getGender() { return gender; }
    public void setGender(String v) { gender = v; }
    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String v) { civilStatus = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { address = v; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double v) { monthlyIncome = v; }
    public String getMobileNum() { return mobileNum; }
    public void setMobileNum(String v) { mobileNum = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }

    @Override
    public String describe() {
        return "Buyer #" + getId() + ": " + getFullName();
    }
}
