package lk.zeamac.app.garmentmachinery.entity;

public class MyOrderDetailsEntity {
    private String id;
    private String imgPath;
    private String title;
    private String price;
    private String status;
    private String date;

    public MyOrderDetailsEntity(String id, String imgPath, String title, String price, String status, String date) {
        this.id = id;
        this.imgPath = imgPath;
        this.title = title;
        this.price = price;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
