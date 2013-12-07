package SimCity.Buildings;
import javax.swing.JPanel;

import jesseRest.*;
import SimCity.Base.Building;
import SimCity.Base.Person;
import SimCity.Base.Role;
/***
 * 
 * @author Eric
 *
 */
public class B_JesseRestaurant extends Building{
	
	public JesseHost host = new JesseHost("JHost");
	public JesseCashier cashier = new JesseCashier("JCashier");
	public JesseCook cook = new JesseCook("JCook");
	int numWaiter = 0;
	
	public boolean hostFilled = false, cashierFilled = false, cookFilled = false;

	
	public B_JesseRestaurant(int id, JPanel jp) {
		super(id, jp);
		// TODO Auto-generated constructor stub
	}
	
	public B_JesseRestaurant(int id, JPanel jp, int xCoord, int yCoord){
		this.id = id;
		buildingPanel = jp;
		x = xCoord;
		y = yCoord;
		tag = "B_JesseRestaurant";
	}
	
	@Override
	public void EnterBuilding(Person person, String role) {
		Role newRole = null;
		try {
			if(role.equals("jesseRestaurant.JesseHost")) { 
				newRole = host;
				hostFilled = true;
				setOpen(areAllNeededRolesFilled());
				}
			else if(role.equals("jesseRestaurant.JesseWaiter")) {
				newRole = new JesseWaiter("JWaiter");
				numWaiter++;
				setOpen(areAllNeededRolesFilled());
			}
			else if(role.equals("jesseRestaurant.JesseCook")) { 
				newRole = cook;
				cookFilled = true;
				setOpen(areAllNeededRolesFilled());
			}
			else if(role.equals("jesseRestaurant.JesseCashier")) {
				newRole = cashier;
				cashierFilled = true;
				setOpen(areAllNeededRolesFilled());
			}
			else if(role.equals("jesseRestaurant.JesseCustomer")) {
				newRole = new JesseCustomer("JCustomer");
				System.out.println("JCustomer Made");
			}
			newRole.setActive(true);
			newRole.setPerson(person);
			person.msgCreateRole(newRole, true);
			fillNeededRoles(person, newRole);
			person.msgEnterBuilding(this);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println ("Building: no class found");
			System.out.println("Checking is newRole actually exists : "+newRole);
		}
	}
	


	@Override
	public boolean areAllNeededRolesFilled() {
		System.out.println("Jesse Restaurant Roles Filled? Host: "+hostFilled+"  Cook: "+cookFilled+"  Cashier: "+cashierFilled+"  Waiters: "+numWaiter);
		return hostFilled && cashierFilled && cookFilled && numWaiter > 0;
	}

	@Override
	public String getManagerString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCustomerString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void fillNeededRoles(Person p, Role r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ExitBuilding(Person person) {
		// TODO Auto-generated method stub
		
	}

}