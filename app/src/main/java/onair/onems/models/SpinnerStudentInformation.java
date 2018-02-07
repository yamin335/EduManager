package onair.onems.models;

/**
 * Created by User on 1/7/2018.
 */

public class SpinnerStudentInformation {

    private String UserID = "";
    private String RollNo = "";
    private String UserName = "";
    private boolean IsImageCaptured = false;

    /**
     * No args constructor for use in serialization
     *
     */
    public SpinnerStudentInformation() {
    }

    public SpinnerStudentInformation(String UserID, String RollNo, String UserName, boolean IsImageCaptured){
        this.UserID = UserID;
        this.RollNo = RollNo;
        this.UserName = UserName;
        this.IsImageCaptured = IsImageCaptured;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String RollNo) {
        this.RollNo = RollNo;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public boolean getIsImageCaptured() {
        return IsImageCaptured;
    }

    public void setIsImageCaptured(boolean IsImageCaptured) {
        this.IsImageCaptured = IsImageCaptured;
    }

}