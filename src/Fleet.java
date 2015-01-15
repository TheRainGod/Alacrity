import java.util.*;
//import java.io.*;
//import java.awt.*;
//import java.awt.Graphics2D.*;
//import java.awt.geom.*;



public class Fleet {

	public ArrayList<Ship>fleet = new ArrayList<Ship>();

	public int fcX, fcY, eX, eY;
	public int temp_variable;
	public int fleetSize = 0;
	
	public String fleetName;

	public boolean selected = false;
	public boolean move = false;
	public boolean redacted = false;
	
	public Fleet()
	{
		selected = false;
		fleet = new ArrayList<Ship>();
		//fcX = 0;
		//fcY =0;
		//eX=0;
		//eY=0;
	}
	
	public Fleet(ArrayList<Ship>fleet1, int fcX1, int fcY1)
    {	
    	fleet = new ArrayList<Ship>(fleet1.size());
    	fleet = fleet1;
    	fcX = fcX1;
    	fcY = fcY1;
    	//eX = eX1;
    	//eY = eY1;
    	
    	fleetName = "Natural Fleet";
    	
    	fleetSize = fleet.size();
    }

    public Fleet(ArrayList<Ship>fleet1, int fcX1, int fcY1, int eX1, int eY1)
    {	
    	fleet = new ArrayList<Ship>(fleet1.size());
    	fleet = fleet1;
    	fcX = fcX1;
    	fcY = fcY1;
    	eX = eX1;
    	eY = eY1;
    	
    	fleetName = "Natural Fleet";
    	
    	fleetSize = fleet.size();
    }

    public int size()
    {

    	return fleet.size();
    }

    public Ship get(int i)
    {

    	return fleet.get(i);
    }
    
    public void remove(int i)
    {
    	fleet.remove(i);
    }
    
    public void remove(Ship ships)
    {
    	fleet.remove(ships);
    }
    
    public void add(Ship i)
    {
    	fleet.add(i);
    	fleetSize++;
    }
    
    public void add(Fleet tf)
    {
    	for(int u = 0; u<tf.size(); u++)
    	{
    		fleet.add(tf.get(u));
    	}
    }
    
    public void clear()
    {
    	fleet.clear();
    	//eX = 0;
    	//eY = 0;
    	//fcX = 0;
    	//fcY = 0;
    	move = false;
    	selected = false;
    }
    
    public String toString()
    {
    	return "Fleet Name: " + fleetName + " :: " + fleet;
    		
    }

}