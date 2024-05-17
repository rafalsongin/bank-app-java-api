package com.inholland.bankapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerRegistrationDto {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8, max = 40)
    private String password;
    
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
    
    @NotBlank
    private String bsn;    
    
    @NotBlank
    private String phoneNumber;
}
