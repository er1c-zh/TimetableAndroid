package cn.ericweb.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.ericweb.timetable.domain.ClassTable;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        myToolbar.setSubtitle(R.string.version_info_simple);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        myToolbar.setSubtitleTextColor(getResources().getColor(R.color.colorText));
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
    }
    @Override
    protected void onResume() {
        super.onResume();
        showTables();
    }

    void showTables() {
        //check if table already exist
        SharedPreferences sharedPref = this.getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        if (!sharedPref.contains("classtable")) {
            Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
            startActivity(intent);
        } else {
            ScrollView scrollView = (ScrollView) findViewById(R.id.main_scroll_view);
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setPadding(16, 16, 16, 16);
            tableLayout.setShrinkAllColumns(true);

            // 课程表
            Gson gson = new Gson();
            ClassTable classTable;
            try {
                classTable = gson.fromJson(sharedPref.getString("classtable", ""), ClassTable.class);
            } catch (Exception e) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("classtable");
                Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
                startActivity(intent);
                return;
            }
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
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }

    //    创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    void refresh() {
        Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
        startActivity(intent);
    }
    void showVersionInfo() {
        Intent intent = new Intent(this, cn.ericweb.timetable.VersionInfo.class);
        startActivity(intent);
    }

    //    菜单click listener
    Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.refresh_classtable:
                    refresh();
                    break;
                case R.id.version_info:
                    showVersionInfo();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
}