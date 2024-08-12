package lk.zeamac.app.garmentmachinery.entity;

public class userDeliveryAddressEntity {
    private String id;
    private String country;
    private String city;
    private String suburb;
    private String streetName ;
    private String houseNumber;
    private String label;
    private String deliveryInstructions;

    private String latitude;
    private String Longitude;

    public userDeliveryAddressEntity() {
    }

    public userDeliveryAddressEntity(String id, String country, String city, String suburb, String streetName, String houseNumber, String label, String deliveryInstructions, String latitude, String longitude) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.suburb = suburb;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.label = label;
        this.deliveryInstructions = deliveryInstructions;
        this.latitude = latitude;
        Longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

}
