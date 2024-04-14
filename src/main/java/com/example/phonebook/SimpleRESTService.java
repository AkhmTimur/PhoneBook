package com.example.phonebook;

import com.example.phonebook.servlet.PersonServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class SimpleRESTService {

    public static void main(String[] args) {
        int port = 8080;
        String webappDirLocation = "target/PhoneBook";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

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
}
