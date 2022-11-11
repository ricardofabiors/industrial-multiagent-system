/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.cognitive;

/**
 * Classe que representa um caixote com todas suas caracterísiticas. No momento,
 * é usada somente para prover os valores inteiros para as cores dos caixotes. 
 * Seu uso, entretanto, será bem maior em versões futuras, nas quais o aramazém 
 * é levado em conta.
 * 
 * @author Fábio Ricardo
 */
public class Box {
    //box colors
    public static final int GREEN = 0;
    public static final int BLACK = 1;
    public static final int UNKNOWN = 5;
    
    private int color;          //cor do caixote
    private int resources;    //quantidade de bolinhas
    private String rfid_code;   //código rfid
    private boolean isOpen;     //se está tampado

    public Box(int color, String rfid_code){
        this.color = color;
        this.resources = 0;
        this.rfid_code = rfid_code;
        this.isOpen = true;
    }

    public Box(int color){
        this(color, "");
    }
    
    public Box(){
        this(UNKNOWN, "");
    } 

    public boolean isOpen(){
        return isOpen;
    }

    public void close(){
        isOpen = false;
    }

    public int getColor(){
        return color;
    }
    
    public String getCode(){
        return rfid_code;
    }
    
    public void setCode(String code){
        rfid_code = code;
    }
    
}
