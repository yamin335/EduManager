package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class StudentModel {

    private int UserID;
    private String UserFullName;
    private Long RFID;
    private int RollNo;
    private int SubjectID;
    private int DepartmentID;
    private int SectionID;
    private int[] InstituteID;
    private int MediumID;
    private int ShiftID;
    private int ClassID;
    private boolean IsPresent;
    private boolean Islate;
    private int LateTime;
    private boolean IsLeave;
    private boolean IsAbsent;
    private String Remarks;
    private String Subject;
    private String Department;
    private String Medium;
    private String Shift;
    private String Class;

    StudentModel(int UserID, String UserFullName, Long RFID, int RollNo, int SubjectID,
                 int DepartmentID, int SectionID, int[] InstituteID, int MediumID, int ShiftID,
                 int ClassID, boolean IsPresent, boolean Islate, int LateTime, boolean IsLeave,
                 boolean IsAbsent, String Remarks, String Subject, String Department, String Medium,
                 String Shift, String Class)
    {
        this.UserID = UserID;
        this.UserFullName = UserFullName;
        this.RFID = RFID;
        this.RollNo = RollNo;
        this.SubjectID = SubjectID;
        this.DepartmentID = DepartmentID;
        this.SectionID = SectionID;
        this.InstituteID = InstituteID;
        this.MediumID = MediumID;
        this.ShiftID = ShiftID;
        this.ClassID = ClassID;
        this.IsPresent = IsPresent;
        this.Islate = Islate;
        this.LateTime = LateTime;
        this.IsLeave = IsLeave;
        this.IsAbsent = IsAbsent;
        this.Remarks = Remarks;
        this.Subject = Subject;
        this.Department = Department;
        this.Medium = Medium;
        this.Shift = Shift;
        this.Class = Class;
    }

    public void setUserID(int UserID)
    {
        this.UserID = UserID;
    }

    public void setUserFullName(String UserFullName )
    {
        this.UserFullName = UserFullName;
    }

    public void setRFID(Long RFID)
    {
        this.RFID = RFID;
    }

    public void setRollNo(int RollNo)
    {
        this.RollNo = RollNo;
    }

    public void setSubjectID(int SubjectID)
    {
        this.SubjectID = SubjectID;
    }

    public void setDepartmentID(int DepartmentID)
    {
        this.DepartmentID = DepartmentID;
    }

    public void setSectionID(int SectionID)
    {
        this.SectionID = SectionID;
    }

    public void setInstituteID(int[] InstituteID)
    {
        this.InstituteID = InstituteID;
    }

    public void setMediumID(int MediumID)
    {
        this.MediumID = MediumID;
    }

    public void setShiftID(int ShiftID)
    {
        this.ShiftID = ShiftID;
    }

    public void setClassID(int ClassID)
    {
        this.ClassID = ClassID;
    }

    public void setIsPresent(boolean IsPresent)
    {
        this.IsPresent = IsPresent;
    }

    public void setIslate(boolean Islate)
    {
        this.Islate = Islate;
    }

    public void setLateTime(int LateTime)
    {
        this.LateTime = LateTime;
    }

    public void setIsLeave(boolean IsLeave)
    {
        this.IsLeave = IsLeave;
    }

    public void setIsAbsent(boolean IsAbsent)
    {
        this.IsAbsent = IsAbsent;
    }

    public void setRemarks(String Remarks)
    {
        this.Remarks = Remarks;
    }

    public void setSubject(String Subject)
    {
        this.Subject = Subject;
    }

    public void setDepartment(String Department)
    {
        this.Department = Department;
    }

    public void setMedium(String Medium)
    {
        this.Medium = Medium;
    }

    public void setShift(String Shift)
    {
        this.Shift = Shift;
    }

    public void setClass(String Class)
    {
        this.Class = Class;
    }

    public int getUserID()
    {
        return UserID;
    }

    public String getUserFullName()
    {
        return UserFullName;
    }

    public Long getRFID()
    {
        return RFID;
    }

    public int getRollNo()
    {
        return RollNo;
    }

    public int getSubjectID()
    {
        return SubjectID;
    }

    public int getDepartmentID()
    {
        return DepartmentID;
    }

    public int getSectionID()
    {
        return SectionID;
    }

    public int[] getInstituteID()
    {
        return InstituteID;
    }

    public int getMediumID()
    {
        return MediumID;
    }

    public int getShiftID()
    {
        return ShiftID;
    }

    public int getClassID()
    {
        return ClassID;
    }

    public boolean getIsPresent()
    {
        return IsPresent;
    }

    public boolean getIslate()
    {
        return Islate;
    }

    public int getLateTime()
    {
        return LateTime;
    }

    public boolean getIsLeave()
    {
        return IsLeave;
    }

    public boolean getIsAbsent()
    {
        return IsAbsent;
    }

    public String getRemarks()
    {
        return Remarks;
    }

    public String getSubject()
    {
        return Subject;
    }

    public String getDepartment()
    {
        return Department;
    }

    public String getMedium()
    {
        return Medium;
    }

    public String getShift()
    {
        return Shift;
    }

    public String getClasss()
    {
        return Class;
    }

}
