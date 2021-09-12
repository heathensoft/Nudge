package nudge.io.net.udp.test.old;

public class ThreadTesting {


    public static void main(String[] args) {


        Object object = new Object();

        Runnable runnable1 = new MyRunnable(object);
        Runnable runnable2 = new MyRunnable(object);

        Thread thread1 = new Thread(runnable1,"Thread1");
        Thread thread2 = new Thread(runnable1,"Thread2");

        thread1.start();
        thread2.start();

        // StoppableRunnable
        /*
        StoppableRunnable stoppableRunnable = new StoppableRunnable();
        Thread thread = new Thread(stoppableRunnable);
        thread.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stoppableRunnable.requestStop();

         */
    }


    public static class MyRunnable implements Runnable {

        int count = 0;
        Object object;

        public MyRunnable() {}

        public MyRunnable(Object object) {
            this.object = object;
        }

        @Override
        public void run() {

            System.out.println(object);
            count();
            System.out.println(Thread.currentThread().getName() + " " + this.count);

        }

        private synchronized void count() {
            for (int i = 0; i < 1000; i++) {
                count++;
            }
        }
    }

    public static class StoppableRunnable implements Runnable {

        private boolean stopRequested;

        public synchronized void requestStop() {
            stopRequested = true;
        }

        public synchronized boolean isStopRequested() {
            return stopRequested;
        }

        @Override
        public void run() {

            while (!isStopRequested()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("...");
            }
            System.out.println("StoppableRunnable stopped");
        }
    }
}
