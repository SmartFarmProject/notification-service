package com.smartfarm.notificationservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BotProperties {

    public static final String START_COMMAND = "/start";
    public static final String SUBSCRIBE_COMMAND = "/subscribe";
    public static final String UNSUBSCRIBE_COMMAND = "/unsubscribe";

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;
}
