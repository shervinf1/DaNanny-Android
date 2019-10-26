package com.example.dananny;

public class AccessService {

    private static Boolean access;
    private static Boolean documentLockdown;
    private static int counter;

    AccessService(){
        access = true;
        documentLockdown = false;
        counter = 0;
    }

    public synchronized void requestAccess(){
        System.out.println("Asking for Document Access");
        while (!access){
            try{
                System.out.println("No access");
                wait();
            }catch (Exception e){

            }
        }

        System.out.println("Access granted");
        access = false;
    }

    public synchronized void releaseAccess(){
        access = true;
        notify();
    }

    public void lockDocument(){
        documentLockdown = true;
    }

    public void increaseCounter(){
        System.out.println("A Thread has started");
        counter++;
        this.printCounter();
    }

    public void addToCounter(int writes){
        counter+= writes;
        this.printCounter();
    }

    public void decreaseCounter(){
        counter--;
        System.out.println("A Thread has finished");
        this.printCounter();
    }

    public void printCounter(){
        System.out.println("Active Threads: " + counter);
    }

    public synchronized void freeDocument(){
        this.printCounter();
        while(counter > 0){
            System.out.println("Thread are still running...");
            this.printCounter();
            try{
                wait();
                System.out.println("Trying to close now");
            }catch (Exception e){

            }
        }
        documentLockdown = false;
        System.out.println("Document is now closed");
    }

}
