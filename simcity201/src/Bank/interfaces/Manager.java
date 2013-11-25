package Bank.interfaces;

import Bank.bankCustomerRole;
import Bank.bankManagerRole;
import Bank.bankManagerRole.Teller;

public interface Manager {

	public abstract Guard getGuard();

	public abstract void setGuard(Guard bg);

	public static final int NCounters = 2;

	//----------------------------------------------Messages-------------------------------------------------
	public abstract void newTeller(Bank.interfaces.Teller teller);

	public abstract void newClient(bankCustomerRole c);

	//----------------------------------------------Scheduler-------------------------------------------------
	public abstract boolean pickAndExecuteAnAction();

	public abstract void callTeller(bankCustomerRole c, Teller t);

	public abstract void workOver();

}