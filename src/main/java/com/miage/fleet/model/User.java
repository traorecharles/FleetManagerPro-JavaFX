package com.miage.fleet.model;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String departement;
    private String avatar;

    public User(int id, String nom, String prenom, String email, String password,
                String role, String departement, String avatar) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.departement = departement;
        this.avatar = avatar;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getDepartement() { return departement; }
    public String getAvatar() { return avatar; }

    public String getNomComplet() { return prenom + " " + nom; }

    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setDepartement(String departement) { this.departement = departement; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRoleLabel() {
        return switch (role) {
            case "admin" -> "Administrateur";
            case "agent_logistique" -> "Agent Logistique";
            case "consultant" -> "Consultant";
            default -> role;
        };
    }
}
