package cn.ericweb.timetable;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.ericweb.timetable.domain.NavItem;
import cn.ericweb.timetable.ericandroid.EricNavListAdapter;
import cn.ericweb.timetable.utils.AppConstant;
import cn.ericweb.timetable.utils.NavListUtils;
import cn.ericweb.timetable.utils.RefreshWidget;

public class MainActivity extends AppCompatActivity {
    public static final int ACTION_MAIN = 1;
    public static final int ACTION_EXAM = 2;
    public static final int ACTION_SUBJECT = 3;
    public static final int ACTION_NEXT_WEEK = 4;
    public static final int ACTION_PRE_WEEK = 5;


    // 现在课程表正在展示的周数
    private int weekShowwing;
    private int nowAction;
    private boolean isInited;

    // 一些需要的对象
    private RelativeLayout fragmentContainer;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.isInited = false;
        this.nowAction = MainActivity.ACTION_MAIN;

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

        // 初始化nav
        LinearLayout _navListContainer = (LinearLayout) findViewById(R.id.nav_list_linearlayout);
        ListView lv = (ListView) _navListContainer.findViewById(R.id.nav_list_listview);
        lv.setAdapter((new NavListUtils(this)).getNavListAdapter());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavItem navItem = (NavItem) adapterView.getItemAtPosition(i);
                nowAction =  navItem.getAction();
                doAction(nowAction);
                closeNav();
            }
        });

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout dl = (DrawerLayout) findViewById(R.id.nav_list_container);
                dl.openDrawer(findViewById(R.id.nav_list_linearlayout));
            }
        });

        // 保存屏幕的Density
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        SharedPreferences config = getSharedPreferences(AppConstant.SHARED_PREF_PHONE, MODE_PRIVATE);
        SharedPreferences.Editor editor = config.edit();
        editor.putInt(AppConstant.PHONE_DENSITY_DPI, densityDpi);
        editor.commit();

        // 初始化私有变量
        this.fragmentContainer = (RelativeLayout) findViewById(R.id.main_container);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 设置weekShowwing 为 当前周
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Calendar now = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
        Date startDate;
        try {
            startDate = yyyymmdd.parse(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.setting_classtable_now_week_first_week_start_date_key), ""));
        } catch (ParseException e) {
            startDate = new Date();
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        long temp = (now.getTimeInMillis() - startCalendar.getTimeInMillis()) / (7 * 1000 * 24 * 60 * 60) + 1;
        weekShowwing = (int) temp;

        this.doAction(this.nowAction);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(isInited == false) {
            isInited = true;
            this.doAction(this.nowAction);
        }
    }

    /**
     * 绘制课程表
     */
    @SuppressLint({"SetTextI18n", "NewApi"})
    void showTables(int week2show) {
        SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences classtableSharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);

        // 防止第一次或清空了数据
        String jsonClasstable = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");
        if (jsonClasstable.equals("")) {
            showRefresh(null);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(ClasstableFragment.WEEK_TO_SHOW, week2show);
        bundle.putBoolean(ClasstableFragment.IF_SHOW_WEEKENDS, config.getBoolean(getString(R.string.setting_classtable_show_weekends_key), true));
        bundle.putString(ClasstableFragment.CLASSTABLE_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, ""));
        // 获得尺寸数据
        // 宽度
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int containerWidth = point.x;
        // 高度
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        Rect viewCanvasRect = new Rect();
        getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(viewCanvasRect);
        int containerHeight = viewCanvasRect.height() - toolbar.getHeight();
        bundle.putInt(ClasstableFragment.CONTAINER_WIDTH, containerWidth);
        bundle.putInt(ClasstableFragment.CONTAINER_HEIGHT, containerHeight);
        bundle.putString(ClasstableFragment.FIRST_WEEK_DATE_STRING, config.getString(getString(R.string.setting_classtable_now_week_first_week_start_date_key), ""));

        ClasstableFragment classtableFragment = new ClasstableFragment();
        classtableFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, classtableFragment).commit();
    }
    void showSubjects() {
        SharedPreferences classtableSharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
        String jsonClasstable = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");
        Bundle bundle = new Bundle();
        bundle.putString(SubjectsFragment.CLASSTABLE_JSON, jsonClasstable);
        SubjectsFragment subFrag = new SubjectsFragment();
        subFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, subFrag).commit();
    }

    /**
     * 显示重新获取的Activity
     */
    public void showRefresh(View view) {
        Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
        startActivity(intent);
        closeNav();
    }

    /**
     * 显示版本信息
     */
    public void showVersionInfo(View view) {
        Intent intent = new Intent(this, cn.ericweb.timetable.VersionInfo.class);
        startActivity(intent);
        closeNav();
    }

    /**
     * 展示设置页面
     */
    public void showSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        closeNav();
    }

    private void cleanMainContainer() {
        // 清理掉容器
        RelativeLayout classTableContainerRelative = (RelativeLayout) findViewById(R.id.main_container);
        classTableContainerRelative.removeAllViews();
    }

    private void closeNav() {
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.nav_list_container);
        dl.closeDrawer(findViewById(R.id.nav_list_linearlayout));
    }

    public void doAction(int action) {
        switch (action) {
            case MainActivity.ACTION_MAIN:
                TransitionManager.beginDelayedTransition(fragmentContainer, new Fade());
                this.cleanMainContainer();
                this.showTables(weekShowwing);
                break;
            case MainActivity.ACTION_EXAM:
                TransitionManager.beginDelayedTransition(fragmentContainer, new Fade());
                this.cleanMainContainer();
                break;
            case MainActivity.ACTION_SUBJECT:
                TransitionManager.beginDelayedTransition(fragmentContainer, new Fade());
                this.cleanMainContainer();
                this.showSubjects();
                break;
            case MainActivity.ACTION_NEXT_WEEK:
                this.weekShowwing++;
                doAction(MainActivity.ACTION_MAIN);
                break;
            case MainActivity.ACTION_PRE_WEEK:
                if(this.weekShowwing > 1) {
                    this.weekShowwing--;
                    doAction(MainActivity.ACTION_MAIN);
                }
                break;
            default:
                doAction(MainActivity.ACTION_MAIN);
                break;
        }
    }
}