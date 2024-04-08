package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
