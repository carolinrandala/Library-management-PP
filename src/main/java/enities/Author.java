package enities;

import database.Database;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.util.List;

@Entity(name = "author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int a_id;

    @Column(name = "author_name")
    private String authorName;


    public Author(String authorName) {
        this.authorName = authorName;
    }

    static Session session = Database.getHibSesh();


    public static void listAuthor() {

        try {
            session.beginTransaction();
            List<Author> authors = session.createQuery("from author").list();

            for (Author author : authors) {
                System.out.println(author);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAuthor(Author author) {
        session.beginTransaction();
        Transaction trans = session.getTransaction();
        try {
            session.persist(author);
            session.flush();
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            e.printStackTrace();
        }
    }
}