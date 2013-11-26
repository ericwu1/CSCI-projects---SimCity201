/**
 * 
 */
package housing.gui;

import housing.interfaces.Tenant;
import housing.roles.TenantRole;

import java.awt.Color;
import java.awt.Graphics2D;

import SimCity.gui.Gui;

/**
 * @author Daniel
 *
 */
public class TenantGui implements Gui {
	
	private Tenant tenant = null;
	
	private int xSize,	ySize;
	
	private int xPos,	yPos;
	private int xDest,	yDest;
	
	private final int xBed  	= 60;
	private final int yBed		= 110;
	
	private final int xDoor 	= 600;
	private final int yDoor 	= 315;
	
	private final int xFridge	= 130;
	private final int yFridge	= 380;
	
	private final int xStove	= 340;
	private final int yStove	= 580;
	
	private final int xTable	= 210;
	private final int yTable	= 190;
	
	private final int xMail		= 580;
	private final int yMail		= 55;
	
	private enum Dest { None, Bed, Table, Fridge, Stove, Mail, Door };
	private Dest destination = Dest.None;
	
	public TenantGui(Tenant tenant) {
		this.tenant = tenant;
	}

	@Override
	public void updatePosition() {
		if (xPos < xDest)
			xPos++;
		else if (xPos > xDest)
			xPos--;

		if (yPos < yDest)
			yPos++;
		else if (yPos > yDest)
			yPos--;
		
		if (xPos == xDest && yPos == yDest) {
			if (destination == Dest.Bed) {
				tenant.msgAtBed();
			}
			else if (destination == Dest.Table) {
				tenant.msgAtTable();
			}
			else if (destination == Dest.Bed) {
				tenant.msgAtBed();
			}
			else if (destination == Dest.Door) {
				tenant.msgAtDoor();
			}
			else if (destination == Dest.Fridge) {
				tenant.msgAtFridge();
			}
			else if (destination == Dest.Mail) {
				tenant.msgAtMail();
			}
			else if (destination == Dest.Stove) {
				tenant.msgAtStove();
			}
			destination = Dest.None;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillOval(xPos, yPos, xSize, ySize);
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restart() {
		// TODO Auto-generated method stub
		
	}

	// Go to the mailbox when tenant needs to pay (or not pay) rent
	public void DoGoToMailbox() {
		xDest = xMail;
		yDest = yMail;
		System.out.println("Going To Mailbox");
	}

	// Go to the door and exit
	public void DoLeaveHouse() {
		xDest = xDoor;
		yDest = yDoor;
		System.out.println("Leaving House");
	}

	// Go to the fridge
	public void DoGoToFridge() {
		xDest = xFridge;
		yDest = yFridge;
		System.out.println("Going To Fridge");
	}
	
	// Go to the stove
	public void DoGoToStove() {
		xDest = xStove;
		yDest = yStove;
		System.out.println("Going To Stove");
	}
	
	// Go to the table
	public void DoGoToTable() {
		xDest = xTable;
		yDest = yTable;
		System.out.println("Going To Table");
	}

	// Go to the bed and sleep
	public void DoGoToBed() {
		xDest = xBed;
		yDest = yBed;
		System.out.println("Going To Bed");
	}

}
