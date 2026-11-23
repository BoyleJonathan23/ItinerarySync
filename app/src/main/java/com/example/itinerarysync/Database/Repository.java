package com.example.itinerarysync.Database;

import android.app.Application;

import com.example.itinerarysync.DAO.ExcursionDAO;
import com.example.itinerarysync.DAO.VacationDAO;
import com.example.itinerarysync.Entities.Excursion;
import com.example.itinerarysync.Entities.Vacation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final ExcursionDAO excursionDAO;
    private final VacationDAO vacationDAO;

    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        excursionDAO = db.excursionDAO();
        vacationDAO = db.vacationDAO();
    }

    public List<Vacation> getAllVacations() {
        try {
            return databaseExecutor.submit(vacationDAO::getAllVacations).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void insert(Vacation vacation) {
        try {
            databaseExecutor.submit(() ->
                    vacationDAO.insert(vacation)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Vacation vacation) {
        try {
            databaseExecutor.submit(() ->
                    vacationDAO.update(vacation)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Vacation vacation) {
        try {
            databaseExecutor.submit(() ->
                    vacationDAO.delete(vacation)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(Excursion excursion) {
        try {
            databaseExecutor.submit(() ->
                    excursionDAO.insert(excursion)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Excursion excursion) {
        try {
            databaseExecutor.submit(() ->
                    excursionDAO.update(excursion)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Excursion excursion) {
        try {
            databaseExecutor.submit(() ->
                    excursionDAO.delete(excursion)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Excursion> getAssociatedExcursions(int vacationId) {
        try {
            return databaseExecutor.submit(() -> excursionDAO.getAssociatedExcursions(vacationId)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Excursion> getAllExcursions() {
        try {
            return databaseExecutor.submit(excursionDAO::getAllExcursions).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
