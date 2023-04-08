package com.smartfarm.notificationservice.controller;

import com.smartfarm.notificationservice.model.IotSensorInstructionDTO;
import com.smartfarm.notificationservice.telegrambot.SmartFarmNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class TelegramBotNotificationController {

    private final SmartFarmNotificationBot smartFarmNotificationBot;

    @PostMapping
    public ResponseEntity<?> sendNotification(@RequestBody IotSensorInstructionDTO sensorInstructionDTO) {
        smartFarmNotificationBot.sendNotificationToAllSubscribers(sensorInstructionDTO);

        return ResponseEntity.ok().build();
    }
}
