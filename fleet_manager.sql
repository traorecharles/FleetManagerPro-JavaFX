-- ============================================================
--  FleetManager Pro — Schéma MySQL
--  Base : fleet_manager
--  Généré pour l'application JavaFX MiageV2
-- ============================================================

CREATE DATABASE IF NOT EXISTS fleet_manager
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE fleet_manager;

-- ------------------------------------------------------------
-- Table : utilisateur
-- ------------------------------------------------------------
DROP TABLE IF EXISTS utilisateur;
CREATE TABLE utilisateur (
  id          INT          NOT NULL AUTO_INCREMENT,
  nom         VARCHAR(50)  NOT NULL,
  prenom      VARCHAR(50)  NOT NULL,
  email       VARCHAR(100) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  role        VARCHAR(30)  NOT NULL DEFAULT 'agent_logistique',
  departement VARCHAR(50)  DEFAULT NULL,
  avatar      VARCHAR(10)  DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Table : vehicule
-- ------------------------------------------------------------
DROP TABLE IF EXISTS vehicule;
CREATE TABLE vehicule (
  id                      INT            NOT NULL AUTO_INCREMENT,
  numero_inventaire       VARCHAR(20)    DEFAULT NULL,
  marque                  VARCHAR(50)    NOT NULL,
  modele                  VARCHAR(50)    NOT NULL,
  annee                   INT            DEFAULT NULL,
  immatriculation         VARCHAR(20)    DEFAULT NULL,
  vin                     VARCHAR(50)    DEFAULT NULL,
  carburant               VARCHAR(20)    DEFAULT NULL,
  transmission            VARCHAR(20)    DEFAULT NULL,
  couleur                 VARCHAR(30)    DEFAULT NULL,
  kilometrage             INT            NOT NULL DEFAULT 0,
  statut                  VARCHAR(30)    NOT NULL DEFAULT 'Disponible',
  categorie               VARCHAR(30)    DEFAULT NULL,
  departement             VARCHAR(50)    DEFAULT NULL,
  date_achat              VARCHAR(20)    DEFAULT NULL,
  prix_achat              DECIMAL(15,2)  NOT NULL DEFAULT 0.00,
  km_achat                INT            NOT NULL DEFAULT 0,
  date_mise_service       VARCHAR(20)    DEFAULT NULL,
  date_hors_service       VARCHAR(20)    DEFAULT NULL,
  assurance_num           VARCHAR(50)    DEFAULT NULL,
  assurance_debut         VARCHAR(20)    DEFAULT NULL,
  assurance_fin           VARCHAR(20)    DEFAULT NULL,
  assurance_compagnie     VARCHAR(100)   DEFAULT NULL,
  visite_technique_date   VARCHAR(20)    DEFAULT NULL,
  visite_technique_echeance VARCHAR(20)  DEFAULT NULL,
  carte_grise_num         VARCHAR(50)    DEFAULT NULL,
  prochain_entretien_km   INT            NOT NULL DEFAULT 0,
  image                   VARCHAR(500)   DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_num_inv (numero_inventaire),
  UNIQUE KEY uq_immat   (immatriculation),
  UNIQUE KEY uq_vin     (vin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Table : mission
-- ------------------------------------------------------------
DROP TABLE IF EXISTS mission;
CREATE TABLE mission (
  id                VARCHAR(20)  NOT NULL,
  vehicule_id       INT          DEFAULT NULL,
  vehicule          VARCHAR(100) DEFAULT NULL,
  immatriculation   VARCHAR(20)  DEFAULT NULL,
  employe           VARCHAR(100) DEFAULT NULL,
  employe_matricule VARCHAR(20)  DEFAULT NULL,
  departement       VARCHAR(50)  DEFAULT NULL,
  destination       VARCHAR(100) DEFAULT NULL,
  motif             TEXT         DEFAULT NULL,
  date_depart       VARCHAR(20)  DEFAULT NULL,
  date_retour       VARCHAR(20)  DEFAULT NULL,
  km_depart         INT          NOT NULL DEFAULT 0,
  km_retour         INT          DEFAULT NULL,
  statut            VARCHAR(30)  NOT NULL DEFAULT 'Planifiée',
  ordre_mission     VARCHAR(20)  DEFAULT NULL,
  chauffeur         VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Table : affectation
-- ------------------------------------------------------------
DROP TABLE IF EXISTS affectation;
CREATE TABLE affectation (
  id                VARCHAR(20)  NOT NULL,
  vehicule_id       INT          DEFAULT NULL,
  vehicule          VARCHAR(100) DEFAULT NULL,
  immatriculation   VARCHAR(20)  DEFAULT NULL,
  employe           VARCHAR(100) DEFAULT NULL,
  employe_matricule VARCHAR(20)  DEFAULT NULL,
  fonction          VARCHAR(50)  DEFAULT NULL,
  departement       VARCHAR(50)  DEFAULT NULL,
  date_debut        VARCHAR(20)  DEFAULT NULL,
  date_fin          VARCHAR(20)  DEFAULT NULL,
  km_debut          INT          NOT NULL DEFAULT 0,
  km_actuel         INT          NOT NULL DEFAULT 0,
  statut            VARCHAR(20)  NOT NULL DEFAULT 'Active',
  motif             TEXT         DEFAULT NULL,
  vehicule_image    VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Table : entretien
-- ------------------------------------------------------------
DROP TABLE IF EXISTS entretien;
CREATE TABLE entretien (
  id                  VARCHAR(20)  NOT NULL,
  vehicule            VARCHAR(100) DEFAULT NULL,
  immatriculation     VARCHAR(20)  DEFAULT NULL,
  numero_inv          VARCHAR(20)  DEFAULT NULL,
  type                VARCHAR(50)  DEFAULT NULL,
  nature              VARCHAR(30)  DEFAULT NULL,
  description         TEXT         DEFAULT NULL,
  garage              VARCHAR(100) DEFAULT NULL,
  adresse_garage      VARCHAR(200) DEFAULT NULL,
  technicien          VARCHAR(100) DEFAULT NULL,
  date_entree         VARCHAR(20)  DEFAULT NULL,
  date_sortie_prevue  VARCHAR(20)  DEFAULT NULL,
  date_sortie_reelle  VARCHAR(20)  DEFAULT NULL,
  cout_estime         INT          NOT NULL DEFAULT 0,
  cout_reel           INT          NOT NULL DEFAULT -1,
  statut              VARCHAR(30)  NOT NULL DEFAULT 'Planifié',
  priorite            VARCHAR(20)  NOT NULL DEFAULT 'Normale',
  km_entree           INT          NOT NULL DEFAULT 0,
  pieces              TEXT         DEFAULT NULL,
  bon_commande        VARCHAR(30)  DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  Données initiales
-- ============================================================

-- Utilisateurs (mots de passe en clair — à hasher en production)
INSERT INTO utilisateur (nom, prenom, email, password, role, departement, avatar) VALUES
('Koné',      'Ibrahim', 'i.kone@societe.ci',       'admin123',  'admin',            'Direction Générale', 'IK'),
('Coulibaly', 'Adjoua',  'a.coulibaly@societe.ci',   'agent123',  'agent_logistique', 'Logistique',         'AC'),
('Traoré',    'Bamba',   'b.traore@societe.ci',      'consul123', 'consultant',       'Commercial',         'BT');

-- Véhicules
INSERT INTO vehicule (numero_inventaire, marque, modele, annee, immatriculation, vin,
  carburant, transmission, couleur, kilometrage, statut, categorie, departement,
  date_achat, prix_achat, km_achat, date_mise_service, date_hors_service,
  assurance_num, assurance_debut, assurance_fin, assurance_compagnie,
  visite_technique_date, visite_technique_echeance, carte_grise_num,
  prochain_entretien_km, image) VALUES
('VH-2022-001','Peugeot','308',2022,'AB-1234-CI','VF3LC9HZ0LW123456',
  'Diesel','Manuelle','Gris Platine',42500,'Disponible','Berline','Direction Générale',
  '15/03/2022',14500000,0,'20/03/2022',NULL,
  'ASS-2024-10234','01/01/2025','31/12/2025','NSIA Assurances CI',
  '10/01/2025','10/01/2026','CG-AB-2022-1234',45000,
  'https://images.unsplash.com/photo-1770385434759-d1e7799e2239?w=400'),

('VH-2023-002','Toyota','Hilux',2023,'BY-5678-CI','JTFST13V80D123456',
  'Diesel','Automatique','Blanc',18200,'En mission','Pick-up','Technique',
  '05/02/2023',22000000,0,'10/02/2023',NULL,
  'ASS-2024-10890','01/01/2025','31/05/2025','SUNU Assurances CI',
  '15/03/2025','15/03/2026','CG-BY-2023-5678',20000,
  'https://images.unsplash.com/photo-1758219944301-48c0bac14d21?w=400'),

('VH-2021-003','Renault','Kangoo',2021,'CV-9012-CI','VF1FC1C0H45123456',
  'Diesel','Manuelle','Blanc Glacé',67800,'En réparation','Utilitaire','Logistique',
  '20/07/2021',9500000,0,'25/07/2021',NULL,
  'ASS-2024-11002','01/01/2025','31/12/2025','Allianz Côte d\'Ivoire',
  '05/06/2024','05/06/2025','CG-CV-2021-9012',70000,
  'https://images.unsplash.com/photo-1649553324097-dec0c232be42?w=400'),

('VH-2020-004','Toyota','Land Cruiser',2020,'DK-3456-CI','UU1HSDB1L12345678',
  'Diesel','Automatique','Gris Minéral',89300,'Disponible','SUV','Commercial',
  '12/04/2020',32000000,0,'15/04/2020',NULL,
  'ASS-2024-10456','01/01/2025','31/12/2025','NSIA Assurances CI',
  '20/02/2025','20/02/2026','CG-DK-2020-3456',90000,
  'https://images.unsplash.com/photo-1653325189816-5d8dc746cc16?w=400'),

('VH-2023-005','Volkswagen','Caddy',2023,'EM-7890-CI','WV2ZZZ2KZLT123456',
  'Diesel','Automatique','Noir Profond',11400,'Affecté','Utilitaire','Direction Générale',
  '08/08/2023',16500000,0,'12/08/2023',NULL,
  'ASS-2024-12345','01/01/2025','31/12/2025','SUNU Assurances CI',
  '18/04/2025','18/04/2026','CG-EM-2023-7890',15000,
  'https://images.unsplash.com/photo-1765597119545-6b481bd14d37?w=400'),

('VH-2018-006','Ford','Transit',2018,'FN-1111-CI','WF0XXXTTGXGT12345',
  'Diesel','Manuelle','Blanc',134600,'Hors service','Fourgon','Logistique',
  '20/06/2018',19000000,0,'25/06/2018','15/03/2025',
  '—','—','—','—',
  '—','—','CG-FN-2018-1111',0,
  'https://images.unsplash.com/photo-1650452233063-8f308616b729?w=400');

-- Missions
INSERT INTO mission (id, vehicule_id, vehicule, immatriculation, employe, employe_matricule,
  departement, destination, motif, date_depart, date_retour, km_depart, km_retour,
  statut, ordre_mission, chauffeur) VALUES
('OM-2025-0042',2,'Toyota Hilux','BY-5678-CI','Kouassi Bamba','EMP-0234',
  'Technique','San-Pédro','Inspection chantier — projet d\'infrastructure portuaire',
  '07/05/2025','10/05/2025',17800,NULL,'En cours','OM-2025-0042','Diallo Sékou'),

('OM-2025-0041',1,'Peugeot 308','AB-1234-CI','Aya Koné','EMP-0118',
  'Commercial','Bouaké','Réunion commerciale — partenariat distribution régionale',
  '05/05/2025','06/05/2025',41200,42150,'Terminée','OM-2025-0041','Aya Koné'),

('OM-2025-0043',4,'Toyota Land Cruiser','DK-3456-CI','Ibrahim Diallo','EMP-0305',
  'RH','Yamoussoukro','Formation — programme développement des compétences',
  '12/05/2025','14/05/2025',89300,NULL,'Planifiée','OM-2025-0043','Ibrahim Diallo'),

('OM-2025-0039',4,'Toyota Land Cruiser','DK-3456-CI','Fatou Coulibaly','EMP-0087',
  'Finance','Daloa','Audit comptable — filiale Ouest',
  '28/04/2025','30/04/2025',87900,89100,'Terminée','OM-2025-0039','Koné Alphonse'),

('OM-2025-0040',1,'Peugeot 308','AB-1234-CI','Pierre Koffi','EMP-0145',
  'Direction Générale','Abidjan — Siège social','Réunion comité de direction — bilan trimestriel',
  '02/05/2025','02/05/2025',40800,41200,'Terminée','OM-2025-0040','Pierre Koffi'),

('OM-2025-0044',3,'Renault Kangoo','CV-9012-CI','Service Logistique','DEPT-LOG',
  'Logistique','Korhogo','Livraison équipements — entrepôt régional Nord',
  '14/05/2025','15/05/2025',67800,NULL,'Planifiée','OM-2025-0044','Bamba Oumar');

-- Affectations
INSERT INTO affectation (id, vehicule_id, vehicule, immatriculation, employe, employe_matricule,
  fonction, departement, date_debut, date_fin, km_debut, km_actuel, statut, motif, vehicule_image) VALUES
('AFF-2025-001',5,'Volkswagen Caddy','EM-7890-CI','Koné Ibrahim','EMP-0001',
  'Directeur Général','Direction Générale','01/01/2025',NULL,9800,11400,'Active',
  'Véhicule de fonction — Directeur Général',
  'https://images.unsplash.com/photo-1765597119545-6b481bd14d37?w=400'),

('AFF-2025-002',4,'Toyota Land Cruiser','DK-3456-CI','Traoré Adjoua','EMP-0198',
  'Responsable RH','Ressources Humaines','15/01/2025',NULL,82000,89300,'Active',
  'Véhicule de service permanent — zone Nord',
  'https://images.unsplash.com/photo-1653325189816-5d8dc746cc16?w=400'),

('AFF-2024-009',1,'Peugeot 308','AB-1234-CI','Coulibaly Aminata','EMP-0022',
  'DRH','Ressources Humaines','01/09/2024','28/02/2025',31000,42500,'Terminée',
  'Véhicule de service temporaire',
  'https://images.unsplash.com/photo-1770385434759-d1e7799e2239?w=400'),

('AFF-2024-007',4,'Toyota Land Cruiser','DK-3456-CI','Koffi Oumar','EMP-0067',
  'Directeur Commercial','Commercial','01/03/2024','31/12/2024',51200,78900,'Terminée',
  'Véhicule de service — tournées commerciales',
  'https://images.unsplash.com/photo-1653325189816-5d8dc746cc16?w=400');

-- Entretiens (pièces séparées par |)
INSERT INTO entretien (id, vehicule, immatriculation, numero_inv, type, nature, description,
  garage, adresse_garage, technicien, date_entree, date_sortie_prevue, date_sortie_reelle,
  cout_estime, cout_reel, statut, priorite, km_entree, pieces, bon_commande) VALUES
('ENT-2025-018','Renault Kangoo','CV-9012-CI','VH-2021-003',
  'Réparation moteur','Curatif',
  'Remplacement joint de culasse — surchauffe signalée par le chauffeur',
  'Renault Service Abidjan','Zone Industrielle de Yopougon, Abidjan','Koné Sylvain',
  '07/05/2025','14/05/2025',NULL,500000,-1,'En cours','Haute',67800,
  'Joint de culasse|Liquide de refroidissement 5L|Joints d\'étanchéité kit',
  'BC-2025-0234'),

('ENT-2025-017','Peugeot 308','AB-1234-CI','VH-2022-001',
  'Vidange + filtres','Préventif',
  'Vidange huile 5W40, filtre huile, filtre air, filtre habitacle — révision à 40 000 km',
  'Peugeot Service Abidjan','Avenue Houphouët-Boigny, Abidjan','Kouassi Derrick',
  '07/03/2025','07/03/2025','07/03/2025',110000,105000,'Terminé','Normale',40000,
  'Huile moteur 5W40 5L|Filtre huile|Filtre air|Filtre habitacle',
  'BC-2025-0198'),

('ENT-2025-019','Toyota Hilux','BY-5678-CI','VH-2023-002',
  'Révision à 20 000 km','Préventif',
  'Révision constructeur à 20 000 km — inspection complète + vidange',
  'Toyota Center Abidjan','Route de Bingerville, Abidjan','Diallo Ousmane',
  '15/05/2025','16/05/2025',NULL,280000,-1,'Planifié','Normale',20000,
  'Huile moteur diesel 5W30 7L|Filtre huile|Filtre carburant|Filtre air',
  'BC-2025-0241'),

('ENT-2025-015','Toyota Land Cruiser','DK-3456-CI','VH-2020-004',
  'Freins — remplacement','Curatif',
  'Usure excessive des plaquettes avant — remplacement disques et plaquettes',
  'Toyota Center Abidjan','Route de Bingerville, Abidjan','Traoré Ibrahima',
  '15/04/2025','16/04/2025','16/04/2025',220000,235000,'Terminé','Haute',87200,
  'Plaquettes frein avant x2|Disques frein avant x2|Liquide de frein',
  'BC-2025-0187');
