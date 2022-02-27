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
public class Video {

    @Id
    long videoId;

    long duration;

    @ManyToOne
    @JoinColumn (name="position_positionId")
    Position position;

    String format;

}
