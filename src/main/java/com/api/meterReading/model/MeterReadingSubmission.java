package com.api.meterReading.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeterReadingSubmission {
    private int meterId;
    private int reading;
    private String dateReading;
    private String meterType;
    private long accountId;
}
