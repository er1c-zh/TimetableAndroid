package cn.ericweb.timetable;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.ericweb.timetable.util.AppConstant;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        myToolbar.setSubtitle(R.string.version_info_simple);
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);


        // TODO: 2016/10/9 这是为了测试新的启动ACTIVITY而设置的跳转
        Intent intent = new Intent(this, cn.ericweb.timetable.HomeActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartX = event.getX();
            touchStartY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchEndX = event.getX();
            touchEndY = event.getY();

            // 分析是否是左右滑动
            if (touchEndX - touchStartX > 50) {
                if (weekShowwing > 1) {
                    LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container_new);
                    classTableContainer.setId(R.id.classtable_container_old);

                    LinearLayout classTableContainerNew = new LinearLayout(this);
                    classTableContainerNew.setId(R.id.classtable_container_new);
                    classTableContainerNew.setOrientation(LinearLayout.VERTICAL);
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.classtable_container);
                    relativeLayout.addView(classTableContainerNew);

                    float containerX = classTableContainer.getX();
                    float containerWidth = classTableContainer.getWidth();
                    ObjectAnimator dispear = ObjectAnimator.ofFloat(classTableContainer, "translationX", containerX, containerX + containerWidth, containerX + containerWidth);
                    dispear.setDuration(500);
                    dispear.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            showTables(--weekShowwing);
                            LinearLayout classTable = (LinearLayout) findViewById(R.id.classtable_container_new);
                            ObjectAnimator.ofFloat(classTable, "alpha", 0f, 1f, 1f).setDuration(1000).start();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    dispear.start();
                }
            } else if (touchStartX - touchEndX > 50) {
                if (weekShowwing < 52) {
                    LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container_new);
                    classTableContainer.setId(R.id.classtable_container_old);

                    LinearLayout classTableContainerNew = new LinearLayout(this);
                    classTableContainerNew.setId(R.id.classtable_container_new);
                    classTableContainerNew.setOrientation(LinearLayout.VERTICAL);
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.classtable_container);
                    relativeLayout.addView(classTableContainerNew);

                    float containerX = classTableContainer.getX();
                    float containerWidth = classTableContainer.getWidth();
                    ObjectAnimator dispear = ObjectAnimator.ofFloat(classTableContainer, "translationX", containerX, containerX - containerWidth, containerX - containerWidth);
                    dispear.setDuration(500);
                    dispear.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            showTables(++weekShowwing);
                            LinearLayout classTable = (LinearLayout) findViewById(R.id.classtable_container_new);
                            ObjectAnimator.ofFloat(classTable, "alpha", 0f, 1f, 1f).setDuration(1000).start();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    dispear.start();
                }
            }
            touchStartX = 0;
            touchStartY = 0;
            touchEndX = 0;
            touchEndY = 0;
        }
        return true;
    }

    private float touchStartX;
    private float touchStartY;
    private float touchEndX;
    private float touchEndY;

    @Override
    protected void onResume() {
        super.onResume();
        // 清理掉之前的课程表
        RelativeLayout classTableContainerRelative = (RelativeLayout) findViewById(R.id.classtable_container);
        classTableContainerRelative.removeAllViews();
        LinearLayout containerNew = new LinearLayout(this);
        containerNew.setId(R.id.classtable_container_new);
        containerNew.setOrientation(LinearLayout.VERTICAL);
        classTableContainerRelative.addView(containerNew);


        // 设置weekShowwing 为 当前周
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        weekShowwing = sharedPref.getInt(getString(R.string.setting_classtable_now_week_key), 1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showTables(weekShowwing);
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
        String jsonClasstableAdditionalInfo = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, "");
        if (jsonClasstable.equals("") || jsonClasstableAdditionalInfo.equals("")) {
            showRefresh();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(ClasstableFragment.WEEK_TO_SHOW, week2show);
        bundle.putBoolean(ClasstableFragment.IF_SHOW_WEEKENDS, config.getBoolean(getString(R.string.setting_classtable_show_weekends_key), true));
        bundle.putString(ClasstableFragment.CLASSTABLE_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, ""));
        bundle.putString(ClasstableFragment.CLASSTABLE_ADDITIONAL_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, ""));
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
        getSupportFragmentManager().beginTransaction().add(R.id.classtable_container_new, classtableFragment).commit();
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 显示重新获取的Activity
     */
    void showRefresh() {
        Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);

        startActivity(intent);
    }

    /**
     * 显示版本信息
     */
    void showVersionInfo() {
        Intent intent = new Intent(this, cn.ericweb.timetable.VersionInfo.class);
        startActivity(intent);
    }

    /**
     * 展示设置页面
     */
    void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // 现在课程表正在展示的周数
    private int weekShowwing;

    // 菜单click listener
    Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.refresh_classtable:
                    showRefresh();
                    break;
                case R.id.version_info:
                    showVersionInfo();
                    break;
                case R.id.settings:
                    showSettings();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
}