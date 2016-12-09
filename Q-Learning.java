/**
*General Algorithim:
* - Initialize Q Values (Q(s,a)) arbitrarily for all state-action pairs.
* - For life or until learning stopped:
*	Choose action (a) in current world state (s)
*	based on current Q Value estimates (Q(s,*))
*	Take action (a) and observe outcome state (s') and reward (r)
*	Update Q(s,a) := Q(s,a) + alpha[r + gamma*max Q(s',a')-Q(s,a)]
*/

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;

public class QLearning {
	static ColorSensor colorSensor = new ColorSensor(SensorPort.S1);
	

	private static boolean isBlack() {
		int red = colorSensor.getColor().getRed();
		int green = colorSensor.getColor().getGreen();
		int blue = colorSensor.getColor().getBlue();
		return (red <= 75)&&(green <= 65)&&(blue <= 55);
	}
	
	private static boolean isYellow() {
		int red = colorSensor.getColor().getRed();
		int green = colorSensor.getColor().getGreen();
		int blue = colorSensor.getColor().getBlue();
		return (red >= 255)&&(green >= 210)&&(blue <= 155);
	}
	
	private static boolean isBlue() {
		int red = colorSensor.getColor().getRed();
		int green = colorSensor.getColor().getGreen();
		int blue = colorSensor.getColor().getBlue();
		return (red <= 165)&&(green >= 175)&&(blue >= 220);
	}

	public static int getState() {
		if (isBlack()) { return 3; }	//Static obstacle.
		else if (isYellow()) { return 2; }	//Variable obstacle.
		else if (isBlue()) { return 1; }	//Goal!
		else { return 0; }	//Open space.
	}
	
	public static void execute(int action, int state) {
		//function to move and turn...etc.
		switch(action) {
		case 0:
			forward(500); 		//500 works well
			stop();
			break;
		case 1:
			turnRight(200);		//200 works well
			stop();
			break;
		case 2:
			turnLeft(200);		//200 works well
			stop();
			break;
		case 3:
			backward(500);		//300 works well
			stop();
			break;
		default:
			break;
		}
	}

	public static int getReward(int state) {
		int reward;
		switch(state) {
		case 0:
			reward = 10; //arbitrary value for white spaces
			break;
		case 1:
			reward = 100; //goal!
			break;
		case 2:
			reward = -50; //variable obstacle
			break;
		case 3:
			reward = -100; //static obstacle
		default:
			reward = 0;
			break;
		}
		return reward;
	}
	
	public static void forward(int length){
		Motor.B.forward();
		Motor.C.forward();
		try {
			Thread.sleep(length);
		} catch(Exception e){};
	}

	public static void backward(int length){
		Motor.B.backward();
		Motor.C.backward();
		try {
			Thread.sleep(length);
		} catch (Exception e) {};
	}

	public static void turnLeft(int length){
		Motor.B.forward();
		Motor.C.backward();
		try {
			Thread.sleep(length);
		} catch (Exception e) {};
	}

	public static void turnRight(int length){
		Motor.B.backward();
		Motor.C.forward();
		try {
			Thread.sleep(length);
		} catch (Exception e) {};
	}
	
	public static void stop(){   
		Motor.B.stop(true);   
		Motor.C.stop();  
	}

	public static void main(String[] args) throws InterruptedException {
		int state = 0;
		int action;
		int nextState;
		int r;
		int randNum;	
		int max;	//Baseline utility.
		int c;		//Will be used to represent actions.
		
		//This will be adjusted as agent learns.
		int[][] QVALUES = {{-4,0,-3,0},{0,0,0,0},{-7,-13,-12,-3},{1,0,0,0}}; //Works well, should be initialized at 0s.
		//First value = states; Second value = actions.
		//0,0	0,1	0,2	0,3
		//1,0	1,1	1,2	1,3			
		//2,0	2,1	2,2	2,3
		//3,0	3,1	3,2	3,3

		double alpha = 0.1;			//Learning rate.
		double discount = 0.5; 		//relative value of short term success versus long term success.
		int exploration = 2;		//rate at which robot will randomly move despite potentially better options.

		//Loops until it finds the goal state.
		while (state != 1) {
			state = getState();
			max = -30000;		//arbitrary value to begin
			action = 4;			//default action.

			//Cycle through the array values for current state to find which action will give the highest utility.
			for (c=0; c<4;c++) {
				if (QVALUES[state][c] > max) {
					max = QVALUES[state][c];
					action = c;
				}
			}
			randNum = (int) (Math.random()*9);	//10% chance the robot will randomly explore.

			//If no action has been found that is better than the others, or the random number is below exploration
			//threshold, generate a random number between 0 and 3 to represent next action.
			if(action == 4 || randNum + 1 <= exploration) {
				action = (int) (Math.random()*3);
			}
			execute(action, state);
			Thread.sleep(450);
			nextState = getState();
			r = getReward(nextState);
			Thread.sleep(50);

			max = -30000;
			for(c = 0; c < 4; c++) {
				if(QVALUES[nextState][c] > max) {
					max = QVALUES[nextState][c];
				}
			}

			//Update matrix value for initial state and action pair based on reward, previous value, and potential
			//utility values for the current state.
			QVALUES[state][action] = 
					(int)((QVALUES[state][action]) + alpha * (r + (discount * max) - QVALUES[state][action]));
			Thread.sleep(500);
			System.out.println(QVALUES[0][0]+" "+QVALUES[0][1]+" "+QVALUES[0][2]+" "+QVALUES[0][3]);
			System.out.println(QVALUES[1][0]+" "+QVALUES[1][1]+" "+QVALUES[1][2]+" "+QVALUES[1][3]);
			System.out.println(QVALUES[2][0]+" "+QVALUES[2][1]+" "+QVALUES[2][2]+" "+QVALUES[2][3]);
			System.out.println(QVALUES[3][0]+" "+QVALUES[3][1]+" "+QVALUES[3][2]+" "+QVALUES[3][3]);
			
		}	//End while loop
	}
}
