package onair.onems.mainactivities.Routine;

/**
 * @author Alhaytham Alfeel on 10/10/2016.
 */

public class RoutineModel
{

    private String subjectId;
    private String teacherId;
    private String timeId;

    public RoutineModel(String subjectId, String teacherId, String timeId) {
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.timeId = timeId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTimeId() {
        return timeId;
    }
}
