package com.miage.fleet.service;

import com.miage.fleet.model.Affectation;
import com.miage.fleet.model.Entretien;
import com.miage.fleet.model.Mission;
import com.miage.fleet.model.User;
import com.miage.fleet.model.Vehicle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockDataService {

    private static MockDataService instance;
    private User currentUser;

    private final List<Vehicle> vehicules;
    private final List<Mission> missions;
    private final List<Affectation> affectations;
    private final List<User> users;
    private final List<Entretien> entretiens;

    private MockDataService() {
        vehicules = initVehicules();
        missions = initMissions();
        affectations = initAffectations();
        users = initUsers();
        entretiens = initEntretiens();
    }

    public static MockDataService getInstance() {
        if (instance == null) instance = new MockDataService();
        return instance;
    }

    /** For tests only — resets the singleton so each test starts fresh */
    public static void resetInstance() { instance = null; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }

    public boolean login(String email, String password) {
        Optional<User> found = users.stream()
            .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
            .findFirst();
        found.ifPresent(u -> currentUser = u);
        return found.isPresent();
    }

    public void logout() { currentUser = null; }

    // ── Read ─────────────────────────────────────────────────────────────────
    public List<Vehicle> getVehicules() { return vehicules; }
    public List<Mission> getMissions() { return missions; }
    public List<Affectation> getAffectations() { return affectations; }
    public List<User> getUsers() { return users; }
    public List<Entretien> getEntretiens() { return entretiens; }

    // ── Vehicle CRUD ─────────────────────────────────────────────────────────
    public void addVehicle(Vehicle v) { vehicules.add(v); }
    public void updateVehicle(Vehicle updated) {
        for (int i = 0; i < vehicules.size(); i++) {
            if (vehicules.get(i).getId() == updated.getId()) { vehicules.set(i, updated); return; }
        }
    }
    public void deleteVehicle(int id) { vehicules.removeIf(v -> v.getId() == id); }

    // ── Mission CRUD ─────────────────────────────────────────────────────────
    public void addMission(Mission m) { missions.add(m); }
    public void updateMission(Mission updated) {
        for (int i = 0; i < missions.size(); i++) {
            if (missions.get(i).getId().equals(updated.getId())) { missions.set(i, updated); return; }
        }
    }
    public void deleteMission(String id) { missions.removeIf(m -> m.getId().equals(id)); }

    // ── Affectation CRUD ─────────────────────────────────────────────────────
    public void addAffectation(Affectation a) { affectations.add(a); }
    public void updateAffectation(Affectation updated) {
        for (int i = 0; i < affectations.size(); i++) {
            if (affectations.get(i).getId().equals(updated.getId())) { affectations.set(i, updated); return; }
        }
    }
    public void deleteAffectation(String id) { affectations.removeIf(a -> a.getId().equals(id)); }

    // ── Entretien CRUD ───────────────────────────────────────────────────────
    public void addEntretien(Entretien e) { entretiens.add(e); }
    public void updateEntretien(Entretien updated) {
        for (int i = 0; i < entretiens.size(); i++) {
            if (entretiens.get(i).getId().equals(updated.getId())) { entretiens.set(i, updated); return; }
        }
    }
    public void deleteEntretien(String id) { entretiens.removeIf(e -> e.getId().equals(id)); }

    // ── User CRUD ────────────────────────────────────────────────────────────
    public void addUser(User u) { users.add(u); }
    public void updateUser(User updated) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updated.getId()) { users.set(i, updated); return; }
        }
    }

    // ── ID generators ────────────────────────────────────────────────────────
    public int generateVehicleId() { return vehicules.isEmpty() ? 1 : vehicules.stream().mapToInt(Vehicle::getId).max().getAsInt() + 1; }
    public String generateVehicleNumInv() { return String.format("VH-%d-%03d", LocalDate.now().getYear(), generateVehicleId()); }
    public String generateMissionId() { return String.format("OM-%d-%04d", LocalDate.now().getYear(), missions.size() + 1); }
    public String generateAffectationId() { return String.format("AFF-%d-%03d", LocalDate.now().getYear(), affectations.size() + 1); }
    public String generateEntretienId() { return String.format("ENT-%d-%03d", LocalDate.now().getYear(), entretiens.size() + 1); }
    public int generateUserId() { return users.isEmpty() ? 1 : users.stream().mapToInt(User::getId).max().getAsInt() + 1; }

    private List<User> initUsers() {
        List<User> list = new ArrayList<>();
        list.add(new User(1, "Koné", "Ibrahim", "i.kone@societe.ci", "admin123", "admin", "Direction Générale", "IK"));
        list.add(new User(2, "Coulibaly", "Adjoua", "a.coulibaly@societe.ci", "agent123", "agent_logistique", "Logistique", "AC"));
        list.add(new User(3, "Traoré", "Bamba", "b.traore@societe.ci", "consul123", "consultant", "Commercial", "BT"));
        return list;
    }

    private List<Vehicle> initVehicules() {
        List<Vehicle> list = new ArrayList<>();

        list.add(new Vehicle(1, "VH-2022-001", "Peugeot", "308", 2022,
            "AB-1234-CI", "VF3LC9HZ0LW123456", "Diesel", "Manuelle", "Gris Platine",
            42500, "Disponible", "Berline", "Direction Générale",
            "15/03/2022", 14500000, 0, "20/03/2022", null,
            "ASS-2024-10234", "01/01/2025", "31/12/2025", "NSIA Assurances CI",
            "10/01/2025", "10/01/2026", "CG-AB-2022-1234", 45000,
            "https://images.unsplash.com/photo-1770385434759-d1e7799e2239?w=400"));

        list.add(new Vehicle(2, "VH-2023-002", "Toyota", "Hilux", 2023,
            "BY-5678-CI", "JTFST13V80D123456", "Diesel", "Automatique", "Blanc",
            18200, "En mission", "Pick-up", "Technique",
            "05/02/2023", 22000000, 0, "10/02/2023", null,
            "ASS-2024-10890", "01/01/2025", "31/05/2025", "SUNU Assurances CI",
            "15/03/2025", "15/03/2026", "CG-BY-2023-5678", 20000,
            "https://images.unsplash.com/photo-1758219944301-48c0bac14d21?w=400"));

        list.add(new Vehicle(3, "VH-2021-003", "Renault", "Kangoo", 2021,
            "CV-9012-CI", "VF1FC1C0H45123456", "Diesel", "Manuelle", "Blanc Glacé",
            67800, "En réparation", "Utilitaire", "Logistique",
            "20/07/2021", 9500000, 0, "25/07/2021", null,
            "ASS-2024-11002", "01/01/2025", "31/12/2025", "Allianz Côte d'Ivoire",
            "05/06/2024", "05/06/2025", "CG-CV-2021-9012", 70000,
            "https://images.unsplash.com/photo-1649553324097-dec0c232be42?w=400"));

        list.add(new Vehicle(4, "VH-2020-004", "Toyota", "Land Cruiser", 2020,
            "DK-3456-CI", "UU1HSDB1L12345678", "Diesel", "Automatique", "Gris Minéral",
            89300, "Disponible", "SUV", "Commercial",
            "12/04/2020", 32000000, 0, "15/04/2020", null,
            "ASS-2024-10456", "01/01/2025", "31/12/2025", "NSIA Assurances CI",
            "20/02/2025", "20/02/2026", "CG-DK-2020-3456", 90000,
            "https://images.unsplash.com/photo-1653325189816-5d8dc746cc16?w=400"));

        list.add(new Vehicle(5, "VH-2023-005", "Volkswagen", "Caddy", 2023,
            "EM-7890-CI", "WV2ZZZ2KZLT123456", "Diesel", "Automatique", "Noir Profond",
            11400, "Affecté", "Utilitaire", "Direction Générale",
            "08/08/2023", 16500000, 0, "12/08/2023", null,
            "ASS-2024-12345", "01/01/2025", "31/12/2025", "SUNU Assurances CI",
            "18/04/2025", "18/04/2026", "CG-EM-2023-7890", 15000,
            "https://images.unsplash.com/photo-1765597119545-6b481bd14d37?w=400"));

        list.add(new Vehicle(6, "VH-2018-006", "Ford", "Transit", 2018,
            "FN-1111-CI", "WF0XXXTTGXGT12345", "Diesel", "Manuelle", "Blanc",
            134600, "Hors service", "Fourgon", "Logistique",
            "20/06/2018", 19000000, 0, "25/06/2018", "15/03/2025",
            "—", "—", "—", "—",
            "—", "—", "CG-FN-2018-1111", 0,
            "https://images.unsplash.com/photo-1650452233063-8f308616b729?w=400"));

        return list;
    }

    private List<Mission> initMissions() {
        List<Mission> list = new ArrayList<>();

        list.add(new Mission("OM-2025-0042", 2, "Toyota Hilux", "BY-5678-CI",
            "Kouassi Bamba", "EMP-0234", "Technique", "San-Pédro",
            "Inspection chantier — projet d'infrastructure portuaire",
            "07/05/2025", "10/05/2025", 17800, null,
            "En cours", "OM-2025-0042", "Diallo Sékou"));

        list.add(new Mission("OM-2025-0041", 1, "Peugeot 308", "AB-1234-CI",
            "Aya Koné", "EMP-0118", "Commercial", "Bouaké",
            "Réunion commerciale — partenariat distribution régionale",
            "05/05/2025", "06/05/2025", 41200, 42150,
            "Terminée", "OM-2025-0041", "Aya Koné"));

        list.add(new Mission("OM-2025-0043", 4, "Toyota Land Cruiser", "DK-3456-CI",
            "Ibrahim Diallo", "EMP-0305", "RH", "Yamoussoukro",
            "Formation — programme développement des compétences",
            "12/05/2025", "14/05/2025", 89300, null,
            "Planifiée", "OM-2025-0043", "Ibrahim Diallo"));

        list.add(new Mission("OM-2025-0039", 4, "Toyota Land Cruiser", "DK-3456-CI",
            "Fatou Coulibaly", "EMP-0087", "Finance", "Daloa",
            "Audit comptable — filiale Ouest",
            "28/04/2025", "30/04/2025", 87900, 89100,
            "Terminée", "OM-2025-0039", "Koné Alphonse"));

        list.add(new Mission("OM-2025-0040", 1, "Peugeot 308", "AB-1234-CI",
            "Pierre Koffi", "EMP-0145", "Direction Générale", "Abidjan — Siège social",
            "Réunion comité de direction — bilan trimestriel",
            "02/05/2025", "02/05/2025", 40800, 41200,
            "Terminée", "OM-2025-0040", "Pierre Koffi"));

        list.add(new Mission("OM-2025-0044", 3, "Renault Kangoo", "CV-9012-CI",
            "Service Logistique", "DEPT-LOG", "Logistique", "Korhogo",
            "Livraison équipements — entrepôt régional Nord",
            "14/05/2025", "15/05/2025", 67800, null,
            "Planifiée", "OM-2025-0044", "Bamba Oumar"));

        return list;
    }

    private List<Affectation> initAffectations() {
        List<Affectation> list = new ArrayList<>();

        list.add(new Affectation("AFF-2025-001", 5, "Volkswagen Caddy", "EM-7890-CI",
            "Koné Ibrahim", "EMP-0001", "Directeur Général", "Direction Générale",
            "01/01/2025", null, 9800, 11400, "Active",
            "Véhicule de fonction — Directeur Général",
            "https://images.unsplash.com/photo-1765597119545-6b481bd14d37?w=400"));

        list.add(new Affectation("AFF-2025-002", 4, "Toyota Land Cruiser", "DK-3456-CI",
            "Traoré Adjoua", "EMP-0198", "Responsable RH", "Ressources Humaines",
            "15/01/2025", null, 82000, 89300, "Active",
            "Véhicule de service permanent — zone Nord",
            "https://images.unsplash.com/photo-1653325189816-5d8dc746cc16?w=400"));

        list.add(new Affectation("AFF-2024-009", 1, "Peugeot 308", "AB-1234-CI",
            "Coulibaly Aminata", "EMP-0022", "DRH", "Ressources Humaines",
            "01/09/2024", "28/02/2025", 31000, 42500, "Terminée",
            "Véhicule de service temporaire",
            "https://images.unsplash.com/photo-1770385434759-d1e7799e2239?w=400"));

        list.add(new Affectation("AFF-2024-007", 4, "Toyota Land Cruiser", "DK-3456-CI",
            "Koffi Oumar", "EMP-0067", "Directeur Commercial", "Commercial",
            "01/03/2024", "31/12/2024", 51200, 78900, "Terminée",
            "Véhicule de service — tournées commerciales",
            "https://images.unsplash.com/photo-1653325189816-5d8dc746cc16?w=400"));

        return list;
    }

    private List<Entretien> initEntretiens() {
        List<Entretien> list = new ArrayList<>();
        list.add(new Entretien("ENT-2025-018", "Renault Kangoo", "CV-9012-CI", "VH-2021-003",
            "Réparation moteur", "Curatif",
            "Remplacement joint de culasse — surchauffe signalée par le chauffeur",
            "Renault Service Abidjan", "Zone Industrielle de Yopougon, Abidjan", "Koné Sylvain",
            "07/05/2025", "14/05/2025", null, 500000, -1, "En cours", "Haute", 67800,
            List.of("Joint de culasse", "Liquide de refroidissement 5L", "Joints d'étanchéité kit"),
            "BC-2025-0234"));
        list.add(new Entretien("ENT-2025-017", "Peugeot 308", "AB-1234-CI", "VH-2022-001",
            "Vidange + filtres", "Préventif",
            "Vidange huile 5W40, filtre huile, filtre air, filtre habitacle — révision à 40 000 km",
            "Peugeot Service Abidjan", "Avenue Houphouët-Boigny, Abidjan", "Kouassi Derrick",
            "07/03/2025", "07/03/2025", "07/03/2025", 110000, 105000, "Terminé", "Normale", 40000,
            List.of("Huile moteur 5W40 5L", "Filtre huile", "Filtre air", "Filtre habitacle"),
            "BC-2025-0198"));
        list.add(new Entretien("ENT-2025-019", "Toyota Hilux", "BY-5678-CI", "VH-2023-002",
            "Révision à 20 000 km", "Préventif",
            "Révision constructeur à 20 000 km — inspection complète + vidange",
            "Toyota Center Abidjan", "Route de Bingerville, Abidjan", "Diallo Ousmane",
            "15/05/2025", "16/05/2025", null, 280000, -1, "Planifié", "Normale", 20000,
            List.of("Huile moteur diesel 5W30 7L", "Filtre huile", "Filtre carburant", "Filtre air"),
            "BC-2025-0241"));
        list.add(new Entretien("ENT-2025-015", "Toyota Land Cruiser", "DK-3456-CI", "VH-2020-004",
            "Freins — remplacement", "Curatif",
            "Usure excessive des plaquettes avant — remplacement disques et plaquettes",
            "Toyota Center Abidjan", "Route de Bingerville, Abidjan", "Traoré Ibrahima",
            "15/04/2025", "16/04/2025", "16/04/2025", 220000, 235000, "Terminé", "Haute", 87200,
            List.of("Plaquettes frein avant x2", "Disques frein avant x2", "Liquide de frein"),
            "BC-2025-0187"));
        return list;
    }

    public int[][] getMissionsMensuellesData() {
        return new int[][]{{1,18},{2,14},{3,22},{4,19},{5,27},{6,23},{7,31},{8,28},{9,24},{10,29},{11,21},{12,33}};
    }

    public int[][] getKmMensuelData() {
        return new int[][]{{7,22100},{8,20400},{9,17300},{10,19800},{11,14900},{12,24600}};
    }

    public String[] getMoisLabels() {
        return new String[]{"Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};
    }
}
