package com.miage.fleet.controller;

import com.miage.fleet.model.Mission;
import com.miage.fleet.model.Vehicle;
import com.miage.fleet.service.MockDataService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DashboardController {

    @FXML private Label dateLabel;
    @FXML private HBox kpiRow;
    @FXML private HBox chartsRow;
    @FXML private StackPane areaChartContainer;
    @FXML private StackPane pieChartContainer;
    @FXML private VBox pieLegend;
    @FXML private VBox missionsListContainer;
    @FXML private VBox alertsContainer;
    @FXML private Label alertCountBadge;
    @FXML private StackPane barChartContainer;
    @FXML private HBox bottomRow;

    private final MockDataService data = MockDataService.getInstance();

    @FXML
    public void initialize() {
        setDate();
        buildKpiCards();
        buildAreaChart();
        buildDonutChart();
        buildMissionsList();
        buildAlerts();
        buildMiniBarChart();
    }

    private void setDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        dateLabel.setText("Vue d'ensemble du parc automobile — " + fmt.format(today));
    }

    private void buildKpiCards() {
        List<Vehicle> vehicles = data.getVehicules();
        long disponibles = vehicles.stream().filter(v -> "Disponible".equals(v.getStatut())).count();
        long enMission   = vehicles.stream().filter(v -> "En mission".equals(v.getStatut())).count();
        long enRep       = vehicles.stream().filter(v -> "En réparation".equals(v.getStatut())).count();
        long horsService = vehicles.stream().filter(v -> "Hors service".equals(v.getStatut())).count();

        kpiRow.getChildren().addAll(
            makeKpiCard("🚗", "Total véhicules", vehicles.size(), "dont " + horsService + " hors service", "kpi-icon-box-blue"),
            makeKpiCard("✓",  "Disponibles",     (int) disponibles, "Prêts à l'emploi",       "kpi-icon-box-emerald"),
            makeKpiCard("✈",  "En mission",      (int) enMission,   "Actuellement déployés",  "kpi-icon-box-violet"),
            makeKpiCard("🔧", "En réparation",   (int) enRep,       "Immobilisés",            "kpi-icon-box-orange")
        );
        kpiRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
    }

    private VBox makeKpiCard(String icon, String label, int value, String sub, String iconStyle) {
        VBox card = new VBox(16);
        card.getStyleClass().add("kpi-card");
        card.setPadding(new Insets(20));

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add(iconStyle);
        iconBox.setMaxSize(40, 40);
        iconBox.setMinSize(40, 40);
        Label iconLbl = new Label(icon);
        iconLbl.getStyleClass().add("kpi-icon");
        iconBox.getChildren().add(iconLbl);

        Label valueLbl = new Label(String.valueOf(value));
        valueLbl.getStyleClass().add("kpi-value");

        Label labelLbl = new Label(label);
        labelLbl.getStyleClass().add("kpi-label");

        Label subLbl = new Label(sub);
        subLbl.getStyleClass().add("kpi-sub");

        card.getChildren().addAll(iconBox, valueLbl, labelLbl, subLbl);
        return card;
    }

    // ─── Smooth area chart ────────────────────────────────────────────────────
    private void buildAreaChart() {
        String[] mois   = data.getMoisLabels();
        int[][] rawData = data.getMissionsMensuellesData();

        // Build values array indexed 0..11
        double[] values = new double[12];
        for (int[] d : rawData) {
            if (d[0] >= 1 && d[0] <= 12) values[d[0] - 1] = d[1];
        }

        double W = 560, H = 220;
        double padL = 44, padR = 16, padT = 20, padB = 36;
        double plotW = W - padL - padR;
        double plotH = H - padT - padB;

        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Grid
        double yMax = 0;
        for (double v : values) yMax = Math.max(yMax, v);
        yMax = yMax < 1 ? 10 : Math.ceil(yMax / 5.0) * 5 + 5;

        gc.setStroke(Color.web("#f1f5f9"));
        gc.setLineWidth(1);
        gc.setFont(Font.font("System", 10));
        gc.setFill(Color.web("#cbd5e1"));
        gc.setTextAlign(TextAlignment.RIGHT);
        for (int i = 0; i <= 4; i++) {
            double y = padT + plotH * (1.0 - i / 4.0);
            gc.strokeLine(padL, y, W - padR, y);
            gc.fillText(String.valueOf((int)(yMax * i / 4.0)), padL - 6, y + 4);
        }

        // X labels
        double step = plotW / 11.0;
        double[] px = new double[12];
        double[] py = new double[12];
        gc.setFill(Color.web("#94a3b8"));
        gc.setTextAlign(TextAlignment.CENTER);
        for (int i = 0; i < 12; i++) {
            px[i] = padL + i * step;
            py[i] = padT + plotH * (1.0 - values[i] / yMax);
            if (i % 2 == 0) gc.fillText(mois[i], px[i], H - 6);
        }

        // Gradient fill under curve
        LinearGradient fillGrad = new LinearGradient(
            0, padT, 0, H - padB, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#3b82f6", 0.22)),
            new Stop(0.6, Color.web("#3b82f6", 0.06)),
            new Stop(1.0, Color.web("#3b82f6", 0.00))
        );
        gc.setFill(fillGrad);
        gc.beginPath();
        gc.moveTo(px[0], py[0]);
        for (int i = 1; i < 12; i++) {
            double cx = (px[i - 1] + px[i]) / 2.0;
            gc.bezierCurveTo(cx, py[i - 1], cx, py[i], px[i], py[i]);
        }
        gc.lineTo(px[11], H - padB);
        gc.lineTo(px[0],  H - padB);
        gc.closePath();
        gc.fill();

        // Stroke curve
        gc.setStroke(Color.web("#3b82f6"));
        gc.setLineWidth(2.5);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.beginPath();
        gc.moveTo(px[0], py[0]);
        for (int i = 1; i < 12; i++) {
            double cx = (px[i - 1] + px[i]) / 2.0;
            gc.bezierCurveTo(cx, py[i - 1], cx, py[i], px[i], py[i]);
        }
        gc.stroke();

        // Find max index
        int maxIdx = 0;
        for (int i = 1; i < 12; i++) if (values[i] > values[maxIdx]) maxIdx = i;

        // Dots + labels on key points
        for (int i : new int[]{0, maxIdx, 11}) {
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.web("#3b82f6"));
            gc.setLineWidth(2.0);
            gc.fillOval(px[i] - 4, py[i] - 4, 8, 8);
            gc.strokeOval(px[i] - 4, py[i] - 4, 8, 8);
            gc.setFill(Color.web("#1e40af"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf((int) values[i]), px[i], py[i] - 9);
        }

        areaChartContainer.getChildren().add(canvas);
    }

    // ─── Donut chart ──────────────────────────────────────────────────────────
    private void buildDonutChart() {
        List<Vehicle> vehicles = data.getVehicules();
        Map<String, Long> groups = new TreeMap<>();
        for (Vehicle v : vehicles) groups.merge(v.getStatut(), 1L, Long::sum);

        String[] statuts = {"Disponible", "En mission", "Affecté", "En réparation", "Visite technique", "Hors service"};
        String[] colors  = {"#10b981",    "#3b82f6",    "#8b5cf6", "#f59e0b",        "#6366f1",          "#ef4444"};

        double total = vehicles.size();
        if (total == 0) total = 1;

        double W = 200, H = 180;
        double cx = W / 2.0, cy = H / 2.0;
        double outerR = 72, innerR = 44;

        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Slices
        double startAngle = 90;
        double gap = 2.0;
        for (int i = 0; i < statuts.length; i++) {
            long count = groups.getOrDefault(statuts[i], 0L);
            if (count == 0) continue;
            double angle = 360.0 * count / total - gap;
            gc.setFill(Color.web(colors[i]));
            gc.fillArc(cx - outerR, cy - outerR, outerR * 2, outerR * 2,
                       startAngle, angle, ArcType.ROUND);
            startAngle += angle + gap;
        }

        // Shadow ring
        gc.setFill(Color.web("#e2e8f0", 0.30));
        gc.fillOval(cx - innerR - 2, cy - innerR - 2, (innerR + 2) * 2, (innerR + 2) * 2);

        // Donut hole
        gc.setFill(Color.WHITE);
        gc.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // Center text
        gc.setFill(Color.web("#0f172a"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 22));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf((int) total), cx, cy + 7);
        gc.setFill(Color.web("#94a3b8"));
        gc.setFont(Font.font("System", 10));
        gc.fillText("véhicules", cx, cy + 19);

        pieChartContainer.getChildren().add(canvas);

        // Legend
        pieLegend.getChildren().clear();
        for (int i = 0; i < statuts.length; i++) {
            long count = groups.getOrDefault(statuts[i], 0L);
            if (count == 0) continue;

            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            Canvas dot = new Canvas(9, 9);
            GraphicsContext dg = dot.getGraphicsContext2D();
            dg.setFill(Color.web(colors[i]));
            dg.fillRoundRect(0, 0, 9, 9, 4, 4);

            Label nameLbl = new Label(statuts[i]);
            nameLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
            HBox.setHgrow(nameLbl, Priority.ALWAYS);

            int pct = (int) Math.round(count * 100.0 / total);
            Label countLbl = new Label(count + "  " + pct + "%");
            countLbl.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-font-weight: bold;");

            row.getChildren().addAll(dot, nameLbl, countLbl);
            pieLegend.getChildren().add(row);
        }
    }

    // ─── Mini vertical bar chart (km mensuel) ────────────────────────────────
    private void buildMiniBarChart() {
        String[] mois   = data.getMoisLabels();
        int[][] rawData = data.getKmMensuelData();

        double[] values = new double[12];
        for (int[] d : rawData) {
            if (d[0] >= 1 && d[0] <= 12) values[d[0] - 1] = d[1];
        }

        double W = 264, H = 80;
        double padL = 4, padR = 4, padT = 6, padB = 18;
        double plotW = W - padL - padR;
        double plotH = H - padT - padB;

        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double yMax = 0;
        for (double v : values) yMax = Math.max(yMax, v);
        if (yMax < 1) yMax = 1;

        double barW = plotW / 12.0;
        double gap  = barW * 0.25;
        double bw   = barW - gap;

        // Current month highlight index
        int curMonth = LocalDate.now().getMonthValue() - 1;

        for (int i = 0; i < 12; i++) {
            double bh  = plotH * (values[i] / yMax);
            double bx  = padL + i * barW + gap / 2.0;
            double by  = padT + plotH - bh;

            // Track (background)
            gc.setFill(Color.web("#f1f5f9"));
            gc.fillRoundRect(bx, padT, bw, plotH, 4, 4);

            // Gradient bar
            boolean highlight = (i == curMonth);
            String col = highlight ? "#3b82f6" : "#6366f1";
            LinearGradient grad = new LinearGradient(
                0, by, 0, by + bh, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(col, 1.0)),
                new Stop(1.0, Color.web(col, 0.45))
            );
            gc.setFill(grad);
            gc.fillRoundRect(bx, by, bw, bh, 4, 4);

            // Month label
            gc.setFill(Color.web(highlight ? "#3b82f6" : "#94a3b8"));
            gc.setFont(Font.font("System", highlight ? FontWeight.BOLD : FontWeight.NORMAL, 9));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(mois[i].substring(0, 3), bx + bw / 2.0, H - 4);
        }

        barChartContainer.getChildren().add(canvas);
    }

    // ─── Missions list ────────────────────────────────────────────────────────
    private void buildMissionsList() {
        List<Mission> enCours = data.getMissions().stream()
            .filter(m -> "En cours".equals(m.getStatut()))
            .toList();

        if (enCours.isEmpty()) {
            Label empty = new Label("Aucune mission en cours");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 32;");
            missionsListContainer.getChildren().add(empty);
            return;
        }

        for (Mission m : enCours) {
            HBox row = new HBox(12);
            row.getStyleClass().add("mission-row");
            row.setPadding(new Insets(14, 20, 14, 20));
            row.setAlignment(Pos.CENTER_LEFT);

            StackPane iconBox = new StackPane();
            iconBox.getStyleClass().add("mission-icon-blue");
            iconBox.setMinSize(32, 32);
            iconBox.setMaxSize(32, 32);
            Label iconLbl = new Label("✈");
            iconLbl.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 13px;");
            iconBox.getChildren().add(iconLbl);

            VBox info = new VBox(3);
            HBox.setHgrow(info, Priority.ALWAYS);

            HBox nameRow = new HBox(6);
            nameRow.setAlignment(Pos.CENTER_LEFT);
            Label nameLbl = new Label(m.getEmploye());
            nameLbl.getStyleClass().add("mission-employe");
            Label matricLbl = new Label("(" + m.getEmployeMatricule() + ")");
            matricLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            nameRow.getChildren().addAll(nameLbl, matricLbl);

            Label vehicleLbl = new Label(m.getVehicule() + " — " + m.getImmatriculation());
            vehicleLbl.getStyleClass().add("mission-vehicle");

            HBox detailRow = new HBox(12);
            detailRow.setAlignment(Pos.CENTER_LEFT);
            Label destLbl = new Label("📍 " + m.getDestination());
            destLbl.getStyleClass().add("mission-detail");
            Label dateLbl = new Label("🕐 " + m.getDateDepart() + " → " + m.getDateRetour());
            dateLbl.getStyleClass().add("mission-detail");
            detailRow.getChildren().addAll(destLbl, dateLbl);

            info.getChildren().addAll(nameRow, vehicleLbl, detailRow);

            Label badge = new Label(m.getStatut());
            badge.getStyleClass().add("badge-en-cours");

            row.getChildren().addAll(iconBox, info, badge);
            missionsListContainer.getChildren().add(row);
        }
    }

    // ─── Alerts ───────────────────────────────────────────────────────────────
    private void buildAlerts() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int alertCount = 0;
        for (Vehicle v : data.getVehicules()) {
            if (!"—".equals(v.getAssuranceFin())) {
                try {
                    LocalDate fin = LocalDate.parse(v.getAssuranceFin(), fmt);
                    long diff = java.time.temporal.ChronoUnit.DAYS.between(today, fin);
                    if (diff >= 0 && diff <= 30) {
                        addAlertRow("🛡 Assurance expire — " + v.getNomComplet(),
                            v.getImmatriculation(), diff, alertsContainer);
                        alertCount++;
                    }
                } catch (Exception ignored) {}
            }

            try {
                LocalDate vtFin = LocalDate.parse(v.getVisiteTechniqueEcheance(), fmt);
                long diff = java.time.temporal.ChronoUnit.DAYS.between(today, vtFin);
                if (diff >= 0 && diff <= 60) {
                    addAlertRow("📅 Visite technique — " + v.getNomComplet(),
                        v.getImmatriculation(), diff, alertsContainer);
                    alertCount++;
                }
            } catch (Exception ignored) {}
        }

        if (alertCount == 0) {
            VBox noAlert = new VBox(8);
            noAlert.setAlignment(Pos.CENTER);
            noAlert.setPadding(new Insets(24, 0, 24, 0));
            Label icon = new Label("✅");
            icon.setStyle("-fx-font-size: 32px;");
            Label text = new Label("Aucune alerte urgente");
            text.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
            noAlert.getChildren().addAll(icon, text);
            alertsContainer.getChildren().add(noAlert);
        } else {
            alertCountBadge.setText(String.valueOf(alertCount));
            alertCountBadge.setVisible(true);
            alertCountBadge.setManaged(true);
        }
    }

    private void addAlertRow(String title, String immat, long diff, VBox container) {
        boolean isUrgent = diff <= 7;
        VBox row = new VBox(4);
        row.getStyleClass().add(isUrgent ? "alert-row-red" : "alert-row-amber");

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("alert-text-red");
        titleLbl.setWrapText(true);

        Label immatLbl = new Label(immat);
        immatLbl.setStyle("-fx-text-fill: #64748b; -fx-font-family: 'Courier New', monospace; -fx-font-size: 11px;");

        String daysText = diff == 0 ? "Expire aujourd'hui !" : "Dans " + diff + " jour" + (diff > 1 ? "s" : "");
        Label daysLbl = new Label(daysText);
        daysLbl.getStyleClass().add(isUrgent ? "alert-days-red" : "alert-days-amber");

        row.getChildren().addAll(titleLbl, immatLbl, daysLbl);
        container.getChildren().add(row);
    }

    @FXML
    private void handleVoirToutes() {
        if (MainController.instance != null) {
            MainController.instance.navigateTo("missions");
        }
    }
}
