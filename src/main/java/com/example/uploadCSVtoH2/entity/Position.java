package com.example.uploadCSVtoH2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.message.Message;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    long positionId;

    long latitudine;

    long longitudine;

    @OneToMany (mappedBy = "position")
    List<TextMessage> messages = new ArrayList<>();

    @OneToMany (mappedBy = "position")
    List<PhoneCall> phoneCalls = new ArrayList<>();

    public Position(long positionId) {
        this.positionId = positionId;
    }

}
