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
    
    String[] Categories={"Ones","Twos","Threes","Fours","Fives","Sixes",
            "Three of a kind","Four of a kind","Full house","Small straight","Large straight","Chance","Yahtzee"};
    int []score=new int[13];
    //in confirm button
    boolean[] used_score=new boolean[13];
    
//all dices should roll only 5 times
    int rollcounter = 0;
    public void rollCount() {
        if (rollcounter == 0) {
            dice.Roll();
            rollcounter += 1;
        } else if (rollcounter < 5) {
            dice.ReRoll();
            rollcounter += 1;
        } 
    }
    //must be in the server to calculate the score after every roll !!!
    public int[] calculateAllScores(){
        //allScore  مؤقته بتحسب بعد كل لفه نرد النتايج علشان تعرضها في الجدول وبتتصفر بعد كل دوره 
        // scoreدائمه هستخدمها علشان اعرض التوتال في الاخير 
        int [] allScore=new int[13];
        allScore[0]=Calculate_Upper(1);
        allScore[1]=Calculate_Upper(2);
        allScore[2]=Calculate_Upper(3);
        allScore[3]=Calculate_Upper(4);
        allScore[4]=Calculate_Upper(5);
        allScore[5]=Calculate_Upper(6);
        allScore[6]=calculate_3_4_ofKind(3);
        allScore[7]=calculate_3_4_ofKind(4);
        allScore[8]=Calculate_fullhouse();
        allScore[9]=Calculate_straights(4);
        allScore[10]=Calculate_straights(5);
        allScore[11]=Calculate_chance();
        allScore[12]=Calculate_yahtzee();
        return allScore;
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
    // لازم اتحقق مرتين برا و جوا اللوب قبل ما اصفر الستريك لان ممكن يكون 
    //[1,1,1,1,1,0]-> normally if we didn't check inside the loop he will find 0 but we reached streak 5
    public int Calculate_straights(int type){//small straight(4) or large straight(5)
     int score=0;
     int streak=0;
     int [] result=new int[6];//هتحفظ لي عدد لتكرارات 
        for (int i = 0; i < dice.dices.length; i++) {
            result[dice.dices[i]-1]+=1;
        }
        for (int i = 0; i < result.length; i++) {
            if(result[i]>0){// if =0 then doesn't appear
                streak+=1;
            }else{// قبل ما اصفر الستريك لازم اتأكد لو وصلت للتجميعه
                if (streak >= 5) {
                    score = 40;
                } else if (streak >= 4) {
                    score = 30;
                }
                streak=0;//بدونها مش هتبقا متسلسله لازم اول ما يفك التسلسل ابدأ من جديد 
            }
        }
        
        if( type==4){
            if(score>=30){
                return 30;
            }else{
                return 0;
            }
        }
        if( type==5){//ممكن يعمل سلسله اطول من  خمس ارقام ورا بعض 
            if(score==40){
               return 40; 
            }else{
                return 0;
            }
        }
        return 0;
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
    //check if upper section full call sum method 
    public boolean isUpperSectionFull() {
    for (int i = 0; i < 6; i++) {
        if (!used_score[i]) {
            return false;
        }
    }
    return true;
}
    //calculate bonus 
    public int calculateBonus(){
        if(isUpperSectionFull() &&calculateSum()>=63 ){
            return 35;
        }
        return 0;
    }
    //create gameover to calculate total in the cofirm button
    public boolean GameOver(){
        for (int i = 0; i < used_score.length; i++) {
            if(!used_score[i]){
                return false;
            }
        }
        return true;
    }
    
}
