/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps;

/**
 * Generic Product
 *
 * @author Rafael
 */
public abstract class Product extends MRA {

    public Product() {
    }

    protected abstract void produce();

    @Override
    protected MRAInfo getMRAInfo() {
        return new MRAInfo();
    }

    @Override
    protected Skill[] getSkills() {
        return new Skill[0];
    }

}
