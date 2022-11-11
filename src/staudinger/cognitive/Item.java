/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.cognitive;

import jade.core.behaviours.Behaviour;

/**
 * Interface que especifica o formato de um item a ser usado no plano de execução.
 * 
 * @author Fábio Ricardo
 */
public interface Item {
    
    Behaviour execute();    //retorna o comportamento a ser adicionado ao agente através da execução do plano  
}
