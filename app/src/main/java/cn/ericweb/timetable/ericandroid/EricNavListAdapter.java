package cn.ericweb.timetable.ericandroid;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.ericweb.timetable.MainActivity;
import cn.ericweb.timetable.R;
import cn.ericweb.timetable.domain.NavItem;

/**
 * Created by eric on 17-3-6.
 */

public class EricNavListAdapter extends BaseAdapter {
    private List<NavItem> items;
    private Context context;
    public EricNavListAdapter(List<NavItem> list, Context _c) {
        items = list;
        context = _c;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NavItem navItem = this.items.get(i);

        final LayoutInflater inflater = LayoutInflater.from(this.context);
        View result = inflater.inflate(R.layout.item_nav, null);

        ImageView icon = (ImageView) result.findViewById(R.id.nav_icon);
        if(navItem.getIcon() != null) {
            icon.setBackground(navItem.getIcon());
        }
        TextView title = (TextView) result.findViewById(R.id.nav_text);
        if(navItem.getTitle() != null) {
            title.setText(navItem.getTitle());
        }

        return result;
    }
}
