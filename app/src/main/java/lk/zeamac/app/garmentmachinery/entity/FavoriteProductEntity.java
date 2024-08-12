package lk.zeamac.app.garmentmachinery.entity;

public class FavoriteProductEntity {
    private String imgPath ;
    private String title;
    private String price;

    public FavoriteProductEntity() {
    }

    public FavoriteProductEntity(String imgPath, String title, String price) {
        this.imgPath = imgPath;
        this.title = title;
        this.price = price;
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

}
