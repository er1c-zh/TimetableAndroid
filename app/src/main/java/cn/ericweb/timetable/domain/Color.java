package cn.ericweb.timetable.domain;

import java.io.Serializable;

/**
 * Created by eric on 17-2-21.
 */
public class Color implements Serializable {
    public int MAX = 255;
    public int MIN = 0;
    private int r;
    private int g;
    private int b;
    private int a;

    public Color() {
        this.r = MAX;
        this.g = MAX;
        this.b = MAX;
        this.a = MAX;
    }

    public Color(int r, int g, int b, int a) {
        this.r = this.getRightColor(r);
        this.g = this.getRightColor(g);
        this.b = this.getRightColor(b);
        this.a = this.getRightColor(a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (r != color.r) return false;
        if (g != color.g) return false;
        if (b != color.b) return false;
        return a == color.a;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + g;
        result = 31 * result + b;
        result = 31 * result + a;
        return result;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = this.getRightColor(r);
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = this.getRightColor(g);
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = this.getRightColor(b);
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = this.getRightColor(a);
    }

    private int getRightColor(int tmp) {
        if (tmp > this.MAX) {
            return this.MAX;
        } else if (tmp < this.MIN) {
            return this.MIN;
        } else {
            return tmp;
        }
    }
}
