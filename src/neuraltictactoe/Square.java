/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuraltictactoe;

/**
 *
 * @author Ishuah  K
 */

// The square used on the board
public class Square{

     private boolean _state;
     private int value;

     public Square(){
     _state = false;
     value = -1;
     }

     public void changeValue(int value){
       if(value == 0 || value == 1){
        this.value = value;
            }else{
            throw new IllegalArgumentException("Number supplied was " + value
					+ " but must be 0 or 1");
    }
         _state = true;

     }

     public int getValue(){
        return value;
     }

     public boolean getState(){
     return _state;
     }
 }

