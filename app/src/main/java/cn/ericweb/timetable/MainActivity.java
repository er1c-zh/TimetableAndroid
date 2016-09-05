package cn.ericweb.timetable;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    class QueryClassTable extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings) {
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

                ClassTable json2ClassTable = gson.fromJson(stringBuilder.toString(), ClassTable.class);

                return json2ClassTable.getDayInClassTable().get(0).getDayTable().get(0).get(0).toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            result.setText(s);
            mainLayout.removeAllViewsInLayout();
            mainLayout.addView(result);
        }
    }
}
