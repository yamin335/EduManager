package onair.onems.models;

public class CombinedReturnValue {
    private String vCardReturnValue;
    private String photoReturnValue;

    public CombinedReturnValue() {
        vCardReturnValue = "";
        photoReturnValue = "";
    }

    public CombinedReturnValue(String vCardReturnValue, String photoReturnValue) {
        this.vCardReturnValue = vCardReturnValue;
        this.photoReturnValue = photoReturnValue;
    }

    public void setvCardReturnValue(String vCardReturnValue) {
        this.vCardReturnValue = vCardReturnValue;
    }

    public void setPhotoReturnValue(String photoReturnValue) {
        this.photoReturnValue = photoReturnValue;
    }

    public String getvCardReturnValue() {
        return vCardReturnValue;
    }

    public String getPhotoReturnValue() {
        return photoReturnValue;
    }
}
