package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
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
        return "{\"id\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"surname\":\"" + surname + '\"' +
                ",\"age\":" + age +
                ",\"phoneNumbers\":" + phoneNumbers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(name, person.name) && Objects.equals(surname, person.surname) && Objects.equals(age, person.age) && Objects.equals(phoneNumbers, person.phoneNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, age, phoneNumbers);
    }
}
