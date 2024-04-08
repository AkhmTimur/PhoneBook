package com.example.phonebook.dao;

import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLConnection {

    private static final Logger log = Logger.getLogger(PostgreSQLConnection.class);
    private static final Connection connection = DatabaseConnection.getConnection();

    public PostgreSQLConnection() {
    }

    public static List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        String query = "SELECT * FROM person;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                persons.add(extractPerson(resultSet));
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }

        return persons;
    }

    public static Person getPersonById(Integer id) {
        String query = "SELECT * FROM person WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractPerson(resultSet);
            } else {
                throw new PersonNotFoundException("Person not found");
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static int addPerson(Person person) {
        int id;
        String insertPersonSQL = "INSERT INTO person (name, surname, age) VALUES (?, ?, ?);";
        String insertPhoneNumber = "INSERT INTO phone_numbers (digits, phone_type) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertPersonSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getSurname());
            ps.setInt(3, person.getAge());
            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                log.debug("Вставка успешна");
            } else {
                log.debug("Не удалось вставить данные.");
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                log.debug("Person added successfully with ID: " + id);
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }

        try (PreparedStatement ps = connection.prepareStatement(insertPhoneNumber)) {
            for (PhoneNumber phoneNumber : person.getPhoneNumbers()) {
                ps.setString(1, phoneNumber.getDigits());
                ps.setString(2, phoneNumber.getPhoneType());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
        return 0;
    }

    public void updatePerson(Person person) {
        String sql = "UPDATE person SET name = ?, surname = ?, age = ? where id = ?";
        String sqlPhone = "UPDATE phone_numbers SET digits = ?, phone_type = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getSurname());
            ps.setInt(3, person.getAge());

            int rowsAffected = ps.executeUpdate();
            log.info("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlPhone)) {
            for (PhoneNumber phoneNumber : person.getPhoneNumbers()) {
                ps.setString(1, phoneNumber.getDigits());
                ps.setString(2, phoneNumber.getPhoneType());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    public void deletePerson(int id) {
        String delete = "DELETE FROM person where id = ?";

        try (PreparedStatement ps = connection.prepareStatement(delete)) {
            ps.setInt(1, id);

            int ro
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }

    }


    private static Person extractPerson(ResultSet resultSet) {
        List<PhoneNumber> phoneNumbers = List.of(new PhoneNumber("5555", "home"));
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            Integer age = resultSet.getInt("age");
            return new Person(id, name, surname, age, phoneNumbers);
        } catch (SQLException e) {
            log.debug(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}


