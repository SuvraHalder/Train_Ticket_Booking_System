package com.suvra.trainbooking.util;
import java.sql.Connection;

public class DbTest {

    public static void main(String[] args) {

        try (Connection connection = Db.getConnection()) {

            System.out.println("Database connection successful!");
            System.out.println("Database: " + connection.getCatalog());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}