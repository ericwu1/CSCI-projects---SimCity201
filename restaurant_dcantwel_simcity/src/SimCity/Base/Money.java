/**
 * 
 */
package SimCity.Base;

/**
 * @author Daniel
 * 
 */
public class Money {

	public int dollars;
	public int cents;

	Money(int dollars, int cents) {
		this.dollars = dollars;
		this.cents = cents;
	}

	/**
	 * Check if dollars and cents are zero
	 * @return true if zero
	 */
	public boolean isZero() {
		if (dollars == 0 && cents == 0)
			return true;
		else
			return false;
	}

	/**
	 * Check to see if current money is greater than m
	 * @param m - money to compare against
	 * @return true if current money is greater than m
	 */
	public boolean isGreaterThan(Money m) {
		if (dollars > m.dollars) {
			return true;
		}
		else if (dollars == m.dollars) {
			if (cents >= m.cents) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add m to current money
	 * @param m - money to add
	 */
	public void add(Money m) {
		dollars += m.dollars;
		cents += m.cents;
	}
	
	/**
	 * Subtract m from current money
	 * @param m - money to subtract
	 */
	public void subtract(Money m) {
		if (this.isGreaterThan(m)) {
			dollars -= m.dollars;
			if (cents >= m.cents) {
				cents -= m.cents;
			}
			else {
				dollars -= 1;
				cents = 100 - (m.cents - cents);
			}
		}
		else {
			System.err.println("Invalid money transaction. Cannot subtract that much money.");
		}
	}
}
