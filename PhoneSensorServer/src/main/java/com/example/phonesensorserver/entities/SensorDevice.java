package com.example.phonesensorserver.entities;

import javax.persistence.*;

public interface SensorDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = null;

    @ManyToOne
    @JoinColumn(name = "clientDataID")
    public ClientData clientData= null;

    @Column(nullable = false)
    public float x = 0;

    @Column(nullable = false)
    public float y = 0;

    @Column(nullable = false)
    public float z = 0;



}
