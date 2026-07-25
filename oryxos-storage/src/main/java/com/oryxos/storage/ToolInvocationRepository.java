package com.oryxos.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ToolInvocation entity
 */
@Repository
public interface ToolInvocationRepository extends JpaRepository<ToolInvocation, Long> {

    List<ToolInvocation> findBySessionId(String sessionId);

    List<ToolInvocation> findByToolName(String toolName);
}
