/* CSE2010 section 04
 * Elevator Project
 *	Charles Smith - smithc2014@my.fit.edu
 *	Borja Canseco - bcanseco2014@my.fit.edu
 *	Liandra M. Bennett - bennettl2014@my.fit.edu
 *	Luke Wiskowski - lwiskowski2014@my.fit.edu
 */
public class Driver {
	public short floors = 1;
	public int currentTime = 0;
	public short currentFloor = 1;
	public short[] floorButtons; 
	public void main (String[] args) {
		floors = Short.parseShort(args[0]);
		String elevatorClass = args[1]; //using this string as a placeholder until I code in the ability to dynamically call classes
		while (true) {
			
		}
	}
	public static void upAndDown() {
		while (currentFloor < floors) {
			if (Person.startFloor == currentFloor) {
				inElevator.add(Person);
			}
			currentFloor++; 
		}
	}
	public void simulation (int ticks) { //ticks references the number of ticks forward you want the simulation to run
		while (ticks > 0) {
			//call elevator class for one tick
			currentTime++; //adds one second to the current time
			ticks--;
		}
	}
}


