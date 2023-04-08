package com.smartfarm.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotSensorInstructionDTO {

    private String farmUnitId;
    private boolean switchLight;
    private boolean switchWater;
}
