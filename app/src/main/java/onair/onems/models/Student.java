package onair.onems.models;

/**
 * Created by User on 12/26/2017.
 */

public class Student {
    private String UserFullName;
    private int RollNo;

    public Student()
    {

    }

    public Student(String UserFullName, String RollNo)
    {
        this.UserFullName = UserFullName;
        this.RollNo = Integer.parseInt(RollNo);

    }

    public String getUserFullName()
    {
        return UserFullName;
    }

    public int getRollNo()
    {
        return RollNo;
    }

}
