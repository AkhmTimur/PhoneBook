package com.example.phonebook.dao;

import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.example.phonebook.util.DatabaseConnection;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class PersonDAO {

    private static final Logger log = Logger.getLogger(PersonDAO.class);
    private final Connection connection;
    private static final PhoneNumberDAO phoneNumberDAO = new PhoneNumberDAO();

    public PersonDAO() {
        connection = DatabaseConnection.getConnection();
    }

    public int addPerson(Person person) {
        int id = 0;
        String insertPersonSQL = "INSERT INTO person (name, surname, age) VALUES (?, ?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(insertPersonSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getSurname());
            ps.setInt(3, person.getAge());
            ps.execute();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                log.debug("Person added successfully with ID: " + id);
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }

        phoneNumberDAO.addPhoneNumber(person.getPhoneNumbers());
        phoneNumberDAO.addPersonPhoneNumber(person.getPhoneNumbers(), id);
        return id;
    }

    public Map<Integer, Person> getPeople() {
        Map<Integer, Person> result = new HashMap<>();
        Map<Integer, Person> personMap = new HashMap<>();
        String person = "SELECT * FROM person;";
        String phones = "SELECT pnn.person_id, pn.digits, pn.phone_type FROM person_phoneNumbers pnn " +
                "LEFT JOIN phone_numbers pn on pn.digits = pnn.phone_number_digit";
        Map<Integer, List<PhoneNumber>> phoneNumbers = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(person);
             PreparedStatement psPhone = connection.prepareStatement(phones)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                personMap.put(rs.getInt("id"), extractPerson(rs));
            }

            ResultSet phoneRs = psPhone.executeQuery();
            while (phoneRs.next()) {
                int personId = phoneRs.getInt("person_id");
                String digits = phoneRs.getString("digits");
                String phoneType = phoneRs.getString("phone_type");
                List<PhoneNumber> tmp = phoneNumbers.getOrDefault(personId, new ArrayList<>());
                tmp.add(new PhoneNumber(digits, phoneType));
                phoneNumbers.put(personId, tmp);
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }

        for (Map.Entry<Integer, Person> entry : personMap.entrySet()) {
            Integer personId = entry.getKey();
            Person personDtoTmp = entry.getValue();
            personDtoTmp.setPhoneNumbers(phoneNumbers.get(personId));
            result.put(personId, personDtoTmp);
        }
        return result;
    }

    public Person getPersonById(Integer id) {
        String query = "SELECT * FROM person WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Person person = extractPerson(resultSet);
                person.setPhoneNumbers(phoneNumberDAO.getPersonPhone(person.getId()));
                return person;
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
        throw new PersonNotFoundException("Person not found");
    }

    public void updatePerson(int id, Person personDto) {
        String sql = "UPDATE person SET name = ?, surname = ?, age = ? where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, personDto.getName());
            ps.setString(2, personDto.getSurname());
            ps.setInt(3, personDto.getAge());
            ps.setInt(4, id);
            int rowsAffected = ps.executeUpdate();
            log.info("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
        phoneNumberDAO.updatePersonPhoneNumber(personDto.getPhoneNumbers(), id);

        getPersonById(id);
    }

    public void deletePerson(int id) {
        phoneNumberDAO.deletePersonPhoneNumbers(id);
        String delete = "DELETE FROM person where id = ?";
        try (PreparedStatement ps = connection.prepareStatement(delete)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    private Person extractPerson(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String surname = resultSet.getString("surname");
        Integer age = resultSet.getInt("age");
        return new Person(id, name, surname, age, Collections.emptyList());
    }
}


