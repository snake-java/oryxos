package com.oryxos.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ScheduledTask entity
 */
@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, String> {

    List<ScheduledTask> findByEnabled(boolean enabled);

    List<ScheduledTask> findByProfileName(String profileName);

    Optional<ScheduledTask> findFirstByEnabledAndNextRunAtBefore(boolean enabled, Instant threshold);
}
