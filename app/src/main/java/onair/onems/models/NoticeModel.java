package onair.onems.models;

/**
 * Created by ravi on 16/11/17.
 */

public class NoticeModel {
    String Title;
    String Subtitle;
    String Date;
    String Time;

    public NoticeModel() {
        this.Title = "Winter Vacation";
        this.Subtitle = "Seven days vacation";
        this.Date = "3 June, 2018";
        this.Time = "5:40 PM";
    }

    public NoticeModel(String Title, String Subtitle, String Date, String Time) {
        this.Title = Title;
        this.Subtitle = Subtitle;
        this.Date = Date;
        this.Time = Time;
    }

    public void setTitle(String Title)
    {
        this.Title = Title;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setSubtitle(String Subtitle)
    {
        this.Subtitle = Subtitle;
    }

    public String getSubtitle()
    {
        return this.Subtitle;
    }

    public void setDate(String Date)
    {
        this.Date = Date;
    }

    public String getDate() {
        return this.Date;
    }

    public void setTime(String Time)
    {
        this.Time = Time;
    }

    public String getTime() {
        return this.Time;
    }
}
