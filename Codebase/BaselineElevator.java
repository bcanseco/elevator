package project;

/******************************************************************************
 *  Elevator Term Project
 *  
 *  Algorithms & Data Structures
 *  Florida Institute of Technology
 *  
 *  Luke Wiskowski
 *  Borja Canseco
 *  Liandra Bennett
 *  
 *  Fall 2015
 ******************************************************************************/

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BaselineElevator extends Elevator {
    private ArrayList<PassengerRequest> inElevator;
    private ArrayList<PassengerReleased> outElevator; // only used in toString()
    private Time startingTime; // only used in toString()
    private boolean direction = true; // true = up, false = down
    private int currentWeight = 0;
    public Queue<Integer> visualQueue = new LinkedList<Integer>(); // for Visual.java
    
    public BaselineElevator (int capacity, int timeMoveOneFloor, int floors,
            int doorDelta, boolean verbose, Time start) {
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
        startingTime = start;
        currentTime = new Time(startingTime.getTime());
    }
    
    void initialize(Queue<PassengerRequest> requests) {
        this.servingQueue = new LinkedList<PassengerRequest>();
        for (PassengerRequest passenger : requests) { // creating deep copy
            servingQueue.add(new PassengerRequest(
                    passenger.getTimePressedButton(),
                    passenger.getFloorFrom(),
                    passenger.getFloorTo(),
                    passenger.getWeight()
                ));
        }
        
        this.inElevator = new ArrayList<PassengerRequest>();
    }
    
    void addSeconds(int seconds) {
        long currentTime = this.currentTime.getTime();
        currentTime += seconds * 1000;
        this.currentTime.setTime(currentTime);
    }
    
    boolean exceedsWeightLimit(PassengerRequest passenger) {
        return passenger.getWeight() > capacity - currentWeight;
    }
    
    boolean directionToMove() {
        if (currentFloor == floors && direction) { // if you've ascended to the top
            direction = false; // start going down
        } else if (currentFloor == 1 && !direction) { // if you've descended to the bottom
            direction = true;
        }
        return direction;
    }
    
    boolean continueOperate() {
        return !(this.servingQueue.isEmpty() && inElevator.isEmpty());
    }
    
    boolean stopAtThisFloor() {
        for (PassengerRequest passenger : inElevator) {
            if (passenger.getFloorTo() == currentFloor) {
                return true; // stop at floor if someone inside needs to get off
            }
        }
        
        for (PassengerRequest passenger : servingQueue) {
            if (passenger.getFloorFrom() != currentFloor) continue;
            
            Time pressButton = passenger.getTimePressedButton();
            if (currentTime.compareTo(pressButton) >= 0 && !exceedsWeightLimit(passenger)) {
                return true; // stop at floor if someone there pressed a button
            }
        }
        
        return false;
    }
    
    ArrayList<PassengerReleased> move() {
        if (!continueOperate()) return null;
        
        ArrayList<PassengerReleased> released =
                new ArrayList<PassengerReleased>();
        
        while (!stopAtThisFloor()) { // while elevator is in motion
            if (directionToMove()) { // if we're going up
                currentFloor++;
                visualQueue.add(1);
            } else {                 // if we're going down
                currentFloor--;
                visualQueue.add(0);
            }
            addSeconds(timeMoveOneFloor); // add the time it takes to move floors to currentTime
        }
        
        /* since we used a while loop and not a do-while, the following code
           will be executed on the starting floor as well */
        
        addSeconds(doorDelta);
        
        /* Releasing passengers phase */
        for (int i = 0; i < inElevator.size(); i++) {
            if (inElevator.get(i).getFloorTo() == currentFloor) {
                PassengerReleased personLeaving = new PassengerReleased(
                        inElevator.get(i), new Time(currentTime.getTime()));
                released.add(personLeaving);
                currentWeight -= inElevator.get(i).getWeight();
                inElevator.remove(i);
                visualQueue.add(3);
            }
        }
        /* Finished releasing passengers */
        
        /* Loading passengers phase */
        for (PassengerRequest passenger : servingQueue) {
            Time pressButton = passenger.getTimePressedButton();
            if (currentTime.compareTo(pressButton) < 0) { // if they haven't pressed the button yet
                break; // ignore them
            }
            
            if (passenger.getFloorFrom() == currentFloor && !exceedsWeightLimit(passenger)) {
                inElevator.add(passenger); // load into elevator
                currentWeight += passenger.getWeight();
                visualQueue.add(2);
            }
        }
        for (int i = 0; i < inElevator.size(); i++) {
            servingQueue.remove(inElevator.get(i)); // remove loaded passengers from queue
        }
        /* Finished loading passengers */

        return released;
    }

    public ArrayList<PassengerReleased> operate() {
        ArrayList<PassengerReleased> released = new ArrayList<PassengerReleased>();
        
        while (this.continueOperate()) {
            released.addAll(this.move());
        }
        
        outElevator = released;
        return released;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Baseline elevator:\n");
        if (verbose) sb.append("From / to | Requested / arrived\n");
        
        long timeDifference = 0;
        for (PassengerReleased passenger : outElevator) {
            if (verbose) {
                sb.append(passenger.getPassengerRequest().getFloorFrom() + " / ");
                sb.append(passenger.getPassengerRequest().getFloorTo() + " | ");
                sb.append(passenger.getPassengerRequest().getTimePressedButton() + " / ");
                sb.append(passenger.getTimeArrived() + "\n");
            }
            
            timeDifference += passenger.getTimeArrived().getTime()
                    - passenger.getPassengerRequest().getTimePressedButton().getTime();
        }
        long cost = this.currentTime.getTime() - startingTime.getTime();
        
        timeDifference /= 1000;
        timeDifference /= outElevator.size();
        
        sb.append("Total cost (in seconds): " + cost / 1000);
        sb.append("\nMean wait (in seconds): " + timeDifference);
        
        return sb.toString();
    }
}