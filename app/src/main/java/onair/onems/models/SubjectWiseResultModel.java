package onair.onems.models;

/**
 * Created by User on 2/11/2018.
 */

public class SubjectWiseResultModel {

    private String SubjectName = "Islamic Studies";
    private String Mark = "100";
    private String Grade = "A+";
    private String Comment = "Excelent";
    public String MCQ = "50";
    public String Written = "100";
    public String Practical = "15";
    public String Attendance = "5";

    public SubjectWiseResultModel()
    {
        this.SubjectName = "Islamic Studies";
        this.Mark = "100";
        this.Grade = "A+";
        this.Comment = "Excelent";
        this.MCQ = "50";
        this.Written = "100";
        this.Practical = "15";
        this.Attendance = "5";
    }

    public SubjectWiseResultModel(String SubjectName, String Mark, String Grade, String Comment)
    {
        this.SubjectName = SubjectName;
        this.Mark = Mark;
        this.Grade = Grade;
        this.Comment = Comment;
    }

    public void setSubjectName(String SubjectName)
    {
        this.SubjectName = SubjectName;
    }

    public String getSubjectName()
    {
        return this.SubjectName;
    }

    public void setMark(String Mark)
    {
        this.Mark = Mark;
    }

    public String getMark()
    {
        return this.Mark;
    }

    public void setGrade(String Grade)
    {
        this.Grade = Grade;
    }

    public String getGrade()
    {
        return this.Grade;
    }

    public void setComment(String Comment)
    {
        this.Comment = Comment;
    }

    public String getComment()
    {
        return this.Comment;
    }
}
