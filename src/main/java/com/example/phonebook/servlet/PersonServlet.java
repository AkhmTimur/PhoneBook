package com.example.phonebook.servlet;

import com.example.phonebook.dao.PostgreSQLConnection;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/person")
@Log4j
public class PersonServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Integer id = null;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug(e.getMessage());
        }

        if(id != null) {
            getPersonById(response, id);
        } else {
            getPeople(response);
        }

    }

    private void getPersonById(HttpServletResponse response, Integer id) {
        Person person = PostgreSQLConnection.getPersonById(id);
        try {
            PrintWriter out = response.getWriter();
            if(person != null) {
                out.println(person);
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void getPeople(HttpServletResponse response) {
        List<Person> people = PostgreSQLConnection.getAllPersons();
        try {
            PrintWriter out = response.getWriter();
            if(!people.isEmpty()) {
                for (Person person : people) {
                    out.println(person);
                }
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);

            String name = jsonObject.get("name").getAsString();
            String surname = jsonObject.get("surname").getAsString();
            Integer age = jsonObject.get("age").getAsInt();
            JsonObject phoneNumbersJson = jsonObject.getAsJsonObject("phoneNumbers");

            List<PhoneNumber> phoneNumbers = parsePhoneNumbers(phoneNumbersJson);

            String method = request.getMethod();
            switch (method) {
                case "POST":
                    handlePost(response, name, surname, age, phoneNumbers);
                    break;
                case "PUT":
                    handlePut(request, response, name, surname, age, phoneNumbers);
                    break;
                case "DELETE":
                    handleDelete(request, response);
                    break;
                default:
                    break;
            }
        } catch (JsonParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug(e.getMessage());
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void handlePost(HttpServletResponse response, String name, String surname, Integer age,
                            List<PhoneNumber> phoneNumbers) throws IOException {
        Person person = new Person(name, surname, age, phoneNumbers);
        PrintWriter out = response.getWriter();
        new PostgreSQLConnection();
        int id = PostgreSQLConnection.addPerson(person);
        person.setId(id);

        response.setStatus(HttpServletResponse.SC_CREATED);

        out.println("Person Added: " + person);

    }

    private void handlePut(HttpServletRequest request, HttpServletResponse response, String name,
                           String surname, Integer age, List<PhoneNumber> phoneNumbers) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Person person = findPersonById(id);
            if (person != null) {
                person.setName(name);
                person.setSurname(surname);
                person.setAge(age);
                person.setPhoneNumbers(phoneNumbers);

                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                out.println("Person Updated: " + person);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Person person = findPersonById(id);
            if (person != null) {

                //persons.remove(person);
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                out.println("Person Deleted: " + person);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<PhoneNumber> parsePhoneNumbers(JsonObject phoneNumbersJson) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        if (phoneNumbersJson != null) {
            for (String key : phoneNumbersJson.keySet()) {
                String phoneType = phoneNumbersJson.get(key).getAsString();
                phoneNumbers.add(new PhoneNumber(key, phoneType));
            }
        }
        return phoneNumbers;
    }

    private Person findPersonById(int id) {
//        for (Person person : persons) {
//            if (person.getId() == id) {
//                return person;
//            }
//        }
        return null;
    }
}
