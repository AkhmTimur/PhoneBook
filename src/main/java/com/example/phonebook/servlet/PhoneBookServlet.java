package com.example.phonebook.servlet;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.model.Person;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/phoneBook")
public class PhoneBookServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(PhoneBookServlet.class);
    private final Gson gson = new Gson();
    private final PersonDAO personDAO = new PersonDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String surname = request.getParameter("surname");
        try {
            if (surname != null) {
                getPeopleBySurname(response, surname);
            } else {
                getAllPeople(response);
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void getAllPeople(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        Map<Integer, Person> peopleMap = personDAO.getAllPersons();
        List<Person> peopleList = new ArrayList<>(peopleMap.values());

        peopleList.sort((p1, p2) -> p1.getSurname().compareToIgnoreCase(p2.getSurname()));

        String jsonResponse = gson.toJson(peopleList);
        out.print(jsonResponse);
        out.flush();
    }

    private void getPeopleBySurname(HttpServletResponse response, String surname) throws IOException {
        PrintWriter out = response.getWriter();

        Map<Integer, Person> peopleMap = personDAO.getAllPersons();
        List<Person> peopleList = new ArrayList<>(peopleMap.values());

        List<Person> filteredPeople = peopleList.stream()
                .filter(person -> person.getSurname().equalsIgnoreCase(surname))
                .collect(Collectors.toList());

        String jsonResponse = gson.toJson(filteredPeople);
        out.print(jsonResponse);
        out.flush();
    }
}
