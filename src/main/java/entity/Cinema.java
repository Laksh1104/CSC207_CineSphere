package entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cinema {
    private final int cinemaId;
    private final String cinemaName;
    private final Map<String, List<ShowTime>> showTimesMap;


    public Cinema(int cinemaId, String cinemaName) {
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.showTimesMap = new HashMap<>();
    }

    public int getCinemaId() {
        return cinemaId; }

    public String getCinemaName() {
        return cinemaName; }

    public Map<String, List<ShowTime>> getShowTimesMap() {
        return showTimesMap; }

    public void addShowTime(String versionType, String startTime, String endTime) {
        showTimesMap.computeIfAbsent(versionType, k -> new ArrayList<>())
                .add(new ShowTime(startTime, endTime));
    }

    public Map<String, List<ShowTime>> getAllShowTimesWithVersion() {
        Map<String, List<ShowTime>> allShowTimes = new HashMap<>();
        for (Map.Entry<String, List<ShowTime>> entry : showTimesMap.entrySet()) {
            allShowTimes.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return allShowTimes;
    }
}
