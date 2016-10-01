/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ANKIT
 */
public class BoardUnit {
    int color;//1=white,2=black
    int kind;//1=flatstone,2=wall,3=capstone
    BoardUnit(int color, int kind){
        this.color = color;
        this.kind = kind;
    }
    void setColor(int color){
        this.color = color;
    }
    void setKind(int kind){
        this.kind = kind;
    }
    int getColor(){
        return this.color;
    }
    int getKind(){
        return this.kind;
    }
}
