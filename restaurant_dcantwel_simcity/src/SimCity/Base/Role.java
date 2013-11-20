/**
 * 
 */
package SimCity.Base;


/**
 * @author Brian Chen
 *
 */
public abstract class Role {
	protected boolean active;
	protected Person myPerson;
	
	
	protected abstract boolean pickAndExecuteAnAction();
	
	public abstract void workOver();
	
	protected void stateChanged(){
		myPerson.stateChanged();
	}
}

