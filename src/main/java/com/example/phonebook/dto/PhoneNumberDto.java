package com.example.phonebook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PhoneNumberDto {
    String digits;
    String phoneType;

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "digits='" + digits + '\'' +
                ", type='" + phoneType + '\'' +
                '}';
    }
}
