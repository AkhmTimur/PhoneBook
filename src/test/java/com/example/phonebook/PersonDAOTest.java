package com.example.phonebook;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.dao.PhoneNumberDAO;
import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PersonDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PhoneNumberDAO phoneNumberDAO;

    @InjectMocks
    private PersonDAO personDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testAddPerson_Success() throws SQLException {
        Person person = new Person(1, "John", "Doe", 30, Collections.emptyList());
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        int id = personDAO.addPerson(person);
        assertEquals(1, id);
    }

    @Test
    void testGetAllPersons_Success() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("John");
        when(resultSet.getString("surname")).thenReturn("Doe");
        when(resultSet.getInt("age")).thenReturn(30);

        Map<Integer, Person> result = personDAO.getAllPersons();
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey(1));
        Person person = result.get(1);
        assertEquals(1, person.getId());
        assertEquals("John", person.getName());
        assertEquals("Doe", person.getSurname());
        assertEquals(30, person.getAge());
    }

    @Test
    void testGetPersonById_Success() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("John");
        when(resultSet.getString("surname")).thenReturn("Doe");
        when(resultSet.getInt("age")).thenReturn(30);

        when(phoneNumberDAO.getPersonPhone(anyInt())).thenReturn(Collections.emptyList());

        Person person = personDAO.getPersonById(1);
        assertEquals(1, person.getId());
        assertEquals("John", person.getName());
        assertEquals("Doe", person.getSurname());
        assertEquals(30, person.getAge());
    }

    @Test
    void testGetPersonById_PersonNotFoundException() throws SQLException {
        when(resultSet.next()).thenReturn(false);
        assertThrows(PersonNotFoundException.class, () -> personDAO.getPersonById(1));
    }

    @Test
    void testUpdatePerson_Success() throws SQLException {
        Person person = new Person(1, "John", "Doe", 30, Collections.emptyList());
        personDAO.updatePerson(1, person);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeletePerson_Success() throws SQLException {
        personDAO.deletePerson(1);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeUpdate();
    }
}

