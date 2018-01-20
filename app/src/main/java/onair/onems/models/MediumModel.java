package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class MediumModel {
    private long MediumID = -2;
    private String MameName = "";
    private long InstMediumID = 0;
    private long InstituteID = 0;
    private int IsActive = 0;

    public MediumModel()
    {
        this.MediumID = -2;
        this.MameName = "";
        this.InstMediumID = 0;
        this.InstituteID = 0;
        this.IsActive = 0;
    }

    public MediumModel(String MediumID, String MameName, String InstMediumID, String InstituteID, String IsActive)
    {
        if(MediumID.equals("null"))
        {
            this.MediumID = -2;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }

        this.MameName = MameName;

        if(InstMediumID.equals("null"))
        {
            this.InstMediumID = 0;
        }
        else
        {
            this.InstMediumID = Long.parseLong(InstMediumID);
        }

        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }

        if(IsActive.equals("null"))
        {
            this.IsActive = 0;
        }
        else
        {
            this.IsActive = Integer.parseInt(IsActive);
        }
    }

    public void setMediumID(String MediumID)
    {
        if(MediumID.equals("null"))
        {
            this.MediumID = -2;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }
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
