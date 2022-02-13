package com.example.uploadCSVtoH2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceEncrypted {

    @Id
    public int id;
    public byte[] first_name;
    public byte[] last_name;
    public byte[] email;
    public byte[] gender;
    public byte[] ip_address;

}
