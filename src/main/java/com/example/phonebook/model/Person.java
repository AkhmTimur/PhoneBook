package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Person {
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
