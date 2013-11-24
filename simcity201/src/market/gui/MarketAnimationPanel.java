package market.gui;

import javax.swing.*;

import market.MarketClerkRole;
import market.MarketDeliveryPersonRole;
import market.MarketManagerRole;
import market.MarketPackerRole;
import SimCity.gui.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class MarketAnimationPanel extends JPanel implements ActionListener {

	/**
	 * 
	 * @author Timothy So
	 * The inner view of a market building for SimCity201, Team 33
	 *
	 */
	private static final long serialVersionUID = 6251178680876605372L;
	
	private String name;
	
	private final int WINDOWX = 680;
	private final int WINDOWY = 680;
	
	private final int xPersonSize	= 20;
	private final int yPersonSize	= 20;

	static final int SPEED = 5;

	private Image bufferImage;
	private Dimension bufferSize;

	private MarketManagerRole managerRole = new MarketManagerRole("Manny");
	private MarketClerkRole clerkRole = new MarketClerkRole("Clark");
    private MarketPackerRole packerRole = new MarketPackerRole("Parker");
    private MarketDeliveryPersonRole deliveryPersonRole = new MarketDeliveryPersonRole("Pearson");

	//public List<MarketPackerGui> packers = Collections.synchronizedList(new ArrayList<MarketPackerGui>());
	//public List<MarketClerkGui> clerks = Collections.synchronizedList(new ArrayList<MarketClerkGui>());
	//public List<MarketDeliveryPersonGui> deliveryPeople = Collections.synchronizedList(new ArrayList<MarketDeliveryPersonGui>());

	private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

	public MarketAnimationPanel(String name) {
		setSize(WINDOWX, WINDOWY);
		setVisible(true);
		
		this.name = name;

		addGui(managerRole.getGui());
        addGui(clerkRole.getGui());
        addGui(packerRole.getGui());
        addGui(deliveryPersonRole.getGui());
		
		bufferSize = this.getSize();
	}

	public void actionPerformed(ActionEvent e) {
		repaint(); // Will have paintComponent called
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// Clear the screen by painting a rectangle the size of the frame
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, WINDOWX, WINDOWY);

		g2.setColor(Color.YELLOW);
		// counter
		g2.fillRect(0, 150, 190, 10);
        g2.fillRect(180, 160, 10, 200);
        g2.fillRect(180, 360, 500, 10);
        
        // shelves
        g2.fillRect(200, 40, 160, 20);
        g2.fillRect(400, 40, 160, 20);
        g2.fillRect(400, 100, 160, 20);
		
		for (Gui gui : guis) {
			if (gui.isPresent()) {
				gui.updatePosition();
			}
		}

		for (Gui gui : guis) {
			if (gui.isPresent()) {
				gui.draw(g2);
			}
		}
		
		// draw building name
		g2.setColor(Color.BLACK);
		g2.drawString(name, 20, 20);
	}

	public void addGui(MarketPackerGui gui) {
		guis.add(gui);
	}
	
	public void addGui(MarketClerkGui gui) {
		guis.add(gui);
	}
	
	public void addGui(MarketDeliveryPersonGui gui) {
		guis.add(gui);
	}
	/*
    public void addGui(MarketCustomerGui gui) {
        guis.add(gui);
    }*/
    
    public void addGui(MarketManagerGui gui) {
        guis.add(gui);
    }
}
