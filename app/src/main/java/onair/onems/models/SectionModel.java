package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class SectionModel {

    private long SectionID;
    private String SectionName;

    public SectionModel(String SectionID, String SectionName)
    {
        this.SectionID = Long.parseLong(SectionID);
        this.SectionName = SectionName;
    }

    public void setSectionID(String SectionID)
    {
        this.SectionID = Long.parseLong(SectionID);
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
