package com.oryxos.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * LongTermMemory - file-based memory storage with keyword and regex search.
 * Memory is stored in .oryxos/memory/MEMORY.md
 */
public class LongTermMemory {

    private static final int MAX_MEMORY_SIZE = 4000;

    private final Path memoryFile;

    public LongTermMemory(String workspaceRoot) {
        this.memoryFile = Paths.get(workspaceRoot, ".oryxos", "memory", "MEMORY.md");
        ensureMemoryFileExists();
    }

    private void ensureMemoryFileExists() {
        try {
            if (!Files.exists(memoryFile)) {
                Files.createDirectories(memoryFile.getParent());
                Files.createFile(memoryFile);
                // Write initial structure
                Files.writeString(memoryFile, "## 核心记忆\n\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize memory file: " + memoryFile, e);
        }
    }

    /**
     * Append content to memory file.
     * @param content content to append
     */
    public void append(String content) {
        try {
            String datedEntry = String.format("\n### %s\n%s\n",
                java.time.LocalDate.now().toString(),
                content);

            String existing = Files.readString(memoryFile);

            // Truncate if would exceed max size
            if (existing.length() + datedEntry.length() > MAX_MEMORY_SIZE * 2) {
                existing = truncateMemory(existing);
            }

            Files.writeString(memoryFile, existing + datedEntry);
        } catch (IOException e) {
            throw new RuntimeException("Failed to append to memory: " + memoryFile, e);
        }
    }

    /**
     * Search memory by keyword (case-insensitive contains).
     * @param keyword search keyword
     * @return matched lines
     */
    public List<String> searchByKeyword(String keyword) {
        try {
            String content = Files.readString(memoryFile);
            String lowerContent = content.toLowerCase();
            String lowerKeyword = keyword.toLowerCase();

            List<String> matched = new ArrayList<>();
            for (String line : content.split("\n")) {
                if (line.toLowerCase().contains(lowerKeyword)) {
                    matched.add(line);
                }
            }
            return matched;
        } catch (IOException e) {
            throw new RuntimeException("Failed to search memory: " + memoryFile, e);
        }
    }

    /**
     * Search memory by regex pattern.
     * @param regex regex pattern
     * @return matched lines
     */
    public List<String> searchByRegex(String regex) {
        try {
            String content = Files.readString(memoryFile);
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            List<String> matched = new ArrayList<>();
            for (String line : content.split("\n")) {
                if (pattern.matcher(line).find()) {
                    matched.add(line);
                }
            }
            return matched;
        } catch (IOException | PatternSyntaxException e) {
            throw new RuntimeException("Failed to search memory by regex: " + e.getMessage(), e);
        }
    }

    /**
     * Get full memory content.
     * @return memory content
     */
    public String getContent() {
        try {
            return Files.readString(memoryFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read memory: " + memoryFile, e);
        }
    }

    private String truncateMemory(String content) {
        // Keep only the most recent half
        int midpoint = content.length() / 2;
        int sectionStart = content.indexOf("\n### ", midpoint);
        if (sectionStart > 0) {
            return content.substring(sectionStart);
        }
        return content.substring(Math.max(0, content.length() - MAX_MEMORY_SIZE));
    }

    public Path getMemoryFile() {
        return memoryFile;
    }
}
