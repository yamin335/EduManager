package onair.onems.models;

/**
 * Created by User on 1/29/2018.
 */

public class GenderModel {
    private long GenderID = -2;
    private String GenderName ="";

    public GenderModel()
    {

    }

    public GenderModel(String GenderID, String GenderName)
    {
        if(GenderID.equals("null"))
        {
            this.GenderID = -2;
        }
        else
        {
            this.GenderID = Long.parseLong(GenderID);
        }

        this.GenderName = GenderName;
    }

    public void setGenderID(String GenderID)
    {
        if(GenderID.equals("null"))
        {
            this.GenderID = -2;
        }
        else
        {
            this.GenderID = Long.parseLong(GenderID);
        }
    }

    public long getGenderID()
    {
        return this.GenderID;
    }

    public void setGenderName(String GenderName)
    {
        this.GenderName = GenderName;
    }

    public String getGenderName()
    {
        return this.GenderName;
    }
}
