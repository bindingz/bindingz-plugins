package io.bindingz.contract.plugin.example.gradle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bindingz.context.jackson.PublishConfigurationFactory;
import io.bindingz.context.loader.TypeScanner;

public class BindingzPublishConfigurationFactory implements PublishConfigurationFactory {
    @Override
    public ObjectMapper objectMapper(TypeScanner typeScanner) {
        return new ObjectMapper();
    }
}
