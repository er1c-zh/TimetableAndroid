package cn.ericweb.timetable.ericandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;

import cn.ericweb.timetable.R;
import cn.ericweb.timetable.domain.Subject;

/**
 * Created by eric on 17-3-7.
 */

public class EricSubjectsListAdapter extends BaseAdapter {
    private LinkedList<Subject> subjects;
    private Context context;
    private HashMap<Integer, View> views;

    public EricSubjectsListAdapter(LinkedList<Subject> subjects, Context context) {
        this.subjects = subjects;
        this.context = context;
        this.views = new HashMap<>();
    }

    @Override
    public int getCount() {
        return this.subjects.size();
    }

    @Override
    public Object getItem(int i) {
        return this.subjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Subject subject = this.subjects.get(i);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View result = inflater.inflate(R.layout.item_subject, null);

        TextView title = (TextView) result.findViewById(R.id.subject_title);
        title.setText(subject.getTitle());
        TextView teacher = (TextView) result.findViewById(R.id.subject_teacher);
        teacher.setText(subject.getTeacher().getName());

        return result;
    }
}
