package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class ClassModel {

    private int ClassID;
    private String ClassName;

    public ClassModel(String ClassID, String ClassName)
    {
        this.ClassID = Integer.parseInt(ClassID);
        this.ClassName = ClassName;
    }

    public void setClassID(String ClassID)
    {
        this.ClassID = Integer.parseInt(ClassID);
    }

    public void setClassName(String ClassName)
    {
        this.ClassName = ClassName;
    }

    public int getClassID()
    {
        return ClassID;
    }

    public String getClassName()
    {
        return ClassName;
    }

}
