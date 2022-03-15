package com.example.phonesensorserver.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_data")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackID;

    @Column(nullable = false, length = 100)
    private LocalDateTime timeStamp;

    @Column(nullable = false)
    private String exerciseType;

    @Column(nullable = false)
    private String pulseDuration;

    @Column(nullable = false)
    private Long durationInSeconds;

}
