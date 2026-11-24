package com.example.studentinfoapp;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

final class WeeklySchedule {
    private static final List<Lesson> MONDAY_THURSDAY = Collections.unmodifiableList(Arrays.asList(
            new Lesson(R.string.schedule_mobile_development, R.string.schedule_slot_one_lab),
            new Lesson(R.string.schedule_data_structures, R.string.schedule_slot_two),
            new Lesson(R.string.schedule_databases, R.string.schedule_slot_three),
            new Lesson(R.string.schedule_interaction_design, R.string.schedule_slot_four)));
    private static final List<Lesson> TUESDAY_FRIDAY = Collections.unmodifiableList(Arrays.asList(
            new Lesson(R.string.schedule_discrete_math, R.string.schedule_slot_one_room),
            new Lesson(R.string.schedule_networking, R.string.schedule_slot_two_lab),
            new Lesson(R.string.schedule_communication, R.string.schedule_slot_three),
            new Lesson(R.string.schedule_ethics, R.string.schedule_slot_four)));

    private WeeklySchedule() {}

    static List<Lesson> forDay(int day) {
        switch (day) {
            case Calendar.MONDAY:
            case Calendar.THURSDAY:
                return MONDAY_THURSDAY;
            case Calendar.TUESDAY:
            case Calendar.FRIDAY:
                return TUESDAY_FRIDAY;
            default:
                return Collections.emptyList();
        }
    }

    static int dayAt(int position) {
        return (position + 1) % 7 + Calendar.SUNDAY;
    }

    static int positionOf(int day) {
        return (day - Calendar.MONDAY + 7) % 7;
    }

    static int today() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    static final class Lesson {
        final int subject;
        final int details;

        Lesson(int subject, int details) {
            this.subject = subject;
            this.details = details;
        }
    }
}
