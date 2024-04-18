package com.example.phonebook.servlet;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.model.Person;
import com.example.phonebook.servlets.PhoneBookServlet;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PhoneBookServletTest {

    private PersonDAO personDAO;

    private final Gson gson = new Gson();
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PhoneBookServlet phoneBookServlet;

    @BeforeEach
    void setUp() {
        personDAO = mock(PersonDAO.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        phoneBookServlet = new PhoneBookServlet(personDAO);
    }

    @Test
    void testGetPhoneBook() throws Exception {
        Map<Integer, Person> peopleMap = new HashMap<>();
        peopleMap.put(1, new Person(1, "John", "Doe", 30, new ArrayList<>()));
        peopleMap.put(2, new Person(2, "Jane", "Smith", 25, new ArrayList<>()));

        personDAO.addPerson(peopleMap.get(1));
        personDAO.addPerson(peopleMap.get(2));

        when(request.getParameter("surname")).thenReturn(null);
        when(request.getParameter("sortType")).thenReturn("asc");
        when(personDAO.getPeople()).thenReturn(peopleMap);

        String expectedJsonResponse = "[{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\",\"age\":30,\"phoneNumbers\":[]}," +
                "{\"id\":2,\"name\":\"Jane\",\"surname\":\"Smith\",\"age\":25,\"phoneNumbers\":[]}]";

        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        phoneBookServlet.doGet(request, response);
        writer.flush();

        assertEquals(expectedJsonResponse, stringWriter.toString());
    }

    @Test
    void testGetPeopleBySurname() throws Exception {
        when(request.getParameter("surname")).thenReturn("Doe");
        when(request.getParameter("sortType")).thenReturn("desc");

        Map<Integer, Person> peopleMap = new HashMap<>();
        Person person = new Person(1, "John", "Doe", 30, new ArrayList<>());
        peopleMap.put(1, person);

        when(personDAO.getPeople()).thenReturn(peopleMap);

        List<Person> peopleList = new ArrayList<>(peopleMap.values());
        String expectedJsonResponse = gson.toJson(peopleList);

        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        phoneBookServlet.doGet(request, response);

        assertEquals(expectedJsonResponse, stringWriter.toString());
    }
}
