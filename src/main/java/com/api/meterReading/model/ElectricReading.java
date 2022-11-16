package com.api.meterReading.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ElectricReading {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int meterId;
    private double reading;
    private Date dateReading;

    @ManyToOne
    @JoinColumn(name="accountId")
    @JsonBackReference
    private Account account;
}
