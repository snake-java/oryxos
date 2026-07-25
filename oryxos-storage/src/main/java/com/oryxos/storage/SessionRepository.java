package com.oryxos.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Session entity
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    List<Session> findByStatus(String status);

    List<Session> findByProfileNameAndStatus(String profileName, String status);

    List<Session> findByLastActiveAtBefore(Instant threshold);

    Optional<Session> findByChannelAndUserIdAndProfileNameAndStatus(
        String channel, String userId, String profileName, String status);
}
