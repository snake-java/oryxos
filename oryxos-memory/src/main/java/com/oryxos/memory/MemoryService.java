package com.oryxos.memory;

import java.util.List;

/**
 * Memory Service - Unified facade for all memory access.
 * Follows Constitution Principle X: MemoryService as unified facade for three memory layers.
 */
public interface MemoryService {

    /**
     * Save content to long-term memory.
     * @param content content to save
     */
    void saveMemory(String content);

    /**
     * Search long-term memory by keyword.
     * @param query search query
     * @return matched lines
     */
    List<String> recallMemory(String query);

    /**
     * Search long-term memory by regex pattern.
     * @param pattern regex pattern
     * @return matched lines
     */
    List<String> recallMemoryRegex(String pattern);

    /**
     * Get all memory content.
     * @return full memory content
     */
    String getMemoryContent();

    /**
     * Get memory file path.
     * @return path to MEMORY.md
     */
    String getMemoryFilePath();
}
