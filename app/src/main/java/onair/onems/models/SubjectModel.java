package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class SubjectModel {

    private int SubjectID = 0;
    private int SubjectNo = 0;
    private String SubjectName;
    private int InsSubjectID = 0;
    private int InstituteID = 0;
    private int DepartmentID = 0;
    private int MediumID = 0;
    private int ClassID = 0;
    private boolean IsActive = true;
    private boolean IsCombined = true;
    private int ParentID = 0;
    private String ParentSubject = "";

    public SubjectModel()
    {

    }

    public SubjectModel(String SubjectID, String SubjectNo, String SubjectName, String InsSubjectID,
                        String InstituteID, String DepartmentID, String MediumID, String ClassID, String IsActive,
                        String IsCombined, String ParentID, String ParentSubject) {
        this.SubjectID = Integer.parseInt(SubjectID);
        this.SubjectNo = Integer.parseInt(SubjectNo);
        this.SubjectName = SubjectName;
        if(InsSubjectID.equals("null"))
        {
            this.InsSubjectID = 0;
        }
        else
        {
            this.InsSubjectID = Integer.parseInt(InsSubjectID);
        }
        this.InstituteID = Integer.parseInt(InstituteID);
        this.DepartmentID = Integer.parseInt(DepartmentID);
        this.MediumID = Integer.parseInt(MediumID);
        this.ClassID = Integer.parseInt(ClassID);
        this.IsActive = Boolean.parseBoolean(IsActive);
        this.IsCombined = Boolean.parseBoolean(IsCombined);
        if(ParentID.equals("null"))
        {
            this.ParentID = 0;
        }
        else
        {
            this.ParentID = Integer.parseInt(ParentID);
        }

        this.ParentSubject = ParentSubject;

    }

    public void setSubjectID(int SubjectID)
    {
        this.SubjectID = SubjectID;
    }

    public void setSubjectNo(int SubjectNo)
    {
        this.SubjectNo = SubjectNo;
    }

    public void setSubjectName(String SubjectName)
    {
        this.SubjectName = SubjectName;
    }

    public void setInsSubjectID(int InsSubjectID)
    {
        this.InsSubjectID = InsSubjectID;
    }

    public void setInstituteID(int InstituteID)
    {
        this.InstituteID = InstituteID;
    }

    public void setDepartmentID(int DepartmentID)
    {
        this.DepartmentID = DepartmentID;
    }

    public void setMediumID(int MediumID)
    {
        this.MediumID = MediumID;
    }

    public void setClassID(int ClassID)
    {
        this.ClassID = ClassID;
    }

    public void setIsActive(boolean IsActive)
    {
        this.IsActive = IsActive;
    }

    public void setIsCombined(boolean IsCombined)
    {
        this.IsCombined = IsCombined;
    }

    public void setParentID(int ParentID)
    {
        this.ParentID = ParentID;
    }

    public void setParentSubject(String ParentSubject)
    {
        this.ParentSubject = ParentSubject;
    }

    public int getSubjectID()
    {
        return SubjectID;
    }

    public int getSubjectNo()
    {
        return SubjectNo;
    }

    public String getSubjectName()
    {
        return SubjectName;
    }

    public int getInsSubjectID()
    {
        return InsSubjectID;
    }

    public int getInstituteID()
    {
        return InstituteID;
    }

    public int getDepartmentID()
    {
        return DepartmentID;
    }

    public int getMediumID()
    {
        return MediumID;
    }

    public int getClassID()
    {
        return ClassID;
    }

    public boolean getIsActive()
    {
        return IsActive;
    }

    public boolean getIsCombined()
    {
        return IsCombined;
    }

    public int getParentID()
    {
        return ParentID;
    }

    public String getParentSubject()
    {
        return ParentSubject;
    }
}
