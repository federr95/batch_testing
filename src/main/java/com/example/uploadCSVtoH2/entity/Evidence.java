package com.example.uploadCSVtoH2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {

    @Id
    public long id;
    public String first_name;
    public String last_name;
    public String email;
    public String gender;
    public String ip_address;

}
