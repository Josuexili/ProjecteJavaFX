package com.projecteprogramacio.model;

/**
 * Classe que representa una beguda alcohòlica.
 * 
 * Aquesta classe conté la informació essencial d'una beguda, com identificadors
 * de marca i tipus, contingut alcohòlic, descripció, volum, preu, imatge i noms
 * descriptius addicionals. Està pensada per mapejar la taula `drinks` de la
 * base de dades.
 * 
 * Inclou constructors amb i sense paràmetres, així com mètodes getters i
 * setters per accedir i modificar les propietats.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class Drink {

	/** Identificador únic de la beguda. */
	private int drinkId;

	/** Nom de la beguda. */
	private String name;

	/** Identificador del tipus de beguda. */
	private int typeId;

	/** Identificador de la marca. */
	private int brandId;

	/** Codi del país d'origen. */
	private String countryCode;

	/** Percentatge d'alcohol. */
	private double alcoholContent;

	/** Descripció de la beguda. */
	private String description;

	/** Volum de la beguda. */
	private double volume;

	/** Preu de la beguda. */
	private double price;

	/** Imatge de la beguda com a array de bytes (BLOB). */
	private byte[] image;

	/** Nom descriptiu de la marca (per visualització). */
	private String brandName;

	/** Nom descriptiu del país (per visualització). */
	private String countryName;

	/**
	 * Constructor buit per defecte.
	 */
	public Drink() {
	}

	/**
	 * Constructor complet que inicialitza tots els camps.
	 *
	 * @param drinkId        Identificador de la beguda.
	 * @param name           Nom de la beguda.
	 * @param typeId         ID del tipus de beguda.
	 * @param brandId        ID de la marca.
	 * @param countryCode    Codi del país d'origen.
	 * @param alcoholContent Percentatge d'alcohol.
	 * @param description    Descripció de la beguda.
	 * @param volume         Volum de la beguda.
	 * @param price          Preu de la beguda.
	 * @param image          Imatge de la beguda (array de bytes).
	 * @param brandName      Nom de la marca (descriptiu).
	 * @param countryName    Nom del país (descriptiu).
	 */
	public Drink(int drinkId, String name, int typeId, int brandId, String countryCode, double alcoholContent,
			String description, double volume, double price, byte[] image, String brandName, String countryName) {
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

	/** @return Identificador de la beguda. */
	public int getDrinkId() {
		return drinkId;
	}

	/** @param drinkId Nou identificador de la beguda. */
	public void setDrinkId(int drinkId) {
		this.drinkId = drinkId;
	}

	/** @return Nom de la beguda. */
	public String getName() {
		return name;
	}

	/** @param name Nou nom de la beguda. */
	public void setName(String name) {
		this.name = name;
	}

	/** @return Identificador del tipus de beguda. */
	public int getTypeId() {
		return typeId;
	}

	/** @param typeId Nou ID del tipus. */
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/** @return Identificador de la marca. */
	public int getBrandId() {
		return brandId;
	}

	/** @param brandId Nou ID de la marca. */
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	/** @return Codi del país d'origen. */
	public String getCountryCode() {
		return countryCode;
	}

	/** @param countryCode Nou codi del país. */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/** @return Percentatge d'alcohol. */
	public double getAlcoholContent() {
		return alcoholContent;
	}

	/** @param alcoholContent Nou percentatge d'alcohol. */
	public void setAlcoholContent(double alcoholContent) {
		this.alcoholContent = alcoholContent;
	}

	/** @return Descripció de la beguda. */
	public String getDescription() {
		return description;
	}

	/** @param description Nova descripció. */
	public void setDescription(String description) {
		this.description = description;
	}

	/** @return Volum de la beguda. */
	public double getVolume() {
		return volume;
	}

	/** @param volume Nou volum. */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	/** @return Preu de la beguda. */
	public double getPrice() {
		return price;
	}

	/** @param price Nou preu. */
	public void setPrice(double price) {
		this.price = price;
	}

	/** @return Imatge de la beguda com a array de bytes. */
	public byte[] getImage() {
		return image;
	}

	/** @param image Nova imatge (array de bytes). */
	public void setImage(byte[] image) {
		this.image = image;
	}

	/** @return Nom descriptiu de la marca. */
	public String getBrandName() {
		return brandName;
	}

	/** @param brandName Nou nom descriptiu de la marca. */
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	/** @return Nom descriptiu del país. */
	public String getCountryName() {
		return countryName;
	}

	/** @param countryName Nou nom descriptiu del país. */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
}
