package com.api.meterReading.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    private long accountId;
    //gas reading
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<GasReading> gasReadings;
    //electric reading
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ElectricReading> electricReadings;

    private double gasUsageSinceLastRead;
    private int periodSinceLastGasRead;
    private double electricUsageSinceLastRead;
    private int periodSinceLastElectricRead;
    private double avgGasDailyUsage;
    private double avgElectricDailyUsage;
    private double gasComparisons;
    private double elecComparisons;

    @CreationTimestamp
    private LocalDateTime dateCreated;
    @UpdateTimestamp
    private LocalDateTime dateUpdated;

//    public Account(int accountId, int gasId, int gasMeterId, int gasReading, int gasDateReading, int elecId, int elecMeterId, int elecReading, int elecDateReading) {
//        this.accountId = accountId;
//        this.gasReadings = Collections.singleton(new MeterReading(gasId, gasMeterId, gasReading, gasDateReading, this));
//        this.electricReadings = Collections.singleton(new MeterReading(elecId, elecMeterId, elecReading, elecDateReading, this));
//    }

    public Account(int accountId) {
        this.accountId = accountId;
    }

}
