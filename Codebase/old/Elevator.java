/* Header */

public class Elevator {
    private Floor[] elevatorShaft; // Array of floors (instances of the Floor class)
    public final int maxOccupancy; // Currently unused
    public int currentFloor = 0;
    
    public Elevator (int maxOccupancy, int floorCount) {
        this.maxOccupancy = maxOccupancy;
        elevatorShaft = new Floor[floorCount];
        
        for (int i = 0; i < elevatorShaft.length; i++) {
            elevatorShaft[i] = new Floor(); // Initializing array with floor objects
            elevatorShaft[i].generatePeople(10); // RNG from 0 to 10 inclusive
        }
        
        elevatorShaft[0].setElevatorPresent(true); // Elevator starts at lobby
    }
    
    /*** Movement ***/
    public void ascend () {
        elevatorShaft[currentFloor].setElevatorPresent(false);
        elevatorShaft[++currentFloor].setElevatorPresent(true);
    }
    
    public void descend () {
        elevatorShaft[currentFloor].setElevatorPresent(false);
        elevatorShaft[--currentFloor].setElevatorPresent(true);
    }
    /****************/
    
    public String toString() {
        /* Temporary: for System.out.println(<Elevator>); */
        return "This elevator is on floor " + currentFloor +
                " where " + elevatorShaft[currentFloor].personsWaiting +
                " people are waiting.";
    }
}
