//package project;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class NewDriver {
    public static boolean verbose = true;
    public static int floors = 9;
    public static int totalPassengers = 15;
    public static int maxWeight = 250;
    public static int capacity = 1000;
    public static int timeMoveOneFloor = 10;
    public static int doorDelta = 15;
    public static int maxSeconds = 200;
    
    @SuppressWarnings("deprecation")
    public static Time start = new Time (8, 0, 0); // (hours, minutes, seconds)
    public static Queue<PassengerRequest> elevatorQueue = new LinkedList<PassengerRequest>();
    
    public static void useCustomValues() {
        Scanner kb = new Scanner(System.in);
        
        System.out.print("Use default values? Y / N: ");
        if (kb.next().equalsIgnoreCase("N")) {
            System.out.print("\nVerbose? 1 / 0: ");
            verbose = kb.nextInt() == 1 ? true : false;
            System.out.print("\nFloors: ");
            floors = kb.nextInt();
            System.out.print("\nTotal passengers: ");
            totalPassengers = kb.nextInt();
            System.out.print("\nMax passenger weight: ");
            maxWeight = kb.nextInt();
            System.out.print("\nCapacity: ");
            capacity = kb.nextInt();
            System.out.print("\nTime to move one floor: ");
            timeMoveOneFloor = kb.nextInt();
            System.out.print("\nTime to open & close doors: ");
            doorDelta = kb.nextInt();
            System.out.print("\nUpper bound for random time"
                    + " interval between button presses: ");
            maxSeconds = kb.nextInt();
            System.out.println();
        }
        kb.close();
    }
    
    public static void generatePassengers() {
        Random random = new Random(0);
        long currentTime = start.getTime();
        
        elevatorQueue.clear();
        for (int i = 0; i < totalPassengers; i++) {
            int floor_from = random.nextInt(floors) + 1;
            int floor_to   = random.nextInt(floors) + 1; 
            
            int weight = random.nextInt(maxWeight);
            int seconds = random.nextInt(maxSeconds);
            
            currentTime += seconds * 1000;

            PassengerRequest request = new PassengerRequest
                    (new Time(currentTime), floor_from, floor_to, weight);
            
            elevatorQueue.add(request);
        }
    }
    
    public static void main (String[] args) {
        ElevatorGUI.main(null); // experimental
        
        useCustomValues(); // ask user if they want to change #floors, capacity, etc.
        generatePassengers(); // fill elevatorQueue with passengers
        
        // Dummy Elevator
        System.out.println("Dummy elevator:");
        Elevator basicElevator = new MultiPersonElevator
                (capacity, timeMoveOneFloor, floors, doorDelta, verbose, start);
        basicElevator.initialize(elevatorQueue);
        basicElevator.operate();
        if (verbose) System.out.println(basicElevator);
        
        // Smart Elevator
        
    }
}
