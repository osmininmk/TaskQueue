package main;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import services.CleanupJob;
import services.Executor;
import services.Generator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class, should be lunched
 * Initialized hibernate configuration from xml config,
 * read property, schedule generator{@link services.Generator},
 * executor {@link services.Executor}, clenup job {@link services.CleanupJob}
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
    private static final SessionFactory ourSessionFactory;
    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    /**
     * Obtain session from sessionFactory, thread safe
     * @return  hibernate session
     * @exception  HibernateException
     */
    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    /*
     * Main method
     * Read properties
     * Schedule works
     */
    public static void main(final String[] args) throws Exception {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(Main.class.getClassLoader().getResource("config.properties").getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Generator().start();
	}
}
