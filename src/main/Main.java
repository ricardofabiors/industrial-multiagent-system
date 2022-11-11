/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fábio Ricardo
 */
public class Main {
    public static void main(String[] args) {    //não estou usando essa classe main por enquanto, pois não sei como setar host, port... essas coisas
        Runtime rt;
        rt = Runtime.instance();
        ContainerController mainContainer;
        mainContainer = rt.createMainContainer(new ProfileImpl(true));
        AgentController myAgentController;
        
        try {
            myAgentController = mainContainer.createNewAgent("Gateway", "staudinger.business.Gateway", null);
            myAgentController.start();
        } catch (StaleProxyException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
