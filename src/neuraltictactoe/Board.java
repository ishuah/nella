/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuraltictactoe;

/**
 *
 * @author Ishuah  K
 */

/*This class is the virtual board on which the moves are made. */
public class Board {

    //These nine squares represent the nine squares on the board

    public Square square_1 = new Square();
    public Square square_2 = new Square();
    public Square square_3 = new Square();
    public Square square_4 = new Square();
    public Square square_5 = new Square();
    public Square square_6 = new Square();
    public Square square_7 = new Square();
    public Square square_8 = new Square();
    public Square square_9 = new Square();

    //Empty cconstructor
    public Board(){

    }
    //Method to check if the board is full
    public boolean isFull(){
    if(square_1.getState() == false || square_2.getState() == false || square_3.getState() == false
                || square_4.getState() == false || square_5.getState() == false || square_6.getState() == false
                || square_7.getState() == false || square_8.getState() == false || square_9.getState() == false){
        return false;
        }else{
        return true;
        }

    }
    //Method to resolve winner
    public int isWinner(int player){

        //Horizontals
        if(square_1.getValue() == player && square_2.getValue() == player && square_3.getValue() == player){
            return 1;
        }else if(square_4.getValue() == player && square_5.getValue() == player && square_6.getValue() == player){
            return 2;
        }else if(square_7.getValue() == player && square_8.getValue() == player && square_9.getValue() == player){
            return 3;
        }else
            //verticals
            if(square_1.getValue() == player && square_4.getValue() == player && square_7.getValue() == player){
    return 4;
    }else if(square_2.getValue() == player && square_5.getValue() == player && square_8.getValue() == player){
    return 5;
    }else if(square_3.getValue() == player && square_6.getValue() == player && square_9.getValue() == player){
    return 6;
    } else //Diagonals
        if(square_1.getValue() == player && square_5.getValue() == player && square_9.getValue() == player){
    return 7;
    } if(square_7.getValue() == player && square_5.getValue() == player && square_3.getValue() == player){
    return 8;
    }
    return 0;
    }

}

 