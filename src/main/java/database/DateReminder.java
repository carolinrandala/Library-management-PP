package database;

import entities.Client;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class DateReminder {
    private LocalDateTime dueDate;
    private LocalDateTime issueDate;

    SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public void Rent(LocalDateTime dueDate, LocalDateTime issueDate) {
        this.dueDate = dueDate;
        this.issueDate = issueDate;
    }

    public DateReminder() {

    }

    public void reminder(Client client) {
        long daysUntilDue = java.time.Duration.between(issueDate, dueDate).toDays();
        if (daysUntilDue <= 5) {
            sendReminder("Your due date is coming up in " + daysUntilDue + " days.", client.getEmail());
        }
    }

    public static void sendReminder(String message, String to) {
        String subject = "Due date reminder";

        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.example.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator("username", "password"));
            email.setStartTLSEnabled(true);
            email.setFrom("library@example.com");
            email.setSubject(subject);
            email.setHtmlMsg(message);
            email.addTo(to);
            //email.send();         // works without it
            System.out.println("Reminder sent successfully!");
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}