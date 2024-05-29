package com.inholland.bankapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionDto {
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
