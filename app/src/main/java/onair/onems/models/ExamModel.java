package onair.onems.models;

public class ExamModel {
    private long ExamID = 0;
    private String ExamName = "";
    private long InsExamID = 0;
    private String CustomName = "";
    private int IsActive = 0;

    public ExamModel() {

    }

    public ExamModel(String ExamID, String ExamName, String InsExamID, String CustomName, String IsActive) {
        if(ExamID.equals("null")) {
            this.ExamID = 0;
        } else {
            this.ExamID = Long.parseLong(ExamID);
        }

        this.ExamName = ExamName;

        if(InsExamID.equals("null")) {
            this.InsExamID = 0;
        } else {
            this.InsExamID = Long.parseLong(InsExamID);
        }

        this.CustomName = CustomName;

        if(IsActive.equals("null")) {
            this.IsActive = 0;
        } else {
            this.IsActive = Integer.parseInt(IsActive);
        }
    }

    public void setExamID(String ExamID) {
        if(ExamID.equals("null")) {
            this.ExamID = 0;
        } else {
            this.ExamID = Long.parseLong(ExamID);
        }
    }

    public long getExamID() {
        return ExamID;
    }

    public void setExamName(String ExamName) {
        this.ExamName = ExamName;
    }

    public String getExamName() {
        return ExamName;
    }

    public void setInsExamID(String InsExamID) {
        if(InsExamID.equals("null")) {
            this.InsExamID = 0;
        } else {
            this.InsExamID = Long.parseLong(InsExamID);
        }
    }

    public long getInsExamID() {
        return InsExamID;
    }

    public void setCustomName(String CustomName) {
        this.CustomName = CustomName;
    }

    public String getCustomName() {
        return CustomName;
    }

    public void setIsActive(String IsActive) {
        if(IsActive.equals("null")) {
            this.IsActive = 0;
        } else {
            this.IsActive = Integer.parseInt(IsActive);
        }
    }

    public int getIsActive() {
        return IsActive;
    }
}
