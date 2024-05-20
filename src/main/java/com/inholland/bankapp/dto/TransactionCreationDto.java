package com.inholland.bankapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionCreationDto {
    @NotNull
    private String transaction_type;

    @NotNull
    private Float amount;

    @NotNull
    private String from_account;

    @NotNull
    private String to_account;

    @NotNull
    private Integer initiated_by_account;

    private String timestamp;
}
