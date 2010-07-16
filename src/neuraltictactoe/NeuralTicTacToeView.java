/*
 * NeuralTicTacToeView.java
 */

package neuraltictactoe;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import neuraltictactoe.trainingset.SaveTrainingSet;

/**
 * The application's main frame.
 */
public class NeuralTicTacToeView extends FrameView {

    public NeuralTicTacToeView(SingleFrameApplication app) {
        super(app);

        initComponents();

        //player values
        player1.setValue(0);
        player2.setValue(1);
        //neural network player
        nella.setValue(1);
        nella.loadNet();
        //set active
        active = player1;
        play_banner.setText("file-->new game");
        //nella's moves
        jDialog1.setTitle("Nella's moves");
        jDialog1.setVisible(true);
        jDialog1.setSize(320, 200);

        jDialog2.setTitle("Graphs");
        jDialog2.setVisible(true);
        jDialog2.setSize(700, 400);
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }


    @Action
    public void appQuit(){
        training.closeConnection();
        System.exit(0);
    }
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = NeuralTicTacToeApp.getApplication().getMainFrame();
            aboutBox = new NeuralTicTacToeAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);

        }
        NeuralTicTacToeApp.getApplication().show(aboutBox);
    }

    @Action
    public void newAction(){
            //set new board
            gameState = true;
            board = new Board();
            nella.trainNet();
           
            ++gamecount;
            try {
            training.initWrite();
        } catch (IOException ex) {
            Logger.getLogger(NeuralTicTacToeView.class.getName()).log(Level.SEVERE, null, ex);
        }
            //visuals are clean
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));
            square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));

            square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));
            square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));

            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));
            square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/blank.png"));

            reverseActive();
            if(active == nella){
            humanplay = false;
            }else {
            humanplay = true;
            }
            play_banner.setText(active.name+"'s turn");
            winner_.setText("game in progress");

    }

     public void boardToArray(){
        board_[0][0] = board.square_1.getValue();
        board_[0][1] = board.square_2.getValue();
        board_[0][2] = board.square_3.getValue();
        board_[1][0] = board.square_4.getValue();
        board_[1][1] = board.square_5.getValue();
        board_[1][2] = board.square_6.getValue();
        board_[2][0] = board.square_7.getValue();
        board_[2][1] = board.square_8.getValue();
        board_[2][2] = board.square_9.getValue();

     }



    public void reverseActive(){
        if(active == player1){
            active = nella;
        }else{
            active = player1;
        }
    }
    
    public int determineWin(){
        
        if(board.isFull()){
            if(board.isWinner(player1.getValue()) == 0 
                    && board.isWinner(player2.getValue()) == 0){
                return -2;
            } else if(board.isWinner(player1.getValue())!=0){
                return player1.getValue();
            }else if(board.isWinner(player2.getValue()) != 0){
                return player2.getValue();
            }else {
                return -2;
            }
        } else{
                if(board.isWinner(player1.getValue())!=0){
                return player1.getValue();
            }else if(board.isWinner(player2.getValue()) != 0){
                return player2.getValue();
            }else {
                return -1;
            }
        }
    }

    public void visualDisplay(int d, int player){
        if(player == 0){
            if(d == 1){
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            } else if(d == 2){
            square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            }else if(d == 3){
            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ohw.png"));
            }else if(d == 4){
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            }else if(d == 5){
            square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            }else if(d == 6){
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/ovw.png"));
            }else if(d == 7){
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/odwr.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/odwr.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/odwr.png"));
            }else if(d == 8){
            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/odwl.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/odwl.png"));
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/odwl.png"));
            }

        } else if(player == 1){
            if(d == 1){
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            } else if(d == 2){
            square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            }else if(d == 3){
            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xhw.png"));
            }else if(d == 4){
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            }else if(d == 5){
            square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            }else if(d == 6){
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xvw.png"));
            }else if(d == 7){
            square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xdwr.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xdwr.png"));
            square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xdwr.png"));
            }else if(d == 8){
            square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xdwl.png"));
            square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xdwl.png"));
            square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/xdwl.png"));
            }
        }
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        square_7 = new javax.swing.JLabel();
        square_8 = new javax.swing.JLabel();
        square_9 = new javax.swing.JLabel();
        square_4 = new javax.swing.JLabel();
        square_5 = new javax.swing.JLabel();
        square_6 = new javax.swing.JLabel();
        square_1 = new javax.swing.JLabel();
        square_2 = new javax.swing.JLabel();
        square_3 = new javax.swing.JLabel();
        winner_ = new javax.swing.JLabel();
        play_banner = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newActionMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jDialog1 = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        nella_graph = new javax.swing.JTextArea();
        jDialog2 = new javax.swing.JDialog();
        graphComponent1 = new neuraltictactoe.GraphComponent();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(neuraltictactoe.NeuralTicTacToeApp.class).getContext().getResourceMap(NeuralTicTacToeView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N

        square_7.setIcon(resourceMap.getIcon("square_7.icon")); // NOI18N
        square_7.setText(resourceMap.getString("square_7.text")); // NOI18N
        square_7.setName("square_7"); // NOI18N
        square_7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_7MouseClicked(evt);
            }
        });

        square_8.setIcon(resourceMap.getIcon("square_8.icon")); // NOI18N
        square_8.setName("square_8"); // NOI18N
        square_8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_8MouseClicked(evt);
            }
        });

        square_9.setIcon(resourceMap.getIcon("square_9.icon")); // NOI18N
        square_9.setName("square_9"); // NOI18N
        square_9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_9MouseClicked(evt);
            }
        });

        square_4.setIcon(resourceMap.getIcon("square_4.icon")); // NOI18N
        square_4.setName("square_4"); // NOI18N
        square_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_4MouseClicked(evt);
            }
        });

        square_5.setIcon(resourceMap.getIcon("square_5.icon")); // NOI18N
        square_5.setName("square_5"); // NOI18N
        square_5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_5MouseClicked(evt);
            }
        });

        square_6.setIcon(resourceMap.getIcon("square_6.icon")); // NOI18N
        square_6.setName("square_6"); // NOI18N
        square_6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_6MouseClicked(evt);
            }
        });

        square_1.setIcon(resourceMap.getIcon("square_1.icon")); // NOI18N
        square_1.setName("square_1"); // NOI18N
        square_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_1MouseClicked(evt);
            }
        });

        square_2.setIcon(resourceMap.getIcon("square_2.icon")); // NOI18N
        square_2.setName("square_2"); // NOI18N
        square_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_2MouseClicked(evt);
            }
        });

        square_3.setIcon(resourceMap.getIcon("square_3.icon")); // NOI18N
        square_3.setName("square_3"); // NOI18N
        square_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                square_3MouseClicked(evt);
            }
        });

        winner_.setForeground(resourceMap.getColor("winner_.foreground")); // NOI18N
        winner_.setText(resourceMap.getString("winner_.text")); // NOI18N
        winner_.setName("winner_"); // NOI18N

        play_banner.setFont(resourceMap.getFont("play_banner.font")); // NOI18N
        play_banner.setForeground(resourceMap.getColor("play_banner.foreground")); // NOI18N
        play_banner.setText(resourceMap.getString("play_banner.text")); // NOI18N
        play_banner.setName("play_banner"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(neuraltictactoe.NeuralTicTacToeApp.class).getContext().getActionMap(NeuralTicTacToeView.class, this);
        exitButton.setAction(actionMap.get("appQuit")); // NOI18N
        exitButton.setText(resourceMap.getString("exitButton.text")); // NOI18N
        exitButton.setToolTipText(resourceMap.getString("exitButton.toolTipText")); // NOI18N
        exitButton.setName("exitButton"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(square_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(square_8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(square_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(winner_, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(square_4)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGap(71, 71, 71)
                                    .addComponent(square_5))
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGap(142, 142, 142)
                                    .addComponent(square_6)))
                            .addGap(18, 18, 18)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(play_banner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(square_1)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGap(71, 71, 71)
                                    .addComponent(square_2))
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGap(142, 142, 142)
                                    .addComponent(square_3)))
                            .addGap(18, 18, 18)
                            .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(square_8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(square_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(square_2))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(square_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(square_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(square_1))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(square_9)
                            .addComponent(winner_, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(square_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(square_3))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(play_banner, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newActionMenuItem.setAction(actionMap.get("newAction")); // NOI18N
        newActionMenuItem.setText(resourceMap.getString("newActionMenuItem.text")); // NOI18N
        newActionMenuItem.setName("newActionMenuItem"); // NOI18N
        fileMenu.add(newActionMenuItem);

        exitMenuItem.setAction(actionMap.get("appQuit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setToolTipText(resourceMap.getString("exitMenuItem.toolTipText")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jDialog1.setTitle(resourceMap.getString("jDialog1.title")); // NOI18N
        jDialog1.setBackground(resourceMap.getColor("jDialog1.background")); // NOI18N
        jDialog1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDialog1.setName("jDialog1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        nella_graph.setColumns(20);
        nella_graph.setEditable(false);
        nella_graph.setFont(resourceMap.getFont("nella_graph.font")); // NOI18N
        nella_graph.setForeground(resourceMap.getColor("nella_graph.foreground")); // NOI18N
        nella_graph.setRows(5);
        nella_graph.setName("nella_graph"); // NOI18N
        jScrollPane1.setViewportView(nella_graph);

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jDialog2.setName("jDialog2"); // NOI18N

        graphComponent1.setName("graphComponent1"); // NOI18N

        javax.swing.GroupLayout graphComponent1Layout = new javax.swing.GroupLayout(graphComponent1);
        graphComponent1.setLayout(graphComponent1Layout);
        graphComponent1Layout.setHorizontalGroup(
            graphComponent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        graphComponent1Layout.setVerticalGroup(
            graphComponent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(graphComponent1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(graphComponent1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    public void updateGraph(){
        Point2D p = new Point2D.Double(gamecount,wincount);
        graphComponent1.addConcurrentPoint(p);
    }

    public void gameState(){
    if(determineWin() == -2){
        winner_.setText("Draw");
        nella.setPoint(1);
        play_banner.setText("Game OVER");
        gameState = false;
        updateGraph();
        training.closeConnection();
        }else if(determineWin() == player1.getValue()){
        winner_.setText("winner is "+player1.name);
        nella.setPoint(-1);
        play_banner.setText("Game OVER");
        gameState = false;
        updateGraph();
        training.closeConnection();
        visualDisplay(board.isWinner(player1.getValue()), player1.getValue());
        }else if(determineWin() == nella.getValue()){
        winner_.setText("winner is "+nella.name);
        nella.setPoint(3);
        ++wincount;
        updateGraph();
        play_banner.setText("Game OVER");
        gameState = false;
        training.closeConnection();
        visualDisplay(board.isWinner(nella.getValue()), nella.getValue());
        }else{
            if(active == nella){
                humanplay = false;
            }else{
            humanplay = true;
            }
        play_banner.setText(active.name+"'s turn");
        }
    }


    private void square_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_1MouseClicked
    double play = 0.1;
       // int play[] = {0,0,0,1};
        if(gameState){
            if(humanplay){
        if(!board.square_1.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
             //   if(active == player2)
              //  training.writeToFile(board_, play);
        board.square_1.changeValue(active.getValue());
        if(board.square_1.getValue() == 0){
        square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
        
       gameState();
        
    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
            }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
            }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }   // TODO add your handling code here:
    }//GEN-LAST:event_square_1MouseClicked

    private void square_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_2MouseClicked
        // TODO add your handling code here:
        double play = 0.2;
        //int play[] = {0,0,1,0};
        if(gameState){
            if(humanplay){
        if(!board.square_2.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
          //  if(active == player2)
              //  training.writeToFile(board_, play);
        board.square_2.changeValue(active.getValue());
        if(board.square_2.getValue() == 0){
        square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
        
       gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
        }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
            }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_2MouseClicked

    private void square_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_3MouseClicked
        double play = 0.3;
        //int play[] = {0,0,1,1};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_3.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
           // if(active == player2)
             //   training.writeToFile(board_, play);
        board.square_3.changeValue(active.getValue());
        if(board.square_3.getValue() == 0){
        square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
       reverseActive();
        
       gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
        }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
        }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_3MouseClicked

    private void square_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_4MouseClicked
        double play = 0.4;
        //int play[] = {0,1,0,0};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_4.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
          //  if(active == player2)
             //   training.writeToFile(board_, play);
        board.square_4.changeValue(active.getValue());
        if(board.square_4.getValue() == 0){
        square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
       reverseActive();
        
        gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
        }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
        }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_4MouseClicked

    private void square_5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_5MouseClicked
        double play = 0.5;
        //int play[] = {0,1,0,1};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_5.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
          //  if(active == player2)
              //  training.writeToFile(board_, play);
        board.square_5.changeValue(active.getValue());
        if(board.square_5.getValue() == 0){
        square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
        
        gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
        }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
        }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_5MouseClicked

    private void square_6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_6MouseClicked
       double play = 0.6;
       // int play[] = {0,1,1,0};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_6.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
           // if(active == player2)
                //training.writeToFile(board_, play);
        board.square_6.changeValue(active.getValue());
        if(board.square_6.getValue() == 0){
        square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
        
       gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
        }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
            }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_6MouseClicked

    private void square_7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_7MouseClicked
        double play = 0.7;
        //int play[] = {0,1,1,1};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_7.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
           // if(active == player2)
              //  training.writeToFile(board_, play);
        board.square_7.changeValue(active.getValue());
        if(board.square_7.getValue() == 0){
        square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
       
        gameState();


    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
        }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
            }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_7MouseClicked

    private void square_8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_8MouseClicked
        double play = 0.8;
        //int play[] = {1,0,0,0};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_8.getState()){
        //Player tmp = getActivePlayer();
             boardToArray();
            // if(active == player2)
              //  training.writeToFile(board_, play);
        board.square_8.changeValue(active.getValue());
        if(board.square_8.getValue() == 0){
        square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
       
        gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
            }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
            }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        }
    }//GEN-LAST:event_square_8MouseClicked

    private void square_9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_square_9MouseClicked
        double play = 0.9;
        //int play[] = {1, 0, 0, 1};
        if(gameState){
            if(humanplay){
        // TODO add your handling code here:
        if(!board.square_9.getState()){
        //Player tmp = getActivePlayer();
            boardToArray();
           // if(active == player2)
             //   training.writeToFile(board_, play);
        board.square_9.changeValue(active.getValue());
        if(board.square_9.getValue() == 0){
        square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/o.png"));
        }else{
        square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
        }
        reverseActive();
        
       gameState();

    }else{
    JOptionPane.showMessageDialog(null,"Invalid move, cell already played");
    }
            }else{
            JOptionPane.showMessageDialog(null,"Nella's turn");
            }
            }else{
        JOptionPane.showMessageDialog(null, "Game is not in play");
        } 
    }//GEN-LAST:event_square_9MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(!humanplay){
        boardToArray();
        double [] inputArray = new double [9];
        int index = 0;
        for(int x =0; x<board_.length; x++){
            for(int y = 0; y<board_.length;y++){
            inputArray [index] =  board_[x][y];
            index++;
            }

        }
        double try_ = Double.MIN_VALUE;
            //neural net
               try_ = nella.calcNet(inputArray);
               
        double movefinal_up = Math.abs(Math.ceil(try_*10));
        double movefinal_down = Math.abs(Math.floor(try_*10));

System.out.println(movefinal_up+" "+movefinal_down);
        if(moveNeural(movefinal_up)== 0){
            
            nella_graph.append("Wrong move at cell: "+movefinal_up+"\n");
            if(moveNeural(movefinal_down) == 0){
                
                nella_graph.append("Wrong move at cell:(d) "+movefinal_down+"\n");
        double square = -2;
        int count =0;
        int move_alt = 0;
            while(square != -1){
            square = inputArray[count];
            
               move_alt = 1 + count;

               count++;
        }
        System.out.println("move alt "+move_alt);
        moveNeural(move_alt);
        nella_graph.append("Correct move at: "+move_alt+"\n");
            } else{

            
            nella_graph.append("Correct move at cell: (d)"+movefinal_down+"\n");

            }
        }else {
            
            nella_graph.append("Correct move at cell: "+movefinal_up+"\n");
        }
       // JOptionPane.showMessageDialog(null, "play cell "+movefinal);
        }else{
        JOptionPane.showMessageDialog(null,"Man's turn!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    public double moveNeural(double movefinal){
        double result = 0;
        if(movefinal >=1 && movefinal <=9){
            if(movefinal == 1){
                if(!board.square_1.getState()){
                    board.square_1.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_1.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                    gameState();


                    result = movefinal;
                }else{
                    result = 0;
                }

            }else if(movefinal == 2){
                if(!board.square_2.getState()){
                    board.square_2.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_2.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                   gameState();


                    result =  movefinal;
                }else{
                result = 0;
                }
            }else if(movefinal == 3){
                if(!board.square_3.getState()){
                    board.square_3.changeValue(nella.getValue());
                   
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_3.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));


                   gameState();


                    result = movefinal;
                }else{
                result = 0;
                }
            }else if(movefinal == 4){
                if(!board.square_4.getState()){
                    board.square_4.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_4.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                    gameState();



                    result = movefinal;
                }else{
                    result = 0;
                }
            }else if(movefinal == 5){
                if(!board.square_5.getState()){
                    board.square_5.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_5.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                   gameState();

                    result = movefinal;
                }else{
                result = 0;
                }
            }else if(movefinal == 6){
                if(!board.square_6.getState()){
                    board.square_6.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_6.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                    gameState();

                    result = movefinal;
                }else{
                    result = 0;
                }
            }else if(movefinal == 7){
                if(!board.square_7.getState()){
                    board.square_7.changeValue(nella.getValue());
                   
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_7.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                    gameState();

                    result = movefinal;
                }else{
                    result = 0;
                }
            }else if(movefinal == 8){
                if(!board.square_8.getState()){
                    board.square_8.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_8.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));

                    gameState();

                    result = movefinal;
                }else{
                    result = 0;
                }
            }else if(movefinal == 9){
                if(!board.square_9.getState()){
                    board.square_9.changeValue(nella.getValue());
                    
                    training.writeToFile(board_, movefinal/10);
                    reverseActive();
                    square_9.setIcon(new javax.swing.ImageIcon("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/resources/x.png"));
                    
                    gameState();

                    result = movefinal;
                }else{
                        result = 0;
                }
            }
            return result;
        } else{
                    return 0;
            }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exitButton;
    private neuraltictactoe.GraphComponent graphComponent1;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextArea nella_graph;
    private javax.swing.JMenuItem newActionMenuItem;
    private javax.swing.JLabel play_banner;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel square_1;
    private javax.swing.JLabel square_2;
    private javax.swing.JLabel square_3;
    private javax.swing.JLabel square_4;
    private javax.swing.JLabel square_5;
    private javax.swing.JLabel square_6;
    private javax.swing.JLabel square_7;
    private javax.swing.JLabel square_8;
    private javax.swing.JLabel square_9;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel winner_;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    public boolean gameState = false;
    public boolean humanplay = true;
    public double gamecount = 0;
    public double wincount = 0;
    Board board = new Board();
    Player active = new Player("active");
    Player player1 = new Player("player1");
    Player player2 = new Player("player2");
    NeuralPlayer nella = new NeuralPlayer();
    SaveTrainingSet training = new SaveTrainingSet();
    int [][] board_ = new int [3][3];
    private JDialog aboutBox;
}
