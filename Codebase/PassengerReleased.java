package project;
import java.sql.Time;

public class PassengerReleased {
    private PassengerRequest passengerRequest; 
    private Time timeArrived;  // time when the passenger arrived at floor_to
    
    public PassengerReleased(PassengerRequest passengerRequest, Time timeArrived) {

        this.passengerRequest = passengerRequest;
        this.timeArrived = timeArrived;
    }
    
    public PassengerRequest getPassengerRequest() {
        return passengerRequest;
    }
    public Time getTimeArrived() {
        return timeArrived;
    }
    public void setPassengerRequest(PassengerRequest passengerRequest) {
        this.passengerRequest = passengerRequest;
    }
    public void setTimeArrived(Time timeArrived) {
        this.timeArrived = timeArrived;
    }
}
