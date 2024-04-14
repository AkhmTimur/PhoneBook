package com.example.phonebook.dao;

import com.example.phonebook.model.PhoneNumber;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PhoneNumberDAO {

    private static final Logger log = Logger.getLogger(PhoneNumberDAO.class);
    private static final Connection connection = DatabaseConnection.getConnection();

    public PhoneNumberDAO() {
    }

    public Map<Integer, List<PhoneNumber>> getPeoplePhones() {
        Map<Integer, List<PhoneNumber>> result = new HashMap<>();
        String sql = "SELECT ppn.person_id, pn.digits, pn.phone_type FROM phone_numbers pn " +
                "LEFT JOIN person_phonenumbers ppn on pn.digits = ppn.phone_number_digit";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int person_id = rs.getInt("person_id");
                String digits = rs.getString("digits");
                String phoneType = rs.getString("phone_type");

                List<PhoneNumber> tmp = result.get(person_id);
                tmp.add(new PhoneNumber(digits, phoneType));
                result.put(person_id, tmp);
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
        result.put(1, Collections.emptyList());
        return result;
    }

    public List<PhoneNumber> getPhone() {
        List<PhoneNumber> result = new ArrayList<>();
        String sql = "SELECT * FROM phone_numbers";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String digit = rs.getString("digits");
                String phoneType = rs.getString("phone_type");
                result.add(new PhoneNumber(digit, phoneType));
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
        return result;
    }


    public List<PhoneNumber> getPersonPhone(int id) {
        String sql = "SELECT pn.* FROM person_phonenumbers ppn " +
                "LEFT JOIN phone_numbers pn on ppn.phone_number_digit = pn.digits" +
                " WHERE person_id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            List<PhoneNumber> phones = new ArrayList<>();
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String digits = rs.getString("digits");
                String phoneType = rs.getString("phone_type");
                phones.add(new PhoneNumber(digits, phoneType));
            }
            return phones;
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
        return Collections.emptyList();
    }

    public void addPhoneNumber(List<PhoneNumber> phoneNumberList) {
        String insertPhoneNumber = "INSERT INTO phone_numbers (digits, phone_type) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertPhoneNumber)) {
            for (PhoneNumber phoneNumber : phoneNumberList) {
                ps.setString(1, phoneNumber.getDigits());
                ps.setString(2, phoneNumber.getPhoneType());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    public void addPersonPhoneNumber(List<PhoneNumber> phoneNumberList, int personId) {
        String insertPersonPhoneNumber = "INSERT INTO person_phonenumbers (person_id, phone_number_digit) VALUES (?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(insertPersonPhoneNumber)) {
            for (PhoneNumber phoneNumber : phoneNumberList) {
                ps.setInt(1, personId);
                ps.setString(2, phoneNumber.getDigits());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    public void updatePhoneNumber(List<PhoneNumber> phoneNumberList, int personId) {
        deletePersonPhoneNumbers(personId);
        addPhoneNumber(phoneNumberList);
        addPersonPhoneNumber(phoneNumberList, personId);
    }

    public void updatePersonPhoneNumber(List<PhoneNumber> phoneNumberList, int personId) {
        deletePersonPhoneNumbers(personId);
        updatePhoneNumber(phoneNumberList, personId);
    }

    public void deletePersonPhoneNumbers(int id) {
        String phoneNumDel = "DELETE FROM phone_numbers " +
                "WHERE digits in (SELECT phone_number_digit FROM person_phonenumbers WHERE person_id = ?)";
        String personPhoneNumDel = "DELETE FROM person_phonenumbers WHERE person_id = ?";

        try(PreparedStatement personPhone = connection.prepareStatement(personPhoneNumDel);
            PreparedStatement phone = connection.prepareStatement(phoneNumDel)) {
            personPhone.setInt(1, id);
            personPhone.executeUpdate();
            phone.setInt(1, id);
            phone.executeUpdate();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }
}
