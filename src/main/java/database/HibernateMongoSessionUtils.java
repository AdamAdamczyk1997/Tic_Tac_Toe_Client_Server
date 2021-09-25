package database;

import org.hibernate.SessionFactory;
import org.hibernate.ogm.cfg.OgmConfiguration;


public class HibernateMongoSessionUtils {
    private HibernateMongoSessionUtils() {
    }

    public static SessionFactory getInstance() {
        OgmConfiguration configuration = new OgmConfiguration();
        configuration.configure();
        return configuration.buildSessionFactory();
    }
}
