package com.example.phonesensorserver.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "client_data")
public class ClientData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientDataID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private int height;
}
