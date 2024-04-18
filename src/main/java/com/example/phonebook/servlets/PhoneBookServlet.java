package com.example.phonebook.servlets;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.model.Person;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/phoneBook")
@NoArgsConstructor
public class PhoneBookServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(PhoneBookServlet.class);
    private final Gson gson = new Gson();
    private PersonDAO personDAO;

    public PhoneBookServlet(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String surname = request.getParameter("surname");
        String sortType = request.getParameter("sortType"); // Параметр для указания типа сортировки
        try {
            if (surname != null) {
                getPeopleBySurname(response, surname, sortType);
            } else {
                getAllPeople(response, sortType);
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void getAllPeople(HttpServletResponse response, String sortType) throws IOException {
        Map<Integer, Person> peopleMap = personDAO.getPeople();
        List<Person> peopleList = new ArrayList<>(peopleMap.values());

        if ("desc".equalsIgnoreCase(sortType)) {
            peopleList.sort(Comparator.comparing(Person::getSurname, String.CASE_INSENSITIVE_ORDER).reversed());
        } else {
            peopleList.sort(Comparator.comparing(Person::getSurname, String.CASE_INSENSITIVE_ORDER));
        }

        sendJsonResponse(response, peopleList);
    }

    private void getPeopleBySurname(HttpServletResponse response, String surname, String sortType) throws IOException {
        Map<Integer, Person> peopleMap = personDAO.getPeople();
        List<Person> peopleList = new ArrayList<>(peopleMap.values());

        List<Person> filteredPeople = peopleList.stream()
                .filter(person -> person.getSurname().equalsIgnoreCase(surname))
                .collect(Collectors.toList());

        // Сортируем результаты по фамилии
        if ("desc".equalsIgnoreCase(sortType)) {
            filteredPeople.sort(Comparator.comparing(Person::getSurname, String.CASE_INSENSITIVE_ORDER).reversed());
        } else {
            filteredPeople.sort(Comparator.comparing(Person::getSurname, String.CASE_INSENSITIVE_ORDER));
        }

        sendJsonResponse(response, filteredPeople);
    }

    private void sendJsonResponse(HttpServletResponse response, Object responseObject) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = gson.toJson(responseObject);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }
}
