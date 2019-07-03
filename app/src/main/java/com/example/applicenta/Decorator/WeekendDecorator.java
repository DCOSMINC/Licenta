package com.example.applicenta.Decorator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.applicenta.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class WeekendDecorator implements DayViewDecorator {

    private static final String TAG = "WeekendDecorator";

    private Drawable drawable;

    public WeekendDecorator(Context context) {
        drawable = ContextCompat.getDrawable(context , R.drawable.red_circle);
    }

    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        Log.d(TAG, "shouldDecorate: " + calendarDay.toString());
        if(calendarDay.getDate().getDayOfWeek().getValue() == Calendar.FRIDAY || calendarDay.getDate().getDayOfWeek().getValue() == Calendar.SATURDAY
                || calendarDay.equals(CalendarDay.today()))
            return true;
        return false;
    }

    @Override
    public void decorate(DayViewFacade dayViewFacade) {
        //dayViewFacade.addSpan(new BackgroundColorSpan(Color.RED));
        dayViewFacade.setBackgroundDrawable(drawable);
        dayViewFacade.setDaysDisabled(true);
    }
}
