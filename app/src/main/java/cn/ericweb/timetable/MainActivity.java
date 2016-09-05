package cn.ericweb.timetable;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

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
        setSupportActionBar(myToolbar);

        result = new TextView(this);
        mainLayout = (ViewGroup) findViewById(R.id.query_result);
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * reflect on Options was clicked
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuSetting:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.Login:
                Intent intentLogin = new Intent(this, Login.class);
                startActivity(intentLogin);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void queryClassTable(View button) {
        tableLayout = new TableLayout(this);

        EditText userIdEditText = (EditText) findViewById(R.id.userId);
        EditText pwdEditText = (EditText) findViewById(R.id.pwd);
        String userId = userIdEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();

        result.setText("Querying...");
        mainLayout.removeAllViewsInLayout();
        mainLayout.addView(result);

        if (!userId.isEmpty() && !pwd.isEmpty()) {
            ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                result.setText("Network error!");
                mainLayout.removeAllViewsInLayout();
                mainLayout.addView(result);
            } else {
                String[] queryInfo = new String[]{userId, pwd};
                new QueryClassTable().execute(queryInfo);
            }
        }
    }

    /**
     * the Key of Intent to pass input's value
     */
    public final static String EXTRA_MESSAGE = "com.ericweb.timetable.MESSAGE";
    public TextView result;
    public ViewGroup mainLayout;
    public TableLayout tableLayout;
    class QueryClassTable extends AsyncTask<String[], Void, ClassTable> {
        @Override
        protected ClassTable doInBackground(String[]... strings) {
            try {
                URL url = new URL("http://www.ericweb.cn:8080/test");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.connect();
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print("userid=" + strings[0][0] + "&pwd=" + strings[0][1] + "&isPhone=true&year=2016&session=1&submit=query");
                out.flush();
                out.close();

                Scanner in = new Scanner(conn.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                while (in.hasNext()) {
                    stringBuilder.append(in.next());
                }

                Gson gson = new Gson();

                return gson.fromJson(stringBuilder.toString(), ClassTable.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ClassTable classtable) {
            // config tablelayout
            tableLayout.setStretchAllColumns(true);
            tableLayout.setShrinkAllColumns(true);
            // set context
            Context context = tableLayout.getContext();


            TableRow dayRow = new TableRow(context);

            TextView classNumberCol = new TextView(context);
            classNumberCol.setText("NO.");
            classNumberCol.setGravity(Gravity.CENTER);
            dayRow.addView(classNumberCol);

            for (int i = 0; i < 7; i++) {
                TextView dayText = new TextView(context);
                int tempCount = i + 1;
                dayText.setGravity(Gravity.CENTER);
                dayText.setText(tempCount + "");
                dayRow.addView(dayText);
            }
            tableLayout.addView(dayRow);


            int classNumberPerDay = classtable.getCourseNumberPerDay();
            for (int countClassIndex = 0; countClassIndex < classNumberPerDay; countClassIndex++) {
                TableRow classRow = new TableRow(context);
                TextView classIndex = new TextView(context);
                int classIndexInt = countClassIndex + 1;
                classIndex.setText("" + classIndexInt);
                classIndex.setGravity(Gravity.CENTER);
                classIndex.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));

                classRow.addView(classIndex);

                for (int countDay = 0; countDay < 7; countDay++) {
                    String courseName;
                    String address;
                    try {
                        courseName = classtable.getDayInClassTable().get(countDay).getDayTable().get(countClassIndex).get(0).getCourse().getCourseName();
                        address = classtable.getDayInClassTable().get(countDay).getDayTable().get(countClassIndex).get(0).getCourse().getAddress();
                    } catch (Exception e) {
                        courseName = "";
                        address = "";
                    }

                    String courseInfo = courseName;
                    if (!address.isEmpty()) {
                        courseInfo = courseInfo + "@" + address;
                    }
                    TextView course = new TextView(context);
                    course.setGravity(Gravity.CENTER);
                    course.setText(courseInfo);
                    course.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));
//                    course.setText("中文中文中文中文");
                    classRow.addView(course);
                }
                tableLayout.addView(classRow);
            }


            mainLayout.removeAllViewsInLayout();
            mainLayout.addView(tableLayout);

        }
    }
}
