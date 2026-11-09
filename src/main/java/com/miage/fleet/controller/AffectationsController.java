package com.miage.fleet.controller;

import com.miage.fleet.model.Affectation;
import com.miage.fleet.service.DatabaseService;
import com.miage.fleet.util.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AffectationsController {

    @FXML private Label activeSectionTitle;
    @FXML private FlowPane activeCardsContainer;
    @FXML private VBox historyTableContainer;
    @FXML private TextField searchField;
    @FXML private StackPane modalOverlay;

    private final DatabaseService data = DatabaseService.getInstance();

    @FXML
    public void initialize() {
        renderActiveCards();
        renderHistoryTable();
    }

    private void renderActiveCards() {
        List<Affectation> actives = data.getAffectations().stream()
            .filter(a -> "Active".equals(a.getStatut()))
            .collect(Collectors.toList());

        activeSectionTitle.setText("Affectations actives (" + actives.size() + ")");
        activeCardsContainer.getChildren().clear();

        for (Affectation a : actives) {
            activeCardsContainer.getChildren().add(makeActiveCard(a));
        }
    }

    private VBox makeActiveCard(Affectation a) {
        VBox card = new VBox(0);
        card.getStyleClass().add("affectation-card");
        card.setPrefWidth(280);
        card.setMaxWidth(320);

        // Vehicle image banner
        StackPane banner = new StackPane();
        banner.setMinHeight(112);
        banner.setMaxHeight(112);
        banner.setStyle("-fx-background-color: linear-gradient(to bottom, #334155, #1e293b);");

        // Car placeholder icon
        Label carIcon = new Label("🚗");
        carIcon.setStyle("-fx-font-size: 40px; -fx-opacity: 0.3;");

        // Gradient overlay
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: linear-gradient(to bottom, transparent, rgba(0,0,0,0.6));");

        // Bottom info on banner
        HBox bannerBottom = new HBox(8);
        bannerBottom.setAlignment(Pos.BOTTOM_LEFT);
        bannerBottom.setPadding(new Insets(0, 10, 10, 10));

        VBox vehicleInfo = new VBox(2);
        HBox.setHgrow(vehicleInfo, Priority.ALWAYS);
        Label vehicleName = new Label(a.getVehicule());
        vehicleName.getStyleClass().add("affectation-vehicle-name");
        Label immatLbl = new Label(a.getImmatriculation());
        immatLbl.getStyleClass().add("affectation-immat");
        vehicleInfo.getChildren().addAll(vehicleName, immatLbl);

        Label activeBadge = new Label("Active");
        activeBadge.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");

        bannerBottom.getChildren().addAll(vehicleInfo, activeBadge);
        StackPane.setAlignment(bannerBottom, Pos.BOTTOM_LEFT);

        banner.getChildren().addAll(carIcon, overlay, bannerBottom);

        // Card body
        VBox body = new VBox(10);
        body.setPadding(new Insets(14));

        // Employee row
        HBox empRow = new HBox(12);
        empRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setMinSize(36, 36);
        avatar.setMaxSize(36, 36);
        avatar.getStyleClass().add("avatar-violet");
        Label avatarText = new Label(String.valueOf(a.getEmploye().charAt(0)));
        avatarText.getStyleClass().add("avatar-text");
        avatar.getChildren().add(avatarText);

        VBox empInfo = new VBox(2);
        HBox.setHgrow(empInfo, Priority.ALWAYS);
        Label empName = new Label(a.getEmploye());
        empName.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-font-weight: 600;");
        Label empRole = new Label(a.getFonction() + " — " + a.getDepartement());
        empRole.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        empInfo.getChildren().addAll(empName, empRole);

        empRow.getChildren().addAll(avatar, empInfo);

        // Details
        VBox details = new VBox(6);
        addDetailRow(details, "Matricule", a.getEmployeMatricule(), true);
        addDetailRow(details, "Depuis", a.getDateDebut(), false);
        addDetailRow(details, "KM parcourus", formatKm(a.getKmParcourus()), false);

        // Motif
        Label motifLbl = new Label(a.getMotif());
        motifLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-style: italic; -fx-wrap-text: true; -fx-border-color: transparent transparent transparent transparent; -fx-padding: 8 0 0 0;");
        motifLbl.setWrapText(true);

        // Action buttons
        HBox actionBtns = new HBox(8);
        VBox.setMargin(actionBtns, new Insets(8, 0, 0, 0));
        Button editBtn = new Button("✏ Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        editBtn.setStyle("-fx-padding: 8 12; -fx-font-size: 12px;");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setOnAction(e -> showEditModal(a));

        Button closeBtn = new Button("✕ Clôturer");
        closeBtn.getStyleClass().add("btn-danger");
        closeBtn.setStyle("-fx-padding: 8 12; -fx-font-size: 12px;");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(closeBtn, Priority.ALWAYS);
        closeBtn.setOnAction(e -> cloturerAffectation(a));

        actionBtns.getChildren().addAll(editBtn, closeBtn);

        body.getChildren().addAll(empRow, details, motifLbl, actionBtns);
        card.getChildren().addAll(banner, body);
        return card;
    }

    private void renderHistoryTable() {
        historyTableContainer.getChildren().clear();
        List<Affectation> filtered = getFiltered();

        VBox table = new VBox(0);
        table.getStyleClass().add("card");
        table.setStyle("-fx-padding: 0; -fx-background-radius: 16; -fx-border-radius: 16;");

        // Header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12 20; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        header.setAlignment(Pos.CENTER_LEFT);
        for (String[] col : new String[][]{{"Réf.", "120"}, {"Employé", "200"}, {"Véhicule", "180"}, {"Période", "200"}, {"KM parcourus", "140"}, {"Statut", "100"}}) {
            Label lbl = new Label(col[0]);
            lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-weight: bold;");
            lbl.setPrefWidth(Double.parseDouble(col[1]));
            header.getChildren().add(lbl);
        }
        table.getChildren().add(header);

        // Rows
        for (Affectation a : filtered) {
            HBox row = makeHistoryRow(a);
            table.getChildren().add(row);
        }

        if (filtered.isEmpty()) {
            Label empty = new Label("Aucune affectation trouvée");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 32; -fx-font-size: 13px;");
            table.getChildren().add(empty);
        }

        historyTableContainer.getChildren().add(table);
    }

    private HBox makeHistoryRow(Affectation a) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 20, 14, 20));
        row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f8fafc; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;"));

        // Ref
        Label refLbl = new Label(a.getId());
        refLbl.setStyle("-fx-font-family: 'Courier New', monospace; -fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        refLbl.setPrefWidth(120);

        // Employee
        HBox empBox = new HBox(8);
        empBox.setAlignment(Pos.CENTER_LEFT);
        empBox.setPrefWidth(200);
        StackPane avatar = new StackPane();
        avatar.setMinSize(28, 28);
        avatar.setMaxSize(28, 28);
        avatar.setStyle("-fx-min-width: 28; -fx-min-height: 28; -fx-max-width: 28; -fx-max-height: 28; -fx-background-radius: 14; -fx-background-color: linear-gradient(to bottom right, #a78bfa, #6366f1);");
        Label avatarText = new Label(String.valueOf(a.getEmploye().charAt(0)));
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        avatar.getChildren().add(avatarText);
        VBox empInfo = new VBox(1);
        Label empName = new Label(a.getEmploye());
        empName.setStyle("-fx-text-fill: #374151; -fx-font-size: 13px; -fx-font-weight: 500;");
        Label empDept = new Label(a.getDepartement());
        empDept.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        empInfo.getChildren().addAll(empName, empDept);
        empBox.getChildren().addAll(avatar, empInfo);

        // Vehicle
        VBox vehicleBox = new VBox(2);
        vehicleBox.setPrefWidth(180);
        Label vehicleName = new Label(a.getVehicule());
        vehicleName.setStyle("-fx-text-fill: #374151; -fx-font-size: 13px;");
        Label immatLbl = new Label(a.getImmatriculation());
        immatLbl.setStyle("-fx-font-family: 'Courier New', monospace; -fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        vehicleBox.getChildren().addAll(vehicleName, immatLbl);

        // Period
        String period = a.getDateDebut() + " → " + (a.getDateFin() != null ? a.getDateFin() : "En cours");
        Label periodLbl = new Label(period);
        periodLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        periodLbl.setPrefWidth(200);

        // KM
        Label kmLbl = new Label(formatKm(a.getKmParcourus()));
        kmLbl.setStyle("-fx-text-fill: #374151; -fx-font-size: 13px; -fx-font-weight: 500;");
        kmLbl.setPrefWidth(140);

        // Status
        Label badge = new Label(a.getStatut());
        badge.getStyleClass().add("Active".equals(a.getStatut()) ? "badge-active" : "badge-terminee");

        row.getChildren().addAll(refLbl, empBox, vehicleBox, periodLbl, kmLbl, badge);
        return row;
    }

    private List<Affectation> getFiltered() {
        String search = searchField != null ? searchField.getText().toLowerCase() : "";
        return data.getAffectations().stream()
            .filter(a -> search.isEmpty() ||
                (a.getEmploye() + " " + a.getVehicule() + " " + a.getDepartement())
                    .toLowerCase().contains(search))
            .collect(Collectors.toList());
    }

    @FXML
    private void applyFilters() {
        renderHistoryTable();
    }

    private void addDetailRow(VBox parent, String label, String value, boolean mono) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        HBox.setHgrow(lbl, Priority.SOMETIMES);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        if (mono) {
            val.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px; -fx-font-weight: 500; -fx-font-family: monospace;");
        } else {
            val.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px; -fx-font-weight: 500;");
        }
        row.getChildren().addAll(lbl, spacer, val);
        parent.getChildren().add(row);
    }

    private String formatKm(int km) {
        return String.format(Locale.FRANCE, "%,d", km).replace(",", " ") + " km";
    }

    @FXML
    private void showNewAffectationForm() {
        showModal();
    }

    @FXML
    private void closeModal() {
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
        }
    }

    private void showModal() {
        modalOverlay.getChildren().clear();
        modalOverlay.setMouseTransparent(false);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);

        VBox modal = new VBox(0);
        modal.getStyleClass().add("modal-card");
        modal.setMaxWidth(480);
        modal.setOnMouseClicked(javafx.event.Event::consume);

        // Header
        HBox header = new HBox(12);
        header.getStyleClass().add("modal-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Nouvelle affectation");
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn2 = new Button("✕");
        closeBtn2.getStyleClass().add("modal-close-btn");
        closeBtn2.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn2);

        // Body
        VBox body = new VBox(12);
        body.setPadding(new Insets(16, 20, 16, 20));

        String[][] fields = {
            {"Employé", "Nom et prénom de l'employé"},
            {"Matricule", "EMP-0000"},
            {"Fonction", "Titre / Poste"},
            {"Département", "Département"},
            {"Véhicule à affecter", "Choisir un véhicule disponible"},
            {"Motif d'affectation", "Raison de l'affectation"},
            {"Date de début", "JJ/MM/AAAA"},
        };

        TextField[] tfs = new TextField[fields.length];
        int fi = 0;
        for (String[] f : fields) {
            VBox fieldBox = new VBox(5);
            Label lbl = new Label(f[0]);
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField();
            tf.setPromptText(f[1]);
            tf.getStyleClass().add("form-input");
            tfs[fi++] = tf;
            fieldBox.getChildren().addAll(lbl, tf);
            body.getChildren().add(fieldBox);
        }

        ScrollPane sp = new ScrollPane(body);
        sp.setFitToWidth(true);
        sp.setMaxHeight(370);
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
            String id = data.generateAffectationId();
            Affectation aff = new Affectation(id, 0,
                tfs[4].getText().trim(), "—",
                tfs[0].getText().trim(), tfs[1].getText().trim(), tfs[2].getText().trim(),
                tfs[3].getText().trim(), tfs[6].getText().trim(), null,
                0, 0, "Active", tfs[5].getText().trim(), null);
            data.addAffectation(aff);
            renderActiveCards();
            renderHistoryTable();
            closeModal();
            ToastUtil.show(modalOverlay, "Affectation enregistrée avec succès");
        });
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, sp, footer);
        modalOverlay.getChildren().add(modal);
    }

    private void showEditModal(Affectation a) {
        modalOverlay.getChildren().clear();
        modalOverlay.setMouseTransparent(false);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);

        VBox modal = new VBox(0);
        modal.getStyleClass().add("modal-card");
        modal.setMaxWidth(480);
        modal.setOnMouseClicked(javafx.event.Event::consume);

        HBox header = new HBox(12);
        header.getStyleClass().add("modal-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Modifier l'affectation — " + a.getId());
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn);

        VBox body = new VBox(12);
        body.setPadding(new Insets(16, 20, 16, 20));

        String[][] prefilled = {
            {"Employé", a.getEmploye()},
            {"Matricule", a.getEmployeMatricule()},
            {"Fonction", a.getFonction()},
            {"Département", a.getDepartement()},
            {"Véhicule affecté", a.getVehicule()},
            {"Motif d'affectation", a.getMotif()},
            {"Date de début", a.getDateDebut()},
        };

        TextField[] tfs = new TextField[prefilled.length];
        int fi = 0;
        for (String[] f : prefilled) {
            VBox fieldBox = new VBox(5);
            Label lbl = new Label(f[0]);
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField(f[1]);
            tf.getStyleClass().add("form-input");
            tfs[fi++] = tf;
            fieldBox.getChildren().addAll(lbl, tf);
            body.getChildren().add(fieldBox);
        }

        ScrollPane sp = new ScrollPane(body);
        sp.setFitToWidth(true);
        sp.setMaxHeight(370);
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
            a.setEmploye(tfs[0].getText().trim());
            a.setEmployeMatricule(tfs[1].getText().trim());
            a.setFonction(tfs[2].getText().trim());
            a.setDepartement(tfs[3].getText().trim());
            a.setVehicule(tfs[4].getText().trim());
            a.setMotif(tfs[5].getText().trim());
            a.setDateDebut(tfs[6].getText().trim());
            data.updateAffectation(a);
            renderActiveCards();
            renderHistoryTable();
            closeModal();
            ToastUtil.show(modalOverlay, "Affectation mise à jour");
        });
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, sp, footer);
        modalOverlay.getChildren().add(modal);
    }

    private void cloturerAffectation(Affectation a) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clôturer l'affectation");
        confirm.setHeaderText("Clôturer " + a.getId() + " — " + a.getEmploye());
        confirm.setContentText("Cette affectation sera marquée comme terminée. Confirmer ?");
        confirm.getButtonTypes().setAll(
            new ButtonType("Clôturer", ButtonBar.ButtonData.OK_DONE),
            new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE)
        );
        confirm.showAndWait().ifPresent(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                a.setStatut("Terminée");
                String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                a.setDateFin(today);
                renderActiveCards();
                renderHistoryTable();
                ToastUtil.show(modalOverlay, "Affectation clôturée");
            }
        });
    }
}
