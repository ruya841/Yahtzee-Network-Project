/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee_networkproject;
import java.net.*;
import java.io.*;
/**
 *
 * @author USER
 */
//وسيط بين السيرفر و اللعبه
public class NetworkManager {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    
    public void connect(String serverIP, int port) throws Exception {
        socket = new Socket(serverIP, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void sendMessage(String msg) {
        out.println(msg);
    }
    
    public String receiveMessage() throws Exception {
        return in.readLine();
    }
}
