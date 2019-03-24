package onair.onems.models;

public class InstituteDepartmentModel {

    private long DepartmentID = 0;
    private String DepartmentName = "";

    public InstituteDepartmentModel() {
        this.DepartmentID = 0;
        this.DepartmentName = "";
    }

    public InstituteDepartmentModel(String DepartmentID, String DepartmentName) {
        if(DepartmentID.equals("null")) {
            this.DepartmentID = 0;
        } else {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
        this.DepartmentName = DepartmentName;
    }

    public void setDepartmentID(String DepartmentID) {
        if(DepartmentID.equals("null")) {
            this.DepartmentID = 0;
        } else {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public long getDepartmentID() {
        return DepartmentID;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }
}