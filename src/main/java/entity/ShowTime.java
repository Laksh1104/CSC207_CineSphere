package entity;

public class ShowTime {
    private final String startTime;
    private final String endTime;

    public ShowTime(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }
}