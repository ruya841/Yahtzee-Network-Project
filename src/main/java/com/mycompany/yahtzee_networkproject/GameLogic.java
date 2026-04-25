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
    
    String[] Categories={"Ones","Twos","Threes","Fours","Fives","Sixes",
            "Three of a kind","Four of a kind","Full house","Small straight","Large straight","Chance","Yahtzee"};
    int []score=new int[13];
    
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
    //calculate the sum of (Ones,Twos,Threes,Fours,Fives,Sixes)only
    public int calculateSum(){  
        int sum=0;
        for (int i = 0; i < 6; i++) {
            sum+=score[i];
        }
        return sum;
    }
    
    public int calculateTotal(){
        int total=0;
        for (int i = 0; i < score.length; i++) {
            total+=score[i];
        }
        return total;
    }
    //to calculate (Ones,Twos,Threes,Fours,Fives,Sixes)
    public int Calculate_Upper(int number) {//number is (Ones,Twos,Threes,Fours,Fives,Sixes)
        int calculateRepeat = 0;
        for (int i = 0; i < dice.dices.length; i++) {
            if (dice.dices[i] == number) {
                calculateRepeat += number;
            }
        }
        return calculateRepeat;
    }
    //compare all dices with first dice 
    public int Calculate_yahtzee(){
        boolean yahtzee=true;
        int score=0;
        int base=dice.dices[0];//first dice
        for(int i=0;i<dice.dices.length;i++){
            if(dice.dices[i]!=base){
                yahtzee=false;
                break;
            }
        }
        if(yahtzee){
            score=50;   
        }
        return score;
    }
    

    public int Calculate_fullhouse(){
        //هتحفظ لي عدد لتكرارات 
       int[] countFrequency=new int[6];//[1,2,3,4,5,6]هحسب كل رقم من دول اتكرر كم مره
       int score=0;
     
       boolean hasTwo=false;
       boolean hasThree=false;
        for (int i = 0; i < dice.dices.length; i++) {
            countFrequency[dice.dices[i]-1]+=1;
        }
        for (int i = 0; i <countFrequency.length ; i++) {
            if(countFrequency[i]==2){
                hasTwo=true;
            }if(countFrequency[i]==3){
                hasThree=true;
            }
        }
       if(hasTwo==true && hasThree==true){
           score=25;
       }
        return score;
    }
   //sum all the dices
    public int Calculate_chance(){
        int score=0;
        for (int i = 0; i < dice.dices.length; i++) {
            score+=dice.dices[i];
        }
        return score;
    }
    //small straight(4)==1,2,3,4 or 2,3,4,5 or 3,4,5,6.
    //large straight(5)==1,2,3,4,5 or 2,3,4,5,6
    public int Calculate_straight(int type){//small straight(4) or large straight(5)
     int score=0;
     int streak=0;
     int [] result=new int[6];//هتحفظ لي عدد لتكرارات 
        for (int i = 0; i < dice.dices.length; i++) {
            result[dice.dices[i]-1]+=1;
        }
        for (int i = 0; i < result.length; i++) {
            if(result[i]>0){// if =0 then doesn't appear
                streak+=1;
            }else{
                streak=0;//بدونها مش هتبقا متسلسله لازم اول ما يفك التسلسل ابدأ من جديد 
            }
        }
        if(streak >=4 && type==4){
            score=30;
        }
        if(streak >=5 && type==5){//ممكن يعمل سلسله اطول من  خمس ارقام ورا بعض 
            score=40;
        }
        return score;
    }
    
    public int calculate_3_4_ofKind(int type){//to calculate three of kind ,four of kind
        int score=0;
        int[] result=new int[6];//هتحفظ لي عدد لتكرارات 
        for (int i = 0; i < dice.dices.length; i++) {
            result[dice.dices[i]-1]+=1;
        }
        for (int i = 0; i < result.length; i++) {
            if(result[i]>=type){   
               score= Calculate_chance();//sum of all dices
               break;//لو في تكرارين  في مصفوفه الريسلت المفروض يحسب النتيجه مره واحده بس 
               //مثلا لوو لقااثنين ثلاثه لازم يحسب النتيجه مع اول ثلاثه تظهر ويوقف قبل ما يلاقي الثلاثه الثانيه
            }
        }
        return score;
    }
    
}
