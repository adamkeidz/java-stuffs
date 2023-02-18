package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.split;

public class ServerWorker extends Thread {

    private final com.muc.Server server;
    int logon =0;
    private final Socket clientSocket;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    public ServerWorker(com.muc.Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try {
            handleClientSocket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while( (line = reader.readLine()) != null) {
            String[] tokens = split(line);
            if (tokens != null && tokens.length > 0){
                String cmd = tokens[0];
                if ("logoff".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    logon =0;
                    handleLogoff();
                    System.out.println(login + " logged out");
                    break;
                } else if("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                } else if("msg".equalsIgnoreCase(cmd)){
                    String[] tokensMsg = split(line,null,3);
                    handleMessage(tokensMsg);
                } else if("join".equalsIgnoreCase(cmd)){
                    handleJoin(tokens);
                }else if("leave".equalsIgnoreCase(cmd)){
                    handleLeave(tokens);
                }else{
                    if(logon == 1) {
                        String msg = login + " typed: " + cmd + "\n";
                        outputStream.write(msg.getBytes());
                    } else if(logon == 0){
                        String msg = "unknown " + cmd + "\n";
                        outputStream.write(msg.getBytes());
                    }
                }
            }
        }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {
        String topic = tokens[1];
        if(tokens.length > 1){
            topicSet.remove(topic);
        }
        System.out.println(login + " left " + topic);
    }

    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        String topic = tokens[1];
        if(tokens.length > 1){
            topicSet.add(topic);
        }
        System.out.println(login + " joined " + topic);
    }

    // "msg" "login" text
    // "msg" "#topic" text
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic;
        if (sendTo.charAt(0) == '#') isTopic = true;
        else isTopic = false;

        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList) {
            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "msg " + sendTo + ": " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }else{
                if(sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        //send others of current user's status
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList){
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length ==3){
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("guest") && password.equals("guest")) || (login.equals("adam") && password.equals("adam"))){
                String msg = "you're online\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println(login + " logged in");
                logon =1;


                List<ServerWorker> workerList = server.getWorkerList();
                //send current user all other logins
                for(ServerWorker worker : workerList){
                    if(worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                //send others of current user's status
                String onlineMsg = "online " + login + "\n";
                for(ServerWorker worker : workerList){
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            } else{
                String msg = "error lah\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}
