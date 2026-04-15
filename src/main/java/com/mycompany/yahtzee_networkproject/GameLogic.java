/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee_networkproject;

/**
 *
 * @author USER
 */
public class GameLogic {
    Dice dice = new Dice();
    int rollcounter = 0;
//all dices should roll only 5 times
    public void rollCount() {
        if (rollcounter == 0) {
            dice.Roll();
            rollcounter += 1;
        } else if (rollcounter < 5) {
            dice.ReRoll();
            rollcounter += 1;
        }
    }
    
}
