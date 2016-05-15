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

public class AdvancedElevator extends Elevator {
    private ArrayList<PassengerRequest> inElevator;
    private ArrayList<PassengerReleased> outElevator; // only used in toString()
    private Time startingTime; // only used in toString()
    private boolean direction = true; // true = up, false = down
    private boolean isBusy = false; /* true if the elevator is empty and must maintain its direction,
                                       but can pick people up along the way going the same direction */
    private int isBusyUntil = -1; // floor where elevator will stop being "busy"
    private int currentWeight = 0;
    public Queue<Integer> visualQueue = new LinkedList<Integer>(); // for Visual.java
    
    public AdvancedElevator (int capacity, int timeMoveOneFloor, int floors,
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
    
    boolean goingSameDirection(PassengerRequest passenger) {
        if (passenger.getFloorTo() > currentFloor && directionToMove()) {
            return true; // if passenger is above us and we're going up
        } else if (passenger.getFloorTo() < currentFloor && !directionToMove()) {
            return true; // if passenger is below us and we're going down
        }
        return false;
    }
    
    boolean getDestinationDirection(PassengerRequest passenger) {
        return passenger.getFloorTo() > currentFloor; // true if floor is above, false if below
    }
    
    boolean getOriginDirection(PassengerRequest passenger) {
        return passenger.getFloorFrom() > currentFloor;
    }
    
    boolean continueOperate() {
        return !(this.servingQueue.isEmpty() && inElevator.isEmpty());
    }
    
    boolean activeRequestsExist() {
        if (!inElevator.isEmpty()) {
            return true; // active requests exist if people are in the elevator
        }
        
        for (PassengerRequest passenger : servingQueue) {
            Time pressButton = passenger.getTimePressedButton();
            if (currentTime.compareTo(pressButton) >= 0) {
                return true; // active requests exist if someone pushed a button
            }
        }
        return false; // active requests don't exist otherwise
    }
    
    ArrayList<PassengerReleased> move() {
        ArrayList<PassengerReleased> released = new ArrayList<PassengerReleased>();
        boolean peopleGotOff = false; // used with peopleCameIn to make sure we only add doorDelta once
        boolean peopleCameIn = false;
        
        if (isBusy && currentFloor == isBusyUntil) {
            isBusy = false;
            isBusyUntil = -1;
        }
        
        /* Our first priority is to check to see if people want to get off.
           Technically, this would be if the elevator was instructed to stop here
           via the floor buttons inside of it. */
        for (int i = 0; i < inElevator.size(); i++) {
            if (inElevator.get(i).getFloorTo() == currentFloor) {
                if (!peopleGotOff) {
                    addSeconds(doorDelta);
                    peopleGotOff = true;
                }
                
                released.add(new PassengerReleased(
                        inElevator.get(i), new Time(currentTime.getTime())));
                currentWeight -= inElevator.get(i).getWeight(); // adjust elevator weight
                inElevator.remove(i); // remove from elevator
                visualQueue.add(3);
            }
        }
        
        /* Next, we load new passengers in that satisfy these conditions:
            -They have pressed the button already, relative to currentTime
            -They won't exceed the elevator's weight capacity if they get on
            -IF (the elevator is empty and isn't on its way to serve someone)
                -They are going the same direction as the first person to press the button
            -ELSE
                -They are going the same direction as the elevator */
        if (inElevator.isEmpty() && !isBusy) {
            boolean firstPusher = true;
            for (PassengerRequest passenger : servingQueue) {
                Time pressButton = passenger.getTimePressedButton();
                if (currentTime.compareTo(pressButton) < 0) { // if they haven't pressed the button yet
                    break; // nobody left to serve (servingQueue sorted by time pressed button)
                }
                
                if (passenger.getFloorFrom() == currentFloor) {
                    if (firstPusher) {
                        direction = getDestinationDirection(passenger); // first to push chooses where to go
                        firstPusher = false;
                    }
                    if (goingSameDirection(passenger) && !exceedsWeightLimit(passenger)) {
                        inElevator.add(passenger);
                        currentWeight += passenger.getWeight();
                        visualQueue.add(2);
                        peopleCameIn = true;
                    }
                }
            }
        } else {
            for (PassengerRequest passenger : servingQueue) {
                Time pressButton = passenger.getTimePressedButton();
                if (currentTime.compareTo(pressButton) < 0) { // if they haven't pressed the button yet
                    break; // nobody left to serve (servingQueue sorted by time pressed button)
                }
                
                if (passenger.getFloorFrom() == currentFloor) {
                    if (goingSameDirection(passenger) && !exceedsWeightLimit(passenger)) {
                        inElevator.add(passenger);
                        currentWeight += passenger.getWeight();
                        visualQueue.add(2);
                        peopleCameIn = true;
                    }
                }
            }
        }
        for (int i = 0; i < inElevator.size(); i++) {
            servingQueue.remove(inElevator.get(i)); // remove loaded passengers from button pushers
        }
        
        if (!peopleGotOff && peopleCameIn) {
            addSeconds(doorDelta);
        } // at this point, we've finished loading passengers
        
        
        /* Quick check to see if we're done. This must be done to prevent null pointers below. */
        if (!continueOperate()) return released;
        
        
        /* Next, we check if anyone is still inside the elevator or pressed a button */
        if (!activeRequestsExist()) {
            // advance time until someone presses the button if necessary
            currentTime.setTime(servingQueue.peek().getTimePressedButton().getTime());
            return released;
        }
        
        /* At this point, there is at least one person that can be serviced in the
           simulation. They are either in the elevator or on a different floor */
        
        if (inElevator.isEmpty() && !isBusy) { // someone pressed a button somewhere else
            isBusy = true;
            isBusyUntil = servingQueue.peek().getFloorFrom();
            direction = getOriginDirection(servingQueue.peek());
        }
        if (directionToMove()) {
            currentFloor++;
            visualQueue.add(1);
        } else {
            currentFloor--;
            visualQueue.add(0);
        }
        addSeconds(timeMoveOneFloor);
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
        
        sb.append("Advanced elevator:\n");
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