package com.projecteprogramacio.model;

/**
 * Classe que representa un usuari del sistema.
 * Conté informació bàsica com username, contrasenya, email, dates de creació i d’últim accés,
 * i el rol dins de l’aplicació.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class User {

    /** Identificador únic de l'usuari. */
    private int userId;

    /** Nom d'usuari per a l'autenticació. */
    private String username;

    /** Contrasenya de l'usuari (hauria d'estar encriptada). */
    private String password;

    /** Correu electrònic de l'usuari. */
    private String email;

    /** Data i hora de creació de l'usuari. */
    private String createdAt;

    /** Data i hora de l'últim accés (login). */
    private String lastLogin;

    /** Data i hora de la última sortida (logout). */
    private String lastLogout;

    /** Rol de l'usuari dins de l'aplicació (ex. admin, worker, client). */
    private String role;

    /**
     * Constructor buit per a ús genèric.
     */
    public User() {}

    /**
     * Constructor complet per inicialitzar un usuari amb totes les propietats.
     * 
     * @param userId    Identificador únic de l'usuari.
     * @param username  Nom d'usuari.
     * @param password  Contrasenya.
     * @param email     Correu electrònic.
     * @param createdAt Data i hora de creació.
     * @param lastLogin Data i hora de l'últim login.
     * @param lastLogout Data i hora de l'últim logout.
     * @param role      Rol dins l'aplicació.
     */
    public User(int userId, String username, String password, String email,
                String createdAt, String lastLogin, String lastLogout, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
        this.role = role;
    }

    /** @return Identificador de l'usuari. */
    public int getUserId() {
        return userId;
    }

    /** @param userId Nou identificador de l'usuari. */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** @return Nom d'usuari. */
    public String getUsername() {
        return username;
    }

    /** @param username Nou nom d'usuari. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return Contrasenya de l'usuari. */
    public String getPassword() {
        return password;
    }

    /** @param password Nova contrasenya. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return Correu electrònic de l'usuari. */
    public String getEmail() {
        return email;
    }

    /** @param email Nou correu electrònic. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return Data i hora de creació de l'usuari. */
    public String getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt Nova data de creació. */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /** @return Data i hora de l'últim login. */
    public String getLastLogin() {
        return lastLogin;
    }

    /** @param lastLogin Nova data de l'últim login. */
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    /** @return Data i hora de l'últim logout. */
    public String getLastLogout() {
        return lastLogout;
    }

    /** @param lastLogout Nova data de l'últim logout. */
    public void setLastLogout(String lastLogout) {
        this.lastLogout = lastLogout;
    }

    /** @return Rol de l'usuari dins l'aplicació. */
    public String getRole() {
        return role;
    }

    /** @param role Nou rol de l'usuari. */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Retorna una representació textual de l'usuari, per defecte el username.
     * 
     * @return Username de l'usuari.
     */
    @Override
    public String toString() {
        return getUsername();
    }
}

