package com.api.meterReading.Repository;

import com.api.meterReading.model.GasReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GasMeterReadingRepository extends JpaRepository<GasReading, Long> {}
