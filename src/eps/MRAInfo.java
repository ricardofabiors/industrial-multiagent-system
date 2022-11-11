/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps;

import jade.content.Concept;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Specific informations about MRAs
 *
 * @author Andre e Rafael
 */
public class MRAInfo implements Concept {

    /**
     * The unique identification in the system for this module/agent.
     */
    private String aid;
    
    /**
     * An array of skill templates of this module/agent
     */
    private SkillTemplate[] skills;
    
    /**
     * An map of properties (key = value pairs). Properties are 
     * mechanical/electrical/logical attributes of a module
     */
    private Map<String, String> properties;

    public MRAInfo() {
        properties = new HashMap<>();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.aid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MRAInfo other = (MRAInfo) obj;
        if (!Objects.equals(this.aid, other.aid)) {
            return false;
        }
        return true;
    }

    /**
     * Get the value of aid
     *
     * @return the value of aid
     */
    public String getAID() {
        return aid;
    }

    /**
     * Set the value of aid
     *
     * @param aid new value of aid
     */
    public void setAID(String aid) {
        this.aid = aid;
    }

    /**
     * Get the value of skills
     *
     * @return the value of skills
     */
    public SkillTemplate[] getSkills() {
        return skills;
    }

    /**
     * Set the value of skills
     *
     * @param skills new value of skills
     */
    public void setSkills(SkillTemplate[] skills) {
        this.skills = skills;
    }

    /**
     * Get the value of properties
     *
     * @return the value of properties
     */
    public String[] getProperties() {
        int size = properties.keySet().size();
        List<String> lines = new ArrayList<>(size);
        for (String key : properties.keySet()) {
            lines.add(key + "=" + properties.get(key));
        }
        return lines.toArray(new String[size]);
    }

    /**
     * Set the value of properties
     *
     * @param props new value of properties
     */
    public void setProperties(String[] props) {
        String[] par;
        properties.clear();
        for (String line : props) {
            if (!line.isEmpty()) {
                par = line.split("=");
                if (par.length == 2) {
                    properties.put(par[0], par[1]);
                }
            }
        }
    }

    public String getPropertyValue(String key) {
        return properties.get(key);
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public void clear() {
        properties.clear();
    }

    @Override
    public String toString() {
        return aid;
    }

    public boolean hasSkillTemplate(SkillTemplate st) {
        return hasSkillTemplate(st, false);
    }
    
    public boolean hasSkillTemplate(SkillTemplate st, boolean ignoreProperties) {
        for(SkillTemplate mySt : skills) {
            if(ignoreProperties) {
                if(mySt.equalsIgnoreProperties(st)) {
                    return true;
                }
            } 
            else {
                if(mySt.equals(st)) {
                    return true;
                }
                else if(mySt.equalsWithoutAllProperties(st)){   
                    return true;
                }
            }
        }
        return false;
    }

}
