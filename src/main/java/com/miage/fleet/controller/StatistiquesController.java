package com.miage.fleet.controller;

import com.miage.fleet.model.Vehicle;
import com.miage.fleet.service.MockDataService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.*;
import java.util.stream.Collectors;

public class StatistiquesController {

    @FXML private HBox kpiRow;
    @FXML private StackPane lineChartContainer;
    @FXML private StackPane pieContainer;
    @FXML private VBox deptBarsContainer;
    @FXML private VBox kmBarsContainer;
    @FXML private StackPane barChartContainer;

    private final MockDataService data = MockDataService.getInstance();

    @FXML
    public void initialize() {
        buildKpis();
        buildSmoothAreaChart();
        buildDonutChart();
        buildDeptBars();
        buildKmBars();
        buildHorizontalBarChart();
    }

    // ─── KPI Cards ────────────────────────────────────────────────────────────

    private void buildKpis() {
        List<Vehicle> vehicles = data.getVehicules();
        int totalKm = vehicles.stream().mapToInt(Vehicle::getKilometrage).sum();
        long totalMissions = data.getMissions().size();
        long terminees = data.getMissions().stream().filter(m -> "Terminée".equals(m.getStatut())).count();
        long actifs = vehicles.stream().filter(v -> !"Hors service".equals(v.getStatut())).count();

        kpiRow.getChildren().addAll(
            makeKpi("🚗", String.valueOf(actifs), "Véhicules actifs", "kpi-icon-box-blue"),
            makeKpi("✈", String.valueOf(totalMissions), "Missions YTD", "kpi-icon-box-violet"),
            makeKpi("⊙", String.format("%,d km", totalKm).replace(",", " "), "Km parcourus YTD", "kpi-icon-box-emerald"),
            makeKpi("🔧", "340 000 FCFA", "Coût entretien YTD", "kpi-icon-box-orange")
        );
        kpiRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
    }

    private VBox makeKpi(String icon, String value, String label, String iconStyle) {
        VBox card = new VBox(12);
        card.getStyleClass().add("kpi-card");
        card.setPadding(new Insets(20));
        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add(iconStyle);
        iconBox.setMinSize(40, 40);
        iconBox.setMaxSize(40, 40);
        Label iconLbl = new Label(icon);
        iconLbl.getStyleClass().add("kpi-icon");
        iconBox.getChildren().add(iconLbl);
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 22px; -fx-font-weight: 700;");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("kpi-label");
        card.getChildren().addAll(iconBox, valLbl, lbl);
        return card;
    }

    // ─── Smooth Area Chart (Canvas) ────────────────────────────────────────────

    private void buildSmoothAreaChart() {
        String[] months = data.getMoisLabels();
        int[][] rawData = data.getMissionsMensuellesData();

        int[] values = new int[12];
        for (int[] d : rawData) values[d[0] - 1] = d[1];

        double w = 560, h = 230;
        double padL = 42, padR = 16, padT = 18, padB = 34;
        double plotW = w - padL - padR;
        double plotH = h - padT - padB;

        int rawMax = Arrays.stream(values).max().orElse(1);
        int yMax = (int) (Math.ceil(rawMax * 1.2 / 5.0) * 5);

        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // ── Grid & Y-axis labels ──
        gc.setFont(Font.font("System", 10));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.CENTER);
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            double y = padT + plotH * i / gridLines;
            gc.setStroke(Color.web("#f1f5f9"));
            gc.setLineWidth(1);
            gc.strokeLine(padL, y, padL + plotW, y);
            int labelVal = yMax - yMax * i / gridLines;
            gc.setFill(Color.web("#cbd5e1"));
            gc.fillText(String.valueOf(labelVal), padL - 6, y);
        }

        // ── Compute points ──
        double[] px = new double[12];
        double[] py = new double[12];
        double step = plotW / 11.0;
        for (int i = 0; i < 12; i++) {
            px[i] = padL + i * step;
            py[i] = padT + plotH * (1.0 - (double) values[i] / yMax);
        }

        // ── Gradient fill under curve ──
        LinearGradient fillGrad = new LinearGradient(0, padT, 0, padT + plotH, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#3b82f6", 0.22)),
            new Stop(0.6, Color.web("#3b82f6", 0.06)),
            new Stop(1.0, Color.web("#3b82f6", 0.0)));
        gc.setFill(fillGrad);
        gc.beginPath();
        gc.moveTo(px[0], py[0]);
        for (int i = 1; i < 12; i++) {
            double cx1 = (px[i - 1] + px[i]) / 2;
            gc.bezierCurveTo(cx1, py[i - 1], cx1, py[i], px[i], py[i]);
        }
        gc.lineTo(px[11], padT + plotH);
        gc.lineTo(px[0], padT + plotH);
        gc.closePath();
        gc.fill();

        // ── Smooth stroke ──
        gc.setStroke(Color.web("#3b82f6"));
        gc.setLineWidth(2.5);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.beginPath();
        gc.moveTo(px[0], py[0]);
        for (int i = 1; i < 12; i++) {
            double cx1 = (px[i - 1] + px[i]) / 2;
            gc.bezierCurveTo(cx1, py[i - 1], cx1, py[i], px[i], py[i]);
        }
        gc.stroke();

        // ── White-filled dots ──
        for (int i = 0; i < 12; i++) {
            gc.setFill(Color.WHITE);
            gc.fillOval(px[i] - 4, py[i] - 4, 8, 8);
            gc.setStroke(Color.web("#3b82f6"));
            gc.setLineWidth(2);
            gc.strokeOval(px[i] - 4, py[i] - 4, 8, 8);
        }

        // ── Value tooltips on first/last/max ──
        int maxIdx = 0;
        for (int i = 1; i < 12; i++) if (values[i] > values[maxIdx]) maxIdx = i;
        int[] highlight = {0, maxIdx, 11};
        gc.setFont(Font.font("System", FontWeight.BOLD, 10));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BOTTOM);
        gc.setFill(Color.web("#3b82f6"));
        for (int idx : highlight) {
            gc.fillText(String.valueOf(values[idx]), px[idx], py[idx] - 6);
        }

        // ── X-axis labels ──
        gc.setFill(Color.web("#94a3b8"));
        gc.setFont(Font.font("System", 10));
        gc.setTextBaseline(VPos.TOP);
        for (int i = 0; i < 12; i++) {
            gc.fillText(months[i], px[i], padT + plotH + 8);
        }

        lineChartContainer.getChildren().clear();
        StackPane wrap = new StackPane(canvas);
        wrap.setPadding(new Insets(4, 12, 4, 4));
        lineChartContainer.getChildren().add(wrap);
    }

    // ─── Donut Chart (Canvas) ─────────────────────────────────────────────────

    private void buildDonutChart() {
        Map<String, Long> byStatut = data.getVehicules().stream()
            .collect(Collectors.groupingBy(Vehicle::getStatut, Collectors.counting()));

        int total = (int) byStatut.values().stream().mapToLong(l -> l).sum();

        String[] statusOrder = {"Disponible", "En mission", "Affecté", "En réparation", "Hors service"};
        String[] hexColors = {"#10b981", "#3b82f6", "#8b5cf6", "#f97316", "#ef4444"};

        double size = 180;
        double cx = size / 2, cy = size / 2;
        double outerR = 74, innerR = 46;

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double startAngle = -90;
        double gap = 2.0;

        for (int i = 0; i < statusOrder.length; i++) {
            String s = statusOrder[i];
            if (!byStatut.containsKey(s)) continue;
            double pct = (double) byStatut.get(s) / total;
            double angle = 360.0 * pct - gap;
            if (angle <= 0) continue;
            gc.setFill(Color.web(hexColors[i]));
            gc.fillArc(cx - outerR, cy - outerR, outerR * 2, outerR * 2, startAngle, angle, ArcType.ROUND);
            startAngle += angle + gap;
        }

        // ── Donut hole + subtle shadow ring ──
        gc.setFill(Color.web("#e2e8f0", 0.3));
        gc.fillOval(cx - innerR - 3, cy - innerR - 3, (innerR + 3) * 2, (innerR + 3) * 2);
        gc.setFill(Color.WHITE);
        gc.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // ── Center text ──
        gc.setFill(Color.web("#0f172a"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(String.valueOf(total), cx, cy - 8);
        gc.setFont(Font.font("System", 10));
        gc.setFill(Color.web("#94a3b8"));
        gc.fillText("véhicules", cx, cy + 11);

        // ── Legend ──
        VBox legend = new VBox(5);
        legend.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < statusOrder.length; i++) {
            String s = statusOrder[i];
            if (!byStatut.containsKey(s)) continue;
            long count = byStatut.get(s);
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Region dot = new Region();
            dot.setMinSize(9, 9);
            dot.setMaxSize(9, 9);
            dot.setStyle("-fx-background-color: " + hexColors[i] + "; -fx-background-radius: 5;");
            Label nameLbl = new Label(s);
            nameLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
            HBox.setHgrow(nameLbl, Priority.ALWAYS);
            Label countLbl = new Label(count + "  " + String.format("%.0f%%", 100.0 * count / total));
            countLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
            row.getChildren().addAll(dot, nameLbl, countLbl);
            legend.getChildren().add(row);
        }

        VBox container = new VBox(16);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(8));
        container.getChildren().addAll(canvas, legend);

        pieContainer.getChildren().clear();
        pieContainer.getChildren().add(container);
    }

    // ─── Dept Bars (custom progress bars) ─────────────────────────────────────

    private void buildDeptBars() {
        Map<String, Long> byDept = new LinkedHashMap<>();
        byDept.put("Direction Générale", 2L);
        byDept.put("Commercial", 2L);
        byDept.put("Logistique", 1L);
        byDept.put("Technique", 1L);
        long max = byDept.values().stream().mapToLong(l -> l).max().orElse(1);
        for (Map.Entry<String, Long> entry : byDept.entrySet()) {
            deptBarsContainer.getChildren().add(makeProgressBar(entry.getKey(), entry.getValue(), max, "#3b82f6"));
        }
    }

    // ─── KM Bars (custom progress bars) ───────────────────────────────────────

    private void buildKmBars() {
        List<Vehicle> vehicles = data.getVehicules();
        int maxKm = vehicles.stream().mapToInt(Vehicle::getKilometrage).max().orElse(1);
        for (Vehicle v : vehicles) {
            double pct = (double) v.getKilometrage() / maxKm;
            String color = pct > 0.8 ? "#ef4444" : pct > 0.6 ? "#f97316" : "#3b82f6";
            kmBarsContainer.getChildren().add(makeProgressBar(
                v.getMarque() + " " + v.getModele(),
                v.getKilometrage(), maxKm, color,
                String.format("%,d km", v.getKilometrage()).replace(",", " ")
            ));
        }
    }

    // ─── Horizontal Bar Chart (Canvas) ────────────────────────────────────────

    private void buildHorizontalBarChart() {
        // Sorted descending by count
        Map<String, Long> byDept = data.getVehicules().stream()
            .collect(Collectors.groupingBy(Vehicle::getDepartement, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (a, b) -> a, LinkedHashMap::new));

        double barH = 28, gapH = 14;
        double padL = 152, padR = 52, padT = 10, padB = 10;
        int n = byDept.size();
        double w = 420;
        double totalH = padT + padB + n * (barH + gapH) - gapH;
        long maxVal = byDept.values().stream().mapToLong(l -> l).max().orElse(1);
        double plotW = w - padL - padR;

        Canvas canvas = new Canvas(w, totalH);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        String[] barHex = {"#3b82f6", "#10b981", "#8b5cf6", "#f97316", "#06b6d4", "#ef4444"};
        int ci = 0;
        double y = padT;

        for (Map.Entry<String, Long> entry : byDept.entrySet()) {
            double pct = (double) entry.getValue() / maxVal;
            double barW = Math.max(plotW * pct, barH + 1);
            String hex = barHex[ci++ % barHex.length];

            // Label
            gc.setFill(Color.web("#374151"));
            gc.setFont(Font.font("System", 12));
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(entry.getKey(), padL - 10, y + barH / 2);

            // Track background
            gc.setFill(Color.web("#f1f5f9"));
            gc.fillRoundRect(padL, y, plotW, barH, barH, barH);

            // Gradient bar
            LinearGradient barGrad = new LinearGradient(padL, 0, padL + barW, 0, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(hex)),
                new Stop(1.0, Color.web(hex, 0.55)));
            gc.setFill(barGrad);
            gc.fillRoundRect(padL, y, barW, barH, barH, barH);

            // Value label
            gc.setFill(Color.web("#374151"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(entry.getValue() + " véh.", padL + barW + 8, y + barH / 2);

            y += barH + gapH;
        }

        barChartContainer.getChildren().clear();
        VBox wrapper = new VBox(canvas);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        wrapper.setPadding(new Insets(8, 12, 8, 12));
        barChartContainer.getChildren().add(wrapper);
    }

    // ─── Progress bar helper ───────────────────────────────────────────────────

    private VBox makeProgressBar(String label, long value, long max, String color) {
        return makeProgressBar(label, value, max, color, String.valueOf(value));
    }

    private VBox makeProgressBar(String label, long value, long max, String color, String displayValue) {
        VBox box = new VBox(4);
        HBox labelRow = new HBox();
        labelRow.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        HBox.setHgrow(lbl, Priority.ALWAYS);
        Label valLbl = new Label(displayValue);
        valLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: 600;");
        labelRow.getChildren().addAll(lbl, valLbl);

        HBox progressRow = new HBox();
        progressRow.setMinHeight(8);
        progressRow.setMaxHeight(8);
        progressRow.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 4;");

        double pct = max > 0 ? (double) value / max : 0;
        Region bar = new Region();
        bar.setMinHeight(8);
        bar.setMaxHeight(8);
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        bar.setPrefWidth(0);
        HBox.setHgrow(bar, Priority.NEVER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        progressRow.getChildren().addAll(bar, spacer);
        progressRow.widthProperty().addListener((obs, old, width) ->
            bar.setPrefWidth(width.doubleValue() * pct));

        box.getChildren().addAll(labelRow, progressRow);
        return box;
    }
}
