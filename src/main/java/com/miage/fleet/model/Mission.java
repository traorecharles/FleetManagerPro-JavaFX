package com.miage.fleet.model;

public class Mission {
    private String id;
    private int vehiculeId;
    private String vehicule;
    private String immatriculation;
    private String employe;
    private String employeMatricule;
    private String departement;
    private String destination;
    private String motif;
    private String dateDepart;
    private String dateRetour;
    private int kmDepart;
    private Integer kmRetour;
    private String statut;
    private String ordreMission;
    private String chauffeur;

    public Mission(String id, int vehiculeId, String vehicule, String immatriculation,
                   String employe, String employeMatricule, String departement,
                   String destination, String motif, String dateDepart, String dateRetour,
                   int kmDepart, Integer kmRetour, String statut, String ordreMission,
                   String chauffeur) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.vehicule = vehicule;
        this.immatriculation = immatriculation;
        this.employe = employe;
        this.employeMatricule = employeMatricule;
        this.departement = departement;
        this.destination = destination;
        this.motif = motif;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.kmDepart = kmDepart;
        this.kmRetour = kmRetour;
        this.statut = statut;
        this.ordreMission = ordreMission;
        this.chauffeur = chauffeur;
    }

    public String getId() { return id; }
    public int getVehiculeId() { return vehiculeId; }
    public String getVehicule() { return vehicule; }
    public String getImmatriculation() { return immatriculation; }
    public String getEmploye() { return employe; }
    public String getEmployeMatricule() { return employeMatricule; }
    public String getDepartement() { return departement; }
    public String getDestination() { return destination; }
    public String getMotif() { return motif; }
    public String getDateDepart() { return dateDepart; }
    public String getDateRetour() { return dateRetour; }
    public int getKmDepart() { return kmDepart; }
    public Integer getKmRetour() { return kmRetour; }
    public String getStatut() { return statut; }
    public String getOrdreMission() { return ordreMission; }
    public String getChauffeur() { return chauffeur; }

    public Integer getKmTotal() {
        return kmRetour != null ? kmRetour - kmDepart : null;
    }

    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public void setEmploye(String employe) { this.employe = employe; }
    public void setEmployeMatricule(String employeMatricule) { this.employeMatricule = employeMatricule; }
    public void setDepartement(String departement) { this.departement = departement; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setMotif(String motif) { this.motif = motif; }
    public void setDateDepart(String dateDepart) { this.dateDepart = dateDepart; }
    public void setDateRetour(String dateRetour) { this.dateRetour = dateRetour; }
    public void setKmDepart(int kmDepart) { this.kmDepart = kmDepart; }
    public void setKmRetour(Integer kmRetour) { this.kmRetour = kmRetour; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setChauffeur(String chauffeur) { this.chauffeur = chauffeur; }
}
