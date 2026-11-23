package com.example.itinerarysync.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class DateUtils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

    private static final DateTimeFormatter FLEXIBLE_PARSER = new DateTimeFormatterBuilder()
            .appendPattern("[MM/dd/yyyy][M/d/yyyy][MM/d/yyyy][M/dd/yyyy]")
            .toFormatter(Locale.US);

    private DateUtils() {
    }

    public static LocalDate parse(String text) throws DateTimeParseException {
        if (text == null) {
            throw new DateTimeParseException("Date string is null", "", 0);
        }
        return LocalDate.parse(text.trim(), FLEXIBLE_PARSER);
    }

    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    public static void showDatePicker(Context context, EditText targetEditText) {
        LocalDate selectedInitialDate = LocalDate.now();
        String currentText = targetEditText.getText() != null ? targetEditText.getText().toString().trim() : "";
        if (!currentText.isEmpty()) {
            try {
                selectedInitialDate = parse(currentText);
            } catch (DateTimeParseException ignored) {
            }
        }
        final LocalDate initialDate = selectedInitialDate;

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selected = LocalDate.of(year, month + 1, dayOfMonth);
                    targetEditText.setText(format(selected));
                    targetEditText.setError(null);
                },
                initialDate.getYear(),
                initialDate.getMonthValue() - 1,
                initialDate.getDayOfMonth()
        );
        dialog.show();
    }

    public static void scheduleAlert(Context context, LocalDate targetDate, String title, String message, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, MyReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerMillis = targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
            }
        } catch (SecurityException e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
        }
    }
}
