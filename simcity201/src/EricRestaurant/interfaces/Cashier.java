package EricRestaurant.interfaces;

import EricRestaurant.EricCashier;
import EricRestaurant.EricHost;
import EricRestaurant.gui.CustomerGui;

public interface Cashier {

	/**
	 * hack to establish connection to Host agent.
	 */
	public abstract void setHost(EricHost host);

	public abstract void setCash(EricCashier cash);

	public abstract void setWaiter(Waiter waiter);

	public abstract String getCustomerName();

	// Messages

	public abstract void gotHungry();

	public abstract void msgSitAtTable(Waiter w, int table);

	public abstract void msgAnimationFinishedGoToSeat();

	public abstract void whatOrder();

	public abstract void checkToCust(EricCashier cs);

	public abstract void foodReceived();

	public abstract void giveChange(double change);

	public abstract void bumChange(double change);

	public abstract void msgAnimationFinishedLeaveRestaurant();

	public abstract void leaveTable();

	public abstract String getName();

	public abstract int getHungerLevel();

	public abstract void setHungerLevel(int hungerLevel);

	public abstract String toString();

	public abstract void setGui(CustomerGui g);

	public abstract CustomerGui getGui();

}