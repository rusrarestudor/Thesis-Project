package com.example.phonesensorserver.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_data")
public class ClientData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientDataID;

    @Column(nullable = false)
    private float accX ;

    @Column(nullable = false)
    private float accY;

    @Column(nullable = false)
    private float accZ;

    @Column(nullable = false)
    private float pulse;

    @Column(nullable = false, length = 100)
    private LocalDateTime timeStamp;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private int height;

    @Column(nullable = false)
    private String exerciseType;

    public ClientData(float accX, float accY, float accZ, float pulse, LocalDateTime timeStamp, int weight, int height, String exerciseType) {
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.pulse = pulse;
        this.timeStamp = timeStamp;
        this.weight = weight;
        this.height = height;
        this.exerciseType = exerciseType;
    }

    public ClientData(){}

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

    public float getPulse() {return pulse;}

    public void setPulse(float pulse) {this.pulse = pulse;}
}
