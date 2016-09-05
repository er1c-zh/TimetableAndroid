/*
 * Copyright (c) 2016, eric
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package cn.ericweb.timetable.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class DayInClassTable implements Serializable {

    private ArrayList<ArrayList<CourseInClassTable>> dayTable;

    public DayInClassTable(int courseNumberPerDay) {
        dayTable = new ArrayList<ArrayList<CourseInClassTable>>();

        for (int i = 0; i < courseNumberPerDay; i++) {
            dayTable.add(i, new ArrayList<CourseInClassTable>());
        }
    }

    public DayInClassTable() {
        dayTable = new ArrayList<ArrayList<CourseInClassTable>>();
    }

    public void addNewClass(int indexOfClass, CourseInClassTable target) {
        if (null == dayTable.get(indexOfClass)) {
            dayTable.add(indexOfClass, new ArrayList<CourseInClassTable>());
        }
        dayTable.get(indexOfClass).add(target);
    }

    public void removeClass(int indexOfClass, CourseInClassTable target) {
        if (null != dayTable.get(indexOfClass)) {
            dayTable.get(indexOfClass).remove(target);
        }
    }

    public void setDayTable(ArrayList<ArrayList<CourseInClassTable>> _dayTable) {
        dayTable = _dayTable;
    }

    public ArrayList<ArrayList<CourseInClassTable>> getDayTable() {
        return dayTable;
    }
}
