package com.miage.fleet.service;

import com.miage.fleet.model.Affectation;
import com.miage.fleet.model.Entretien;
import com.miage.fleet.model.Mission;
import com.miage.fleet.model.User;
import com.miage.fleet.model.Vehicle;
import com.miage.fleet.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DatabaseService {

    private static DatabaseService instance;
    private User currentUser;

    private DatabaseService() {}

    public static DatabaseService getInstance() {
        if (instance == null) instance = new DatabaseService();
        return instance;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }

    // ── Auth ─────────────────────────────────────────────────────────────────

    public boolean login(String email, String password) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND password = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentUser = mapUser(rs);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() { currentUser = null; }

    // ── Véhicules ────────────────────────────────────────────────────────────

    public List<Vehicle> getVehicules() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicule ORDER BY id";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapVehicle(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addVehicle(Vehicle v) {
        String sql = """
            INSERT INTO vehicule (numero_inventaire, marque, modele, annee, immatriculation, vin,
              carburant, transmission, couleur, kilometrage, statut, categorie, departement,
              date_achat, prix_achat, km_achat, date_mise_service, date_hors_service,
              assurance_num, assurance_debut, assurance_fin, assurance_compagnie,
              visite_technique_date, visite_technique_echeance, carte_grise_num,
              prochain_entretien_km, image)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setVehicleParams(ps, v);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVehicle(Vehicle v) {
        String sql = """
            UPDATE vehicule SET numero_inventaire=?, marque=?, modele=?, annee=?, immatriculation=?,
              vin=?, carburant=?, transmission=?, couleur=?, kilometrage=?, statut=?, categorie=?,
              departement=?, date_achat=?, prix_achat=?, km_achat=?, date_mise_service=?,
              date_hors_service=?, assurance_num=?, assurance_debut=?, assurance_fin=?,
              assurance_compagnie=?, visite_technique_date=?, visite_technique_echeance=?,
              carte_grise_num=?, prochain_entretien_km=?, image=?
            WHERE id=?
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setVehicleParams(ps, v);
            ps.setInt(28, v.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVehicle(int id) {
        exec("DELETE FROM vehicule WHERE id = ?", id);
    }

    // ── Missions ─────────────────────────────────────────────────────────────

    public List<Mission> getMissions() {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM mission ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapMission(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addMission(Mission m) {
        String sql = """
            INSERT INTO mission (id, vehicule_id, vehicule, immatriculation, employe,
              employe_matricule, departement, destination, motif, date_depart, date_retour,
              km_depart, km_retour, statut, ordre_mission, chauffeur)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getId());
            ps.setInt(2, m.getVehiculeId());
            ps.setString(3, m.getVehicule());
            ps.setString(4, m.getImmatriculation());
            ps.setString(5, m.getEmploye());
            ps.setString(6, m.getEmployeMatricule());
            ps.setString(7, m.getDepartement());
            ps.setString(8, m.getDestination());
            ps.setString(9, m.getMotif());
            ps.setString(10, m.getDateDepart());
            ps.setString(11, m.getDateRetour());
            ps.setInt(12, m.getKmDepart());
            if (m.getKmRetour() != null) ps.setInt(13, m.getKmRetour()); else ps.setNull(13, Types.INTEGER);
            ps.setString(14, m.getStatut());
            ps.setString(15, m.getOrdreMission());
            ps.setString(16, m.getChauffeur());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMission(Mission m) {
        String sql = """
            UPDATE mission SET vehicule_id=?, vehicule=?, immatriculation=?, employe=?,
              employe_matricule=?, departement=?, destination=?, motif=?, date_depart=?,
              date_retour=?, km_depart=?, km_retour=?, statut=?, chauffeur=?
            WHERE id=?
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getVehiculeId());
            ps.setString(2, m.getVehicule());
            ps.setString(3, m.getImmatriculation());
            ps.setString(4, m.getEmploye());
            ps.setString(5, m.getEmployeMatricule());
            ps.setString(6, m.getDepartement());
            ps.setString(7, m.getDestination());
            ps.setString(8, m.getMotif());
            ps.setString(9, m.getDateDepart());
            ps.setString(10, m.getDateRetour());
            ps.setInt(11, m.getKmDepart());
            if (m.getKmRetour() != null) ps.setInt(12, m.getKmRetour()); else ps.setNull(12, Types.INTEGER);
            ps.setString(13, m.getStatut());
            ps.setString(14, m.getChauffeur());
            ps.setString(15, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMission(String id) {
        exec("DELETE FROM mission WHERE id = ?", id);
    }

    // ── Affectations ─────────────────────────────────────────────────────────

    public List<Affectation> getAffectations() {
        List<Affectation> list = new ArrayList<>();
        String sql = "SELECT * FROM affectation ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapAffectation(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addAffectation(Affectation a) {
        String sql = """
            INSERT INTO affectation (id, vehicule_id, vehicule, immatriculation, employe,
              employe_matricule, fonction, departement, date_debut, date_fin, km_debut,
              km_actuel, statut, motif, vehicule_image)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getId());
            ps.setInt(2, a.getVehiculeId());
            ps.setString(3, a.getVehicule());
            ps.setString(4, a.getImmatriculation());
            ps.setString(5, a.getEmploye());
            ps.setString(6, a.getEmployeMatricule());
            ps.setString(7, a.getFonction());
            ps.setString(8, a.getDepartement());
            ps.setString(9, a.getDateDebut());
            ps.setString(10, a.getDateFin());
            ps.setInt(11, a.getKmDebut());
            ps.setInt(12, a.getKmActuel());
            ps.setString(13, a.getStatut());
            ps.setString(14, a.getMotif());
            ps.setString(15, a.getVehiculeImage());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAffectation(Affectation a) {
        String sql = """
            UPDATE affectation SET vehicule_id=?, vehicule=?, immatriculation=?, employe=?,
              employe_matricule=?, fonction=?, departement=?, date_debut=?, date_fin=?,
              km_debut=?, km_actuel=?, statut=?, motif=?
            WHERE id=?
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getVehiculeId());
            ps.setString(2, a.getVehicule());
            ps.setString(3, a.getImmatriculation());
            ps.setString(4, a.getEmploye());
            ps.setString(5, a.getEmployeMatricule());
            ps.setString(6, a.getFonction());
            ps.setString(7, a.getDepartement());
            ps.setString(8, a.getDateDebut());
            ps.setString(9, a.getDateFin());
            ps.setInt(10, a.getKmDebut());
            ps.setInt(11, a.getKmActuel());
            ps.setString(12, a.getStatut());
            ps.setString(13, a.getMotif());
            ps.setString(14, a.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAffectation(String id) {
        exec("DELETE FROM affectation WHERE id = ?", id);
    }

    // ── Entretiens ───────────────────────────────────────────────────────────

    public List<Entretien> getEntretiens() {
        List<Entretien> list = new ArrayList<>();
        String sql = "SELECT * FROM entretien ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapEntretien(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addEntretien(Entretien e) {
        String sql = """
            INSERT INTO entretien (id, vehicule, immatriculation, numero_inv, type, nature,
              description, garage, adresse_garage, technicien, date_entree, date_sortie_prevue,
              date_sortie_reelle, cout_estime, cout_reel, statut, priorite, km_entree,
              pieces, bon_commande)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setEntretienParams(ps, e);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateEntretien(Entretien e) {
        String sql = """
            UPDATE entretien SET vehicule=?, immatriculation=?, numero_inv=?, type=?, nature=?,
              description=?, garage=?, adresse_garage=?, technicien=?, date_entree=?,
              date_sortie_prevue=?, date_sortie_reelle=?, cout_estime=?, cout_reel=?, statut=?,
              priorite=?, km_entree=?, pieces=?, bon_commande=?
            WHERE id=?
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getVehicule());
            ps.setString(2, e.getImmatriculation());
            ps.setString(3, e.getNumeroInv());
            ps.setString(4, e.getType());
            ps.setString(5, e.getNature());
            ps.setString(6, e.getDescription());
            ps.setString(7, e.getGarage());
            ps.setString(8, e.getAdresseGarage());
            ps.setString(9, e.getTechnicien());
            ps.setString(10, e.getDateEntree());
            ps.setString(11, e.getDateSortiePrevue());
            ps.setString(12, e.getDateSortieReelle());
            ps.setInt(13, e.getCoutEstime());
            ps.setInt(14, e.getCoutReel());
            ps.setString(15, e.getStatut());
            ps.setString(16, e.getPriorite());
            ps.setInt(17, e.getKmEntree());
            ps.setString(18, piecesToString(e.getPieces()));
            ps.setString(19, e.getBonCommande());
            ps.setString(20, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteEntretien(String id) {
        exec("DELETE FROM entretien WHERE id = ?", id);
    }

    // ── Utilisateurs ─────────────────────────────────────────────────────────

    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY id";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addUser(User u) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, password, role, departement, avatar) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getDepartement());
            ps.setString(7, u.getAvatar());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User u) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, role=?, departement=?, avatar=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getDepartement());
            ps.setString(6, u.getAvatar());
            ps.setInt(7, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Générateurs d'ID ─────────────────────────────────────────────────────

    public int generateVehicleId() {
        return queryMaxInt("SELECT COALESCE(MAX(id), 0) + 1 FROM vehicule");
    }

    public String generateVehicleNumInv() {
        return String.format("VH-%d-%03d", LocalDate.now().getYear(), generateVehicleId());
    }

    public String generateMissionId() {
        int n = queryMaxInt("SELECT COUNT(*) + 1 FROM mission");
        return String.format("OM-%d-%04d", LocalDate.now().getYear(), n);
    }

    public String generateAffectationId() {
        int n = queryMaxInt("SELECT COUNT(*) + 1 FROM affectation");
        return String.format("AFF-%d-%03d", LocalDate.now().getYear(), n);
    }

    public String generateEntretienId() {
        int n = queryMaxInt("SELECT COUNT(*) + 1 FROM entretien");
        return String.format("ENT-%d-%03d", LocalDate.now().getYear(), n);
    }

    public int generateUserId() {
        return queryMaxInt("SELECT COALESCE(MAX(id), 0) + 1 FROM utilisateur");
    }

    // ── Statistiques (données agrégées pour les graphiques) ──────────────────

    public int[][] getMissionsMensuellesData() {
        return new int[][]{{1,18},{2,14},{3,22},{4,19},{5,27},{6,23},{7,31},{8,28},{9,24},{10,29},{11,21},{12,33}};
    }

    public int[][] getKmMensuelData() {
        return new int[][]{{7,22100},{8,20400},{9,17300},{10,19800},{11,14900},{12,24600}};
    }

    public String[] getMoisLabels() {
        return new String[]{"Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(
            rs.getInt("id"),
            rs.getString("numero_inventaire"),
            rs.getString("marque"),
            rs.getString("modele"),
            rs.getInt("annee"),
            rs.getString("immatriculation"),
            rs.getString("vin"),
            rs.getString("carburant"),
            rs.getString("transmission"),
            rs.getString("couleur"),
            rs.getInt("kilometrage"),
            rs.getString("statut"),
            rs.getString("categorie"),
            rs.getString("departement"),
            rs.getString("date_achat"),
            rs.getDouble("prix_achat"),
            rs.getInt("km_achat"),
            rs.getString("date_mise_service"),
            rs.getString("date_hors_service"),
            rs.getString("assurance_num"),
            rs.getString("assurance_debut"),
            rs.getString("assurance_fin"),
            rs.getString("assurance_compagnie"),
            rs.getString("visite_technique_date"),
            rs.getString("visite_technique_echeance"),
            rs.getString("carte_grise_num"),
            rs.getInt("prochain_entretien_km"),
            rs.getString("image")
        );
    }

    private Mission mapMission(ResultSet rs) throws SQLException {
        int kmRetourRaw = rs.getInt("km_retour");
        Integer kmRetour = rs.wasNull() ? null : kmRetourRaw;
        return new Mission(
            rs.getString("id"),
            rs.getInt("vehicule_id"),
            rs.getString("vehicule"),
            rs.getString("immatriculation"),
            rs.getString("employe"),
            rs.getString("employe_matricule"),
            rs.getString("departement"),
            rs.getString("destination"),
            rs.getString("motif"),
            rs.getString("date_depart"),
            rs.getString("date_retour"),
            rs.getInt("km_depart"),
            kmRetour,
            rs.getString("statut"),
            rs.getString("ordre_mission"),
            rs.getString("chauffeur")
        );
    }

    private Affectation mapAffectation(ResultSet rs) throws SQLException {
        return new Affectation(
            rs.getString("id"),
            rs.getInt("vehicule_id"),
            rs.getString("vehicule"),
            rs.getString("immatriculation"),
            rs.getString("employe"),
            rs.getString("employe_matricule"),
            rs.getString("fonction"),
            rs.getString("departement"),
            rs.getString("date_debut"),
            rs.getString("date_fin"),
            rs.getInt("km_debut"),
            rs.getInt("km_actuel"),
            rs.getString("statut"),
            rs.getString("motif"),
            rs.getString("vehicule_image")
        );
    }

    private Entretien mapEntretien(ResultSet rs) throws SQLException {
        return new Entretien(
            rs.getString("id"),
            rs.getString("vehicule"),
            rs.getString("immatriculation"),
            rs.getString("numero_inv"),
            rs.getString("type"),
            rs.getString("nature"),
            rs.getString("description"),
            rs.getString("garage"),
            rs.getString("adresse_garage"),
            rs.getString("technicien"),
            rs.getString("date_entree"),
            rs.getString("date_sortie_prevue"),
            rs.getString("date_sortie_reelle"),
            rs.getInt("cout_estime"),
            rs.getInt("cout_reel"),
            rs.getString("statut"),
            rs.getString("priorite"),
            rs.getInt("km_entree"),
            stringToPieces(rs.getString("pieces")),
            rs.getString("bon_commande")
        );
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getString("departement"),
            rs.getString("avatar")
        );
    }

    private void setVehicleParams(PreparedStatement ps, Vehicle v) throws SQLException {
        ps.setString(1, v.getNumeroInventaire());
        ps.setString(2, v.getMarque());
        ps.setString(3, v.getModele());
        ps.setInt(4, v.getAnnee());
        ps.setString(5, v.getImmatriculation());
        ps.setString(6, v.getVin());
        ps.setString(7, v.getCarburant());
        ps.setString(8, v.getTransmission());
        ps.setString(9, v.getCouleur());
        ps.setInt(10, v.getKilometrage());
        ps.setString(11, v.getStatut());
        ps.setString(12, v.getCategorie());
        ps.setString(13, v.getDepartement());
        ps.setString(14, v.getDateAchat());
        ps.setDouble(15, v.getPrixAchat());
        ps.setInt(16, v.getKmAchat());
        ps.setString(17, v.getDateMiseService());
        ps.setString(18, v.getDateHorsService());
        ps.setString(19, v.getAssuranceNum());
        ps.setString(20, v.getAssuranceDebut());
        ps.setString(21, v.getAssuranceFin());
        ps.setString(22, v.getAssuranceCompagnie());
        ps.setString(23, v.getVisiteTechniqueDate());
        ps.setString(24, v.getVisiteTechniqueEcheance());
        ps.setString(25, v.getCarteGriseNum());
        ps.setInt(26, v.getProchainEntretienKm());
        ps.setString(27, v.getImage());
    }

    private void setEntretienParams(PreparedStatement ps, Entretien e) throws SQLException {
        ps.setString(1, e.getId());
        ps.setString(2, e.getVehicule());
        ps.setString(3, e.getImmatriculation());
        ps.setString(4, e.getNumeroInv());
        ps.setString(5, e.getType());
        ps.setString(6, e.getNature());
        ps.setString(7, e.getDescription());
        ps.setString(8, e.getGarage());
        ps.setString(9, e.getAdresseGarage());
        ps.setString(10, e.getTechnicien());
        ps.setString(11, e.getDateEntree());
        ps.setString(12, e.getDateSortiePrevue());
        ps.setString(13, e.getDateSortieReelle());
        ps.setInt(14, e.getCoutEstime());
        ps.setInt(15, e.getCoutReel());
        ps.setString(16, e.getStatut());
        ps.setString(17, e.getPriorite());
        ps.setInt(18, e.getKmEntree());
        ps.setString(19, piecesToString(e.getPieces()));
        ps.setString(20, e.getBonCommande());
    }

    private String piecesToString(List<String> pieces) {
        if (pieces == null || pieces.isEmpty()) return "";
        return String.join("|", pieces);
    }

    private List<String> stringToPieces(String s) {
        if (s == null || s.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(s.split("\\|")));
    }

    private void exec(String sql, Object param) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param instanceof Integer i) ps.setInt(1, i);
            else ps.setString(1, param.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int queryMaxInt(String sql) {
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
