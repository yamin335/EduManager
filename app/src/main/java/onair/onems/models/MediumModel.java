package onair.onems.models;

public class MediumModel {
    private long MediumID = -2;
    private String MameName = "";
    private boolean IsDefault = false;

    public MediumModel()
    {
        this.MediumID = -2;
        this.MameName = "";
        this.IsDefault = false;
    }

    public MediumModel(String MediumID, String MameName, String IsDefault)
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

        if(IsDefault.equals("null"))
        {
            this.IsDefault = false;
        }
        else
        {
            this.IsDefault = Boolean.parseBoolean(IsDefault);
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

    public void setIsDefault(String IsDefault)
    {
        if(IsDefault.equals("null"))
        {
            this.IsDefault = false;
        }
        else
        {
            this.IsDefault = Boolean.parseBoolean(IsDefault);
        }
    }
    public long getMediumID()
    {
        return MediumID;
    }

    public String getMameName()
    {
        return MameName;
    }

    public boolean getIsDefault()
    {
        return IsDefault;
    }
}
