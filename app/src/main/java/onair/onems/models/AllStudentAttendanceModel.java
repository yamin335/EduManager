package onair.onems.models;


public class AllStudentAttendanceModel {
    private String UserID = "";
    private String UserFullName = "";
    private String RFID = "";
    private String RollNo = "";
    private long DepartmentID = 0;
    private long SectionID = 0;
    private long MediumID = 0;
    private long ShiftID = 0;
    private long ClassID = 0;
    private String ImageUrl = "";

    public AllStudentAttendanceModel(){
        super();
    }

    public void setUserID(String UserID)
    {
        this.UserID = UserID;
    }

    public String getUserID()
    {
        return UserID;
    }

    public void setUserFullName(String UserFullName)
    {
        this.UserFullName = UserFullName;
    }

    public String getUserFullName()
    {
        return UserFullName;
    }

    public void setRFID(String RFID)
    {
        this.RFID = RFID;
    }

    public String getRFID()
    {
        return RFID;
    }

    public void setRollNo(String RollNo)
    {
        this.RollNo = RollNo;
    }

    public String getRollNo()
    {
        return RollNo;
    }

    public void setDepartmentID(String DepartmentID)
    {
        if(!DepartmentID.equalsIgnoreCase("null")){
            this.DepartmentID = Long.parseLong(DepartmentID);
        }else {
            this.DepartmentID = 0;
        }
    }

    public long getDepartmentID()
    {
        return DepartmentID;
    }

    public void setSectionID(String SectionID)
    {
        if(!SectionID.equalsIgnoreCase("null")){
            this.SectionID = Long.parseLong(SectionID);
        }else {
            this.SectionID = 0;
        }
    }

    public long getSectionID()
    {
        return SectionID;
    }

    public void setMediumID(String MediumID)
    {
        if(!MediumID.equalsIgnoreCase("null")){
            this.MediumID = Long.parseLong(MediumID);
        }else {
            this.MediumID = 0;
        }
    }

    public long getMediumID()
    {
        return MediumID;
    }

    public void setShiftID(String ShiftID)
    {
        if(!ShiftID.equalsIgnoreCase("null")){
            this.ShiftID = Long.parseLong(ShiftID);
        }else {
            this.ShiftID = 0;
        }
    }

    public long getShiftID()
    {
        return ShiftID;
    }

    public void setClassID(String ClassID)
    {
        if(!ClassID.equalsIgnoreCase("null")){
            this.ClassID = Long.parseLong(ClassID);
        }else {
            this.ClassID = 0;
        }
    }

    public long getClassID()
    {
        return ClassID;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
}
