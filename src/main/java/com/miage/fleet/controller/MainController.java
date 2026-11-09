package com.miage.fleet.controller;

import com.miage.fleet.FleetApp;
import com.miage.fleet.model.User;
import com.miage.fleet.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class MainController {

    public static MainController instance;

    @FXML private VBox navContainer;
    @FXML private VBox adminSection;
    @FXML private Button adminNavBtn;
    @FXML private Label breadcrumbCurrent;
    @FXML private Label sidebarUserName;
    @FXML private Label sidebarUserDept;
    @FXML private Label headerAvatarText;
    @FXML private Label topbarAvatarText;
    @FXML private Label topbarUserName;
    @FXML private HBox roleBadgeContainer;
    @FXML private StackPane contentArea;

    private Button activeNavBtn;
    private String currentPage = "";

    private static final String[][] NAV_ITEMS = {
        {"⊞", "Tableau de bord", "dashboard"},
        {"🚗", "Véhicules", "vehicules"},
        {"✈", "Missions", "missions"},
        {"👤", "Affectations", "affectations"},
        {"🔧", "Entretien & Réparations", "entretien"},
        {"📄", "Documents & Assurances", "documents"},
        {"📊", "Statistiques & États", "statistiques"},
    };

    @FXML
    public void initialize() {
        instance = this;
        User user = DatabaseService.getInstance().getCurrentUser();
        if (user != null) {
            sidebarUserName.setText(user.getNomComplet());
            sidebarUserDept.setText(user.getDepartement());
            headerAvatarText.setText(user.getAvatar());
            topbarAvatarText.setText(user.getAvatar());
            topbarUserName.setText(user.getNomComplet());

            Label roleBadge = new Label(user.getRoleLabel());
            roleBadge.getStyleClass().add(getRoleBadgeClass(user.getRole()));
            roleBadgeContainer.getChildren().add(roleBadge);

            if ("admin".equals(user.getRole())) {
                adminSection.setVisible(true);
                adminSection.setManaged(true);
            }
        }

        buildNavItems();
        navigate("dashboard", "Tableau de bord");
    }

    private void buildNavItems() {
        navContainer.getChildren().clear();
        for (String[] item : NAV_ITEMS) {
            String icon = item[0];
            String label = item[1];
            String page = item[2];

            Button btn = new Button(icon + "  " + label);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.getStyleClass().add("nav-item");
            btn.setOnAction(e -> navigate(page, label));
            navContainer.getChildren().add(btn);
        }
    }

    private void navigate(String page, String label) {
        if (page.equals(currentPage)) return;

        // Update active button style
        navContainer.getChildren().forEach(n -> {
            if (n instanceof Button b) {
                b.getStyleClass().remove("nav-item-active");
                if (!b.getStyleClass().contains("nav-item")) b.getStyleClass().add("nav-item");
            }
        });

        // Find and activate the clicked button
        for (int i = 0; i < NAV_ITEMS.length; i++) {
            if (NAV_ITEMS[i][2].equals(page) && i < navContainer.getChildren().size()) {
                Button btn = (Button) navContainer.getChildren().get(i);
                btn.getStyleClass().remove("nav-item");
                btn.getStyleClass().add("nav-item-active");
                break;
            }
        }

        if (adminNavBtn != null) {
            adminNavBtn.getStyleClass().remove("nav-item-active");
            if (!adminNavBtn.getStyleClass().contains("nav-item")) adminNavBtn.getStyleClass().add("nav-item");
            if ("administration".equals(page)) {
                adminNavBtn.getStyleClass().remove("nav-item");
                adminNavBtn.getStyleClass().add("nav-item-active");
            }
        }

        breadcrumbCurrent.setText(label);
        currentPage = page;
        loadPage(page);
    }

    private void loadPage(String page) {
        String fxmlPath = "/com/miage/fleet/fxml/" + page + ".fxml";
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                showPlaceholder(page);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            showPlaceholder(page);
        }
    }

    private void showPlaceholder(String page) {
        VBox placeholder = new VBox();
        placeholder.setAlignment(javafx.geometry.Pos.CENTER);
        placeholder.setSpacing(12);
        Label icon = new Label("🚧");
        icon.setStyle("-fx-font-size: 48px;");
        Label text = new Label("Page « " + page + " » — En construction");
        text.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px;");
        placeholder.getChildren().addAll(icon, text);
        contentArea.getChildren().setAll(placeholder);
    }

    public void navigateTo(String page) {
        for (String[] item : NAV_ITEMS) {
            if (item[2].equals(page)) { navigate(page, item[1]); return; }
        }
        if ("administration".equals(page)) navigate(page, "Administration");
    }

    @FXML
    private void navigateToAdmin() {
        navigate("administration", "Administration");
    }

    @FXML
    private void handleLogout() {
        DatabaseService.getInstance().logout();
        try {
            FleetApp.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRoleBadgeClass(String role) {
        return switch (role) {
            case "admin" -> "role-badge-admin";
            case "agent_logistique" -> "role-badge-agent";
            default -> "role-badge-consultant";
        };
    }
}
