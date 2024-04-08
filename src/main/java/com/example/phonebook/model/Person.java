package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Person {
    int id;
    String name;
    String surname;
    Integer age;
    List<PhoneNumber> phoneNumbers;

    public Person(String name, String surname, Integer age, List<PhoneNumber> phoneNumbers) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
    }

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
