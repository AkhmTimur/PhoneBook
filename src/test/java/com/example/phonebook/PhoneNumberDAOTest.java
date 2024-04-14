package com.example.phonebook;

import com.example.phonebook.dao.PhoneNumberDAO;
import com.example.phonebook.model.PhoneNumber;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PhoneNumberDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private PhoneNumberDAO phoneNumberDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testGetPeoplePhones_Success() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("person_id")).thenReturn(1);
        when(resultSet.getString("digits")).thenReturn("1234567890");
        when(resultSet.getString("phone_type")).thenReturn("mobile");

        List<PhoneNumber> expected = Collections.singletonList(new PhoneNumber("1234567890", "mobile"));
        assertEquals(expected, phoneNumberDAO.getPeoplePhones().get(1));
    }

    @Test
    void testGetPhone_Success() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("digits")).thenReturn("1234567890");
        when(resultSet.getString("phone_type")).thenReturn("mobile");

        List<PhoneNumber> expected = Collections.singletonList(new PhoneNumber("1234567890", "mobile"));
        assertEquals(expected, phoneNumberDAO.getPhone());
    }

    @Test
    void testGetPersonPhone_Success() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("digits")).thenReturn("1234567890");
        when(resultSet.getString("phone_type")).thenReturn("mobile");

        List<PhoneNumber> expected = Collections.singletonList(new PhoneNumber("1234567890", "mobile"));
        assertEquals(expected, phoneNumberDAO.getPersonPhone(1));
    }

    @Test
    void testAddPhoneNumber_Success() throws SQLException {
        phoneNumberDAO.addPhoneNumber(Collections.singletonList(new PhoneNumber("1234567890", "mobile")));
        verify(preparedStatement, times(1)).executeBatch();
    }

    @Test
    void testAddPersonPhoneNumber_Success() throws SQLException {
        phoneNumberDAO.addPersonPhoneNumber(Collections.singletonList(new PhoneNumber("1234567890", "mobile")), 1);
        verify(preparedStatement, times(1)).executeBatch();
    }

    @Test
    void testUpdatePhoneNumber_Success() throws SQLException {
        phoneNumberDAO.updatePhoneNumber(Collections.singletonList(new PhoneNumber("1234567890", "mobile")), 1);
        verify(preparedStatement, times(2)).executeBatch();
    }

    @Test
    void testUpdatePersonPhoneNumber_Success() throws SQLException {
        phoneNumberDAO.updatePersonPhoneNumber(Collections.singletonList(new PhoneNumber("1234567890", "mobile")), 1);
        verify(preparedStatement, times(2)).executeBatch();
    }

    @Test
    void testDeletePersonPhoneNumbers_Success() throws SQLException {
        phoneNumberDAO.deletePersonPhoneNumbers(1);
        verify(preparedStatement, times(2)).executeUpdate();
    }
}

