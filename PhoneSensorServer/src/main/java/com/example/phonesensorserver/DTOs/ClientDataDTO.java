package com.example.phonesensorserver.DTOs;

import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

public class ClientDataDTO extends RepresentationModel<ClientDataDTO> {

    private Long clientDataID;

    private float accX ;

    private float accY;

    private float accZ;

    private LocalDateTime timeStamp;

    private int weight;

    private int height;

    private String exerciseType;

    public ClientDataDTO(float accX, float accY, float accZ, LocalDateTime timeStamp, int weight, int height, String exerciseType) {
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.timeStamp = timeStamp;
        this.weight = weight;
        this.height = height;
        this.exerciseType = exerciseType;
    }


    public Long getClientDataID() {return clientDataID;}

    public void setClientDataID(Long clientDataID) {this.clientDataID = clientDataID;}

    public float getAccX() {return accX;}

    public void setAccX(float accX) {this.accX = accX;}

    public float getAccY() {return accY;}

    public void setAccY(float accY) {this.accY = accY;}

    public float getAccZ() {return accZ;}

    public void setAccZ(float accZ) {this.accZ = accZ;}

    public LocalDateTime getTimeStamp() {return timeStamp;}

    public void setTimeStamp(LocalDateTime timeStamp) {this.timeStamp = timeStamp;}

    public int getWeight() {return weight;}

    public void setWeight(int weight) {this.weight = weight;}

    public int getHeight() {return height;}

    public void setHeight(int height) {this.height = height;}

    public String getExerciseType() {return exerciseType;}

    public void setExerciseType(String exerciseType) {this.exerciseType = exerciseType;}
}
