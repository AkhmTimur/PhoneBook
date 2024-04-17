package com.example.phonebook.dao;

import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.example.phonebook.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@Testcontainers
class PhoneNumberDAOTest {

    private PersonDAO personDAO;
    private PhoneNumberDAO phoneNumberDAO;
    private static final Connection connection = DatabaseConnection.getConnection();

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @BeforeEach
    void setUp() {
        DatabaseConnection.setURL(postgreSQLContainer.getJdbcUrl());
        phoneNumberDAO = new PhoneNumberDAO();
        personDAO = new PersonDAO();
    }

    @Test
    void testGetPersonPhones() {
        PhoneNumber pn = new PhoneNumber("987654321", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));

        int id = personDAO.addPerson(person);

        assertEquals(person.getPhoneNumbers(), phoneNumberDAO.getPersonPhone(id));
    }

    @Test
    void testGetPeoplePhones() {
        PhoneNumber pn = new PhoneNumber("987-654-32-10", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));
        PhoneNumber pn1 = new PhoneNumber("225-55-55", "work");
        Person person1 = new Person("John", "Doevich", 30, List.of(pn1));

        int id = personDAO.addPerson(person);
        int id1 = personDAO.addPerson(person1);

        assertEquals(pn, phoneNumberDAO.getPeoplePhones().get(id).get(0));
        assertEquals(pn1, phoneNumberDAO.getPeoplePhones().get(id1).get(0));
    }

    @Test
    void testGetPersonPhone() {
        PhoneNumber pn = new PhoneNumber("987-654-32-10", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));
        int id = personDAO.addPerson(person);
        person.setId(id);

        List<PhoneNumber> result = phoneNumberDAO.getPersonPhone(id);
        PhoneNumber phoneNumber = result.get(0);
        assertEquals(pn.getDigits(), phoneNumber.getDigits());
        assertEquals(pn.getPhoneType(), phoneNumber.getPhoneType());
    }

    @Test
    void testAddPersonPhoneNumber() {
        PhoneNumber pn = new PhoneNumber("987654321", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));

        int id = personDAO.addPerson(person);
        phoneNumberDAO.addPersonPhoneNumber(Collections.singletonList(pn), id);

        List<PhoneNumber> result = phoneNumberDAO.getPersonPhone(id);
        PhoneNumber phoneNumber = result.get(0);
        assertEquals(pn.getDigits(), phoneNumber.getDigits());
        assertEquals(pn.getPhoneType(), phoneNumber.getPhoneType());
    }

    @Test
    void testUpdatePhoneNumber() {
        PhoneNumber pn = new PhoneNumber("987654321", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));

        int id = personDAO.addPerson(person);
        PhoneNumber updatedPhoneNumber = new PhoneNumber("123456789", "work");
        phoneNumberDAO.updatePhoneNumber(Collections.singletonList(updatedPhoneNumber), id);

        PhoneNumber phoneNumber = phoneNumberDAO.getPersonPhone(id).get(0);
        assertEquals(updatedPhoneNumber.getDigits(), phoneNumber.getDigits());
        assertEquals(updatedPhoneNumber.getPhoneType(), phoneNumber.getPhoneType());
    }

    @Test
    void testUpdatePersonPhoneNumber() {
        PhoneNumber pn = new PhoneNumber("987654321", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));

        int id = personDAO.addPerson(person);
        PhoneNumber updatedPhoneNumber = new PhoneNumber("123456789", "work");
        phoneNumberDAO.updatePersonPhoneNumber(Collections.singletonList(updatedPhoneNumber), id);

        PhoneNumber phoneNumber = phoneNumberDAO.getPersonPhone(id).get(0);
        assertEquals(updatedPhoneNumber.getDigits(), phoneNumber.getDigits());
        assertEquals(updatedPhoneNumber.getPhoneType(), phoneNumber.getPhoneType());
    }

    @Test
    void testDeletePersonPhoneNumbers() {
        PhoneNumber pn = new PhoneNumber("987654321", "home");
        Person person = new Person("John", "Doe", 30, List.of(pn));

        int id = personDAO.addPerson(person);
        phoneNumberDAO.deletePersonPhoneNumbers(id);

        List<PhoneNumber> result = phoneNumberDAO.getPersonPhone(id);
        assertTrue(result.isEmpty());
    }
}