package com.borombo.demo.storelocatordemo;

import java.io.Serializable;

/**
 * Created by Erwan on 19/04/2016.
 */
public class Restaurant implements Serializable{

    private int id;
    private String nom;
    private String adresse;
    private String complementAdresse;
    private String codePostal;
    private String ville;
    private double latitude;
    private double longitude;
    private boolean handicape = false;
    private boolean parking = false;
    private boolean terrasse = false;
    private boolean espaceEnfant = false;
    private String photoUrl;
    private String infosSup;
    private String telephone;

    private int distanceToUser;

    /***********************
     *                     *
     *  Getters & Setters  *
     *                     *
     **********************/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getComplementAdresse() {
        return complementAdresse;
    }

    public void setComplementAdresse(String complementAdresse) {
        this.complementAdresse = complementAdresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isHandicape() {
        return handicape;
    }

    public void setHandicape(boolean handicape) {
        this.handicape = handicape;
    }

    public boolean isParking() { return parking; }

    public void setParking(boolean parking) { this.parking = parking; }

    public boolean isTerrasse() {
        return terrasse;
    }

    public void setTerrasse(boolean terrasse) {
        this.terrasse = terrasse;
    }

    public boolean isEspaceEnfant() {
        return espaceEnfant;
    }

    public void setEspaceEnfant(boolean espaceEnfant) {
        this.espaceEnfant = espaceEnfant;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getInfosSup() {
        return infosSup;
    }

    public void setInfosSup(String inforsSup) {
        this.infosSup = inforsSup;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
