package com.inholland.bankapp.dto;

import com.inholland.bankapp.model.AccountApprovalStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerDto extends UserDto {

    @NotBlank
    private String bsn;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private AccountApprovalStatus accountApprovalStatus;
}
