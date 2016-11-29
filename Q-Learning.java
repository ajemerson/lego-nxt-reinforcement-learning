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
		if (isBlack()) { return 3; }
		else if (isYellow()) { return 2; }
		else if (isBlue()) { return 1; }
		else { return 0; }
	}
	
	public static void execute(int action, int state) {
		//function to move and turn...etc.
		switch(action) {
		case 0:
			forward(1300); //figure out value to go forward 1 space
			break;
		case 1:
			break;
		case 2:
			//reverse, turn left/right, move to that space
			backward(1300);
			Motor.B.backward();	//whatever turn angle we decide
			Motor.C.forward();
			try {
				Thread.sleep(600);
				} catch(Exception e){};
			forward(1300);
			break;
		case 3:
			//reverse, turn left/right, move to that space
			backward(1300);
			Motor.B.backward();	//whatever turn angle we decide
			Motor.C.forward();
			try {
				Thread.sleep(600);
				} catch(Exception e){};
			forward(1300);
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
			reward = -50; //static obstacle
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
	
	public static void main(String[] args) throws InterruptedException {
		int state;
		int action;
		int nextState;
		int r;
		int randNum;
		int max;
		int c;
		
		//This will be adjusted as agent learns (more rows/cols)
		int[][] QVALUES = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}; 
		
		double alpha = 0.1;		//Learning rate
		double discount = 0.5; //relative value of short term success versus long term success
		int exploration = 2;	//rate at which robot will randomly move despite potentially better options
		
		while (true) {
			state = getState();
			max = -30000;	//arbitrary value? not sure yet
			action = 4;		//however many actions we choose to implement?
			for (c=0; c<4;c++) {
				if (QVALUES[state][c] > max) {
					max = QVALUES[state][c];
					action = c;
				}
			}
			randNum = (int) (Math.random()*9);

			if(action == 4 || randNum + 1 <= exploration) {
				action = (int) (Math.random()*3);
			}
			execute(action, state);
			Thread.sleep(450);
			nextState = getState();
			r = getReward(nextState);
			Thread.sleep(50);
			
			max = -32000;
			for(c = 0; c < 4; c++) {
				if(QVALUES[nextState][c] > max) {
					max = QVALUES[nextState][c];
				}
			}
			
			QVALUES[state][action] = 
					(int)((QVALUES[state][action]) + alpha * (r + (discount * max) - QVALUES[state][action]));
			
			Thread.sleep(500);
		}
	}
	
}
