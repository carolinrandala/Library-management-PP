package entities;

import database.Database;
import database.DateReminder;
import lombok.Data;
import lombok.NoArgsConstructor;
import menu.Menu;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;


@Entity(name = "book")
@Data
@NoArgsConstructor
public class Book {
    static Session session = Database.getHibSesh();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int b_id;

    @Column(name = "book_title")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "qty_in_library")
    private int qtyInLibrary;

    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(name = "isAvailable")
    private boolean isAvailable;

    public Book(String title, String category, int qtyInLibrary, int authorId, boolean isAvailable) {
        this.title = title;
        this.category = category;
        this.qtyInLibrary = qtyInLibrary;
        this.author.setA_id(authorId);
        this.isAvailable = isAvailable;
    }

    public static void addBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hi, you wonderful admin! You are here to add a book");
        System.out.println("Enter the title of the book:");
        String title = scanner.nextLine();
        System.out.println("Enter the category of the book:");
        String category = scanner.nextLine();
        System.out.println("Enter the author of the new book:");
        String author_name = scanner.nextLine();
        System.out.println("Enter the quantity of this book:");
        int qty = scanner.nextInt();
        session.beginTransaction();
        Transaction trans = session.getTransaction();
        try {
            Author author = (Author) session.createQuery("from author where author_name = :author_name")
                    .setParameter("author_name", author_name)
                    .uniqueResult();
            if (author == null) {
                author = new Author();
                author.setAuthorName(author_name);
                session.persist(author);
            }
            Book book = new Book();
            book.setTitle(title);
            book.setCategory(category);
            book.setAuthor(author);
            book.setAvailable(true);
            book.setQtyInLibrary(qty);
            session.persist(book);
            session.flush();
            trans.commit();
            System.out.println("Thank you for adding new book to our library!");
            System.out.println("Info about new book is: " + session.get(Book.class, book.getB_id()));
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
    }

    public static void updateBook() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, please enter book's ISBN:");
        int id = scanner.nextInt();

        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Book book = session.get(Book.class, id);
        System.out.println("Book you want to update is:" + session.get(Book.class, book.getB_id()));
        System.out.println("Now there are few thing to update. Stay in line.");

        System.out.println("Enter books's new title:");
        scanner.nextLine();
        String title = scanner.nextLine();

        System.out.println("Enter book's category:");
        String category = scanner.next();

        System.out.println("Enter the quantity of the book:");
        int qty = scanner.nextInt();

        System.out.println("Enter if the book is available in the library or not(true/false)");
        boolean availability = scanner.nextBoolean();

        System.out.println("Book with the inserted id will be updated. Please hold for further information...");

        try {
            book.setTitle(title);
            book.setCategory(category);
            book.setAvailable(true);
            book.setQtyInLibrary(qty);
            session.merge(book);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
        System.out.println("Thank you for update");
        System.out.println("Updated info is here:");
        System.out.println("Title of the book: " + title);
        System.out.println("Category of the book:" + category);
        System.out.println("Quantity in the library: " + qty);
        System.out.println("And the book is available: " + availability);
    }

    public static void deleteBook() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, please enter book's ISBN:");
        int isbn = scanner.nextInt();
        System.out.println("Book with the inserted ISBN will be deleted. Please hold for further information...");
        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Book book = session.get(Book.class, isbn);

        try {
            session.delete(book);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
        System.out.println("Book with the id: " + isbn + "is deleted. Thank you!");
    }

    public static void listBook() {
        System.out.println("Here are the list of books we have in our library:");

        try {
            session.beginTransaction();
            List<Book> books = session.createQuery("from book").list();

            for (Book book : books) {
                System.out.println(book);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Scanner sc = new Scanner(System.in);

    public boolean isBookAvailable(int qty) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Book> query = builder.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);
        query.select(root)
                .where(builder.equal(root.get("b_id"), this.b_id));
        List<Book> books = session.createQuery(query).getResultList();
        if (books.size() == 0) {
            System.out.println("This book does not exist. Please enter a valid ISBN.");
            return false;
        } else {
            if (this.qtyInLibrary > 0 || this.qtyInLibrary > qty) {
                return true;
            } else {
                System.out.println("Sorry this book is out of stock at the moment. Come back later!");
                return false;
            }
        }
    }

    public static void returnBookByIsbn() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your personal id to return book:");
        int client_id = scanner.nextInt();

        System.out.println("Enter a book's isbn to return:");
        int bk = scanner.nextInt();

        System.out.println("How many books you will return?");
        int qty = scanner.nextInt();

        session.beginTransaction();
        Book book = session.get(Book.class, bk);
        if (book == null) {
            System.out.println("Book with ISBN " + bk + " doesn't exist.");
            return;
        }
        Client client = session.get(Client.class, client_id);
        if (client == null) {
            System.out.println("Client with ID " + client_id + " doesn't exist.");
            return;
        }
        try {
            List<Rent> rents = session.createQuery("from rent where client_id = :client_id ").setParameter("client_id", client_id).list();
            if (rents.isEmpty()) {
                throw new Exception("You have no books to return ");
            } else {
                for (Rent rent : rents) {
                    if (rent.getBook().getB_id() == bk) {
                        rent.setBook(book);
                        rent.setClient(client);
                        rent.setReturned(true);
                        book.setQtyInLibrary(book.qtyInLibrary + qty);
                        book.setAvailable(true);
                        session.update(rent);
                        session.merge(book);
                        session.flush();
                        session.getTransaction().commit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("The book you rented is returned! Thank you!");
        System.out.println("Enter 0 to exit or any other key to continue search:");
        int option = scanner.nextInt();
        if (option == 0) {
            System.exit(0);
        } else {
            Menu.clientMenu();
        }
    }

    public static void searchBooksByIsbn() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter ISBN of the book to search:");
            int b_id = scanner.nextInt();

            List<Book> books = session.createQuery("from book where b_id = :b_id", Book.class)
                    .setParameter("b_id", b_id)
                    .getResultList();

            if (books.isEmpty()) {
                System.out.println("No books found for the search term: " + b_id);
            } else {
                for (Book book : books) {
                    System.out.println("ISBN: " + book.getB_id() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor());
                }
            }
            System.out.println("Enter 0 to exit or any other key to continue search:");
            int choice = scanner.nextInt();
            if (choice == 0) {
                System.exit(0);
            } else {
                Menu.clientMenu();
            }
        }
    }

    public static void searchBooksByTitle() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter title of the book to search:");
            String book_title = scanner.nextLine();

            List<Book> books = session.createQuery("from book where book_title = :book_title", Book.class)
                    .setParameter("book_title", book_title)
                    .getResultList();

            if (books.isEmpty()) {
                System.out.println("No books found for the search term: " + book_title);
            } else {
                for (Book book : books) {
                    System.out.println("ISBN: " + book.getB_id() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor());
                }
            }
            System.out.println("Enter 0 to exit or any other key to continue search:");
            int choice = scanner.nextInt();
            if (choice == 0) {
                System.exit(0);
            } else {
                Menu.clientMenu();
            }
        }
    }

    public static void issueBookByIsbn() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your id:");
        int id = scanner.nextInt();

        System.out.println("Enter a book isbn to check if it's available:");
        int bk = scanner.nextInt();

        System.out.println("How many books you would like to rent?");
        int qty = scanner.nextInt();

        session.beginTransaction();
        Transaction trans = session.getTransaction();
        Book book = session.get(Book.class, bk);
        Client client = session.get(Client.class, id);

        if (book.isBookAvailable(qty)) {
            try {
                Rent rent = new Rent();
                rent.setBook(book);
                rent.setIssueDate(Timestamp.valueOf(Rent.issueDate()));
                rent.setDueDate(Timestamp.valueOf(Rent.dueDate()));
                rent.setClient(client);
                rent.setReturned(false);
                session.merge(client);
                session.merge(book);
                session.merge(rent);
                session.flush();
                trans.commit();
                DateReminder.sendReminder("Due date reminder message!", client.getEmail());

            } catch (Exception e) {
                System.out.println("Something went wrong. Try again or go back to the main menu");
                System.out.println("Press 1 to start again or 0 to go back to the main menu");
                int option = scanner.nextInt();

                switch (option) {
                    case 0:
                        Menu.mainMenu();
                        break;
                    case 1:
                        Book.issueBookByIsbn();
                        break;
                }
            }
        } else {
            System.out.println("This book is not available at the moment. Come back later.");
            System.out.println("You can press 1 to rent another book or 0 to go back to the main menu");
            int option = scanner.nextInt();

            switch (option) {
                case 0:
                    Menu.mainMenu();
                    break;
                case 1:
                    Book.issueBookByIsbn();
                    break;
            }
        }
        System.out.println("The book you rented is: " + book);
        Menu.clientMenu();
    }
}






