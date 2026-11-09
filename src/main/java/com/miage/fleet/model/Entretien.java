package com.miage.fleet.model;

import java.util.List;

public class Entretien {
    private String id;
    private String vehicule;
    private String immatriculation;
    private String numeroInv;
    private String type;
    private String nature;
    private String description;
    private String garage;
    private String adresseGarage;
    private String technicien;
    private String dateEntree;
    private String dateSortiePrevue;
    private String dateSortieReelle;
    private int coutEstime;
    private int coutReel;
    private String statut;
    private String priorite;
    private int kmEntree;
    private List<String> pieces;
    private String bonCommande;

    public Entretien(String id, String vehicule, String immatriculation, String numeroInv,
                     String type, String nature, String description, String garage,
                     String adresseGarage, String technicien, String dateEntree,
                     String dateSortiePrevue, String dateSortieReelle,
                     int coutEstime, int coutReel, String statut, String priorite,
                     int kmEntree, List<String> pieces, String bonCommande) {
        this.id = id;
        this.vehicule = vehicule;
        this.immatriculation = immatriculation;
        this.numeroInv = numeroInv;
        this.type = type;
        this.nature = nature;
        this.description = description;
        this.garage = garage;
        this.adresseGarage = adresseGarage;
        this.technicien = technicien;
        this.dateEntree = dateEntree;
        this.dateSortiePrevue = dateSortiePrevue;
        this.dateSortieReelle = dateSortieReelle;
        this.coutEstime = coutEstime;
        this.coutReel = coutReel;
        this.statut = statut;
        this.priorite = priorite;
        this.kmEntree = kmEntree;
        this.pieces = pieces;
        this.bonCommande = bonCommande;
    }

    public String getId() { return id; }
    public String getVehicule() { return vehicule; }
    public String getImmatriculation() { return immatriculation; }
    public String getNumeroInv() { return numeroInv; }
    public String getType() { return type; }
    public String getNature() { return nature; }
    public String getDescription() { return description; }
    public String getGarage() { return garage; }
    public String getAdresseGarage() { return adresseGarage; }
    public String getTechnicien() { return technicien; }
    public String getDateEntree() { return dateEntree; }
    public String getDateSortiePrevue() { return dateSortiePrevue; }
    public String getDateSortieReelle() { return dateSortieReelle; }
    public int getCoutEstime() { return coutEstime; }
    public int getCoutReel() { return coutReel; }
    public String getStatut() { return statut; }
    public String getPriorite() { return priorite; }
    public int getKmEntree() { return kmEntree; }
    public List<String> getPieces() { return pieces; }
    public String getBonCommande() { return bonCommande; }

    public void setType(String type) { this.type = type; }
    public void setNature(String nature) { this.nature = nature; }
    public void setDescription(String description) { this.description = description; }
    public void setGarage(String garage) { this.garage = garage; }
    public void setAdresseGarage(String adresseGarage) { this.adresseGarage = adresseGarage; }
    public void setTechnicien(String technicien) { this.technicien = technicien; }
    public void setDateEntree(String dateEntree) { this.dateEntree = dateEntree; }
    public void setDateSortiePrevue(String dateSortiePrevue) { this.dateSortiePrevue = dateSortiePrevue; }
    public void setDateSortieReelle(String dateSortieReelle) { this.dateSortieReelle = dateSortieReelle; }
    public void setCoutEstime(int coutEstime) { this.coutEstime = coutEstime; }
    public void setCoutReel(int coutReel) { this.coutReel = coutReel; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public void setKmEntree(int kmEntree) { this.kmEntree = kmEntree; }
    public void setPieces(List<String> pieces) { this.pieces = pieces; }
    public void setBonCommande(String bonCommande) { this.bonCommande = bonCommande; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public void setNumeroInv(String numeroInv) { this.numeroInv = numeroInv; }
}
