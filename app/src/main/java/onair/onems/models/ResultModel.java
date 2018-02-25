package onair.onems.models;

/**
 * Created by User on 2/11/2018.
 */

public class ResultModel {

    private String ExamName = "Exam Name";
    private String PublishDate = "3-3-2055";

    public ResultModel()
    {
        this.ExamName = "Exam Name";
        this.PublishDate = "3-3-2055";
    }

    public ResultModel(String ExamName, String PublishDate)
    {
        this.ExamName = ExamName;
        this.PublishDate = PublishDate;
    }

    public void setExamName(String ExamName)
    {
        this.ExamName = ExamName;
    }

    public String getExamName()
    {
        return this.ExamName;
    }

    public void setPublishDate(String PublishDate)
    {
        this.PublishDate = PublishDate;
    }

    public String getPublishDate()
    {
        return this.PublishDate;
    }
}
