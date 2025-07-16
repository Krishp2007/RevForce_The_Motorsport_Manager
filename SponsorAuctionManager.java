package com.revForce.sponsorship;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class SponsorAuctionManager {
    // =====================
    // Auction Status Constants
    // =====================
    public static final int AUCTION_PRE_AUCTION = 0;
    public static final int AUCTION_LIVE = 1;
    public static final int AUCTION_FROZEN = 2;
    public static final int AUCTION_CLOSED = 3;
    public static final int AUCTION_COMPLETED = 4;
    public static final int BID_PENDING = 0;
    public static final int BID_QUALIFIED = 1;
    public static final int BID_REJECTED = 2;
    public static final int BID_WON = 3;
    public static final int BID_LOST = 4;
    public static final int CONTRACT_ACTIVE = 0;
    public static final int CONTRACT_TERMINATED = 1;
    public static final int CONTRACT_COMPLETED = 2;

    // =====================
    // Core Data Structures
    // =====================
    private final List<SponsorBid> activeBids = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, SponsorContract> signedContracts = new HashMap<>();
    private final PriorityQueue<SponsorBid> bidPriorityQueue = new PriorityQueue<>(
            (b1, b2) -> Double.compare(b2.getBidAmount(), b1.getBidAmount())
    );
    private final BlockingQueue<AuctionEvent> auctionEventQueue = new LinkedBlockingQueue<>();

    private double currentMinBid = 10000.00;
    private int auctionStatus = AUCTION_CLOSED;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    private final ScheduledExecutorService auctionEngine = Executors.newScheduledThreadPool(3);

    // =====================
    // Core Auction Logic
    // =====================
    public synchronized void startAuction(int durationMinutes) {
        this.auctionStartTime = LocalDateTime.now();
        this.auctionEndTime = auctionStartTime.plusMinutes(durationMinutes);
        this.auctionStatus = AUCTION_LIVE;

        auctionEngine.scheduleAtFixedRate(this::processBids, 0, 5, TimeUnit.SECONDS);
        auctionEngine.scheduleAtFixedRate(this::monitorAuction, 1, 1, TimeUnit.MINUTES);
    }

    public synchronized void submitBid(Sponsor sponsor, String teamId, double amount) throws Exception {
        if (auctionStatus != AUCTION_LIVE) {
            throw new Exception("Bidding not allowed in current auction state");
        }

        if (amount < currentMinBid) {
            throw new Exception("Bid must be ≥ $" + currentMinBid);
        }

        SponsorBid bid = new SponsorBid(sponsor, teamId, amount, BID_PENDING);
        activeBids.add(bid);
        bidPriorityQueue.add(bid);
        recalculateMinBid();
        auctionEventQueue.add(new BidReceivedEvent(bid));
    }

    // =====================
    // Bid Processing
    // =====================
    private void processBids() {
        synchronized (activeBids) {
            Iterator<SponsorBid> it = activeBids.iterator();
            while (it.hasNext()) {
                SponsorBid bid = it.next();
                if (validateBid(bid)) {
                    bid.setStatus(BID_QUALIFIED);
                    auctionEventQueue.add(new BidQualifiedEvent(bid));
                } else {
                    bid.setStatus(BID_REJECTED);
                    it.remove();
                    bidPriorityQueue.remove(bid);
                }
            }
        }
    }

    private boolean validateBid(SponsorBid bid) {
        // Implement validation logic:
        // 1. Sponsor financial check
        // 2. Team eligibility
        // 3. Anti-collusion checks
        return true; // Simplified for example
    }

    // =====================
    // Auction Monitoring
    // =====================
    private void monitorAuction() {
        if (auctionStatus == AUCTION_LIVE && LocalDateTime.now().isAfter(auctionEndTime)) {
            completeAuction();
        }

        // Anti-sniping check
        if (auctionStatus == AUCTION_LIVE && LocalDateTime.now().isAfter(auctionEndTime.minusMinutes(5))) {
            auctionStatus = AUCTION_FROZEN;
            auctionEventQueue.add(new AuctionExtendedEvent(10)); // Extend by 10 min
            auctionEndTime = auctionEndTime.plusMinutes(10);
            auctionStatus = AUCTION_LIVE;
        }
    }

    private void completeAuction() {
        auctionStatus = AUCTION_COMPLETED;
        SponsorBid winningBid = bidPriorityQueue.poll();

        if (winningBid != null) {
            winningBid.setStatus(BID_WON);
            SponsorContract contract = createContract(winningBid);
            signedContracts.put(contract.getContractId(), contract);

            // Notify losers
            activeBids.stream()
                    .filter(bid -> bid != winningBid)
                    .forEach(bid -> bid.setStatus(BID_LOST));

            auctionEventQueue.add(new AuctionCompletedEvent(winningBid));
        }
    }

    // =====================
    // Contract Management
    // =====================
    private SponsorContract createContract(SponsorBid bid) {
        return new SponsorContract(
                "CON-" + System.currentTimeMillis(),
                bid,
                LocalDateTime.now(),
                CONTRACT_ACTIVE
        );
    }

    public void terminateContract(String contractId, String reason) {
        SponsorContract contract = signedContracts.get(contractId);
        if (contract != null && contract.getStatus() == CONTRACT_ACTIVE) {
            contract.setStatus(CONTRACT_TERMINATED);
            // Apply penalty logic here
        }
    }

    // =====================
    // Helper Methods
    // =====================
    private void recalculateMinBid() {
        if (!bidPriorityQueue.isEmpty()) {
            currentMinBid = bidPriorityQueue.peek().getBidAmount() * 0.9;
        }
    }

    public List<SponsorBid> getActiveBidsSnapshot() {
        synchronized (activeBids) {
            return new ArrayList<>(activeBids);
        }
    }

    // =====================
    // Getters for Auction Status and Current Minimum Bid
    // =====================
    public double getCurrentMinBid() {
        return currentMinBid;
    }

    public int getAuctionStatus() {
        return auctionStatus;
    }

    // =====================
    // Supporting Classes
    // =====================
    public static class SponsorBid {
        private final String bidId;
        private final Sponsor sponsor;
        private final String teamId;
        private final double bidAmount;
        private final LocalDateTime bidTime;
        private int status;
        private boolean fraudFlagged;

        public SponsorBid(Sponsor sponsor, String teamId, double bidAmount, int status) {
            this.bidId = "BID-" + System.currentTimeMillis();
            this.sponsor = sponsor;
            this.teamId = teamId;
            this.bidAmount = bidAmount;
            this.bidTime = LocalDateTime.now();
            this.status = status;
            this.fraudFlagged = false;
        }

        public double getBidAmount() {
            return bidAmount;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public Sponsor getSponsor() {
            return sponsor;
        }

        public String getBidId() {
            return bidId;
        }
    }

    public static class SponsorContract {
        private final String contractId;
        private final SponsorBid winningBid;
        private final LocalDateTime signedDate;
        private int status;

        public SponsorContract(String contractId, SponsorBid winningBid, LocalDateTime signedDate, int status) {
            this.contractId = contractId;
            this.winningBid = winningBid;
            this.signedDate = signedDate;
            this.status = status;
        }

        public String getContractId() {
            return contractId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public SponsorBid getWinningBid() {
            return winningBid;
        }
    }

    // Auction Events (base class + concrete implementations)
    public abstract static class AuctionEvent {}
    public static class BidReceivedEvent extends AuctionEvent {
        private final SponsorBid bid;

        public BidReceivedEvent(SponsorBid bid) {
            this.bid = bid;
        }

        public SponsorBid getBid() {
            return bid;
        }
    }

    public static class BidQualifiedEvent extends AuctionEvent {
        private final SponsorBid bid;

        public BidQualifiedEvent(SponsorBid bid) {
            this.bid = bid;
        }

        public SponsorBid getBid() {
            return bid;
        }
    }

    public static class AuctionCompletedEvent extends AuctionEvent {
        private final SponsorBid winningBid;

        public AuctionCompletedEvent(SponsorBid winningBid) {
            this.winningBid = winningBid;
        }

        public SponsorBid getWinningBid() {
            return winningBid;
        }
    }

    public static class AuctionExtendedEvent extends AuctionEvent {
        private final int extensionMinutes;

        public AuctionExtendedEvent(int extensionMinutes) {
            this.extensionMinutes = extensionMinutes;
        }

        public int getExtensionMinutes() {
            return extensionMinutes;
        }
    }
}
