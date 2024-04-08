package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PhoneBook {
    Map<String, String> personPhoneNumbers;
}
