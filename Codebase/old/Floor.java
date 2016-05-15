/* Header */

import java.util.Random;

public class Floor {
    public boolean elevatorPresent = false;
    public int personsWaiting;
    
    public Floor (int p) {
        personsWaiting = p;
    }
    
    public Floor () {
        personsWaiting = 0;
    }
    
    public void generatePeople (int max) { // RNG from 0 to 10 inclusive
        Random r = new Random();
        personsWaiting = r.nextInt(max);
    }
    
    public void setElevatorPresent (boolean bool) {
        elevatorPresent = bool;
    }
}
