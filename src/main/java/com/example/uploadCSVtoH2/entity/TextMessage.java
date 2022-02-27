package com.example.uploadCSVtoH2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextMessage {
    @Id
    long messageId;

    String text;

    long sender;

    long receiver;

    @ManyToOne
    @JoinColumn (name="position_positionId")
    Position position;
}
