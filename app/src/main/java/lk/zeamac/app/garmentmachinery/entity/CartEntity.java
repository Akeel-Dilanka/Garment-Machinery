package lk.zeamac.app.garmentmachinery.entity;

public class CartEntity {

    private String id;
    private String productId;
    private String userId;
    private Long qty;
    private Long cartProductFixedPrice;
    boolean isSelected;

    public CartEntity() {
    }

    public CartEntity(String id, String productId, String userId, Long qty, Long cartProductFixedPrice, boolean isSelected) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.qty = qty;
        this.cartProductFixedPrice = cartProductFixedPrice;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getCartProductFixedPrice() {
        return cartProductFixedPrice;
    }

    public void setCartProductFixedPrice(Long cartProductFixedPrice) {
        this.cartProductFixedPrice = cartProductFixedPrice;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
