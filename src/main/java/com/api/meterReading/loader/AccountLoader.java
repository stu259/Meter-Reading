package com.api.meterReading.loader;

import com.api.meterReading.Repository.AccountRepository;
import com.api.meterReading.Repository.ElectricMeterReadingRepository;
import com.api.meterReading.Repository.GasMeterReadingRepository;
import com.api.meterReading.model.Account;
import com.api.meterReading.model.ElectricReading;
import com.api.meterReading.model.GasReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class AccountLoader implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final GasMeterReadingRepository gasMeterReadingRepository;
    private final ElectricMeterReadingRepository electricMeterReadingRepository;

    @Autowired
    public AccountLoader(AccountRepository accountRepository, GasMeterReadingRepository gasMeterReadingRepository, ElectricMeterReadingRepository electricMeterReadingRepository) {
        this.accountRepository = accountRepository;
        this.gasMeterReadingRepository = gasMeterReadingRepository;
        this.electricMeterReadingRepository = electricMeterReadingRepository;
    }

    public void run(ApplicationArguments args) throws ParseException {
        //date
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        //Create new account
        Account acc1 = new Account(1);
        Account acc2 = new Account(2);

        //Create new meter readings
        GasReading gas1 = new GasReading(1, 123, 20202, df.parse("05-11-2020"), acc1);
        GasReading gas2 = new GasReading(2, 123, 28184, df.parse("13-11-2020"), acc1);
        GasReading gas3 = new GasReading(3, 123, 38500, df.parse("20-11-2020"), acc1);
        GasReading gas4 = new GasReading(4, 123, 5156, df.parse("25-11-2020"), acc2);
        ElectricReading elec1 = new ElectricReading(1, 123, 1651, df.parse("11-11-2020"), acc1);
        ElectricReading elec2 = new ElectricReading(2, 123, 5165, df.parse("11-11-2020"), acc2);

        accountRepository.save(acc1);
        accountRepository.save(acc2);

        gasMeterReadingRepository.save(gas1);
        gasMeterReadingRepository.save(gas2);
        gasMeterReadingRepository.save(gas3);
        gasMeterReadingRepository.save(gas4);

        electricMeterReadingRepository.save(elec1);
        electricMeterReadingRepository.save(elec2);

    }
}
