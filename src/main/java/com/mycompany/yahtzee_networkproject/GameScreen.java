/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.yahtzee_networkproject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class GameScreen extends javax.swing.JFrame {
    GameLogic game = new GameLogic();
    NetworkManager network;
    String CurrentPlayerName;               // اسمك
    String opponentName = "";    // اسم الخصم
    boolean myTurn = false;      // دورك ولا لأ
    //if i create it in constructor roll_btn can not reach 
    JButton[]buttons;
     int selectedRow = -1;
     
    //hold selected buttons 
    public void selectedButtons() {
        for (int i = 0; i < buttons.length; i++) {
            // game.dice.held[i] gave error because (الاي بتتغير كل شويه وهو عايز حاجه ثابته فا خزنتها)
            int index = i;
            //once button clicked:
            buttons[i].addActionListener(e -> {
                boolean hold =  game.dice.held[index];
                if(hold==true){
                   hold=false;
                   buttons[index].setBorderPainted(false);
                }else if(hold==false){
                    hold=true;
                        // حطي فريم ملون
                    buttons[index].setBorderPainted(true); // لما يتحدد
                    buttons[index].setBorder(BorderFactory.createLineBorder(new Color(0xF4F1BB), 3));
                }
                game.dice.held[index]=hold; 
            });
        }
    ////------------------GUI-------------------
//         ImageIcon icon = new ImageIcon(getClass().getResource("/images/wood.png"));
//
//        Image imgee = icon.getImage().getScaledInstance(
//                jLabel2.getWidth(),
//                jLabel2.getHeight(),
//                Image.SCALE_SMOOTH
//        );
//
//        jLabel2.setIcon(new ImageIcon(imgee));
//        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBorderPainted(false);
            buttons[i].setContentAreaFilled(false); // ← المهم
            buttons[i].setFocusPainted(false);
            buttons[i].setOpaque(false); // ← ضيفي ده كمان
        }
        for (int i = 0; i < buttons.length; i++) {
            Image img = new ImageIcon(
                    getClass().getResource("/images/" + (i + 1) + ".png")
            ).getImage().getScaledInstance(
                    70, // نفس عرض الزرار
                    70, // نفس طول الزرار
                    Image.SCALE_SMOOTH
            );
            buttons[i].setIcon(new ImageIcon(img));
            buttons[i].setText("");
        }
        // no one can edit the table
        score_table.setDefaultEditor(Object.class, null);
        //SUM,BONUS AND TOTAL DIFFRENT COLORS
        score_table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (row == 6 || row == 7 || row == 15) {
                    setBackground(new Color(0xF4F1BB));
                    setForeground(new Color(0x3658C9));
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                } else {
                    int scoreIndex;
                    if (row < 6) {
                        scoreIndex = row;
                    } else {
                        scoreIndex = row - 2;
                    }
                    if (game.used_score[scoreIndex] && column == 1) {
                        setForeground(Color.RED);
                        
                    } else if (isSelected) {
                        setBackground(table.getSelectionBackground());
                        setForeground(table.getSelectionForeground());
                    } else {
                        setBackground(new Color(224, 235, 232));
                        setForeground(new Color(54, 88, 201));
                    }
                }
                return this;
            }
        });
}
    /**
     * Creates new form GameScreen
     */
    //constractor takes the network ip and the name of the player
    public GameScreen(NetworkManager network, String CurrentPlayerName) {
        initComponents();
        
         ImageIcon icon = new ImageIcon(getClass().getResource("/images/gamescreen.png"));

        Image img = icon.getImage().getScaledInstance(
                jLabel2.getWidth(),
                jLabel2.getHeight(),
                Image.SCALE_SMOOTH
        );

        jLabel2.setIcon(new ImageIcon(img));
        //when the player press X it will tell the otherplayer
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                network.sendMessage("GAME_OVER");
                dispose();
            }
        });
        
        this.network = network;
        this.CurrentPlayerName = CurrentPlayerName;

        buttons = new JButton[]{btn_dice1, btn_dice2, btn_dice3, btn_dice4, btn_dice5};
        selectedButtons();
        //يقفل الزر حد ما السيرفر يقرر مين يلعب والا الاثنين يقدرو يلعبو 
        btn_roll.setEnabled(false);
        startListening();// ابدأ تسمعي من السيرفر

        score_table.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = score_table.getSelectedRow();
        });
    }
   
    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = network.receiveMessage();
                    if (message == null) {//disconnected
                        break;
                    }
                    //takke the message and handle it 
                    handleMessage(message);
                }
            } catch (Exception e) {
                System.out.println("Connection lost!");
            }
        }).start();
    }

    private void handleMessage(String message) {
        if (message.startsWith("PLAYER:")) {
            // if PLAYER:1number will be 1 .. substring to delete PLAYER: word
            String number = message.substring(7);
            System.out.println("I am player " + number);

        } else if (message.equals("TURN:YOUR_TURN")) {
            myTurn = true;
            
            // SwingUtilities -> to update UI
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (game.GameOver()) {
                    return;
                }
                btn_roll.setEnabled(true);
                game.rollcounter = 0;
                label_Rollcounter.setText("Rolls Left: 5");
                label_status.setText("Your Turn - " + CurrentPlayerName);
                 for (int i = 0; i < buttons.length; i++) {
                    game.dice.held[i] = false;
                    buttons[i].setBorderPainted(false);
                }
            });

        } else if (message.equals("TURN:WAIT")) {
            myTurn = false;
            //SwingUtilities -> to update UI
            javax.swing.SwingUtilities.invokeLater(() -> {
                btn_roll.setEnabled(false);
                label_status.setText("Waiting for " + opponentName + "...");
            });

        } else if (message.startsWith("ROLL:")) {
            // النرد بتاع الخصم عرضيه في عنوان الشاشة مثلاً
            String[] dices = message.substring(5).split(",");
            javax.swing.SwingUtilities.invokeLater(() -> {
                label_status.setText("Opponent rolled: "
                        + dices[0] + "," + dices[1] + "," + dices[2] + "," + dices[3] + "," + dices[4]);
            });
        }else if (message.startsWith("NAME:")) {
            opponentName = message.substring(5);
            // حطي اسم الخصم في الجدول
            javax.swing.SwingUtilities.invokeLater(() -> {
                DefaultTableModel model = (DefaultTableModel) score_table.getModel();
                model.setColumnIdentifiers(
                        new String[]{"Rolls", CurrentPlayerName, opponentName});
            });

        } else if (message.startsWith("SCORE:")) {
            // سجلي نتيجة الخصم في الجدول
            //ex: SCORE:"8,25"-> 8 means full house for ex and 25 the score 
            // 1.delete score: then take 8 as row and 25 S VALUE 
            String[] parts = message.substring(6).split(",");
            int row = Integer.parseInt(parts[0]);
            int value = Integer.parseInt(parts[1]);
            javax.swing.SwingUtilities.invokeLater(() -> {
                DefaultTableModel model = (DefaultTableModel) score_table.getModel();
                model.setValueAt(value, row, 2); //  put the score it the opponent player(column 2)
            });

        } else if (message.startsWith("RESULT:")) {
            String result = message.substring(7);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    this.toBack();
                    new End_page(result).setVisible(true);
                });
            } else if (message.equals("OPPONENT_DISCONNECTED")) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Opponent disconnected!", "Game Over", JOptionPane.WARNING_MESSAGE);
                this.dispose();
            new LogIn().setVisible(true);
                    });
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog2 = new javax.swing.JDialog();
        jDialog3 = new javax.swing.JDialog();
        label_photo = new java.awt.Label();
        label_winner = new java.awt.Label();
        label_score = new java.awt.Label();
        jPanel2 = new javax.swing.JPanel();
        btn_confirm = new javax.swing.JButton();
        btn_roll = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        score_table = new javax.swing.JTable();
        label_Rollcounter = new javax.swing.JLabel();
        label_status = new javax.swing.JLabel();
        btn_dice4 = new javax.swing.JButton();
        btn_dice1 = new javax.swing.JButton();
        btn_dice5 = new javax.swing.JButton();
        btn_dice3 = new javax.swing.JButton();
        btn_dice2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jDialog3.setIconImage(null);

        label_photo.setText("label1");

        label_winner.setText("label1");

        label_score.setText("label1");

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog3Layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_winner, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                            .addComponent(label_score, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jDialog3Layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(label_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(label_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(label_winner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(label_score, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 204));

        jPanel2.setBackground(new java.awt.Color(0, 0, 51));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_confirm.setBackground(new java.awt.Color(246, 185, 202));
        btn_confirm.setFont(new java.awt.Font("Snap ITC", 1, 18)); // NOI18N
        btn_confirm.setForeground(new java.awt.Color(54, 88, 201));
        btn_confirm.setText("Confirm");
        btn_confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_confirmActionPerformed(evt);
            }
        });
        jPanel2.add(btn_confirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 470, 160, 50));

        btn_roll.setBackground(new java.awt.Color(244, 241, 187));
        btn_roll.setFont(new java.awt.Font("Snap ITC", 1, 18)); // NOI18N
        btn_roll.setForeground(new java.awt.Color(227, 138, 100));
        btn_roll.setText("Roll Dice");
        btn_roll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_rollActionPerformed(evt);
            }
        });
        jPanel2.add(btn_roll, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 470, 390, 50));

        score_table.setBackground(new java.awt.Color(224, 235, 232));
        score_table.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, java.awt.Color.white, java.awt.Color.black, java.awt.Color.white));
        score_table.setFont(new java.awt.Font("Snap ITC", 0, 12)); // NOI18N
        score_table.setForeground(new java.awt.Color(54, 88, 201));
        score_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Ones", null, null},
                {"Twos", null, null},
                {"Threes", null, null},
                {"Fours", null, null},
                {"Fives", null, null},
                {"Sixes", null, null},
                {"Sum", null, null},
                {"Bonus", null, null},
                {"Three of a kind", null, null},
                {"Four of a kind", null, null},
                {"Full house", null, null},
                {"Small straight", null, null},
                {"Large straight", null, null},
                {"Chance", null, null},
                {"Yahtzee", null, null},
                {"Total", null, null}
            },
            new String [] {
                "Rolls", "Player1", "Player2"
            }
        ));
        score_table.setToolTipText("");
        score_table.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        score_table.setFocusable(false);
        score_table.setGridColor(new java.awt.Color(246, 185, 202));
        score_table.setName(""); // NOI18N
        score_table.setRowHeight(22);
        score_table.setSelectionBackground(new java.awt.Color(54, 88, 201));
        score_table.setSelectionForeground(new java.awt.Color(244, 235, 232));
        jScrollPane1.setViewportView(score_table);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 360, 380));

        label_Rollcounter.setBackground(new java.awt.Color(224, 235, 232));
        label_Rollcounter.setFont(new java.awt.Font("Snap ITC", 1, 14)); // NOI18N
        label_Rollcounter.setForeground(new java.awt.Color(0, 0, 102));
        label_Rollcounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_Rollcounter.setText("Rolls Left: 5");
        label_Rollcounter.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, java.awt.Color.white, java.awt.Color.black, java.awt.Color.white));
        label_Rollcounter.setOpaque(true);
        jPanel2.add(label_Rollcounter, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 132, 32));

        label_status.setBackground(new java.awt.Color(244, 241, 187));
        label_status.setFont(new java.awt.Font("Snap ITC", 1, 14)); // NOI18N
        label_status.setForeground(new java.awt.Color(227, 138, 100));
        label_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_status.setOpaque(true);
        jPanel2.add(label_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 359, 32));

        btn_dice4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dice4ActionPerformed(evt);
            }
        });
        jPanel2.add(btn_dice4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 270, 100, 90));

        btn_dice1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dice1ActionPerformed(evt);
            }
        });
        jPanel2.add(btn_dice1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 360, 100, 90));

        btn_dice5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dice5ActionPerformed(evt);
            }
        });
        jPanel2.add(btn_dice5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 360, 100, 90));

        btn_dice3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dice3ActionPerformed(evt);
            }
        });
        jPanel2.add(btn_dice3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 270, 100, 90));

        btn_dice2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dice2ActionPerformed(evt);
            }
        });
        jPanel2.add(btn_dice2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 360, 100, 90));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gamescreen.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1020, 600));

        jMenu1.setText("More");

        jMenuItem1.setText("Back");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1032, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_rollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_rollActionPerformed
        if(!myTurn){
            return;
        }
        game.rollCount();
        for (int i = 0; i <buttons.length ; i++) {
            //game.dice=  return object but we want arraylist then .dices
            //choose frrom 1-6 store in DiceNum
            int DiceNum=game.dice.dices[i];
            //bring the photo based on DiceNum
            // زبطي حجم الصورة عشان تتناسب مع الزرار
            Image img = new ImageIcon(
                    getClass().getResource("/images/" + DiceNum + ".png")
            ).getImage().getScaledInstance(
                    70, // نفس عرض الزرار
                    70, // نفس طول الزرار
                    Image.SCALE_SMOOTH
            );
            buttons[i].setIcon(new ImageIcon(img));
            buttons[i].setText("");
            
             //close the button after 5th roll
        if(game.rollcounter>=5){
            btn_roll.setEnabled(false);
            }
        }
       
        // ابعتي النرد للسيرفر عشان الخصم يشوفه
    String diceValues = game.dice.dices[0] + "," + game.dice.dices[1] + ","
                      + game.dice.dices[2] + "," + game.dice.dices[3] + ","
                      + game.dice.dices[4];
    network.sendMessage("ROLL:" + diceValues);     
        //present the score on the table(ignore sum,bonus, and total rows)
        int[] count_score=game.calculateAllScores();
        DefaultTableModel tableModel = (DefaultTableModel)score_table.getModel();//take a copy from my table
        //put the score on the table
        //for upper section
        for (int i = 0; i <6; i++) {
            if(!game.used_score[i]){
            tableModel.setValueAt(count_score[i],i,1);
            }
        }
        //for lower section
        for (int i = 6; i <13; i++) {
            if(!game.used_score[i]){
            tableModel.setValueAt(count_score[i],i+2,1);
            }
        }
//        tableModel.setValueAt(count_score[0],0,1);
//        tableModel.setValueAt(count_score[1],1,1);
//        tableModel.setValueAt(count_score[2],2,1);
//        tableModel.setValueAt(count_score[3],3,1);
//        tableModel.setValueAt(count_score[4],4,1);
//        tableModel.setValueAt(count_score[5],5,1);
//        tableModel.setValueAt(count_score[6],8,1);
//        tableModel.setValueAt(count_score[7],9,1);
//        tableModel.setValueAt(count_score[8],10,1);
//        tableModel.setValueAt(count_score[9],11,1);
//        tableModel.setValueAt(count_score[10],12,1);
//        tableModel.setValueAt(count_score[11],13,1);
//        tableModel.setValueAt(count_score[12],14,1);
        //when all dices seleced client can't press roll button again
        boolean allHeld=true;
        for (int i = 0; i < game.dice.held.length; i++) {
            if(game.dice.held[i]==false){
                allHeld=false;
            }
        }
        if(allHeld==true){
            btn_roll.setEnabled(false);
        }
        ///////
        label_Rollcounter.setText("Rolls Left: "+ + (5 - game.rollcounter));
    }//GEN-LAST:event_btn_rollActionPerformed
    //لتأكيد الاختيار يتحقق من كل الشروط قبل تسجيل الاختيار
    private void btn_confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_confirmActionPerformed
        if(selectedRow==-1){
           JOptionPane.showMessageDialog(this,"Please select a category first!", "warning",JOptionPane.WARNING_MESSAGE);
           return;
        }
           //if the user selected sum,bonus or total ignore don't store the score
       if(selectedRow==6 ||selectedRow==7||selectedRow==15 ){
           return;
       }
       //if the user selected upper section(ones,twos..) the score index same otherwise -2 because sum and bonus toke the plase
       //for example three a kind is 8 but -2 cause sum and bonus doesn't count
       int scoreIndex;
       if(selectedRow<6){
           scoreIndex=selectedRow;
       }else{
           scoreIndex=selectedRow-2;
       }
       
       if(game.used_score[scoreIndex]){
        JOptionPane.showMessageDialog(this,"This category is already used!","Warning",JOptionPane.WARNING_MESSAGE);
        return; 
       }
       // يبعت رساله لو اللاعب حاول يسجل نتايج بنفس النرد الي لعب بيها المره الي فاتت 
        if (game.rollcounter == 0) {
            JOptionPane.showMessageDialog(this,
                    "Roll the dice first!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
       //game.rollcounter > 0 بتمنعي اللاعب من تسجيل درجة قبل ما يلف النرد خالص
       //!game.used_score[scoreIndex] يتأكد الخانه فاضيه
         if(!game.used_score[scoreIndex]&&game.rollcounter > 0){
             //تسجيل النتيحه
        game.score[scoreIndex] = game.calculateAllScores()[scoreIndex];
        
        game.used_score[scoreIndex] = true;
        //check if upper section full call sum method
             if (game.isUpperSectionFull()) {
                 int sum = game.calculateSum();
                 int bonus=game.calculateBonus();
                 DefaultTableModel model = (DefaultTableModel) score_table.getModel();
                 model.setValueAt(sum, 6, 1); // row 6 = Sum
                 //for bonus
                model.setValueAt(bonus, 7, 1); // row 7 = Bonus
             }
             if (game.GameOver()) {
                int total = game.calculateTotal();
                DefaultTableModel model = (DefaultTableModel) score_table.getModel();
                model.setValueAt(total, 15, 1); // row 15 = Total
                // ابعتي للسيرفر إن اللعبة خلصت
                network.sendMessage("TOTAL:"+total);
                
        }

        // ثبتي الدرجة في الجدول
        DefaultTableModel model = (DefaultTableModel) score_table.getModel();
        model.setValueAt(game.score[scoreIndex], selectedRow, 1);
        score_table.repaint();
        //هبعت النتيجه للسيرفر
        network.sendMessage("SCORE:" + selectedRow + "," + game.score[scoreIndex]);
        
        //if the user selected a score صفري العداد و افتحي الزر يبدأ من الاول علشان الاعب الثاني
        //مؤقت المفروض يقفلها عندي وقفتحها عند اللاعب الي عليه الدور 
            btn_roll.setEnabled(false);
            game.rollcounter=0;
            
             for (int i = 0; i < buttons.length; i++) {
                 game.dice.held[i]=false;
                 buttons[i].setBackground(null);
                 
             }
            
         }
    }//GEN-LAST:event_btn_confirmActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        int result=JOptionPane.showConfirmDialog(rootPane, "Once you Back the game will Finish Confirm?");
        if(result==JOptionPane.YES_OPTION){
            network.sendMessage("GAME_OVER");
            this.dispose();
            LogIn log=new LogIn();
             log.setVisible(true);
             
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void btn_dice3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dice3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_dice3ActionPerformed

    private void btn_dice4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dice4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_dice4ActionPerformed

    private void btn_dice2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dice2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_dice2ActionPerformed

    private void btn_dice5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dice5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_dice5ActionPerformed

    private void btn_dice1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dice1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_dice1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_confirm;
    private javax.swing.JButton btn_dice1;
    private javax.swing.JButton btn_dice2;
    private javax.swing.JButton btn_dice3;
    private javax.swing.JButton btn_dice4;
    private javax.swing.JButton btn_dice5;
    private javax.swing.JButton btn_roll;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_Rollcounter;
    private java.awt.Label label_photo;
    private java.awt.Label label_score;
    private javax.swing.JLabel label_status;
    private java.awt.Label label_winner;
    private javax.swing.JTable score_table;
    // End of variables declaration//GEN-END:variables
}
