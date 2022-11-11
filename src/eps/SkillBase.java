/*
 * Copyright (c) Andre Cavalcante 2008-2015
 * All right reserved
 */
package eps;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class base para skills
 *
 * @author andre
 */
public abstract class SkillBase implements Serializable {

    protected String name;
    protected String resultType;
    protected String[] argsTypes;
    protected String[] argsValues;
    protected HashMap<String, String> properties;
    protected String result;

    public SkillBase() {
        this.name = "";
        this.resultType = "";
        this.argsTypes = new String[0];
        this.argsValues = new String[0];
        this.properties = new HashMap<>();
        this.result = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String[] getArgsTypes() {
        return argsTypes;
    }

    public void setArgsTypes(String[] argsTypes) {
        this.argsTypes = argsTypes;
    }

    public String[] getArgsValues() {
        return argsValues;
    }

    public void setArgsValues(String[] argsValues) {
        this.argsValues = argsValues;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String[] getProperties() {
        String[] props = new String[properties.size()];
        int i = 0;
        for (String key : properties.keySet()) {
            props[i] = key + "=" + properties.get(key);
            i++;
        }
        return props;
    }

    public void setProperties(String[] props) {
        String[] sp;
        properties.clear();
        if (props == null || props.length == 0) {
            return;
        }
        for (String s : props) {
            sp = s.split("=");
            if (sp.length == 2) {
                properties.put(sp[0], sp[1]);
            }
        }
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public void removeProperty(String name) {
        properties.remove(name);
    }

    public String getPropertyValue(String name) {
        return properties.get(name);
    }

    protected static String getPropName(String prop) {
        String[] vet = prop.split("=");
        return vet[0];
    }

    protected static String getPropValue(String prop) {
        String[] vet = prop.split("=");
        return vet[1];
    }
    
    /**
     * Verifica se o objeto SkillBase em questão contém, em suas propriedades,
     * as propriedades passadas como parâmetro.
     * @param props Propriedades a serem verificadas se estão contidas.
     * @return True se as propriedades passadas estiverem contidas.
     */
    protected boolean hasTheseProps(String[] props){
        if (props == null || props.length == 0 && !properties.isEmpty()) {
            return false;
        }
        String[] sp;
        for (String s : props) {
            sp = s.split("=");
            if (sp.length == 2) {
                String unverified_key = sp[0];
                if(!properties.containsKey(unverified_key)){    //se a "chave não verificada" NÃO está contida em properties
                    return false;
                }
            }
            else return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.name);
        hash = 71 * hash + Objects.hashCode(this.resultType);
        hash = 71 * hash + Arrays.deepHashCode(this.argsTypes);
        hash = 71 * hash + Arrays.deepHashCode(this.getProperties());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SkillBase)) {
            return false;
        }
        final SkillBase other = (SkillBase) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.resultType, other.resultType)) {
            return false;
        }
        if (!Arrays.deepEquals(this.argsTypes, other.argsTypes)) {
            return false;
        }
        if (!Arrays.deepEquals(this.getProperties(), other.getProperties())) {
            return false;
        }
        return true;
    }

    public boolean equalsIgnoreProperties(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SkillBase)) {
            return false;
        }
        final SkillBase other = (SkillBase) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.resultType, other.resultType)) {
            return false;
        }
        if (!Arrays.deepEquals(this.argsTypes, other.argsTypes)) {
            return false;
        }
        return true;
    }
    
    /**
     * Verifica se o objeto passado é um SkillBase igual a este, mas sem precisar
     * conter todas as propriedades.
     * @param obj Objeto SkillBase a ser comparado.
     * @return True se os objetos forem iguais sem que as propriedades sejam 
     * exatamente as mesmas.
     */
    public boolean equalsWithoutAllProperties(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SkillBase)) {
            return false;
        }
        final SkillBase other = (SkillBase) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.resultType, other.resultType)) {
            return false;
        }
        if (!Arrays.deepEquals(this.argsTypes, other.argsTypes)) {
            return false;
        }
        if(!hasTheseProps(other.getProperties())){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("{");
        sb.append(" name: ").append(getName());
        sb.append(" args: {");
        if (getArgsTypes() != null) {
            for (int i = 0; i < getArgsTypes().length; i++) {
                sb.append(getArgsTypes()[i]);
                if (i < getArgsTypes().length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}");
        sb.append(" resultType: ").append(getResultType());
        sb.append("}");
        sb.append(" properties: {");
        if (properties != null) {
            String[] names = properties.keySet().toArray(new String[0]);
            for (int i = 0; i < names.length; i++) {
                sb.append(names[i]).append("=").append(properties.get(names[i]));
                if (i < names.length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
