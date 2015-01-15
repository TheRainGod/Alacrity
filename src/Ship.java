import java.util.*;
//import java.io.*;
import java.awt.*;
import java.awt.Graphics2D.*;
import java.awt.geom.Path2D;
//import java.awt.geom.*;


public class Ship {

	//ints
	public double x, y, ox, oy, w, h;
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
	public double[] x1;
	public double[] y1;
	public double[] temp_x;
	public double[] temp_y;
	public double[] x2;
	public double[] y2;
	public double xx2[];
	public double yy2[];
	public double[] xcoord = new double[4];
   	public double[] ycoord = new double[4];
	public ArrayList<Double>x3;
	public ArrayList<Double>y3;
	public ArrayList<Double>x4;
	public ArrayList<Double>y4;
	public Point p1,p2,p3,p0;
	public double shipAngle;
	public int s;

	//polygons
	public Polygon shipShape;
	public Path2D shipShapeDouble = new Path2D.Double();

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

	public Ship(double a, double b, double c, double d, ShipType t, String name1, boolean yes)   //width and then height BOTH NEED TO BE DIVISIBLE BY 2
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
    	double[] x1 = {x, x+(w/2), x, x-(w/2)};
    	double[] y1 = {y-(h/2), y, y+(h/2), y};

    	double[] xx2 = {0+ox, w/2+ox, 0+ox, 0-(w/2)+ox};
    	double[] yy2 = {0-(h/2)+oy, 0+oy, h/2+oy, 0+oy};

       // p0 = new Point(x1[0],y1[0]);
       // p1 = new Point(x1[1],y1[1]);
       // p2 = new Point(x1[2],y1[2]);
       // p3 = new Point(x1[3],y1[3]);

		x3 = new ArrayList<Double>();
		y3 = new ArrayList<Double>();
		x3.add(0,0.0);
		x3.add(1,(w/2.0));
		x3.add(2,0.0);
		x3.add(3,(0-(w/2)));
		y3.add(0,0-(h/2));
		y3.add(1,0.0);
		y3.add(2,h/2);
		y3.add(3,0.0);

		x4 = new ArrayList<Double>(); x4.add(0.0); x4.add(0.0); x4.add(0.0);x4.add(0.0);
		//Collections.copy(x4, x3);
		y4 = new ArrayList<Double>(4); y4.add(0.0); y4.add(0.0); y4.add(0.0);y4.add(0.0);
		//Collections.copy(y4, y3);

    	///double[] x2 = {x, x+(w/2), x, x-(w/2)};
    	//double[] y2 = {y, y+(h/2), y+h, y+(h/2)};
    	s = 4;
    	
    	
    	createPolygon();
    	
    	//shipShape = new Polygon(xx2,yy2,s);
		
		sightRange = 150;
	
    	shipType = t;
    	shipName = name1;
    	colorBlue = yes;
    	
    	notBot = true;
    	
    	findFireRange(shipType);
    }
	
	public void createPolygon()
	{
		shipShapeDouble.moveTo(x3.get(0), y3.get(0));
    	shipShapeDouble.lineTo(x3.get(1), y3.get(1));
    	shipShapeDouble.lineTo(x3.get(2), y3.get(2));
    	shipShapeDouble.lineTo(x3.get(3), y3.get(3));
    	shipShapeDouble.closePath();
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
    	 double delta_x = eX-ox;
    	 double delta_y = oy-eY;
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
       			 xcoord[i] = temp_x2 + ox;
       			 ycoord[i] = temp_y2 + oy;
       			 //x3.add(i,(int)(x+(x3.get(i)*Math.sin(angle)-y3.get(i)*Math.cos(angle))));
       			 //y3.add(i,(int)(y+(x3.get(i)*Math.cos(angle)+y3.get(i)*Math.sin(angle))));
    		 }
    		 
    	//shipShape = new Polygon(xcoord, ycoord, s);
    		createPolygon2(xcoord, ycoord);
    	 }
 	 }
    
    public void createPolygon2(double[] XX, double[] YY)
    {	
    	shipShapeDouble.moveTo(XX[0], YY[0]);
    	for(int i = 1; i<4; i++)
    	{
    		shipShapeDouble.lineTo(XX[i], YY[i]);
    		
    	}
    }

 	public void move(int eX1, int eY1, int fcX1, int fcY1) //moves 
 	{
 		double slope = (eY1+0.0-fcY1)/(eX1+0.0-fcX1);
 		//double slopeDub = (eY1+0.0-fcY1)/(eX1+0.0-fcX1);
 		boolean trick = false;
 		//System.out.println("Y COORD: " + (eY1+0.0-fcY1));
 		//System.out.println("X COORD: " + (eX1+0.0-fcX1));
 		temp_x = new double[4];
 		temp_y = new double[4];
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
 					temp_y[i] = (y3.get(i) + oy + dy);
 					oy = (oy+dy);
 					//System.out.println("+dy");
 				}
 				else
 				{
 					temp_y[i] = (y3.get(i) + oy - dy);
 					oy= (oy-dy);
 					//System.out.println("-dy");
 				}
 			}
 			else
 			{
	 				if(eX1-fcX1 == 0)
	 				{
	 					if(eY1-fcY1>0)
	 						temp_y[i] = (y3.get(i)+ oy + dy);
	 					else
	 						temp_y[i] = (y3.get(i) + oy - dy);
	 				}

	 				if(eY1-fcY1==0)
	 				{
	 					if(eX1-fcX1>0)
	 						temp_x[i] = (x3.get(i) + ox + dx);
	 					else
	 						temp_x[i] = (x3.get(i) + ox - dx);
	 				}

	 				else
	 				{
	 					if(eX1-fcX1>0)
		 					temp_x[i] = (x3.get(i)+ox+dx);

		 				else
		 					temp_x[i] = (x3.get(i)+ox-dx);

		 				temp_y[i] = (y3.get(i)+oy + ((slope * (temp_x[i]+0.0 - ox))) );
	 				}

 					if(eX1-fcX1==0)
 					{
 						if(eY1-fcY1>0)
 							oy = (oy + dy);
 						else
 							oy = (oy - dy);
 					}
 					else
 					{

 						if(eX1-fcX1>0)
 						{
 							ox = (ox + dx);
 							oy = Math.round(oy + ((slope) * (dx+0.0)));
 						}
 						else
 						{
 							ox = (ox - dx);
 							oy = Math.round(oy + ((slope) * (0.0-dx)));
 						}
 					}
 			}
 		}
 		trick = false;

 		//shipShape = new Polygon(temp_x, temp_y,s);
 		createPolygon2(temp_x, temp_y);
 	}

 	public void move2(int eX1, int eY1, int fcX1, int fcY1)
 	{	
 		
 		int delta_x = fcX1 - eX1;
 		int delta_y = fcY1 - eY1;
 		double angle = Math.atan2(delta_y, delta_x);
 		
 		shipAngle = angle;
 		temp_x = new double[4];
 		temp_y = new double[4];
 		
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = ((x3.get(i) + ox) + (Math.cos((shipAngle) * dz)));
 			temp_y[i] = ((y3.get(i)+oy) + (Math.sin((shipAngle) * dz)));
 			ox = (int) (ox + (Math.cos((shipAngle) * dz)));
 			oy = (int) (oy + (Math.sin((shipAngle) * dz)));
 		}
 		
 		//shipShape = new Polygon(temp_x, temp_y,s);
 		createPolygon2(temp_x, temp_y);
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
       			 xcoord[i] = temp_x2 + ox;
       			 ycoord[i] = temp_y2 + oy;
       			 //x3.add(i,(int)(x+(x3.get(i)*Math.sin(angle)-y3.get(i)*Math.cos(angle))));
       			 //y3.add(i,(int)(y+(x3.get(i)*Math.cos(angle)+y3.get(i)*Math.sin(angle))));
    		 }

    	//shipShape = new Polygon(xcoord, ycoord, s);
    		 createPolygon2(xcoord, ycoord);
 	}
 	
 	public void auriFrigateScoutMove() //moves auri's Frigates in a scouting formation
 	{
 		temp_x = new double[4];
 		temp_y = new double[4];
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
 			temp_x[i] = ((x3.get(i) + ox) + (dx * scaleX));
 			temp_y[i] = ((y3.get(i)+oy) + (dy * scaleY));
 			ox = (ox + (dx * scaleX));
 			oy = (oy + (dy * scaleY));
 		}
 		//shipShape = new Polygon(temp_x, temp_y,s);
 		createPolygon2(temp_x, temp_y);
 	}
 	
 	public void auriFrigateScoutMove2() //moves auri's Frigates in a scouting formation
 	{
 		temp_x = new double[4];
 		temp_y = new double[4];
 		dx=1.0;
 		dy=1.0;
 		
 		double scaleX = Math.cos(Math.toRadians(auriAngle));
 		double scaleY = Math.sin(Math.toRadians(auriAngle));
 		
 		if(auriAngle>=180)
 			dx = 0-1.0;
 		
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = ((x3.get(i) + ox) + (dx * scaleX));
 			temp_y[i] = ((y3.get(i)+oy) + (dy * scaleY / scaleX));
 			ox = (ox + (dx));
 			oy = (oy + (dy * scaleY / scaleX));
 		}
 		//shipShape = new Polygon(temp_x, temp_y,s);
 		createPolygon2(temp_x, temp_y);
 	}
 	
 	public void auriFrigateScout()
 	{	
 		temp_x = new double[4];
 		temp_y = new double[4];
 		
 		for(int i = 0; i<4; i++)
 		{
 			temp_x[i] = ((x3.get(i) + ox) + (Math.cos(Math.toRadians(auriAngle) * dz)));
 			temp_y[i] =  ((y3.get(i)+oy) + (Math.sin(Math.toRadians(auriAngle) * dz)));
 			ox = (ox + (Math.cos(Math.toRadians(auriAngle) * dz)));
 			oy = (oy + (Math.sin(Math.toRadians(auriAngle) * dz)));
 		}
 		
 		//shipShape = new Polygon(temp_x, temp_y,s);
 		createPolygon2(temp_x, temp_y);
 	}
    // y = oy + ((eY - oy)/(eX - ox))*(x - ox)  line equation
}
