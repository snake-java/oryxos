package com.oryxos.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for LlmCall entity
 */
@Repository
public interface LlmCallRepository extends JpaRepository<LlmCall, Long> {

    List<LlmCall> findBySessionId(String sessionId);

    List<LlmCall> findByProvider(String provider);
}
