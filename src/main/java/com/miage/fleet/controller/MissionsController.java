package com.miage.fleet.controller;

import com.miage.fleet.model.Mission;
import com.miage.fleet.service.DatabaseService;
import com.miage.fleet.util.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MissionsController {

    @FXML private HBox statsRow;
    @FXML private HBox statusFilters;
    @FXML private TextField searchField;
    @FXML private VBox missionsContainer;
    @FXML private VBox emptyState;
    @FXML private StackPane modalOverlay;

    private final DatabaseService data = DatabaseService.getInstance();
    private String activeStatut = "Tous";
    private String expandedId = null;

    private static final String[] STATUTS = {"Tous", "En cours", "Planifiée", "Terminée", "Annulée"};
    private static final java.util.Map<String, String> BADGE_CLASS = java.util.Map.of(
        "En cours", "badge-en-cours",
        "Terminée", "badge-terminee",
        "Planifiée", "badge-planifiee",
        "Annulée", "badge-annulee"
    );

    @FXML
    public void initialize() {
        buildStatsCards();
        buildStatusFilters();
        renderMissions();
    }

    private void buildStatsCards() {
        List<Mission> missions = data.getMissions();
        long enCours = missions.stream().filter(m -> "En cours".equals(m.getStatut())).count();
        long planifiees = missions.stream().filter(m -> "Planifiée".equals(m.getStatut())).count();
        long terminees = missions.stream().filter(m -> "Terminée".equals(m.getStatut())).count();

        statsRow.getChildren().addAll(
            makeStatCard("Total missions", missions.size(), "stat-card-slate", "stat-value-slate"),
            makeStatCard("En cours", (int) enCours, "stat-card-blue", "stat-value-blue"),
            makeStatCard("Planifiées", (int) planifiees, "stat-card-violet", "stat-value-violet"),
            makeStatCard("Terminées", (int) terminees, "stat-card-emerald", "stat-value-emerald")
        );
        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
    }

    private VBox makeStatCard(String label, int value, String cardStyle, String valueStyle) {
        VBox card = new VBox(4);
        card.getStyleClass().add(cardStyle);
        card.setPadding(new Insets(16));
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(String.valueOf(value));
        val.getStyleClass().add(valueStyle);
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private void buildStatusFilters() {
        statusFilters.getChildren().clear();
        for (String s : STATUTS) {
            Button btn = new Button(s);
            btn.getStyleClass().add(s.equals(activeStatut) ? "filter-btn-active" : "filter-btn");
            btn.setStyle(s.equals(activeStatut) ? "-fx-font-size: 13px; -fx-padding: 8 14;" : "-fx-font-size: 13px; -fx-padding: 8 14;");
            btn.setOnAction(e -> {
                activeStatut = s;
                buildStatusFilters();
                renderMissions();
            });
            statusFilters.getChildren().add(btn);
        }
    }

    @FXML
    private void applyFilters() {
        renderMissions();
    }

    private List<Mission> getFiltered() {
        String search = searchField.getText().toLowerCase();
        return data.getMissions().stream()
            .filter(m -> {
                boolean matchSearch = search.isEmpty() ||
                    (m.getEmploye() + " " + m.getVehicule() + " " + m.getDestination() + " " + m.getId())
                        .toLowerCase().contains(search);
                boolean matchStatut = "Tous".equals(activeStatut) || m.getStatut().equals(activeStatut);
                return matchSearch && matchStatut;
            })
            .collect(Collectors.toList());
    }

    private void renderMissions() {
        missionsContainer.getChildren().clear();
        List<Mission> filtered = getFiltered();

        if (filtered.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            for (Mission m : filtered) {
                missionsContainer.getChildren().add(makeMissionCard(m));
            }
        }
    }

    private VBox makeMissionCard(Mission m) {
        VBox card = new VBox(0);
        card.getStyleClass().add("mission-card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 16; -fx-padding: 20;");

        HBox mainRow = new HBox(16);
        mainRow.setAlignment(Pos.TOP_LEFT);

        // Icon
        StackPane iconBox = new StackPane();
        iconBox.setMinSize(40, 40);
        iconBox.setMaxSize(40, 40);
        iconBox.getStyleClass().add(getIconBoxClass(m.getStatut()));
        Label iconLbl = new Label("✈");
        iconLbl.setStyle("-fx-text-fill: " + getIconColor(m.getStatut()) + "; -fx-font-size: 16px;");
        iconBox.getChildren().add(iconLbl);

        // Info
        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Top row: order number + badge
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label orderLbl = new Label(m.getOrdreMission());
        orderLbl.getStyleClass().add("immat-tag");
        Label statusBadge = new Label(m.getStatut());
        statusBadge.getStyleClass().add(BADGE_CLASS.getOrDefault(m.getStatut(), "badge-en-cours"));
        topRow.getChildren().addAll(orderLbl, statusBadge);

        // Employee → destination
        HBox empRow = new HBox(8);
        empRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setMinSize(24, 24);
        avatar.setMaxSize(24, 24);
        avatar.getStyleClass().add("avatar-sm-blue");
        Label avatarText = new Label(String.valueOf(m.getEmploye().charAt(0)));
        avatarText.getStyleClass().add("avatar-text-sm");
        avatar.getChildren().add(avatarText);

        Label empName = new Label(m.getEmploye());
        empName.getStyleClass().add("mission-employe-name");
        Label matricLbl = new Label("(" + m.getEmployeMatricule() + ")");
        matricLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-family: monospace;");
        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 16px;");
        Label destLbl = new Label("📍 " + m.getDestination());
        destLbl.getStyleClass().add("mission-destination");
        empRow.getChildren().addAll(avatar, empName, matricLbl, arrow, destLbl);

        // Motif
        Label motifLbl = new Label(m.getMotif());
        motifLbl.getStyleClass().add("mission-motif");

        // Details row
        HBox detailRow = new HBox(16);
        detailRow.setAlignment(Pos.CENTER_LEFT);
        Label vehicleLbl = new Label("🚗 " + m.getVehicule() + " — " + m.getImmatriculation());
        vehicleLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Label dateLbl = new Label("📅 " + m.getDateDepart() + " → " + m.getDateRetour());
        dateLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        detailRow.getChildren().addAll(vehicleLbl, dateLbl);

        if (m.getKmTotal() != null) {
            Label kmLbl = new Label("⊙ " + String.format(Locale.FRANCE, "%,d", m.getKmTotal()).replace(",", " ") + " km");
            kmLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
            detailRow.getChildren().add(kmLbl);
        }

        info.getChildren().addAll(topRow, empRow, motifLbl, detailRow);

        // Expand button
        boolean isExpanded = m.getId().equals(expandedId);
        Button expandBtn = new Button(isExpanded ? "∧" : "∨");
        expandBtn.getStyleClass().add("expand-btn");
        expandBtn.setOnAction(e -> {
            expandedId = isExpanded ? null : m.getId();
            renderMissions();
        });

        mainRow.getChildren().addAll(iconBox, info, expandBtn);
        card.getChildren().add(mainRow);

        // Expanded section
        if (isExpanded) {
            VBox expandedBox = new VBox(0);
            expandedBox.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #f1f5f9 transparent transparent transparent; -fx-border-width: 1; -fx-padding: 16 0 0 0; -fx-margin: 16 0 0 0;");
            VBox.setMargin(expandedBox, new Insets(16, 0, 0, 0));

            HBox cols = new HBox(20);
            cols.setPadding(new Insets(0));

            // Col 1: Mission details
            VBox col1 = new VBox(8);
            HBox.setHgrow(col1, Priority.ALWAYS);
            Label c1Title = new Label("DÉTAILS MISSION");
            c1Title.getStyleClass().add("detail-section-title");
            addDetailRow(col1, "Ordre de mission", m.getOrdreMission(), true);
            addDetailRow(col1, "Département", m.getDepartement(), false);
            addDetailRow(col1, "Chauffeur", m.getChauffeur(), false);
            addDetailRow(col1, "Départ", m.getDateDepart(), false);
            addDetailRow(col1, "Retour prévu", m.getDateRetour(), false);
            col1.getChildren().add(0, c1Title);

            // Col 2: Kilométrage
            VBox col2 = new VBox(8);
            HBox.setHgrow(col2, Priority.ALWAYS);
            Label c2Title = new Label("KILOMÉTRAGE");
            c2Title.getStyleClass().add("detail-section-title");
            addDetailRow(col2, "KM départ", formatKm(m.getKmDepart()), false);
            addDetailRow(col2, "KM retour", m.getKmRetour() != null ? formatKm(m.getKmRetour()) : "Non renseigné", false);
            addDetailRow(col2, "Distance totale", m.getKmTotal() != null ? formatKm(m.getKmTotal()) : "—", false);
            col2.getChildren().add(0, c2Title);

            // Col 3: Actions
            VBox col3 = new VBox(8);
            HBox.setHgrow(col3, Priority.ALWAYS);
            Label c3Title = new Label("ACTIONS");
            c3Title.getStyleClass().add("detail-section-title");
            Label motifDetail = new Label(m.getMotif());
            motifDetail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px; -fx-wrap-text: true;");
            motifDetail.setWrapText(true);

            HBox actionBtns = new HBox(8);
            VBox.setMargin(actionBtns, new Insets(8, 0, 0, 0));
            Button editBtn = new Button("✏ Modifier");
            editBtn.getStyleClass().add("btn-primary");
            editBtn.setStyle("-fx-padding: 8 12; -fx-font-size: 12px;");
            HBox.setHgrow(editBtn, Priority.ALWAYS);
            editBtn.setMaxWidth(Double.MAX_VALUE);
            editBtn.setOnAction(ev -> showEditMissionModal(m));
            Button docBtn = new Button("📄");
            docBtn.getStyleClass().add("btn-secondary");
            docBtn.setStyle("-fx-padding: 8 12;");
            actionBtns.getChildren().addAll(editBtn, docBtn);

            col3.getChildren().addAll(c3Title, motifDetail, actionBtns);

            cols.getChildren().addAll(col1, col2, col3);
            expandedBox.getChildren().add(cols);
            card.getChildren().add(expandedBox);
        }

        return card;
    }

    private void addDetailRow(VBox parent, String label, String value, boolean mono) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("detail-label");
        HBox.setHgrow(lbl, Priority.SOMETIMES);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        if (mono) {
            val.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px; -fx-font-weight: 500; -fx-font-family: monospace;");
        } else {
            val.getStyleClass().add("detail-value");
        }
        row.getChildren().addAll(lbl, spacer, val);
        parent.getChildren().add(row);
    }

    private String getIconBoxClass(String statut) {
        return switch (statut) {
            case "En cours" -> "mission-icon-en-cours";
            case "Terminée" -> "mission-icon-terminee";
            case "Planifiée" -> "mission-icon-planifiee";
            default -> "mission-icon-default";
        };
    }

    private String getIconColor(String statut) {
        return switch (statut) {
            case "En cours" -> "#2563eb";
            case "Terminée" -> "#059669";
            case "Planifiée" -> "#7c3aed";
            default -> "#94a3b8";
        };
    }

    private String formatKm(int km) {
        return String.format(Locale.FRANCE, "%,d", km).replace(",", " ") + " km";
    }

    private static int safeParseInt(String s) {
        try { return Integer.parseInt(s.trim().replace(" ", "").replace("km", "")); }
        catch (Exception e) { return 0; }
    }

    @FXML
    private void showNewMissionForm() {
        showModal();
    }

    private void showEditMissionModal(Mission m) {
        modalOverlay.getChildren().clear();
        modalOverlay.setMouseTransparent(false);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);

        VBox modal = new VBox(0);
        modal.getStyleClass().add("modal-card");
        modal.setMaxWidth(560);
        modal.setOnMouseClicked(javafx.event.Event::consume);

        HBox header = new HBox(12);
        header.getStyleClass().add("modal-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Modifier — " + m.getOrdreMission());
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 20, 16, 20));

        String[][] fields = {
            {m.getEmploye(),         "Employé",        "0", "0"},
            {m.getEmployeMatricule(),"Matricule",       "1", "0"},
            {m.getDepartement(),     "Département",     "0", "1"},
            {m.getVehicule(),        "Véhicule",        "1", "1"},
            {m.getDestination(),     "Destination",     "0-1", "2"},
            {m.getMotif(),           "Motif",           "0-1", "3"},
            {m.getDateDepart(),      "Date départ",     "0", "4"},
            {m.getDateRetour(),      "Date retour",     "1", "4"},
            {String.valueOf(m.getKmDepart()), "KM départ", "0", "5"},
            {m.getChauffeur(),       "Chauffeur",       "1", "5"},
        };

        TextField[] tfs = new TextField[fields.length];
        int fi = 0;
        for (String[] f : fields) {
            VBox fieldBox = new VBox(6);
            Label lbl = new Label(f[1]);
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField(f[0]);
            tf.getStyleClass().add("form-input");
            tfs[fi++] = tf;
            fieldBox.getChildren().addAll(lbl, tf);
            if (f[2].contains("-")) {
                grid.add(fieldBox, 0, Integer.parseInt(f[3]), 2, 1);
            } else {
                grid.add(fieldBox, Integer.parseInt(f[2]), Integer.parseInt(f[3]));
                GridPane.setHgrow(fieldBox, Priority.ALWAYS);
            }
        }

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setMaxHeight(360);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");

        HBox footer = new HBox(10);
        footer.getStyleClass().add("modal-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("btn-outline");
        cancelBtn.setOnAction(e -> closeModal());
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            m.setEmploye(tfs[0].getText().trim());
            m.setEmployeMatricule(tfs[1].getText().trim());
            m.setDepartement(tfs[2].getText().trim());
            m.setVehicule(tfs[3].getText().trim());
            m.setDestination(tfs[4].getText().trim());
            m.setMotif(tfs[5].getText().trim());
            m.setDateDepart(tfs[6].getText().trim());
            m.setDateRetour(tfs[7].getText().trim());
            m.setKmDepart(safeParseInt(tfs[8].getText()));
            m.setChauffeur(tfs[9].getText().trim());
            data.updateMission(m);
            statsRow.getChildren().clear();
            buildStatsCards();
            renderMissions();
            closeModal();
            ToastUtil.show(modalOverlay, "Mission mise à jour");
        });
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, sp, footer);
        modalOverlay.getChildren().add(modal);
    }

    @FXML
    private void closeModal() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    private void showModal() {
        modalOverlay.getChildren().clear();
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);

        VBox modal = new VBox(0);
        modal.getStyleClass().add("modal-card");
        modal.setMaxWidth(560);
        modal.setOnMouseClicked(javafx.event.Event::consume);

        // Header
        HBox header = new HBox(12);
        header.getStyleClass().add("modal-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Nouvel ordre de mission");
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn);

        // Body - 2-column grid
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 20, 16, 20));

        String[][] fields = {
            {"Employé", "Nom et prénom", "0", "0"},
            {"Matricule", "EMP-0000", "1", "0"},
            {"Département", "Département", "0", "1"},
            {"Véhicule", "Choisir un véhicule", "1", "1"},
            {"Destination", "Ville / Lieu", "0-1", "2"},
            {"Motif", "Objet de la mission", "0-1", "3"},
            {"Date départ", "JJ/MM/AAAA", "0", "4"},
            {"Date retour", "JJ/MM/AAAA", "1", "4"},
            {"KM départ", "0", "0", "5"},
            {"Chauffeur", "Nom du chauffeur", "1", "5"},
        };

        TextField[] tfs = new TextField[fields.length];
        int fi = 0;
        for (String[] f : fields) {
            VBox fieldBox = new VBox(6);
            Label lbl = new Label(f[0]);
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField();
            tf.setPromptText(f[1]);
            tf.getStyleClass().add("form-input");
            tfs[fi++] = tf;
            fieldBox.getChildren().addAll(lbl, tf);

            if (f[2].contains("-")) {
                grid.add(fieldBox, 0, Integer.parseInt(f[3]), 2, 1);
            } else {
                grid.add(fieldBox, Integer.parseInt(f[2]), Integer.parseInt(f[3]));
                GridPane.setHgrow(fieldBox, Priority.ALWAYS);
            }
        }

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setMaxHeight(360);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");

        // Footer
        HBox footer = new HBox(10);
        footer.getStyleClass().add("modal-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("btn-outline");
        cancelBtn.setOnAction(e -> closeModal());
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            String id = data.generateMissionId();
            Mission newM = new Mission(id, 0,
                tfs[3].getText().trim(), "—",
                tfs[0].getText().trim(), tfs[1].getText().trim(), tfs[2].getText().trim(),
                tfs[4].getText().trim(), tfs[5].getText().trim(),
                tfs[6].getText().trim(), tfs[7].getText().trim(),
                safeParseInt(tfs[8].getText()), null,
                "Planifiée", id, tfs[9].getText().trim());
            data.addMission(newM);
            statsRow.getChildren().clear();
            buildStatsCards();
            renderMissions();
            closeModal();
            ToastUtil.show(modalOverlay, "Ordre de mission enregistré");
        });
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, sp, footer);
        modalOverlay.getChildren().add(modal);
    }
}
