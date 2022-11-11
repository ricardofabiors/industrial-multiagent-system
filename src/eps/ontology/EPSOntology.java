/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps.ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;

/**
 * Define Ontology for EPScore.
 *
 * @author andre
 */
public class EPSOntology extends BeanOntology {

    public static final String EPSONTOLOGYNAME = "eps-ontology";

    public static EPSOntology instance() {
        return instance;
    }

    private static final EPSOntology instance = new EPSOntology();

    private EPSOntology() {
        super(EPSONTOLOGYNAME);

        try {
            add("eps.ontology");
        } catch (BeanOntologyException ex) {
            System.out.println("Error criating an ontology. Exception " + ex);
        }
    }
}

