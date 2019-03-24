package onair.onems.models;

public class ReportAllStudentRowModel {
    private String UserID = "";
    private String UserName = "";
    private String RollNo = "";
    private String ClassName = "";
    private String SectionName = "";
    private String DepartmentName = "";
    private String RFID = "";
    private String ImageUrl = "";

    public ReportAllStudentRowModel()
    {

    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String RollNo) {
        this.RollNo = RollNo;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
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

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }
}
