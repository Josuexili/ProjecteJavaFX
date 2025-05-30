package com.projecteprogramacio.model;

/**
 * Classe que representa un tipus de beguda.
 * 
 * Aquesta classe s'utilitza com a model de dades per representar la taula
 * `types` de la base de dades, que emmagatzema els diferents tipus de begudes
 * (per exemple, cervesa, vi, licor, etc.).
 * 
 * Inclou constructors per a nous registres i per carregar dades existents, així
 * com mètodes getters i setters per accedir i modificar les propietats.
 * 
 * També redefineix el mètode {@code toString()} per retornar el nom del tipus.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class DrinkType {

	/** Identificador únic del tipus de beguda. */
	private int typeId;

	/** Nom del tipus de beguda (ex. "Cervesa", "Vi"). */
	private String name;

	/** Imatge associada al tipus, emmagatzemada com a BLOB (array de bytes). */
	private byte[] image;

	/**
	 * Constructor complet amb tots els camps, incloent l'ID. Útil per carregar
	 * dades existents de la base de dades.
	 *
	 * @param typeId Identificador del tipus.
	 * @param name   Nom del tipus de beguda.
	 * @param image  Imatge associada (com a array de bytes).
	 */
	public DrinkType(int typeId, String name, byte[] image) {
		this.typeId = typeId;
		this.name = name;
		this.image = image;
	}

	/**
	 * Constructor sense ID, pensat per a nous registres.
	 *
	 * @param name  Nom del tipus de beguda.
	 * @param image Imatge associada (com a array de bytes).
	 */
	public DrinkType(String name, byte[] image) {
		this.name = name;
		this.image = image;
	}

	/** @return Identificador del tipus de beguda. */
	public int getTypeId() {
		return typeId;
	}

	/** @param typeId Nou identificador del tipus. */
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/** @return Nom del tipus de beguda. */
	public String getName() {
		return name;
	}

	/** @param name Nou nom del tipus. */
	public void setName(String name) {
		this.name = name;
	}

	/** @return Imatge associada al tipus (com a array de bytes). */
	public byte[] getImage() {
		return image;
	}

	/** @param image Nova imatge associada (array de bytes). */
	public void setImage(byte[] image) {
		this.image = image;
	}

	/**
	 * Retorna una representació textual del tipus de beguda.
	 *
	 * @return Nom del tipus de beguda.
	 */
	@Override
	public String toString() {
		return name;
	}
}
