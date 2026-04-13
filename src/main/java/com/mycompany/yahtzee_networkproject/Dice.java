/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.yahtzee_networkproject;

import java.util.ArrayList;

/**
 *
 * @author USER
 */
public class Dice {
 int[] dices=new int[5];
 
 public void roll(){
     // *6 takes 6 number (0.0 - 5.999)/ +1 (1.0 - 6.999)/cast because random method return double
     for (int i = 0; i < 5; i++) {
         int rand =(int)(Math.random()*6+1);
         dices[i]=rand;
     }
 }
 } 