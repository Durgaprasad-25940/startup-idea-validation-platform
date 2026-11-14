package com.startup.dto;

/**
 * AdminStatsResponse holds platform-wide metric counts.
 */
public class AdminStatsResponse {

    private long totalUsers;
    private long totalIdeas;
    private long totalAnalyzedIdeas;

    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalUsers, long totalIdeas, long totalAnalyzedIdeas) {
        this.totalUsers = totalUsers;
        this.totalIdeas = totalIdeas;
        this.totalAnalyzedIdeas = totalAnalyzedIdeas;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalIdeas() { return totalIdeas; }
    public void setTotalIdeas(long totalIdeas) { this.totalIdeas = totalIdeas; }

    public long getTotalAnalyzedIdeas() { return totalAnalyzedIdeas; }
    public void setTotalAnalyzedIdeas(long totalAnalyzedIdeas) { this.totalAnalyzedIdeas = totalAnalyzedIdeas; }
}
