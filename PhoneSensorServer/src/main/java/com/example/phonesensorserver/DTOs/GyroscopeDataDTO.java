package com.example.phonesensorserver.DTOs;

import com.example.phonesensorserver.entities.ClientData;

import javax.persistence.*;
import java.time.LocalDateTime;

public class GyroscopeDataDTO {

    public Long gyroscope_id;

    public Long clientData;

    public float x;

    public float y;

    public float z;

    private LocalDateTime timeStamp;

    public GyroscopeDataDTO(Long clientDataID, float gyroX, float gyroY, float gyroZ, LocalDateTime timestamp) {
        this.clientData = clientDataID;
        this.x = gyroX;
        this.y = gyroY;
        this.z = gyroZ;
        this.timeStamp = timestamp;
    }
}
