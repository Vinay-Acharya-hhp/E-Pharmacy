package com.epharmacy.pharmacy_user_service.customerDto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class PasswordReqestDTO {
    
	 @NotBlank
     private String oldPassword;
	 @NotBlank
     private String newPassword;
	 @NotBlank
     private String confiremPassword;
}
