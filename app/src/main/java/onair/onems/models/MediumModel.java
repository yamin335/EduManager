package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class MediumModel {
    private long MediumID = 0;
    private String MameName = "";
    private long InstMediumID = 0;
    private long InstituteID = 0;
    private int IsActive = 0;

    public MediumModel(String MediumID, String MameName, String InstMediumID, String InstituteID, String IsActive)
    {
        this.MediumID = Long.parseLong(MediumID);
        this.MameName = MameName;
        if(InstMediumID.equals("null"))
        {
            this.InstMediumID = 0;
        }
        else
        {
            this.InstMediumID = Long.parseLong(InstMediumID);
        }
        this.InstituteID = Long.parseLong(InstituteID);
        this.IsActive = Integer.parseInt(IsActive);
    }

    public void setMediumID(String MediumID)
    {
        this.MediumID = Long.parseLong(MediumID);
    }

    public void setMameName(String MameName)
    {
        this.MameName = MameName;
    }

    public void setInstMediumID(String InstMediumID)
    {
        if(InstMediumID.equals("null"))
        {
            this.InstMediumID = 0;
        }
        else
        {
            this.InstMediumID = Long.parseLong(InstMediumID);
        }
    }

    public void setInstituteID(String InstituteID)
    {
        this.InstituteID = Long.parseLong(InstituteID);

    }

    public void setIsActive(String IsActive)
    {
        this.IsActive = Integer.parseInt(IsActive);

    }

    public long getMediumID()
    {
        return MediumID;
    }

    public String getMameName()
    {
        return MameName;
    }

    public long getInstMediumID()
    {
        return InstMediumID;
    }

    public long getInstituteID()
    {
        return InstituteID;
    }

    public int getIsActive()
    {
        return IsActive;
    }
}
