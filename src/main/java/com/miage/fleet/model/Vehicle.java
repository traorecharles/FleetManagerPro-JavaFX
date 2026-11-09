package com.miage.fleet.model;

public class Vehicle {
    private int id;
    private String numeroInventaire;
    private String marque;
    private String modele;
    private int annee;
    private String immatriculation;
    private String vin;
    private String carburant;
    private String transmission;
    private String couleur;
    private int kilometrage;
    private String statut;
    private String categorie;
    private String departement;
    private String dateAchat;
    private double prixAchat;
    private int kmAchat;
    private String dateMiseService;
    private String dateHorsService;
    private String assuranceNum;
    private String assuranceDebut;
    private String assuranceFin;
    private String assuranceCompagnie;
    private String visiteTechniqueDate;
    private String visiteTechniqueEcheance;
    private String carteGriseNum;
    private int prochainEntretienKm;
    private String image;

    public Vehicle(int id, String numeroInventaire, String marque, String modele, int annee,
                   String immatriculation, String vin, String carburant, String transmission,
                   String couleur, int kilometrage, String statut, String categorie,
                   String departement, String dateAchat, double prixAchat, int kmAchat,
                   String dateMiseService, String dateHorsService, String assuranceNum,
                   String assuranceDebut, String assuranceFin, String assuranceCompagnie,
                   String visiteTechniqueDate, String visiteTechniqueEcheance,
                   String carteGriseNum, int prochainEntretienKm, String image) {
        this.id = id;
        this.numeroInventaire = numeroInventaire;
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.immatriculation = immatriculation;
        this.vin = vin;
        this.carburant = carburant;
        this.transmission = transmission;
        this.couleur = couleur;
        this.kilometrage = kilometrage;
        this.statut = statut;
        this.categorie = categorie;
        this.departement = departement;
        this.dateAchat = dateAchat;
        this.prixAchat = prixAchat;
        this.kmAchat = kmAchat;
        this.dateMiseService = dateMiseService;
        this.dateHorsService = dateHorsService;
        this.assuranceNum = assuranceNum;
        this.assuranceDebut = assuranceDebut;
        this.assuranceFin = assuranceFin;
        this.assuranceCompagnie = assuranceCompagnie;
        this.visiteTechniqueDate = visiteTechniqueDate;
        this.visiteTechniqueEcheance = visiteTechniqueEcheance;
        this.carteGriseNum = carteGriseNum;
        this.prochainEntretienKm = prochainEntretienKm;
        this.image = image;
    }

    public int getId() { return id; }
    public String getNumeroInventaire() { return numeroInventaire; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public int getAnnee() { return annee; }
    public String getImmatriculation() { return immatriculation; }
    public String getVin() { return vin; }
    public String getCarburant() { return carburant; }
    public String getTransmission() { return transmission; }
    public String getCouleur() { return couleur; }
    public int getKilometrage() { return kilometrage; }
    public String getStatut() { return statut; }
    public String getCategorie() { return categorie; }
    public String getDepartement() { return departement; }
    public String getDateAchat() { return dateAchat; }
    public double getPrixAchat() { return prixAchat; }
    public int getKmAchat() { return kmAchat; }
    public String getDateMiseService() { return dateMiseService; }
    public String getDateHorsService() { return dateHorsService; }
    public String getAssuranceNum() { return assuranceNum; }
    public String getAssuranceDebut() { return assuranceDebut; }
    public String getAssuranceFin() { return assuranceFin; }
    public String getAssuranceCompagnie() { return assuranceCompagnie; }
    public String getVisiteTechniqueDate() { return visiteTechniqueDate; }
    public String getVisiteTechniqueEcheance() { return visiteTechniqueEcheance; }
    public String getCarteGriseNum() { return carteGriseNum; }
    public int getProchainEntretienKm() { return prochainEntretienKm; }
    public String getImage() { return image; }

    public String getNomComplet() { return marque + " " + modele; }

    public void setMarque(String marque) { this.marque = marque; }
    public void setModele(String modele) { this.modele = modele; }
    public void setAnnee(int annee) { this.annee = annee; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public void setVin(String vin) { this.vin = vin; }
    public void setCarburant(String carburant) { this.carburant = carburant; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setDepartement(String departement) { this.departement = departement; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }
    public void setPrixAchat(double prixAchat) { this.prixAchat = prixAchat; }
    public void setKmAchat(int kmAchat) { this.kmAchat = kmAchat; }
    public void setDateMiseService(String dateMiseService) { this.dateMiseService = dateMiseService; }
    public void setDateHorsService(String dateHorsService) { this.dateHorsService = dateHorsService; }
    public void setAssuranceNum(String assuranceNum) { this.assuranceNum = assuranceNum; }
    public void setAssuranceDebut(String assuranceDebut) { this.assuranceDebut = assuranceDebut; }
    public void setAssuranceFin(String assuranceFin) { this.assuranceFin = assuranceFin; }
    public void setAssuranceCompagnie(String assuranceCompagnie) { this.assuranceCompagnie = assuranceCompagnie; }
    public void setVisiteTechniqueDate(String visiteTechniqueDate) { this.visiteTechniqueDate = visiteTechniqueDate; }
    public void setVisiteTechniqueEcheance(String visiteTechniqueEcheance) { this.visiteTechniqueEcheance = visiteTechniqueEcheance; }
    public void setCarteGriseNum(String carteGriseNum) { this.carteGriseNum = carteGriseNum; }
    public void setNumeroInventaire(String numeroInventaire) { this.numeroInventaire = numeroInventaire; }
    public void setProchainEntretienKm(int prochainEntretienKm) { this.prochainEntretienKm = prochainEntretienKm; }
    public void setImage(String image) { this.image = image; }
}
