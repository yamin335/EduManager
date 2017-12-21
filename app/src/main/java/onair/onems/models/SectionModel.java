package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class SectionModel {

    private int SectionID;
    private String SectionName;

    public SectionModel(int SectionID, String SectionName)
    {
        this.SectionID = SectionID;
        this.SectionName = SectionName;
    }

    public void setSectionID(int SectionID)
    {
        this.SectionID = SectionID;
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
