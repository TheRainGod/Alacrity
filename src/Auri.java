import java.util.*;
//import java.awt.*;

public class Auri implements Runnable //Ai superclass
{
	//public int variables
		public int auriFleetSize;
		public int temp_fcX = 0;
		public int temp_fcY = 0;;
		public int eX1, eY1;
		public int test_X, test_Y;
		public int bagX, bagY;
		public int currentFirePower; // calcd on a dmg per shot basis
		public int currentPlayerFirePower;
		
		public int fleetFirePower; // on a dmg per shot basis meaning that the ai can overestimate its dmg if it doesnt shoot at the same rate as the humans
	
	//all of Auri ships
		public Fleet auriFleet;
		public ArrayList<Ship>shipHold = new ArrayList<Ship>();
		public ArrayList<Fleet>fleetHold = new ArrayList<Fleet>();
		public ArrayList<Fleet>auriFleets = new ArrayList<Fleet>();
		public Fleet scoutShips;
	
	
	//AI command booleans
		public boolean isPatrol;
		public boolean isAttacking;
		public boolean isDeffending;
	
		public boolean auriMove;
		public boolean coagulateTrue = true;
	
	//AI combate booleans
		public boolean inCombat;
	
	//vision booleans
		public boolean noneSpotted;
		public boolean enemySpotted;
	
	Thread r;
	
	public Auri()
	{
		//never do this
	}
	
	public Auri(ArrayList<Fleet>fleet1)
	{	
		//initiate
			auriFleets = fleet1;
			scoutShips = new Fleet();
		
		//fill fleets
			sortFrigates();
			//findFleetCommand(auriFleets.get(0));
			//findFleetCommand(scoutShips);
		
		//add fleets to auriFleets
			//auriFleets.add(auriFleet);
			//auriFleets.add(scoutShips);
		
		//command booleans set
			isPatrol = false;
			isAttacking = false;
			isDeffending = false;
			auriMove = true;
		
		//vision booleans set
			noneSpotted = true;
			enemySpotted = false;
		
		auriFleetSize = auriFleets.get(0).size();
		findFleetCommand(auriFleets.get(0));
		
		test_X = auriFleets.get(0).fcX;
		test_Y = auriFleets.get(0).fcY;
		
		r = new Thread(this);
		
		moveFrigateClockWork();
	}

	public void run() 
	{	
		while(true)
		{
			command();
			
			try
			{
				Thread.sleep(80);
			}
			catch(InterruptedException e)
			{;}
			
		}
	}
	
//main loop
	public void command() //this is in a loop, so we do not need more loops in the command loop
	{
		checkForDeadShips();
		//findFleetFirePower();
		if(noneSpotted && !Alacrity.deBugStopAll) //if there are no spotted enemy
		{
			//move frigates for vision
			//and relocate capital ships for protection
			//testMove();
			//moveToRadial();
			continueScoutFlight();
			askIfSpotted();
			coagulateTrue = true;
			
		}
		else if(!noneSpotted && enemySpotted && !Alacrity.deBugStopAll) // if we have spottede an enemy
		{
			/*//once we have spotted, find the closest incident
			//have the frigate that is spotted chase but not get into fireRange
			//check collective firepower of auriFleets vs visible ships //// compare using dmg/singleshot
			
			//if auriFleet has more firepower, than collect all capital ships with all unused vision-frigates into one location
			// then move collected fleet towards visible ships
			//when in range change incomabt to true
			
			//else if aurifleet has less firepower
			//group fleet up and move away from the location of all visible ships using random vectors that do
			//not coincide with enemy ships */
			
				findOwnFirePower(); 			//System.out.println("Us: " + currentFirePower);
				findSpottedEnemyFirePower();  	//System.out.println("Them: " + currentPlayerFirePower);
				
				if(currentFirePower >= currentPlayerFirePower) //if auri more powerful
				{
					if(coagulateTrue)
					{
						coagulate();
						checkNearestCoord();
						checkCoag();   ///need to remove spottedShip targets when they no longer have a spotted
					}
					else
					{	
						checkNearestCoord();
						moveToAttack();    ///the second time the target isnt reseting or something
					}
						
				}
				if(currentFirePower < currentPlayerFirePower) // if player more powerful
				{
					
				}
			
			askIfStillSpotted(); //checks if auri loses vision of player
		}
	}

//move methods
	public void testMove(int eX, int eY)
	{	//System.out.println("Auri is Alive");
		//System.out.println(scoutShips.size());
		findFleetCommand(auriFleets.get(1));
		
		//System.out.println(distanceToTarget(auriFleets.get(1)));
		if(!(distanceToTarget(auriFleets.get(1))<=50))
		{
			for(int u = 0; u<auriFleets.get(1).size(); u++)
			{
				if(!(distanceToTarget(auriFleets.get(1))<50))
				{
					auriFleets.get(1).get(u).makeCoords(eX, eY, auriFleets.get(1).fcX, auriFleets.get(1).fcY);
					auriFleets.get(1).get(u).move(eX, eY, auriFleets.get(1).fcX, auriFleets.get(1).fcY);
					auriFleets.get(1).get(u).makeCoords(eX, eY, auriFleets.get(1).fcX, auriFleets.get(1).fcY);
				}
				
			}findFleetCommand(auriFleets.get(1));
		}
		
	}
	
	public void testMove()
	{	//System.out.println("Auri is Alive");
		//System.out.println(scoutShips.size());
		auriFleets.get(1).eX = 500;
		auriFleets.get(1).eY = 200;
		findFleetCommand(auriFleets.get(1));
		
		//System.out.println(distanceToTarget(auriFleets.get(1)));
		if(!(distanceToTarget(auriFleets.get(1))<=50))
		{
			for(int u = 0; u<auriFleets.get(1).size(); u++)
			{
					auriFleets.get(1).get(u).makeCoords(auriFleets.get(1).eX, auriFleets.get(1).eY, auriFleets.get(1).fcX, auriFleets.get(1).fcY);
					auriFleets.get(1).get(u).move(auriFleets.get(1).eX, auriFleets.get(1).eY, auriFleets.get(1).fcX, auriFleets.get(1).fcY);
					auriFleets.get(1).get(u).makeCoords(auriFleets.get(1).eX, auriFleets.get(1).eY, auriFleets.get(1).fcX, auriFleets.get(1).fcY);
				
			}
		findFleetCommand(auriFleets.get(1));
		}
		
	}
	
	public void moveIndividual(Ship ships, int eX, int eY)
	{	
		if(!(distanceToTarget(auriFleets.get(1))<=50))
		{
			//ships.makeCoords(eX, eY, ships.ox, ships.oy);
			//ships.move(eX, eY, ships.ox, ships.oy);
			//ships.makeCoords(eX, eY, ships.ox, ships.oy);
		}
	}
	
	public void moveIndividualShip(Ship ships, int eX, int eY) //use this one
	{
		if(distanceFromShipToTarget(ships, eX, eY)>=30)
		{
			//ships.makeCoords(eX, eY, ships.ox, ships.oy);
			//ships.move(eX, eY, ships.ox, ships.oy);
			//ships.makeCoords(eX, eY, ships.ox, ships.oy);
		}
	}
	
	public void moveToFakeRadial()
	{	
		if(auriFleets.get(1).size()!=0)
		{	findFleetCommand(auriFleets.get(0));
			int count = 0;
			for(int u = 0; u<auriFleets.get(1).size();u++)
			{
				if(count == 0)
				{
					moveIndividual(auriFleets.get(1).get(u), auriFleets.get(1).fcX + 200, auriFleets.get(1).fcY + 200);
				}
				if(count == 1)
				{
					moveIndividual(auriFleets.get(1).get(u), auriFleets.get(1).fcX - 200, auriFleets.get(1).fcY - 200);
				}
				if(count == 2)
				{
					moveIndividual(auriFleets.get(1).get(u), auriFleets.get(1).fcX + 200, auriFleets.get(1).fcY - 200);
				}
				if(count == 3)
				{
					moveIndividual(auriFleets.get(1).get(u), auriFleets.get(1).fcX - 200, auriFleets.get(1).fcY + 200);
					count = 0;
				}
				count ++;
			}
		}
	}
	
	public void moveToRadial()
	{	
		if(auriFleets.get(1).size()!=0)
		{	findFleetCommand(auriFleets.get(0));
			int count = 0;
			for(int u = 0; u<auriFleets.get(1).size();u++)
			{
				if(count == 0)
				{
					moveIndividualShip(auriFleets.get(1).get(u), test_X + 200, test_Y + 200);
				}
				if(count == 1)
				{
					moveIndividualShip(auriFleets.get(1).get(u), test_X - 200, test_Y - 200);
				}
				if(count == 2)
				{
					moveIndividualShip(auriFleets.get(1).get(u), test_X + 200, test_Y - 200);
				}
				if(count == 3)
				{
					moveIndividualShip(auriFleets.get(1).get(u), test_X - 200, test_Y + 200);
					count = 0;
				}
				count ++;
			}
		}
	}
	
	public void moveFrigateClockWork() //this setsup the angle that frigates fly at to scout;
	{
		int clockSize = auriFleets.get(1).size();
		double angle = 360.0 / clockSize;
		//System.out.println(angle);
		for(int u = 1; u<clockSize; u++)
		{
			auriFleets.get(1).get(u).auriAngle = u * angle;
		}
	}
	
	public void continueScoutFlight() // this makes firgates scout along predetermined angles
	{
		for(int u = 0; u<auriFleets.get(1).size(); u++)
		{	
			calcTarg(auriFleets.get(1).get(u)); //calcs the invis targets im sending them after
			auriFleets.get(1).get(u).auriRotate();
			moveIndividualShip(auriFleets.get(1).get(u), bagX, bagY);	//auriFleets.get(1).get(u).auriFrigateScout();		//auriFleets.get(1).get(u).auriFrigateScoutMove();
			auriFleets.get(1).get(u).auriRotate();
		}
	}

	public void coagulate()
	{	findFleetCommand(auriFleets.get(0));
		for(int u = 0; u<auriFleets.get(1).size(); u++)
		{
			if(!(auriFleets.get(1).get(u).isSpotted))
			{
				moveIndividualShip(auriFleets.get(1).get(u), auriFleets.get(0).fcX, auriFleets.get(0).fcY);
			}
			else if(auriFleets.get(1).get(u).isSpotted && auriFleets.get(1).get(u).numInRange(auriFleets.get(1).get(u).spottedShip) >=140)
			{
				//moveIndividualShip(auriFleets.get(1).get(u), auriFleets.get(1).get(u).spottedShip.ox, auriFleets.get(1).get(u).spottedShip.oy);
			}
			else if(auriFleets.get(1).get(u).isSpotted && auriFleets.get(1).get(u).numInRange(auriFleets.get(1).get(u).spottedShip) <130)
			{
				moveIndividualShip(auriFleets.get(1).get(u), auriFleets.get(0).fcX, auriFleets.get(0).fcY);
			}
			if(auriFleets.get(1).get(u).isSpotted)
			{
				auriFleets.get(1).get(u).auriSpotted = true;
			}
			if((!(auriFleets.get(1).get(u).isSpotted)) && distanceFromShipToTarget(auriFleets.get(1).get(u), auriFleets.get(0).fcX, auriFleets.get(0).fcY) <=40)
				auriFleets.get(1).get(u).auriCoag = true;
		}
	}
	
	public void moveToAttack() //when gathered, move to attack
	{
		for(int u = 0; u<auriFleets.size(); u++)
		{
			for(int i = 0; i<auriFleets.get(u).size(); i++)
			{
				if(!(auriFleets.get(u).get(i).auriSpotted))  
				{	
					int rngX1 = (int)(Math.random()*(30-1));
					int rngY1 = (int)(Math.random()*(30-1));
					moveIndividualShip(auriFleets.get(u).get(i), eX1 + rngX1, eY1 + rngY1);
				}
			}
		}
	}
	
//vision methods
	
	public void askIfSpotted()
	{
		if(Alacrity.currentVision == true)
		{
			noneSpotted = false;
			enemySpotted = true;
		}
	}
	
	public void askIfStillSpotted()
	{
		if(Alacrity.currentVision == false)
		{
			noneSpotted = true;
			enemySpotted = false;
		}
	}
	
	
//game methods
	public void checkNearestCoord()
	{
		for(int u = 0; u<auriFleets.get(1).size(); u++)
		{
			//if(auriFleets.get(1).get(u).isSpotted && distanceToTargetFromFleet(auriFleets.get(1), auriFleets.get(1).get(u).spottedShip.ox, auriFleets.get(1).get(u).spottedShip.oy) < distanceToTargetFromFleet(auriFleets.get(1), eX1, eY1) )
			{
				//eX1 = auriFleets.get(1).get(u).spottedShip.ox;
				//eY1 = auriFleets.get(1).get(u).spottedShip.oy;
			}
		}
	}
	
	public void checkCoag()
	{	int yp=0;
		for(int u = 0; u<auriFleets.get(1).size();u++)
		{
			if(!auriFleets.get(1).get(u).auriCoag && !auriFleets.get(1).get(u).isSpotted)
				yp++;
		}
		if(yp==0)
			coagulateTrue = false;
		System.out.println("YES \n" + yp);
	}
	
	public void findOwnFirePower()
	{	currentFirePower = 0;
		for(int u = 0; u<auriFleets.size(); u++)
		{
			for(int i = 0; i<auriFleets.get(u).size(); i++)
			{
				currentFirePower = currentFirePower + auriFleets.get(u).get(i).firePower;
			}
		}
	}
	
	public void findSpottedEnemyFirePower()
	{
		currentPlayerFirePower = Alacrity.spottedPlayerFirePower;
	}
	
	public void calcTarg(Ship ships)
	{int tx = 15;
		if(ships.auriAngle>=180+tx && ships.auriAngle<=360 - tx)
		{
			bagX = 0-2000;
			bagY = (int) (bagX * Math.sin(Math.toRadians(ships.auriAngle) / Math.cos(Math.toRadians(ships.auriAngle))));
		}
		else if(ships.auriAngle<=180-tx && ships.auriAngle >= tx)
		{
			bagX = 2000;
			bagY = (int) (bagX * Math.sin(Math.toRadians(ships.auriAngle) / Math.cos(Math.toRadians(ships.auriAngle)))) ;
		}
		else if(ships.auriAngle<=tx || ships.auriAngle>=360 - tx)
		{
			//bagX = ships.ox;
			bagY = 2000;
		}
		else if(ships.auriAngle<=180+tx || ships.auriAngle>=180-tx)
		{
			//bagX = ships.ox;
			bagY = 0-2000;
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
	
	public void findFleetCommand(Fleet ships)
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

			ships.fcX = totalX/ships.size() ;
			ships.fcY = totalY/ships.size() ;
		}
	}
	
	public int distanceToTarget(Fleet temp)
	{
		findFleetCommand(temp);
		int td = (int)(Math.pow( (Math.pow(temp.fcX-temp.eX,2) + Math.pow(temp.fcY-temp.eY,2)) , .5 ) );
		//System.out.println(temp);
		//System.out.println("Distance: " +td);
		return td;
	}
	
	public int distanceToTargetFromFleet(Fleet temp, int eX, int eY)
	{
		findFleetCommand(temp);
		int td = (int)(Math.pow( (Math.pow(temp.fcX-eX,2) + Math.pow(temp.fcY-eY,2)) , .5 ) );
		return td;
	}
	
	public int distanceFromShipToTarget(Ship ships, int eX, int eY)
	{
		int td = (int)(Math.pow( (Math.pow(ships.ox-eX,2) + Math.pow(ships.oy-eY,2)) , .5 ) );
		return td;
	}

	public void sortFrigates()
	{
		for(int u = 0; u<auriFleets.size(); u++)
		{
			for(int i = 0; i<auriFleets.get(u).size(); i++)
			{
				if(auriFleets.get(u).get(i).shipType == ShipType.FRIGATE)
				{
					scoutShips.add(auriFleets.get(u).get(i));
					//auriFleets.get(u).remove(auriFleets.get(u).get(i)); //might want to remove
				}
			}
			
		}
		auriFleets.add(scoutShips);
	}
	
	public void findFleetFirePower()
	{	fleetFirePower = 0;
		for(int u = 0; u<auriFleets.size(); u++)
		{
			for(int i = 0; i<auriFleets.get(u).size(); i++)
			{
				fleetFirePower = auriFleets.get(u).get(i).firePower;
			}
		}
	}

	public void checkForDeadShips()
	{
		for(int u = 0; u<auriFleets.size(); u++)
		{
			for(int i = 0; i<auriFleets.get(u).size(); i++)
			{
				if(auriFleets.get(u).get(i).currentHP<=0)
					auriFleets.get(u).remove(i);
			}
		}
	}
	
}
