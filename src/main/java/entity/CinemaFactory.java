package entity;

public class CinemaFactory {
    public Cinema create(int id, String name) {
        return new Cinema(id, name);
    }
}