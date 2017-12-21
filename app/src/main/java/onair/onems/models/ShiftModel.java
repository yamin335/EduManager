package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class ShiftModel {

    private int ShiftID;
    private String ShiftName;

    public ShiftModel(String ShiftID, String ShiftName)
    {
        this.ShiftID = Integer.parseInt(ShiftID);
        this.ShiftName = ShiftName;
    }

    public void setShiftID(String ShiftID)
    {
        this.ShiftID = Integer.parseInt(ShiftID);
    }

    public void setShiftName(String ShiftName)
    {
        this.ShiftName = ShiftName;
    }

    public int getShiftID()
    {
        return ShiftID;
    }

    public String getShiftName()
    {
        return ShiftName;
    }
}
