package com.miage.fleet.model;

public class Affectation {
    private String id;
    private int vehiculeId;
    private String vehicule;
    private String immatriculation;
    private String employe;
    private String employeMatricule;
    private String fonction;
    private String departement;
    private String dateDebut;
    private String dateFin;
    private int kmDebut;
    private int kmActuel;
    private String statut;
    private String motif;
    private String vehiculeImage;

    public Affectation(String id, int vehiculeId, String vehicule, String immatriculation,
                       String employe, String employeMatricule, String fonction,
                       String departement, String dateDebut, String dateFin,
                       int kmDebut, int kmActuel, String statut, String motif,
                       String vehiculeImage) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.vehicule = vehicule;
        this.immatriculation = immatriculation;
        this.employe = employe;
        this.employeMatricule = employeMatricule;
        this.fonction = fonction;
        this.departement = departement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.kmDebut = kmDebut;
        this.kmActuel = kmActuel;
        this.statut = statut;
        this.motif = motif;
        this.vehiculeImage = vehiculeImage;
    }

    public String getId() { return id; }
    public int getVehiculeId() { return vehiculeId; }
    public String getVehicule() { return vehicule; }
    public String getImmatriculation() { return immatriculation; }
    public String getEmploye() { return employe; }
    public String getEmployeMatricule() { return employeMatricule; }
    public String getFonction() { return fonction; }
    public String getDepartement() { return departement; }
    public String getDateDebut() { return dateDebut; }
    public String getDateFin() { return dateFin; }
    public int getKmDebut() { return kmDebut; }
    public int getKmActuel() { return kmActuel; }
    public String getStatut() { return statut; }
    public String getMotif() { return motif; }
    public String getVehiculeImage() { return vehiculeImage; }

    public int getKmParcourus() { return kmActuel - kmDebut; }

    public void setStatut(String statut) { this.statut = statut; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }
    public void setEmploye(String employe) { this.employe = employe; }
    public void setEmployeMatricule(String employeMatricule) { this.employeMatricule = employeMatricule; }
    public void setFonction(String fonction) { this.fonction = fonction; }
    public void setDepartement(String departement) { this.departement = departement; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public void setMotif(String motif) { this.motif = motif; }
    public void setKmActuel(int kmActuel) { this.kmActuel = kmActuel; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
}
