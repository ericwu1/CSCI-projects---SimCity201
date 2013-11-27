package restaurant;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import restaurant.gui.CookGui;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import agent.Agent;

public class CookAgent extends Agent {
	
	private final int NORMAL_AMOUNT_TO_ORDER = 8;
	
	String name;
	
	private CookGui gui;
	
	private Timer timer = new Timer();

	private List<Order> itemsToCook = Collections.synchronizedList(new ArrayList<Order>());
	private List<Food> foods = Collections.synchronizedList(new ArrayList<Food>());
	private List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	
	private HashMap<String, Integer> cookingMap = new HashMap<String, Integer>();
	
	private boolean outOfFood;
	enum CookState { idle, cooking };
	private CookState state = CookState.idle;
	
	enum OrderState { pending, cooking, justFinished, ready }
	
	public CookAgent(String name)
	{
		super();
		this.name = name;
	}

	public void msgCannotFulfill(MarketAgent market, String choice)
	{
		// food out, try different market
		synchronized(markets)
		{
			for (MyMarket m : markets)
			{
				if (market == m.marketRef)
				{
					// cannot order from that market
					m.foodAvail.remove(choice);
					// food not on order anymore
					print("Cannot order " + choice + " from " + market.name + " anymore.");
					synchronized(foods)
					{
						for (Food f : foods)
						{
							// force cook to order again
							if (f.name.equals(choice))
							{
								f.state = FoodState.inStock;
								break;
							}
						}
					}
					stateChanged();
					return;
				}
			}
		}
	}

	public void msgCanFulfillPartial(MarketAgent market, String choice, int amount)
	{
		synchronized(markets)
		{
			for (MyMarket m : markets)
			{
				if (market == m.marketRef)
				{
					// cannot order from that market
					m.foodAvail.remove(choice);
					// food not on order anymore
					print("Cannot order " + choice + " from " + market.name + " anymore.");
					synchronized(foods)
					{
						for (Food f : foods)
						{
							// force cook to order again
							if (f.name.equals(choice))
							{
								f.state = FoodState.inStock;
								f.amountToOrder -= amount;
								break;
							}
						}
					}
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgOrderDelivered(String choice, int amount)
	{
		synchronized(foods)
		{
			for (Food f : foods)
			{
				if (f.name.equals(choice))
				{
					f.state = FoodState.inStock;
					f.inventory += amount;
					outOfFood = false;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgPartialOrderDelivered(Market market, String choice, int amount)
	{
		synchronized(foods)
		{
			for (Food f : foods)
			{
				if (f.name.equals(choice))
				{
					f.state = FoodState.inStock;
					f.inventory += amount;
				}
			}
		}
		// fill rest of order from other market
		synchronized(markets)
		{
			for (MyMarket m : markets)
			{
				if (market == m.marketRef)
				{
					// cannot order from that market
					m.foodAvail.remove(choice);
					// food not on order anymore
					synchronized(foods)
					{
						for (Food f : foods)
						{
							if (f.name.equals(choice))
							{
								f.state = FoodState.inStock;
							}
						}
					}
				}
			}
		}
		outOfFood = false;
		stateChanged();
	}
	
	public void msgHereIsAnOrder(Waiter waiter, String choice, int tableNumber)
	{
		// if food is out
		if (outOfFood)
		{
			// out of place msg
			waiter.msgOutOfFood(tableNumber);
			return;
		}
		// get food object
		Food food = null;
		synchronized(foods)
		{
			for (Food f : foods)
			{
				if (f.name.equals(choice))
				{
					food = f;
					break;
				}
			}
		}
		if (food == null)
		{
			// food not found
			return;
		}
		if (food.inventory > 0)
		{
			Order order = new Order(waiter, choice, tableNumber);
			itemsToCook.add(order);
			food.inventory--;
			print("Recieved Order.");
			//debug
			print(food.inventory + " " + food.name + "(s) left.");
		}
		else
		{
			// out of place msg
			// notify waiter
			waiter.msgCannotCookItem(tableNumber);
		}
		stateChanged();
	}
	
	public void msgPickedUpOrder(int tableNumber)
	{
		synchronized(itemsToCook)
		{
			for (Order order : itemsToCook)
			{
				if (order.tableNumber == tableNumber)
				{
					gui.removeServingArea(order.tableNumber);
					itemsToCook.remove(order);
					stateChanged();
					return;
				}
			}
		}
	}
	
	@Override
	protected boolean pickAndExecuteAnAction()
	{
		if (state == CookState.idle)
		{
			synchronized(itemsToCook)
			{
				for (Order order : itemsToCook)
				{
					if (order.state == OrderState.justFinished)
					{
						print("Order up! Table " + order.tableNumber + "!");
						orderDone(order);
						return true;
					}
					else if (order.state ==  OrderState.pending)
					{
						Do("Cooking " + order.choice + ".");
						state = CookState.cooking;
						cook(order);
						return true;
					}
				}
			}
		}
		synchronized(itemsToCook)
		{
			for (Food f : foods)
			{
				if (f.inventory <= 3)
				{
					// out of item
					// notify waiter
					// order more if haven't already
					if (f.state == FoodState.inStock)
					{
						// order more;
						orderMoreFood(f, f.amountToOrder);
						// if amount was changed, reset to initial value
						f.amountToOrder = NORMAL_AMOUNT_TO_ORDER;
						return true;
					}
				}
			}
			// find out if cook has no food
			for (Food f : foods)
			{
				if (f.inventory > 0)
				{
					return true;
				}
			}
		}
		// all out of food
		outOfFood = true;
		return true;
	}

	// actions
	private void cook(Order order)
	{
		gui.setGrill(order.choice);
		TimerTask t = new TimerTask() {
			Order order;
			public void run() {
				state = CookState.idle;
				order.state = OrderState.justFinished;
				//isHungry = false;
				gui.setGrill("");
				stateChanged();
			}
			public TimerTask init(Order o)
			{
				order = o;
				return this;
			}
		}.init(order);
		timer.schedule(t, cookingMap.get(order.choice));//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void orderDone(Order order)
	{
		gui.addServingArea(order.tableNumber, order.choice);
		order.state = OrderState.ready;
		order.waiter.msgOrderIsReady(order.tableNumber, order.choice);
	}
	
	private void orderMoreFood(Food food, int amount)
	{
		// sequentially chooses markets
		for (MyMarket market : markets)
		{
			// if market has food
			if (market.foodAvail.contains(food.name))
			{
				food.state = FoodState.onOrder;
				Do("Ordering " + amount + "x " + food.name + " from " + market.marketRef.getName() + ".");
				market.marketRef.msgWantMoreFood(this, food.name, amount);
				break;
			}
		}
	}
	
	public void addItemToInventory(String choice, int amount, int cookingTime)
	{
		Food food = new Food(choice, amount);
		cookingMap.put(choice, cookingTime);
		foods.add(food);
		stateChanged();
	}

	public void addMarket(Market market)
	{
		ArrayList<String> foodList = new ArrayList<String>();
		for (Food food : foods)
		{
			foodList.add(food.name);
		}
		markets.add(new MyMarket(market, foodList));
		stateChanged();
	}
	
	public String getName() {
		return name;
	}
	
	private class Order
	{
		String choice;
		int tableNumber;
		Waiter waiter;
		OrderState state;
		
		public Order(Waiter waiter, String choice, int tableNumber)
		{
			this.waiter = waiter;
			this.choice = choice;
			this.tableNumber = tableNumber;
			state = OrderState.pending;
		}
	}
	
	private enum FoodState { inStock, onOrder };
	
	private class Food
	{
		String name;
		int inventory;
		int amountToOrder;
		FoodState state;
		
		public Food(String name, int inventory)
		{
			this.name = name;
			this.inventory = inventory;
			amountToOrder = NORMAL_AMOUNT_TO_ORDER;
			state = FoodState.inStock;
		}
	}
	
	private class MyMarket
	{
		Market marketRef;
		ArrayList<String> foodAvail;
		
		public MyMarket(Market market, ArrayList<String> foods)
		{
			marketRef = market;
			foodAvail = foods;
		}
	}

	public void setGui(CookGui cookGui) {
		gui = cookGui;
	}
}