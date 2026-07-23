package com.oryxos;

import java.util.List;

/**
 * Memory Service - unified facade for three-layer memory
 */
public interface MemoryService {

    /**
     * Get memory context for prompt injection
     */
    String getContextForPrompt(Session session);

    /**
     * Save to long-term memory
     * @param content content to save
     * @param scope CORE or ARCHIVAL
     */
    void save(String content, MemoryScope scope);

    /**
     * Recall by keyword
     * @param keyword keyword to search
     * @return matching entries
     */
    List<String> recallByKeyword(String keyword);

    /**
     * Get all long-term memory
     */
    String getAllMemory();

    enum MemoryScope {
        CORE,    // Core memory - never truncated
        ARCHIVAL // Archival memory - subject to truncation
    }
}
