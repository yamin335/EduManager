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

    public MediumModel(String MediumID, String MameName, String InstMediumID, String InstituteID, String IsActive)
    {
        this.MediumID = Integer.parseInt(MediumID);
        this.MameName = MameName;
        this.InstMediumID = Integer.parseInt(InstMediumID);
        this.InstituteID = Integer.parseInt(InstituteID);
        this.IsActive = Boolean.parseBoolean(IsActive);
    }

    public void setMediumID(String MediumID)
    {
        this.MediumID = Integer.parseInt(MediumID);
    }

    public void setMameName(String MameName)
    {
        this.MameName = MameName;
    }

    public void setInstMediumID(String InstMediumID)
    {
        this.InstMediumID = Integer.parseInt(InstMediumID);
    }

    public void setInstituteID(String InstituteID)
    {
        this.InstituteID = Integer.parseInt(InstituteID);

    }

    public void setIsActive(String IsActive)
    {
        this.IsActive = Boolean.parseBoolean(IsActive);

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
