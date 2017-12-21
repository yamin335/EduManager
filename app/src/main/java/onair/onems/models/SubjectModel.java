package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class SubjectModel {

    private int SubjectID;
    private int SubjectNo;
    private String SubjectName;
    private int InsSubjectID;
    private int InstituteID;
    private int DepartmentID;
    private int MediumID;
    private int ClassID;
    private boolean IsActive;
    private boolean IsCombined;
    private int ParentID;
    private String ParentSubject;

    public SubjectModel(int SubjectID, int SubjectNo, String SubjectName, int InsSubjectID,
                 int InstituteID, int DepartmentID, int MediumID, int ClassID, boolean IsActive,
                 boolean IsCombined, int ParentID, String ParentSubject) {
        this.SubjectID = SubjectID;
        this.SubjectNo = SubjectNo;
        this.SubjectName = SubjectName;
        this.InsSubjectID = InsSubjectID;
        this.InstituteID = InstituteID;
        this.DepartmentID = DepartmentID;
        this.MediumID = MediumID;
        this.ClassID = ClassID;
        this.IsActive = IsActive;
        this.IsCombined = IsCombined;
        this.ParentID = ParentID;
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
