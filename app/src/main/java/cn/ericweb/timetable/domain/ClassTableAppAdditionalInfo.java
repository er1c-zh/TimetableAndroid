package cn.ericweb.timetable.domain;

import java.util.ArrayList;

/**
 * to contain 额外的课程信息
 * Created by eric on 16-9-15.
 */
public class ClassTableAppAdditionalInfo {
    public ClassTableAppAdditionalInfo() {
        this.containers = new ArrayList<>();
    }

    /**
     * 添加新的课程附加信息
     *
     * @param _course
     */
    public void addCourseAppAdditionalInfo(Course _course) {
        if (!isTargetExist(_course)) {
            CourseAppAdditionalInfo newInfo = new CourseAppAdditionalInfo(_course);
            containers.add(newInfo);
        }
    }

    public void replaceCourseAppAdditionalInfo(CourseAppAdditionalInfo _new) {
        if (this.isTargetExist(_new.getCourse())) {
            for (int i = 0; i < containers.size(); i++) {
                if (containers.get(i).isInfoAboutThisCourse(_new.getCourse())) {
                    containers.set(i, _new);
                }
            }
        } else {
            containers.add(_new);
        }
    }

    /**
     * 获得目标的附加信息
     *
     * @param _string
     * @return 如果未找到则返回null
     */
    public CourseAppAdditionalInfo getCourseAppAdditionalInfo(String _string) {
        if (isTargetExist(_string)) {
            for (CourseAppAdditionalInfo c : containers) {
                if (c.isInfoAboutThisCourse(_string)) {
                    return c;
                }
            }
        }
        return null;
    }

    public CourseAppAdditionalInfo getCourseAppAdditionalInfo(Course _course) {
        if (isTargetExist(_course)) {
            for (CourseAppAdditionalInfo c : containers) {
                if (c.isInfoAboutThisCourse(_course)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * 检查目标课程的信息是否存在了
     *
     * @param _course
     * @return
     */
    public boolean isTargetExist(Course _course) {
        for (CourseAppAdditionalInfo c : containers) {
            if (c.isInfoAboutThisCourse(_course)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查目标课程的信息是否存在了
     *
     * @param _string
     * @return
     */
    public boolean isTargetExist(String _string) {
        for (CourseAppAdditionalInfo c : containers) {
            if (c.isInfoAboutThisCourse(_string)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<CourseAppAdditionalInfo> containers;
}
