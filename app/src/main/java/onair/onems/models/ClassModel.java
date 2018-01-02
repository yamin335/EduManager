package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class ClassModel {

    private long ClassID;
    private String ClassName;

    public ClassModel(String ClassID, String ClassName)
    {
        this.ClassID = Integer.parseInt(ClassID);
        this.ClassName = ClassName;
    }

    public void setClassID(String ClassID)
    {
        this.ClassID = Long.parseLong(ClassID);
    }

    public void setClassName(String ClassName)
    {
        this.ClassName = ClassName;
    }

    public long getClassID()
    {
        return ClassID;
    }

    public String getClassName()
    {
        return ClassName;
    }

}
