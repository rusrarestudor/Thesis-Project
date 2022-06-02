package com.example.phonesensorserver.entities;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "accelerometer_data")
public class AccelerometerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long accelerometer_id;

    @ManyToOne
    @JoinColumn(name = "clientDataID")
    public ClientData clientData;

    @Column(nullable = false)
    public float x;

    @Column(nullable = false)
    public float y;

    @Column(nullable = false)
    public float z;

    @Column(nullable = false, length = 100)
    private LocalDateTime timeStamp;

}
