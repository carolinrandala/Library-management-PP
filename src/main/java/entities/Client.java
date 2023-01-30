package entities;


import database.Database;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import menu.Menu;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

@Entity(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int c_id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    static Session session = Database.getHibSesh();

    public static void updateClientByAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, please enter client's ID:");
        int id = scanner.nextInt();

        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Client client = session.get(Client.class, id);
        System.out.println("Client you want to update is:" + session.get(Client.class, client.getC_id()));
        System.out.println("Now there are few thing to update. Stay in line.");

        System.out.println("Enter client's new name:");
        scanner.nextLine();
        String name = scanner.nextLine();

        System.out.println("Enter client's new email address:");
        String email = scanner.next();

        System.out.println("Client with the inserted id will be updated. Please hold for further information...");

        try {
            client.setName(name);
            client.setEmail(email);
            session.merge(client);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
        System.out.println("Thank you for update! Please be mindful that client's new name is: " + name + "and client's new email is: " + email);
    }

    public static void deleteClientByAdmin() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, please enter client's ID:");
        int id = scanner.nextInt();
        System.out.println("Client with the inserted id will be deleted. Please hold for further information...");

        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Client client = session.get(Client.class, id);

        try {
            session.get(Client.class, id);
            session.delete(client);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
        System.out.println("Client with the id: " + id + "is deleted. Thank you!");
    }

    public static void listClient() {
        Session session = Database.getHibSesh();

        try {
            session.beginTransaction();
            List<Client> clients = session.createQuery("from client").list();

            for (Client client : clients) {
                System.out.println(client);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addNewClientByClient() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, dear booklover!");
        System.out.println("For registration, please enter your name: ");
        String name = scanner.nextLine();

        System.out.println("Now please enter your email:");
        String email = scanner.nextLine();

        System.out.println("Thank you! Please hold for further information...");
        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Client client = new Client();

        try {
            client.setName(name);
            client.setEmail(email);
            session.persist(client);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
        System.out.println("Thank you for registration! Please be mindful that your id for this library is:" + session.get(Client.class, client.getC_id()));
    }

    public static void addNewClientByAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, you wonderful admin!");
        System.out.println("For registration, please enter client's name: ");
        String name = scanner.nextLine();

        System.out.println("Now please enter client's email:");
        String email = scanner.nextLine();

        System.out.println("Thank you! Please hold for further information...");
        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Client client = new Client();

        try {

            client.setName(name);
            client.setEmail(email);
            session.persist(client);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
        System.out.println("Thank you for registration! Please be mindful that client's id for this library is:" + session.get(Client.class, client.getC_id()));
    }

    public static void listOfRentedBooksByClientId() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Hello, wonderful client! Please enter your client id: ");
        int client_id = scanner.nextInt();

        System.out.println("Thank you!");
        System.out.println("Here are the books you are currently renting:");

        try {
            session.beginTransaction();
            List<Rent> rents = session.createQuery("from rent where client_id = :client_id", Rent.class)
                    .setParameter("client_id", client_id)
                    .getResultList();
            for (Rent rent : rents) {
                System.out.println(rent);
            }
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Thank you for viewing the list!");
    }

    public static void adminLogin() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("You are trying to enter the admin menu. For that you need to enter the password:");

        String password = scanner.nextLine();

        if (password.equals("greenlibrary123")) {
            Menu.adminMenu();
        } else {
            System.out.println("Sorry, the password is wrong.");
            System.out.println("-----------------------------");
        }
        Menu.mainMenu();
    }
}
