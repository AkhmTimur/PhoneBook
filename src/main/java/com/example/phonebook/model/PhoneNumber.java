package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PhoneNumber {
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
