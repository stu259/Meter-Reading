package com.api.meterReading.service;

import com.api.meterReading.Repository.AccountRepository;
import com.api.meterReading.Repository.ElectricMeterReadingRepository;
import com.api.meterReading.Repository.GasMeterReadingRepository;
import com.api.meterReading.model.Account;
import com.api.meterReading.model.ElectricReading;
import com.api.meterReading.model.GasReading;
import com.api.meterReading.model.MeterReadingSubmission;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final GasMeterReadingRepository gasMeterReadingRepository;
    private final ElectricMeterReadingRepository electricMeterReadingRepository;

    private final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public AccountService(AccountRepository accountRepository, GasMeterReadingRepository gasMeterReadingRepository, ElectricMeterReadingRepository electricMeterReadingRepository) {
        this.accountRepository = accountRepository;
        this.gasMeterReadingRepository = gasMeterReadingRepository;
        this.electricMeterReadingRepository = electricMeterReadingRepository;
    }

    public Iterable<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    public Account findAccountById(long accountNumber) {
        return getAccount(accountNumber);
    }

    public Account addAccount(Account account) {
        return accountRepository.save(account);
    }

    private Account getAccount(long accountNumber){
        Optional<Account> account = accountRepository.findById(accountNumber);

        if(account.isPresent()) {
            System.out.println("Account Details: " + account.get());
            updateAccountUsages(account.get());
            return account.get();
        } else {
            return null;
        }
    }

    public Map<Boolean,String> addReading(MeterReadingSubmission meterReadingSubmission) {
        //check account exists
        Map<Boolean,String> map=new HashMap<>();
        Account acc = getAccount(meterReadingSubmission.getAccountId());
        if(acc == null){
            map.put(false, "Invalid Account Id");
            return map;
        }

        //add relevant reading
        String meterType = meterReadingSubmission.getMeterType().trim().toLowerCase();
        if(meterType.equals("electric")){
            ElectricReading electricReading;
            try {
                electricReading = new ElectricReading();
                electricReading.setMeterId(meterReadingSubmission.getMeterId());
                electricReading.setReading(meterReadingSubmission.getReading());
                electricReading.setDateReading(df.parse(meterReadingSubmission.getDateReading()));
                electricReading.setAccount(acc);
            } catch (ParseException e) {
                e.printStackTrace();
                map.put(false, "Invalid date format. Please use: 'dd-MM-yyyy'");
                return map;
            }
            if(validateElectricReading(acc, electricReading)){
                electricMeterReadingRepository.save(electricReading);
                map.put(true, "Electric reading added");
                return map;
            }
            map.put(false, "Invalid meter reading. Please ensure meter reading is higher than the last and after the previous submission");
            return map;
        }
        if(meterType.equals("gas")){
            GasReading gasReading;
            try {
                gasReading = new GasReading();
                gasReading.setMeterId(meterReadingSubmission.getMeterId());
                gasReading.setReading(meterReadingSubmission.getReading());
                gasReading.setDateReading(df.parse(meterReadingSubmission.getDateReading()));
                gasReading.setAccount(acc);
            } catch (ParseException e) {
                e.printStackTrace();
                map.put(false, "Invalid date format. Please use: 'dd-MM-yyyy'");
                return map;
            }
            if(validateGasReading(acc, gasReading)) {
                gasMeterReadingRepository.save(gasReading);
                map.put(true, "Gas reading added");
                return map;
            }
            map.put(false, "Invalid meter reading. Please ensure meter reading is higher than the last and after the previous submission");
            return map;
        }
        map.put(false, "Invalid meter type. Please choose 'electric' or 'gas'");
        return map;
    }

    public Map<Boolean,String> addMultipleReadings(List<MeterReadingSubmission> meterReadingSubmissions) {
        //check account exists
        Map<Boolean,String> map=new HashMap<>();
        for(MeterReadingSubmission meterReadingSubmission: meterReadingSubmissions) {
            Account acc = getAccount(meterReadingSubmission.getAccountId());
            if (acc == null) {
                map.put(false, "Invalid Account Id: " + meterReadingSubmission.getAccountId());
                return map;
            }

            //add relevant reading
            String meterType = meterReadingSubmission.getMeterType().trim().toLowerCase();
            if (meterType.equals("electric")) {
                ElectricReading electricReading;
                try {
                    electricReading = new ElectricReading();
                    electricReading.setMeterId(meterReadingSubmission.getMeterId());
                    electricReading.setReading(meterReadingSubmission.getReading());
                    electricReading.setDateReading(df.parse(meterReadingSubmission.getDateReading()));
                    electricReading.setAccount(acc);
                } catch (ParseException e) {
                    e.printStackTrace();
                    map.put(false, "Invalid date format: " + meterReadingSubmission.getDateReading() + ". Please use: 'dd-MM-yyyy'");
                    return map;
                }
                if (validateElectricReading(acc, electricReading)) {
                    electricMeterReadingRepository.save(electricReading);
                }else{
                    map.put(false, "Invalid meter reading: " + meterReadingSubmission.getReading() + ". Please ensure meter reading is higher than the last and after the previous submission");
                    return map;
                }
            }
            else if (meterType.equals("gas")) {
                GasReading gasReading;
                try {
                    gasReading = new GasReading();
                    gasReading.setMeterId(meterReadingSubmission.getMeterId());
                    gasReading.setReading(meterReadingSubmission.getReading());
                    gasReading.setDateReading(df.parse(meterReadingSubmission.getDateReading()));
                    gasReading.setAccount(acc);
                } catch (ParseException e) {
                    e.printStackTrace();
                    map.put(false, "Invalid date format: " + meterReadingSubmission.getDateReading() + ".Please use: 'dd-MM-yyyy'");
                    return map;
                }
                if (validateGasReading(acc, gasReading)) {
                    gasMeterReadingRepository.save(gasReading);
                }else {
                    map.put(false, "Invalid meter reading: " + meterReadingSubmission.getReading() + ". Please ensure meter reading is higher than the last and after the previous submission");
                    return map;
                }
            }else{
                map.put(false, "Invalid meter type: " + meterReadingSubmission.getMeterType() + ". Please choose 'electric' or 'gas'");
                return map;
            }
        }
        map.put(true, "All readings added");
        return map;
    }

    private boolean validateGasReading(Account acc, GasReading newReading){
        GasReading lastGasReading = acc.getGasReadings().get(acc.getGasReadings().size() - 1);
        //check date
        if(lastGasReading.getDateReading().after(newReading.getDateReading())){
            return false;
        }
        //check reading
        return lastGasReading.getReading() < newReading.getReading();
    }

    private boolean validateElectricReading(Account acc, ElectricReading newReading){
        ElectricReading lastGasReading = acc.getElectricReadings().get(acc.getElectricReadings().size() - 1);
        //check date
        if(lastGasReading.getDateReading().after(newReading.getDateReading())){
            return false;
        }
        //check reading
        return lastGasReading.getReading() < newReading.getReading();
    }

    public void updateAccountUsages(Account acc){
        //update all dynamic readings
        usageSinceLastRead(acc);
        periodSinceLastRead(acc);
        averageDailyUsage(acc);
        averageAccountUsage(acc);
    }

    //updates usage of gas and electic since last read respectively
    private void usageSinceLastRead(Account acc){
        //update gasUsageSinceLastRead
        int numGasReadings = acc.getGasReadings().size();
        if(numGasReadings >= 2){
            acc.setGasUsageSinceLastRead(acc.getGasReadings().get(numGasReadings - 1).getReading() - acc.getGasReadings().get(numGasReadings - 2).getReading());
        }
        //update electricUsageSinceLastRead
        int numElectricReadings = acc.getElectricReadings().size();
        if(numElectricReadings >= 2){
            acc.setElectricUsageSinceLastRead(acc.getElectricReadings().get(numElectricReadings - 1).getReading() - acc.getElectricReadings().get(numElectricReadings - 2).getReading());
        }
    }

    //updates number of days since last submission of gas and electic readings
    private void periodSinceLastRead(Account acc){
        //get current date
        Date currentDate = new Date();
        //update periodSinceLastGasRead
        Date lastGasReadingDate = acc.getGasReadings().get(acc.getGasReadings().size() - 1).getDateReading();
        acc.setPeriodSinceLastGasRead(daysBetweenDates(currentDate, lastGasReadingDate));

        //update periodSinceLastElectricRead
        Date lastElecReadingDate = acc.getElectricReadings().get(acc.getElectricReadings().size() - 1).getDateReading();
        acc.setPeriodSinceLastElectricRead(daysBetweenDates(currentDate, lastElecReadingDate));
    }

    //updates average usage per day for gas and electric
    private void averageDailyUsage(Account acc){
        List<GasReading> gasReadings = acc.getGasReadings();
        int numGasReadings = gasReadings.size();
        if(numGasReadings >= 2){
            double totalGasUsage = gasReadings.get(numGasReadings - 1).getReading() - gasReadings.get(0).getReading();
            Date firstReading = gasReadings.get(0).getDateReading();
            Date lastReading = gasReadings.get(numGasReadings - 1).getDateReading();
            acc.setAvgGasDailyUsage(totalGasUsage/daysBetweenDates(lastReading, firstReading));
        }
        //update electricUsageSinceLastRead
        List<ElectricReading> electricReadings = acc.getElectricReadings();
        int numElectricReadings = acc.getElectricReadings().size();
        if(numElectricReadings >= 2){
            double totalElectricUsage = electricReadings.get(numGasReadings - 1).getReading() - electricReadings.get(0).getReading();
            Date firstReading = electricReadings.get(0).getDateReading();
            Date lastReading = electricReadings.get(numElectricReadings - 1).getDateReading();
            acc.setAvgElectricDailyUsage(totalElectricUsage/daysBetweenDates(lastReading, firstReading));
        }
    }

    private void averageAccountUsage(Account acc){
        double totalGasAccounts = 0;
        double totalElecAccounts = 0;
        double totalGasAverage = 0;
        double totalElecAverage = 0;
        for(Account account: accountRepository.findAll()){
            averageDailyUsage(account);
            if(account.getAvgGasDailyUsage() > 0){
                totalGasAccounts++;
                totalGasAverage+=account.getAvgGasDailyUsage();
            }
            if(account.getAvgGasDailyUsage() > 0){
                totalElecAccounts++;
                totalElecAverage+=account.getAvgElectricDailyUsage();
            }
        }
        if(acc.getAvgGasDailyUsage() > 0){
            acc.setGasComparisons(acc.getAvgGasDailyUsage() - totalGasAverage/totalGasAccounts);
        }
        if(acc.getAvgElectricDailyUsage() > 0){
            acc.setElecComparisons(acc.getAvgElectricDailyUsage() - totalElecAverage/totalElecAccounts);
        }
    }

    private int daysBetweenDates(Date date1, Date date2){
        long timeDiff = Math.abs(date1.getTime() - date2.getTime());
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        return Math.toIntExact(daysDiff);
    }


}
