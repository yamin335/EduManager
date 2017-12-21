package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class MediumModel {
    private int MediumID;
    private String MameName;
    private int InstMediumID;
    private int InstituteID;
    private boolean IsActive;

    MediumModel(int MediumID, String MameName, int InstMediumID, int InstituteID, boolean IsActive)
    {
        this.MediumID = MediumID;
        this.MameName = MameName;
        this.InstMediumID =InstMediumID;
        this.InstituteID = InstituteID;
        this.IsActive = IsActive;
    }

    public void setMediumID(int MediumID)
    {
        this.MediumID = MediumID;
    }

    public void setMameName(String MameName)
    {
        this.MameName = MameName;
    }

    public void setInstMediumID(int InstMediumID)
    {
        this.InstMediumID = InstMediumID;
    }

    public void setInstituteID(int InstituteID)
    {
        this.InstituteID = InstituteID;

    }

    public void setIsActive(boolean IsActive)
    {
        this.IsActive = IsActive;

    }

    public int getMediumID()
    {
        return MediumID;
    }

    public String getMameName()
    {
        return MameName;
    }

    public int getInstMediumID()
    {
        return InstMediumID;
    }

    public int getInstituteID()
    {
        return InstituteID;
    }

    public boolean getIsActive()
    {
        return IsActive;
    }
}
