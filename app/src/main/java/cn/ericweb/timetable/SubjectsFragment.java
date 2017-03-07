package cn.ericweb.timetable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.ericweb.timetable.domain.Classtable;
import cn.ericweb.timetable.ericandroid.EricSubjectsListAdapter;

public class SubjectsFragment extends Fragment {
    public final static String CLASSTABLE_JSON = "cn.ericweb.SubjectsFragment.classtable_json";

    public SubjectsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedInstanceState = getArguments();
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_subjects, container, false);
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
            Classtable classtable = gson.fromJson(savedInstanceState.getString(SubjectsFragment.CLASSTABLE_JSON), Classtable.class);
            EricSubjectsListAdapter eSubjectAdapter = new EricSubjectsListAdapter(classtable.getSubjects(), container.getContext());
            ListView subjectList = (ListView) frameLayout.findViewById(R.id.subjects_list);
            subjectList.setAdapter(eSubjectAdapter);
        } catch (Exception e) {
            TextView textView = new TextView(frameLayout.getContext());
            textView.setText("Error: " + e.toString());
            frameLayout.addView(textView);
        }

        return frameLayout;
    }
}
