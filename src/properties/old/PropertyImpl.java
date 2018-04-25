/*
 * Meme Team Software Engineering Project
 * Property Tycoon
 */
package properties.old;

import java.util.Arrays;
import property_tycoon.model.Player;

/**
 *
 * @author Matt
 */


class PropertyImpl extends Property
{
    private final String description;
    private Group group;
    private boolean isMortgaged;
    private Level level;
    private Player owner;
    private final int[] rent;
    private final int value;

    public PropertyImpl(String description, int value, int[] rent)
    {
        // Check arguments
        assert description != null : "description should not be null.";
        assert !description.isEmpty() : "description should not be empty.";
        assert value > 0 : "value should be positive.";

        assert rent != null : "rent should not be null.";
        assert rent.length == Level.LEVEL_COUNT : 
            String.format(
                "rent should contain %d (Level.LEVEL_COUNT) elements not %d.", 
                Level.LEVEL_COUNT, 
                rent.length);

        // Check rent elements are positive
        int i = 0;
        while(i < rent.length && rent[i] > 0) {
            i++;
        }

        assert i == rent.length :
            "rent should only contain positive elements.";

        // Assign fields
        this.description = description;
        this.value = value;

        // Copy the array so that elements cannot
        // be subsequently modified by external code.
        this.rent = Arrays.copyOf(rent, rent.length);

        group = null;
        owner = null;
        level = Level.UNIMPROVED;
        isMortgaged = false;
    }

    @Override
    public Property buy(Player buyer)
    {
        if(isOwned()) {
            throw new IllegalStateException("Property already has an owner."
                + " It must be sold before being rebought.");
        }

        if(buyer == null) {
            throw new IllegalArgumentException("buyer should not be null.");
        }

        owner = buyer;
        return new PropertyProxy(this);
    }

    @Override
    public int downgrade()
    {
        if(!isGrouped()) {
            throw new IllegalStateException("Property is not grouped.");
        }
        
        if(getLevel().compareTo(getGroup().getHighestLevel()) < 0) {
            throw new IllegalStateException("Property cannot be downgraded."
                + " Doing so would cause improvment levels"
                + " in this group to differ by more than one.");
        }

        if(getLevel().isMin()) {
            throw new IllegalStateException("Property cannot be downgraded."
                + " It is already at the minimum improvment level.");
        }

        level = getLevel().getPrevious();
        
        return getHouseCost();
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public Group getGroup()
    {
        if(!isGrouped()) {
            throw new IllegalStateException();
        }

        return group;
    }

    @Override
    public void setGroup(Group g)
    {
        if(isGrouped()) {
            throw new IllegalStateException("Property is already in a group.\n"
                + "A Property can only be assigned a group once.");
        }

        if(g == null) {
            throw new IllegalArgumentException();
        }

        group = g;
    }

    @Override
    public Level getLevel()
    {
        return level;
    }

    @Override
    public Player getOwner()
    {
        if(!isOwned()) {
            throw new IllegalStateException("Property does not have an owner.");
        }

        return owner;
    }

    @Override
    public int getRentCost()
    {
        return rent[getLevel().getValue()];
    }

    @Override
    public int getRentCost(Level l)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getValue()
    {
        return value;
    }

    @Override
    public boolean isGrouped()
    {
        return group != null;
    }

    @Override
    public boolean isOwned()
    {
        return owner != null;
    }

    @Override
    public int upgrade()
    {
        if(!isGrouped()) {
            throw new IllegalStateException("Property is not grouped.");
        }
        
        if(getLevel().compareTo(getGroup().getLowestLevel()) > 0) {
            throw new IllegalStateException("Property cannot be upgraded."
                + " Doing so would cause improvment levels"
                + " in this group to differ by more than one.");
        }

        if(getLevel().isMax()) {
            throw new IllegalStateException("Property cannot be upgraded."
                + " It is already at the maximum improvment level.");
        }

        level = getLevel().getNext();
        
        return getHouseCost();
    }

    @Override
    public boolean isMortgaged()
    {
        return isMortgaged;
    }

    @Override
    public boolean isValid()
    {
        throw new UnsupportedOperationException(
            "isValid() is not supported by real properties.");
    }

    @Override
    public int mortgage()
    {
        if(!isOwned()) {
            throw new IllegalStateException(
                "Property has no owner so cannot be morgaged.");
        }

        if(isMortgaged()) {
            throw new IllegalStateException(
                "Property is already mortgaged.");
        }

        isMortgaged = true;
        
        return getValue() - getMortgagedValue();
    }

    @Override
    public int sell()
    {
        if(!isOwned()) {
            throw new IllegalStateException("Property has no owner.");
        }

        owner = null;

        if(isMortgaged()) {
            isMortgaged = false;
            return getMortgagedValue();
        }
        else {
            return getValue();
        }
    }

    @Override
    public Property trade(Player buyer, Player seller)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int unmortgage()
    {
        if(isMortgaged()) {
            throw new IllegalStateException(
                "Property is not mortgaged.");
        }

        isMortgaged = false;
        
        return getValue() - getMortgagedValue();
    }

}
