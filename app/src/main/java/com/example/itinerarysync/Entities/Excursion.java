package com.example.itinerarysync.Entities;

import androidx.room.Entity;

@Entity(tableName = "excursions")
public class Excursion extends ItineraryItem{
    private int vacationId;

    public Excursion(int id, String title, String startDate, int vacationId){
        super(id, title, startDate);
        this.vacationId=vacationId;
    }

    public int getVacationId(){return vacationId;}
    public void setVacationId(int vacationId){this.vacationId=vacationId;}

    @Override
    public String getSummaryDetails(){
        return "Excursion: " + getTitle() + " | Date: " + getStartDate();
    }
}
