package market;

import java.util.*;

import SimCity.Base.Role;
import market.MarketClerkRole.OrderState;
import market.gui.MarketManagerGui;
import market.interfaces.MarketManager;

/**
 * Market Manager Agent
 * 
 * @author Timothy So
 */
public class MarketManagerRole extends Role implements MarketManager {
	
	private String name;
	private MarketManagerGui gui = new MarketManagerGui(this);
	
	/**
	 * Data
	 */
	
	private Map<String, Inventory> inventory = Collections.synchronizedMap(new HashMap<String, Inventory>());
	public List<MyPacker> packers = Collections.synchronizedList(new ArrayList<MyPacker>());
	public List<MyClerk> clerks = Collections.synchronizedList(new ArrayList<MyClerk>());
    public List<MyDeliveryPerson> deliveryPeople = Collections.synchronizedList(new ArrayList<MyDeliveryPerson>());

    public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
    
	public MarketManagerRole(String name) {
		super();
		
		// initalize inventory
		inventory.put("Steak", new Inventory(10, 0));
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/** 
	 * Messages
	 */
	
	public void msgWantFood(String name, String choice, int amount)
	{
	    
	}
	
	public void msgFulfillOrder(String name, String choice, int amount)
    {
        
    }
	
	public void msgOrderPacked(String name, String choice, int amount)
    {
        
    }
	
    public void msgHereIsTheMoney(String name, int amount)
    {
        
    }
    
    public void msgWithdrawalSuccessful(int amount)
    {
        
    }
    
    public void msgDepositSuccessful()
    {
        
    }
    
    public void workOver()
    {
        // TODO Auto-generated method stub
        
    }

	/**
	 * Scheduler. Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
	    
	    
		return false;
		// we have tried all our rules and found
		// nothing to do. So return false to main loop of abstract agent
		// and wait.
	}

	/**
	 * Actions
	 */

    /**
     * Utilities
     */

	public void setGui(MarketManagerGui gui) {
		this.gui = gui;
	}

	
	public MarketManagerGui getGui() { return gui; }
	

    /**
     * Inner Classes
     */
	
	public enum PackerState { Idle, Packing };
	
	public class MyPacker 
	{
		MarketPackerRole packer;
		PackerState state;
		
		MyPacker(MarketPackerRole packer) 
		{
			this.packer = packer;
			state = PackerState.Idle;
		}
	}
    
    public enum ClerkState { Idle, Busy };
    
    public class MyClerk 
    {
        MarketClerkRole clerk;
        ClerkState state;
        
        MyClerk(MarketClerkRole clerk) 
        {
            this.clerk = clerk;
            state = ClerkState.Idle;
        }
    }
    
    public enum DeliveryPersonState { Idle, Delivering };
    
    public class MyDeliveryPerson 
    {
        MarketDeliveryPersonRole deliveryPerson;
        DeliveryPersonState state;
        
        MyDeliveryPerson(MarketDeliveryPersonRole deliveryPerson) 
        {
            this.deliveryPerson = deliveryPerson;
            state = DeliveryPersonState.Idle;
        }
    }

    public enum OrderState { Pending, Processing, Ready };
    
    public class Order
    {
        String name;
        String choice;
        int amount;
        OrderState state;
        
        Order(String name, String choice, int amount)
        {
            this.name = name;
            this.choice = choice;
            this.amount = amount;
            state = OrderState.Pending;
        }
    }
    
    public class Inventory
    {
        private int amount;
        private int location;
        
        Inventory(int amount, int location)
        {
            this.amount = amount;
            this.location = location;
            gui.updateInventory(name, amount, location);
        }
        
        void changeAmount(int amount)
        {
            this.amount = amount;
            gui.updateInventory(name, amount, location);
        }
        
        void changeLocation(int location)
        {
            this.location = location;
            gui.updateInventory(name, amount, location);
        }
        
        int getAmount()
        {
            return amount;
        }
        
        int getLocation()
        {
            return location;
        }
    }
}