package com.miage.fleet.controller;

import com.miage.fleet.model.Entretien;
import com.miage.fleet.service.DatabaseService;
import com.miage.fleet.util.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class EntretienController {

    @FXML private HBox statsRow;
    @FXML private HBox statutFilters;
    @FXML private HBox natureFilters;
    @FXML private TextField searchField;
    @FXML private VBox cardsContainer;
    @FXML private VBox emptyState;
    @FXML private StackPane modalOverlay;

    private String activeStatut = "Tous";
    private String activeNature = "Tous";
    private String expandedId = null;

    private final DatabaseService data = DatabaseService.getInstance();

    @FXML
    public void initialize() {
        buildStats();
        buildFilters();
        renderCards();
    }

    private void buildStats() {
        List<Entretien> entretiens = data.getEntretiens();
        long enCours = entretiens.stream().filter(e -> "En cours".equals(e.getStatut())).count();
        long planifies = entretiens.stream().filter(e -> "Planifié".equals(e.getStatut())).count();
        int totalCout = entretiens.stream().filter(e -> e.getCoutReel() > 0).mapToInt(Entretien::getCoutReel).sum();

        statsRow.getChildren().addAll(
            makeStatCard(String.valueOf(entretiens.size()), "Total opérations", "#0f172a", "white", "#e2e8f0"),
            makeStatCard(String.valueOf(enCours), "En cours", "#c2410c", "#fff7ed", "#fed7aa"),
            makeStatCard(String.valueOf(planifies), "Planifiés", "#1d4ed8", "#eff6ff", "#bfdbfe"),
            makeStatCard(String.format("%,d FCFA", totalCout).replace(",", " "), "Coût réel cumulé", "#0f172a", "white", "#e2e8f0")
        );
        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
    }

    private VBox makeStatCard(String value, String label, String valueColor, String bg, String border) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 12; -fx-border-color: " + border + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 16;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + valueColor + "; -fx-font-size: 20px; -fx-font-weight: 700;");
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private void buildFilters() {
        for (String s : new String[]{"Tous", "En cours", "Planifié", "Terminé"}) {
            Button btn = new Button(s);
            btn.getStyleClass().add("Tous".equals(s) ? "filter-btn-active" : "filter-btn");
            btn.setOnAction(e -> {
                activeStatut = s;
                statutFilters.getChildren().forEach(n -> n.getStyleClass().setAll("filter-btn"));
                btn.getStyleClass().setAll("filter-btn-active");
                renderCards();
            });
            statutFilters.getChildren().add(btn);
        }
        for (String n : new String[]{"Tous", "Préventif", "Curatif"}) {
            Button btn = new Button(n);
            btn.getStyleClass().add("Tous".equals(n) ? "filter-btn-active" : "filter-btn");
            btn.setOnAction(e -> {
                activeNature = n;
                natureFilters.getChildren().forEach(node -> node.getStyleClass().setAll("filter-btn"));
                btn.getStyleClass().setAll("filter-btn-active");
                renderCards();
            });
            natureFilters.getChildren().add(btn);
        }
    }

    @FXML
    private void applyFilters() { renderCards(); }

    private void renderCards() {
        cardsContainer.getChildren().clear();
        String search = searchField.getText().toLowerCase();

        List<Entretien> filtered = data.getEntretiens().stream().filter(e -> {
            boolean matchSearch = (e.getVehicule() + " " + e.getImmatriculation() + " " + e.getType()).toLowerCase().contains(search);
            boolean matchStatut = "Tous".equals(activeStatut) || activeStatut.equals(e.getStatut());
            boolean matchNature = "Tous".equals(activeNature) || activeNature.equals(e.getNature());
            return matchSearch && matchStatut && matchNature;
        }).toList();

        emptyState.setVisible(filtered.isEmpty());
        emptyState.setManaged(filtered.isEmpty());

        for (Entretien e : filtered) {
            cardsContainer.getChildren().add(makeCard(e));
        }
    }

    private VBox makeCard(Entretien e) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1;");
        card.getChildren().add(makeCardHeader(e, card));
        return card;
    }

    private HBox makeCardHeader(Entretien e, VBox card) {
        HBox header = new HBox(14);
        header.setAlignment(Pos.TOP_LEFT);
        header.setPadding(new Insets(16));

        StackPane iconBox = new StackPane();
        iconBox.setMinSize(40, 40);
        iconBox.setMaxSize(40, 40);
        String iconBg = switch (e.getStatut()) {
            case "Terminé" -> "#d1fae5";
            case "En cours" -> "#ffedd5";
            default -> "#dbeafe";
        };
        iconBox.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 10;");
        Label iconLbl = new Label("🔧");
        iconLbl.setStyle("-fx-font-size: 16px;");
        iconBox.getChildren().add(iconLbl);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox badgesRow = new HBox(6);
        badgesRow.setAlignment(Pos.CENTER_LEFT);
        Label idLbl = new Label(e.getId());
        idLbl.setStyle("-fx-font-family: monospace; -fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-background-color: #f1f5f9; -fx-background-radius: 4; -fx-padding: 1 6;");
        Label natureLbl = new Label(e.getNature());
        natureLbl.setStyle("Préventif".equals(e.getNature())
            ? "-fx-text-fill: #2563eb; -fx-background-color: #eff6ff; -fx-background-radius: 20; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: 600;"
            : "-fx-text-fill: #dc2626; -fx-background-color: #fef2f2; -fx-background-radius: 20; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: 600;");
        Label prioriteLbl = new Label(e.getPriorite());
        prioriteLbl.setStyle(switch (e.getPriorite()) {
            case "Haute" -> "-fx-text-fill: #b91c1c; -fx-background-color: #fee2e2; -fx-background-radius: 20; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: 600;";
            case "Basse" -> "-fx-text-fill: #166534; -fx-background-color: #dcfce7; -fx-background-radius: 20; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: 600;";
            default -> "-fx-text-fill: #475569; -fx-background-color: #f1f5f9; -fx-background-radius: 20; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: 600;";
        });
        badgesRow.getChildren().addAll(idLbl, natureLbl, prioriteLbl);

        HBox vehicleRow = new HBox(8);
        vehicleRow.setAlignment(Pos.CENTER_LEFT);
        Label vehicleLbl = new Label(e.getVehicule());
        vehicleLbl.setStyle("-fx-font-weight: 600; -fx-text-fill: #0f172a; -fx-font-size: 14px;");
        Label immatLbl = new Label(e.getImmatriculation());
        immatLbl.setStyle("-fx-font-family: monospace; -fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-background-color: #f1f5f9; -fx-background-radius: 4; -fx-padding: 1 6;");
        Label dotLbl = new Label("·");
        dotLbl.setStyle("-fx-text-fill: #cbd5e1;");
        Label invLbl = new Label(e.getNumeroInv());
        invLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        vehicleRow.getChildren().addAll(vehicleLbl, immatLbl, dotLbl, invLbl);

        Label typeLbl = new Label(e.getType());
        typeLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");

        Label descLbl = new Label(e.getDescription());
        descLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        descLbl.setMaxWidth(500);

        HBox detailRow = new HBox(16);
        detailRow.setAlignment(Pos.CENTER_LEFT);
        int cout = e.getCoutReel() > 0 ? e.getCoutReel() : e.getCoutEstime();
        String coutSuffix = e.getCoutReel() > 0 ? "" : " (estimé)";
        for (String txt : new String[]{
            "📅 " + e.getDateEntree() + " → " + e.getDateSortiePrevue(),
            "💰 " + String.format("%,d", cout).replace(",", " ") + " FCFA" + coutSuffix,
            "🏭 " + e.getGarage()
        }) {
            Label d = new Label(txt);
            d.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            detailRow.getChildren().add(d);
        }

        info.getChildren().addAll(badgesRow, vehicleRow, typeLbl, descLbl, detailRow);

        VBox right = new VBox(8);
        right.setAlignment(Pos.TOP_RIGHT);
        Label statutLbl = new Label(e.getStatut());
        statutLbl.setStyle(switch (e.getStatut()) {
            case "Terminé" -> "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-background-radius: 20; -fx-padding: 4 10; -fx-font-size: 11px; -fx-font-weight: 600;";
            case "En cours" -> "-fx-background-color: #ffedd5; -fx-text-fill: #9a3412; -fx-background-radius: 20; -fx-padding: 4 10; -fx-font-size: 11px; -fx-font-weight: 600;";
            default -> "-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-background-radius: 20; -fx-padding: 4 10; -fx-font-size: 11px; -fx-font-weight: 600;";
        });

        Button expandBtn = new Button("∨");
        expandBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 4 8;");
        expandBtn.setOnAction(ev -> toggleExpand(e, card, expandBtn));
        right.getChildren().addAll(statutLbl, expandBtn);

        header.getChildren().addAll(iconBox, info, right);
        return header;
    }

    private void toggleExpand(Entretien e, VBox card, Button btn) {
        boolean wasExpanded = e.getId().equals(expandedId);
        expandedId = wasExpanded ? null : e.getId();
        btn.setText(wasExpanded ? "∨" : "∧");

        if (card.getChildren().size() > 1) {
            card.getChildren().remove(1);
        }
        if (!wasExpanded) {
            card.getChildren().add(makeExpandedSection(e));
        }
    }

    private VBox makeExpandedSection(Entretien e) {
        VBox section = new VBox(0);
        section.setStyle("-fx-border-color: #f1f5f9 transparent transparent transparent; -fx-border-width: 1; -fx-padding: 16;");

        HBox cols = new HBox(24);

        VBox col1 = new VBox(8);
        HBox.setHgrow(col1, Priority.ALWAYS);
        Label title1 = new Label("ATELIER & FACTURATION");
        title1.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 10px;");
        col1.getChildren().add(title1);
        for (String[] row : new String[][]{
            {"Garage", e.getGarage()}, {"Adresse", e.getAdresseGarage()}, {"Technicien", e.getTechnicien()},
            {"Bon de commande", e.getBonCommande()},
            {"KM entrée", String.format("%,d km", e.getKmEntree()).replace(",", " ")},
            {"Coût estimé", String.format("%,d FCFA", e.getCoutEstime()).replace(",", " ")},
            {"Coût réel", e.getCoutReel() > 0 ? String.format("%,d FCFA", e.getCoutReel()).replace(",", " ") : "En attente"}
        }) {
            col1.getChildren().add(makeInfoRow(row[0], row[1]));
        }

        VBox col2 = new VBox(8);
        HBox.setHgrow(col2, Priority.ALWAYS);
        Label title2 = new Label("PIÈCES DE RECHANGE");
        title2.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 10px;");
        col2.getChildren().add(title2);
        for (String piece : e.getPieces()) {
            HBox pieceRow = new HBox(8);
            pieceRow.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 7px;");
            Label pieceLbl = new Label(piece);
            pieceLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
            pieceLbl.setWrapText(true);
            pieceRow.getChildren().addAll(dot, pieceLbl);
            col2.getChildren().add(pieceRow);
        }

        VBox col3 = new VBox(8);
        HBox.setHgrow(col3, Priority.ALWAYS);
        Label title3 = new Label("DESCRIPTION");
        title3.setStyle("-fx-font-weight: 700; -fx-text-fill: #475569; -fx-font-size: 10px;");
        Label descLbl = new Label(e.getDescription());
        descLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        descLbl.setWrapText(true);
        Button editBtn = new Button("✎  Modifier");
        editBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-size: 12px; -fx-cursor: hand;");
        editBtn.setOnAction(ev -> showEditForm(e));
        col3.getChildren().addAll(title3, descLbl, editBtn);

        cols.getChildren().addAll(col1, col2, col3);
        section.getChildren().add(cols);
        return section;
    }

    private HBox makeInfoRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        lbl.setMinWidth(90);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value != null ? value : "—");
        val.setStyle("-fx-text-fill: #334155; -fx-font-size: 11px; -fx-font-weight: 600;");
        val.setMaxWidth(160);
        val.setWrapText(true);
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    private void showEditForm(Entretien e) {
        if (modalOverlay == null) return;
        modalOverlay.setMouseTransparent(false);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.getChildren().setAll(buildModal(e));
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    @FXML
    private void showNewEntretienForm() {
        if (modalOverlay == null) return;
        modalOverlay.setMouseTransparent(false);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.getChildren().setAll(buildModal(null));
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    @FXML
    private void closeModal() {
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
        }
    }

    private VBox buildModal(Entretien prefill) {
        boolean isEdit = prefill != null;
        VBox modal = new VBox(0);
        modal.getStyleClass().add("modal-card");
        modal.setMaxWidth(540);
        modal.setOnMouseClicked(e -> e.consume());

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getStyleClass().add("modal-header");
        Label titleLbl = new Label(isEdit ? "Modifier — " + prefill.getId() : "Nouvel entretien");
        titleLbl.getStyleClass().add("modal-title");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        titleRow.getChildren().addAll(titleLbl, closeBtn);

        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setMaxHeight(380);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints());
        grid.getColumnConstraints().get(0).setPercentWidth(50);
        grid.getColumnConstraints().get(1).setPercentWidth(50);

        // order: 0=Véhicule(span2), 1=Type, 2=Nature, 3=Priorité, 4=Garage, 5=Technicien,
        //        6=BonCommande, 7=DateEntrée, 8=DateSortiePrévue, 9=KMEntrée, 10=CoutEstimé, 11=Description(span2)
        String[] prefillVals = isEdit ? new String[]{
            prefill.getVehicule() + (prefill.getImmatriculation() != null && !prefill.getImmatriculation().isBlank() ? " — " + prefill.getImmatriculation() : ""),
            prefill.getType(), prefill.getNature(), prefill.getPriorite(),
            prefill.getGarage(), prefill.getTechnicien(), prefill.getBonCommande(),
            prefill.getDateEntree(), prefill.getDateSortiePrevue(),
            String.valueOf(prefill.getKmEntree()), String.valueOf(prefill.getCoutEstime()),
            prefill.getDescription()
        } : new String[12];

        String[][] fields = {
            {"Véhicule (Marque Modèle — Immat.)", "2"},
            {"Type d'entretien", "1"}, {"Nature", "1"},
            {"Priorité", "1"}, {"Garage / Atelier", "1"},
            {"Technicien", "1"}, {"Bon de commande", "1"},
            {"Date entrée (JJ/MM/AAAA)", "1"}, {"Date sortie prévue", "1"},
            {"KM entrée", "1"}, {"Coût estimé (FCFA)", "1"},
            {"Description", "2"}
        };

        TextField[] tfs = new TextField[fields.length];
        int r = 0, c = 0;
        for (int fi = 0; fi < fields.length; fi++) {
            String[] f = fields[fi];
            int span = Integer.parseInt(f[1]);
            VBox fieldBox = new VBox(5);
            Label lbl = new Label(f[0]);
            lbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
            String val = (prefillVals[fi] != null) ? prefillVals[fi] : "";
            TextField tf = new TextField(val);
            tf.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 10; -fx-font-size: 13px;");
            tfs[fi] = tf;
            fieldBox.getChildren().addAll(lbl, tf);
            grid.add(fieldBox, c, r, span, 1);
            if (span == 2 || c == 1) { r++; c = 0; } else { c = 1; }
        }
        sp.setContent(grid);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.getStyleClass().add("modal-footer");
        Button cancel = new Button("Annuler");
        cancel.getStyleClass().add("btn-outline");
        cancel.setOnAction(e -> closeModal());
        Button save = new Button("Enregistrer");
        save.getStyleClass().add("btn-primary");
        save.setOnAction(ev -> {
            // Parse véhicule field: "Toyota Hilux — BY-5678-CI"
            String vehiculeStr = tfs[0].getText().trim();
            String vehicule = vehiculeStr.contains("—") ? vehiculeStr.split("—")[0].trim() : vehiculeStr;
            String immat = vehiculeStr.contains("—") ? vehiculeStr.split("—")[1].trim() : "";

            if (isEdit) {
                prefill.setVehicule(vehicule);
                prefill.setImmatriculation(immat);
                prefill.setType(tfs[1].getText().trim());
                prefill.setNature(tfs[2].getText().trim());
                prefill.setPriorite(tfs[3].getText().trim());
                prefill.setGarage(tfs[4].getText().trim());
                prefill.setTechnicien(tfs[5].getText().trim());
                prefill.setBonCommande(tfs[6].getText().trim());
                prefill.setDateEntree(tfs[7].getText().trim());
                prefill.setDateSortiePrevue(tfs[8].getText().trim());
                prefill.setKmEntree(safeParseInt(tfs[9].getText()));
                prefill.setCoutEstime(safeParseInt(tfs[10].getText()));
                prefill.setDescription(tfs[11].getText().trim());
                data.updateEntretien(prefill);
            } else {
                String id = data.generateEntretienId();
                Entretien e = new Entretien(id, vehicule, immat, "—",
                    tfs[1].getText().trim(), tfs[2].getText().trim(),
                    tfs[11].getText().trim(), tfs[4].getText().trim(), "",
                    tfs[5].getText().trim(), tfs[7].getText().trim(),
                    tfs[8].getText().trim(), null,
                    safeParseInt(tfs[10].getText()), -1, "Planifié",
                    tfs[3].getText().trim(), safeParseInt(tfs[9].getText()),
                    new ArrayList<>(), tfs[6].getText().trim());
                data.addEntretien(e);
            }
            statsRow.getChildren().clear();
            buildStats();
            expandedId = null;
            renderCards();
            closeModal();
            ToastUtil.show(modalOverlay, isEdit ? "Entretien mis à jour" : "Entretien enregistré");
        });
        btnRow.getChildren().addAll(cancel, save);

        modal.getChildren().addAll(titleRow, sp, btnRow);
        return modal;
    }

    private static int safeParseInt(String s) {
        try { return Integer.parseInt(s.trim().replace(" ", "").replace("FCFA", "").replace("km", "")); }
        catch (Exception e) { return 0; }
    }
}
