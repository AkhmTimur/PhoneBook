package com.example.phonebook.servlet;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.dao.PhoneNumberDAO;
import com.example.phonebook.dto.PersonDto;
import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.mapper.NumberMapper;
import com.example.phonebook.mapper.PersonMapper;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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

@WebServlet("/person")
public class PersonServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(PersonServlet.class);
    private final Gson gson = new Gson();
    private final PersonDAO personDAO = new PersonDAO();
    private final PersonMapper personMapper = new PersonMapper();
    private final PhoneNumberDAO phoneDao = new PhoneNumberDAO();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Integer id = null;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug(e.getMessage());
        }

        if (id != null) {
            getPersonById(response, id);
        } else {
            getPeople(response);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
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

    private void getPersonById(HttpServletResponse response, Integer id) {
        try {
            PrintWriter out = response.getWriter();
            PersonDto personDto = personMapper.personToDto(personDAO.getPersonById(id));
            out.println(personDto);
        } catch (IOException | PersonNotFoundException e) {
            log.debug(e.getMessage());
        }
    }

    private void getPeople(HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            Map<Integer, Person> people = personDAO.getAllPersons();
            Map<Integer, List<PhoneNumber>> phones = phoneDao.getPeoplePhones();
            for (Map.Entry<Integer, List<PhoneNumber>> entry : phones.entrySet()) {
                people.get(entry.getKey()).setPhoneNumbers(entry.getValue());
            }
            if (!people.isEmpty()) {
                for (Person person : people.values()) {
                    out.println(personMapper.personToDto(person));
                }
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            String method = request.getMethod();
            switch (method) {
                case "POST":
                    handlePost(request, response);
                    break;
                case "PUT":
                    handlePut(request, response);
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

    private void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PersonDto personDto = extractPersonFromRequest(request);
        PrintWriter out = response.getWriter();
        int id = personDAO.addPerson(personMapper.dtoToPerson(personDto));
        personDto.setId(id);
        response.setStatus(HttpServletResponse.SC_CREATED);
        out.println("Person Added: " + personDto);
    }

    private void handlePut(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            PersonDto personDto = extractPersonFromRequest(request);
            PrintWriter out = response.getWriter();
            personDAO.updatePerson(id, personMapper.dtoToPerson(personDto));
            personDto.setId(id);
            response.setStatus(HttpServletResponse.SC_OK);
            out.println("Person Updated: " + personDto);
        } catch (PersonNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            try {
                personDAO.getPersonById(id);
                personDAO.deletePerson(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (PersonNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private PersonDto extractPersonFromRequest(HttpServletRequest request) {
        JsonObject jsonObject = extractJsonFromRequest(request);
        int id = -1;
        if(jsonObject.has("id")) {
            id = jsonObject.get("id").getAsInt();
        }
        String name = jsonObject.get("name").getAsString();
        String surname = jsonObject.get("surname").getAsString();
        Integer age = jsonObject.get("age").getAsInt();
        JsonObject phoneNumbersJson = jsonObject.getAsJsonObject("phoneNumbers");
        List<PhoneNumber> phoneNumbers = parsePhoneNumbers(phoneNumbersJson);
        if (id != -1) {
            return new PersonDto(id, name, surname, age, phoneNumbers);
        }
        return PersonDto.builder()
                .name(name)
                .surname(surname)
                .age(age)
                .phoneNumbers(phoneNumbers)
                .build();
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

    private JsonObject extractJsonFromRequest(HttpServletRequest request) {
        String requestBody = null;
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return gson.fromJson(requestBody, JsonObject.class);
    }
}

