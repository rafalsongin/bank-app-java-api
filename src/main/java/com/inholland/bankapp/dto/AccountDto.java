package com.inholland.bankapp.dto;
import com.inholland.bankapp.model.AccountType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDto {

    @NotNull
    private int accountId;

    @NotNull
    private String IBAN;

    @NotNull
    private AccountType accountType;

    @NotNull
    private float balance;

    @NotNull
    private float absoluteTransferLimit;

    @NotNull
    private float dailyTransferLimit;

    private String customerFullName;

    private float availableDailyAmountForTransfer;
}
