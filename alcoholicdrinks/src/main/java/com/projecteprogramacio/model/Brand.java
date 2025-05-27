package com.projecteprogramacio.model;



public class Brand {
    private int brandId;
    private String name;
    private String countryCode;

    // Constructor
    public Brand(int brandId, String name, String countryCode) {
        this.brandId = brandId;
        this.name = name;
        this.countryCode = countryCode;
    }

    // Constructor sense ID (per a nous registres)
    public Brand(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
    }

    // Getters i Setters
    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    // Per mostrar fàcilment
    @Override
    public String toString() {
        return name + " (" + countryCode + ")";
    }
}

