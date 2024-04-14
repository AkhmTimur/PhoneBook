package com.example.phonebook.mapper;

import com.example.phonebook.dto.PersonDto;
import com.example.phonebook.model.Person;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PersonMapper {
    public Person dtoToPerson(PersonDto dto) {
        return Person.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .age(dto.getAge())
                .phoneNumbers(dto.getPhoneNumbers())
                .build();
    }

    public PersonDto personToDto(Person person) {
        return PersonDto.builder()
                .id(person.getId())
                .name(person.getName())
                .surname(person.getSurname())
                .age(person.getAge())
                .phoneNumbers(person.getPhoneNumbers())
                .build();
    }
}
