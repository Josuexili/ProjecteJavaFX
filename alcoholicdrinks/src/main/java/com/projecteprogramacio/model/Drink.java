package com.projecteprogramacio.model;

public class Drink {
    private int drinkId;
    private String name;
    private int typeId;
    private int brandId;
    private String countryCode;
    private double alcoholContent;
    private String description;
    private double volume;
    private double price;
    private byte[] image; // BLOB en SQLite es representa com un array de bytes

    // Camps nous per noms descriptius
    private String brandName;
    private String countryName;

    // Constructor buit
    public Drink() {}

    // Constructor complet (amb els nous camps)
    public Drink(int drinkId, String name, int typeId, int brandId, String countryCode, double alcoholContent,
                 String description, double volume, double price, byte[] image,
                 String brandName, String countryName) {
        this.drinkId = drinkId;
        this.name = name;
        this.typeId = typeId;
        this.brandId = brandId;
        this.countryCode = countryCode;
        this.alcoholContent = alcoholContent;
        this.description = description;
        this.volume = volume;
        this.price = price;
        this.image = image;
        this.brandName = brandName;
        this.countryName = countryName;
    }

    // Getters i Setters
    public int getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(int drinkId) {
        this.drinkId = drinkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    // Getters i setters nous
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}

