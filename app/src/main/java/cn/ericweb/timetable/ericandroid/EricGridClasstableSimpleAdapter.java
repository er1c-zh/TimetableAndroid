package cn.ericweb.timetable.ericandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * 绘制课程表Grid view 时使用的adapter
 * 实现了 1.定制Item高度
 * Created by Eric on 2016/10/9.
 */

public class EricGridClasstableSimpleAdapter extends SimpleAdapter {
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public EricGridClasstableSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.mContext = context;
        this.mData = data;
        this.mResource = resource;
        this.mFrom = from;
        this.mTo = to;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.isSetHeight = false;
        this.isSetWidth = false;
    }

    @Override
    public int getCount() {
        return this.mData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        if (this.isSetHeight) {
            params.height = this.height;
        }
        if (this.isSetWidth) {
            params.width = this.width;
        }
        convertView.setLayoutParams(params);

        return convertView;
    }

    public void setWidth(int width) {
        this.width = width;
        this.isSetWidth = true;
    }

    public void setHeight(int height) {
        this.height = height;
        this.isSetHeight = true;
    }

    private Context mContext;
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private String[] mFrom;
    private int[] mTo;
    private LayoutInflater mInflater;

    private int height;
    private boolean isSetHeight;
    private int width;
    private boolean isSetWidth;
}
