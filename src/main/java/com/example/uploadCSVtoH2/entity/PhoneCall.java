package com.example.uploadCSVtoH2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneCall {

    @Id
    long phoneCallId;

    long phoneCallReceiver;

    long phoneCallSender;

    long duration;

    @ManyToOne
    @JoinColumn (name="position_positionId")
    Position position;
}
