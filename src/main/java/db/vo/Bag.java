package db.vo;

public class Bag {
    private int bagId;
    private String bagName;
    private int userId;

    public Bag(String bagName,int userId){
        this.bagName = bagName;
        this.userId = userId;
    }
    public Bag(){
        
    }

    // get和set方法
    public int getBagId() {
        return bagId;
    }
    public void setBagId(int bagId) {
        this.bagId = bagId;
    }
    public String getBagName() {
        return bagName;
    }
    public void setBagName(String bagName) {
        this.bagName = bagName;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
}
