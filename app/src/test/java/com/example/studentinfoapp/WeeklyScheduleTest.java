package com.example.studentinfoapp;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class WeeklyScheduleTest {
    @Test public void classDaysHaveFourDistinctSubjectsAndOtherDaysAreFree() {
        for (int day : new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.THURSDAY, Calendar.FRIDAY}) {
            List<WeeklySchedule.Lesson> classes = WeeklySchedule.forDay(day);
            assertEquals(4, classes.size());
            assertEquals(4, classes.stream().map(lesson -> lesson.subject).collect(Collectors.toSet()).size());
        }
        for (int day : new int[]{Calendar.WEDNESDAY, Calendar.SATURDAY, Calendar.SUNDAY}) {
            assertEquals(0, WeeklySchedule.forDay(day).size());
        }
    }

    @Test public void pickerRunsFromMondayToSunday() {
        int[] days = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
        for (int i = 0; i < days.length; i++) {
            assertEquals(days[i], WeeklySchedule.dayAt(i));
            assertEquals(i, WeeklySchedule.positionOf(days[i]));
        }
    }
}
