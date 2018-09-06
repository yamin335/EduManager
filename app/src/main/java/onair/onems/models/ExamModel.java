package onair.onems.models;

public class ExamModel {
    private long ExamID = 0;
    private String ExamName = "";
    private long InsExamID = 0;
    private int Sequence = 0;
    private int IsActive = 0;

    public ExamModel() {

    }

    public ExamModel(String ExamID, String ExamName, String InsExamID, String Sequence, String IsActive) {
        if(ExamID.equals("null")||ExamID.equals("")) {
            this.ExamID = 0;
        } else {
            this.ExamID = Long.parseLong(ExamID);
        }

        this.ExamName = ExamName;

        if(InsExamID.equals("null")||InsExamID.equals("")) {
            this.InsExamID = 0;
        } else {
            this.InsExamID = Long.parseLong(InsExamID);
        }

        if(Sequence.equals("null")||Sequence.equals("")) {
            this.Sequence = 0;
        } else {
            this.Sequence = Integer.parseInt(Sequence);
        }

        if(IsActive.equals("null")||IsActive.equals("")) {
            this.IsActive = 0;
        } else {
            this.IsActive = Integer.parseInt(IsActive);
        }
    }

    public void setExamID(String ExamID) {
        if(ExamID.equals("null")||ExamID.equals("")) {
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
        if(InsExamID.equals("null")||InsExamID.equals("")) {
            this.InsExamID = 0;
        } else {
            this.InsExamID = Long.parseLong(InsExamID);
        }
    }

    public long getInsExamID() {
        return InsExamID;
    }

    public void setSequence(String Sequence) {
        if(Sequence.equals("null")||Sequence.equals("")) {
            this.Sequence = 0;
        } else {
            this.Sequence = Integer.parseInt(Sequence);
        }
    }

    public int getSequence() {
        return Sequence;
    }

    public void setIsActive(String IsActive) {
        if(IsActive.equals("null")||IsActive.equals("")) {
            this.IsActive = 0;
        } else {
            this.IsActive = Integer.parseInt(IsActive);
        }
    }

    public int getIsActive() {
        return IsActive;
    }
}
