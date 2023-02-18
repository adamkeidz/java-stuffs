package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;

    private final ArrayList<com.muc.ServerWorker> workerList = new ArrayList<>();

    public Server(int serverPort){
        this.serverPort = serverPort;
    }

    public List<com.muc.ServerWorker> getWorkerList(){
        return workerList;
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("Accepting client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connection accepted from " + clientSocket);
                com.muc.ServerWorker worker = new com.muc.ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removeWorker(com.muc.ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
