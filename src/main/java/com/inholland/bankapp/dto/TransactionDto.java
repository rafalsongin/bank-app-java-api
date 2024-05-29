package com.inholland.bankapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionDto {
    @NotNull
    private String transactionType;

    @NotNull
    private Float amount;

    @NotNull
    private String fromAccount;

    @NotNull
    private String toAccount;

    @NotNull
    private Integer initiatedByUser;

    private String timestamp;
}
