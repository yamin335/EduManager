package onair.onems.models;


public class ClassModel {

    private long ClassID = -2;
    private String ClassName = "";

    public ClassModel() {
        this.ClassID = -2;
        this.ClassName = "";
    }

    public ClassModel(String ClassID, String ClassName) {
        if(ClassID.equals("null")) {
            this.ClassID = -2;
        } else {
            this.ClassID = Long.parseLong(ClassID);
        }
        this.ClassName = ClassName;
    }

    public void setClassID(String ClassID) {
        if(ClassID.equals("null")) {
            this.ClassID = -2;
        } else {
            this.ClassID = Long.parseLong(ClassID);
        }
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
