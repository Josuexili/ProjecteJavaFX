package com.projecteprogramacio.model;

/**
 * Classe que representa un país amb un codi de país i un nom.
 * 
 * Aquesta classe s'utilitza com a model de dades per representar la taula
 * `countries` de la base de dades.
 * 
 * Inclou un constructor per defecte i un constructor amb paràmetres, així com
 * mètodes getters i setters per accedir i modificar les propietats.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class Country {

	/** Codi del país (clau primària). */
	private String countryCode;

	/** Nom complet del país. */
	private String name;

	/**
	 * Constructor per defecte. Necessari per a algunes biblioteques o frameworks.
	 */
	public Country() {
	}

	/**
	 * Constructor amb tots els camps.
	 *
	 * @param countryCode Codi del país.
	 * @param name        Nom del país.
	 */
	public Country(String countryCode, String name) {
		this.countryCode = countryCode;
		this.name = name;
	}

	/** @return Codi del país. */
	public String getCountryCode() {
		return countryCode;
	}

	/** @param countryCode Nou codi del país. */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/** @return Nom del país. */
	public String getName() {
		return name;
	}

	/** @param name Nou nom del país. */
	public void setName(String name) {
		this.name = name;
	}
}
