package cn.ericweb.timetable;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.RemoteViews;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.ericweb.timetable.util.AppConstant;

/**
 * Implementation of App Widget functionality.
 */
public class ClasstableWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.classtable_widget);
        SharedPreferences classtableSharedPref = context.getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, Context.MODE_PRIVATE);
        String bitmapString = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_CACHE, "");
        if (!bitmapString.isEmpty()) {
            byte[] bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            bitmap = Bitmap.createBitmap(bitmap);
            views.setImageViewBitmap(R.id.imageView, bitmap);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

