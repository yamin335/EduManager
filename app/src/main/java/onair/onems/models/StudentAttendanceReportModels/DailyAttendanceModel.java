package onair.onems.models.StudentAttendanceReportModels;

/**
 * Created by onAir on 15-Mar-18.
 */

public class DailyAttendanceModel {
//    private String UserID = "";
//    private String UserName = "";
//    private String RFID = "";
//    private String RollNo = "";
//    private String Guardian = "";
//    private String GuradianEmail = "";
    private String Date = "";
    private int Present = 0;
    private int Late = 0;
    private int TotalClassDay = 0;
    private int TotalPresent = 0;

    public DailyAttendanceModel(){
        super();
    }

//    public void setUserID(String UserID)
//    {
//        this.UserID = UserID;
//    }
//
//    public String getUserID()
//    {
//        return UserID;
//    }

//    public void setUserName(String UserName)
//    {
//        this.UserName = UserName;
//    }
//
//    public String getUserName()
//    {
//        return UserName;
//    }
//    public void setRFID(String RFID)
//    {
//        this.RFID = RFID;
//    }
//
//    public String getRFID()
//    {
//        return RFID;
//    }
//
//    public void setRollNo(String RollNo)
//    {
//        this.RollNo = RollNo;
//    }
//
//    public String getRollNo()
//    {
//        return RollNo;
//    }
//
//    public void setGuardian(String Guardian)
//    {
//        this.Guardian = Guardian;
//    }
//
//    public String getGuardian()
//    {
//        return Guardian;
//    }
//
//    public void setGuradianEmail(String GuradianEmail)
//    {
//        this.GuradianEmail = GuradianEmail;
//    }
//
//    public String getGuradianEmail()
//    {
//        return GuradianEmail;
//    }

    public void setDate(String Date)
    {
        String[] formattedDate = Date.split("T");
        this.Date = formattedDate[0];
    }

    public String getDate()
    {
        return Date;
    }

    public void setPresent(String Present)
    {
        if(!Present.equalsIgnoreCase("null")){
            this.Present = Integer.parseInt(Present);
        }else {
            this.Present = 0;
        }
    }

    public int getPresent()
    {
        return Present;
    }

    public void setLate(String Late)
    {
        if(!Late.equalsIgnoreCase("null")){
            this.Late = Integer.parseInt(Late);
        }else {
            this.Late = 0;
        }
    }

    public int getLate()
    {
        return Late;
    }

    public void setTotalClassDay(String TotalClassDay)
    {
        if(!TotalClassDay.equalsIgnoreCase("null")){
            this.TotalClassDay = Integer.parseInt(TotalClassDay);
        }else {
            this.TotalClassDay = 0;
        }
    }

    public int getTotalClassDay()
    {
        return TotalClassDay;
    }

    public void setTotalPresent(String TotalPresent)
    {
        if(!TotalPresent.equalsIgnoreCase("null")){
            this.TotalPresent = Integer.parseInt(TotalPresent);
        }else {
            this.TotalPresent = 0;
        }
    }

    public int getTotalPresent()
    {
        return TotalPresent;
    }
}
