package com.example.phonebook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber)) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(digits, that.digits) && Objects.equals(phoneType, that.phoneType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digits, phoneType);
    }
}
