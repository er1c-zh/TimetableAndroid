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

/**
 * 放入课程表每个格子中的课程信息，可以用来扩展一些与Course相关性较差的数据。
 *
 * @author eric
 */
public class CourseInClassTable implements Serializable {

    private Course course;
    private String timeArrangementForCourse;

    @Override
    public String toString() {
        return "++" + course.toString() + "==" + timeArrangementForCourse + "++";
//        return "hello";
    }

    public void setCourse(Course _course) {
        course = _course;
    }

    public Course getCourse() {
        return course;
    }

    public void setTimeArrangementForCourse(String _timeArrangementForCourse) {
        timeArrangementForCourse = _timeArrangementForCourse;
    }

    public String getTimeArrangementForCourse() {
        return timeArrangementForCourse;
    }
}
