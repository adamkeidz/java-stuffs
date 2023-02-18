package com.muc;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8818;
        com.muc.Server server = new com.muc.Server(port);
        server.start();
    }

}
