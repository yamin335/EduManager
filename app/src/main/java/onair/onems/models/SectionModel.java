package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class SectionModel {

    private int SectionID;
    private String SectionName;

    public SectionModel(String SectionID, String SectionName)
    {
        this.SectionID = Integer.parseInt(SectionID);
        this.SectionName = SectionName;
    }

    public void setSectionID(String SectionID)
    {
        this.SectionID = Integer.parseInt(SectionID);
    }

    public void setSectionName(String SectionName)
    {
        this.SectionName = SectionName;
    }

    public int getSectionID()
    {
        return SectionID;
    }

    public String getSectionName()
    {
        return SectionName;
    }

}
