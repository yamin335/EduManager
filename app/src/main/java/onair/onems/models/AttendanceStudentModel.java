package onair.onems.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/21/2017.
 */

public class AttendanceStudentModel {

    private String UserFullName;
    @SerializedName("UserID")
    @Expose
    private String UserID;
    @SerializedName("RFID")
    @Expose
    private String RFID;
    @SerializedName("RollNo")
    @Expose
    private String RollNo;
    @SerializedName("SubjectID")
    @Expose
    private long SubjectID;
    @SerializedName("DepartmentID")
    @Expose
    private long DepartmentID;
    @SerializedName("SectionID")
    @Expose
    private long SectionID;
    @SerializedName("MediumID")
    @Expose
    private long MediumID;
    @SerializedName("ShiftID")
    @Expose
    private long ShiftID;
    @SerializedName("Remarks")
    @Expose
    private String Remarks;
    @SerializedName("ClassID")
    @Expose
    private long ClassID;
    @SerializedName("BoardID")
    @Expose
    private long BoardID;
    @SerializedName("BrunchID")
    @Expose
    private long BrunchID;
    @SerializedName("SessionID")
    @Expose
    private long SessionID;
    @SerializedName("IsPresent")
    @Expose
    private int IsPresent;
    @SerializedName("Islate")
    @Expose
    private int Islate;
    @SerializedName("LateTime")
    @Expose
    private int LateTime;
    @SerializedName("IsLeave")
    @Expose
    private int IsLeave;
    @SerializedName("IsAbsent")
    @Expose
    private int IsAbsent;

    public AttendanceStudentModel()
    {

    }

    public AttendanceStudentModel(String UserFullName, String UserID, String RFID, String RollNo, String SubjectID,
                                  String DepartmentID, String SectionID, String MediumID, String ShiftID,
                                  String Remarks, String ClassID, String BoardID, String BrunchID,
                                  String SessionID, String IsPresent, String Islate, String LateTime,
                                  String IsLeave, String IsAbsent)
    {
        this.UserFullName = UserFullName;
        this.UserID = UserID;
        this.RFID = RFID;
        this.RollNo = RollNo;

        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }

        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = 0;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }

        if(SectionID.equals("null"))
        {
            this.SectionID = 0;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }

        if(MediumID.equals("null"))
        {
            this.MediumID = 0;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }

        if(ShiftID.equals("null"))
        {
            this.ShiftID = 0;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }

        this.Remarks = Remarks;

        if(ClassID.equals("null"))
        {
            this.ClassID = 0;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }

        if(BoardID.equals("null"))
        {
            this.BoardID = 0;
        }
        else
        {
            this.BoardID = Long.parseLong(BoardID);
        }

        if(BrunchID.equals("null"))
        {
            this.BrunchID = 0;
        }
        else
        {
            this.BrunchID = Long.parseLong(BrunchID);
        }

        if(SessionID.equals("null"))
        {
            this.SessionID = 0;
        }
        else
        {
            this.SessionID = Long.parseLong(SessionID);
        }
        this.IsPresent = Integer.parseInt(IsPresent);
        this.Islate = Integer.parseInt(Islate);
        this.LateTime = Integer.parseInt(LateTime);
        this.IsLeave = Integer.parseInt(IsLeave);
        this.IsAbsent = Integer.parseInt(IsAbsent);
    }

    public void setUserFullName(String UserFullName)
    {
        this.UserFullName = UserFullName;
    }

    public void setUserID(String UserID)
    {
        this.UserID = UserID;
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
        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }
    }

    public void setDepartmentID(String DepartmentID)
    {
        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = 0;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public void setSectionID(String SectionID)
    {
        if(SectionID.equals("null"))
        {
            this.SectionID = 0;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }
    }

    public void setMediumID(String MediumID)
    {
        if(MediumID.equals("null"))
        {
            this.MediumID = 0;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }
    }

    public void setShiftID(String ShiftID)
    {
        if(ShiftID.equals("null"))
        {
            this.ShiftID = 0;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }
    }

    public void setRemarks(String Remarks)
    {
        this.Remarks = Remarks;
    }

    public void setClassID(String ClassID)
    {
        if(ClassID.equals("null"))
        {
            this.ClassID = 0;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }
    }

    public void setBoardID(String BoardID)
    {
        if(BoardID.equals("null"))
        {
            this.BoardID = 0;
        }
        else
        {
            this.BoardID = Long.parseLong(BoardID);
        }
    }

    public void setBrunchID(String BrunchID)
    {
        if(BrunchID.equals("null"))
        {
            this.BrunchID = 0;
        }
        else
        {
            this.BrunchID = Long.parseLong(BrunchID);
        }
    }

    public void setSessionID(String SessionID)
    {
        if(SessionID.equals("null"))
        {
            this.SessionID = 0;
        }
        else
        {
            this.SessionID = Long.parseLong(SessionID);
        }
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

    public String getUserFullName()
    {
        return UserFullName;
    }

    public String getUserID()
    {
        return UserID;
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

    public long getMediumID()
    {
        return MediumID;
    }

    public long getShiftID()
    {
        return ShiftID;
    }

    public String getRemarks()
    {
        return Remarks;
    }

    public long getClassID()
    {
        return ClassID;
    }

    public long getBoardID()
    {
        return BoardID;
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
}
