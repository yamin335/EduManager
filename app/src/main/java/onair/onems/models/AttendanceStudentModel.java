package onair.onems.models;

import java.util.ArrayList;

public class AttendanceStudentModel {

    private String SubAtdDetailID;

    private String SubAtdID;

    private String UserFullName;

    private String UserID;

    private String RFID;

    private String RollNo;

    private long SubjectID;

    private long DepartmentID;

    private long SectionID;

    private String Section;

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

    private String DisplayDate;

    private boolean isReadOnly;

    private int IsPresentID;

    private String UserFirstName;

    private String UserLastName;

    private String ImageUrl;

    private ArrayList<Long> InstituteID;

    public AttendanceStudentModel() {

    }

    public AttendanceStudentModel(String SubAtdDetailID, String SubAtdID, String UserFullName,
                                  String UserID, String RFID, String RollNo, String SubjectID,
                                  String DepartmentID, String SectionID, String Section,
                                  String MediumID, String ShiftID, String ClassID, String BoardID,
                                  String Board, String Brunch, String Session, String BrunchID,
                                  String SessionID, String IsPresent, String Islate, String LateTime,
                                  String IsLeave, String IsAbsent, String Remarks, String Subject,
                                  String Department, String Medium, String Shift, String Class,
                                  String DisplayDate, String isReadOnly, String IsPresentID, String UserFirstName, String UserLastName, String ImageUrl, ArrayList<Long> InstituteID) {
        if(SubAtdDetailID.equals("null")) {
            this.SubAtdDetailID = "0";
        } else {
            this.SubAtdDetailID = SubAtdDetailID;
        }

        if(SubAtdID.equals("null")) {
            this.SubAtdID = "0";
        } else {
            this.SubAtdID = SubAtdID;
        }

        this.UserFullName = UserFullName;
        this.UserID = UserID;
        this.RFID = RFID;
        this.RollNo = RollNo;

        if(SubjectID.equals("null")) {
            this.SubjectID = -2;
        } else {
            this.SubjectID = Long.parseLong(SubjectID);
        }

        if(DepartmentID.equals("null")) {
            this.DepartmentID = -2;
        } else {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }

        if(SectionID.equals("null")) {
            this.SectionID = -2;
        } else {
            this.SectionID = Long.parseLong(SectionID);
        }

        this.Section = Section;

        if(MediumID.equals("null")) {
            this.MediumID = -2;
        } else {
            this.MediumID = Long.parseLong(MediumID);
        }

        if(ShiftID.equals("null")) {
            this.ShiftID = -2;
        } else {
            this.ShiftID = Long.parseLong(ShiftID);
        }

        if(ClassID.equals("null")) {
            this.ClassID = -2;
        } else {
            this.ClassID = Long.parseLong(ClassID);
        }

        if(BoardID.equals("null")) {
            this.BoardID = -2;
        } else {
            this.BoardID = Long.parseLong(BoardID);
        }

        this.Board = Board;
        this.Brunch = Brunch;
        this.Session = Session;

        if(BrunchID.equals("null")) {
            this.BrunchID = -2;
        } else {
            this.BrunchID = Long.parseLong(BrunchID);
        }

        if(SessionID.equals("null")) {
            this.SessionID = -2;
        } else {
            this.SessionID = Long.parseLong(SessionID);
        }

        switch (IsPresent) {
            case "null":
//                if(this.SubAtdDetailID.equalsIgnoreCase("0") && this.SubAtdID.equalsIgnoreCase("0"))
                this.IsPresent = 0;
                break;
            case "0":
//                if(this.SubAtdDetailID.equalsIgnoreCase("0") && this.SubAtdID.equalsIgnoreCase("0"))
                this.IsPresent = Integer.parseInt(IsPresent);
                break;
            default:
                this.IsPresent = Integer.parseInt(IsPresent);
                break;
        }

        if(Islate.equals("null")) {
            this.Islate = 0;
        } else {
            this.Islate = Integer.parseInt(Islate);
        }

        if(LateTime.equals("null")) {
            this.LateTime = 0;
        } else {
            this.LateTime = Integer.parseInt(LateTime);
        }

        if(IsLeave.equals("null")) {
            this.IsLeave = 0;
        } else {
            this.IsLeave = Integer.parseInt(IsLeave);
        }

        if(IsAbsent.equals("null")) {
            this.IsAbsent = 0;
        } else {
            this.IsAbsent = Integer.parseInt(IsAbsent);
        }

        this.Remarks = Remarks;
        this.Subject = Subject;
        this.Department = Department;
        this.Medium = Medium;
        this.Shift = Shift;
        this.Class = Class;
        this.DisplayDate = DisplayDate;

        if(isReadOnly.equals("null")) {
            this.isReadOnly = false;
        } else {
            this.isReadOnly = Boolean.parseBoolean(isReadOnly);
        }

        if(IsPresentID.equals("null")) {
            this.IsPresentID = 0;
        } else {
            this.IsPresentID = Integer.parseInt(IsPresentID);
        }

        this.UserFirstName = UserFirstName;
        this.UserLastName = UserLastName;
        this.ImageUrl = ImageUrl;
        this.InstituteID = InstituteID;
    }

    public void setSubAtdDetailID(String SubAtdDetailID) {
        this.SubAtdDetailID = SubAtdDetailID;
    }

    public void setSubAtdID(String SubAtdID) {
        this.SubAtdID = SubAtdID;
    }

    public void setUserFullName(String UserFullName) {
        this.UserFullName = UserFullName;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public void setRollNo(String RollNo) {
        this.RollNo = RollNo;
    }

    public void setSubjectID(String SubjectID) {
        if(SubjectID.equals("null")) {
            this.SubjectID = -2;
        } else {
            this.SubjectID = Long.parseLong(SubjectID);
        }
    }

    public void setDepartmentID(String DepartmentID) {
        if(DepartmentID.equals("null")) {
            this.DepartmentID = -2;
        } else {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public void setSectionID(String SectionID) {
        if(SectionID.equals("null")) {
            this.SectionID = -2;
        } else {
            this.SectionID = Long.parseLong(SectionID);
        }
    }

    public void setMediumID(String MediumID) {
        if(MediumID.equals("null")) {
            this.MediumID = -2;
        } else {
            this.MediumID = Long.parseLong(MediumID);
        }
    }

    public void setShiftID(String ShiftID) {
        if(ShiftID.equals("null")) {
            this.ShiftID = -2;
        } else {
            this.ShiftID = Long.parseLong(ShiftID);
        }
    }

    public void setClassID(String ClassID) {
        if(ClassID.equals("null")) {
            this.ClassID = -2;
        } else {
            this.ClassID = Long.parseLong(ClassID);
        }
    }

    public void setBoardID(String BoardID) {
        if(BoardID.equals("null")) {
            this.BoardID = -2;
        } else {
            this.BoardID = Long.parseLong(BoardID);
        }
    }

    public void setBoard(String Board) {
        this.Board = Board;
    }

    public void setBrunch(String Brunch) {
        this.Brunch = Brunch;
    }

    public void setSession(String Session) {
        this.Session = Session;
    }

    public void setBrunchID(String BrunchID) {
        if(BrunchID.equals("null")) {
            this.BrunchID = -2;
        } else {
            this.BrunchID = Long.parseLong(BrunchID);
        }
    }

    public void setSessionID(String SessionID) {
        if(SessionID.equals("null")) {
            this.SessionID = -2;
        } else {
            this.SessionID = Long.parseLong(SessionID);
        }
    }

    public void setIsPresent(String IsPresent) {
        if(IsPresent.equals("null")) {
            this.IsPresent = 0;
        } else {
            this.IsPresent = Integer.parseInt(IsPresent);
        }
    }

    public void setIslate(String Islate) {
        if(Islate.equals("null")) {
            this.Islate = 0;
        } else {
            this.Islate = Integer.parseInt(Islate);
        }
    }

    public void setLateTime(String LateTime) {
        if(LateTime.equals("null")) {
            this.LateTime = 0;
        } else {
            this.LateTime = Integer.parseInt(LateTime);
        }
    }

    public void setIsLeave(String IsLeave) {
        if(IsLeave.equals("null")) {
            this.IsLeave = 0;
        } else {
            this.IsLeave = Integer.parseInt(IsLeave);
        }
    }

    public void setIsAbsent(String IsAbsent) {
        if(IsAbsent.equals("null")) {
            this.IsAbsent = 0;
        } else {
            this.IsAbsent = Integer.parseInt(IsAbsent);
        }
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

    public void setSubject(String Subject) {
        this.Subject = Subject;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

    public void setMedium(String Medium) {
        this.Medium = Medium;
    }

    public void setShift(String Shift) {
        this.Shift = Shift;
    }

    public void setClass(String Class) {
        this.Class = Class;
    }

    public void setDisplayDate(String DisplayDate) {
        this.DisplayDate = DisplayDate;
    }

    public void setisReadOnly(String isReadOnly) {
        if(isReadOnly.equals("null")) {
            this.isReadOnly = false;
        } else {
            this.isReadOnly = Boolean.parseBoolean(isReadOnly);
        }
    }

    public void setIsPresentID(String IsPresentID) {
        if(IsPresentID.equals("null")) {
            this.IsPresentID = 0;
        } else {
            this.IsPresentID = Integer.parseInt(IsPresentID);
        }
    }

    public String getDisplayDate() {
        return this.DisplayDate;
    }
    public boolean getisReadOnly() {
        return this.isReadOnly;
    }
    public int getIsPresentID() {
        return this.IsPresentID;
    }

    public String getSubAtdDetailID() {
        return this.SubAtdDetailID;
    }

    public String getSubAtdID() {
        return this.SubAtdID;
    }

    public String getUserFullName() {
        return UserFullName;
    }

    public String getUserID() {
        return UserID;
    }

    public String getRFID() {
        return RFID;
    }

    public String getRollNo() {
        return RollNo;
    }

    public long getSubjectID() {
        return SubjectID;
    }

    public long getDepartmentID() {
        return DepartmentID;
    }

    public long getSectionID() {
        return SectionID;
    }

    public String getSection() {
        return this.Section;
    }

    public long getMediumID() {
        return MediumID;
    }

    public long getShiftID() {
        return ShiftID;
    }

    public long getClassID() {
        return ClassID;
    }

    public long getBoardID() {
        return BoardID;
    }

    public String getBoard() {
        return this.Board;
    }

    public String getBrunch() {
        return this.Brunch;
    }

    public String getSession() {
        return this.Session;
    }

    public long getBrunchID() {
        return BrunchID;
    }

    public long getSessionID() {
        return SessionID;
    }

    public int getIsPresent() {
        return IsPresent;
    }

    public int getIslate() {
        return Islate;
    }

    public int getLateTime() {
        return LateTime;
    }

    public int getIsLeave() {
        return IsLeave;
    }

    public int getIsAbsent() {
        return IsAbsent;
    }


    public String getRemarks() {
        return Remarks;
    }

    public String getSubject() {
        return this.Subject;
    }

    public String getDepartment() {
        return this.Department;
    }

    public String getMedium() {
        return this.Medium;
    }

    public String getShift() {
        return this.Shift;
    }

    public String getClasss() {
        return this.Class;
    }
}
