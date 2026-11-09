package com.miage.fleet.service;

import com.miage.fleet.model.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MockDataServiceTest {

    @BeforeEach
    void reset() {
        MockDataService.resetInstance();
    }

    // ─── Singleton ────────────────────────────────────────────────────────────

    @Test @Order(1)
    void singletonReturnsSameInstance() {
        MockDataService a = MockDataService.getInstance();
        MockDataService b = MockDataService.getInstance();
        assertSame(a, b);
    }

    @Test @Order(2)
    void resetInstanceCreatesNewSingleton() {
        MockDataService first = MockDataService.getInstance();
        MockDataService.resetInstance();
        MockDataService second = MockDataService.getInstance();
        assertNotSame(first, second);
    }

    // ─── Initial data ─────────────────────────────────────────────────────────

    @Test @Order(3)
    void initialDataIsLoaded() {
        MockDataService ds = MockDataService.getInstance();
        assertFalse(ds.getVehicules().isEmpty(), "vehicles should be seeded");
        assertFalse(ds.getMissions().isEmpty(), "missions should be seeded");
        assertFalse(ds.getAffectations().isEmpty(), "affectations should be seeded");
        assertFalse(ds.getUsers().isEmpty(), "users should be seeded");
        assertFalse(ds.getEntretiens().isEmpty(), "entretiens should be seeded");
    }

    // ─── Login / Logout ───────────────────────────────────────────────────────

    @Test @Order(4)
    void loginSucceedsWithCorrectCredentials() {
        MockDataService ds = MockDataService.getInstance();
        assertTrue(ds.login("i.kone@societe.ci", "admin123"));
        assertNotNull(ds.getCurrentUser());
        assertEquals("Ibrahim", ds.getCurrentUser().getPrenom());
    }

    @Test @Order(5)
    void loginFailsWithBadPassword() {
        MockDataService ds = MockDataService.getInstance();
        assertFalse(ds.login("i.kone@societe.ci", "wrong"));
        assertNull(ds.getCurrentUser());
    }

    @Test @Order(6)
    void logoutClearsCurrentUser() {
        MockDataService ds = MockDataService.getInstance();
        ds.login("i.kone@societe.ci", "admin123");
        ds.logout();
        assertNull(ds.getCurrentUser());
    }

    // ─── Vehicle CRUD ─────────────────────────────────────────────────────────

    @Test @Order(10)
    void addVehicleIncreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        int before = ds.getVehicules().size();
        Vehicle v = new Vehicle(ds.generateVehicleId(), ds.generateVehicleNumInv(),
            "Kia", "Sportage", 2024, "XX-0001-CI", "KIA123456789",
            "Essence", "Automatique", "Blanc", 0, "Disponible", "SUV",
            "Technique", "01/01/2024", 18000000, 0, "01/01/2024", null,
            "—", "—", "—", "—", "—", "—", "—", 5000, null);
        ds.addVehicle(v);
        assertEquals(before + 1, ds.getVehicules().size());
    }

    @Test @Order(11)
    void updateVehicleChangesStatut() {
        MockDataService ds = MockDataService.getInstance();
        Vehicle first = ds.getVehicules().get(0);
        int id = first.getId();
        first.setStatut("Hors service");
        ds.updateVehicle(first);
        Vehicle updated = ds.getVehicules().stream()
            .filter(v -> v.getId() == id).findFirst().orElseThrow();
        assertEquals("Hors service", updated.getStatut());
    }

    @Test @Order(12)
    void deleteVehicleDecreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        int id = ds.getVehicules().get(0).getId();
        int before = ds.getVehicules().size();
        ds.deleteVehicle(id);
        assertEquals(before - 1, ds.getVehicules().size());
        assertTrue(ds.getVehicules().stream().noneMatch(v -> v.getId() == id));
    }

    @Test @Order(13)
    void generateVehicleIdIsUnique() {
        MockDataService ds = MockDataService.getInstance();
        int id1 = ds.generateVehicleId();
        Vehicle v = new Vehicle(id1, "V1", "A", "B", 2024, "I", "VIN", "D", "M", "C",
            0, "Disponible", "Cat", "Dept", "01/01/2024", 0, 0, "01/01/2024",
            null, "—", "—", "—", "—", "—", "—", "—", 5000, null);
        ds.addVehicle(v);
        int id2 = ds.generateVehicleId();
        assertNotEquals(id1, id2);
        assertTrue(id2 > id1);
    }

    // ─── Mission CRUD ─────────────────────────────────────────────────────────

    @Test @Order(20)
    void addMissionIncreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        int before = ds.getMissions().size();
        String id = ds.generateMissionId();
        Mission m = new Mission(id, 1, "Toyota Hilux", "BY-5678-CI",
            "Test User", "EMP-9999", "Test Dept", "Abidjan",
            "Test mission", "01/06/2025", "02/06/2025",
            10000, null, "Planifiée", id, "Chauffeur Test");
        ds.addMission(m);
        assertEquals(before + 1, ds.getMissions().size());
    }

    @Test @Order(21)
    void updateMissionChangesStatut() {
        MockDataService ds = MockDataService.getInstance();
        Mission first = ds.getMissions().get(0);
        String id = first.getId();
        first.setStatut("Annulée");
        ds.updateMission(first);
        Mission updated = ds.getMissions().stream()
            .filter(m -> m.getId().equals(id)).findFirst().orElseThrow();
        assertEquals("Annulée", updated.getStatut());
    }

    @Test @Order(22)
    void deleteMissionDecreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        String id = ds.getMissions().get(0).getId();
        int before = ds.getMissions().size();
        ds.deleteMission(id);
        assertEquals(before - 1, ds.getMissions().size());
        assertTrue(ds.getMissions().stream().noneMatch(m -> m.getId().equals(id)));
    }

    @Test @Order(23)
    void generateMissionIdHasPrefix() {
        MockDataService ds = MockDataService.getInstance();
        String id = ds.generateMissionId();
        assertTrue(id.startsWith("OM-"), "mission ID should start with OM-: " + id);
    }

    // ─── Affectation CRUD ─────────────────────────────────────────────────────

    @Test @Order(30)
    void addAffectationIncreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        int before = ds.getAffectations().size();
        String id = ds.generateAffectationId();
        Affectation a = new Affectation(id, 1, "Peugeot 308", "AB-1234-CI",
            "Nom Prénom", "EMP-0099", "Ingénieur", "IT",
            "01/05/2025", null, 5000, 5000, "Active",
            "Affectation test", null);
        ds.addAffectation(a);
        assertEquals(before + 1, ds.getAffectations().size());
    }

    @Test @Order(31)
    void cloturerAffectationChangesStatut() {
        MockDataService ds = MockDataService.getInstance();
        Affectation active = ds.getAffectations().stream()
            .filter(a -> "Active".equals(a.getStatut())).findFirst().orElseThrow();
        String id = active.getId();
        active.setStatut("Terminée");
        active.setDateFin("31/05/2025");
        ds.updateAffectation(active);
        Affectation updated = ds.getAffectations().stream()
            .filter(a -> a.getId().equals(id)).findFirst().orElseThrow();
        assertEquals("Terminée", updated.getStatut());
        assertEquals("31/05/2025", updated.getDateFin());
    }

    @Test @Order(32)
    void deleteAffectationDecreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        String id = ds.getAffectations().get(0).getId();
        int before = ds.getAffectations().size();
        ds.deleteAffectation(id);
        assertEquals(before - 1, ds.getAffectations().size());
    }

    @Test @Order(33)
    void generateAffectationIdHasPrefix() {
        MockDataService ds = MockDataService.getInstance();
        String id = ds.generateAffectationId();
        assertTrue(id.startsWith("AFF-"), "affectation ID should start with AFF-: " + id);
    }

    // ─── Entretien CRUD ───────────────────────────────────────────────────────

    @Test @Order(40)
    void addEntretienIncreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        int before = ds.getEntretiens().size();
        String id = ds.generateEntretienId();
        Entretien e = new Entretien(id, "Toyota Hilux", "BY-5678-CI", "VH-2023-002",
            "Vidange", "Préventif", "Vidange routine",
            "Garage Test", "Adresse Test", "Tech Test",
            "01/06/2025", "02/06/2025", null,
            80000, -1, "Planifié", "Normale", 20000,
            List.of("Huile 5W40"), "BC-TEST");
        ds.addEntretien(e);
        assertEquals(before + 1, ds.getEntretiens().size());
    }

    @Test @Order(41)
    void updateEntretienChangesStatut() {
        MockDataService ds = MockDataService.getInstance();
        Entretien first = ds.getEntretiens().get(0);
        String id = first.getId();
        first.setStatut("Terminé");
        ds.updateEntretien(first);
        Entretien updated = ds.getEntretiens().stream()
            .filter(e -> e.getId().equals(id)).findFirst().orElseThrow();
        assertEquals("Terminé", updated.getStatut());
    }

    @Test @Order(42)
    void deleteEntretienDecreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        String id = ds.getEntretiens().get(0).getId();
        int before = ds.getEntretiens().size();
        ds.deleteEntretien(id);
        assertEquals(before - 1, ds.getEntretiens().size());
    }

    @Test @Order(43)
    void generateEntretienIdHasPrefix() {
        MockDataService ds = MockDataService.getInstance();
        String id = ds.generateEntretienId();
        assertTrue(id.startsWith("ENT-"), "entretien ID should start with ENT-: " + id);
    }

    // ─── User CRUD ────────────────────────────────────────────────────────────

    @Test @Order(50)
    void addUserIncreasesCount() {
        MockDataService ds = MockDataService.getInstance();
        int before = ds.getUsers().size();
        int id = ds.generateUserId();
        User u = new User(id, "Diallo", "Fatou", "f.diallo@societe.ci",
            "pass123", "consultant", "Finance", "FD");
        ds.addUser(u);
        assertEquals(before + 1, ds.getUsers().size());
    }

    @Test @Order(51)
    void updateUserChangesRole() {
        MockDataService ds = MockDataService.getInstance();
        User first = ds.getUsers().get(0);
        int id = first.getId();
        first.setRole("consultant");
        ds.updateUser(first);
        User updated = ds.getUsers().stream()
            .filter(u -> u.getId() == id).findFirst().orElseThrow();
        assertEquals("consultant", updated.getRole());
    }

    @Test @Order(52)
    void generateUserIdIsSequential() {
        MockDataService ds = MockDataService.getInstance();
        int id1 = ds.generateUserId();
        ds.addUser(new User(id1, "A", "B", "ab@test.ci", "p", "consultant", "IT", "AB"));
        int id2 = ds.generateUserId();
        assertTrue(id2 > id1);
    }

    @Test @Order(53)
    void loginAfterAddUserWorks() {
        MockDataService ds = MockDataService.getInstance();
        int id = ds.generateUserId();
        ds.addUser(new User(id, "Doe", "Jane", "j.doe@societe.ci", "secret", "consultant", "IT", "JD"));
        assertTrue(ds.login("j.doe@societe.ci", "secret"));
    }

    // ─── Model helpers ────────────────────────────────────────────────────────

    @Test @Order(60)
    void vehicleNomComplet() {
        MockDataService ds = MockDataService.getInstance();
        Vehicle v = ds.getVehicules().get(0);
        assertEquals(v.getMarque() + " " + v.getModele(), v.getNomComplet());
    }

    @Test @Order(61)
    void missionKmTotal() {
        Mission m = new Mission("X", 1, "V", "I", "E", "EM", "D", "Dest",
            "Motif", "01/01/2025", "02/01/2025", 1000, 1250,
            "Terminée", "X", "C");
        assertEquals(250, m.getKmTotal());
    }

    @Test @Order(62)
    void missionKmTotalNullWhenNoRetour() {
        Mission m = new Mission("X", 1, "V", "I", "E", "EM", "D", "Dest",
            "Motif", "01/01/2025", "02/01/2025", 1000, null,
            "En cours", "X", "C");
        assertNull(m.getKmTotal());
    }

    @Test @Order(63)
    void affectationKmParcourus() {
        Affectation a = new Affectation("A1", 1, "V", "I", "E", "EM", "F",
            "D", "01/01/2025", null, 5000, 8000, "Active", "Motif", null);
        assertEquals(3000, a.getKmParcourus());
    }

    @Test @Order(64)
    void userNomCompletConcatenates() {
        User u = new User(1, "Koné", "Ibrahim", "i@ci", "p", "admin", "DG", "IK");
        assertEquals("Ibrahim Koné", u.getNomComplet());
    }

    @Test @Order(65)
    void userRoleLabelAdmin() {
        User u = new User(1, "K", "I", "i@ci", "p", "admin", "DG", "IK");
        assertEquals("Administrateur", u.getRoleLabel());
    }

    @Test @Order(66)
    void userRoleLabelAgentLogistique() {
        User u = new User(2, "K", "I", "i@ci", "p", "agent_logistique", "L", "IK");
        assertEquals("Agent Logistique", u.getRoleLabel());
    }
}
