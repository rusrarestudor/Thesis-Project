package com.example.phonesensorserver.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gyroscope_data")
public class GyrosocopeData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long gyroscope_id;

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
