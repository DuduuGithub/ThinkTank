package db.vo;

public class Bag2document {
    int bagId;
    int documentId;
    public Bag2document(int bagId, int documentId) {
        this.bagId = bagId;
        this.documentId = documentId;
    }
    public int getBagId() {
        return bagId;
    }
    public void setBagId(int bagId) {
        this.bagId = bagId;
    }
    public int getDocumentId() {
        return documentId;
    }
    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    
}
