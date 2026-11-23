package com.example.itinerarysync;

import com.example.itinerarysync.Entities.Excursion;
import com.example.itinerarysync.Entities.Vacation;
import com.example.itinerarysync.UI.DateUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.*;

public class ItinerarySyncTest {

    @Test
    public void testDateParsingAndFormatting() {
        LocalDate date = DateUtils.parse("05/15/2026");
        assertEquals(2026, date.getYear());
        assertEquals(5, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());

        assertEquals("05/15/2026", DateUtils.format(date));
    }

    @Test
    public void testFlexibleDateParsing() {
        LocalDate date = DateUtils.parse("5/5/2026");
        assertEquals("05/05/2026", DateUtils.format(date));
    }

    @Test(expected = DateTimeParseException.class)
    public void testInvalidDateFormatThrowsException() {
        DateUtils.parse("not-a-date");
    }

    @Test
    public void testVacationDateRangeValidation() {
        LocalDate startDate = DateUtils.parse("06/01/2026");
        LocalDate endDate = DateUtils.parse("06/10/2026");
        assertFalse(endDate.isBefore(startDate));

        LocalDate invalidEndDate = DateUtils.parse("05/25/2026");
        assertTrue(invalidEndDate.isBefore(startDate));
    }

    @Test
    public void testExcursionDateWithinVacationRange() {
        LocalDate vacStart = DateUtils.parse("07/01/2026");
        LocalDate vacEnd = DateUtils.parse("07/15/2026");

        LocalDate excursionExactStart = DateUtils.parse("07/01/2026");
        LocalDate excursionExactEnd = DateUtils.parse("07/15/2026");
        LocalDate excursionMiddle = DateUtils.parse("07/08/2026");
        LocalDate excursionTooEarly = DateUtils.parse("06/30/2026");
        LocalDate excursionTooLate = DateUtils.parse("07/16/2026");

        // Inclusive boundaries
        assertFalse(excursionExactStart.isBefore(vacStart) || excursionExactStart.isAfter(vacEnd));
        assertFalse(excursionExactEnd.isBefore(vacStart) || excursionExactEnd.isAfter(vacEnd));
        assertFalse(excursionMiddle.isBefore(vacStart) || excursionMiddle.isAfter(vacEnd));

        // Out of range
        assertTrue(excursionTooEarly.isBefore(vacStart) || excursionTooEarly.isAfter(vacEnd));
        assertTrue(excursionTooLate.isBefore(vacStart) || excursionTooLate.isAfter(vacEnd));
    }

    @Test
    public void testEntitySummaryDetails() {
        Vacation vacation = new Vacation(1, "Maui Trip", "08/01/2026", "Grand Wailea", "08/07/2026");
        assertEquals("Vacation: Maui Trip | Hotel: Grand Wailea (08/01/2026 - 08/07/2026)", vacation.getSummaryDetails());

        Excursion excursion = new Excursion(10, "Helicopter Tour", "08/03/2026", 1);
        assertEquals("Excursion: Helicopter Tour | Date: 08/03/2026", excursion.getSummaryDetails());
    }

    @Test
    public void testSameDayVacationIsValid() {
        LocalDate startDate = DateUtils.parse("06/01/2026");
        LocalDate endDate = DateUtils.parse("06/01/2026");
        assertFalse(endDate.isBefore(startDate));
    }

    @Test
    public void testWhitespaceTrimmingAndNullDate() {
        LocalDate trimmed = DateUtils.parse("  09/12/2026  ");
        assertEquals("09/12/2026", DateUtils.format(trimmed));

        try {
            DateUtils.parse(null);
            fail("Expected DateTimeParseException for null input");
        } catch (DateTimeParseException expected) {
            // Expected
        }
    }

    @Test(expected = DateTimeParseException.class)
    public void testImpossibleCalendarDateThrowsException() {
        DateUtils.parse("02/30/2026");
    }

    @Test
    public void testVacationSearchMatching() {
        Vacation v1 = new Vacation(1, "Hocking Hills Cabin", "08/19/2026", "Cedar Grove", "08/22/2026");
        Vacation v2 = new Vacation(2, "Indianapolis Weekend", "10/23/2026", "JW Marriott", "10/25/2026");

        String query = "marriott";
        boolean matchesV1 = v1.getTitle().toLowerCase().contains(query) || v1.getHotel().toLowerCase().contains(query);
        boolean matchesV2 = v2.getTitle().toLowerCase().contains(query) || v2.getHotel().toLowerCase().contains(query);

        assertFalse(matchesV1);
        assertTrue(matchesV2);
    }
}
