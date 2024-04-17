package com.example.phonebook;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.dao.PhoneNumberDAO;
import com.example.phonebook.dto.PersonDto;
import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.mapper.PersonMapper;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.example.phonebook.servlets.PersonServlet;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PersonServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PersonDAO personDAO;

    @Mock
    private PhoneNumberDAO phoneNumberDAO;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonServlet personController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoGet_WithValidId_ShouldReturnPerson() throws Exception {
        int id = 1;
        PersonDto personDto = new PersonDto(id, "John", "Doe", 30, Collections.emptyList());
        Person person = new Person(id, "John", "Doe", 30, Collections.emptyList());
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(personDAO.getPersonById(id)).thenReturn(person);
        when(personMapper.personToDto(person)).thenReturn(personDto);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        personController.doGet(request, response);
        writer.flush();

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        assertEquals(personDto.toString(), stringWriter.toString().trim());
    }

    @Test
    void testDoGet_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        when(request.getParameter("id")).thenReturn("invalid_id");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        personController.doGet(request, response);
        writer.flush();

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPost_ShouldAddPersonAndReturnCreated() throws Exception {
        // Arrange
        String jsonRequest = "{\"name\":\"John\",\"surname\":\"Doe\",\"age\":30,\"phoneNumbers\":{\"123456\":\"home\",\"7891011\":\"work\"}}";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "John");
        jsonObject.addProperty("surname", "Doe");
        jsonObject.addProperty("age", 30);
        JsonObject phoneNumbersJson = new JsonObject();
        phoneNumbersJson.addProperty("123456", "home");
        phoneNumbersJson.addProperty("7891011", "work");
        jsonObject.add("phoneNumbers", phoneNumbersJson);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        PersonDto personDto = new PersonDto(1, "John", "Doe", 30, Arrays.asList(new PhoneNumber("123456", "home"), new PhoneNumber("7891011", "work")));
        Person person = new Person(1, "John", "Doe", 30, Arrays.asList(new PhoneNumber("123456", "home"), new PhoneNumber("7891011", "work")));
        when(personMapper.dtoToPerson(any(PersonDto.class))).thenReturn(person);
        when(personDAO.addPerson(person)).thenReturn(1);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        personController.doPost(request, response);
        writer.flush();

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertEquals("Person Added: " + personDto, stringWriter.toString().trim());
    }

    @Test
    void testDoGet_GetPeople_Success() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        Map<Integer, Person> people = new HashMap<>();
        Person person1 = new Person(1, "John", "Doe", 30, Collections.emptyList());
        Person person2 = new Person(2, "Jane", "Smith", 25, Collections.emptyList());
        people.put(1, person1);
        people.put(2, person2);

        when(personDAO.getPeople()).thenReturn(people);
        when(phoneNumberDAO.getPeoplePhones()).thenReturn(Collections.emptyMap());

        personController.doGet(request, response);

        String expectedResponse = "{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\",\"age\":30,\"phoneNumbers\":[]}\n" +
                "{\"id\":2,\"name\":\"Jane\",\"surname\":\"Smith\",\"age\":25,\"phoneNumbers\":[]}\n";
        assertEquals(expectedResponse, stringWriter.toString());
    }

    @Test
    void testDoGet_GetPersonById_Success() throws IOException, PersonNotFoundException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        Person person = new Person(1, "John", "Doe", 30, Collections.emptyList());
        when(personDAO.getPersonById(1)).thenReturn(person);

        when(request.getParameter("id")).thenReturn("1");
        personController.doGet(request, response);

        String expectedResponse = "{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\",\"age\":30,\"phoneNumbers\":[]}";
        assertEquals(expectedResponse, stringWriter.toString());
    }
}

