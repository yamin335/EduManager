package onair.onems.models;

import java.util.ArrayList;

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

    public StudentModel()
    {

    }

    public StudentModel(String UserID, String UserFullName, String RFID, String RollNo, String SubjectID,
                        String DepartmentID, String SectionID, String[] InstituteID, String MediumID, String ShiftID,
                        String ClassID, String IsPresent, String Islate, String LateTime, String IsLeave,
                        String IsAbsent, String Remarks, String Subject, String Department, String Medium,
                        String Shift, String Class)
    {
        this.UserID = Integer.parseInt(UserID);
        this.UserFullName = UserFullName;
        this.RFID = Long.parseLong(RFID);
        this.RollNo = Integer.parseInt(RollNo);
        this.SubjectID = Integer.parseInt(SubjectID);
        this.DepartmentID = Integer.parseInt(DepartmentID);
        this.SectionID = Integer.parseInt(SectionID);
        this.InstituteID = new int[InstituteID.length];
        for(int i = 0; i<InstituteID.length; i++)
        {
            this.InstituteID[i] = Integer.parseInt(InstituteID[i]);
        }
        this.MediumID = Integer.parseInt(MediumID);
        this.ShiftID = Integer.parseInt(ShiftID);
        this.ClassID = Integer.parseInt(ClassID);
        this.IsPresent = Boolean.parseBoolean(IsPresent);
        this.Islate = Boolean.parseBoolean(Islate);
        this.LateTime = Integer.parseInt(LateTime);
        this.IsLeave = Boolean.parseBoolean(IsLeave);
        this.IsAbsent = Boolean.parseBoolean(IsAbsent);
        this.Remarks = Remarks;
        this.Subject = Subject;
        this.Department = Department;
        this.Medium = Medium;
        this.Shift = Shift;
        this.Class = Class;
    }

    public void setUserID(String UserID)
    {
        this.UserID = Integer.parseInt(UserID);
    }

    public void setUserFullName(String UserFullName )
    {
        this.UserFullName = UserFullName;
    }

    public void setRFID(String RFID)
    {
        this.RFID = Long.parseLong(RFID);
    }

    public void setRollNo(String RollNo)
    {
        this.RollNo = Integer.parseInt(RollNo);
    }

    public void setSubjectID(String SubjectID)
    {
        this.SubjectID = Integer.parseInt(SubjectID);
    }

    public void setDepartmentID(String DepartmentID)
    {
        this.DepartmentID = Integer.parseInt(DepartmentID);
    }

    public void setSectionID(String SectionID)
    {
        this.SectionID = Integer.parseInt(SectionID);
    }

    public void setInstituteID(String[] InstituteID)
    {
        this.InstituteID = new int[InstituteID.length];
        for(int i = 0; i<InstituteID.length; i++)
        {
            this.InstituteID[i] = Integer.parseInt(InstituteID[i]);
        }
    }

    public void setMediumID(String MediumID)
    {
        this.MediumID = Integer.parseInt(MediumID);
    }

    public void setShiftID(String ShiftID)
    {
        this.ShiftID = Integer.parseInt(ShiftID);
    }

    public void setClassID(String ClassID)
    {
        this.ClassID = Integer.parseInt(ClassID);
    }

    public void setIsPresent(String IsPresent)
    {
        this.IsPresent = Boolean.parseBoolean(IsPresent);
    }

    public void setIslate(String Islate)
    {
        this.Islate = Boolean.parseBoolean(Islate);
    }

    public void setLateTime(String LateTime)
    {
        this.LateTime = Integer.parseInt(LateTime);
    }

    public void setIsLeave(String IsLeave)
    {
        this.IsLeave = Boolean.parseBoolean(IsLeave);
    }

    public void setIsAbsent(String IsAbsent)
    {
        this.IsAbsent = Boolean.parseBoolean(IsAbsent);
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
