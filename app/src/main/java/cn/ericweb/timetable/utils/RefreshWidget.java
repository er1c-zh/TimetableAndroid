package cn.ericweb.timetable.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import cn.ericweb.timetable.ClasstableWidget;

/**
 * Created by Eric on 2016/10/28.
 */

public class RefreshWidget {
    public static void updateWidgetClasstable(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, ClasstableWidget.class));
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(ClasstableWidget.WIDGET_CLASSTABLE_KEY_IDS, ids);

        context.sendBroadcast(intent);
    }
}
