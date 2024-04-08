package com.example.phonebook.dao;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    static String phone_numbers = "CREATE TABLE IF NOT EXISTS phoneNumbers (" +
            "digits VARCHAR(100) PRIMARY KEY," +
            "phone_type VARCHAR(100) NOT NULL" +
            ")";

    static String person_phone_numbers = "CREATE TABLE IF NOT EXISTS person_phoneNumbers (" +
            "person_id INT," +
            "phone_number_digit VARCHAR(15)," +
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
            statement.executeUpdate(personTable);
            statement.executeUpdate(phoneNumbersTable);
            statement.executeUpdate(personPhoneNumbers);
        } catch (SQLException | NullPointerException e) {
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
