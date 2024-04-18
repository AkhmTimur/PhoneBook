package com.example.phonebook.servlets;

import com.example.phonebook.dao.PersonDAO;
import com.example.phonebook.dao.PhoneNumberDAO;
import com.example.phonebook.dto.PersonDto;
import com.example.phonebook.exception.PersonNotFoundException;
import com.example.phonebook.mapper.PersonMapper;
import com.example.phonebook.model.Person;
import com.example.phonebook.model.PhoneNumber;
import com.google.gson.*;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/person")
public class PersonServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(PersonServlet.class);
    private final Gson gson = new Gson();
    private final PersonDAO personDAO;
    private final PersonMapper personMapper;
    private final PhoneNumberDAO phoneDao;

    public PersonServlet() {
        this.personDAO = new PersonDAO();
        this.personMapper = new PersonMapper();
        this.phoneDao = new PhoneNumberDAO();
    }

    public PersonServlet(PersonDAO personDAO, PersonMapper personMapper, PhoneNumberDAO phoneDao) {
        this.personDAO = personDAO;
        this.personMapper = personMapper;
        this.phoneDao = phoneDao;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = -1;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                log.debug(e.getMessage());
            }
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
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    private void getPersonById(HttpServletResponse response, Integer id) {
        try {
            Person person = personDAO.getPersonById(id);
            if (person != null) {
                PersonDto personDto = personMapper.personToDto(person);
                String json = gson.toJson(personDto);
                setContextTypeAndEncoding(response);
                response.getWriter().write(json);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Person not found");
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }


    private void getPeople(HttpServletResponse response) {
        try {
            Map<Integer, Person> people = personDAO.getPeople();
            Map<Integer, List<PhoneNumber>> phones = phoneDao.getPeoplePhones();
            for (Map.Entry<Integer, List<PhoneNumber>> entry : phones.entrySet()) {
                people.get(entry.getKey()).setPhoneNumbers(entry.getValue());
            }
            List<PersonDto> personDtos = people.values().stream()
                    .map(personMapper::personToDto)
                    .collect(Collectors.toList());
            String json = gson.toJson(personDtos);
            setContextTypeAndEncoding(response);
            response.getWriter().write(json);
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
        }
    }

    private void handlePost(HttpServletRequest request, HttpServletResponse response) {
        try {
            PersonDto personDto = extractPersonFromRequest(request);
            int id = personDAO.addPerson(personMapper.dtoToPerson(personDto));
            personDto.setId(id);
            String json = gson.toJson(personDto);
            setContextTypeAndEncoding(response);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(json);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void handlePut(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            PersonDto personDto = extractPersonFromRequest(request);
            personDAO.updatePerson(id, personMapper.dtoToPerson(personDto));
            personDto.setId(id);
            String json = gson.toJson(personDto);
            setContextTypeAndEncoding(response);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(json);
        } catch (PersonNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = -1;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                log.debug(e.getMessage());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
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
        int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : -1;
        String name = jsonObject.get("name").getAsString();
        String surname = jsonObject.get("surname").getAsString();
        int age = jsonObject.get("age").getAsInt();

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        if (jsonObject.has("phoneNumbers")) {
            JsonElement phoneNumbersElement = jsonObject.get("phoneNumbers");
            if (phoneNumbersElement.isJsonArray()) {
                JsonArray phoneNumbersArray = phoneNumbersElement.getAsJsonArray();
                for (JsonElement element : phoneNumbersArray) {
                    JsonObject phoneObject = element.getAsJsonObject();
                    String number = phoneObject.get("number").getAsString();
                    String type = phoneObject.get("type").getAsString();
                    phoneNumbers.add(new PhoneNumber(number, type));
                }
            } else if (phoneNumbersElement.isJsonObject()) {
                JsonObject phoneNumbersObject = phoneNumbersElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : phoneNumbersObject.entrySet()) {
                    String number = entry.getKey();
                    String type = entry.getValue().getAsString();
                    phoneNumbers.add(new PhoneNumber(number, type));
                }
            }
        }

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

    private JsonObject extractJsonFromRequest(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            log.debug(e.getMessage());
            return new JsonObject();
        }
    }

    private static void setContextTypeAndEncoding(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }
}


