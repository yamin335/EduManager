package onair.onems.models;

public class StudentInformationEntry {
    private long UserClassID = -2;
    private long UserID = 0;
    private String RFID = "";
    private String RollNo = "";
    private String StudentNo = "";
    private long SectionID = -2;
    private long ClassID = -2;
    private long BrunchID = -2;
    private long ShiftID = -2;
    private String Remarks = "";
    private long InstituteID = 0;
    private long UserTypeID = 3;
    private long GenderID = -2;
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
    private long ReligionID = -2;
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
    private String UserNo = "";
    private long UserTitleID = -2;
    private String UserFirstName = "";
    private String UserMiddleName = "";
    private String UserLastName = "";
    private String NickName = "";
    private String SkypeID = "";
    private String FacebookID = "";
    private String WhatsApp = "";
    private String Viber = "";
    private String LinkedIN = "";
    private String ParAddress = "";
    private String ParThana = "";
    private String ParPostCode = "";
    private long ParCountryID = -2;
    private long ParStateID = -2;
    private long ParCityID = -2;
    private String PreThana = "";
    private String PrePostCode = "";
    private long PreCountryID = -2;
    private long PreStateID = -2;
    private long PreCityID = -2;
    private String MobileNo = "";
    private String UniqueIdentity = "";
    private long BloodGroupID = -2;
    private int Weigth = 0;
    private int Height = 0;
    private String BirthCertificate = "";
    private String PassportNO = "";
    private String NID = "";
    private boolean IsActive = true;
    private long StatusID = -2;
    private String GuardianMobileNo = "";
    private String GuardianUserFirstName = "";
    private String GuardianUserMiddleName = "";
    private String GuardianUserLastName = "";
    private String GuardianNickName = "";
    private String GuardianUniqueIdentity = "";
    private long GuardianBloodGroupID = -2;
    private String GuardianPassportNO = "";
    private String GuardianNID = "";
    private long RelationID = -2;
    private boolean IsLocalGuardian = true;
    private boolean IsActiveFamily = true;
    private long SessionID = -2;
    private long BoardID = -2;
    private boolean IsActiveStudent = true;

    public StudentInformationEntry() {

    }

    public long getUserClassID() {
        return UserClassID;
    }

    public void setUserClassID(String UserClassID) {

        if(UserClassID.equals("null"))
        {
            this.UserClassID = -2;
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
            this.BrunchID = -2;
        }
        else
        {
            this.BrunchID = Long.parseLong(BrunchID);
        }
    }

    public long getBoardID() {
        return BoardID;
    }

    public void setBoardID(String BoardID) {

        if(BoardID.equals("null"))
        {
            this.BoardID = -2;
        }
        else
        {
            this.BoardID = Long.parseLong(BoardID);
        }
    }

    public long getSessionID() {
        return SessionID;
    }

    public void setSessionID(String SessionID) {

        if(SessionID.equals("null"))
        {
            this.SessionID = -2;
        }
        else
        {
            this.SessionID = Long.parseLong(SessionID);
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
            this.UserTypeID = -2;
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
            this.GenderID = -2;
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
            this.ReligionID = -2;
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

    public void setBloodGroupID(String BloodGroupID)
    {
        if(BloodGroupID.equals("null"))
        {
            this.BloodGroupID = -2;
        }
        else
        {
            this.BloodGroupID = Long.parseLong(BloodGroupID);
        }
    }

    public long getBloodGroupID()
    {
        return BloodGroupID;
    }

    public String getUserNo() {
        return UserNo;
    }

    public void setUserNo(String UserNo) {
        this.UserNo = UserNo;
    }

    public long getUserTitleID() {
        return UserTitleID;
    }

    public void setUserTitleID(String UserTitleID) {
        if(UserTitleID.equals("null"))
        {
            this.UserTitleID = -2;
        }
        else
        {
            this.UserTitleID = Long.parseLong(UserTitleID);
        }
    }

    public String getUserFirstName() {
        return UserFirstName;
    }

    public void setUserFirstName(String UserFirstName) {
        this.UserFirstName = UserFirstName;
    }

    public String getUserMiddleName() {
        return UserMiddleName;
    }

    public void setUserMiddleName(String UserMiddleName) {
        this.UserMiddleName = UserMiddleName;
    }

    public String getUserLastName() {
        return UserLastName;
    }

    public void setUserLastName(String UserLastName) {
        this.UserLastName = UserLastName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getSkypeID() {
        return SkypeID;
    }

    public void setSkypeID(String SkypeID) {
        this.SkypeID = SkypeID;
    }

    public String getFacebookID() {
        return FacebookID;
    }

    public void setFacebookID(String FacebookID) {
        this.FacebookID = FacebookID;
    }

    public String getWhatsApp() {
        return WhatsApp;
    }

    public void setWhatsApp(String WhatsApp) {
        this.WhatsApp = WhatsApp;
    }

    public String getViber() {
        return Viber;
    }

    public void setViber(String Viber) {
        this.Viber = Viber;
    }

    public String getLinkedIN() {
        return LinkedIN;
    }

    public void setLinkedIN(String LinkedIN) {
        this.LinkedIN = LinkedIN;
    }

    public String getParAddress() {
        return ParAddress;
    }

    public void setParAddress(String ParAddress) {
        this.ParAddress = ParAddress;
    }

    public String getParThana() {
        return ParThana;
    }

    public void setParThana(String ParThana) {
        this.ParThana = ParThana;
    }

    public String getParPostCode() {
        return ParPostCode;
    }

    public void setParPostCode(String ParPostCode) {
        this.ParPostCode = ParPostCode;
    }


    public long getParCountryID() {
        return ParCountryID;
    }

    public void setParCountryID(String ParCountryID) {
        if(ParCountryID.equals("null"))
        {
            this.ParCountryID = -2;
        }
        else
        {
            this.ParCountryID = Long.parseLong(ParCountryID);
        }
    }

    public long getParStateID() {
        return ParStateID;
    }

    public void setParStateID(String ParStateID) {
        if(ParStateID.equals("null"))
        {
            this.ParStateID = -2;
        }
        else
        {
            this.ParStateID = Long.parseLong(ParStateID);
        }
    }

    public long getParCityID() {
        return ParCityID;
    }

    public void setParCityID(String ParCityID) {
        if(ParCityID.equals("null"))
        {
            this.ParCityID = -2;
        }
        else
        {
            this.ParCityID = Long.parseLong(ParCityID);
        }
    }

    public String getPreThana() {
        return PreThana;
    }

    public void setPreThana(String PreThana) {
        this.PreThana = PreThana;
    }

    public String getPrePostCode() {
        return PrePostCode;
    }

    public void setPrePostCode(String PrePostCode) {
        this.PrePostCode = PrePostCode;
    }

    public long getPreCountryID() {
        return PreCountryID;
    }

    public void setPreCountryID(String PreCountryID) {
        if(PreCountryID.equals("null"))
        {
            this.PreCountryID = -2;
        }
        else
        {
            this.PreCountryID = Long.parseLong(PreCountryID);
        }
    }

    public long getPreStateID() {
        return PreStateID;
    }

    public void setPreStateID(String PreStateID) {
        if(PreStateID.equals("null"))
        {
            this.PreStateID = -2;
        }
        else
        {
            this.PreStateID = Long.parseLong(PreStateID);
        }
    }

    public long getPreCityID() {
        return PreCityID;
    }

    public void setPreCityID(String PreCityID) {
        if(PreCityID.equals("null"))
        {
            this.PreCityID = -2;
        }
        else
        {
            this.PreCityID = Long.parseLong(PreCityID);
        }
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
    }

    public String getUniqueIdentity() {
        return UniqueIdentity;
    }

    public void setUniqueIdentity(String UniqueIdentity) {
        this.UniqueIdentity = UniqueIdentity;
    }

    public int getWeigth() {
        return Weigth;
    }

    public void setWeigth(String Weigth) {
        if(Weigth.equals("null"))
        {
            this.Weigth = 0;
        }
        else
        {
            this.Weigth = Integer.parseInt(Weigth);
        }
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(String Height) {
        if(Height.equals("null"))
        {
            this.Height = 0;
        }
        else
        {
            this.Height = Integer.parseInt(Height);
        }
    }

    public String getBirthCertificate() {
        return BirthCertificate;
    }

    public void setBirthCertificate(String BirthCertificate) {
        this.BirthCertificate = BirthCertificate;
    }

    public String getPassportNO() {
        return PassportNO;
    }

    public void setPassportNO(String PassportNO) {
        this.PassportNO = PassportNO;
    }

    public String getNID() {
        return NID;
    }

    public void setNID(String NID) {
        this.NID = NID;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(String IsActive) {
        if(IsActive.equals("null"))
        {
            this.IsActive = true;
        }
        else
        {
            this.IsActive = Boolean.parseBoolean(IsActive);
        }
    }

    public long getStatusID() {
        return StatusID;
    }

    public void setStatusID(String StatusID) {
        if(StatusID.equals("null"))
        {
            this.StatusID = -2;
        }
        else
        {
            this.StatusID = Long.parseLong(StatusID);
        }
    }

    public String getGuardianMobileNo() {
        return GuardianMobileNo;
    }

    public void setGuardianMobileNo(String GuardianMobileNo) {
        this.GuardianMobileNo = GuardianMobileNo;
    }

    public String getGuardianUserFirstName() {
        return GuardianUserFirstName;
    }

    public void setGuardianUserFirstName(String GuardianUserFirstName) {
        this.GuardianUserFirstName = GuardianUserFirstName;
    }

    public String getGuardianUserMiddleName() {
        return GuardianUserMiddleName;
    }

    public void setGuardianUserMiddleName(String GuardianUserMiddleName) {
        this.GuardianUserMiddleName = GuardianUserMiddleName;
    }

    public String getGuardianUserLastName() {
        return GuardianUserLastName;
    }

    public void setGuardianUserLastName(String GuardianUserLastName) {
        this.GuardianUserLastName = GuardianUserLastName;
    }

    public String getGuardianNickName() {
        return GuardianNickName;
    }

    public void setGuardianNickName(String GuardianNickName) {
        this.GuardianNickName = GuardianNickName;
    }

    public String getGuardianUniqueIdentity() {
        return GuardianUniqueIdentity;
    }

    public void setGuardianUniqueIdentity(String GuardianUniqueIdentity) {
        this.GuardianUniqueIdentity = GuardianUniqueIdentity;
    }

    public long getGuardianBloodGroupID() {
        return GuardianBloodGroupID;
    }

    public void setGuardianBloodGroupID(String GuardianBloodGroupID) {
        if(GuardianBloodGroupID.equals("null"))
        {
            this.GuardianBloodGroupID = -2;
        }
        else
        {
            this.GuardianBloodGroupID = Long.parseLong(GuardianBloodGroupID);
        }
    }

    public String getGuardianPassportNO() {
        return GuardianPassportNO;
    }

    public void setGuardianPassportNO(String GuardianPassportNO) {
        this.GuardianPassportNO = GuardianPassportNO;
    }

    public String getGuardianNID() {
        return GuardianNID;
    }

    public void setGuardianNID(String GuardianNID) {
        this.GuardianNID = GuardianNID;
    }

    public long getRelationID() {
        return RelationID;
    }

    public void setRelationID(String RelationID) {
        if(RelationID.equals("null"))
        {
            this.RelationID = -2;
        }
        else
        {
            this.RelationID = Long.parseLong(RelationID);
        }
    }

    public boolean getIsLocalGuardian() {
        return IsLocalGuardian;
    }

    public void setIsLocalGuardian(String IsLocalGuardian) {
        if(IsLocalGuardian.equals("null"))
        {
            this.IsLocalGuardian = true;
        }
        else
        {
            this.IsLocalGuardian = Boolean.parseBoolean(IsLocalGuardian);
        }
    }

    public boolean getIsActiveFamily() {
        return IsActiveFamily;
    }

    public void setIsActiveFamily(String IsActiveFamily) {
        if(IsActiveFamily.equals("null"))
        {
            this.IsActiveFamily = true;
        }
        else
        {
            this.IsActiveFamily = Boolean.parseBoolean(IsActiveFamily);
        }
    }

    public boolean getIsActiveStudent() {
        return IsActiveStudent;
    }

    public void setIsActiveStudent(String IsActiveStudent) {
        if(IsActiveStudent.equals("null"))
        {
            this.IsActiveStudent = true;
        }
        else
        {
            this.IsActiveStudent = Boolean.parseBoolean(IsActiveStudent);
        }
    }

}
