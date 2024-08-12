package lk.zeamac.app.garmentmachinery.entity;

public class UserEntity {

    private String id;
    private String title;
    private String name;
    private String fullName;
    private String email;
    private String type;
    private String mobile;

    public UserEntity() {
    }

    public UserEntity(String id, String title, String name, String fullName, String email, String type, String mobile) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.type = type;
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
