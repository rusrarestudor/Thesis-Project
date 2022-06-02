package com.example.phonesensorserver.DTOs;

import java.time.LocalDateTime;

public class LinearaccDataDTO {

    public Long linearacc_id;

    public Long clientData;

    public float x;

    public float y;

    public float z;

    private LocalDateTime timeStamp;

    public LinearaccDataDTO(Long clientDataID, float linAccX, float linAccY, float linAccZ, LocalDateTime timestamp) {
        this.clientData = clientDataID;
        this.x = linAccX;
        this.y = linAccY;
        this.z = linAccZ;
        this.timeStamp = timestamp;
    }
}
