/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee_networkproject;

import java.io.BufferedReader;
import java.io.*; //for PrintWriter and BufferedReader
import java.net.Socket;

/**
 *
 * @author USER
 */
public class GameSession implements Runnable{
    Socket player1;
    Socket player2;
    // حفظ أسماء اللاعبين
     String player1Name = "";
     String player2Name = "";
     int player1Total = 0;
     int player2Total = 0;
    
    public GameSession(Socket p1, Socket p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    @Override
    public void run() {
        try{
        // PrintWriter the mouth of the server and BufferedReader the ear 
        //tills the players their names 
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        
        // ③ استقبل أسماء اللاعبين الأول
        // player1 بيبعت اسمه أول ما يتصل
        String name1 = in1.readLine(); 
        player1Name = name1.substring(5); 
        String name2 = in2.readLine(); 
        player2Name = name2.substring(5); 
            System.out.println("Session: "+player1Name+" vs "+player2Name);
        out1.println("PLAYER:1");
        out2.println("PLAYER:2");
        // ابعت اسم الخصم لكل لاعب
        out1.println("NAME:" + player2Name); // player1 يعرف اسم player2
        out2.println("NAME:" + player1Name); // player2 يعرف اسم player1
        
        out1.println("TURN:YOUR_TURN");
        out2.println("TURN:WAIT");
        System.out.println("Game started");
        
        // ⑤ الـ Game Loop - السيرفر بيسمع ويوصل الرسايل
        // currentIn  = مين دوره يتكلم دلوقتي
        // currentOut = مين المفروض يسمع دلوقتي
        // otherOut   = اللاعب التاني
        BufferedReader currentIn = in1;// listen to current player(1)
        PrintWriter currentOut = out1;// replay to current player (1)
        PrintWriter otherOut = out2;// the other player
        int currentPlayer = 1;

        while (true) {
            //readLine listen to current player
            String message = currentIn.readLine();
            
            // in case player disconnected
            if (message == null) {
                System.out.println("Player " + currentPlayer + " disconnected!");
                otherOut.println("OPPONENT_DISCONNECTED");
                break;
            }

           System.out.println("[" + player1Name + " vs " + player2Name + "] Player " + currentPlayer + ": " + message);
            // if the player pressed on roll button
            if (message.startsWith("ROLL:")) {
                // ex: "ROLL:3,5,1,4,2"
                // send the result to the player 2
                otherOut.println(message);
                // تأكيد على الاستلام
                currentOut.println("ROLL_OK");
                
            } else if (message.startsWith("HOLD:")) {
                otherOut.println(message);

            } else if (message.startsWith("SCORE:")) {
                otherOut.println(message);

                // change the currentplayer after playing
                if (currentPlayer == 1) {
                    currentIn = in2;//listen to player2
                    currentOut = out2;//replay to player 2
                    otherOut = out1;
                    currentPlayer = 2;
                } else {
                    currentIn = in1;
                    currentOut = out1;
                    otherOut = out2;
                    currentPlayer = 1;
                }
                //inform the player's turn
                currentOut.println("TURN:YOUR_TURN");
                otherOut.println("TURN:WAIT");

            } else if (message.startsWith("NAME:")) {
                // مثال: "NAME:Sara"
                // وصّل اسم اللاعب للاعب التاني عشان يعرضه
                otherOut.println(message);
                
            } else if (message.startsWith("TOTAL:")) {
                int total = Integer.parseInt(message.substring(6));

                if (currentPlayer == 1) {
                    player1Total = total;
                    
                    //نبدل علشان نسمع من اللاعب 3
                    currentIn = in2;
                    currentOut = out2;
                    otherOut = out1;
                    currentPlayer = 2;
                    out2.println("TURN:YOUR_TURN");
                    out1.println("TURN:WAIT");
                } else {
                    player2Total = total;
                }

                // لما الاتنين بعتوا التوتال
                if (player1Total > 0 && player2Total > 0) {
                    String winner;
                    if (player1Total > player2Total) {
                        winner = player1Name + " wins!";
                    } else if (player2Total > player1Total) {
                        winner = player2Name + " wins!";
                    } else {
                        winner = "It's a tie!";
                    }
                    // ابعت النتيجة للاعبين
                    out1.println("RESULT:" + winner + "|" + player1Name + ":" + player1Total + " " + player2Name + ":" + player2Total);
                    out2.println("RESULT:" + winner + "|" + player1Name + ":" + player1Total + " " +player2Name + ":" + player2Total);
                }
                } else if (message.equals("GAME_OVER")) { 
                otherOut.println("OPPONENT_DISCONNECTED");
                System.out.println("Game finished!");
                break;
            }
            }
        // close all the conections 
            in1.close();
            in2.close();
            out1.close();
            out2.close();
            player1.close();
            player2.close();
            
        }catch(Exception e){
            System.out.println("Session error: " + e.getMessage());
        }
    }
    
}
