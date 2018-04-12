package onair.onems.models;

public class SessionModel {
    private long SessionID = 0;
    private String SessionName = "";

    public SessionModel() {

    }
    public SessionModel(String SessionID, String SessionName) {
        if(SessionID.equals("null")) {
            this.SessionID = 0;
        } else {
            this.SessionID = Long.parseLong(SessionID);
        }

        this.SessionName = SessionName;
    }

    public void setSessionID(String SessionID) {
        if(SessionID.equals("null")) {
            this.SessionID = 0;
        } else {
            this.SessionID = Long.parseLong(SessionID);
        }
    }

    public long getSessionID() {
        return SessionID;
    }

    public void setSessionName(String SessionName) {
        this.SessionName = SessionName;
    }

    public String getSessionName() {
        return SessionName;
    }
}
