package com.example.phonebook.servlet;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.dao.PhoneNumberDAO;
import com.example.phonebook.dto.PersonDto;
import com.example.phonebook.mapper.PersonMapper;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.example.phonebook.servlets.PersonServlet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PersonServletTest {

    private PersonDAO personDAO;
    private PersonMapper personMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PersonServlet personServlet;

    @BeforeEach
    void setUp() {
        personDAO = mock(PersonDAO.class);
        personMapper = mock(PersonMapper.class);
        PhoneNumberDAO phoneNumberDAO = mock(PhoneNumberDAO.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        personServlet = new PersonServlet(personDAO, personMapper, phoneNumberDAO);
    }

    @AfterEach
    void tearDown() {
        stringWriter = null;
        personServlet = null;
    }

    @Test
    void testDoPost() throws Exception {
        String jsonInput = "{\"name\":\"John\",\"surname\":\"Doe\",\"age\":30,\"phoneNumbers\":{\"123456789\":\"mobile\"}}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        when(request.getReader()).thenReturn(reader);
        when(request.getMethod()).thenReturn("POST");

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber("123456789", "mobile"));
        Person person = new Person(1, "John", "Doe", 30, new ArrayList<>());
        when(personMapper.dtoToPerson(Mockito.any())).thenReturn(person);
        when(personDAO.addPerson(Mockito.any())).thenReturn(1);

        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        personServlet.doPost(request, response);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("id", 1);
        jsonResponse.addProperty("name", "John");
        jsonResponse.addProperty("surname", "Doe");
        jsonResponse.addProperty("age", 30);
        JsonArray phoneNumbersArray = new JsonArray();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            JsonObject phoneJson = new JsonObject();
            phoneJson.addProperty("digits", phoneNumber.getDigits());
            phoneJson.addProperty("phoneType", phoneNumber.getPhoneType());
            phoneNumbersArray.add(phoneJson);
        }
        jsonResponse.add("phoneNumbers", phoneNumbersArray);
        stringWriter.flush();
        assertEquals(jsonResponse.toString(), stringWriter.toString());
    }


    @Test
    void testDoGet() throws Exception {
        when(request.getParameter("id")).thenReturn("1");

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber("123456789", "mobile"));
        Person person = new Person(1, "John", "Doe", 30, phoneNumbers);
        PersonDto personDto = new PersonDto(1, "John", "Doe", 30, phoneNumbers);
        when(personDAO.getPersonById(1)).thenReturn(person);
        when(personMapper.personToDto(person)).thenReturn(personDto);

        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        personServlet.doGet(request, response);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("id", 1);
        jsonResponse.addProperty("name", "John");
        jsonResponse.addProperty("surname", "Doe");
        jsonResponse.addProperty("age", 30);
        JsonArray phoneNumbersArray = new JsonArray();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            JsonObject phoneJson = new JsonObject();
            phoneJson.addProperty("digits", phoneNumber.getDigits());
            phoneJson.addProperty("phoneType", phoneNumber.getPhoneType());
            phoneNumbersArray.add(phoneJson);
        }
        jsonResponse.add("phoneNumbers", phoneNumbersArray);
        stringWriter.flush();
        assertEquals(jsonResponse.toString(), stringWriter.toString());
    }

    @Test
    void testDoPut() throws Exception {
        when(request.getMethod()).thenReturn("POST");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "John");
        jsonObject.addProperty("surname", "Doe");
        jsonObject.addProperty("age", 30);
        jsonObject.add("phoneNumbers", new JsonObject());

        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonObject.toString()));
        when(request.getReader()).thenReturn(bufferedReader);

        Person person = new Person(1, "John", "Doe", 30, new ArrayList<>());
        when(personMapper.dtoToPerson(Mockito.any())).thenReturn(person);
        when(personDAO.addPerson(Mockito.any())).thenReturn(1);

        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);

        personServlet.doPost(request, response);

        verify(personDAO).addPerson(person);

        String expectedJsonResponse = "{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\",\"age\":30,\"phoneNumbers\":[]}";
        verify(printWriter).write(expectedJsonResponse);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }


    @Test
    void testDoDelete() {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getMethod()).thenReturn("DELETE");

        personServlet.doDelete(request, response);

        verify(personDAO).deletePerson(1);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

}
