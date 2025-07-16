package com.revForce.sponsorship;


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class Sponsor {
    private final String sponsorId; // Unique identifier for the sponsor
    private final String name; // Name of the sponsor
    private double budget; // Available budget for bidding

    public Sponsor(String sponsorId, String name, double budget) {
        this.sponsorId = sponsorId;
        this.name = name;
        this.budget = budget;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    // Additional methods can be added as needed
}

class SponsorParticipation {
    // Dependencies
    private final SponsorAuctionManager auctionManager;
    private final UserValidator userValidator;

    // Active participant tracking
    private final Map<String, ParticipantSession> activeSessions = new ConcurrentHashMap<>();
    private final Set<String> bannedParticipants = ConcurrentHashMap.newKeySet();

    public SponsorParticipation(SponsorAuctionManager auctionManager) {
        this.auctionManager = Objects.requireNonNull(auctionManager, "Auction manager cannot be null");
        this.userValidator = new UserValidator();
    }

    // ===================
    // Core Participation Flow
    // ===================
    public void joinAuction(String userId, String teamId) throws AuctionParticipationException {
        validateParticipationPrerequisites(userId, teamId);

        ParticipantSession session = new ParticipantSession(userId, teamId);
        activeSessions.put(userId, session);

        System.out.println("User  " + userId + " joined auction for team " + teamId);
    }

    public void placeBid(String userId, double amount) throws AuctionBidException {
        try {
            validateBidConditions(userId, amount);

            ParticipantSession session = activeSessions.get(userId);
            Sponsor sponsor = resolveSponsorIdentity(userId);

            auctionManager.submitBid(sponsor, session.getTeamId(), amount);
            session.recordBid(amount);

            System.out.println("Bid placed: $" + amount + " by user " + userId);

        } catch (IllegalStateException e) {
            throw new AuctionBidException("System error processing bid", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void leaveAuction(String userId) {
        ParticipantSession session = activeSessions.remove(userId);
        if (session != null) {
            System.out.println("User  " + userId + " left the auction");
        }
    }

    // ===================
    // Validation Layer
    // ===================
    private void validateParticipationPrerequisites(String userId, String teamId)
            throws AuctionParticipationException {

        // Basic input validation
        if (userId == null || userId.isBlank()) {
            throw new AuctionParticipationException("User  ID cannot be empty");
        }

        if (teamId == null || teamId.isBlank()) {
            throw new AuctionParticipationException("Team ID cannot be empty");
        }

        // Business rule validation
        if (bannedParticipants.contains(userId)) {
            throw new AuctionParticipationException("User  is banned from participating");
        }

        if (!userValidator.hasRequiredCredits(userId)) {
            throw new AuctionParticipationException("Insufficient account credits");
        }

        if (auctionManager.getAuctionStatus() != SponsorAuctionManager.AUCTION_LIVE) {
            throw new AuctionParticipationException("No active auction to join");
        }
    }

    private void validateBidConditions(String userId, double amount)
            throws AuctionBidException {

        if (!activeSessions.containsKey(userId)) {
            throw new AuctionBidException("User  not registered in auction");
        }

        ParticipantSession session = activeSessions.get(userId);

        if (session.getConsecutiveRejectedBids() > 2) {
            banParticipant(userId);
            throw new AuctionBidException("Too many invalid bids - account suspended");
        }

        if (amount < auctionManager.getCurrentMinBid()) {
            session.recordRejectedBid();
            throw new AuctionBidException("Bid must be at least $" + auctionManager.getCurrentMinBid());
        }

        if (!userValidator.validateBidAmount(userId, amount)) {
            session.recordRejectedBid();
            throw new AuctionBidException("Bid exceeds available credit limit");
        }
    }

    // ===================
    // Security & Fraud Prevention
    // ===================
    private Sponsor resolveSponsorIdentity(String userId) throws AuctionBidException {
        try {
            Sponsor sponsor = userValidator.getSponsorAccount(userId);
            if (sponsor == null) {
                throw new AuctionBidException("No valid sponsor account found");
            }
            return sponsor;
        } catch (SecurityException e) {
            banParticipant(userId);
            throw new AuctionBidException("Account verification failed", e);
        }
    }

    private void banParticipant(String userId) {
        bannedParticipants.add(userId);
        activeSessions.remove(userId);
        System.err.println("BANNED user: " + userId);
    }

    // ===================
    // Supporting Classes
    // ===================
    private static class ParticipantSession {
        private final String userId;
        private final String teamId;
        private int rejectedBids;
        private final List<Double> bidHistory = new ArrayList<>();

        public ParticipantSession(String userId, String teamId) {
            this.userId = userId;
            this.teamId = teamId;
        }

        public void recordBid(double amount) {
            bidHistory.add(amount);
            rejectedBids = 0; // Reset counter on successful bid
        }

        public void recordRejectedBid() {
            rejectedBids++;
        }

        public int getConsecutiveRejectedBids() {
            return rejectedBids;
        }

        public String getTeamId() {
            return teamId;
        }
    }

    // ===================
    // Custom Exceptions
    // ===================
    public static class AuctionParticipationException extends Exception {
        public AuctionParticipationException(String message) {
            super(message);
        }

        public AuctionParticipationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AuctionBidException extends Exception {
        public AuctionBidException(String message) {
            super(message);
        }

        public AuctionBidException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

// Mock validation service (would be implemented separately)
class UserValidator {
    public boolean hasRequiredCredits(String userId) {
        // Actual implementation would check database
        return true;
    }

    public boolean validateBidAmount(String userId, double amount) {
        // Check against credit limits
        return amount <= 100_000.00; // Example limit
    }

    public Sponsor getSponsorAccount(String userId) throws SecurityException {
        // Verify user-sponsor relationship
        return new Sponsor("Sponsor_" + userId, "Sponsor Name", 100_000.00);
    }
}

