import java.util.*;
//import java.io.*;
import java.awt.*;
//import java.awt.Graphics2D.*;
//import java.awt.geom.*;


public class Ship {

	//ints
	public int x, y, ox, oy, w, h;
	public int sightRange;
	public int fireRange = 75;
	public double dx, dy;
	public int dz = 3;
	public int currentHP = 100;
	public int totalHP = 100;
	public int shotTimer  =0;
	public int firePower;
	public int reloadTime;
	
	//auri Variables
	public double auriAngle = 0;
	public boolean auriCoag = false;
	public boolean auriSpotted = false;

	//shipCoordinates
	public int[] x1;
	public int[] y1;
	public int[] temp_x;
	public int[] temp_y;
	public double[] x2;
	public double[] y2;
	public int xx2[];
	public int yy2[];
	public int[] xcoord = new int[4];
   	public int[] ycoord = new int[4];
	public ArrayList<Integer>x3;
	public ArrayList<Integer>y3;
	public ArrayList<Integer>x4;
	public ArrayList<Integer>y4;
	public Point p1,p2,p3,p0;
	public double shipAngle;
	public int s;

	//polygons
	public Polygon shipShape;

	//strings
	public ShipType shipType;
	public String shipName;

	//booleans
	public boolean fleetCommand;
	public boolean isSpotted = false;
	public boolean colorBlue;
	public boolean hasTarget, hasShot;
	public boolean notBot = false;
	
	//ships
	public Ship shipTarget;
	public Ship spottedShip;
	
	/* ship types
	 *
	 *Frigate: 1<w<5 :: 1<h<10
	 *Destroyer 5<w<8 :: 5<h<15
	 *Cruiser 5<w<10 :: 10<h<20
	 *BattleCruiser 5<w<10 :: 15<h<30
	 *BattleShip 5<w<10 :: 20<h<40
	 *Dreadnought w=10 :: 30<h<60
	 *
	 */
	
	public Ship()
	{
		notBot = false;
	}
	
	public Ship(int centerX, int centerY) //fleet center, not drawn
	{
		x = centerX;
		y = centerY;

		shipShape = new Polygon();
		shipType = ShipType.FRIGATE;
		shipName = "FC1";
		colorBlue = false;
		fleetCommand = true;
	}

    public Ship(int a, int b,  boolean yes)
    {
    	x = a;
    	y = b;
    	w = 5;
    	h = 15;
    	colorBlue = yes;
    }

	public Ship(int a, int b, int c, int d, ShipType t, String name1, boolean yes)   //width and then height BOTH NEED TO BE DIVISIBLE BY 2
    {
    	x = a;
    	y = b;
    	w = c;
    	h = d;

    	ox = x;
    	oy = y+(h/2);

    	//fill arrays with ships 4 points
    	//int[] x1 = new int[4];
    	//int[] y1 = new int[4];
    	int[] x1 = {x, x+(w/2), x, x-(w/2)};
    	int[] y1 = {y-(h/2), y, y+(h/2), y};

    	int[] xx2 = {0+ox, w/2+ox, 0+ox, 0-(w/2)+ox};
    	int[] yy2 = {0-(h/2)+oy, 0+oy, h/2+oy, 0+oy};

        p0 = new Point(x1[0],y1[0]);
        p1 = new Point(x1[1],y1[1]);
        p2 = new Point(x1[2],y1[2]);
        p3 = new Point(x1[3],y1[3]);

		x3 = new ArrayList<Integer>();
		y3 = new ArrayList<Integer>();
		x3.add(0,0);
		x3.add(1,(w/2));
		x3.add(2,0);
		x3.add(3,(0-(w/2)));
		y3.add(0,0-(h/2));
		y3.add(1,0);
		y3.add(2,h/2);
		y3.add(3,0);

		x4 = new ArrayList<Integer>(); x4.add(0); x4.add(0); x4.add(0);x4.add(0);
		//Collections.copy(x4, x3);
		y4 = new ArrayList<Integer>(4); y4.add(0); y4.add(0); y4.add(0);y4.add(0);
		//Collections.copy(y4, y3);

    	///double[] x2 = {x, x+(w/2), x, x-(w/2)};
    	//double[] y2 = {y, y+(h/2), y+h, y+(h/2)};
    	s = 4;

    	shipShape = new Polygon(xx2,yy2,s);
		
		sightRange = 150;
	
    	shipType = t;
    	shipName = name1;
    	colorBlue = yes;
    	
    	notBot = true;
    	
    	findFireRange(shipType);
    }
	
	public int getWidth()
	{
		int tp = (int)((currentHP+0.0)/totalHP*30);
		return tp;
	}
    
    public void findFireRange(ShipType shipType)
    {
    	switch(shipType)
    	{
    	
    	case BATTLESHIP:
    		fireRange = 105;
    		currentHP = 250;
    		totalHP = 250;
    		firePower = 35;
    		reloadTime = 25;
    	break;
    	
    	case FRIGATE:
    		fireRange = 75;
    		currentHP = 100;
    		totalHP = 100;
    		firePower = 5;
    		reloadTime = 10;
    	break;
    	
    	case CRUISER:
    		fireRange = 95;
    		currentHP = 200;
    		totalHP = 150;
    		firePower = 20;
    		reloadTime = 15;
    	break;
    	
    	default:
    		fireRange = 75;
    		currentHP = 100;
    		totalHP = 100;
    		firePower = 5;
    		reloadTime = 10;
    	break;	
    	
    	}
    	
    	
    }

//important method
    public String toString()
    {
    	return "[Ship Type: " + shipType + " :: Ship Name: " + shipName + "]";
    }
    
    public int[] addCenterX(int[] t)
    {
    	int[] cage = new int[10];

    	for(int i=0; i<4;i++)
    	{
    		cage[i] = t[i] + ox;

    	}

    	return cage;

    }

    public int[] addCenterY(int[] t)
    {
    	int[] cage = new int[10];

    	for(int i=0; i<4;i++)
    	{
    		cage[i] = t[i] + oy;

    	}

    	return cage;

    }

    public int getX1(int i)
    {
    	System.out.println(x1[i]);
    	return x1[i];
    }

    public int getY1(int i)
    {

    	return y1[i];
    }

    public Polygon getShipShape()
    {

    	return shipShape;
    }
    
    public boolean inRange(Ship beta)
    {
    	if(Math.pow((Math.pow(ox-beta.ox,2) + Math.pow(oy-beta.oy,2)),.5)<=sightRange)
    		return true;
    	return false;
    }
    
    public int numInRange(Ship beta)
    {
    	int blag  = (int) Math.pow((Math.pow(ox-beta.ox,2) + Math.pow(oy-beta.oy,2)),.5);
    	return blag;
    }

    public void makeCoords(int eX, int eY, int fcX, int fcY) //rotates
   	{
    	 int  i;
    	 int delta_x = eX-ox;
    	 int delta_y = oy-eY;
    	 double angle = Math.atan2(delta_y, delta_x);

    	 if(shipAngle==angle)
    	 {

    	 }
    	 else
    	 {
    	 	shipAngle = angle;
 		 	//angle = Math.toRadians(angle);
 		 		//double temp_x = x;
 		 		//double temp_y = y;
 		 		double temp_x2;
 		 		double temp_y2;

    		 for (i=0; i<4; i++)
   			 {
   			 	temp_x2 =(((x3.get(i)+0.0)*Math.sin(angle)-(y3.get(i)+0.0)*Math.cos(angle)));
   			 	temp_y2 =(((x3.get(i)+0.0)*Math.cos(angle)+(y3.get(i)+0.0)*Math.sin(angle)));
       			 xcoord[i] = (int)temp_x2 + ox;
       			 ycoord[i] = (int)temp_y2 + oy;
       			 //x3.add(i,(int)(x+(x3.get(i)*Math.sin(angle)-y3.get(i)*Math.cos(angle))));
       			 //y3.add(i,(int)(y+(x3.get(i)*Math.cos(angle)+y3.get(i)*Math.sin(angle))));
    		 }
    		 
    	shipShape = new Polygon(xcoord, ycoord, s);
    	 }
 	 }

 	public void move(int eX1, int eY1, int fcX1, int fcY1) //moves 
 	{
 		double slope = (eY1+0.0-fcY1)/(eX1+0.0-fcX1);
 		//double slopeDub = (eY1+0.0-fcY1)/(eX1+0.0-fcX1);
 		boolean trick = false;
 		//System.out.println("Y COORD: " + (eY1+0.0-fcY1));
 		//System.out.println("X COORD: " + (eX1+0.0-fcX1));
 		temp_x = new int[4];
 		temp_y = new int[4];
 		dx=1.0;
 		dy=1.0;

 		if(slope>=4 || slope<=-4)
 		{
 			slope=0;
 			trick = true;
 		}
 		else if(slope>=2)
 		{
 			slope = 1;
 		}
 		else if(slope<=-2)
 		{
 			slope = -1;
 		}


	/*	if(slope>=10)
		 	slope = slope/4;

		if(slope<=-10)
		 	slope = 0-slope/4;

	 	if(slope>=4)
			slope = slope/2;

 		if(slope<=-4)
 			slope = 0-slope/2;

 		if(slope>=6)
			slope = slope/3;

		if(slope<=-6)
			slope = 0-slope/3; */



 		//System.out.println("NewSlope " + slope);
 		//System.out.println();
 		for(int i = 0; i<4; i++)
 		{
 			if(slope == 0 && trick)
 			{
 				//System.out.println("SlopeDub " + slopeDub)
 				if(eY1+0.0-fcY1>0)
 				{
 					temp_y[i] =(int) (y3.get(i) + oy + dy);
 					oy = (int)(oy+dy);
 					//System.out.println("+dy");
 				}
 				else
 				{
 					temp_y[i] =(int) (y3.get(i) + oy - dy);
 					oy= (int)(oy-dy);
 					//System.out.println("-dy");
 				}
 			}
 			else
 			{
	 				if(eX1-fcX1 == 0)
	 				{
	 					if(eY1-fcY1>0)
	 						temp_y[i] =(int) (y3.get(i)+ oy + dy);
	 					else
	 						temp_y[i] =(int) (y3.get(i) + oy - dy);
	 				}

	 				if(eY1-fcY1==0)
	 				{
	 					if(eX1-fcX1>0)
	 						temp_x[i] = (int) (x3.get(i) + ox + dx);
	 					else
	 						temp_x[i] = (int) (x3.get(i) + ox - dx);
	 				}

	 				else
	 				{
	 					if(eX1-fcX1>0)
		 					temp_x[i] = (int) (x3.get(i)+ox+dx);

		 				else
		 					temp_x[i] = (int) (x3.get(i)+ox-dx);

		 				temp_y[i] = (int) (y3.get(i)+oy + ((slope * (temp_x[i]+0.0 - ox))) );
	 				}

 					if(eX1-fcX1==0)
 					{
 						if(eY1-fcY1>0)
 							oy = (int) (oy + dy);
 						else
 							oy = (int) (oy - dy);
 					}
 					else
 					{

 						if(eX1-fcX1>0)
 						{
 							ox=(int) (ox + dx);
 							oy = (int)Math.round(oy + ((slope) * (dx+0.0)));
 						}
 						else
 						{
 							ox = (int) (ox - dx);
 							oy = (int)Math.round(oy + ((slope) * (0.0-dx)));
 						}
 					}
 			}
 		}
 		trick = false;

 		shipShape = new Polygon(temp_x, temp_y,s);

 	}

 	public void move2(int eX1, int eY1, int fcX1, int fcY1)
 	{	
 		
 		int delta_x = fcX1 - eX1;
 		int delta_y = fcY1 - eY1;
 		double angle = Math.atan2(delta_y, delta_x);
 		
 		shipAngle = angle;
 		temp_x = new int[4];
 		temp_y = new int[4];
 		
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = (int) ((x3.get(i) + ox) + (Math.cos((shipAngle) * dz)));
 			temp_y[i] = (int) ((y3.get(i)+oy) + (Math.sin((shipAngle) * dz)));
 			ox = (int) (ox + (Math.cos((shipAngle) * dz)));
 			oy = (int) (oy + (Math.sin((shipAngle) * dz)));
 		}
 		
 		shipShape = new Polygon(temp_x, temp_y,s);
 	}
 
 //auri's methods	
 	public void auriRotate() //rotates auri's Ships aroun their auriangle
 	{
    	 	double angle = Math.toRadians(auriAngle);
 		 	//angle = Math.toRadians(angle);
 		 		//double temp_x = x;
 		 		//double temp_y = y;
 		 		double temp_x2;
 		 		double temp_y2;

    		 for (int i=0; i<4; i++)
   			 {

   			 	temp_x2 =(((x3.get(i)+0.0)*Math.sin(angle)-(y3.get(i)+0.0)*Math.cos(angle)));
   			 	temp_y2 =(((x3.get(i)+0.0)*Math.cos(angle)+(y3.get(i)+0.0)*Math.sin(angle)));
       			 xcoord[i] = (int)temp_x2 + ox;
       			 ycoord[i] = (int)temp_y2 + oy;
       			 //x3.add(i,(int)(x+(x3.get(i)*Math.sin(angle)-y3.get(i)*Math.cos(angle))));
       			 //y3.add(i,(int)(y+(x3.get(i)*Math.cos(angle)+y3.get(i)*Math.sin(angle))));
    		 }

    	shipShape = new Polygon(xcoord, ycoord, s);
 	}
 	
 	public void auriFrigateScoutMove() //moves auri's Frigates in a scouting formation
 	{
 		temp_x = new int[4];
 		temp_y = new int[4];
 		dx=1.0;
 		dy=1.0;
 		
 		double scaleX = Math.cos(Math.toRadians(auriAngle));
 		double scaleY = Math.sin(Math.toRadians(auriAngle));
 		
 		if(auriAngle>=180)
 		{
 			//dx = 0-1.0;
 			//dy = 0-1.0;
 		}
 			
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = (int) ((x3.get(i) + ox) + (dx * scaleX));
 			temp_y[i] = (int) ((y3.get(i)+oy) + (dy * scaleY));
 			ox = (int) (ox + (dx * scaleX));
 			oy = (int) (oy + (dy * scaleY));
 		}
 		shipShape = new Polygon(temp_x, temp_y,s);
 	}
 	
 	public void auriFrigateScoutMove2() //moves auri's Frigates in a scouting formation
 	{
 		temp_x = new int[4];
 		temp_y = new int[4];
 		dx=1.0;
 		dy=1.0;
 		
 		double scaleX = Math.cos(Math.toRadians(auriAngle));
 		double scaleY = Math.sin(Math.toRadians(auriAngle));
 		
 		if(auriAngle>=180)
 			dx = 0-1.0;
 		
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = (int) ((x3.get(i) + ox) + (dx * scaleX));
 			temp_y[i] = (int) ((y3.get(i)+oy) + (dy * scaleY / scaleX));
 			ox = (int) (ox + (dx));
 			oy = (int) (oy + (dy * scaleY / scaleX));
 		}
 		shipShape = new Polygon(temp_x, temp_y,s);
 	}
 	
 	public void auriFrigateScout()
 	{	
 		temp_x = new int[4];
 		temp_y = new int[4];
 		
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = (int) ((x3.get(i) + ox) + (Math.cos(Math.toRadians(auriAngle) * dz)));
 			temp_y[i] = (int) ((y3.get(i)+oy) + (Math.sin(Math.toRadians(auriAngle) * dz)));
 			ox = (int) (ox + (Math.cos(Math.toRadians(auriAngle) * dz)));
 			oy = (int) (oy + (Math.sin(Math.toRadians(auriAngle) * dz)));
 		}
 		
 		shipShape = new Polygon(temp_x, temp_y,s);
 	}
    // y = oy + ((eY - oy)/(eX - ox))*(x - ox)  line equation
}
