package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private final com.muc.ChatClient client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    public LoginWindow(){
        super("Login");

        this.client = new com.muc.ChatClient("localhost",8818);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.add(loginField);
        p.add(passwordField);
        p.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        getContentPane().add(p, BorderLayout.CENTER);
        p.setSize(150,150);
        pack();

        setVisible(true);
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if(client.login(login,password)){
                //pop online userlist window
                com.muc.UserListPane userListPane = new com.muc.UserListPane(client);
                JFrame frame = new JFrame("Online User");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400,600);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);

                setVisible(false);
            } else{
                //get error
                JOptionPane.showMessageDialog(this,"Wrong login/password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginWindow logWin = new LoginWindow();
        logWin.setVisible(true);
    }
}
