package onair.onems.models;

/**
 * Created by onAir on 15-Mar-18.
 */

public class DailySybjectWiseAttendanceModel {
    private long SubjectID = 0;
    private String Subject = "";
    private String SubjectNo = "";
    private int Status = 0;
    private long AtdUserID = 0;
    private String ClassTeacher = "";

    public DailySybjectWiseAttendanceModel(){
        super();
    }

    public void setSubjectID(String SubjectID){
        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }
    }

    public long getSubjectID()
    {
        return SubjectID;
    }

    public void setSubject(String Subject)
    {
        this.Subject = Subject;
    }

    public String getSubject()
    {
        return Subject;
    }

    public void setSubjectNo(String SubjectNo)
    {
        this.SubjectNo = SubjectNo;
    }

    public String getSubjectNo()
    {
        return SubjectNo;
    }

    public void setStatus(String Status){
        if(Status.equals("null"))
        {
            this.Status = 0;
        }
        else
        {
            this.Status = Integer.parseInt(Status);
        }
    }

    public int getStatus()
    {
        return Status;
    }

    public void setAtdUserID(String AtdUserID){
        if(AtdUserID.equals("null"))
        {
            this.AtdUserID = 0;
        }
        else
        {
            this.AtdUserID = Long.parseLong(AtdUserID);
        }
    }

    public long getAtdUserID()
    {
        return AtdUserID;
    }

    public void setClassTeacher(String ClassTeacher)
    {
        this.ClassTeacher = ClassTeacher;
    }

    public String getClassTeacher()
    {
        return ClassTeacher;
    }
}
