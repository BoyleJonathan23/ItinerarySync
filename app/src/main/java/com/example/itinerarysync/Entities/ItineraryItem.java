package com.example.itinerarysync.Entities;

import androidx.room.PrimaryKey;

public abstract class ItineraryItem{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String startDate;

    public ItineraryItem(int id, String title, String startDate) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
    }

    public int getId(){return id;}
    public void setId(int id){this.id=id;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public String getStartDate(){return startDate;}
    public void setStartDate(String startDate){this.startDate=startDate;}
    public abstract String getSummaryDetails();
}

