package services;

import main.Main;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.TimerTask;
 /*
  * Class for clean database from obsolete ignored tasks
  */
public class CleanupJob implements Runnable{
	private static Logger log = Logger.getLogger(CleanupJob.class);

    /*
     * Class for clean database from obsolete ignored tasks
     * Mark all precessing task as obsolete(-1),
     * All task that currently processing in executor will be
     * removed form queue (by executors) during some time
     * all obsolete task that still exists, mark as new
     */
    @Override
    public void run() {
		log.info("Cleanup obsolete items");
        Session session = Main.getSession();
		session.beginTransaction();
        session.createSQLQuery("update Queue set processId = -1 where processId <> 0").executeUpdate();
		session.getTransaction().commit();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		session.beginTransaction();
        session.createSQLQuery("update Queue set processId = 0 where processId = -1").executeUpdate();
		session.getTransaction().commit();
        session.close();
		log.info("Cleanup finished");
    }
}
