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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import cn.ericweb.timetable.domain.ClassTable;
import cn.ericweb.timetable.domain.ClassTableAppAdditionalInfo;
import cn.ericweb.timetable.domain.QueryResult;
import cn.ericweb.timetable.util.AppConstant;

public class QueryClassTable extends AppCompatActivity {

    private Intent intent2Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_class_table);

//        布置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.query_toolbar);
        toolbar.setTitle("查询");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 初始化指向main的intent
        intent2Main = new Intent(this, cn.ericweb.timetable.MainActivity.class);

        SharedPreferences sharedPref = getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        TextView temp = (TextView) findViewById(R.id.classtable_status);
        if (sharedPref.contains("classtable")) {
            temp.setText("exist");
        } else {
            temp.setText("none");
        }

        //初始化查询状态的View的指向
        queryStatus = (TextView) findViewById(R.id.query_status);
    }

    public void queryClassTable(View button) {
        EditText userIdEditText = (EditText) findViewById(R.id.query_userId);
        EditText pwdEditText = (EditText) findViewById(R.id.query_pwd);
        String userId = userIdEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();

        if (!userId.isEmpty() && !pwd.isEmpty()) {
            ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                queryStatus.setText("Network error!");
            } else {
                queryStatus.setText("开始链接网络查询");
                String[] queryInfo = new String[]{userId, pwd};
                new Query().execute(queryInfo);
            }
        }
    }

    /**
     * 设置课程表类和附加信息类到SharedPreference
     *
     * @param jsonClasstable
     * @return
     */
    private boolean setClasstableAndAdditionalInfo2SharedPreference(String jsonClasstable) {
        Gson gson = new Gson();
        ClassTableAppAdditionalInfo additionalInfo = new ClassTableAppAdditionalInfo();
        try {
            ClassTable temp = gson.fromJson(jsonClasstable, ClassTable.class);

            // 构建额外信息
            int classPerDay = temp.getCourseNumberPerDay();
            for (int day = 0; day < 7; day++) {
                for (int classIndex = 0; classIndex < classPerDay; classIndex++) {
                    if (null != temp.getCourseInClassTable(day, classIndex)) {
                        additionalInfo.addCourseAppAdditionalInfo(temp.getCourseInClassTable(day, classIndex).getCourse());
                    }
                }
            }
        } catch (Exception e) {
            queryStatus.setText("数据出现了错误===" + jsonClasstable);
            return false;
        }
        SharedPreferences sharedPref = this.getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstant.CLASSTABLE_KEY_MAIN, jsonClasstable);
        editor.putString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, gson.toJson(additionalInfo));
        editor.commit();
        return true;
    }

    private void showErrorInfo(String errorInfo) {
        queryStatus.setText(errorInfo);
    }

    /**
     * the Key of Intent to pass input's value
     */
    public final static String EXTRA_MESSAGE = "com.ericweb.timetable.MESSAGE";
    public TextView queryStatus;

    class Query extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings) {
            try {
                // 这是老版服务器的网址 应该是没用了
//                URL url = new URL("http://www.ericweb.cn:8080/test");
                URL url = new URL(getResources().getString(R.string.link_query_classtable));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.connect();
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(AppConstant.CLASSTABLE_ID + "=" + strings[0][0] + "&" + AppConstant.CLASSTABLE_PASSWORD + "=" + strings[0][1] + "&" + AppConstant.CLASSTABLE_YEAR + "=2016&" + AppConstant.CLASSTABLE_SESSION + "=1");
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
                return "";
            }
        }

        @Override
        protected void onPostExecute(String queryResultJson) {
            // init gson
            Gson gson = new Gson();
            QueryResult result;
            // 检查获得的数据 防止获得空内容
            try {
                result = gson.fromJson(queryResultJson, QueryResult.class);
                if (result.getStatus() == QueryResult.QUERY_BAD) {
                    showErrorInfo(result.getInfo());
                } else {
                    if (true == setClasstableAndAdditionalInfo2SharedPreference(result.getResultJson())) {
//                        startActivity(intent2Main);
                        finish();
                    }
                }
            } catch (Exception e) {
                showErrorInfo(getResources().getString(R.string.error_info_unknown_net_error));
                return;
            }
        }
    }
}
