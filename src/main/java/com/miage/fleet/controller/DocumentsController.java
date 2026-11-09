package com.miage.fleet.controller;

import com.miage.fleet.model.Vehicle;
import com.miage.fleet.service.DatabaseService;
import com.miage.fleet.util.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DocumentsController {

    @FXML private VBox alertBanner;
    @FXML private HBox summaryRow;
    @FXML private HBox typeFilters;
    @FXML private TextField searchField;
    @FXML private VBox docTableContainer;
    @FXML private FlowPane vehicleCardsContainer;
    @FXML private StackPane modalOverlay;

    private String activeType = "tous";
    private final DatabaseService data = DatabaseService.getInstance();
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    record DocEntry(String vehiculeId, String vehicule, String immatriculation,
                    String departement, String type, String compagnie,
                    String debut, String fin, String urgency, long diffDays) {}

    private List<DocEntry> allDocs = new ArrayList<>();

    @FXML
    public void initialize() {
        buildDocData();
        buildAlertBanner();
        buildSummaryCards();
        buildTypeFilters();
        buildDocTable();
        buildVehicleCards();
    }

    private void buildDocData() {
        allDocs.clear();
        for (Vehicle v : data.getVehicules()) {
            if ("Hors service".equals(v.getStatut())) continue;
            allDocs.add(new DocEntry(v.getNumeroInventaire(), v.getNomComplet(),
                v.getImmatriculation(), v.getDepartement(), "assurance",
                v.getAssuranceCompagnie(), v.getAssuranceDebut(), v.getAssuranceFin(),
                urgency(v.getAssuranceFin()), diffDays(v.getAssuranceFin())));
            allDocs.add(new DocEntry(v.getNumeroInventaire(), v.getNomComplet(),
                v.getImmatriculation(), v.getDepartement(), "vt",
                "Centre de contrôle technique", v.getVisiteTechniqueDate(), v.getVisiteTechniqueEcheance(),
                urgency(v.getVisiteTechniqueEcheance()), diffDays(v.getVisiteTechniqueEcheance())));
        }
    }

    private void buildAlertBanner() {
        List<DocEntry> alerts = allDocs.stream().filter(d -> !"ok".equals(d.urgency()))
            .sorted((a, b) -> Long.compare(a.diffDays(), b.diffDays())).toList();
        if (alerts.isEmpty()) return;

        alertBanner.setVisible(true);
        alertBanner.setManaged(true);
        alertBanner.getChildren().clear();

        HBox top = new HBox(10);
        top.setAlignment(Pos.TOP_LEFT);
        Label icon = new Label("⚠");
        icon.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 16px;");
        VBox textBox = new VBox(6);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label title = new Label(alerts.size() + " document" + (alerts.size() > 1 ? "s" : "") + " nécessitent une action immédiate");
        title.setStyle("-fx-text-fill: #b91c1c; -fx-font-weight: 700; -fx-font-size: 13px;");

        HBox chips = new HBox(8);
        chips.setMaxWidth(680);
        for (DocEntry a : alerts.subList(0, Math.min(4, alerts.size()))) {
            String label = "assurance".equals(a.type()) ? "Assurance" : "VT";
            String info = a.diffDays() < 0
                ? "expiré il y a " + Math.abs(a.diffDays()) + "j"
                : "dans " + a.diffDays() + "j";
            Label chip = new Label(a.vehicule() + " — " + label + " (" + info + ")");
            chip.setStyle("-fx-background-color: white; -fx-border-color: #fca5a5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #dc2626; -fx-font-size: 11px; -fx-padding: 3 10;");
            chips.getChildren().add(chip);
        }
        textBox.getChildren().addAll(title, chips);
        top.getChildren().addAll(icon, textBox);
        alertBanner.getChildren().add(top);
    }

    private void buildSummaryCards() {
        long expired = allDocs.stream().filter(d -> "expired".equals(d.urgency())).count();
        long soon = allDocs.stream().filter(d -> "soon".equals(d.urgency())).count();
        long ok = allDocs.stream().filter(d -> "ok".equals(d.urgency())).count();

        summaryRow.getChildren().addAll(
            makeSummaryCard(String.valueOf(expired), "Expirés", "#dc2626", "#fef2f2", "#fecaca"),
            makeSummaryCard(String.valueOf(soon), "Expire ≤ 30 jours", "#d97706", "#fffbeb", "#fde68a"),
            makeSummaryCard(String.valueOf(ok), "Valides", "#059669", "#ecfdf5", "#a7f3d0")
        );
        summaryRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
    }

    private VBox makeSummaryCard(String val, String label, String color, String bg, String border) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 12; -fx-border-color: " + border + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 16;");
        Label valLbl = new Label(val);
        valLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 28px; -fx-font-weight: 700;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        card.getChildren().addAll(valLbl, lbl);
        return card;
    }

    private void buildTypeFilters() {
        String[][] types = {{"tous", "Tous les documents"}, {"assurance", "Assurances"}, {"vt", "Visites techniques"}};
        for (String[] t : types) {
            Button btn = new Button(t[1]);
            btn.getStyleClass().add("tous".equals(t[0]) ? "filter-btn-active" : "filter-btn");
            String key = t[0];
            btn.setOnAction(e -> {
                activeType = key;
                typeFilters.getChildren().forEach(n -> n.getStyleClass().setAll("filter-btn"));
                btn.getStyleClass().setAll("filter-btn-active");
                buildDocTable();
            });
            typeFilters.getChildren().add(btn);
        }
    }

    @FXML private void applyFilters() { buildDocTable(); }

    private void buildDocTable() {
        docTableContainer.getChildren().clear();
        String search = searchField != null ? searchField.getText().toLowerCase() : "";

        List<DocEntry> filtered = allDocs.stream().filter(d -> {
            boolean matchSearch = (d.vehicule() + " " + d.immatriculation()).toLowerCase().contains(search);
            boolean matchType = "tous".equals(activeType) || activeType.equals(d.type());
            return matchSearch && matchType;
        }).toList();

        // Header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10 16; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        for (String[] col : new String[][]{{"Type", "110"}, {"Véhicule", "180"}, {"Immatriculation", "140"}, {"Organisme", "180"}, {"Début validité", "110"}, {"Échéance", "140"}, {"État", "120"}, {"", "60"}}) {
            Label l = new Label(col[0]);
            l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px; -fx-font-weight: 700;");
            l.setPrefWidth(Double.parseDouble(col[1]));
            header.getChildren().add(l);
        }
        docTableContainer.getChildren().add(header);

        if (filtered.isEmpty()) {
            Label empty = new Label("Aucun document trouvé");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 40;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            docTableContainer.getChildren().add(empty);
            return;
        }

        for (DocEntry doc : filtered) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16));
            row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f8fafc; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;"));

            // Type icon + label
            HBox typeCell = new HBox(6);
            typeCell.setAlignment(Pos.CENTER_LEFT);
            typeCell.setPrefWidth(110);
            StackPane typeIcon = new StackPane();
            typeIcon.setMinSize(26, 26);
            typeIcon.setMaxSize(26, 26);
            boolean isAss = "assurance".equals(doc.type());
            typeIcon.setStyle(isAss
                ? "-fx-background-color: #dbeafe; -fx-background-radius: 7;"
                : "-fx-background-color: #ede9fe; -fx-background-radius: 7;");
            Label typeIconLbl = new Label(isAss ? "🛡" : "📋");
            typeIconLbl.setStyle("-fx-font-size: 12px;");
            typeIcon.getChildren().add(typeIconLbl);
            Label typeName = new Label(isAss ? "Assurance" : "Visite Tech.");
            typeName.setStyle(isAss
                ? "-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 600;"
                : "-fx-text-fill: #7c3aed; -fx-font-size: 11px; -fx-font-weight: 600;");
            typeCell.getChildren().addAll(typeIcon, typeName);

            // Vehicle
            VBox vehicleCell = new VBox(1);
            vehicleCell.setPrefWidth(180);
            Label vLbl = new Label(doc.vehicule());
            vLbl.setStyle("-fx-font-weight: 600; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
            Label deptLbl = new Label(doc.departement());
            deptLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
            vehicleCell.getChildren().addAll(vLbl, deptLbl);

            // Immat
            Label immat = new Label(doc.immatriculation());
            immat.setStyle("-fx-font-family: monospace; -fx-text-fill: #64748b; -fx-background-color: #f1f5f9; -fx-background-radius: 4; -fx-padding: 1 6; -fx-font-size: 11px;");
            immat.setPrefWidth(140);

            // Organisme
            Label orgLbl = new Label(doc.compagnie());
            orgLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            orgLbl.setPrefWidth(180);

            // Début
            Label debutLbl = new Label("—".equals(doc.debut()) ? "—" : doc.debut());
            debutLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            debutLbl.setPrefWidth(110);

            // Échéance
            VBox echeanceCell = new VBox(1);
            echeanceCell.setPrefWidth(140);
            String echeanceColor = switch (doc.urgency()) {
                case "expired" -> "#dc2626";
                case "soon" -> "#d97706";
                default -> "#334155";
            };
            Label echeanceLbl = new Label(doc.fin());
            echeanceLbl.setStyle("-fx-font-weight: 600; -fx-text-fill: " + echeanceColor + "; -fx-font-size: 12px;");
            if (doc.diffDays() != -9999) {
                String diffStr = doc.diffDays() < 0
                    ? "Expiré il y a " + Math.abs(doc.diffDays()) + "j"
                    : doc.diffDays() == 0 ? "Expire aujourd'hui"
                    : "Dans " + doc.diffDays() + "j";
                Label diffLbl = new Label(diffStr);
                diffLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
                echeanceCell.getChildren().addAll(echeanceLbl, diffLbl);
            } else {
                echeanceCell.getChildren().add(echeanceLbl);
            }

            // État badge
            Label etatLbl = new Label(switch (doc.urgency()) {
                case "ok" -> "✓ Valide";
                case "soon" -> "⚠ Expire bientôt";
                default -> "✕ Expiré";
            });
            etatLbl.setStyle(switch (doc.urgency()) {
                case "ok" -> "-fx-background-color: #ecfdf5; -fx-text-fill: #065f46; -fx-border-color: #a7f3d0; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px;";
                case "soon" -> "-fx-background-color: #fffbeb; -fx-text-fill: #92400e; -fx-border-color: #fde68a; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px;";
                default -> "-fx-background-color: #fef2f2; -fx-text-fill: #991b1b; -fx-border-color: #fecaca; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px;";
            });
            etatLbl.setPrefWidth(120);

            // Actions
            HBox actions = new HBox(4);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.setPrefWidth(60);
            Button eyeBtn = new Button("👁");
            eyeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 4;");
            eyeBtn.setOnAction(e -> ToastUtil.show(modalOverlay, "Document : " + doc.vehicule() + " — " + (isAss ? "Assurance" : "Visite Tech.")));
            Button uploadBtn = new Button("↑");
            uploadBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 4;");
            uploadBtn.setOnAction(e -> showImportModal());
            actions.getChildren().addAll(eyeBtn, uploadBtn);

            row.getChildren().addAll(typeCell, vehicleCell, immat, orgLbl, debutLbl, echeanceCell, etatLbl, actions);
            docTableContainer.getChildren().add(row);
        }
    }

    private void buildVehicleCards() {
        vehicleCardsContainer.getChildren().clear();
        for (Vehicle v : data.getVehicules()) {
            if ("Hors service".equals(v.getStatut())) continue;
            vehicleCardsContainer.getChildren().add(makeVehicleCard(v));
        }
    }

    private VBox makeVehicleCard(Vehicle v) {
        String assU = urgency(v.getAssuranceFin());
        String vtU = urgency(v.getVisiteTechniqueEcheance());
        String overall = "expired".equals(assU) || "expired".equals(vtU) ? "expired"
            : "soon".equals(assU) || "soon".equals(vtU) ? "soon" : "ok";

        String borderColor = switch (overall) {
            case "expired" -> "#fecaca";
            case "soon" -> "#fde68a";
            default -> "#e2e8f0";
        };

        VBox card = new VBox(0);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + borderColor + "; -fx-border-radius: 12; -fx-border-width: 1;");

        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setPadding(new Insets(12, 14, 12, 14));
        cardHeader.setStyle("-fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");
        VBox nameBox = new VBox(2);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label nameLbl = new Label(v.getNomComplet());
        nameLbl.setStyle("-fx-font-weight: 600; -fx-text-fill: #0f172a; -fx-font-size: 12px;");
        Label immatLbl = new Label(v.getImmatriculation());
        immatLbl.setStyle("-fx-font-family: monospace; -fx-text-fill: #94a3b8; -fx-font-size: 10px;");
        nameBox.getChildren().addAll(nameLbl, immatLbl);
        Label overallBadge = new Label(switch (overall) {
            case "ok" -> "Valide";
            case "soon" -> "Expire bientôt";
            default -> "Expiré";
        });
        overallBadge.setStyle(switch (overall) {
            case "ok" -> "-fx-background-color: #ecfdf5; -fx-text-fill: #065f46; -fx-border-color: #a7f3d0; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 10px;";
            case "soon" -> "-fx-background-color: #fffbeb; -fx-text-fill: #92400e; -fx-border-color: #fde68a; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 10px;";
            default -> "-fx-background-color: #fef2f2; -fx-text-fill: #991b1b; -fx-border-color: #fecaca; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 10px;";
        });
        cardHeader.getChildren().addAll(nameBox, overallBadge);

        VBox body = new VBox(10);
        body.setPadding(new Insets(12, 14, 12, 14));
        body.getChildren().addAll(
            makeDocRow("🛡", "Assurance", v.getAssuranceFin(), v.getAssuranceCompagnie(), assU),
            makeDocRow("📋", "Visite technique", v.getVisiteTechniqueEcheance(), "Centre CT", vtU),
            makeDocRow("📄", "Carte grise", "Permanente", v.getCarteGriseNum(), "ok")
        );

        card.getChildren().addAll(cardHeader, body);
        return card;
    }

    private HBox makeDocRow(String icon, String label, String echeance, String info, String urgency) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        HBox left = new HBox(8);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);
        String iconColor = switch (urgency) {
            case "ok" -> "#059669";
            case "soon" -> "#d97706";
            default -> "#dc2626";
        };
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: " + iconColor + "; -fx-font-size: 12px;");
        VBox labelBox = new VBox(1);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155; -fx-font-size: 11px;");
        Label infoLbl = new Label(info);
        infoLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
        labelBox.getChildren().addAll(lbl, infoLbl);
        left.getChildren().addAll(iconLbl, labelBox);
        String textColor = switch (urgency) {
            case "ok" -> "#059669";
            case "soon" -> "#d97706";
            default -> "#dc2626";
        };
        Label echeanceLbl = new Label(echeance);
        echeanceLbl.setStyle("-fx-font-weight: 600; -fx-text-fill: " + textColor + "; -fx-font-size: 11px;");
        row.getChildren().addAll(left, echeanceLbl);
        return row;
    }

    @FXML
    private void showImportModal() {
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
        Label title = new Label("Importer un document");
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn);

        VBox body = new VBox(16);
        body.getStyleClass().add("modal-body");

        String[][] fields = {
            {"Véhicule",          "Sélectionner un véhicule"},
            {"Type de document",  "assurance / visite_technique / carte_grise"},
            {"Organisme",         "Nom de l'organisme ou compagnie"},
            {"Date de début",     "JJ/MM/AAAA"},
            {"Date d'échéance",   "JJ/MM/AAAA"},
            {"Numéro de police",  "Référence du document"},
        };
        for (String[] f : fields) {
            VBox fieldBox = new VBox(6);
            Label lbl = new Label(f[0]);
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField();
            tf.setPromptText(f[1]);
            tf.getStyleClass().add("form-input");
            fieldBox.getChildren().addAll(lbl, tf);
            body.getChildren().add(fieldBox);
        }

        HBox footer = new HBox(12);
        footer.getStyleClass().add("modal-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("btn-outline");
        cancelBtn.setOnAction(e -> closeModal());
        Button saveBtn = new Button("Importer");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            closeModal();
            ToastUtil.show(modalOverlay, "Document importé avec succès");
        });
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, body, footer);
        modalOverlay.getChildren().add(modal);
    }

    @FXML private void closeModal() {
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
        }
    }

    private String urgency(String dateStr) {
        if ("—".equals(dateStr) || dateStr == null) return "expired";
        try {
            long diff = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(dateStr, FMT));
            if (diff < 0) return "expired";
            if (diff <= 30) return "soon";
            return "ok";
        } catch (Exception e) { return "ok"; }
    }

    private long diffDays(String dateStr) {
        if ("—".equals(dateStr) || dateStr == null) return -9999;
        try {
            return ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(dateStr, FMT));
        } catch (Exception e) { return -9999; }
    }
}
