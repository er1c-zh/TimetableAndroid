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
import com.google.gson.GsonBuilder;

import java.lang.Class;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

import cn.ericweb.timetable.domain.*;
import cn.ericweb.timetable.eframework.ResponseBean;
import cn.ericweb.timetable.utils.AppConstant;
import cn.ericweb.timetable.utils.RefreshWidget;

public class QueryClassTable extends AppCompatActivity {

    private String startYear;
    private String semester;
    private QueryInfo queryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_class_table);

        // 布置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.query_toolbar);
        toolbar.setTitle(R.string.query_class_table_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //初始化查询状态的View的指向
        queryStatus = (TextView) findViewById(R.id.query_status);

        this.queryInfo = new QueryInfo();

        // 初始化默认的学年学期
        // TODO: 17-3-7 完成学年学期的功能
        TextView yearTextview = (TextView) findViewById(R.id.query_year_textview);
        TextView semesterTextview = (TextView) findViewById(R.id.query_semester_textview);
        int _semester;
        int _year;

    }

    public void queryClassTable(View button) {
        // 初始化查询信息
        EditText userIdEditText = (EditText) findViewById(R.id.query_userId);
        EditText pwdEditText = (EditText) findViewById(R.id.query_pwd);
        this.queryInfo.setId(userIdEditText.getText().toString());
        this.queryInfo.setPwd(pwdEditText.getText().toString());
        this.queryInfo.setCheckcode("");

        if (!this.queryInfo.getId().isEmpty() && !this.queryInfo.getPwd().isEmpty()) {
            ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                queryStatus.setText("Network error!");
            } else {
                queryStatus.setText("开始链接网络查询");
                new Query().execute(this.queryInfo);
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
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
        try {
            Classtable _c = gson.fromJson(jsonClasstable, Classtable.class);

            LinkedList<Integer> colors = new LinkedList<>();
            Class Rcolor = R.color.class;
            for(int i = 1 ; i <= 16; i++) {
                int tmpColor;
                try {
                    Field field = Rcolor.getField("colorClasstableBg" + i);
                    tmpColor = getResources().getColor(field.getInt("colorClasstableBg" + i));
                } catch (Exception e) {
                    tmpColor = getResources().getColor(R.color.colorClassBackground);
                }
                colors.add(tmpColor);
            }

            LinkedList<Activity> _tmpList = new LinkedList<>();
            for(Activity _claxx : _c.getActivities()) {
                if(_claxx.getColorBg() == null) {
                    int _colorInt = colors.get(_tmpList.size() % 16);
                    int red = (_colorInt & 0xff0000) >> 16;
                    int green = (_colorInt & 0x00ff00) >> 8;
                    int blue = (_colorInt & 0x0000ff);
                    _claxx.setColorBg(new Color(red, green, blue, 255));
                }
                _tmpList.add(_claxx);
            }
            _c.setActivities(_tmpList);

            SharedPreferences sharedPref = this.getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(AppConstant.CLASSTABLE_KEY_MAIN, gson.toJson(_c));
            editor.commit();
        } catch (Exception e) {
            queryStatus.setText(e.getMessage());
            return false;
        }

        return true;
    }

    private void showErrorInfo(String errorInfo) {
        queryStatus.setText(errorInfo);
    }

    /**
     * the Key of Intent to pass input's value
     */
    public TextView queryStatus;

    class Query extends AsyncTask<QueryInfo, Void, String> {
        @Override
        protected String doInBackground(QueryInfo[] queryInfos) {
            try {
                QueryInfo queryInfo = queryInfos[0];

                URL url = new URL("http://123.207.161.188:8080/timetable/json?" + AppConstant.CLASSTABLE_ID + "=" + queryInfo.getId() + "&" + AppConstant.CLASSTABLE_PASSWORD + "=" + queryInfo.getPwd() + "&" + AppConstant.CLASSTABLE_CHECKCODE + "=" + queryInfo.getCheckcode() + "&" + AppConstant.CLASSTABLE_YEAR + "=2016&" + AppConstant.CLASSTABLE_SESSION + "=2");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("charset", "utf-8");

                conn.connect();

                Scanner in = new Scanner(conn.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                while (in.hasNext()) {
                    stringBuilder.append(in.next());
                }
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String queryResultJson) {
            // init gson
            Gson gson = new Gson();
            ResponseBean result;
            // 检查获得的数据 防止获得空内容
            try {
                result = gson.fromJson(queryResultJson, ResponseBean.class);
                if (result.sc == ResponseBean.SC_FAIL) {
                    String errorInfo;
                    QueryLoginResult qlr = gson.fromJson(result.content, QueryLoginResult.class);
                    errorInfo = qlr.getInfo();
                    showErrorInfo(errorInfo);
                } else {
                    if (setClasstableAndAdditionalInfo2SharedPreference(result.content)) {
                        RefreshWidget.updateWidgetClasstable(getBaseContext());
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
