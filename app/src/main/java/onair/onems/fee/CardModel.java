package onair.onems.fee;

public class CardModel {
    // Name of the Android version (e.g. Gingerbread, Honeycomb, Ice Cream Sandwich)
    private String title;

    // Android version number (e.g. 2.3-2.7, 3.0-3.2.6, 4.0-4.0.4)
    private String amount;


    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public CardModel(String vName, String vNumber) {
        title = vName;
        amount = vNumber;

    }






}
