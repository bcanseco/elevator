//package project;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Example of the class extending the elevator. 
 * Current implementation works by taking one person at a time.
 * @author Ivan Bogun
 *
 */
public class OnePersonElevator extends Elevator{

	public OnePersonElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose) {
		super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);

	}


	public void initialize(Queue<PassengerRequest> requests) {
		servingQueue = requests;
	}
	
	public ArrayList<PassengerReleased> move() {
		if (!continueOperate()) return null;
		
		ArrayList<PassengerReleased> released =
					new ArrayList<PassengerReleased>();
		
		PassengerRequest request = this.servingQueue.remove();
		
		long currentTime = Math.max(this.currentTime.getTime(),
				request.getTimePressedButton().getTime()); // whichever happened latest
		
		long timeInMiliseconds = currentTime + 
				this.doorDelta*1000 +  // delta to open AND close the door to let the passenger in
				1000*this.timeMoveOneFloor* (Math.abs(currentFloor -request.getFloorFrom()))+
				// time to get to the passenger's floor
				1000*this.timeMoveOneFloor* (Math.abs(request.getFloorFrom() -request.getFloorTo()))+
				// time to move to the destination
				this.doorDelta*1000; // delta to open AND close the door to let the passenger out

		PassengerReleased requestReleased = new PassengerReleased(request, 
				new Time(timeInMiliseconds));
		

		
		released.add(requestReleased);
		if (verbose) {
			StringBuilder sb = new StringBuilder();
			sb.append(currentFloor+" / " + request.getFloorFrom() + " / " +request.getFloorTo()+" | ");
			sb.append(request.getTimePressedButton() + " / " +
					new Time(timeInMiliseconds));
			
			System.out.println(new String(sb));
		}
		this.currentTime.setTime(timeInMiliseconds);
		currentFloor = request.getFloorTo();
		return released;
	}

	public boolean continueOperate() {
		if(this.servingQueue.isEmpty()) {
			return false;
		} else{
			return true;
		}
	}
	
	public ArrayList<PassengerReleased> operate() {
		ArrayList<PassengerReleased> released = new ArrayList<PassengerReleased>();
		if (verbose) {
			System.out.println("Floor at / floor from / floor to | Requested / arrived");
		}
		
		while (this.continueOperate()) {
			ArrayList<PassengerReleased> moved = this.move();
			released.addAll(moved);
		}
		return released;
	}

	
	@SuppressWarnings("deprecation")
    public static void main(String[] args) {
		Random rnd= new Random(0);
		int maxWeight = 250;                   // max is 250 lbs
		int capacity = maxWeight*4;
		Time startingTime = new Time(8,0,0);   // 8 am
		int floors = 9;
		boolean verbose = true;
		int secondsPerFloor = 10;
		int timeOpenDoors = 15; // 15 seconds to open the door
		long currentTime = startingTime.getTime();
		int maxSeconds = 200;
		int numberOfPassangers = 15;  		   // number of passengers to generate
		Queue<PassengerRequest> elevatorQueue = new LinkedList<PassengerRequest>();
		
		for (int i = 0; i < numberOfPassangers; i++) {
			int floor_from =rnd.nextInt(floors) + 1; // to generate numbers from [1, floords]
			int floor_to   = rnd.nextInt(floors) + 1; 
			
			int weight = rnd.nextInt(maxWeight);
			
			int seconds = rnd.nextInt(maxSeconds);
			currentTime+=seconds * 1000;
			Time time = new Time(currentTime);
			PassengerRequest request = new PassengerRequest(time, floor_from, 
					floor_to, weight);
			elevatorQueue.add(request);
		}
		
		Elevator elevator = new OnePersonElevator(capacity,
				secondsPerFloor,floors, timeOpenDoors, verbose);
		
		elevator.initialize(elevatorQueue);
		
		ArrayList<PassengerReleased> output = elevator.operate();
		
		long cost = 0;
		
		for (int i = 0; i < output.size(); i++) {
			
			PassengerReleased passenger = output.get(i);
			Time timeRequested = passenger.getTimeArrived();
			Time timeLeft = passenger.getPassengerRequest().getTimePressedButton();

			cost+=Math.abs(timeLeft.getTime() - timeRequested.getTime());
		}
		
		System.out.println("Total cost (in seconds): " + cost/ 1000);
	}

}
