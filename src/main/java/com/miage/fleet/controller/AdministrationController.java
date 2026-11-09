package com.miage.fleet.controller;

import com.miage.fleet.model.User;
import com.miage.fleet.service.DatabaseService;
import com.miage.fleet.util.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdministrationController {

    @FXML private VBox usersTable;
    @FXML private TextField seuilAssurance;
    @FXML private TextField seuilVisite;
    @FXML private TextField intervalleEntretien;
    @FXML private StackPane modalOverlay;

    private final DatabaseService data = DatabaseService.getInstance();

    @FXML
    public void initialize() {
        buildUsersTable();
    }

    private void buildUsersTable() {
        usersTable.getChildren().clear();
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10 20; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        for (String[] col : new String[][]{{"Utilisateur", "220"}, {"Email", "260"}, {"Rôle", "180"}, {"Département", "180"}, {"Statut", "100"}, {"", "80"}}) {
            Label l = new Label(col[0]);
            l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-weight: bold;");
            l.setPrefWidth(Double.parseDouble(col[1]));
            header.getChildren().add(l);
        }
        usersTable.getChildren().add(header);

        for (User u : data.getUsers()) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 20, 12, 20));
            row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;");
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f8fafc; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #f1f5f9 transparent; -fx-border-width: 1;"));

            HBox userBox = new HBox(10);
            userBox.setAlignment(Pos.CENTER_LEFT);
            userBox.setPrefWidth(220);
            StackPane avatar = new StackPane();
            avatar.setMinSize(32, 32);
            avatar.setMaxSize(32, 32);
            avatar.setStyle("-fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; -fx-background-radius: 16; -fx-background-color: linear-gradient(to bottom right, #60a5fa, #6366f1);");
            Label avatarText = new Label(u.getAvatar());
            avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
            avatar.getChildren().add(avatarText);
            Label nameLabel = new Label(u.getNomComplet());
            nameLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px; -fx-font-weight: 500;");
            userBox.getChildren().addAll(avatar, nameLabel);

            Label emailLbl = new Label(u.getEmail());
            emailLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px; -fx-font-family: monospace;");
            emailLbl.setPrefWidth(260);

            Label roleLbl = new Label(u.getRoleLabel());
            roleLbl.getStyleClass().add(getRoleBadgeClass(u.getRole()));
            roleLbl.setPrefWidth(180);

            Label deptLbl = new Label(u.getDepartement());
            deptLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
            deptLbl.setPrefWidth(180);

            Label statusLbl = new Label("Actif");
            statusLbl.getStyleClass().add("badge-disponible");

            Button editBtn = new Button("✏ Modifier");
            editBtn.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-border-color: #bfdbfe; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4 10; -fx-font-size: 11px; -fx-cursor: hand;");
            editBtn.setPrefWidth(80);
            editBtn.setOnAction(e -> showEditUserModal(u));

            row.getChildren().addAll(userBox, emailLbl, roleLbl, deptLbl, statusLbl, editBtn);
            usersTable.getChildren().add(row);
        }
    }

    private String getRoleBadgeClass(String role) {
        return switch (role) {
            case "admin" -> "role-badge-admin";
            case "agent_logistique" -> "role-badge-agent";
            default -> "role-badge-consultant";
        };
    }

    @FXML
    private void showNewUserForm() {
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
        Label title = new Label("Nouvel utilisateur");
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn);

        // Body
        VBox body = new VBox(16);
        body.getStyleClass().add("modal-body");

        String[][] fields = {
            {"Nom",          "Nom de famille"},
            {"Prénom",       "Prénom"},
            {"Email",        "email@societe.ci"},
            {"Mot de passe", "••••••••"},
            {"Rôle",         "admin / agent_logistique / consultant"},
            {"Département",  "Département"},
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
            body.getChildren().add(fieldBox);
        }

        // Footer
        HBox footer = new HBox(10);
        footer.getStyleClass().add("modal-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("btn-outline");
        cancelBtn.setOnAction(e -> closeModal());
        Button saveBtn = new Button("Créer l'utilisateur");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            String nom = tfs[0].getText().trim();
            String prenom = tfs[1].getText().trim();
            String avatar = (!prenom.isEmpty() ? String.valueOf(prenom.charAt(0)) : "") +
                            (!nom.isEmpty() ? String.valueOf(nom.charAt(0)) : "?");
            int id = data.generateUserId();
            User newUser = new User(id, nom, prenom,
                tfs[2].getText().trim(), tfs[3].getText().trim(),
                tfs[4].getText().trim(), tfs[5].getText().trim(), avatar);
            data.addUser(newUser);
            buildUsersTable();
            closeModal();
            ToastUtil.show(modalOverlay, "Utilisateur créé avec succès");
        });
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, body, footer);
        modalOverlay.getChildren().add(modal);
    }

    private void showEditUserModal(User u) {
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
        Label title = new Label("Modifier — " + u.getNomComplet());
        title.getStyleClass().add("modal-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, closeBtn);

        VBox body = new VBox(16);
        body.getStyleClass().add("modal-body");

        String[][] fields = {
            {"Nom",         u.getNom()},
            {"Prénom",      u.getPrenom()},
            {"Email",       u.getEmail()},
            {"Rôle",        u.getRole()},
            {"Département", u.getDepartement()},
        };
        TextField[] tfs = new TextField[fields.length];
        int fi = 0;
        for (String[] f : fields) {
            VBox fieldBox = new VBox(6);
            Label lbl = new Label(f[0]);
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField(f[1]);
            tf.getStyleClass().add("form-input");
            tfs[fi++] = tf;
            fieldBox.getChildren().addAll(lbl, tf);
            body.getChildren().add(fieldBox);
        }

        HBox footer = new HBox(10);
        footer.getStyleClass().add("modal-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("btn-outline");
        cancelBtn.setOnAction(e -> closeModal());
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            u.setNom(tfs[0].getText().trim());
            u.setPrenom(tfs[1].getText().trim());
            u.setEmail(tfs[2].getText().trim());
            u.setRole(tfs[3].getText().trim());
            u.setDepartement(tfs[4].getText().trim());
            data.updateUser(u);
            buildUsersTable();
            closeModal();
            ToastUtil.show(modalOverlay, "Utilisateur mis à jour");
        });
        footer.getChildren().addAll(cancelBtn, saveBtn);

        modal.getChildren().addAll(header, body, footer);
        modalOverlay.getChildren().add(modal);
    }

    @FXML
    private void saveConfig() {
        ToastUtil.show(modalOverlay, "Configuration enregistrée");
    }

    @FXML
    private void closeModal() {
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
        }
    }
}
