/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package staudinger.cognitive;

import eps.MRA;
import eps.SkillTemplate;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import java.util.ArrayList;

/**
 * Classe que define um plano de execução dentro da aplicação. Tais planos são
 * usados nos agentes do tipo produto, os quais especificam os itens do plano e
 * os executam.
 * 
 * @author Fábio Ricardo
 */
public class Plan {
    private SequentialBehaviour seqBehaviour;   //comportamento sequencial ao qual os itens (subcomportamentos) são adicionados
    private MRA owner;                          //agente pelo qual o plano será executado
    private ArrayList plan;                     //lista de itens que constituem um plano
    
    /**
     * Construtor padrão da classe que recebe a quantidade de bolinhas.
     * @param owner Agente pelo qual o plano será executado.
     */
    public Plan(MRA owner) {
        this.seqBehaviour = new SequentialBehaviour();
        this.setOwner(owner);
        this.plan = new ArrayList();
        this.owner.addBehaviour(this.seqBehaviour);
    }
    
    /**
     * Adiciona um item do tipo PlanItem ao plano, que é criado utilizando o 
     * método "createNewPlanItem". É usado pelos agentes do tipo produto para
     * especificar as etapas do plano.
     * @param skill "SkillTemplate" que define a skill necessitada para a etapa
     * do plano.
     */
    public void addNewPlanItem(SkillTemplate skill){
        Item newItem = createNewPlanItem(skill);
        plan.add(newItem);
    }
    
    /**
     * Cria um novo item de plano (PlanItem). É encapsulado em "addNewPlanItem"
     * mas também é usado para criar itens de escolha para itens de decisão.
     * @param skill "SkillTemplate" que define a skill necessitada para a etapa
     * do plano.
     * @return O item de plano (PlanItem) recém criado.
     */
    public PlanItem createNewPlanItem(SkillTemplate skill){
        PlanItem newItem = new PlanItem(skill, owner);
        return newItem;
    }

    public void addNewParallelItem(PlanItem item_1, PlanItem item_2){
        Item newItem = new ParallelItem(item_1, item_2);
        plan.add(newItem);
    }
    
    /**
     * Cria e adiciona um novo item de decisão ao plano. É usado pelos agentes 
     * do tipo produto.
     * @param decision PlanItem cujo o resultado do comportamento decidirá o 
     * próximo item a ser executado.
     * @param choice0 PlanItem que representa a primeira escolha.
     * @param choice1 PlanItem que representa a segunda escolha.
     */
    public void addNewDecisionItem(PlanItem decision, PlanItem choice0, PlanItem choice1){
        Item newItem = new DecisionItem(decision, choice0, choice1);
        plan.add(newItem);
    }
    
    /**
     * Adiciona os comportamentos (utilizando o método "execute") de cada item
     * ao comportamento sequencial do plano, que por sua vez é adicionado ao agente
     * através do construtor. Por último, adiciona-se um comportamento para 
     * deletar o agente.
     */
    public void execute(){
        for(int i = 0; i < plan.size(); i++){
            Behaviour executionBehaviour = ((Item) plan.get(i)).execute();
            this.seqBehaviour.addSubBehaviour(executionBehaviour);
        }
        //encerra o plano deletando o agente
        this.seqBehaviour.addSubBehaviour(new OneShotBehaviour(){
            @Override
            public void action() {
                owner.doDelete();
            }
        });
    }

    public void setOwner(MRA owner) {
        this.owner = owner;
    }
}
