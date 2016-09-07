package cn.ericweb.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import cn.ericweb.timetable.domain.ClassTable;
import cn.ericweb.timetable.domain.Course;
import cn.ericweb.timetable.domain.CourseInClassTable;
import cn.ericweb.timetable.domain.DayInClassTable;
import cn.ericweb.timetable.domain.Teacher;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        myToolbar.setSubtitle(R.string.version_info);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorTitle));
        myToolbar.setSubtitleTextColor(getResources().getColor(R.color.colorTitle));
        setSupportActionBar(myToolbar);


        //check if table already exist
        SharedPreferences sharedPref = this.getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        if (!sharedPref.contains("classtable")) {
            Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
            startActivity(intent);
        } else {
            ScrollView scrollView = (ScrollView) findViewById(R.id.main_scroll_view);
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setPadding(16, 0, 16, 0);
            tableLayout.setShrinkAllColumns(true);

            // 标题
            TableRow tableTitleRow = new TableRow(this);
            TextView tableTitleTextView = new TextView(this);
            tableTitleTextView.setText("title");
            tableTitleTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tableTitleTextView.setGravity(Gravity.CENTER);
            tableTitleRow.addView(tableTitleTextView);
            tableLayout.addView(tableTitleRow);

            // 课程表
            Gson gson = new Gson();
            ClassTable classTable = gson.fromJson(sharedPref.getString("classtable", ""), ClassTable.class);
            for (int classIndex = 0; classIndex < classTable.getCourseNumberPerDay(); classIndex++) {
                TableRow row = new TableRow(this);
                for (int day = 0; day < 5; day++) {
                    TextView aClass = new TextView(this);
                    aClass.setGravity(Gravity.CENTER);
                    aClass.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    try {
                        String temp = classTable.getCourseInClassTable(day, classIndex).toString();
                        aClass.setText(temp);
                    } catch (Exception e) {
                        aClass.setText(" ");
                    }
                    row.addView(aClass);
                }
                tableLayout.addView(row);
            }

            scrollView.addView(tableLayout);
        }
    }
}
