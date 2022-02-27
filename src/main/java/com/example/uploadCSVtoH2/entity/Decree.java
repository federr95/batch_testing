package com.example.uploadCSVtoH2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Decree {
    @Id
    long decreeId;

    String date;

    int evidenceNumber;

}
