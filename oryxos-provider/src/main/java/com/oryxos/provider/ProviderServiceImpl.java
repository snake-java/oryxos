package com.oryxos.provider;

import com.oryxos.OryxTool;
import com.oryxos.Profile;
import com.oryxos.ProviderService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provider Service Implementation
 * Uses explicit Map<String, ChatModel> instead of type scanning
 */
public class ProviderServiceImpl implements ProviderService {

    private final Map<String, ChatModel> modelRegistry;

    public ProviderServiceImpl(List<ChatModel> chatModels) {
        this.modelRegistry = new HashMap<>();
        for (ChatModel model : chatModels) {
            // Register with model name as key
            modelRegistry.put(model.getDefaultOptions().getModel().toLowerCase(), model);
        }
    }

    @Override
    public String call(Profile profile, String prompt) {
        String providerName = profile.provider().name();
        String modelName = profile.provider().model();

        ChatModel model = findModel(providerName, modelName);
        if (model == null) {
            throw new IllegalArgumentException("Provider not found: " + providerName + "/" + modelName);
        }

        ChatClient chatClient = ChatClient.builder(model).build();

        var response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return response != null ? response : "";
    }

    private ChatModel findModel(String providerName, String modelName) {
        // First try exact match
        ChatModel model = modelRegistry.get(modelName.toLowerCase());
        if (model != null) {
            return model;
        }
        // Try provider prefix
        return modelRegistry.get(providerName.toLowerCase() + "-" + modelName.toLowerCase());
    }

    @Override
    public Map<String, String> getAvailableProviders() {
        Map<String, String> providers = new HashMap<>();
        for (Map.Entry<String, ChatModel> entry : modelRegistry.entrySet()) {
            providers.put(entry.getKey(), entry.getValue().getDefaultOptions().getModel());
        }
        return providers;
    }
}
