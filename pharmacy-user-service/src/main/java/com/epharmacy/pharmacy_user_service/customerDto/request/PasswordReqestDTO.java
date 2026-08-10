package com.epharmacy.pharmacy_user_service.customerDto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordReqestDTO {
    
     private String oldPassword;
     private String newPassword;
     private String confiremPassword;
}
