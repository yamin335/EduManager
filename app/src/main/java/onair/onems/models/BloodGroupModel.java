package onair.onems.models;

public class BloodGroupModel {

    private long BloodGroupID = -2;
    private String BloodGroupName = "";
    public BloodGroupModel()
    {
        this.BloodGroupID = -2;
        this.BloodGroupName = "";
    }

    public BloodGroupModel(String BloodGroupID, String BloodGroupName)
    {
        if(BloodGroupID.equals("null"))
        {
            this.BloodGroupID = -2;
        }
        else
        {
            this.BloodGroupID = Long.parseLong(BloodGroupID);
        }
        this.BloodGroupName = BloodGroupName;
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

    public void setBloodGroupName(String BloodGroupName)
    {
        this.BloodGroupName = BloodGroupName;
    }

    public long getBloodGroupID()
    {
        return BloodGroupID;
    }

    public String getBloodGroupName()
    {
        return BloodGroupName;
    }

}
