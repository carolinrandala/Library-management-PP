package database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Database {
    public static Session getHibSesh() {

        Session session = null;

        try {
            SessionFactory seshFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

            session = seshFactory.openSession();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return session;

    }
}
