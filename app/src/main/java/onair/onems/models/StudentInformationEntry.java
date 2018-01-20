package onair.onems.models;

/**
 * Created by User on 1/13/2018.
 */

public class StudentInformationEntry {
    private long UserClassID = 0;
    private long UserID = 0;
    private String RFID = "";
    private String RollNo = "";
    private String StudentNo = "";
    private long SectionID = -2;
    private long ClassID = -2;
    private long BrunchID = 0;
    private long ShiftID = -2;
    private String Remarks = "";
    private long InstituteID = 0;
    private long UserTypeID = 3;
    private long GenderID = 0;
    private String UserName = "";
    private String PhoneNo = "";
    private String EmailID = "";
    private String ImageUrl = "";
    private String FingerUrl = "";
    private String SignatureUrl = "";
    private String Guardian = "";
    private String GuardianPhone = "";
    private String GuardianEmailID = "";
    private String DOB = "";
    private String PreAddress = "";
    private long ReligionID = 0;
    private long CreateBy = 0;
    private String CreateOn = "";
    private String CreatePc = "";
    private long UpdateBy = 0;
    private String UpdateOn = "";
    private String UpdatePc = "";
    private long IsDeleted = 0;
    private long DeleteBy = 0;
    private String DeleteOn = "";
    private String DeletePc = "";
    private boolean IsImageCaptured = false;
    private long DepartmentID = -2;
    private long MediumID = -2;

    /**
     * No args constructor for use in serialization
     *
     */

    public StudentInformationEntry() {
    }

    public long getUserClassID() {
        return UserClassID;
    }

    public void setUserClassID(String UserClassID) {

        if(UserClassID.equals("null"))
        {
            this.UserClassID = 0;
        }
        else
        {
            this.UserClassID = Long.parseLong(UserClassID);
        }
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {

        if(UserID.equals("null"))
        {
            this.UserID = 0;
        }
        else
        {
            this.UserID = Long.parseLong(UserID);
        }
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
            this.SectionID = -2;
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
            this.ClassID = -2;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
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
            this.ShiftID = -2;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
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

    public long getReligionID() {
        return ReligionID;
    }

    public void setReligionID(String ReligionID) {

        if(ReligionID.equals("null"))
        {
            this.ReligionID = 0;
        }
        else
        {
            this.ReligionID = Long.parseLong(ReligionID);
        }
    }

    public long getCreateBy() {
        return CreateBy;
    }

    public void setCreateBy(String CreateBy) {

        if(CreateBy.equals("null"))
        {
            this.CreateBy = 0;
        }
        else
        {
            this.CreateBy = Long.parseLong(CreateBy);
        }
    }

    public String getCreateOn() {
        return CreateOn;
    }

    public void setCreateOn(String CreateOn) {
        this.CreateOn = CreateOn;
    }

    public String getCreatePc() {
        return CreatePc;
    }

    public void setCreatePc(String CreatePc) {
        this.CreatePc = CreatePc;
    }

    public long getUpdateBy() {
        return UpdateBy;
    }

    public void setUpdateBy(String UpdateBy) {

        if(UpdateBy.equals("null"))
        {
            this.UpdateBy = 0;
        }
        else
        {
            this.UpdateBy = Long.parseLong(UpdateBy);
        }
    }

    public String getUpdateOn() {
        return UpdateOn;
    }

    public void setUpdateOn(String UpdateOn) {
        this.UpdateOn = UpdateOn;
    }

    public String getUpdatePc() {
        return UpdatePc;
    }

    public void setUpdatePc(String UpdatePc) {
        this.UpdatePc = UpdatePc;
    }

    public long getIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(String IsDeleted) {

        if(IsDeleted.equals("null"))
        {
            this.IsDeleted = 0;
        }
        else
        {
            this.IsDeleted = Long.parseLong(IsDeleted);
        }
    }

    public long getDeleteBy() {
        return DeleteBy;
    }

    public void setDeleteBy(String DeleteBy) {

        if(DeleteBy.equals("null"))
        {
            this.DeleteBy = 0;
        }
        else
        {
            this.DeleteBy = Long.parseLong(DeleteBy);
        }
    }

    public String getDeleteOn() {
        return DeleteOn;
    }

    public void setDeleteOn(String DeleteOn) {
        this.DeleteOn = DeleteOn;
    }

    public String getDeletePc() {
        return DeletePc;
    }

    public void setDeletePc(String DeletePc) {
        this.DeletePc = DeletePc;
    }

    public boolean getIsImageCaptured() {
        return IsImageCaptured;
    }

    public void setIsImageCaptured(boolean IsImageCaptured) {
        this.IsImageCaptured = IsImageCaptured;
    }

    public long getDepartmentID() {
        return DepartmentID;
    }

    public void setDepartmentID(String DepartmentID) {

        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = -2;
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
            this.MediumID = -2;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }
    }

}
