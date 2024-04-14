package com.example.phonebook.dao;

import org.apache.log4j.Logger;

import java.sql.*;

public class DatabaseConnection {

    private static final Logger log = Logger.getLogger(DatabaseConnection.class);
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection connection;

    static String personTable = "CREATE TABLE IF NOT EXISTS person (" +
            "id SERIAL PRIMARY KEY," +
            "name VARCHAR(100) NOT NULL," +
            "surname VARCHAR(100) NOT NULL," +
            "age INT" +
            ")";

    static String phoneNumbersTable = "CREATE TABLE IF NOT EXISTS phone_numbers (" +
            "digits VARCHAR(100) PRIMARY KEY," +
            "phone_type VARCHAR(100) NOT NULL" +
            ")";

    static String personPhoneNumbers = "CREATE TABLE IF NOT EXISTS person_phonenumbers (" +
            "person_id INT," +
            "phone_number_digit VARCHAR(100)," +
            "FOREIGN KEY (person_id) REFERENCES person(id)," +
            "FOREIGN KEY (phone_number_digit) REFERENCES phone_numbers(digits)," +
            "PRIMARY KEY (person_id, phone_number_digit)" +
            ")";

    public static Connection getConnection() {
        loadDBDriver();
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                initializeTables();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void initializeTables() {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate("DROP TABLE person_phoneNumbers");
            statement.executeUpdate("DROP TABLE phone_numbers");
            statement.executeUpdate("DROP TABLE person");
            statement.executeUpdate(personTable);
            statement.executeUpdate(phoneNumbersTable);
            statement.executeUpdate(personPhoneNumbers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void loadDBDriver() {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            log.debug(e.getMessage());
        }
    }
}
