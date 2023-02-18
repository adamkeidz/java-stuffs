package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.split;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<com.muc.UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<com.muc.MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost",8818);
        client.addUserStatusListener(new com.muc.UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener(new com.muc.MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + "--> " + msgBody);
            }
        });

        if (!client.connect()) {
            System.err.println("Connection failed.");
        } else {
            System.out.println("Connected!");
            if(client.login("guest","guest")){
                System.out.println("Login successful");

                client.msg("adam","Hello!");
            }else{
                System.err.println("Login failed");
            }

            //client.logoff();
        }
    }

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response: " + response);

        if("you're online".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        }else{
            return false;
        }
    }

    public void logoff() throws IOException{
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    private void startMessageReader() {
        Thread t = new Thread(){
            @Override
            public void run(){
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
            try {
                String line;
                while ((line = bufferedIn.readLine()) != null){
                    String[] tokens = split(line);
                    if(tokens != null && tokens.length >0){
                        String cmd = tokens[0];
                        if("online".equalsIgnoreCase(cmd)){
                            handleOnline(tokens);
                        }else if("offline".equalsIgnoreCase(cmd)){
                            handleOffline(tokens);
                        } else if("msg".equalsIgnoreCase(cmd)){
                            String[] tokensMsg = StringUtils.split(line,null,3);
                            handleMessage(tokensMsg);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(com.muc.MessageListener listener : messageListeners){
            listener.onMessage(login,msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(com.muc.UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(com.muc.UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }



    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(com.muc.UserStatusListener listener){
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(com.muc.UserStatusListener listener){
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(com.muc.MessageListener listener){
        messageListeners.add(listener);
    }

    public void removeMessageListener(com.muc.MessageListener listener){
        messageListeners.remove(listener);
    }
}
