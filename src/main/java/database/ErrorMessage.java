package database;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;

public class ErrorMessage {

    public static void errorMessage(Exception e) {
        Session session = Database.getHibSesh();
        if (e instanceof NumberFormatException) {
            System.out.println("Error: Please enter a valid number.");
        } else if (e instanceof SQLException) {
            System.out.println("Error: There was a problem with the database connection, please try again.");
        } else if (e instanceof ConstraintViolationException) {
            System.out.println("Error: The input data violates a database constraint, please try again.");
        } else if (e instanceof IllegalArgumentException) {
            System.out.println("Error: Please enter a valid input and try again.");
        } else if (e.getMessage().contains("Invalid string input")) {
            System.out.println("Error: Please enter a valid string input.");
        } else {
            System.out.println("An error has occurred, please try again: " + e.getMessage());
        }
        e.printStackTrace();
        session.getTransaction().rollback();
    }

}
