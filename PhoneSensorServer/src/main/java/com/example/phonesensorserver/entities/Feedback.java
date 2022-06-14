package com.example.phonesensorserver.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feedback_data")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackID;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String pulseVariation;

    @Column(nullable = false)
    private Long durationInSeconds;

    @Column(nullable = false)
    private Long timeStamp;
}
