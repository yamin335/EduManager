package onair.onems.models;

public class SectionModel {

    private long SectionID = -2;
    private String SectionName = "";

    public SectionModel()
    {
        this.SectionID = -2;
        this.SectionName = "";
    }

    public SectionModel(String SectionID, String SectionName)
    {
        if(SectionID.equals("null"))
        {
            this.SectionID = -2;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }
        this.SectionName = SectionName;
    }

    public void setSectionID(String SectionID)
    {
        if(SectionID.equals("null"))
        {
            this.SectionID = -2;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }
    }

    public void setSectionName(String SectionName)
    {
        this.SectionName = SectionName;
    }

    public long getSectionID()
    {
        return SectionID;
    }

    public String getSectionName()
    {
        return SectionName;
    }

}
