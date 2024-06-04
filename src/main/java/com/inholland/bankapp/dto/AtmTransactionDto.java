package com.inholland.bankapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtmTransactionDto {
    @NotNull
    private Double amount;
}
