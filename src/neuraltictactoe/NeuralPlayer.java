/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuraltictactoe;

/**
 *
 * @author Ishuah  K
 */

import java.io.FileNotFoundException;
import org.neuroph.core.NeuralNetwork;
import java.util.Vector;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.core.learning.SupervisedTrainingElement;
import java.util.Scanner;
import java.io.File;
import javax.swing.JOptionPane;

//The neural network player
public class NeuralPlayer extends Player{

    int point = 0;
    int prevpoint = 0;
    NeuralNetwork myNeuralNetwork;
    double move;
    private  File input;
    private Scanner r;

        // Load the neural network. Be sure it points to the right file in folder neuralnetworksfromneuroph.
    // You can interchange with the different neuraal networks in that file. (*.nnet)
        public void loadNet(){
            myNeuralNetwork = NeuralNetwork.load("C:/Users/Ishuah  K/Desktop/neuroph_2.3.1/nella_2.nnet");
            }


        public double calcNet(double [] inputArray){
        myNeuralNetwork.setInput(inputArray); // calculate network
        myNeuralNetwork.calculate(); // get network output
        Vector <Double> networkOutput = myNeuralNetwork.getOutput();
       move =  networkOutput.firstElement();
       return move;
        }

        public void setPoint(int outcome){
            prevpoint = point;
            this.point = point + outcome;
        }

        public void trainNet(){
            double traininput [] = new double [9];
            double trainoutput [] = new double[1];
            TrainingSet trainingSet = new TrainingSet();
            input = new File("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/trainingset/tictactoedata.tttd");

            try {
            r = new Scanner(input);

            System.out.println("File ok.(1)complete");
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file:"+ex);
        }
       
       
        while(r.hasNext()){
       
            System.out.println("one");
        int ind = 1;
        int index = 0;
        for( ind = 1; ind<10; ind++){
        traininput [index] = r.nextInt();
        System.out.print(traininput[index]+" ");
        index++;
        }
        trainoutput [0] = r.nextDouble();
        System.out.println(trainoutput[0]);
        trainingSet.addElement(new SupervisedTrainingElement(traininput, trainoutput));
        
        }
        if(point >= prevpoint){
            myNeuralNetwork.learnInSameThread(trainingSet);
        }else{
            System.out.println("No training done, lost game.");
        }
      // myNeuralNetwork.save("C:/Users/Ishuah  K/Desktop/neuroph_2.3.1/nella.nnet");
        System.out.println("Finished");
        r.close();
        }

}
