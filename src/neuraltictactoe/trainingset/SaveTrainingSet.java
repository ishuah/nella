/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuraltictactoe.trainingset;

/**
 *
 * @author Ishuah  K
 */

    import java.io.File;
    import java.io.PrintWriter;
    import java.io.IOException;
    


public class SaveTrainingSet {

    private File output;
    private PrintWriter pw;

    public void initWrite() throws IOException{
        try{
        output = new File("C:/Users/Ishuah  K/NeuralTicTacToe/src/neuraltictactoe/trainingset/tictactoedata.tttd");
        pw = new PrintWriter(output);

        }
        catch(SecurityException se){
        System.out.println("Write acces denied "+se);
        }
        

        
    }

    public void writeToFile(int [][] board, double move_){
    for(int x = 0; x<board.length;x++){
                for(int y = 0; y<board.length;y++){
                pw.print(board[x][y]+" ");

                }
              }
    /*
              for(int i = 0; i<move_.length; i++){
              pw.print(move_[i]+" ");
              } */

              pw.println(move_+" ");
    }

    public void closeConnection(){
    pw.close();
    }
}
