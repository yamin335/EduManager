package onair.onems.models;

public class SubjectModel {

    private long SubjectID = 0;
    private String SubjectNo = "";
    private String SubjectName = "";
    private long InsSubjectID = 0;
    private long InstituteID = 0;
    private long DepartmentID = -2;
    private long MediumID = -2;
    private long ClassID = -2;
    private boolean IsActive = true;
    private boolean IsCombined = true;
    private long ParentID = 0;
//    private String ParentSubject = "";

    public SubjectModel()
    {

    }

    public SubjectModel(String SubjectID, String SubjectNo, String SubjectName, String InsSubjectID,
                        String InstituteID, String DepartmentID, String MediumID, String ClassID, String IsActive,
                        String IsCombined, String ParentID) {
        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }

        this.SubjectNo = SubjectNo;
        this.SubjectName = SubjectName;

        if(InsSubjectID.equals("null"))
        {
            this.InsSubjectID = 0;
        }
        else
        {
            this.InsSubjectID = Long.parseLong(InsSubjectID);
        }

        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }

        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = 0;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }

        if(MediumID.equals("null"))
        {
            this.MediumID = 0;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }


        if(ClassID.equals("null"))
        {
            this.ClassID = 0;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }

        if(IsActive.equals("null"))
        {
            this.IsActive = true;
        }
        else
        {
            this.IsActive = Boolean.parseBoolean(IsActive);
        }

        if(IsCombined.equals("null"))
        {
            this.IsCombined = true;
        }
        else
        {
            this.IsCombined = Boolean.parseBoolean(IsCombined);
        }

        if(ParentID.equals("null"))
        {
            this.ParentID = 0;
        }
        else
        {
            this.ParentID = Long.parseLong(ParentID);
        }

//        this.ParentSubject = ParentSubject;

    }

    public void setSubjectID(String SubjectID)
    {
        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }
    }

    public void setSubjectNo(String SubjectNo)
    {
        this.SubjectNo = SubjectNo;
    }

    public void setSubjectName(String SubjectName)
    {
        this.SubjectName = SubjectName;
    }

    public void setInsSubjectID(String InsSubjectID)
    {
        if(InsSubjectID.equals("null"))
        {
            this.InsSubjectID = 0;
        }
        else
        {
            this.InsSubjectID = Long.parseLong(InsSubjectID);
        }
    }

    public void setInstituteID(String InstituteID)
    {
        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }
    }

    public void setDepartmentID(String DepartmentID)
    {
        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = 0;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public void setMediumID(String MediumID)
    {
        if(MediumID.equals("null"))
        {
            this.MediumID = 0;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }
    }

    public void setClassID(String ClassID)
    {
        if(ClassID.equals("null"))
        {
            this.ClassID = 0;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }
    }

    public void setIsActive(String IsActive)
    {
        if(IsActive.equals("null"))
        {
            this.IsActive = true;
        }
        else
        {
            this.IsActive = Boolean.parseBoolean(IsActive);
        }
    }

    public void setIsCombined(String IsCombined)
    {
        if(IsCombined.equals("null"))
        {
            this.IsCombined = true;
        }
        else
        {
            this.IsCombined = Boolean.parseBoolean(IsCombined);
        }
    }

    public void setParentID(String ParentID)
    {
        if(ParentID.equals("null"))
        {
            this.ParentID = 0;
        }
        else
        {
            this.ParentID = Long.parseLong(ParentID);
        }
    }

//    public void setParentSubject(String ParentSubject)
//    {
//        this.ParentSubject = ParentSubject;
//    }

    public long getSubjectID()
    {
        return SubjectID;
    }

    public String getSubjectNo()
    {
        return SubjectNo;
    }

    public String getSubjectName()
    {
        return SubjectName;
    }

    public long getInsSubjectID()
    {
        return InsSubjectID;
    }

    public long getInstituteID()
    {
        return InstituteID;
    }

    public long getDepartmentID()
    {
        return DepartmentID;
    }

    public long getMediumID()
    {
        return MediumID;
    }

    public long getClassID()
    {
        return ClassID;
    }

    public boolean getIsActive()
    {
        return IsActive;
    }

    public boolean getIsCombined()
    {
        return IsCombined;
    }

    public long getParentID()
    {
        return ParentID;
    }

//    public String getParentSubject()
//    {
//        return ParentSubject;
//    }
}
