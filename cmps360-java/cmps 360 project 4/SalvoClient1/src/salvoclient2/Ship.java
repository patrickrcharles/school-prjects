/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salvoclient2;

/**
 *
 * @author Patrick
 */
public class Ship {

    public int type;
    //first ship to place is battleship = 5
    public int size;
    public boolean vertical;
    public int health;
    
    //ships spaces occupied
    public int x1, y1;
    public int x2, y2;
    public int x3, y3;
    public int x4, y4;
    public int x5, y5;
    

    Ship() {
        vertical = true;

    }

    //ship's size/type and if vertical
    public Ship(int size, boolean vertical) {
        this.size = size;
        this.health = size;
        this.vertical = vertical;
        //switch get type, set health at 5,4,32, etc..
    }

    public void hit() {
        //update cell color
        //decrement health
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getShipType() {

        String temp = null;
        switch (size) {
            case 5:
                temp = "Battleship";
                break;
            case 4:
                temp = "Cruiser";
                break;
            case 3:
                temp = "Submarine";
                break;
            case 2:
                temp = "Destroyer";
                break;
        }
        return temp;
    }

    public void placeBattleShip(int row, int column) {
        // check if Vertical
        // check neighbors
        // update cell data
    }

    public void placeCruiser(int row, int column) {

    }

    public void placeSubmarine(int row, int column) {

    }

    public void placeDestroyer(int row, int column) {

    }

}
