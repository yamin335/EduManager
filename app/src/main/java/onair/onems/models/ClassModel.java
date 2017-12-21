package onair.onems.models;

/**
 * Created by User on 12/21/2017.
 */

public class ClassModel {

    private int ClassID;
    private String ClassName;

    ClassModel(int ClassID, String ClassName)
    {
        this.ClassID = ClassID;
        this.ClassName = ClassName;
    }

    public void setClassID(int ClassID)
    {
        this.ClassID = ClassID;
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
