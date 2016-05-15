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

import java.util.LinkedList;
import java.util.Queue;

public class Visual {
    public static int[] pplPerFloor;
    public static Queue<PassengerRequest> servingQueue;
    public static int totalFloors;
    public static int inElevator;
    public static int currentFloor;
    public static Queue<Integer> tasks;
    private static int delay = 100;

    public static void simulation(int f, Queue<PassengerRequest> requests,
            Queue<Integer> visualQueue) throws InterruptedException {
        totalFloors = f;
        pplPerFloor = new int[totalFloors + 1];
        tasks = visualQueue;
        servingQueue = new LinkedList<PassengerRequest>();
        currentFloor = 1;
        inElevator = 0;

        for (PassengerRequest passenger : requests) { // creating deep copy
            servingQueue.add(new PassengerRequest(passenger
                    .getTimePressedButton(), passenger.getFloorFrom(),
                    passenger.getFloorTo(), passenger.getWeight()));
            pplPerFloor[passenger.getFloorFrom()]++;
        }

        drawFloors();
        drawFloorNumbers();

        while (!tasks.isEmpty()) {
            int currentTask = tasks.poll();

            switch (currentTask) {
            case 0: // descend
                descend();
                currentFloor--;
                break;
            case 1: // ascend
                ascend();
                currentFloor++;
                break;
            case 2: // load
                pplPerFloor[currentFloor]--;
                updateFloorNumber();
                inElevator++;
                break;
            case 3: // release
                inElevator--;
                break;
            }
        }
        
        if (currentFloor >= totalFloors / 2) {
            descend();
        } else {
            ascend();
        }
        
        System.out.println("Finished showing simulation!");
    }

    public static void descend() throws InterruptedException {
        double s = adjust(totalFloors); // scale

        int beg = currentFloor - 1;
        int end = currentFloor;
        
        for (int i = end - 1; i > beg - 2; i--) {
            wipeNumElev(i);
            StdDraw.setPenColor(StdDraw.RED);
            
            // don't draw red line on roof
            if (!(end == totalFloors && i == end - 1)) {
                StdDraw.line(-1 / s, (-5 + 2 + i) / s, 1 / s, (-5 + 2 + i) / s); // top
            }
            StdDraw.line(-1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 1 + i) / s); // bottom
            StdDraw.line(-1 / s, (-5 + 1 + i) / s, -1 / s, (-5 + 2 + i) / s); // left
            StdDraw.line(1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 2 + i) / s); // right
            
            drawPerson(-.5, (-5.5 + 2 + i));
            numbersInElevator(i);

            Thread.sleep(delay);

            if (i != beg - 1) {
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.line(-1 / s, (-5 + 1 + i) / s, -1 / s, (-5 + 2 + i) / s); // left
                StdDraw.line(1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 2 + i) / s); // right
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.line(-1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 1 + i) / s); // bottom
                if (!(end == totalFloors && i == end - 1)) { // don't wipe roof
                    StdDraw.line(-1 / s, (-5 + 2 + i) / s, 1 / s, (-5 + 2 + i) / s); // top
                }
                erasePerson(-.4, (-5.5 + 2 + i));
                wipeNumElev(i);
            }
        }
    }
    
    public static void ascend() throws InterruptedException {
        double s = adjust(totalFloors); // scale

        int beg = currentFloor;
        int end = currentFloor + 1;

        for (int i = beg - 1; i < end; i++) {
            wipeNumElev(i);
            StdDraw.setPenColor(StdDraw.RED);

            // don't draw red line on roof
            if (!(end == totalFloors && i == beg)) {
                StdDraw.line(-1 / s, (-5 + 2 + i) / s, 1 / s, (-5 + 2 + i) / s); // top
            }
            StdDraw.line(-1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 1 + i) / s); // bottom
            StdDraw.line(-1 / s, (-5 + 1 + i) / s, -1 / s, (-5 + 2 + i) / s); // left
            StdDraw.line(1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 2 + i) / s); // right
            
            drawPerson(-.5, (-5.5 + 2 + i));
            numbersInElevator(i);

            Thread.sleep(delay);

            if (i != end - 1) {
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.line(-1 / s, (-5 + 1 + i) / s, -1 / s, (-5 + 2 + i) / s); // left
                StdDraw.line(1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 2 + i) / s); // right
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.line(-1 / s, (-5 + 1 + i) / s, 1 / s, (-5 + 1 + i) / s); // bottom
                StdDraw.line(-1 / s, (-5 + 2 + i) / s, 1 / s, (-5 + 2 + i) / s); // top
                
                erasePerson(-.4, (-5.5 + 2 + i));
                wipeNumElev(i);
            }
        }
    }

    public static void drawFloors() {
        StdDraw.setPenRadius(0.05);
        StdDraw.setCanvasSize(600, 600);
        StdDraw.setXscale(-6, 6);
        StdDraw.setYscale(-5, 8);

        double scale = adjust(totalFloors);
        for (int i = 0; i < totalFloors - 1; i++) { // set for floors of
                                                    // elevator
            StdDraw.line(1 / scale, (-5 + 1 + i) / scale, 3 / scale,
                    (-5 + 1 + i) / scale); // bottom
            StdDraw.line(1 / scale, (-5 + 3 + i) / scale, 3 / scale,
                    (-5 + 3 + i) / scale); // top
            StdDraw.line(1 / scale, (-5 + 1 + i) / scale, 1 / scale,
                    (-5 + 3 + i) / scale); // left
            StdDraw.line(3 / scale, (-5 + 1 + i) / scale, 3 / scale,
                    (-5 + 3 + i) / scale); // right

            if (pplPerFloor[i + 1] != 0) drawPerson(-2.5, (-5.5 + 2 + i));
            StdDraw.line(-1 / scale, (-5 + 1 + i) / scale, -3 / scale,
                    (-5 + 1 + i) / scale); // bottom
            StdDraw.line(-1 / scale, (-5 + 3 + i) / scale, -3 / scale,
                    (-5 + 3 + i) / scale); // top
            StdDraw.line(-1 / scale, (-5 + 1 + i) / scale, -1 / scale,
                    (-5 + 3 + i) / scale); // left
            StdDraw.line(-3 / scale, (-5 + 1 + i) / scale, -3 / scale,
                    (-5 + 3 + i) / scale); // right
            if (i == totalFloors - 2) {
                StdDraw.line(-3 / scale, (-5 + 3 + i) / scale, 3 / scale,
                        (-5 + 3 + i) / scale); // top
                StdDraw.line(-3 / scale, (-5 + 3 + i) / scale, 0 / scale,
                        (1 + i) / scale);
                StdDraw.line(0 / scale, (1 + i) / scale, 3 / scale,
                        (-5 + 3 + i) / scale);
            }
        }
        drawPerson(-2.5, (-5.5 + 2 + totalFloors - 1));
    }

    public static void drawFloorNumbers() {
        double scale = adjust(totalFloors);
        String num = "";
        for (int i = 0; i < totalFloors; i++) {
            if (pplPerFloor[i + 1] >= 10) {
                num = "" + pplPerFloor[i + 1];
                StdDraw.text1(-1.5 / scale, ((-5.5 + 2 + i) / scale) - .15, num, scale,
                        true);
            } else if (pplPerFloor[i + 1] == 0) {
                // do nothing
            } else {
                num = "" + pplPerFloor[i + 1];
                StdDraw.text1(-1.5 / scale, ((-5.5 + 2 + i) / scale) -.15, num, scale,
                        false);
            }

        }
    }

    public static void updateFloorNumber() {
        StdDraw.setPenColor(StdDraw.WHITE);
        double scale = adjust(totalFloors);
        
        StdDraw.filledSquare(-1.5 / scale,
                ((-1 + (-5 + 3 + currentFloor - 1)) + (-5 + 1 + currentFloor - 1))
                        / (scale * 2.0) -.02, .4 / scale);
        
        if (pplPerFloor[currentFloor] > 0) {
            int i = currentFloor - 1;
            String num = "" + pplPerFloor[currentFloor];
            StdDraw.setPenColor(StdDraw.GRAY);
            StdDraw.text1(-1.5 / scale, ((-5.5 + 2 + i) / scale) -.15, num, scale, false);
            return;
        }
        
        //Delete person
        StdDraw.filledSquare(-2.3 / scale - .2, (((-5.3 + 2 + currentFloor - 1)
                - .08) / scale) - .2, .5 / scale - .1);
        
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.line(-1 / scale, (-5 + 1 + currentFloor - 1) / scale, -3 / scale,
                (-5 + 1 + currentFloor - 1) / scale); // restore floor beneath person
    }
    
    public static void numbersInElevator(int curfloor) {
        double scale = adjust(totalFloors);
        String num = "" + inElevator;
        StdDraw.text1(0 / scale, (-5.5 + 1.8 + curfloor) / scale, num, scale,
                false);
    }

    public static void wipeNumElev(int curfloor) {
        StdDraw.setPenColor(StdDraw.WHITE);
        double scale = adjust(totalFloors);
        StdDraw.filledSquare(0 / scale,
                (2 + (-1 + (-5 + 3 + curfloor - 1)) + (-5 + 1 + curfloor - 1))
                        / (scale * 2), .45 / scale);
    }

    public static double adjust(int n) {
        double scale = 1.0;
        if (n <= 11) {
            scale = 1.0;
        } else if (n <= 21) {
            scale = 2.0;
        } else if (n <= 31) {
            scale = 3.0;
        } else if (n <= 41) {
            scale = 4.0;
        } else if (n <= 51) {
            scale = 5.0;
        } else if (n <= 61) {
            scale = 6.0;
        } else if (n <= 71) {
            scale = 7.0;
        } else if (n <= 81) {
            scale = 8.0;
        } else if (n <= 91) {
            scale = 9.0;
        } else if (n <= 101) {
            scale = 10.0;
        }
        return scale * 1.2;
    }

    public static void drawPerson(double x0, double y0) { 
        // enter coordinates for position
        double s = adjust(totalFloors); // scale
        StdDraw.setPenColor();
        StdDraw.filledCircle(x0 / s, y0 / s, .1 / s); // head
        StdDraw.line(x0 / s, y0 / s, x0 / s, (y0 - .4) / s); // body
        StdDraw.line(x0 / s, (y0 - .4) / s, (x0 + .15) / s, (y0 - .4 - .1) / s);
        StdDraw.line(x0 / s, (y0 - .4) / s, (x0 - .15) / s, (y0 - .4 - .1) / s);
        StdDraw.line((x0 - .1) / s, (y0 - .2) / s, (x0 + .1) / s, (y0 - .2) / s);
    }

    public static void erasePerson(double x0, double y0) { 
        // enter coordinates for position
        double scale = adjust(totalFloors);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledSquare(x0 / scale, (y0 - .2) / scale, .5 / scale);
    }

}