package com.example.phonebook.dao;

import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.model.Person;
import com.example.phonebook.util.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PersonDAOTest {
    private PersonDAO personDAO;
    private static final Connection connection = DatabaseConnection.getConnection();

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @BeforeEach
    public void setUp() throws SQLException {
        DatabaseConnection.setURL(postgreSQLContainer.getJdbcUrl());
        dropTables();
        createTables();
        personDAO = new PersonDAO();
    }

    @AfterEach
    public void afterEach() {
        dropTables();
    }

    @Test
    void testGetPersonById() {
        Person person = new Person("John", "Doe", 30, Collections.emptyList());
        int id = personDAO.addPerson(person);
        Person retrievedPerson = personDAO.getPersonById(id);
        assertNotNull(retrievedPerson);
        assertEquals(id, retrievedPerson.getId());
        assertEquals("John", retrievedPerson.getName());
        assertEquals("Doe", retrievedPerson.getSurname());
        assertEquals(30, retrievedPerson.getAge());
    }

    @Test
    void testGetPeople() {
        Map<Integer, Person> people = personDAO.getPeople();
        assertNotNull(people);
        assertTrue(people.isEmpty());
    }

    @Test void getAllPeople() {
        Person person1 = new Person("John", "Doe", 30, Collections.emptyList());
        Person person2 = new Person("Jane", "Doe", 25, Collections.emptyList());
        Person person3 = new Person("Alice", "Smith", 35, Collections.emptyList());

        int person1Id = personDAO.addPerson(person1);
        person1.setId(person1Id);
        int person2Id = personDAO.addPerson(person2);
        person2.setId(person2Id);
        int person3Id = personDAO.addPerson(person3);
        person3.setId(person3Id);

        Map<Integer, Person> map = personDAO.getPeople();

        assertEquals(map.get(person1Id).getName(), person1.getName());
        assertEquals(map.get(person1Id).getSurname(), person1.getSurname());
        assertEquals(map.get(person1Id).getAge(), person1.getAge());

        assertEquals(map.get(person2Id).getName(), person2.getName());
        assertEquals(map.get(person2Id).getSurname(), person2.getSurname());
        assertEquals(map.get(person2Id).getAge(), person2.getAge());

        assertEquals(map.get(person3Id).getName(), person3.getName());
        assertEquals(map.get(person3Id).getSurname(), person3.getSurname());
        assertEquals(map.get(person3Id).getAge(), person3.getAge());
    }

    @Test
    void testUpdatePerson() {
        Person person = new Person("John", "Doe", 30, Collections.emptyList());
        int id = personDAO.addPerson(person);

        Person updatedPerson = new Person(id, "Jane", "Smith", 35, Collections.emptyList());
        personDAO.updatePerson(id, updatedPerson);

        Person retrievedPerson = personDAO.getPersonById(id);
        assertNotNull(retrievedPerson);
        assertEquals(id, retrievedPerson.getId());
        assertEquals("Jane", retrievedPerson.getName());
        assertEquals("Smith", retrievedPerson.getSurname());
        assertEquals(35, retrievedPerson.getAge());
    }

    @Test
    void testDeletePerson() {
        Person person = new Person("John", "Doe", 30, Collections.emptyList());
        int id = personDAO.addPerson(person);

        personDAO.deletePerson(id);

        assertThrows(PersonNotFoundException.class, () -> personDAO.getPersonById(id));
    }


    private void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String personTable = "CREATE TABLE IF NOT EXISTS person (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "surname VARCHAR(100) NOT NULL," +
                    "age INT" +
                    ")";
            String phoneNumbersTable = "CREATE TABLE IF NOT EXISTS phone_numbers (" +
                    "digits VARCHAR(100) PRIMARY KEY," +
                    "phone_type VARCHAR(100) NOT NULL" +
                    ")";
            String personPhoneNumbers = "CREATE TABLE IF NOT EXISTS person_phonenumbers (" +
                    "person_id INT," +
                    "phone_number_digit VARCHAR(100)," +
                    "FOREIGN KEY (person_id) REFERENCES person(id)," +
                    "FOREIGN KEY (phone_number_digit) REFERENCES phone_numbers(digits)," +
                    "PRIMARY KEY (person_id, phone_number_digit)" +
                    ")";

            statement.executeUpdate(personTable);
            statement.executeUpdate(phoneNumbersTable);
            statement.executeUpdate(personPhoneNumbers);
        }
    }

    static void dropTables() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS person_phoneNumbers");
            statement.executeUpdate("DROP TABLE IF EXISTS phone_numbers");
            statement.executeUpdate("DROP TABLE IF EXISTS person");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}

