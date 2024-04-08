package com.example.phonebook.dto;

import com.example.phonebook.model.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PersonDto {
    int id;
    String name;
    String surname;
    Integer age;
    List<PhoneNumber> phoneNumbers;

    @Override
    public String toString() {
        return "Person{id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age='" + age + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }
}
