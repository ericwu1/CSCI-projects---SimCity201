package brianRest;

import SimCity.Base.Role;
import SimCity.Buildings.B_BrianRestaurant;
import agent.Agent;
import brianRest.interfaces.BrianCustomer;
import brianRest.interfaces.BrianHost;
import brianRest.interfaces.BrianWaiter;

import java.util.*;

import restaurant.interfaces.Waiter;


/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class BrianHostRole extends Role implements BrianHost {
	
	private final int WINDOWX = 640;
	private final int WINDOWY = 640;
	
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	private List<WaitingCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<WaitingCustomer>());
	private enum WaitingCustomerState {none, full};
	public int customersInRestaurant = 0;
	
	//List of waiters
	private List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	
	public ArrayList<BrianTable> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	boolean wantToGoHome = false;
	
	private enum MyWaiterState {none, wantBreak, allowedBreak, onBreak};
	int workingWaiters = 0;

	public BrianHostRole(String name) {
		super();
		this.name = name;
		// make some tables
		tables = new ArrayList<BrianTable>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new BrianTable(ix, WINDOWX/(NTABLES+2) * ix, 7*WINDOWY/10)); // animation for later
		}
	}

	public String getName() {
		return name;
	}

//########### Messages #####################
	
	//Waiter wants a break.
	@Override
	public void msgWaiterWantsABreak(BrianWaiter waiter){
		synchronized (waiters){
			for(MyWaiter w: waiters){
				if (w.waiter == waiter){
					w.state = MyWaiterState.wantBreak;
					stateChanged();
					return;
				}
			}
		}
	}
	
	@Override
	public void msgWaiterOffBreak(BrianWaiter waiter){
		synchronized (waiters){
			for(MyWaiter w: waiters){
				if (w.waiter == waiter){
					w.state = MyWaiterState.none;
					workingWaiters++;
					stateChanged();
					return;
				}
			}
		}
	}

	@Override
	public void msgIWantToEat(BrianCustomer c){
		System.out.println("Customer wants to eat");
		waitingCustomers.add(new WaitingCustomer(c));
		stateChanged();
	}
	
	@Override
	public void msgTableIsClear(BrianTable t){
		t.occupiedBy = null;
		customersInRestaurant --;
		stateChanged();
	}
	
	@Override
	public void msgLeavingEarly(BrianCustomer c){
		synchronized (waitingCustomers){
			for (WaitingCustomer wc: waitingCustomers){
				if (wc.customer == c){
					waitingCustomers.remove(wc);
					return;
				}
			}
		}
	}

//########### Scheduler ##############
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/*if !waitingCustomer.empty() there exists a Table t in tables such that t.occupiedBy == null 
		 * 
		 * 			then notifyWaiter(t, w);*/
				
		synchronized (waiters){
			if (!waitingCustomers.isEmpty()){
				synchronized(waitingCustomers){
					for (int i=0; i<waitingCustomers.size(); i++){
						if (waitingCustomers.get(i).customerNumber < 0){
							ChangeCustomerNumber(waitingCustomers.get(i), 0);
						}
					}
				}
				
				if (waiters.size() > 0){
					for (BrianTable t : tables){
						if (t.occupiedBy == null){
							synchronized (waiters){
								MyWaiter w = findWaiterWithLowestCust();
								notifyWaiter(t, w);
							}
							return true;
						}
					}
					//All tables are full
					synchronized (waitingCustomers){
						for (WaitingCustomer wc : waitingCustomers){
							if (wc.state == WaitingCustomerState.none)
							notifyCustomerFullHouse(wc);
						}
					}
				}
			}
			
			//Waiter break code/
			synchronized (waiters){
				for(MyWaiter w: waiters){
					if (w.state == MyWaiterState.wantBreak && workingWaiters> 1){
						workingWaiters--;
						w.state = MyWaiterState.allowedBreak;
						WaiterOnBreak(w);
						return true;
					}
				}
			}
			
			synchronized(waiters){
				for (MyWaiter w: waiters){
					if (w.state == MyWaiterState.wantBreak){
						return true;
					}
				}
			}
		}
		
		if (wantToGoHome){
			leaveRestaurant();
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

// ######################   Actions  ##################
	private void notifyWaiter(BrianTable t, MyWaiter w){
		 System.out.println("BrianHost is notifying BrianWaiter");
		   w.numberOfCustomers++;
		   WaitingCustomer c = waitingCustomers.remove(0);
		   w.waiter.msgSeatAtTable(c.customer, t);
		   customersInRestaurant ++;
		}

	private void WaiterOnBreak(MyWaiter w){
		//Do("Allowed "+ w.waiter.name + " to go on break.");
		w.state = MyWaiterState.onBreak;
		w.waiter.msgCanGoOnBreak();
	}
	
	private void notifyCustomerFullHouse(WaitingCustomer c){
		Do("Notifying customer Restaurant full.");
		c.state = WaitingCustomerState.full;
		c.customer.msgFullHouse();
		
	}
	
	private void leaveRestaurant(){
		Do("Leaving Restaurant");
		if (waitingCustomers.size() == 0){
			//msg all waiters that they are allowed to leave
			for (MyWaiter w: waiters){
				w.waiter.msgLeaveRestaurant();
			}
			B_BrianRestaurant br = (B_BrianRestaurant)myPerson.getBuilding();
			br.getCashier().msgLeaveRestaurant();
			br.cookRole.msgLeaveRestaurant();
			br.hostFilled = false;
			br.cookFilled = false;
			br.numberOfWaiters = 0;
			wantToGoHome = false;
			myPerson.msgGoHome();
			exitBuilding(myPerson);
		}
	}

	//utilities
	public MyWaiter findWaiterWithLowestCust(){
		
		int index = 0;
		int lowest = waiters.get(0).numberOfCustomers;
		//First find the first waiter who is not on break as initial waiter to find lowest customers assigned to waiters.
		//This is to prevent if waiter #1 decides to go on break but he has the lowest customer assigned.
		synchronized(waiters){
			for (int i=0; i<waiters.size(); i++){
				if (waiters.get(i).state != MyWaiterState.allowedBreak){
					index = i;
					lowest = waiters.get(i).numberOfCustomers;
					break;
				}
			}
		}
		synchronized(waiters){
			for (int i=0; i<waiters.size(); i++){
				//If the waiter has not been approved to take a break and the waiter has the lowest number of customers.
				if (waiters.get(i).state != MyWaiterState.allowedBreak && waiters.get(i).numberOfCustomers < lowest){
					lowest = waiters.get(i).numberOfCustomers;
					index = i;
				}
			}
		}
		return waiters.get(index);
		
	}
	
	public void ChangeCustomerNumber(WaitingCustomer c, int number){
		synchronized (waitingCustomers){
			for (int i=0; i<waitingCustomers.size(); i++){
				if (waitingCustomers.get(i).customerNumber == number){
					number++;
					i = 0;
				}
			}
			c.changeCustNumber(number);
		}
	}
	
	public void addWaiter(BrianWaiterRole w){
		waiters.add(new MyWaiter(w));
		workingWaiters++;
		stateChanged();
	}
	
	public Collection<BrianTable> getTables(){
		return tables;
	}
	
	private class WaitingCustomer{
		WaitingCustomerState state = WaitingCustomerState.none;
		BrianCustomer customer;
		int customerNumber = -1;
		
		public WaitingCustomer(BrianCustomer c){
			customer = c;
		}
		
		public void changeCustNumber(int num){
			customerNumber = num;
			customer.getGui().setCustNumber(num);
			customer.getGui().DoGoToWaitingArea();
		}
	
	};
	
	
	
	private class MyWaiter {
		MyWaiterState state = MyWaiterState.none;
		BrianWaiter waiter;
		int numberOfCustomers;
		
		public MyWaiter(BrianWaiter w){
			waiter = w;
			numberOfCustomers = 0;
		}
	}
	

	@Override
	protected void enterBuilding() {
		
	}

	@Override
	public void workOver() {
		//Do not accept any new people.
		B_BrianRestaurant rest = (B_BrianRestaurant)myPerson.getBuilding();
		rest.setOpen(false);
		wantToGoHome = true;
		
	}
}


