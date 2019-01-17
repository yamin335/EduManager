package onair.onems.models;

public class ShiftModel {

    private long ShiftID = -2;
    private String ShiftName = "";

    public ShiftModel()
    {
        this.ShiftID = -2;
        this.ShiftName = "";
    }

    public ShiftModel(String ShiftID, String ShiftName)
    {
        if(ShiftID.equals("null"))
        {
            this.ShiftID = -2;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }
        this.ShiftName = ShiftName;
    }

    public void setShiftID(String ShiftID)
    {
        if(ShiftID.equals("null"))
        {
            this.ShiftID = -2;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }
    }

    public void setShiftName(String ShiftName)
    {
        this.ShiftName = ShiftName;
    }

    public long getShiftID()
    {
        return ShiftID;
    }

    public String getShiftName()
    {
        return ShiftName;
    }
}
