package onair.onems.models;

/**
 * Created by User on 1/18/2018.DepartmentModel
 */

public class DepartmentModel {

    private long DepartmentID = -2;
    private String DepartmentName = "";

    public DepartmentModel()
    {
        this.DepartmentID = -2;
        this.DepartmentName = "";
    }

    public DepartmentModel(String DepartmentID, String DepartmentName)
    {
        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = -2;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
        this.DepartmentName = DepartmentName;
    }

    public void setDepartmentID(String DepartmentID)
    {
        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = -2;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public void setDepartmentName(String DepartmentName)
    {
        this.DepartmentName = DepartmentName;
    }

    public long getDepartmentID()
    {
        return DepartmentID;
    }

    public String getDepartmentName()
    {
        return DepartmentName;
    }
}