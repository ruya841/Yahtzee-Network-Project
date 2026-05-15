/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee_networkproject;
 import java.net.*;// for Socket and ServerSocket

/**
 *
 * @author USER
 */
public class GameServer {
    
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server started...");
        while(true){
            //wait for the players to connect+ getInetAddress() returns the Ip 
        Socket player1 = server.accept();
        System.out.println("Player 1 connected"+player1.getInetAddress());
        Socket player2 = server.accept();
        System.out.println("Player 2 connected"+player2.getInetAddress());
        
        new Thread(new GameSession(player1,player2)).start();// بيشغل سيشن جديد
            System.out.println("new session started!");
        }
        }
    }
