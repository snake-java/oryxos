package com.oryxos;

import java.util.Map;

/**
 * Provider Service - LLM provider abstraction
 *
 * Uses explicit Map<String, ChatModel> instead of type scanning.
 * This is a core interface that must be implemented by oryxos-provider module.
 */
public interface ProviderService {

    /**
     * Call LLM with profile and prompt
     * @param profile profile containing provider config
     * @param prompt assembled prompt
     * @return LLM response
     */
    String call(Profile profile, String prompt);

    /**
     * Get available providers
     */
    Map<String, String> getAvailableProviders();
}
