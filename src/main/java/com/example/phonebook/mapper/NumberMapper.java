package com.example.phonebook.mapper;

import com.example.phonebook.dto.PhoneNumberDto;
import com.example.phonebook.model.PhoneNumber;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NumberMapper {
    public static PhoneNumber dtoToNumber(PhoneNumberDto dto) {
        return PhoneNumber.builder()
                .digits(dto.getDigits())
                .phoneType(dto.getPhoneType())
                .build();
    }

    public static PhoneNumberDto numberToDto(PhoneNumber number) {
        return PhoneNumberDto.builder()
                .digits(number.getDigits())
                .phoneType(number.getPhoneType())
                .build();
    }
}
