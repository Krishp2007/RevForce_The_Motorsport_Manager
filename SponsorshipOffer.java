package model;

import java.sql.Date;

public class SponsorshipOffer {
    public int offerId;
    public int teamId;
    public String sponsorName;
    public String industry;
    public long amount;
    public int contractDurationMonths;
    public String status;
    public Date offerDate;

    public SponsorshipOffer(int offerId, int teamId, String sponsorName, String industry,
                            long amount, int contractDurationMonths, String status, Date offerDate) {
        this.offerId = offerId;
        this.teamId = teamId;
        this.sponsorName = sponsorName;
        this.industry = industry;
        this.amount = amount;
        this.contractDurationMonths = contractDurationMonths;
        this.status = status;
        this.offerDate = offerDate;
    }

    @Override
    public String toString() {
        return String.format("Offer ID: %d | Sponsor: %s | Industry: %s | Amount: %,d | Duration: %d months | Status: %s | Date: %s",
                offerId, sponsorName, industry, amount, contractDurationMonths, status, offerDate.toString());
    }
}
