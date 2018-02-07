package onair.onems.models;

/**
 * Created by User on 1/29/2018.
 */

public class ReligionModel {

    private long ReligionID = -2;
    private String ReligionName ="";

    public ReligionModel()
    {

    }

    public ReligionModel(String ReligionID, String ReligionName)
    {
        if(ReligionID.equals("null"))
        {
            this.ReligionID = -2;
        }
        else
        {
            this.ReligionID = Long.parseLong(ReligionID);
        }

        this.ReligionName = ReligionName;
    }

    public void setReligionID(String ReligionID)
    {
        if(ReligionID.equals("null"))
        {
            this.ReligionID = -2;
        }
        else
        {
            this.ReligionID = Long.parseLong(ReligionID);
        }
    }

    public long getReligionID()
    {
        return this.ReligionID;
    }

    public void setReligionName(String ReligionName)
    {
        this.ReligionName = ReligionName;
    }

    public String getReligionName()
    {
        return this.ReligionName;
    }
}
