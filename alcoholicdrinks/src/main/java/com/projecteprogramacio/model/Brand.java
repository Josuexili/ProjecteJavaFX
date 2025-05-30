package com.projecteprogramacio.model;

/**
 * Classe que representa una marca (Brand) amb un identificador, un nom i un
 * codi de país associat.
 * 
 * Aquesta classe s'utilitza com a model de dades per representar la taula
 * `brands` de la base de dades.
 * 
 * Inclou constructors per crear instàncies noves amb o sense ID (per
 * insercions), així com mètodes getters i setters per accedir i modificar les
 * propietats.
 * 
 * També redefineix el mètode {@code toString()} per facilitar la visualització.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class Brand {

	/** Identificador únic de la marca. */
	private int brandId;

	/** Nom de la marca. */
	private String name;

	/** Codi de país associat a la marca. */
	private String countryCode;

	/**
	 * Constructor amb tots els camps, incloent l'ID. Útil per carregar dades des de
	 * la base de dades.
	 *
	 * @param brandId     Identificador de la marca.
	 * @param name        Nom de la marca.
	 * @param countryCode Codi de país de la marca.
	 */
	public Brand(int brandId, String name, String countryCode) {
		this.brandId = brandId;
		this.name = name;
		this.countryCode = countryCode;
	}

	/**
	 * Constructor sense ID, pensat per a nous registres.
	 *
	 * @param name        Nom de la marca.
	 * @param countryCode Codi de país de la marca.
	 */
	public Brand(String name, String countryCode) {
		this.name = name;
		this.countryCode = countryCode;
	}

	/** @return Identificador de la marca. */
	public int getBrandId() {
		return brandId;
	}

	/** @param brandId Nou identificador de la marca. */
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	/** @return Nom de la marca. */
	public String getName() {
		return name;
	}

	/** @param name Nou nom de la marca. */
	public void setName(String name) {
		this.name = name;
	}

	/** @return Codi de país de la marca. */
	public String getCountryCode() {
		return countryCode;
	}

	/** @param countryCode Nou codi de país de la marca. */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Retorna una representació textual de la marca.
	 *
	 * @return Nom de la marca seguit del codi de país entre parèntesis.
	 */
	@Override
	public String toString() {
		return name + " (" + countryCode + ")";
	}
}
