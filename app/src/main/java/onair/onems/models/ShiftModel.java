package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class ShiftModel {

    private int ShiftID;
    private String ShiftName;

    ShiftModel(int ShiftID, String ShiftName)
    {
        this.ShiftID = ShiftID;
        this.ShiftName = ShiftName;
    }

    public void setShiftID(int shiftID)
    {
        this.ShiftID = ShiftID;
    }

    public void setShiftName(String shiftName)
    {
        this.ShiftName = shiftName;
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
