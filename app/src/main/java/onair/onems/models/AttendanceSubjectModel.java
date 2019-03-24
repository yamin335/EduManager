package onair.onems.models;

public class AttendanceSubjectModel {

    private long SubjectID = 0;
    private String SubjectNo = "";
    private String SubjectName = "";

    public AttendanceSubjectModel()
    {

    }

    public AttendanceSubjectModel(String SubjectID, String SubjectNo, String SubjectName) {

        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }

        this.SubjectNo = SubjectNo;
        this.SubjectName = SubjectName;

    }

    public void setSubjectID(String SubjectID)
    {
        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }
    }

    public void setSubjectNo(String SubjectNo)
    {
        this.SubjectNo = SubjectNo;
    }

    public void setSubjectName(String SubjectName)
    {
        this.SubjectName = SubjectName;
    }

    public long getSubjectID()
    {
        return SubjectID;
    }

    public String getSubjectNo()
    {
        return SubjectNo;
    }

    public String getSubjectName()
    {
        return SubjectName;
    }
}
