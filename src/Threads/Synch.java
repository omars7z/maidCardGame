//package Threads;
//
//public class Synch {
//
//    public final Object lock;
//
//    public Synch(Object lock) {
//        this.lock = lock;
//    }
//
//    public void playTurn() {
//        synchronized (lock) {
//            lock.notify(); // Notify the waiting thread
//        }
//    }
//
//    public void waitForTurn() throws InterruptedException {
//        synchronized (lock) {
//            lock.wait(); // Wait until notified
//        }
//    }
//}
