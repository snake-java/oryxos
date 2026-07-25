package com.oryxos.provider;

import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Explicit registry for ChatModel instances.
 * Follows Constitution Principle V: No type scanning, explicit Map<String, ChatModel>.
 */
public class ChatModelRegistry {

    private final Map<String, ChatModel> models = new ConcurrentHashMap<>();

    /**
     * Register a ChatModel with explicit name.
     * @param name unique identifier (e.g., "deepseek", "qwen")
     * @param model ChatModel instance
     */
    public void register(String name, ChatModel model) {
        models.put(name.toLowerCase(), model);
    }

    /**
     * Get ChatModel by name.
     * @param name model identifier
     * @return ChatModel or null if not found
     */
    public ChatModel get(String name) {
        return models.get(name.toLowerCase());
    }

    /**
     * Get ChatModel by provider name and model name.
     * @param providerName provider identifier
     * @param modelName model identifier
     * @return ChatModel or null if not found
     */
    public ChatModel get(String providerName, String modelName) {
        // First try exact model name
        ChatModel model = models.get(modelName.toLowerCase());
        if (model != null) {
            return model;
        }
        // Try provider-model combination
        return models.get(providerName.toLowerCase() + "-" + modelName.toLowerCase());
    }

    /**
     * Check if a model is registered.
     * @param name model identifier
     * @return true if registered
     */
    public boolean contains(String name) {
        return models.containsKey(name.toLowerCase());
    }

    /**
     * Get all registered model names.
     * @return array of model names
     */
    public String[] getRegisteredNames() {
        return models.keySet().toArray(new String[0]);
    }
}
