package com.example.uploadCSVtoH2.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {

    @Id
    public int id;
    public String first_name;
    public String last_name;
    public String email;
    public String gender;
    public String ip_address;

}
