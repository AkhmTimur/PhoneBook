package com.example.phonebook;

import com.example.phonebook.dao.PostgreSQLConnection;
import com.example.phonebook.model.Person;
import com.example.phonebook.servlet.PersonServlet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SimpleRESTService {

    public static void main(String[] args) {
        String webappDirLocation = "target/PhoneBook";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context context = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        Tomcat.addServlet(context, "PersonServlet", new PersonServlet());
        context.addServletMappingDecoded("/person", "PersonServlet");

        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.getResponseHeaders().set("Location", "/persons");
                exchange.sendResponseHeaders(301, -1);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }
    }

    static class PersonsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                StringBuilder response = new StringBuilder();
                response.append("<html><body><h1>Persons:</h1><ul>");

                List<Person> persons = PostgreSQLConnection.getAllPersons();
                for (Person person : persons) {
                    response.append("<li>").append("ID: ").append(person.getId())
                            .append(", Name: ").append(person.getName())
                            .append(", Surname: ").append(person.getSurname())
                            .append(", Birthday: ").append(person.getAge())
                            .append("</li>");
                }
                response.append("</ul></body></html>");

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.toString().getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }
}
