package com.api.meterReading.controller;

import com.api.meterReading.model.Account;
import com.api.meterReading.model.MeterReadingSubmission;
import com.api.meterReading.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smart/reads")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Iterable<Account> findAllAccounts() {
        return accountService.findAllAccounts();
    }

    @GetMapping("/{ACCOUNTNUMBER}")
    public ResponseEntity<Account> findAccountById(@PathVariable(value = "ACCOUNTNUMBER") long accountNumber) {
        Account acc = accountService.findAccountById(accountNumber);

        if(acc != null) {
            return ResponseEntity.ok().body(acc);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/addaccount")
    public Account addAccount(@Validated @RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PostMapping("/addreading")
    public ResponseEntity<String> addReading(@Validated @RequestBody MeterReadingSubmission meterReadingSubmission) {
        //check account exists
        Map<Boolean,String> pair = accountService.addReading(meterReadingSubmission);
        if(pair.containsKey(false)){
            return ResponseEntity.badRequest().body(pair.get(false));
        }else{
            return ResponseEntity.ok().body(pair.get(true));
        }
    }

    @PostMapping("/addmultiplereadings")
    public ResponseEntity<String> addMultipleReadings(@Validated @RequestBody List<MeterReadingSubmission> meterReadingSubmissions) {
        Map<Boolean,String> pair = accountService.addMultipleReadings(meterReadingSubmissions);
        if(pair.containsKey(false)){
            return ResponseEntity.badRequest().body(pair.get(false));
        }else{
            return ResponseEntity.ok().body(pair.get(true));
        }
    }
}
