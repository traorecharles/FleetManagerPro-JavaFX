package com.miage.fleet.controller;

import com.miage.fleet.model.Vehicle;
import com.miage.fleet.service.DatabaseService;
import com.miage.fleet.util.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class VehiculesController {

    @FXML private Label vehicleCountLabel;
    @FXML private TextField searchField;
    @FXML private HBox statusFilters;
    @FXML private HBox deptFilters;
    @FXML private HBox contentRow;
    @FXML private VBox tableContainer;
    @FXML private VBox detailPanel;
    @FXML private Button listViewBtn;
    @FXML private Button gridViewBtn;
    @FXML private StackPane modalOverlay;

    private final DatabaseService data = DatabaseService.getInstance();
    private String activeStatut = "Tous";
    private String activeDept = "Tous";
    private boolean isListView = true;
    private Vehicle selectedVehicle = null;

    private static final String[] STATUTS = {"Tous", "Disponible", "En mission", "Affecté", "En réparation", "Hors service"};
    private static final Map<String, String> STATUT_BADGE = Map.of(
        "Disponible", "badge-disponible",
        "En mission", "badge-en-mission",
        "Affecté", "badge-affecte",
        "En réparation", "badge-en-reparation",
        "Visite technique", "badge-visite-technique",
        "Hors service", "badge-hors-service"
    );

    @FXML
    public void initialize() {
        vehicleCountLabel.setText(data.getVehicules().size() + " véhicules — inventaire du parc");
        buildStatusFilters();
        buildDeptFilters();
        renderList();
    }

    private void buildStatusFilters() {
        statusFilters.getChildren().clear();
        for (String s : STATUTS) {
            Button btn = new Button(s);
            boolean isActive = s.equals(activeStatut);
            btn.getStyleClass().add(isActive ? "filter-btn-active" : "filter-btn");
            btn.setOnAction(e -> {
                activeStatut = s;
                buildStatusFilters();
                applyFilters();
            });
            statusFilters.getChildren().add(btn);
        }
    }

    private void buildDeptFilters() {
        deptFilters.getChildren().clear();
        List<String> depts = new ArrayList<>();
        depts.add("Tous");
        data.getVehicules().stream()
            .map(Vehicle::getDepartement)
            .distinct().sorted()
            .forEach(depts::add);

        for (String d : depts) {
            Button btn = new Button(d);
            boolean isActive = d.equals(activeDept);
            btn.getStyleClass().add(isActive ? "filter-btn-active" : "filter-btn");
            btn.setStyle(isActive ? "-fx-background-color: #374151; -fx-text-fill: white;" : "");
            btn.setOnAction(e -> {
                activeDept = d;
                buildDeptFilters();
                applyFilters();
            });
            deptFilters.getChildren().add(btn);
        }
    }

    @FXML
    private void applyFilters() {
        renderList();
    }

    @FXML
    private void switchToList() {
        isListView = true;
        listViewBtn.getStyleClass().remove("view-btn");
        if (!listViewBtn.getStyleClass().contains("view-btn-active")) listViewBtn.getStyleClass().add("view-btn-active");
        gridViewBtn.getStyleClass().remove("view-btn-active");
        if (!gridViewBtn.getStyleClass().contains("view-btn")) gridViewBtn.getStyleClass().add("view-btn");
        renderList();
    }

    @FXML
    private void switchToGrid() {
        isListView = false;
        gridViewBtn.getStyleClass().remove("view-btn");
        if (!gridViewBtn.getStyleClass().contains("view-btn-active")) gridViewBtn.getStyleClass().add("view-btn-active");
        listViewBtn.getStyleClass().remove("view-btn-active");
        if (!listViewBtn.getStyleClass().contains("view-btn")) listViewBtn.getStyleClass().add("view-btn");
        renderList();
    }

    private List<Vehicle> getFiltered() {
        String search = searchField.getText().toLowerCase();
        return data.getVehicules().stream()
            .filter(v -> {
                boolean matchSearch = search.isEmpty() ||
                    (v.getMarque() + " " + v.getModele() + " " + v.getImmatriculation() + " " + v.getNumeroInventaire())
                        .toLowerCase().contains(search);
                boolean matchStatut = "Tous".equals(activeStatut) || v.getStatut().equals(activeStatut);
                boolean matchDept = "Tous".equals(activeDept) || v.getDepartement().equals(activeDept);
                return matchSearch && matchStatut && matchDept;
            })
            .collect(Collectors.toList());
    }

    private void renderList() {
        tableContainer.getChildren().clear();
        List<Vehicle> filtered = getFiltered();

        if (filtered.isEmpty()) {
            VBox empty = new VBox(8);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            empty.getStyleClass().add("card");
            Label icon = new Label("🚗");
            icon.setStyle("-fx-font-size: 36px; -fx-opacity: 0.2;");
            Label text = new Label("Aucun véhicule trouvé");
            text.setStyle("-fx-text-fill: #94a3b8;");
            empty.getChildren().addAll(icon, text);
            tableContainer.getChildren().add(empty);
            return;
        }

        if (isListView) {
            renderTableView(filtered);
        } else {
            renderGridView(filtered);
        }
    }

    private void renderTableView(List<Vehicle> vehicles) {
        VBox table = new VBox(0);
        table.getStyleClass().add("card");
        table.setStyle("-fx-padding: 0; -fx-background-radius: 16; -fx-border-radius: 16;");

        // Header row
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10 16; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        header.setAlignment(Pos.CENTER_LEFT);
        for (String col : new String[]{"N° Inv.", "Véhicule", "Immatriculation", "Département", "Kilométrage", "Docs", "Statut", ""}) {
            Label lbl = new Label(col);
            lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-weight: bold;");
            setColWidth(lbl, col);
            header.getChildren().add(lbl);
        }
        table.getChildren().add(header);

        // Data rows
        for (Vehicle v : vehicles) {
            HBox row = makeTableRow(v);
            table.getChildren().add(row);
        }

        tableContainer.getChildren().add(table);
    }

    private HBox makeTableRow(Vehicle v) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: " + (v.equals(selectedVehicle) ? "#eff6ff" : "white") +
            "; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1; -fx-cursor: hand;");

        row.setOnMouseEntered(e -> {
            if (!v.equals(selectedVehicle))
                row.setStyle("-fx-background-color: #f8fafc; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1; -fx-cursor: hand;");
        });
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-background-color: " + (v.equals(selectedVehicle) ? "#eff6ff" : "white") +
                "; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1; -fx-cursor: hand;");
        });
        row.setOnMouseClicked(e -> {
            if (v.equals(selectedVehicle)) {
                selectedVehicle = null;
                detailPanel.setVisible(false);
                detailPanel.setManaged(false);
            } else {
                selectedVehicle = v;
                showDetailPanel(v);
            }
            renderList();
        });

        // N° inv
        Label invLbl = new Label(v.getNumeroInventaire());
        invLbl.setStyle("-fx-font-family: 'Courier New', monospace; -fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        invLbl.setPrefWidth(120);

        // Vehicle name + year
        VBox vehicleInfo = new VBox(2);
        vehicleInfo.setPrefWidth(200);
        Label nameLbl = new Label(v.getMarque() + " " + v.getModele());
        nameLbl.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px; -fx-font-weight: 500;");
        Label yearLbl = new Label(v.getAnnee() + " — " + v.getCategorie());
        yearLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        vehicleInfo.getChildren().addAll(nameLbl, yearLbl);

        // Immatriculation
        Label immatLbl = new Label(v.getImmatriculation());
        immatLbl.getStyleClass().add("immat-tag");
        immatLbl.setPrefWidth(150);

        // Département
        Label deptLbl = new Label(v.getDepartement());
        deptLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        deptLbl.setPrefWidth(160);

        // Kilométrage
        Label kmLbl = new Label(String.format(Locale.FRANCE, "%,d km", v.getKilometrage()).replace(",", " "));
        kmLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        kmLbl.setPrefWidth(120);

        // Doc badges
        HBox docBadges = new HBox(4);
        docBadges.setAlignment(Pos.CENTER_LEFT);
        docBadges.setPrefWidth(80);
        docBadges.getChildren().addAll(
            makeDocBadge("Ass.", docUrgency(v.getAssuranceFin())),
            makeDocBadge("VT", docUrgency(v.getVisiteTechniqueEcheance()))
        );

        // Statut badge
        Label badge = new Label(v.getStatut());
        badge.getStyleClass().add(STATUT_BADGE.getOrDefault(v.getStatut(), "badge-hors-service"));
        badge.setPrefWidth(130);

        // Expand button
        Label expandIcon = new Label(v.equals(selectedVehicle) ? "∧" : "∨");
        expandIcon.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");

        row.getChildren().addAll(invLbl, vehicleInfo, immatLbl, deptLbl, kmLbl, docBadges, badge, expandIcon);
        return row;
    }

    private void renderGridView(List<Vehicle> vehicles) {
        FlowPane grid = new FlowPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPrefWrapLength(Double.MAX_VALUE);

        for (Vehicle v : vehicles) {
            VBox card = makeGridCard(v);
            card.setPrefWidth(260);
            grid.getChildren().add(card);
        }

        tableContainer.getChildren().add(grid);
    }

    private VBox makeGridCard(Vehicle v) {
        VBox card = new VBox(0);
        card.getStyleClass().add("affectation-card");
        boolean isSelected = v.equals(selectedVehicle);
        if (isSelected) card.setStyle("-fx-border-color: #3b82f6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 2);");
        card.setCursor(javafx.scene.Cursor.HAND);

        card.setOnMouseClicked(e -> {
            if (v.equals(selectedVehicle)) {
                selectedVehicle = null;
                detailPanel.setVisible(false);
                detailPanel.setManaged(false);
            } else {
                selectedVehicle = v;
                showDetailPanel(v);
            }
            renderList();
        });

        // Image area
        StackPane imgArea = new StackPane();
        imgArea.setMinHeight(140);
        imgArea.setMaxHeight(140);
        imgArea.setStyle("-fx-background-color: #e2e8f0;");

        if (v.getImage() != null && !v.getImage().isEmpty()) {
            imgArea.setStyle(coverStyle(v.getImage(), null));
        } else {
            imgArea.setStyle("-fx-background-color: #e2e8f0;");
            Label carIcon = new Label("🚗");
            carIcon.setStyle("-fx-font-size: 40px; -fx-opacity: 0.3;");
            imgArea.getChildren().add(carIcon);
        }

        // Status badge overlay
        Label statusBadge = new Label(v.getStatut());
        statusBadge.getStyleClass().add(STATUT_BADGE.getOrDefault(v.getStatut(), "badge-hors-service"));
        StackPane.setAlignment(statusBadge, Pos.TOP_LEFT);
        StackPane.setMargin(statusBadge, new Insets(10, 0, 0, 10));
        imgArea.getChildren().add(statusBadge);

        // Card body
        VBox body = new VBox(8);
        body.setPadding(new Insets(14));

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        VBox nameBox = new VBox(2);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label nameLbl = new Label(v.getMarque() + " " + v.getModele());
        nameLbl.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-font-weight: 600;");
        Label yearLbl = new Label(v.getAnnee() + " — " + v.getCategorie());
        yearLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        nameBox.getChildren().addAll(nameLbl, yearLbl);

        Label invTag = new Label(v.getNumeroInventaire());
        invTag.getStyleClass().add("immat-tag");
        titleRow.getChildren().addAll(nameBox, invTag);

        Label immatLbl = new Label(v.getImmatriculation());
        immatLbl.setStyle("-fx-font-family: 'Courier New', monospace; -fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        HBox detailRow = new HBox(12);
        detailRow.setAlignment(Pos.CENTER_LEFT);
        Label kmLbl = new Label("⊙ " + String.format("%,d", v.getKilometrage()).replace(",", " ") + " km");
        kmLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Label fuelLbl = new Label("⛽ " + v.getCarburant());
        fuelLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        detailRow.getChildren().addAll(kmLbl, fuelLbl);

        body.getChildren().addAll(titleRow, immatLbl, detailRow);
        card.getChildren().addAll(imgArea, body);
        return card;
    }

    private void showDetailPanel(Vehicle v) {
        detailPanel.getChildren().clear();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        // Header image area
        StackPane imgHeader = new StackPane();
        imgHeader.setMinHeight(160);
        imgHeader.setMaxHeight(160);
        imgHeader.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 16 16 0 0;");

        if (v.getImage() != null && !v.getImage().isEmpty()) {
            imgHeader.setStyle(coverStyle(v.getImage(), "-fx-background-radius: 16 16 0 0;"));
        } else {
            imgHeader.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 16 16 0 0;");
            Label carIcon = new Label("🚗");
            carIcon.setStyle("-fx-font-size: 56px; -fx-opacity: 0.3;");
            imgHeader.getChildren().add(carIcon);
        }

        // Close button
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 20; -fx-padding: 4 8; -fx-cursor: hand; -fx-text-fill: #374151;");
        closeBtn.setOnAction(e -> {
            selectedVehicle = null;
            detailPanel.setVisible(false);
            detailPanel.setManaged(false);
            renderList();
        });
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(10, 10, 0, 0));

        // Status badge
        Label statusBadge = new Label(v.getStatut());
        statusBadge.getStyleClass().add(STATUT_BADGE.getOrDefault(v.getStatut(), "badge-hors-service"));
        StackPane.setAlignment(statusBadge, Pos.BOTTOM_LEFT);
        StackPane.setMargin(statusBadge, new Insets(0, 0, 10, 12));

        imgHeader.getChildren().addAll(closeBtn, statusBadge);

        // Title section
        VBox titleSection = new VBox(6);
        titleSection.setPadding(new Insets(14, 16, 14, 16));
        titleSection.setStyle("-fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");

        Label titleLbl = new Label(v.getMarque() + " " + v.getModele() + " " + v.getAnnee());
        titleLbl.getStyleClass().add("detail-panel-title");

        HBox tags = new HBox(6);
        tags.setAlignment(Pos.CENTER_LEFT);
        Label immatTag = new Label(v.getImmatriculation());
        immatTag.getStyleClass().add("immat-tag");
        Label invTag = new Label(v.getNumeroInventaire());
        invTag.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-padding: 2 8; -fx-background-radius: 6;");
        tags.getChildren().addAll(immatTag, invTag);

        titleSection.getChildren().addAll(titleLbl, tags);

        // Tabs
        HBox tabsRow = new HBox(0);
        tabsRow.setStyle("-fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");
        String[] tabNames = {"Général", "Documents", "Cycle de vie", "Historique"};
        VBox[] tabContents = {
            buildTabGeneral(v),
            buildTabDocuments(v),
            buildTabCycleVie(v),
            buildTabHistorique()
        };

        StackPane tabContent = new StackPane();
        tabContent.getChildren().add(tabContents[0]);

        Button[] tabBtns = new Button[tabNames.length];
        for (int i = 0; i < tabNames.length; i++) {
            final int idx = i;
            Button tb = new Button(tabNames[i]);
            tb.getStyleClass().add(i == 0 ? "tab-btn-active" : "tab-btn");
            tb.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(tb, Priority.ALWAYS);
            tb.setOnAction(e -> {
                for (Button b : tabBtns) {
                    b.getStyleClass().remove("tab-btn-active");
                    if (!b.getStyleClass().contains("tab-btn")) b.getStyleClass().add("tab-btn");
                }
                tb.getStyleClass().remove("tab-btn");
                if (!tb.getStyleClass().contains("tab-btn-active")) tb.getStyleClass().add("tab-btn-active");
                tabContent.getChildren().setAll(tabContents[idx]);
            });
            tabBtns[i] = tb;
            tabsRow.getChildren().add(tb);
        }

        ScrollPane tabScroll = new ScrollPane(tabContent);
        tabScroll.setFitToWidth(true);
        tabScroll.getStyleClass().add("scroll-pane-white");
        tabScroll.setMaxHeight(420);
        tabScroll.setPrefHeight(420);

        detailPanel.getChildren().addAll(imgHeader, titleSection, tabsRow, tabScroll);
    }

    private VBox buildTabGeneral(Vehicle v) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle("-fx-background-color: white;");
        String km = String.format(Locale.FRANCE, "%,d", v.getKilometrage()).replace(",", " ") + " km";
        String prochainKm = String.format(Locale.FRANCE, "%,d", v.getProchainEntretienKm()).replace(",", " ") + " km";

        addInfoRow(box, "Marque / Modèle", v.getMarque() + " " + v.getModele());
        addInfoRow(box, "Année", String.valueOf(v.getAnnee()));
        addInfoRow(box, "Catégorie", v.getCategorie());
        addInfoRow(box, "Carburant", v.getCarburant());
        addInfoRow(box, "Transmission", v.getTransmission());
        addInfoRow(box, "Couleur", v.getCouleur());
        addInfoRow(box, "Kilométrage actuel", km);
        addInfoRowMono(box, "N° VIN", v.getVin());
        addInfoRow(box, "Département", v.getDepartement());
        addInfoRow(box, "Prochain entretien", prochainKm);

        Button editBtn = new Button("✏ Modifier");
        editBtn.getStyleClass().add("btn-primary");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle("-fx-padding: 8 0;");
        editBtn.setOnAction(e -> openVehicleModal(v));
        VBox.setMargin(editBtn, new Insets(8, 0, 0, 0));
        box.getChildren().add(editBtn);
        return box;
    }

    private VBox buildTabDocuments(Vehicle v) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle("-fx-background-color: white;");

        // Carte grise
        VBox cg = buildDocSection("📄 Carte Grise");
        addInfoRowMono(cg, "N° Carte Grise", v.getCarteGriseNum());
        addInfoRowMono(cg, "Immatriculation", v.getImmatriculation());
        box.getChildren().add(cg);

        // Assurance
        String assuranceUrgency = docUrgency(v.getAssuranceFin());
        VBox ass = buildDocSection("🛡 Assurance (" + urgencyIcon(assuranceUrgency) + ")");
        addInfoRowMono(ass, "N° Police", v.getAssuranceNum());
        addInfoRow(ass, "Compagnie", v.getAssuranceCompagnie());
        addInfoRow(ass, "Début", v.getAssuranceDebut());
        addInfoRowColored(ass, "Échéance", v.getAssuranceFin(), assuranceUrgency);
        box.getChildren().add(ass);

        // Visite technique
        String vtUrgency = docUrgency(v.getVisiteTechniqueEcheance());
        VBox vt = buildDocSection("✓ Visite Technique (" + urgencyIcon(vtUrgency) + ")");
        addInfoRow(vt, "Dernière visite", v.getVisiteTechniqueDate());
        addInfoRowColored(vt, "Prochaine échéance", v.getVisiteTechniqueEcheance(), vtUrgency);
        box.getChildren().add(vt);

        return box;
    }

    private VBox buildTabCycleVie(Vehicle v) {
        VBox box = new VBox(0);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle("-fx-background-color: white;");

        addTimelineItem(box, "📦", "#3b82f6", "Achat", v.getDateAchat(),
            String.format("Prix : %,.0f FCFA — KM : %d", v.getPrixAchat(), v.getKmAchat()), false);
        addTimelineItem(box, "🚗", "#10b981", "Mise en service", v.getDateMiseService(),
            "Département : " + v.getDepartement(), false);
        addTimelineItem(box, "🔧", "#f59e0b", "Entretiens", "Multiple", "Voir onglet Historique", false);

        if (v.getDateHorsService() != null) {
            addTimelineItem(box, "✕", "#ef4444", "Mise hors service", v.getDateHorsService(),
                "Véhicule retiré du parc", true);
        } else {
            String km = String.format(Locale.FRANCE, "%,d", v.getKilometrage()).replace(",", " ") + " km parcourus";
            addTimelineItem(box, "○", "#cbd5e1", "En service", "Actif", km, true);
        }
        return box;
    }

    private VBox buildTabHistorique() {
        VBox box = new VBox(0);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle("-fx-background-color: white;");

        Label title = new Label("Historique des opérations");
        title.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px; -fx-font-weight: 500;");
        VBox.setMargin(title, new Insets(0, 0, 8, 0));
        box.getChildren().add(title);

        String[][] ops = {
            {"07/03/2025", "Vidange + filtres", "40 000 km"},
            {"14/11/2024", "Révision générale", "35 000 km"},
            {"02/07/2024", "Remplacement pneus", "30 000 km"},
        };
        for (String[] op : ops) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.TOP_LEFT);
            row.setPadding(new Insets(10, 0, 10, 0));
            row.setStyle("-fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");

            Label dot = new Label("•");
            dot.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 16px;");

            VBox info = new VBox(2);
            Label opName = new Label(op[1]);
            opName.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px; -fx-font-weight: 500;");
            Label opDetail = new Label(op[0] + " — " + op[2]);
            opDetail.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            info.getChildren().addAll(opName, opDetail);

            row.getChildren().addAll(dot, info);
            box.getChildren().add(row);
        }
        return box;
    }

    // ─── Helpers ─────────────────────────────────────────────

    private VBox buildDocSection(String title) {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-color: white; -fx-background-radius: 12; -fx-padding: 12;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px; -fx-font-weight: 600; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1; -fx-padding: 0 0 8 0;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        section.getChildren().add(lbl);
        return section;
    }

    private void addInfoRow(VBox parent, String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("info-row-label");
        HBox.setHgrow(lbl, Priority.SOMETIMES);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        val.getStyleClass().add("info-row-value");
        row.getChildren().addAll(lbl, spacer, val);
        parent.getChildren().add(row);
    }

    private void addInfoRowMono(VBox parent, String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("info-row-label");
        HBox.setHgrow(lbl, Priority.SOMETIMES);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px; -fx-font-weight: 500; -fx-font-family: 'Courier New', monospace;");
        row.getChildren().addAll(lbl, spacer, val);
        parent.getChildren().add(row);
    }

    private void addInfoRowColored(VBox parent, String label, String value, String urgency) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("info-row-label");
        HBox.setHgrow(lbl, Priority.SOMETIMES);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        String color = switch (urgency) {
            case "ok" -> "#10b981";
            case "soon" -> "#d97706";
            default -> "#dc2626";
        };
        val.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: 500;");
        row.getChildren().addAll(lbl, spacer, val);
        parent.getChildren().add(row);
    }

    private void addTimelineItem(VBox parent, String icon, String color, String label, String date, String detail, boolean last) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);
        row.setPadding(new Insets(0, 0, last ? 0 : 16, 0));

        VBox dotCol = new VBox(0);
        dotCol.setAlignment(Pos.TOP_CENTER);
        StackPane dot = new StackPane();
        dot.setMinSize(28, 28);
        dot.setMaxSize(28, 28);
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 14;");
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        dot.getChildren().add(iconLbl);

        if (!last) {
            Region line = new Region();
            line.setMinWidth(2);
            line.setMaxWidth(2);
            line.setMinHeight(24);
            line.setStyle("-fx-background-color: #e2e8f0;");
            VBox.setVgrow(line, Priority.ALWAYS);
            dotCol.getChildren().addAll(dot, line);
        } else {
            dotCol.getChildren().add(dot);
        }

        VBox info = new VBox(2);
        VBox.setVgrow(info, Priority.ALWAYS);
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-font-weight: 500;");
        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 11px;");
        Label detailLbl = new Label(detail);
        detailLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        info.getChildren().addAll(labelLbl, dateLbl, detailLbl);

        row.getChildren().addAll(dotCol, info);
        parent.getChildren().add(row);
    }

    private Label makeDocBadge(String text, String urgency) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add(switch (urgency) {
            case "ok" -> "doc-badge-ok";
            case "soon" -> "doc-badge-soon";
            default -> "doc-badge-expired";
        });
        return lbl;
    }

    private String docUrgency(String dateStr) {
        if ("—".equals(dateStr)) return "expired";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(dateStr, fmt);
            long diff = ChronoUnit.DAYS.between(LocalDate.now(), date);
            if (diff < 0) return "expired";
            if (diff <= 30) return "soon";
            return "ok";
        } catch (Exception e) {
            return "ok";
        }
    }

    private static int safeParseInt(String s) {
        try { return Integer.parseInt(s.trim().replace(" ", "").replace("km", "").replace("FCFA", "")); }
        catch (Exception e) { return 0; }
    }

    private static double safeParseDouble(String s) {
        try { return Double.parseDouble(s.trim().replace(" ", "").replace("FCFA", "").replace(",", ".")); }
        catch (Exception e) { return 0.0; }
    }

    private String urgencyIcon(String urgency) {
        return switch (urgency) {
            case "ok" -> "✓";
            case "soon" -> "⚠";
            default -> "✕";
        };
    }

    private void setColWidth(Label lbl, String col) {
        switch (col) {
            case "N° Inv." -> lbl.setPrefWidth(120);
            case "Véhicule" -> lbl.setPrefWidth(200);
            case "Immatriculation" -> lbl.setPrefWidth(150);
            case "Département" -> lbl.setPrefWidth(160);
            case "Kilométrage" -> lbl.setPrefWidth(120);
            case "Docs" -> lbl.setPrefWidth(80);
            case "Statut" -> lbl.setPrefWidth(130);
            default -> lbl.setPrefWidth(40);
        }
    }

    @FXML
    private void showNewVehicleForm() {
        openVehicleModal(null);
    }

    @FXML
    private void closeModal() {
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
            modalOverlay.setMouseTransparent(false);
            modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
            modalOverlay.getChildren().clear();
        }
    }

    private void openVehicleModal(Vehicle prefill) {
        boolean isEdit = prefill != null;
        modalOverlay.getChildren().clear();
        modalOverlay.setMouseTransparent(false);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);

        VBox modal = new VBox(0);
        modal.getStyleClass().add("modal-card");
        modal.setMaxWidth(540);
        modal.setOnMouseClicked(javafx.event.Event::consume);

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("modal-header");
        Label titleLbl = new Label(isEdit ? "Modifier — " + prefill.getNumeroInventaire() : "Nouveau véhicule");
        titleLbl.getStyleClass().add("modal-title");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(titleLbl, closeBtn);

        // Form body (2-col grid)
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setMaxHeight(390);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc1.setPercentWidth(50);
        cc2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(cc1, cc2);

        // Field definitions: {label, span, prefill_value_or_empty}
        String[][] fields = {
            {"Marque", "1", isEdit ? prefill.getMarque() : ""},
            {"Modèle", "1", isEdit ? prefill.getModele() : ""},
            {"Année", "1", isEdit ? String.valueOf(prefill.getAnnee()) : ""},
            {"Immatriculation", "1", isEdit ? prefill.getImmatriculation() : ""},
            {"Catégorie", "1", isEdit ? prefill.getCategorie() : ""},
            {"Carburant", "1", isEdit ? prefill.getCarburant() : ""},
            {"Transmission", "1", isEdit ? prefill.getTransmission() : ""},
            {"Couleur", "1", isEdit ? prefill.getCouleur() : ""},
            {"Département", "1", isEdit ? prefill.getDepartement() : ""},
            {"N° VIN", "1", isEdit ? prefill.getVin() : ""},
            {"Prix d'achat (FCFA)", "1", isEdit ? String.valueOf(prefill.getPrixAchat()) : ""},
            {"Date d'achat", "1", isEdit ? prefill.getDateAchat() : ""},
            {"Kilométrage actuel", "1", isEdit ? String.valueOf(prefill.getKilometrage()) : ""},
            {"Statut", "1", isEdit ? prefill.getStatut() : "Disponible"},
        };

        TextField[] tfs = new TextField[fields.length];
        int r = 0, c = 0, fi = 0;
        for (String[] f : fields) {
            int span = Integer.parseInt(f[1]);
            VBox fieldBox = new VBox(5);
            Label lbl = new Label(f[0]);
            lbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
            TextField tf = new TextField(f[2]);
            tf.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 10; -fx-font-size: 13px;");
            tfs[fi++] = tf;
            fieldBox.getChildren().addAll(lbl, tf);
            grid.add(fieldBox, c, r, span, 1);
            if (span == 2 || c == 1) { r++; c = 0; } else { c = 1; }
        }
        sp.setContent(grid);

        // Image picker section
        String[] selectedImagePath = { isEdit ? prefill.getImage() : null };

        VBox imgPickerSection = new VBox(8);
        imgPickerSection.setPadding(new Insets(0, 20, 12, 20));

        StackPane imgPreview = new StackPane();
        imgPreview.setMinHeight(110);
        imgPreview.setMaxHeight(110);
        imgPreview.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1;");

        refreshPreview(imgPreview, selectedImagePath[0]);

        Button pickImgBtn = new Button("📁  Choisir une image");
        pickImgBtn.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-border-color: #bfdbfe; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 14; -fx-font-size: 12px; -fx-cursor: hand;");
        pickImgBtn.setOnAction(ev -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choisir une image pour le véhicule");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
            File file = fc.showOpenDialog(modalOverlay.getScene().getWindow());
            if (file != null) {
                selectedImagePath[0] = file.getAbsolutePath();
                refreshPreview(imgPreview, selectedImagePath[0]);
            }
        });

        imgPickerSection.getChildren().addAll(imgPreview, pickImgBtn);

        // Footer
        HBox btnRow = new HBox(10);
        btnRow.getStyleClass().add("modal-footer");
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Annuler");
        cancel.getStyleClass().add("btn-outline");
        cancel.setOnAction(e -> closeModal());
        Button save = new Button("Enregistrer");
        save.getStyleClass().add("btn-primary");
        save.setOnAction(e -> {
            if (isEdit) {
                prefill.setMarque(tfs[0].getText().trim());
                prefill.setModele(tfs[1].getText().trim());
                prefill.setAnnee(safeParseInt(tfs[2].getText()));
                prefill.setImmatriculation(tfs[3].getText().trim());
                prefill.setCategorie(tfs[4].getText().trim());
                prefill.setCarburant(tfs[5].getText().trim());
                prefill.setTransmission(tfs[6].getText().trim());
                prefill.setCouleur(tfs[7].getText().trim());
                prefill.setDepartement(tfs[8].getText().trim());
                prefill.setVin(tfs[9].getText().trim());
                prefill.setPrixAchat(safeParseDouble(tfs[10].getText()));
                prefill.setDateAchat(tfs[11].getText().trim());
                prefill.setKilometrage(safeParseInt(tfs[12].getText()));
                prefill.setStatut(tfs[13].getText().trim());
                prefill.setImage(selectedImagePath[0]);
                data.updateVehicle(prefill);
            } else {
                int id = data.generateVehicleId();
                String inv = data.generateVehicleNumInv();
                Vehicle nv = new Vehicle(id, inv,
                    tfs[0].getText().trim(), tfs[1].getText().trim(), safeParseInt(tfs[2].getText()),
                    tfs[3].getText().trim(), tfs[9].getText().trim(),
                    tfs[5].getText().trim(), tfs[6].getText().trim(), tfs[7].getText().trim(),
                    safeParseInt(tfs[12].getText()), tfs[13].getText().trim().isEmpty() ? "Disponible" : tfs[13].getText().trim(),
                    tfs[4].getText().trim(), tfs[8].getText().trim(),
                    tfs[11].getText().trim(), safeParseDouble(tfs[10].getText()), 0,
                    tfs[11].getText().trim(), null,
                    "—", "—", "—", "—", "—", "—", "—",
                    safeParseInt(tfs[12].getText()) + 5000, selectedImagePath[0]);
                data.addVehicle(nv);
            }
            vehicleCountLabel.setText(data.getVehicules().size() + " véhicules — inventaire du parc");
            buildDeptFilters();
            renderList();
            closeModal();
            ToastUtil.show(modalOverlay, isEdit ? "Véhicule mis à jour" : "Véhicule ajouté au parc");
        });
        btnRow.getChildren().addAll(cancel, save);

        modal.getChildren().addAll(header, sp, imgPickerSection, btnRow);
        modalOverlay.getChildren().add(modal);
    }

    /** CSS background-size: cover — remplit le cadre en centrant, ratio préservé, excédent rogné. */
    private String coverStyle(String imagePath, String extraCss) {
        try {
            String uri = new File(imagePath).toURI().toString();
            return "-fx-background-image: url('" + uri + "'); " +
                   "-fx-background-size: cover; " +
                   "-fx-background-position: center center; " +
                   "-fx-background-repeat: no-repeat;" +
                   (extraCss != null ? " " + extraCss : "");
        } catch (Exception e) {
            return "-fx-background-color: #e2e8f0;" + (extraCss != null ? " " + extraCss : "");
        }
    }

    /** Prévisualisation dans le modal : image entière visible (contain), centrée. */
    private void refreshPreview(StackPane preview, String imagePath) {
        preview.getChildren().clear();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Image img = new Image(new File(imagePath).toURI().toString(), true);
                ImageView iv = new ImageView(img);
                iv.setPreserveRatio(true);
                iv.setFitWidth(460);
                iv.setFitHeight(106);
                preview.getChildren().add(iv);
                return;
            } catch (Exception ex) { /* fall through to placeholder */ }
        }
        Label ph = new Label("🚗");
        ph.setStyle("-fx-font-size: 32px; -fx-opacity: 0.3;");
        preview.getChildren().add(ph);
    }
}
