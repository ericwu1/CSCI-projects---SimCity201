package market.gui;

import javax.swing.*;

import exterior.astar.AStarTraversal;
import exterior.gui.PersonGui;
import exterior.gui.SimCityGui;
import market.MarketClerkRole;
import market.MarketDeliveryPersonRole;
import market.MarketManagerRole;
import market.MarketPackerRole;
import SimCity.Base.God;
import SimCity.Base.Person;
import SimCity.Base.Person.Morality;
import SimCity.Base.Person.Vehicle;
import SimCity.Buildings.B_House;
import SimCity.Buildings.B_DannyRestaurant;
import SimCity.Buildings.B_Market;
import SimCity.Globals.Money;
import SimCity.gui.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class MarketAnimationPanel extends JPanel implements ActionListener {

	/**
	 * 
	 * @author Timothy So
	 * The inner view of a market building for SimCity201, Team 33
	 *
	 */
	private static final long serialVersionUID = 6251178680876605372L;
	
	private SimCityGui cityGui;
	
	private String name;
	
	private final int WINDOWX = 640;
	private final int WINDOWY = 640;
	
	private final int xPersonSize	= 20;
	private final int yPersonSize	= 20;

	static final int SPEED = 5;

    private static final int TILESIZE = 64;

	private Image bufferImage;
	private Dimension bufferSize;

	private Person manager;
    private Person clerk;
    private Person packer;
    private Person deliveryPerson;
    
    private B_DannyRestaurant chanos;
    private B_Market market;
    
    private List<Person> people = Collections.synchronizedList(new ArrayList<Person>());
    
    private Semaphore[][] pedestrianGrid = new Semaphore[1920/(TILESIZE)][1920/(TILESIZE)];

	//public List<MarketPackerGui> packers = Collections.synchronizedList(new ArrayList<MarketPackerGui>());
	//public List<MarketClerkGui> clerks = Collections.synchronizedList(new ArrayList<MarketClerkGui>());
	//public List<MarketDeliveryPersonGui> deliveryPeople = Collections.synchronizedList(new ArrayList<MarketDeliveryPersonGui>());

	private List<MyGui> guis = Collections.synchronizedList(new ArrayList<MyGui>());
	public MarketAnimationPanel(String name)
	{
		setSize(WINDOWX, WINDOWY);
		setVisible(true);
        Timer timer = new Timer(10, this);
        timer.start();
		
		this.name = name;
	}
	
	public void setBMarket(B_Market mar){
		market = mar;
	}
	public B_Market getBMarket(){return market;}
	
	public MarketAnimationPanel(String name, SimCityGui gui)
	{
        setSize(WINDOWX, WINDOWY);
        setVisible(true);
        Timer timer = new Timer(10, this);
        timer.start();
        
        this.name = name;
        
        AStarTraversal aStarTraversal = new AStarTraversal(pedestrianGrid);
        
        PersonGui mPersonGui = new PersonGui(gui, aStarTraversal);
        manager = new Person("Manny", mPersonGui, "market.MarketManagerRole", Vehicle.walk, Morality.good, new Money(100, 0), new Money(10, 0), 20, 3, "Apartment", (B_House)gui.buildingList.get(0), market);
        manager.mainRole.setActive(true);
        mPersonGui.setPerson(manager);
        Gui mGui = new MarketManagerGui((MarketManagerRole) manager.mainRole);
        addGui(mGui);
        //manager.msgCreateRole(managerRole);
        manager.startThread();

        PersonGui cPersonGui = new PersonGui(gui, aStarTraversal);
        clerk = new Person("Clark", cPersonGui, "market.MarketClerkRole", Vehicle.walk, Morality.good, new Money(100, 0), new Money(10, 0), 20, 3, "Apartment", (B_House)gui.buildingList.get(0), market);
        clerk.mainRole.setActive(true);
        Gui cGui = new MarketClerkGui((MarketClerkRole) clerk.mainRole);
        cPersonGui.setPerson(clerk);
        addGui(cGui);
        ((MarketClerkRole) clerk.mainRole).setManager((MarketManagerRole) manager.mainRole);
        clerk.startThread();

        PersonGui pPersonGui = new PersonGui(gui, aStarTraversal);
        packer = new Person("Parker", pPersonGui, "market.MarketPackerRole", Vehicle.walk, Morality.good, new Money(100, 0), new Money(10, 0), 20, 3, "Apartment", (B_House)gui.buildingList.get(0), market);
        packer.mainRole.setActive(true);
        Gui pGui = new MarketPackerGui((MarketPackerRole) packer.mainRole);
        pPersonGui.setPerson(packer);
        addGui(pGui);
        ((MarketPackerRole) packer.mainRole).setManager((MarketManagerRole) manager.mainRole);
        packer.startThread();

        PersonGui dPersonGui = new PersonGui(gui, aStarTraversal);
        deliveryPerson = new Person("Parson", dPersonGui, "market.MarketDeliveryPersonRole", Vehicle.walk, Morality.good, new Money(100, 0), new Money(10, 0), 20, 3, "Apartment", (B_House)gui.buildingList.get(0), market);
        deliveryPerson.mainRole.setActive(true);
        Gui dGui = new MarketDeliveryPersonGui((MarketDeliveryPersonRole) deliveryPerson.mainRole);
        dPersonGui.setPerson(deliveryPerson);
        addGui(dGui);
        ((MarketDeliveryPersonRole) deliveryPerson.mainRole).setManager((MarketManagerRole) manager.mainRole);
        deliveryPerson.startThread();

        initializeLocations();
        
        ((MarketManagerRole) manager.mainRole).addClerk((MarketClerkRole) clerk.mainRole);
        ((MarketManagerRole) manager.mainRole).addPacker((MarketPackerRole) packer.mainRole);
        ((MarketManagerRole) manager.mainRole).addDeliveryPerson((MarketDeliveryPersonRole) deliveryPerson.mainRole);
    
        /*chanos = (B_Restaurant) God.Get().findRandomRestaurant();
        ((MarketManagerRole) manager.mainRole).msgWantFood(chanos.getID(), "Steak", 10);
        ((MarketManagerRole) manager.mainRole).msgWantFood(chanos.getID(), "Salad", 10);
        ((MarketManagerRole) manager.mainRole).msgWantFood(chanos.getID(), "Tomatoes", 10);
        */bufferSize = this.getSize();
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
        g2.fillRect(180, 360, 460, 10);
        
        // shelves
        g2.fillRect(200, 40, 160, 20);
        g2.fillRect(400, 40, 160, 20);
        g2.fillRect(400, 100, 160, 20);
		
		for (MyGui myGui : guis) {
			if (myGui.gui.isPresent()) {
			    myGui.gui.updatePosition();
			}
		}

		for (MyGui myGui : guis) {
			if (myGui.gui.isPresent()) {
			    myGui.gui.draw(g2);
			}
		}
		
		// draw building name
		g2.setColor(Color.BLACK);
		g2.drawString(name, 20, 20);
	}

	public void addGui(MarketPackerGui gui) {
		guis.add(new MyGui(gui));
        initializeLocations();
	}
	
	public void addGui(MarketClerkGui gui) {
		guis.add(new MyGui(gui));
	}
	
	public void addGui(MarketDeliveryPersonGui gui) {
		guis.add(new MyGui(gui));
	}
	
    public void addGui(MarketCustomerGui gui) {
        guis.add(new MyGui(gui));
    }
    
    public void addGui(MarketManagerGui gui) {
        guis.add(new MyGui(gui));
        initializeLocations();
    }

    public void addGui(Gui gui) {
        guis.add(new MyGui(gui));
    }
    
    public void initializeLocations()
    {
        Map<Integer, Point> locations = Collections.synchronizedMap(new HashMap<Integer, Point>());
        int locCount = 0;
        for (int i = 200; i < 360; i+=20)
        {
            locations.put(locCount, new Point(i, 40));
            locCount++;
        }
        for (int i = 200; i < 360; i+=20)
        {
            locations.put(locCount, new Point(i, 50));
            locCount++;
        }
        for (int i = 400; i < 560; i+=20)
        {
            locations.put(locCount, new Point(i, 40));
            locCount++;
        }
        for (int i = 400; i < 560; i+=20)
        {
            locations.put(locCount, new Point(i, 50));
            locCount++;
        }
        for (int i = 400; i < 560; i+=20)
        {
            locations.put(locCount, new Point(i, 100));
            locCount++;
        }
        for (int i = 400; i < 560; i+=20)
        {
            locations.put(locCount, new Point(i, 110));
            locCount++;
        }

        for (MyGui myGui : guis)
        {
            if (!myGui.initialized)
            {
                if (myGui.gui instanceof MarketManagerGui)
                {
                    ((MarketManagerGui) myGui.gui).setLocations(locations);
                    ((MarketManagerRole) manager.mainRole).initializeInventory((MarketManagerGui) myGui.gui);
                }
                else if (myGui.gui instanceof MarketPackerGui)
                {
                    ((MarketPackerGui) myGui.gui).setLocations(locations);
                }
            }
        }
    }
    
    /**
     * Inner Classes
     */
    
    private class MyGui
    {
        public Gui gui;
        public boolean initialized;
        
        public MyGui(Gui gui)
        {
            this.gui = gui;
            initialized = false;
        }
    }
    
    /**
     * Debug
     */
    
    public void debugTestRestaurant()
    {
        chanos = (B_DannyRestaurant) God.Get().findRandomRestaurant();
    	((MarketManagerRole) manager.mainRole).msgWantFood(chanos.getID(), "Steak", 10);
    	((MarketManagerRole) manager.mainRole).msgWantFood(chanos.getID(), "Salad", 10);
    	((MarketManagerRole) manager.mainRole).msgWantFood(chanos.getID(), "Tomatoes", 10);
  	}
}
