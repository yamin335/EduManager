package onair.onems.models;

public class DocumentModel {
    private int DocumentID = 0;
    private String DocumentUrl = "";

    public DocumentModel() {

    }

    public DocumentModel(String DocumentID, String DocumentUrl) {
        if(DocumentID.equals("null")) {
            this.DocumentID = 0;
        } else {
            this.DocumentID = Integer.parseInt(DocumentID);
        }

        this.DocumentUrl = DocumentUrl;
    }

    public void setDocumentID(String DocumentID) {
        if(DocumentID.equals("null")) {
            this.DocumentID = 0;
        } else {
            this.DocumentID = Integer.parseInt(DocumentID);
        }
    }

    public void setDocumentUrl(String DocumentUrl) {
        this.DocumentUrl = DocumentUrl;
    }

    public int getDocumentID() {
        return DocumentID;
    }

    public String getDocumentUrl() {
        return DocumentUrl;
    }
}
