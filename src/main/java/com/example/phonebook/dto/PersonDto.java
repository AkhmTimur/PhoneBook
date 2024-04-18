package com.example.phonebook.dto;

import com.example.phonebook.model.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PersonDto {
    int id;
    String name;
    String surname;
    Integer age;
    List<PhoneNumber> phoneNumbers;

    public PersonDto(String name, String surname, Integer age, List<PhoneNumber> phoneNumbers) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
    }


    @Override
    public String toString() {
        return "{\"id\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"surname\":\"" + surname + '\"' +
                ",\"age\":" + age  +
                ",\"phoneNumbers\":" + phoneNumbers +
                '}';
    }
}
