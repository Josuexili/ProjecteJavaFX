package com.projecteprogramacio.model;


public class DrinkType {
    private int typeId;
    private String name;
    private byte[] image; // per guardar la imatge com a BLOB

    // Constructor complet
    public DrinkType(int typeId, String name, byte[] image) {
        this.typeId = typeId;
        this.name = name;
        this.image = image;
    }

    // Constructor sense ID (per inserir nous registres)
    public DrinkType(String name, byte[] image) {
        this.name = name;
        this.image = image;
    }

    // Getters i Setters
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }
}

