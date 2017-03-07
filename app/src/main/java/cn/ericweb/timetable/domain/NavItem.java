package cn.ericweb.timetable.domain;

import android.app.*;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import java.lang.*;
import java.lang.Class;

/**
 * Created by eric on 17-3-6.
 */

public class NavItem {
    private String title;
    private Drawable icon;
    private int action;

    public NavItem() {
        this.title = null;
        this.icon = null;
        this.action = 0;
    }

    public NavItem(String title, Drawable icon, int action) {
        this.title = title;
        this.icon = icon;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
