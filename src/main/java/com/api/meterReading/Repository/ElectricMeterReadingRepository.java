package com.api.meterReading.Repository;

import com.api.meterReading.model.ElectricReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricMeterReadingRepository extends JpaRepository<ElectricReading, Long> {}
