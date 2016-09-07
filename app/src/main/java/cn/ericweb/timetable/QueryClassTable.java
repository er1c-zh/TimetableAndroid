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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import cn.ericweb.timetable.domain.ClassTable;

public class QueryClassTable extends AppCompatActivity {

    private Intent intent2Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_class_table);

        Toolbar toolbar = (Toolbar) findViewById(R.id.query_toolbar);
        toolbar.setTitle("查询");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitle));
        setSupportActionBar(toolbar);

        // 初始化指向main的inent
        intent2Main = new Intent(this, cn.ericweb.timetable.MainActivity.class);

        SharedPreferences sharedPref = getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        TextView temp = (TextView) findViewById(R.id.classtable_status);
        if (sharedPref.contains("classtable")) {
            temp.setText("exist");
        } else {
            temp.setText("none");
        }
    }

    public void queryClassTable(View button) {
        tableLayout = new TableLayout(this);

        EditText userIdEditText = (EditText) findViewById(R.id.query_userId);
        EditText pwdEditText = (EditText) findViewById(R.id.query_pwd);
        String userId = userIdEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();

        if (!userId.isEmpty() && !pwd.isEmpty()) {
            ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                result.setText("Network error!");
                mainLayout.removeAllViewsInLayout();
                mainLayout.addView(result);
            } else {
                String[] queryInfo = new String[]{userId, pwd};
                new Query().execute(queryInfo);
            }
        }
    }

    private void setClasstable2SharedPreference(String jsonClasstable) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = this.getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("classtable", jsonClasstable);
        editor.commit();
    }

    /**
     * the Key of Intent to pass input's value
     */
    public final static String EXTRA_MESSAGE = "com.ericweb.timetable.MESSAGE";
    public TextView result;
    public ViewGroup mainLayout;
    public TableLayout tableLayout;

    class Query extends AsyncTask<String[], Void, String> {
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
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonClasstable) {
            setClasstable2SharedPreference(jsonClasstable);
//            Intent intent = new Intent(this, cn.ericweb.timetable.MainActivity.class);
            startActivity(intent2Main);
            System.out.println("hello");
        }
    }
}
