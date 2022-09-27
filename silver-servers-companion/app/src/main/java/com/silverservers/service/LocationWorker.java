package com.silverservers.service;

public class LocationWorker extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("Service is running...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
