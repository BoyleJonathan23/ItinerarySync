package com.example.itinerarysync.Entities;

import androidx.room.Entity;

@Entity(tableName = "vacations")
public class Vacation extends ItineraryItem{
    private String hotel;
    private String endDate;

    public Vacation(int id, String title, String startDate, String hotel, String endDate){
        super(id, title, startDate);
        this.hotel=hotel;
        this.endDate=endDate;
    }

    public String getHotel(){return hotel;}
    public void setHotel(String hotel){this.hotel=hotel;}
    public String getEndDate(){return endDate;}
    public void setEndDate(String endDate){this.endDate=endDate;}

    @Override
    public String getSummaryDetails(){
        return "Vacation: " + getTitle() + " | Hotel: " + hotel + " (" + getStartDate() + " - " + endDate + ")";
    }
}
