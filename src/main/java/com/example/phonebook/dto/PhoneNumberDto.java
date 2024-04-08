package com.example.phonebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneNumberDto {
    String digit;
    String phoneType;

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "digits='" + digit + '\'' +
                ", type='" + phoneType + '\'' +
                '}';
    }
}
