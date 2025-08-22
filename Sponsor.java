package model;

public class Sponsor {
    public int sponsorId;
    public String name;
    public String industry;
    public long contractValue;
    public int contractDurationMonths;

    public Sponsor(int sponsorId, String name, String industry, long contractValue, int contractDurationMonths) {
        this.sponsorId = sponsorId;
        this.name = name;
        this.industry = industry;
        this.contractValue = contractValue;
        this.contractDurationMonths = contractDurationMonths;
    }

    @Override
    public String toString() {
        return String.format("%d - %s | Industry: %s | Contract: %,d over %d months",
                sponsorId, name, industry, contractValue, contractDurationMonths);
    }
}
