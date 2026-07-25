package com.oryxos.storage;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * ScheduledTask entity - stores cron-based task scheduling configuration.
 */
@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTask {

    @Id
    @Column(name = "task_id", length = 255)
    private String taskId;

    @Column(name = "profile_name", nullable = false, length = 255)
    private String profileName;

    @Column(name = "cron", nullable = false, length = 100)
    private String cron;

    @Column(name = "zone", nullable = false, length = 50)
    private String zone;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "next_run_at", nullable = false)
    private Instant nextRunAt;

    @Column(name = "last_run_at")
    private Instant lastRunAt;

    @Column(name = "last_status", length = 20)
    private String lastStatus;

    @Column(name = "run_count", nullable = false)
    private Integer runCount;

    public ScheduledTask() {}

    public ScheduledTask(String taskId, String profileName, String cron, String zone, String message) {
        this.taskId = taskId;
        this.profileName = profileName;
        this.cron = cron;
        this.zone = zone;
        this.message = message;
        this.enabled = true;
        this.runCount = 0;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getCron() { return cron; }
    public void setCron(String cron) { this.cron = cron; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Instant getNextRunAt() { return nextRunAt; }
    public void setNextRunAt(Instant nextRunAt) { this.nextRunAt = nextRunAt; }

    public Instant getLastRunAt() { return lastRunAt; }
    public void setLastRunAt(Instant lastRunAt) { this.lastRunAt = lastRunAt; }

    public String getLastStatus() { return lastStatus; }
    public void setLastStatus(String lastStatus) { this.lastStatus = lastStatus; }

    public Integer getRunCount() { return runCount; }
    public void setRunCount(Integer runCount) { this.runCount = runCount; }

    public void incrementRunCount() {
        this.runCount++;
        this.lastRunAt = Instant.now();
    }
}
