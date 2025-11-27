package entity;

public class Seat {
    private String seatName;
    private boolean isbooked;

    public Seat(String seatName) {
        this.seatName = seatName;
        isbooked = false;
    }
    public String getSeatName() {
        return seatName;
    }
    public boolean isBooked() {
        return isbooked;
    }
    public void book(){
        isbooked = true;
    }
}
