package cn.ericweb.timetable.utils;

import android.content.Context;

import java.util.LinkedList;

import cn.ericweb.timetable.MainActivity;
import cn.ericweb.timetable.R;
import cn.ericweb.timetable.domain.NavItem;
import cn.ericweb.timetable.ericandroid.EricNavListAdapter;

/**
 * Created by eric on 17-3-7.
 */

public class NavListUtils {
    private LinkedList<NavItem> items;
    private Context context;
    private EricNavListAdapter ericNavListAdapter;
    public NavListUtils(Context _context) {
        this.items = new LinkedList<>();
        this.context = _context;
        items.add(new NavItem(context.getString(R.string.nav_title_home), context.getDrawable(R.drawable.ic_home_black_48dp), MainActivity.ACTION_MAIN));
        items.add(new NavItem(context.getString(R.string.nav_title_exam), context.getDrawable(R.drawable.ic_create_black_48dp), MainActivity.ACTION_EXAM));
        items.add(new NavItem(context.getString(R.string.nav_title_subject), context.getDrawable(R.drawable.ic_view_list_black_48dp), MainActivity.ACTION_SUBJECT));
        ericNavListAdapter = new EricNavListAdapter(this.items, this.context);
    }

    public EricNavListAdapter getNavListAdapter() {
        return this.ericNavListAdapter;
    }
}
