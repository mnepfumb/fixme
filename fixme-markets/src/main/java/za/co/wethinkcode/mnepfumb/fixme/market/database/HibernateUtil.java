package za.co.wethinkcode.mnepfumb.fixme.market.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final HibernateUtil ourInstance = new HibernateUtil();
   
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Session session = sessionFactory.openSession();

    public static HibernateUtil getInstance() {
        return ourInstance;
    }

    private void close() {
        sessionFactory.close();
    }
}