package services;

import domain.entities.ActiveTask;
import domain.entities.CompletedTask;
import main.Main;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

 /*
  * Class that get tasks from database,
  * calculate value, save result into database
  */
public class Executor implements Runnable {
	private static Logger log = Logger.getLogger(Executor.class);
    private static final int BATCH_SIZE_PROCESS_ITEMS = 1000;
    private int parallelsExecutors;
    private Random rn = new Random();
    private ExecutorService executorService;

     /*
      * Constructor for executor
      * @param parallelsExecutorsCount after data got from DB, this data calculated parallels with parallelsExecutorsCount count
      */
    public Executor(int parallelsExecutors) {
        this.parallelsExecutors = parallelsExecutors;
        executorService = Executors.newFixedThreadPool(parallelsExecutors);
    }


     /*
      * get tasks from database,
      * calculate value, save result into database
      */
    @Override
    public void run() {
        Session session = Main.getSession();
        boolean processSuccessfully = true;
        int processId = rn.nextInt(Integer.MAX_VALUE - 3) + 1;
		log.debug("start execution " + processId);
		try {
            while (true) {
				//mark some portion of tasks as in progress(by unique processId), to prevent processing other executors
                session.getTransaction().begin();
                if (session.createSQLQuery("update Queue set processId = ? where processId = 0 limit ?").
                        setString(0, String.valueOf(processId)).
                        setInteger(1, BATCH_SIZE_PROCESS_ITEMS).executeUpdate() == 0) {
					log.info("There is no activeTask, exit");

                    session.getTransaction().commit();
                    break;
                }
                session.getTransaction().commit();

                processSuccessfully = false;

                //find all tasks that marked by current processId, processed this tasks
                @SuppressWarnings("unchecked")
                List<ActiveTask> list = session.createCriteria(ActiveTask.class)
						.add(Restrictions.eq("processId", processId)).list();
				log.debug("Execution " + processId + " processing " + list.size() + " items");
                if (processList(list, session)) {
                    processSuccessfully = true;
                }
            }
        } finally {
            session.close();
            if (!processSuccessfully) {
				log.warn("Execution processing has error, revert changes");
                session.beginTransaction();
                session.createSQLQuery("update Queue set processId = 0 where processId = ?")
						.setString(0, String.valueOf(processId)).executeUpdate();
                session.getTransaction().commit();
                session.close();
            }
        }
		log.debug("Execution completed");
    }
    /*
     * Calculate concurrently ActiveTask, remove it, and save result
     * CountDownLatch use to synch when all tasks will be calculated
     *
     * @param activeTasks active task obtained current executor
     * @param session active session to save, remove operation
     *
     * @return true if processing finished successfully, false otherwise
     */
    private boolean processList(List<ActiveTask> activeTasks, Session session) {
        log.info("processing activeTasks, count:" + activeTasks.size());
        if (activeTasks.size() == 0) {
            log.info("There is no activeTask, exit");
            return true;
        }
        CountDownLatch latch = new CountDownLatch(activeTasks.size());
        ConcurrentListHandler concurrentListHandler = new ConcurrentListHandler(activeTasks.iterator(), session, latch);
        session.beginTransaction();

        for (int i = 0; i < parallelsExecutors; i++) {
            executorService.submit(concurrentListHandler);
        }

        try {
            if (!latch.await(100, TimeUnit.SECONDS)) {
                session.getTransaction().rollback();
                return false;
            } else {
                session.getTransaction().commit();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		log.info("Finished processing activeTasks, count:" + activeTasks.size());
        return true;
    }

    /*
     * Helper class for concurrent calculation ActiveTasks
     * Using iterator, session are synchronized
     */
    private class ConcurrentListHandler extends Thread {
        private Iterator<ActiveTask> iterator;
        private Session session;
        private CountDownLatch latch;

        /*
         * Using iterator, session are synchronized
         * @param it iterator from list active tasks that should be calculated
         * @param session session for save, delete operation
         * @param latch use CountDownLatch to figure out when all active tasks will be calculated
         */
        ConcurrentListHandler(Iterator<ActiveTask> it, Session session, CountDownLatch latch) {
            iterator = it;
            this.session = session;
            this.latch = latch;
        }

        /*
         * Get next element from list, calculated task,
         * delete active task, save result
         */
        public void run() {
            log.info("Task processing thread");
            ActiveTask activeTask;
            while (true) {
                activeTask = null;
                synchronized (iterator) {
                    if (iterator.hasNext()) {
                        activeTask = iterator.next();
                        iterator.remove();
                    }
                }

                if (activeTask == null) {
                    break;
                }
				if(log.isTraceEnabled()) {
					log.trace("Processing task " + activeTask);
				}
                CompletedTask h = new CompletedTask(activeTask);
                h.calculate();
                synchronized (session) {
                    session.save(h);
                    session.delete(activeTask);
                }
                latch.countDown();
            }
			log.info("Task processing thread finished");
        }
    }
}
