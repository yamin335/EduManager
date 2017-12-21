package onair.onems.mainactivities;

/**
 * Created by Lincoln on 18/05/16.
 */
public class AttendanceSheet {
    private String name;
    private int numOfSongs;
    private int thumbnail;

    public AttendanceSheet() {
    }

    public AttendanceSheet(String name, int numOfSongs) {
        this.name = name;
        this.numOfSongs = numOfSongs;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}