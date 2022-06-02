package com.example.phonesensorserver.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccelerometerDataDTO {
    public Long accelerometer_id;

    public Long clientData;

    public float x;

    public float y;

    public float z;

    private LocalDateTime timeStamp;

    public AccelerometerDataDTO(Long clientID, float accX, float accY, float accZ, LocalDateTime timestamp) {
        this.clientData = clientID;
        this.x = accX;
        this.y = accY;
        this.z = accZ;
        this.timeStamp = timestamp;
    }
}
