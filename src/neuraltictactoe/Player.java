/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuraltictactoe;

/**
 *
 * @author Ishuah  K
 */

// Basic player object
public class Player {

    public String name;
    private int value;

    public Player(String name){
    this.name = name;
    value = -1;

    }
    public Player(){
    name = "nella";
    value = -1;
    }

    public void setValue(int value){
    if(value == 0 || value == 1){
    this.value = value;
    }else{
    throw new IllegalArgumentException("Number supplied was " + value
					+ " but must be 0 or 1");
    }
    }

    public int getValue(){
    return value;
    }
}
