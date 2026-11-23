package com.example.itinerarysync.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.itinerarysync.Entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId ORDER BY id ASC")
    List<Excursion> getAssociatedExcursions(int vacationId);

    @Query("SELECT * FROM excursions ORDER BY id ASC")
    List<Excursion> getAllExcursions();
}
