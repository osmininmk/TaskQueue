package services;

import domain.entities.ActiveTask;
import main.Main;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.Random;

/**
 * Create new ActiveTasks and insert it in Database
 * At each step create random number [1,10] of ActiveTasks
 * Delay between execution is random ([0,3] seconds)
 */
public class Generator extends Thread {
	private static Logger log = Logger.getLogger(Generator.class);
    private Random rn = new Random();

    /**
     * Create random number [1,10] of ActiveTasks and insert it in Database
     */
    private void generate() {
        Session session = Main.getSession();
        try {
            log.info("Generating tasks");
            int taskCount = rn.nextInt(10);
            session.beginTransaction();

            for (int i = 0; i <= taskCount; i++) {
                session.save(new ActiveTask());
            }
            session.getTransaction().commit();
            log.info("Generated tasks: " + taskCount);
        } finally {
            session.close();
        }
    }

    /*
     * With random delay ([0,3] seconds), execute task to generate new ActiveTasks
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(rn.nextInt(4) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generate();
        }
    }
}
