
import java.util.*;
//import java.io.*;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

/*TODO
 * 
		-fix intial move coord
		-add terrain
		-fix fps lag when in battle //wtf i fixed it, lists were getting to sizes of 400,000,000 long
		-fix ai
			-give move command
			-have calculate firepower vs vision
			-different types of auri
			
		
		// jsfml
		 * lib gdx
		 * lwgl
		
 */


public class Alacrity extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener{

	private static final long serialVersionUID = 1L;
	Graphics bufferGraphics;
	Graphics bufferGraphics2;
 	Image offscreen;
 	Image backGround;

	public int x,y;
	public static int dx;
	public static int dy;
	public int eX, eY, endX, endY, startX, startY;
	public int eX2, eY2;
	public static int spottedPlayerFirePower = 0; //for the ai to know the power of player
	public long shotTime = System.currentTimeMillis();
	public Polygon changePlace;
//+
	public int fcX, fcY;
	public int temp_fcX, temp_fcY;

	public int screenSizeX = 1590;//1590;
	public int screenSizeY = 778;//778;

	public int gameSizeX = 2000;
	public int gameSizeY = 2000;
	public double time = 0;

	Ship bi;
	Fleet temp_fleet;
	Fleet temp_fleet2;
	public Fleet selectedFleet;

	public ArrayList<Ship>player1Ships = new ArrayList<Ship>();
	public ArrayList<Ship>player1SelectedShips = new ArrayList<Ship>();
	public ArrayList<Fleet>player1Fleets = new ArrayList<Fleet>();
	public ArrayList<Ship>player1SpottedShips = new ArrayList<Ship>();
	public ArrayList<Fleet>fleetHold;
	
//AI VARIABLES/////////////////////////////////////
	public Auri auri;
	
	public ArrayList<Fleet>player2Fleets = new ArrayList<Fleet>();
	public ArrayList<Ship>player2SpottedShips = new ArrayList<Ship>();
	Fleet player2Ships = new Fleet();

	//Ship Commands
	public boolean fullStop = false;
	public boolean attack = false;
	
	//Toggleables
	public boolean drawRings = false;
	public boolean showHpBars = false;
	
	//booleansssss
	//public boolean move = false;
	public boolean selected = false;
	public boolean inDrag = false;
	public boolean construct = false;
	public boolean ended = false;
	public boolean inCombat = false;
	public static boolean currentVision = false;
	public static boolean deBugStopAll = false;
	
	//threads
	Thread t;
	Thread AI;
	
//setup implementers
	public static void main(String[] args)
	{
		Alacrity b = new Alacrity();
		b.start();
	}
	
	public void init() //has Ships creations and stuff
	{

		////////////////////////////////

		//ship bi = new ship(100,100,12,20, "Frigate", "The Oblivinator", true);

		//player1Ships.add(bi);

		//bi = new ship(200,100,12,20, "Frigate", "The Oblivinator", true);

		//player1Ships.add(bi);

		///////////////////////////////
		selectedFleet = new Fleet();
		temp_fleet2 = new Fleet();
		selectedFleet.selected = true;

		fleetHold = new ArrayList<Fleet>(100);
		for(int u = 0; u<100; u++)
		{
			Fleet yip = new Fleet();
			fleetHold.add(yip);
		}

		shipCreation(50);

		////////////////////////////////

		player1Fleets.add(selectedFleet);
		x = 0; //coordinates of screen
		y = 0; //coordniates of screen

		////////////////////////////////

		setBackground(Color.black);
		offscreen = createImage(gameSizeX,gameSizeY);
		backGround = createImage(gameSizeX,gameSizeY);

        bufferGraphics = offscreen.getGraphics();
        bufferGraphics2 = backGround.getGraphics();
        drawStars();
        
        /////////////////////////////////////////////////////////////////////////
        
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		t = new Thread(this);

		this.resize(screenSizeX,screenSizeY);

		t.start();
		
		auriSetup();

	}
	
	public void auriSetup()
	{
		aiShipCreation(50);
		auri = new Auri(player2Fleets);
		
		Thread AI = new Thread(auri);
		AI.start();
	}

	public void run()
	{

		while(true)
		{
			updateShipTimers(); //update cooldowns on ship guns;
			repaint();
			try{

				Thread.sleep(4000/50);
			}
			catch( InterruptedException e)
			{;}
		}
	}

//keyListener methods

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_S)
			fullStop = true;

		if(e.getKeyChar() == KeyEvent.VK_LEFT && x!=0)
		{
			x--;
		}
		if(e.getKeyChar() == KeyEvent.VK_RIGHT && x!= (3000-1590))
		{
			x++;
		}
		if(e.getKeyChar() == KeyEvent.VK_UP && y!= 0)
		{
			y--;
		}
		if(e.getKeyChar() == KeyEvent.VK_DOWN && y!= (3000-778))
		{
			y++;
		}
		if(e.getKeyChar() == 'x')
		{
			showHpBars = true;
		}
		if(e.getKeyChar() == 'c')
		{
			drawRings = true;
		}
		//System.out.println(e.getKeyChar());
	}

	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyChar() == 'x')
		{
			showHpBars = false;
		}
		if(e.getKeyChar() == 'c')
		{
			drawRings = false;
		}
		if(e.getKeyChar() == 'd')
		{
			deBugStopAll = true;
			fullStop = true;
		}
		if(e.getKeyChar() == 'r')
		{
			deBugStopAll = false;
			fullStop = false;
		}
	}

	public void keyTyped(KeyEvent e)
	{

	}

//mouseMoveAction methods

	public void mouseDragged(MouseEvent e)
	{
		if(e.getButton()==MouseEvent.BUTTON3)
		{
			mouseClicked(e);
			//e.consume();
		}
		else
		{
			
			endX = e.getX()+dx;
			endY = e.getY()+dy;
			
			//System.out.println("StartX: " + startX + " Start Y: " + startY);
			//System.out.println("EndX: " + endX + " EndY: " + endY);
			for(int u = 0; u<player1Fleets.size(); u++)
			{
				for(int i = 0; i<player1Fleets.get(u).size();i++)
				{
					if(startX<endX && startY<endY)
						if(startX<=player1Fleets.get(u).get(i).ox && player1Fleets.get(u).get(i).ox<=endX && startY<=player1Fleets.get(u).get(i).oy && player1Fleets.get(u).get(i).oy<=endY)
						{
							selectedFleet.add(player1Fleets.get(u).get(i));
							player1Fleets.get(u).remove(i);
						}
	
					if(startX<endX && endY<startY)
						if(startX<=player1Fleets.get(u).get(i).ox && player1Fleets.get(u).get(i).ox<=endX && endY<=player1Fleets.get(u).get(i).oy && player1Fleets.get(u).get(i).oy<=startY)
						{
							selectedFleet.add(player1Fleets.get(u).get(i));
							player1Fleets.get(u).remove(i);
						}
					if(endX<startX && startY<endY)
						if(endX<=player1Fleets.get(u).get(i).ox && player1Fleets.get(u).get(i).ox<=startX && startY<=player1Fleets.get(u).get(i).oy && player1Fleets.get(u).get(i).oy<=endY)
						{
							selectedFleet.add(player1Fleets.get(u).get(i));
							player1Fleets.get(u).remove(i);
						}
					if(endX<startX && endY<startY)
						if(endX<=player1Fleets.get(u).get(i).ox && player1Fleets.get(u).get(i).ox<=startX && endY<=player1Fleets.get(u).get(i).oy && player1Fleets.get(u).get(i).oy<=startY)
						{
							selectedFleet.add(player1Fleets.get(u).get(i));
							player1Fleets.get(u).remove(i);
						}
				}
			}
	
			if(selectedFleet.size()!=0)
			{
				selected = true;
				selectedFleet.selected = true;
				//selectedFleet.move = false;
				selectedFleet.fleetName = "Selected Fleet";
				findFleetCommand(selectedFleet);
						player1Fleets.set(1, selectedFleet);
			}
			e.consume();
		}
	}

//mouseListen methods

	public void mouseEntered(MouseEvent e)
	{

	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void mouseClicked(MouseEvent e)
	{
		//System.out.println(e.getButton());
		if(e.getButton()==MouseEvent.BUTTON3)
		{
			eX = e.getX()+dx;
			eY = e.getY()+dy;
			for(int u = 0; u<player1Fleets.size();u++)
			{
				if(selected)
				{
					if(player1Fleets.get(u).selected)
					{
						player1Fleets.get(u).eX = e.getX()+x;
						player1Fleets.get(u).eY = e.getY()+y;
						player1Fleets.get(u).move = true;
					}
				}
				else
				{
					player1Fleets.get(u).eX = e.getX()+x;
					player1Fleets.get(u).eY = e.getY()+y;
					player1Fleets.get(u).move = true;
				}
			}
			fullStop = false;
		}
		else if(e.getButton()==1)
		{
			//System.out.println("before: " +player1Fleets.size());
			for(int u = 0; u<player1Fleets.size(); u++)
			{
				if(player1Fleets.get(u).selected)
				{
					player1Fleets.get(u).selected = false;

					fleetHold.get(fleetHold.size()-1).add(player1Fleets.get(u));
					fleetHold.get(fleetHold.size()-1).fcX = player1Fleets.get(u).fcX;		
					fleetHold.get(fleetHold.size()-1).fcY = player1Fleets.get(u).fcY;
					fleetHold.get(fleetHold.size()-1).eX = player1Fleets.get(u).eX;
					fleetHold.get(fleetHold.size()-1).eY = player1Fleets.get(u).eY;
					fleetHold.get(fleetHold.size()-1).redacted = true;
					fleetHold.get(fleetHold.size()-1).selected = false;
					fleetHold.get(fleetHold.size()-1).move = true;
					fleetHold.get(fleetHold.size()-1).fleetName = "Fleet " +player1Fleets.size();

					player1Fleets.add(fleetHold.get(fleetHold.size()-1));
					player1Fleets.get(u).clear();
					fleetHold.remove(fleetHold.size()-1);
				}
			}
			//System.out.println("middle: " +player1Fleets.size());

			selected = false;
			selectedFleet.clear();
			player1Fleets.set(1,selectedFleet);
			//System.out.println("after: " +player1Fleets.size());
		}
		e.consume();
	}

	public void mousePressed(MouseEvent e)
	{
			startX = e.getX()+dx;
			startY = e.getY()+dy;
			inDrag = true;
			ended = false;
			
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			for(int u = 0; u<player1Fleets.size(); u++)
			{
				if(player1Fleets.get(u).selected)
				{
					player1Fleets.get(u).selected = false;

					fleetHold.get(fleetHold.size()-1).add(player1Fleets.get(u));
					fleetHold.get(fleetHold.size()-1).fcX = player1Fleets.get(u).fcX;		
					fleetHold.get(fleetHold.size()-1).fcY = player1Fleets.get(u).fcY;
					fleetHold.get(fleetHold.size()-1).eX = player1Fleets.get(u).eX;
					fleetHold.get(fleetHold.size()-1).eY = player1Fleets.get(u).eY;
					fleetHold.get(fleetHold.size()-1).redacted = true;
					fleetHold.get(fleetHold.size()-1).selected = false;
					fleetHold.get(fleetHold.size()-1).move = true;
					fleetHold.get(fleetHold.size()-1).fleetName = "Fleet " +player1Fleets.size();

					player1Fleets.add(fleetHold.get(fleetHold.size()-1));
					player1Fleets.get(u).clear();
					fleetHold.remove(fleetHold.size()-1);
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		inDrag=false;
		endX = e.getX()+dx;
		endY = e.getY()+dy;
		ended = true;

		//e.consume();

	}

	public void mouseMoved(MouseEvent e)
	{
		eX2 = e.getX();
		eY2 = e.getY();
		
		//System.out.println(eX2 + " :: " + eY2);

		/*if(eX2<=15 && x!=0)
		{
			x=x - 10;
			dx=dx - 10;
		}
			
		if(eX2>=1570 && x!=3000-screenSizeX)
		{
			x=x+ 10;
			dx=dx + 10;
		}
			
		if(eY2<=15 && y!=0)
		{
			y=y-10;
			dy=-dy-10;
		}
			
		if(eY2>=750 && y!=3000-screenSizeY)
		{
			y=y+10;
			dy=dy+10;
		}
		e.consume(); */
			
	}

	public void reCheckMouse()
	{
		if(eX2<=15 && x!=0)
		{
			x=x - 10;
			dx = dx - 10;
		}
			
		if(eX2>=1570 && x!=3000-screenSizeX)
		{
			x=x + 10;
			dx = dx + 10;
		}
			
		if(eY2<=15 && y!=0)
		{
			y= y - 10;
			dy= dy - 10;
		}
			
		if(eY2>=750 && y!=3000-screenSizeY)
		{
			y=y + 10;
			dy= dy + 10;
		}
		//System.out.println(dx + " :: " + dy);
	}

//game Methods
	
	public void shipCreation(int num)
	{
		int axx = 0; int bxx = 0;
		for(int i = 0; i<num; i++)
		{
			axx = (int)(Math.random()*(200-0+1));
			bxx = (int)(Math.random()*(200-0+1));

			Ship bi = new Ship(50 + axx, 50 + bxx, 8, 16, ShipType.FRIGATE, "White Rain", true );
			player1Ships.add(bi);
		}

		bi = new Ship( 200, 200, 10, 32, ShipType.CRUISER, "Black Rain", true);
		player1Ships.add(bi);

		bi = new Ship(180, 180, 10, 50, ShipType.DESTROYER, "Deep Blue", true);
		player1Ships.add(bi);
		findFleetCommand(player1Ships);
		temp_fleet = new Fleet(player1Ships,fcX,fcY);
		player1Fleets.add(temp_fleet);


	}
	
	public void aiShipCreation(int num)
	{
		int axx = 0; int bxx = 0;
		for(int i = 0; i<num; i++)
		{
			axx = (int)(Math.random()*(700-500+1)+500);
			bxx = (int)(Math.random()*(700-500+1)+500);

			Ship bi = new Ship(50 + axx, 50 + bxx, 8, 16, ShipType.FRIGATE, "Death", false );
			player2Ships.add(bi);
		}
		bi = new Ship( 600, 600, 10, 32, ShipType.CRUISER, "Auri", false);
		player2Ships.add(bi);
		bi = new Ship(620, 610, 10, 42, ShipType.BATTLECRUISER, "Blarg", false);
		player2Ships.add(bi);
		findFleetCommand(player2Ships);
		player2Fleets.add(player2Ships);
		
	}

	public void printFleets()
	{
		for(int u = 0; u<player1Fleets.size();u++)
		{
			//System.out.println(player1Fleets.get(u).fleet);
		}
	}

	public void findFleetCommand(ArrayList<Ship>ships) //finds fleet center
	{
		int totalX = 0;
		int totalY = 0;

		if(ships.size()!=0)
		{
			for(int i = 0; i<ships.size(); i++)
			{
				totalX+=ships.get(i).ox ;
				totalY+=ships.get(i).oy ;
			}

			fcX = (totalX/ships.size());
			fcY = (totalY/ships.size());
		}


	}

	public void findFleetCommand(Fleet ships)
	{
		int totalX = 0;
		int totalY = 0;

		if(ships.size()!=0)
		{
			for(int i = 0; i<ships.size(); i++)
			{
				totalX+=ships.get(i).ox ;
				totalY+=ships.get(i).oy ;
			}

			ships.fcX = totalX/ships.size();
			ships.fcY = totalY/ships.size();
		}


	}

	public void checkFleetCommand(Fleet ships)
	{
		int totalX = 0;
		int totalY = 0;

		if(ships.size()!=0)
		{
			for(int i = 0; i<ships.size(); i++)
			{
				totalX+=ships.get(i).ox;
				totalY+=ships.get(i).oy;
			}

			temp_fcX = totalX/ships.size();
			temp_fcY = totalY/ships.size();
		}

	}

	public boolean checkSelectFleet() //fuck booleans
	{	int bong= 0;
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			if(player1Fleets.get(u).selected)
				bong = -1;
		}
		if(bong == -1)
			return true;
		return false;

	}

	public int distanceToTarget(Fleet temp)
	{
		checkFleetCommand(temp);
		int td = (int)(Math.pow( (Math.pow(temp_fcX-temp.eX,2) + Math.pow(temp_fcY-temp.eY,2)) , .5 ) );
		//System.out.println(temp);
		//System.out.println("Distance: " +td);
		return td;
	}
	
	public void checkForDeadShips() //checks for in ships have zero hp
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{
				if(player1Fleets.get(u).get(i).currentHP<=0)
					player1Fleets.get(u).remove(i);
			}
		}
		for(int u = 0; u<player2Fleets.size(); u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{
				if(player2Fleets.get(u).get(i).currentHP<=0)
					player2Fleets.get(u).remove(i);
			}
		}
		for(int t = 0; t<player2SpottedShips.size(); t++)
		{
			if(player2SpottedShips.get(t).currentHP<=0)
				player2SpottedShips.remove(t);
		}
		for(int t = 0; t<player1SpottedShips.size(); t++)
		{
			if(player1SpottedShips.get(t).currentHP<=0)
				player1SpottedShips.remove(t);
		}
	}
	
	public void checkIfInCombat()
	{	int tag = 0;
	
		if(inCombat)
		{
			for(int u = 0; u<player1Fleets.size(); u++)
			{
				for(int i = 0; i<player1Fleets.get(u).size(); i++)
				{
					if(player1Fleets.get(u).get(i).hasTarget)
						tag++;
				}
			}
			for(int u = 0; u<player2Fleets.size(); u++)
			{
				for(int i = 0; i<player2Fleets.get(u).size(); i++)
				{
					tag++;
				}
			}
			if(tag==0)
				inCombat = false;
		}

	}
	
	public void updateSpottedPlayerFirePower() //updates the firepower assesment auri has of player
	{	spottedPlayerFirePower = 0;
		for(int u = 0; u<player1SpottedShips.size(); u++)
		{
			spottedPlayerFirePower = spottedPlayerFirePower + player1SpottedShips.get(u).firePower;
		}
	}
	
//vision stuff
	
	public void checkInRange(Ship num) //idk again
	{
		for(int u = 0; u<player2Fleets.size(); u++)
		{
			
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{
				if(num.inRange(player2Fleets.get(u).get(i)))
				{
					player2Fleets.get(u).get(i).isSpotted = true;
					player2Fleets.get(u).get(i).spottedShip = num;
					
					if(!( player2SpottedShips.contains(player2Fleets.get(u).get(i)) )) //removes redundant ship additions
						player2SpottedShips.add(player2Fleets.get(u).get(i));
					
					if(!( player1SpottedShips.contains(num) )) //removes redundant ship additions
						player1SpottedShips.add(num);

					num.isSpotted = true;
				}
					
			}
		}
	}
	
	public void checkEnemyInRange() //idk
	{
		for(int u = 0; u<player1Fleets.size();u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{
				checkInRange(player1Fleets.get(u).get(i));
			}
		}
	}
	
	public void checkIfEnemyStillSpotted() // checks if player2 Spotted Ships are still being spotted by player 1
	{
		for(int u = 0; u<player2SpottedShips.size();u++)
		{
			checkIf2StillSpotted(player2SpottedShips.get(u));
		}	
	}
	
	public void checkIf2StillSpotted(Ship num) // if not spotted, and out of range, then player2 ships turns to have no target
	{	int geo = 0;int neo = 0;
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size();i++)
			{
				if(num.inRange(player1Fleets.get(u).get(i)))
				{
					geo++;
				}
				if(num.numInRange(player1Fleets.get(u).get(i))<num.fireRange)
				{
					neo++;
				}
			}
		}
		if(geo == 0)
		{
			player2SpottedShips.remove(num);
			num.isSpotted = false;
		}
		if(neo == 0)
		{
			num.hasTarget = false;
		}
	}
	
	public void checkIfStillInEnemyVision() // checks if player1 ships are still in player 2 vision
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{
				isEnemyVision(player1Fleets.get(u).get(i));
			}
		}
	}
	
	public void isEnemyVision(Ship bt) // this is prob where phantom laser problem is // checks if player1 ship still had player 2 target or is spotted by player 2
	{	int ghj = 0; int neo = 0;
		for(int u = 0; u<player2Fleets.size(); u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{
				if(bt.inRange(player2Fleets.get(u).get(i)))
				{
					ghj++;
				}
				if(bt.numInRange(player2Fleets.get(u).get(i))<bt.fireRange)
				{
					neo++;
				}
			}
		}
		if(ghj==0)
		{
			bt.isSpotted = false;
			player1SpottedShips.remove(bt); // this makes it so that when player ships leave sight of auri, they are removed from spotted list
		}
			
		if(neo==0)
			bt.hasTarget = false;
	}

	public void updateIsSpotted() //this updates vision booleans for auri
	{
		if(player2SpottedShips.size()>0)
		{
			currentVision = true;
			//System.out.println(player2SpottedShips.size());
		}
		else
			currentVision = false;
	}
	
//combate methods
	
	public void checkFireCommand()
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{
				bi = player1Fleets.get(u).get(i);
				if(player1Fleets.get(u).get(i).isSpotted && player1Fleets.get(u).get(i).hasTarget==false)
				{
					player1Fleets.get(u).get(i).shipTarget = checkNearestInRange(player1Fleets.get(u).get(i));
					if(player1Fleets.get(u).get(i).shipTarget.notBot == true)
					{
						player1Fleets.get(u).get(i).hasTarget = true;
						inCombat = true;
						//System.out.println("CD: " + player1Fleets.get(u).get(i).shotTimer);
						if(player1Fleets.get(u).get(i).shotTimer==player1Fleets.get(u).get(i).reloadTime)
						{
							player1Fleets.get(u).get(i).shipTarget.currentHP = player1Fleets.get(u).get(i).shipTarget.currentHP-player1Fleets.get(u).get(i).firePower;
							player1Fleets.get(u).get(i).shotTimer = 0;
						}	
					}	
				}
				else
					player1Fleets.get(u).get(i).hasTarget = false;
			}
		}
	}
	
	public void checkEnemyFireCommand()
	{
		for(int u = 0; u<player2Fleets.size(); u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{
				bi = player2Fleets.get(u).get(i);
				if((player2Fleets.get(u).get(i).isSpotted || player2Fleets.get(u).get(i).hasTarget==false) || (player2Fleets.get(u).get(i).isSpotted && player2Fleets.get(u).get(i).hasTarget==false))
				{
					player2Fleets.get(u).get(i).shipTarget = checkNearestInRangeEnemy(player2Fleets.get(u).get(i));
					if(player2Fleets.get(u).get(i).shipTarget.notBot == true)
					{
						player2Fleets.get(u).get(i).hasTarget = true;
						inCombat = true;
						//System.out.println("CD: " + player1Fleets.get(u).get(i).shotTimer);
						if(player2Fleets.get(u).get(i).shotTimer==player2Fleets.get(u).get(i).reloadTime)
						{
							player2Fleets.get(u).get(i).shipTarget.currentHP = player2Fleets.get(u).get(i).shipTarget.currentHP-player2Fleets.get(u).get(i).firePower;
							player2Fleets.get(u).get(i).shotTimer = 0;
						}	
					}	
				}
				else
					player2Fleets.get(u).get(i).hasTarget = false;
			}
		}
	}
	
	public Ship checkNearestInRangeEnemy(Ship bt)
	{	
		Ship base = new Ship();
		int dist = 0;
		for(int u = 0; u<player1Fleets.size();u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{
				if(player1Fleets.get(u).get(i).numInRange(bt)<=bt.fireRange && player1Fleets.get(u).get(i).numInRange(bt)>dist)
				{
					base = player1Fleets.get(u).get(i);
					dist = player1Fleets.get(u).get(i).numInRange(bt);
				}
			}
		}
		return base;
	}
	
	public Ship checkNearestInRange(Ship bt)
	{	
		Ship base = new Ship();
		int dist = 0;
		for(int u = 0; u<player2Fleets.size();u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{
				if(player2Fleets.get(u).get(i).numInRange(bt)<=bt.fireRange && player2Fleets.get(u).get(i).numInRange(bt)>dist)
				{
					base = player2Fleets.get(u).get(i);
					dist = player2Fleets.get(u).get(i).numInRange(bt);
				}
			}
		}
		return base;
	}
	
	public void calcEnemyHPLosses()
	{
		
	}
	
	public void updateShipTimers()
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{	
				if(player1Fleets.get(u).get(i).shotTimer==player1Fleets.get(u).get(i).reloadTime)
				{
					
				}
				else
					player1Fleets.get(u).get(i).shotTimer++;
			}
		}
		for(int u = 0; u<player2Fleets.size(); u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{	
				if(player2Fleets.get(u).get(i).shotTimer==player2Fleets.get(u).get(i).reloadTime)
				{
					
				}
				else
					player2Fleets.get(u).get(i).shotTimer++;
			}
		}
	}
	
//draw methods
	
	public void drawStars()
	{
		for(int i = 0; i<10000; i++)
		{
			bufferGraphics2.setColor(Color.white);
			int rgx = (int) (Math.random()*(gameSizeX-1));
			int rgy = (int) (Math.random()*(gameSizeY-1));
			bufferGraphics2.drawLine(rgx, rgy, rgx, rgy);
		}
	}
	
	public void drawClock(Graphics g)
	{
		bufferGraphics.drawString("CURRENT TIME: " + time/10, 800 + dx, 50 + dy);
		time++;
	}

	public void drawPlayer1Ships(Graphics g) //debug vision code inside
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			if(player1Fleets.get(u).selected==false)
			{
				for(int i = 0; i<player1Fleets.get(u).size(); i++)
				{
					if(player1Fleets.get(u).get(i).colorBlue)
					{
						changePlace = player1Fleets.get(u).get(i).shipShape; //arraylist of fleets
						
						if(drawRings)//debugg for vision
						{
							bufferGraphics.setColor(Color.white);
							bufferGraphics.drawOval(player1Fleets.get(u).get(i).ox-player1Fleets.get(u).get(i).sightRange, player1Fleets.get(u).get(i).oy-player1Fleets.get(u).get(i).sightRange, player1Fleets.get(u).get(i).sightRange*2, player1Fleets.get(u).get(i).sightRange*2);
						
							bufferGraphics.setColor(Color.red);
							bufferGraphics.drawOval(player1Fleets.get(u).get(i).ox-player1Fleets.get(u).get(i).fireRange, player1Fleets.get(u).get(i).oy-player1Fleets.get(u).get(i).fireRange, player1Fleets.get(u).get(i).fireRange*2, player1Fleets.get(u).get(i).fireRange*2);
						}
						///////////////////
						
						bufferGraphics.setColor(Color.blue);
						bufferGraphics.fillPolygon(changePlace);

						bufferGraphics.setColor(Color.black);
						bufferGraphics.drawPolygon(changePlace);
					}
				}
			}
		}
	}

	public void drawPlayer1SelectedShips(Graphics g) //debug vision code inside
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			if(player1Fleets.get(u).selected)
			{
				for(int i = 0; i<player1Fleets.get(u).size(); i++)
				{
					if(player1Fleets.get(u).get(i).colorBlue)
					{
						changePlace = player1Fleets.get(u).get(i).shipShape;
						
						if(drawRings)//debugg for vision
						{
							bufferGraphics.setColor(Color.white);
							bufferGraphics.drawOval(player1Fleets.get(u).get(i).ox-player1Fleets.get(u).get(i).sightRange, player1Fleets.get(u).get(i).oy-player1Fleets.get(u).get(i).sightRange, player1Fleets.get(u).get(i).sightRange*2, player1Fleets.get(u).get(i).sightRange*2);
						
							bufferGraphics.setColor(Color.red);
							bufferGraphics.drawOval(player1Fleets.get(u).get(i).ox-player1Fleets.get(u).get(i).fireRange, player1Fleets.get(u).get(i).oy-player1Fleets.get(u).get(i).fireRange, player1Fleets.get(u).get(i).fireRange*2, player1Fleets.get(u).get(i).fireRange*2);
						}
						///////////////////
						
						bufferGraphics.setColor(Color.blue);
						bufferGraphics.fillPolygon(changePlace);

						bufferGraphics.setColor(Color.white);
						bufferGraphics.drawPolygon(changePlace);
					}
				}
			}
		}
	}
	
	public void drawWhatAuriSees(Graphics g) // draws waht auri sees, debuging only
	{
		for(int u = 0; u<player1SpottedShips.size(); u++)
		{
			if(player1SpottedShips.get(u).colorBlue)
			{
				changePlace = player1SpottedShips.get(u).shipShape; 
				
				if(drawRings)//debugg for vision
				{
					bufferGraphics.setColor(Color.white);
					bufferGraphics.drawOval(player1SpottedShips.get(u).ox-player1SpottedShips.get(u).sightRange, player1SpottedShips.get(u).oy-player1SpottedShips.get(u).sightRange, player1SpottedShips.get(u).sightRange*2, player1SpottedShips.get(u).sightRange*2);
				
					bufferGraphics.setColor(Color.red);
					bufferGraphics.drawOval(player1SpottedShips.get(u).ox-player1SpottedShips.get(u).fireRange, player1SpottedShips.get(u).oy-player1SpottedShips.get(u).fireRange, player1SpottedShips.get(u).fireRange*2, player1SpottedShips.get(u).fireRange*2);
				}
				
		////////////////////////////////////////////////////////
				bufferGraphics.setColor(Color.pink);
				bufferGraphics.fillPolygon(changePlace);

				bufferGraphics.setColor(Color.black);
				bufferGraphics.drawPolygon(changePlace);
			}
		}
	}
	
	public void drawMoveCoord(Graphics g)
	{

		if(!(fullStop) && eX!=0 && eY!=0)
		{
			bufferGraphics.setColor(Color.red);
			bufferGraphics.fillRect(eX-10, eY-3, 19, 5);
			bufferGraphics.fillRect(eX-3, eY-10, 5, 20);

			bufferGraphics.setColor(Color.black);
			bufferGraphics.drawRect(eX-10, eY-3, 19, 5);
			bufferGraphics.drawRect(eX-3, eY-10, 5, 20);


		}
	}

	public void drawFleetCommand(Graphics g)
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			bufferGraphics.setColor(Color.red);
			bufferGraphics.fillRect(player1Fleets.get(u).fcX, player1Fleets.get(u).fcY, 5,5);

			bufferGraphics.setColor(Color.black);
			bufferGraphics.drawRect(player1Fleets.get(u).fcX, player1Fleets.get(u).fcY, 5,5);
		}
	}
	
	public void drawHpBars(Graphics g)
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			for(int i = 0 ; i<player1Fleets.get(u).size(); i++)
			{
				bi = player1Fleets.get(u).get(i);
				
				bufferGraphics.setColor(Color.white);
				bufferGraphics.fillRect(bi.ox-15, bi.oy-20, 30, 5);
				
				bufferGraphics.setColor(Color.blue);
				bufferGraphics.fillRect(bi.ox-15, bi.oy-20, bi.getWidth(), 5);
			
				bufferGraphics.setColor(Color.red);
				bufferGraphics.drawRect(bi.ox-15, bi.oy-20, 30, 5);
				
			}
		}	
		//for(int u = 0; u<player2Fleets.size(); u++)
		{
			//for(int i = 0 ; i<player2Fleets.get(u).size(); i++)
			{
				///bi = player2Fleets.get(u).get(i);
				
				//bufferGraphics.setColor(Color.white);
				//bufferGraphics.fillRect(bi.ox-15, bi.oy-20, 30, 5);
				
				//bufferGraphics.setColor(Color.red);
				//bufferGraphics.fillRect(bi.ox-15, bi.oy-20, bi.getWidth(), 5);
			
				//bufferGraphics.setColor(Color.black);
				//bufferGraphics.drawRect(bi.ox-15, bi.oy-20, 30, 5);
				
			}
		}
		for(int y = 0; y<player2SpottedShips.size(); y++)
		{
			bi = player2SpottedShips.get(y);
			
			bufferGraphics.setColor(Color.white);
			bufferGraphics.fillRect(bi.ox-15, bi.oy-20, 30, 5);
			
			bufferGraphics.setColor(Color.green);
			bufferGraphics.fillRect(bi.ox-15, bi.oy-20, bi.getWidth(), 5);
		
			bufferGraphics.setColor(Color.red);
			bufferGraphics.drawRect(bi.ox-15, bi.oy-20, 30, 5);
		}
	}
	
	public void drawToEnemyFire(Graphics g) //////now qoriking
	{
		for(int u = 0; u<player1Fleets.size();u++)
		{
			for(int i = 0; i<player1Fleets.get(u).size(); i++)
			{
				bi = player1Fleets.get(u).get(i);
				if(player1Fleets.get(u).get(i).hasTarget )
				{
					bufferGraphics.setColor(new Color(128,255,255));
					bufferGraphics.drawLine(player1Fleets.get(u).get(i).ox, player1Fleets.get(u).get(i).oy, player1Fleets.get(u).get(i).shipTarget.ox, player1Fleets.get(u).get(i).shipTarget.oy);
				}
			}
		}
	}
	
	public void drawFromEnemyFire(Graphics g) // draws enemy fire
	{
		for(int u = 0; u<player2Fleets.size();u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size(); i++)
			{
				//bi = player2Fleets.get(u).get(i);
				if(player2Fleets.get(u).get(i).hasTarget ) // 
				{
					//bufferGraphics.setColor(new Color(255,255,255));
					//bufferGraphics.drawLine(player2Fleets.get(u).get(i).ox + player2Fleets.get(u).get(i).x3.get(2), player2Fleets.get(u).get(i).oy + player2Fleets.get(u).get(i).y3.get(2), player2Fleets.get(u).get(i).shipTarget.ox, player2Fleets.get(u).get(i).shipTarget.oy);
				}
			}
		}
		for(int u = 0; u<player2SpottedShips.size(); u++)
		{
			if(player2SpottedShips.get(u).hasTarget ) // 
			{
				bufferGraphics.setColor(new Color(255,0,0));
				bufferGraphics.drawLine(player2SpottedShips.get(u).ox + player2SpottedShips.get(u).x3.get(2), player2SpottedShips.get(u).oy + player2SpottedShips.get(u).y3.get(2), player2SpottedShips.get(u).shipTarget.ox, player2SpottedShips.get(u).shipTarget.oy);
			}
		}
	}
	
//draw Player2Ships

	public void drawSpottedEnemyShips(Graphics g)
	{
		for(int u = 0; u<player2Fleets.size(); u++)
		{
			for(int i = 0; i<player2Fleets.get(u).size();i++) //this is for debugging only
			{	
				//System.out.println(player2Fleets.get(u));
				
				changePlace = player2Fleets.get(u).get(i).shipShape;
				bufferGraphics.setColor(Color.red);
				bufferGraphics.fillPolygon(changePlace);

				bufferGraphics.setColor(Color.black);
				bufferGraphics.drawPolygon(changePlace);
			}
		}
		
		for(int u = 0; u<auri.auriFleets.size(); u++)
		{
			for(int i = 0; i<auri.auriFleets.get(u).size(); i++)
			{
				/*
				changePlace = auri.auriFleets.get(u).get(i).shipShape;
				bufferGraphics.setColor(Color.yellow);
				bufferGraphics.fillPolygon(changePlace);

				bufferGraphics.setColor(Color.black);
				bufferGraphics.drawPolygon(changePlace); */
			}
		}
		
		for(int u = 0; u<player2SpottedShips.size();u++)
		{
			changePlace = player2SpottedShips.get(u).shipShape;
			bufferGraphics.setColor(Color.green);
			bufferGraphics.fillPolygon(changePlace);

			bufferGraphics.setColor(Color.black);
			bufferGraphics.drawPolygon(changePlace);
		}
	}
	
	//move player2 ships
	//rotate player2 ships
	//calc move player2 (brains)
	
//ship methods

	public void player1ShipsUpdate() //rotates all ships
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			if(player1Fleets.get(u).move)
			{
				if(selected || player1Fleets.get(u).redacted)
				{
					if(player1Fleets.get(u).selected || player1Fleets.get(u).redacted)
					{
						for(int i = 0; i<player1Fleets.get(u).size(); i++)
						{
							player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
						}
					}
				}
				else
				{
					for(int i = 0; i<player1Fleets.get(u).size(); i++)
					{
						player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
					}
				}
			}
		}
	}

	public void player1ShipsMove() //need to work on my conditions for stopping ships that go past the beacon.   (AND KEEP THEM MOVING UNTIL IN CERTAIN RANGE FROM BEACON)
	{
		for(int u = 0; u<player1Fleets.size(); u++)
		{
			if(player1Fleets.get(u).move)
			{
				if(selected || player1Fleets.get(u).redacted)
				{
					if(player1Fleets.get(u).selected || player1Fleets.get(u).redacted)
					{
						for(int i = 0; i<player1Fleets.get(u).size(); i++) //fix the if statements to make them stop turing when close to eXeY
						{
							if(!(distanceToTarget(player1Fleets.get(u))<50))
							{
								player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
								player1Fleets.get(u).get(i).move(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
								player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
							}
							else
							{
								player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
								player1Fleets.get(u).get(i).move(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
								player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
							}
						}
					}
				}
				else
				{
					for(int i = 0; i<player1Fleets.get(u).size(); i++) //fix the if statements to make them stop turing when close to eXeY
					{
						if(!(distanceToTarget(player1Fleets.get(u))<50))
						{
							player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
							player1Fleets.get(u).get(i).move(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
							player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
						}
						else
						{
							player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
							player1Fleets.get(u).get(i).move(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
							player1Fleets.get(u).get(i).makeCoords(player1Fleets.get(u).eX, player1Fleets.get(u).eY, player1Fleets.get(u).fcX, player1Fleets.get(u).fcY);
						}
					}
				}
				findFleetCommand(player1Fleets.get(u));

				if((distanceToTarget(player1Fleets.get(u))<10))
					player1Fleets.get(u).move = false;
			}
		}
	}

//MAIN METHODSS

	public void paint(Graphics g)
	{ 	bufferGraphics.clearRect(0,0,gameSizeX,gameSizeY);
//remove selector
		//if(player1SelectedShips.size()==0)
		//selected=false;
		//printFleets();
//drawReferenceStars
		bufferGraphics.drawImage(backGround,0,0, gameSizeX,gameSizeY, 0, 0, 1590, 778, this);
	
//drawClock
		drawClock(g);
		
//reCheck Mouse
		reCheckMouse();

//draw player1Ships
		drawPlayer1Ships(g);

//draw player1SelectedShips
		if(selected)
		drawPlayer1SelectedShips(g);
		
//draws what auri Sees
		drawWhatAuriSees(g);
		
//draw HP bars
		if(showHpBars)
			drawHpBars(g);

//draw fleetCenters (debugging only)
		drawFleetCommand(g);
		//System.out.println("paint: " +player1Fleets.size());
		
//draw Move Coordinates
		drawMoveCoord(g);
				
//check if Enemy in Range
		checkIfInCombat();
		//if(!inCombat)
			checkEnemyInRange();
		
//drawAI SPOTTED SHIPS
		drawSpottedEnemyShips(g);
		
//check if Still in range
		checkIfEnemyStillSpotted();
		
//checks if still spotted or in range?
		checkIfStillInEnemyVision();

//update ship, if there is command
		if(!fullStop)
			//player1ShipsUpdate();

//move ship in updated direction
		if(!fullStop)
			player1ShipsMove();

//updates AI's vision boolean
		updateIsSpotted();
		
//updates auri's firepower assesment
		updateSpottedPlayerFirePower();

//FIRE COMMAND methods
		checkFireCommand();
		checkEnemyFireCommand();
		
		//if(System.currentTimeMillis()-shotTime >10);
		{	
			drawToEnemyFire(g);
			drawFromEnemyFire(g);
			//shotTime=System.currentTimeMillis();
		}
		
		//calcEnemyHPLosses();
		//updateShipTimers();
		
//finally check for dead guys both player1 and 2
		checkForDeadShips();

//draws bufferedImage
		bufferGraphics.setColor(Color.white);
		bufferGraphics.drawLine(10,10,gameSizeX - 10,10);
		bufferGraphics.drawLine(gameSizeX-10,10,gameSizeX-10,gameSizeY-10);
		bufferGraphics.drawLine(gameSizeX-10,gameSizeY-10,10,gameSizeY-10);
		bufferGraphics.drawLine(10,gameSizeY-10,10,10);
		g.drawImage(offscreen,0,0, 1590,778, x, y, x+1590, y+778, this);
		
	}

	public void update(Graphics g)
	{	
		//bufferGraphics.clearRect(0,0,gameSizeX,gameSizeY); 
		paint(g);
	}


}