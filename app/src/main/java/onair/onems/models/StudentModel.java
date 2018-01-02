package onair.onems.models;

import java.util.ArrayList;

/**
 * Created by User on 12/21/2017.
 */

public class StudentModel {

    private String UserID;
    private String UserFullName;
    private String RFID;
    private String RollNo;
    private long SubjectID;
    private long DepartmentID;
    private long SectionID;
    private String Section;
//    private long[] InstituteID;
    private long MediumID;
    private long ShiftID;
    private long ClassID;
    private long BoardID;
    private String Board;
    private String Brunch;
    private String Session;
    private long BrunchID;
    private long SessionID;
    private int IsPresent;
    private int Islate;
    private int LateTime;
    private int IsLeave;
    private int IsAbsent;
    private String Remarks;
    private String Subject;
    private String Department;
    private String Medium;
    private String Shift;
    private String Class;

    public StudentModel()
    {

    }

    public StudentModel(String UserFullName, String RollNo, String IsPresent, String Islate,
                        String LateTime, String IsLeave, String IsAbsent)
    {
//        this.UserID = Integer.parseInt(UserID);
        this.UserFullName = UserFullName;
//        this.RFID = Long.parseLong(RFID);
        this.RollNo = RollNo;
//        this.SubjectID = Integer.parseInt(SubjectID);
//        this.DepartmentID = Integer.parseInt(DepartmentID);
//        this.SectionID = Integer.parseInt(SectionID);
//        this.InstituteID = new int[InstituteID.length];
//        for(int i = 0; i<InstituteID.length; i++)
//        {
//            this.InstituteID[i] = Integer.parseInt(InstituteID[i]);
//        }
//        this.MediumID = Integer.parseInt(MediumID);
//        this.ShiftID = Integer.parseInt(ShiftID);
//        this.ClassID = Integer.parseInt(ClassID);
        this.IsPresent = Integer.parseInt(IsPresent);
        this.Islate = Integer.parseInt(Islate);
        this.LateTime = Integer.parseInt(LateTime);
        this.IsLeave = Integer.parseInt(IsLeave);
        this.IsAbsent = Integer.parseInt(IsAbsent);
//        this.Remarks = Remarks;
//        this.Subject = Subject;
//        this.Department = Department;
//        this.Medium = Medium;
//        this.Shift = Shift;
//        this.Class = Class;
//        ,
//        String[] InstituteID
    }

    public StudentModel(String UserID, String UserFullName, String RFID, String RollNo,
                        String SubjectID, String DepartmentID, String SectionID, String Section,
                        String MediumID, String ShiftID, String ClassID,
                        String BoardID, String Board, String Brunch, String Session, String BrunchID,
                        String SessionID, String IsPresent, String Islate, String LateTime,
                        String IsLeave, String IsAbsent, String Remarks, String Subject,
                        String Department, String Medium, String Shift, String Class)
    {
        this.UserID = UserID;
        this.UserFullName = UserFullName;
        this.RFID = RFID;
        this.RollNo = RollNo;
        this.SubjectID = Long.parseLong(SubjectID);
        this.DepartmentID = Long.parseLong(DepartmentID);
        this.SectionID = Long.parseLong(SectionID);
        this.Section = Section;
//        this.InstituteID = new long[InstituteID.length];
//        for(int i = 0; i<InstituteID.length; ++i)
//        {
//            this.InstituteID[i] = Long.parseLong(InstituteID[i]);
//        }
        this.MediumID = Long.parseLong(MediumID);
        this.ShiftID = Long.parseLong(ShiftID);
        this.ClassID = Long.parseLong(ClassID);
        this.BoardID = Long.parseLong(BoardID);
        this.Board = Board;
        this.Brunch = Brunch;
        this.Session = Session;
        this.BrunchID = Long.parseLong(BrunchID);
        this.SessionID = Long.parseLong(SessionID);
        this.IsPresent = Integer.parseInt(IsPresent);
        this.Islate = Integer.parseInt(Islate);
        this.LateTime = Integer.parseInt(LateTime);
        this.IsLeave = Integer.parseInt(IsLeave);
        this.IsAbsent = Integer.parseInt(IsAbsent);
        this.Remarks = Remarks;
        this.Subject = Subject;
        this.Department = Department;
        this.Medium = Medium;
        this.Shift = Shift;
        this.Class = Class;
    }

    public void setUserID(String UserID)
    {
        this.UserID = UserID;
    }

    public void setUserFullName(String UserFullName)
    {
        this.UserFullName = UserFullName;
    }

    public void setRFID(String RFID)
    {
        this.RFID = RFID;
    }

    public void setRollNo(String RollNo)
    {
        this.RollNo = RollNo;
    }

    public void setSubjectID(String SubjectID)
    {
        this.SubjectID = Long.parseLong(SubjectID);
    }

    public void setDepartmentID(String DepartmentID)
    {
        this.DepartmentID = Long.parseLong(DepartmentID);
    }

    public void setSectionID(String SectionID)
    {
        this.SectionID = Long.parseLong(SectionID);
    }

    public void setSection(String Section)
    {
        this.Section = Section;
    }

//    public void setInstituteID(String[] InstituteID)
//    {
//        this.InstituteID = new long[InstituteID.length];
//        for(int i = 0; i<InstituteID.length; i++)
//        {
//            this.InstituteID[i] = Long.parseLong(InstituteID[i]);
//        }
//    }

    public void setMediumID(String MediumID)
    {
        this.MediumID = Long.parseLong(MediumID);
    }

    public void setShiftID(String ShiftID)
    {
        this.ShiftID = Long.parseLong(ShiftID);
    }

    public void setClassID(String ClassID)
    {
        this.ClassID = Long.parseLong(ClassID);
    }

    public void setBoardID(String BoardID)
    {
        this.BoardID = Long.parseLong(BoardID);
    }

    public void setBoard(String Board)
    {
        this.Board = Board;
    }

    public void setBrunch(String Brunch)
    {
        this.Brunch = Brunch;
    }

    public void setSession(String Session)
    {
        this.Session = Session;
    }

    public void setBrunchID(String BrunchID)
    {
        this.BrunchID = Long.parseLong(BrunchID);
    }

    public void setSessionID(String SessionID)
    {
        this.SessionID = Long.parseLong(SessionID);
    }

    public void setIsPresent(String IsPresent)
    {
        this.IsPresent = Integer.parseInt(IsPresent);
    }

    public void setIslate(String Islate)
    {
        this.Islate = Integer.parseInt(Islate);
    }

    public void setLateTime(String LateTime)
    {
        this.LateTime = Integer.parseInt(LateTime);
    }

    public void setIsLeave(String IsLeave)
    {
        this.IsLeave = Integer.parseInt(IsLeave);
    }

    public void setIsAbsent(String IsAbsent)
    {
        this.IsAbsent = Integer.parseInt(IsAbsent);
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

    public String getUserID()
    {
        return UserID;
    }

    public String getUserFullName()
    {
        return UserFullName;
    }

    public String getRFID()
    {
        return RFID;
    }

    public String getRollNo()
    {
        return RollNo;
    }

    public long getSubjectID()
    {
        return SubjectID;
    }

    public long getDepartmentID()
    {
        return DepartmentID;
    }

    public long getSectionID()
    {
        return SectionID;
    }

    public String getSection()
    {
        return Section;
    }

//    public long[] getInstituteID()
//    {
//        return InstituteID;
//    }

    public long getMediumID()
    {
        return MediumID;
    }

    public long getShiftID()
    {
        return ShiftID;
    }

    public long getClassID()
    {
        return ClassID;
    }

    public long getBoardID()
    {
        return BoardID;
    }

    public String getBoard()
    {
        return Board;
    }

    public String getBrunch()
    {
        return Brunch;
    }

    public String getSession()
    {
        return Session;
    }

    public long getBrunchID()
    {
        return BrunchID;
    }

    public long getSessionID()
    {
        return SessionID;
    }

    public int getIsPresent()
    {
        return IsPresent;
    }

    public int getIslate()
    {
        return Islate;
    }

    public int getLateTime()
    {
        return LateTime;
    }

    public int getIsLeave()
    {
        return IsLeave;
    }

    public int getIsAbsent()
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
