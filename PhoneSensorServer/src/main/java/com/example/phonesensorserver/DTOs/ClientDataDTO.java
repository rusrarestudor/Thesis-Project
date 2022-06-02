package com.example.phonesensorserver.DTOs;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class ClientDataDTO extends RepresentationModel<ClientDataDTO> {

    private Long clientDataID;

    private int weight;

    private int height;
}
