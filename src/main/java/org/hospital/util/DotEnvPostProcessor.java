package org.hospital.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import java.util.HashMap;
import java.util.Map;

public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, Object> dotEnvProps = new HashMap<>();
        dotenv.entries().forEach(entry -> dotEnvProps.put(entry.getKey(), entry.getValue()));

        if (!dotEnvProps.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("dotEnvProperties", dotEnvProps));
        }
    }
}