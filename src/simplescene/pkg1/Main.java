/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package simplescene.pkg1;

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
    public static void main(String[] args) { 
        jade.core.Runtime rt;
        rt = jade.core.Runtime.instance();
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
