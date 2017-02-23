package com.example.abans_000.receiver;

public class Event {

    private String title, description;
    private long start, finish;
    private double latitude, longitude;

    public Event() {

    }

    public Event(String title, String description, long start, long finish, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.start = start;
        this.finish = finish;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setfinish(long finish) {
        this.finish = finish;
    }

    public void setstart(long start) {
        this.start = start;
    }

    public String getTitle() { return title; }
    public String getDescription() {
        return description;
    }
    public long getStart() {
        return start;
    }
    public long getFinish() {
        return finish;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

}