package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements com.muc.MessageListener {

    private final com.muc.ChatClient client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();
    //public static javax.swing.JLabel img_server;


    public MessagePane(com.muc.ChatClient client, String login) throws ClassNotFoundException, IOException {
        this.client = client;
        this.login = login;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        //add(img_server, BorderLayout.NORTH);
        add(messageList, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);



        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msg(login, text);
                    listModel.addElement("You: " + text);
                    inputField.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        /*Socket server = new Socket();
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        ImageIcon ic;

        while(true){
            ic = (ImageIcon) in.readObject();
            img_server.setIcon(ic);
        }*/
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if (login.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + ": " + msgBody;
            listModel.addElement(line);
        }
    }


}
