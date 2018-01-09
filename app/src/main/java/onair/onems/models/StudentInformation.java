package onair.onems.models;

/**
 * Created by User on 1/7/2018.
 */

public class StudentInformation {

    private String UserClassID;
    private String UserID;
    private String RFID;
    private String RollNo;
    private String StudentNo;
    private long SectionID;
    private long ClassID;
    private long DepartmentID;
    private long MediumID;
    private long BrunchID;
    private long ShiftID;
    private long SessionID;
    private long BoardID;
    private String Remarks;
    private long InstituteID;
    private long UserTypeID;
    private long GenderID;
    private String UserName;
    private String PhoneNo;
    private String EmailID;
    private String ImageUrl;
    private String FingerUrl;
    private String SignatureUrl;
    private String Guardian;
    private String GuardianPhone;
    private String GuardianEmailID;
    private String SectionName;
    private String ClassName;
    private String DepartmentName;
    private String MameName;
    private String BrunchName;
    private String ShiftName;
    private String SessionName;
    private String BoardName;
    private String Gender;
    private String Religion;
    private String DOB;
    private String PreAddress;

    /**
     * No args constructor for use in serialization
     *
     */
    public StudentInformation() {
    }

    public StudentInformation(String UserClassID, String UserID, String RFID, String RollNo,
                              String StudentNo, String SectionID, String ClassID, String DepartmentID,
                              String MediumID, String BrunchID, String ShiftID, String SessionID,
                              String BoardID, String Remarks, String InstituteID, String UserTypeID,
                              String GenderID, String UserName, String PhoneNo, String EmailID,
                              String ImageUrl, String FingerUrl, String SignatureUrl, String Guardian,
                              String GuardianPhone, String GuardianEmailID, String SectionName,
                              String ClassName, String DepartmentName, String MameName, String BrunchName,
                              String ShiftName, String SessionName, String BoardName, String Gender,
                              String Religion, String DOB, String PreAddress) {
        super();
        this.UserClassID = UserClassID;
        this.UserID = UserID;
        this.RFID = RFID;
        this.RollNo = RollNo;
        this.StudentNo = StudentNo;
        if(SectionID.equals("null"))
        {
            this.SectionID = 0;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }

        if(ClassID.equals("null"))
        {
            this.ClassID = 0;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }

        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = 0;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }

        if(MediumID.equals("null"))
        {
            this.MediumID = 0;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }

        if(BrunchID.equals("null"))
        {
            this.BrunchID = 0;
        }
        else
        {
            this.BrunchID = Long.parseLong(BrunchID);
        }

        if(ShiftID.equals("null"))
        {
            this.ShiftID = 0;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }

        if(SessionID.equals("null"))
        {
            this.SessionID = 0;
        }
        else
        {
            this.SessionID = Long.parseLong(SessionID);
        }

        if(BoardID.equals("null"))
        {
            this.BoardID = 0;
        }
        else
        {
            this.BoardID = Long.parseLong(BoardID);
        }
        this.Remarks = Remarks;

        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }

        if(UserTypeID.equals("null"))
        {
            this.UserTypeID = 0;
        }
        else
        {
            this.UserTypeID = Long.parseLong(UserTypeID);
        }

        if(GenderID.equals("null"))
        {
            this.GenderID = 0;
        }
        else
        {
            this.GenderID = Long.parseLong(GenderID);
        }
        this.UserName = UserName;
        this.PhoneNo = PhoneNo;
        this.EmailID = EmailID;
        this.ImageUrl = ImageUrl;
        this.FingerUrl = FingerUrl;
        this.SignatureUrl = SignatureUrl;
        this.Guardian = Guardian;
        this.GuardianPhone = GuardianPhone;
        this.GuardianEmailID = GuardianEmailID;
        this.SectionName = SectionName;
        this.ClassName = ClassName;
        this.DepartmentName = DepartmentName;
        this.MameName = MameName;
        this.BrunchName = BrunchName;
        this.ShiftName = ShiftName;
        this.SessionName = SessionName;
        this.BoardName = BoardName;
        this.Gender = Gender;
        this.Religion = Religion;
        this.DOB = DOB;
        this.PreAddress = PreAddress;
    }

    public String getUserClassID() {
        return UserClassID;
    }

    public void setUserClassID(String UserClassID) {
        this.UserClassID = UserClassID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String RollNo) {
        this.RollNo = RollNo;
    }

    public String getStudentNo() {
        return StudentNo;
    }

    public void setStudentNo(String StudentNo) {
        this.StudentNo = StudentNo;
    }

    public long getSectionID() {
        return SectionID;
    }

    public void setSectionID(String SectionID) {
        if(SectionID.equals("null"))
        {
            this.SectionID = 0;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }
    }

    public long getClassID() {
        return ClassID;
    }

    public void setClassID(String ClassID) {
        if(ClassID.equals("null"))
        {
            this.ClassID = 0;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }
    }

    public long getDepartmentID() {
        return DepartmentID;
    }

    public void setDepartmentID(String DepartmentID) {
        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = 0;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public long getMediumID() {
        return MediumID;
    }

    public void setMediumID(String MediumID) {
        if(MediumID.equals("null"))
        {
            this.MediumID = 0;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }
    }

    public long getBrunchID() {
        return BrunchID;
    }

    public void setBrunchID(String BrunchID) {
        if(BrunchID.equals("null"))
        {
            this.BrunchID = 0;
        }
        else
        {
            this.BrunchID = Long.parseLong(BrunchID);
        }
    }

    public long getShiftID() {
        return ShiftID;
    }

    public void setShiftID(String ShiftID) {
        if(ShiftID.equals("null"))
        {
            this.ShiftID = 0;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }
    }

    public long getSessionID() {
        return SessionID;
    }

    public void setSessionID(String SessionID) {
        if(SessionID.equals("null"))
        {
            this.SessionID = 0;
        }
        else
        {
            this.SessionID = Long.parseLong(SessionID);
        }
    }

    public long getBoardID() {
        return BoardID;
    }

    public void setBoardID(String BoardID) {
        if(BoardID.equals("null"))
        {
            this.BoardID = 0;
        }
        else
        {
            this.BoardID = Long.parseLong(BoardID);
        }
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

    public long getInstituteID() {
        return InstituteID;
    }

    public void setInstituteID(String InstituteID) {
        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }
    }

    public long getUserTypeID() {
        return UserTypeID;
    }

    public void setUserTypeID(String UserTypeID) {
        if(UserTypeID.equals("null"))
        {
            this.UserTypeID = 0;
        }
        else
        {
            this.UserTypeID = Long.parseLong(UserTypeID);
        }
    }

    public long getGenderID() {
        return GenderID;
    }

    public void setGenderID(String GenderID) {
        if(GenderID.equals("null"))
        {
            this.GenderID = 0;
        }
        else
        {
            this.GenderID = Long.parseLong(GenderID);
        }
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String PhoneNo) {
        this.PhoneNo = PhoneNo;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String EmailID) {
        this.EmailID = EmailID;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getFingerUrl() {
        return FingerUrl;
    }

    public void setFingerUrl(String FingerUrl) {
        this.FingerUrl = FingerUrl;
    }

    public String getSignatureUrl() {
        return SignatureUrl;
    }

    public void setSignatureUrl(String SignatureUrl) {
        this.SignatureUrl = SignatureUrl;
    }

    public String getGuardian() {
        return Guardian;
    }

    public void setGuardian(String Guardian) {
        this.Guardian = Guardian;
    }

    public String getGuardianPhone() {
        return GuardianPhone;
    }

    public void setGuardianPhone(String GuardianPhone) {
        this.GuardianPhone = GuardianPhone;
    }

    public String getGuardianEmailID() {
        return GuardianEmailID;
    }

    public void setGuardianEmailID(String GuardianEmailID) {
        this.GuardianEmailID = GuardianEmailID;
    }

    public String getSectionName() {
        return SectionName;
    }

    public void setSectionName(String SectionName) {
        this.SectionName = SectionName;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public String getMameName() {
        return MameName;
    }

    public void setMameName(String MameName) {
        this.MameName = MameName;
    }

    public String getBrunchName() {
        return BrunchName;
    }

    public void setBrunchName(String BrunchName) {
        this.BrunchName = BrunchName;
    }

    public String getShiftName() {
        return ShiftName;
    }

    public void setShiftName(String ShiftName) {
        this.ShiftName = ShiftName;
    }

    public String getSessionName() {
        return SessionName;
    }

    public void setSessionName(String SessionName) {
        this.SessionName = SessionName;
    }

    public String getBoardName() {
        return BoardName;
    }

    public void setBoardName(String BoardName) {
        this.BoardName = BoardName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getReligion() {
        return Religion;
    }

    public void setReligion(String Religion) {
        this.Religion = Religion;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPreAddress() {
        return PreAddress;
    }

    public void setPreAddress(String PreAddress) {
        this.PreAddress = PreAddress;
    }

}