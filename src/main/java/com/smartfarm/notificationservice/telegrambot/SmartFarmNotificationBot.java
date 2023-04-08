package com.smartfarm.notificationservice.telegrambot;

import com.smartfarm.notificationservice.config.BotProperties;
import com.smartfarm.notificationservice.model.IotSensorInstructionDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.smartfarm.notificationservice.config.BotProperties.*;
import static org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmartFarmNotificationBot extends TelegramLongPollingBot {

    private static final Set<Long> ALL_USER_IDS = new ConcurrentSkipListSet<>();

    private final BotProperties botProperties;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            if (update.getMessage().getText().equals(START_COMMAND)) {
                log.info("User: {} just started using notification bot", chatId);
                String text = """
                        Hello! 👋

                        By using this bot, you can receive alerts concerning any alterations made to your farm.
                        You can control notification with the following commands:
                                                
                        🔔 /subscribe - start receiving notifications.
                        🔕 /unsubscribe - stop receiving notifications.
                        """;

                execute(builder().chatId(chatId).text(text).build());
            } else if (update.getMessage().getText().equals(SUBSCRIBE_COMMAND)) {
                log.info("User: {} just subscribed on notifications", chatId);
                ALL_USER_IDS.add(chatId);
                String text = """
                        🔔 You successfully subscribed for the notifications.
                        """;

                execute(builder().chatId(chatId).text(text).build());
            } else if (update.getMessage().getText().equals(UNSUBSCRIBE_COMMAND)) {
                log.info("User: {} just unsubscribed from notifications", chatId);
                ALL_USER_IDS.remove(chatId);
                String text = """
                        🔕 You successfully unsubscribed for the notifications.
                        """;
                execute(builder().chatId(chatId).text(text).build());
            }
        }
    }

    public void sendNotificationToAllSubscribers(IotSensorInstructionDTO sensorInstructionDTO) {
        String notificationText = """
                💡 The light switcher for unit %s was pressed! Current status: %s
                💧 The water switcher for unit %s was pressed! Current status: %s
                """.formatted(sensorInstructionDTO.getFarmUnitId(),
                sensorInstructionDTO.isSwitchLight() ? "<b>on</b>" : "<b>off</b",
                sensorInstructionDTO.getFarmUnitId(),
                sensorInstructionDTO.isSwitchWater() ? "<b>on</b>" : "<b>off</b");

        ALL_USER_IDS.forEach(chatId -> {
            try {
                execute(builder().chatId(chatId).text(notificationText).parseMode("HTML").build());
            } catch (TelegramApiException e) {
                SendMessage.builder().chatId(chatId).text("Whoops... something went wrong").build();
            }
        });
    }

    @SneakyThrows
    @Override
    public void onRegister() {
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand(START_COMMAND, "start bot"));
        menu.add(new BotCommand(SUBSCRIBE_COMMAND, "start receiving notifications"));
        menu.add(new BotCommand(UNSUBSCRIBE_COMMAND, "stop receiving notifications"));

        execute(new SetMyCommands(menu, new BotCommandScopeDefault(), null));
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}
