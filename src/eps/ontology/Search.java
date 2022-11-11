/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps.ontology;

import eps.MRAInfo;
import eps.SkillTemplate;
import jade.content.AgentAction;

/**
 *
 * @author Rafael
 */
public class Search implements AgentAction {

    private boolean ignoreProperties;
    private SkillTemplate skillTemplate;
    private MRAInfo[] mraInfoArr;

    public Search() {
    }

    public boolean isIgnoreProperties() {
        return ignoreProperties;
    }

    public void setIgnoreProperties(boolean ignoreProperties) {
        this.ignoreProperties = ignoreProperties;
    }

    public SkillTemplate getSkillTemplate() {
        return skillTemplate;
    }

    public void setSkillTemplate(SkillTemplate skillTemplate) {
        this.skillTemplate = skillTemplate;
    }

    public MRAInfo[] getMraInfoArr() {
        return mraInfoArr;
    }

    public void setMraInfoArr(MRAInfo[] mraInfoArr) {
        this.mraInfoArr = mraInfoArr;
    }
   
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Search{");
        sb.append(" skillTemplate: {").append(skillTemplate.toString()).append("}");
        sb.append(" mraInfoArr: {");
        for(MRAInfo mraInfo : mraInfoArr) {
            sb.append(mraInfo.toString()).append(",");
        }
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }
}
