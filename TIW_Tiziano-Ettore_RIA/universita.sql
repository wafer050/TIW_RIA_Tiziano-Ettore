-- Database Creation and Table Reset
DROP DATABASE IF EXISTS `universita`;
CREATE DATABASE IF NOT EXISTS `universita` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;

USE `universita`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Tabella docente
DROP TABLE IF EXISTS `docente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `docente` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `nome` VARCHAR(45) NOT NULL,
  `cognome` VARCHAR(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella studente
DROP TABLE IF EXISTS `studente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studente` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `nome` VARCHAR(45) NOT NULL,
  `cognome` VARCHAR(45) NOT NULL,
  `matricola` INT(6) NOT NULL,
  `mail` VARCHAR(45) NOT NULL,
  `corso_di_laurea` VARCHAR(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella corso
DROP TABLE IF EXISTS `corso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `corso` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nome` VARCHAR(45) NOT NULL,
  `id_docente` INT NOT NULL,
  FOREIGN KEY (`id_docente`) REFERENCES `docente`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella partecipazione
DROP TABLE IF EXISTS `partecipazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partecipazione` (
  `id_studente` INT NOT NULL,
  `id_corso` INT NOT NULL,
  PRIMARY KEY (`id_studente`, `id_corso`),
  FOREIGN KEY (`id_studente`) REFERENCES `studente`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION,
  FOREIGN KEY (`id_corso`) REFERENCES `corso`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella appello
DROP TABLE IF EXISTS `appello`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appello` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `data` DATE NOT NULL,
  `id_corso` INT NOT NULL,
  FOREIGN KEY (`id_corso`) REFERENCES `corso`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella verbale
DROP TABLE IF EXISTS `verbale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verbale` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `data_creazione` DATE NOT NULL,
  `ora_creazione` TIME NOT NULL,  -- Rimosso il valore di default
  `id_appello` INT NOT NULL,
  FOREIGN KEY (`id_appello`) REFERENCES `appello`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella verbalizzazione
DROP TABLE IF EXISTS `verbalizzazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verbalizzazione` (
  `id_studente` INT NOT NULL,
  `id_verbale` INT NOT NULL,
  PRIMARY KEY (`id_studente`, `id_verbale`),
  FOREIGN KEY (`id_studente`) REFERENCES `studente`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION,
  FOREIGN KEY (`id_verbale`) REFERENCES `verbale`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Tabella esito
DROP TABLE IF EXISTS `esito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `esito` (
  `id_studente` INT NOT NULL,
  `id_appello` INT NOT NULL,
  `voto` VARCHAR(20) NOT NULL,
  `stato_di_valutazione` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id_studente`, `id_appello`),
  FOREIGN KEY (`id_studente`) REFERENCES `studente`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION,
  FOREIGN KEY (`id_appello`) REFERENCES `appello`(`id`) ON UPDATE CASCADE ON DELETE NO ACTION,
  CONSTRAINT `chk_voto` CHECK (
    `voto` IN ('', 'assente', 'rimandato', 'riprovato', 
               '18', '19', '20', '21', '22', '23', '24',
               '25', '26', '27', '28', '29', '30', '30 e lode')
  ),
  CONSTRAINT `chk_stato_di_valutazione` CHECK (
    `stato_di_valutazione` IN ('non inserito', 'inserito', 'pubblicato', 'rifiutato', 'verbalizzato')
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Inserimento dati nella tabella docente
LOCK TABLES `docente` WRITE;
/*!40000 ALTER TABLE `docente` DISABLE KEYS */;
INSERT INTO `docente` (`username`, `password`, `nome`, `cognome`)
VALUES 
('mrossi', 'password123', 'Mario', 'Rossi'),
('lverdi', 'password456', 'Laura', 'Verdi'),
('gsimone', 'password789', 'Giuseppe', 'Simone'),
('docente', 'docente', 'Piero', 'Fraternali');
/*!40000 ALTER TABLE `docente` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella studente
LOCK TABLES `studente` WRITE;
/*!40000 ALTER TABLE `studente` DISABLE KEYS */;
INSERT INTO `studente` (`username`, `password`, `nome`, `cognome`, `matricola`, `mail`, `corso_di_laurea`)
VALUES
('a', 'pw', 'Alessandro', 'Bianchi', 123455, 'alessandro.bianchi@example.com', 'Informatica'),
('b654321', 'password456', 'Beatrice', 'Rossi', 654321, 'beatrice.rossi@example.com', 'Matematica'),
('c789012', 'password789', 'Carlo', 'Verdi', 789012, 'carlo.verdi@example.com', 'Fisica'),
('studente', 'studente', 'Tiziano', 'Rossi', 123456, 'tiziano.rossi@example.com', 'Informatica'),
('s000005', 'password005', 'Francesco', 'Verdi', 123457, 'francesco.verdi@example.com', 'Informatica'),
('s000006', 'password006', 'Luca', 'Bianchi', 123458, 'luca.bianchi@example.com', 'Informatica'),
('s000007', 'password007', 'Giulia', 'Rossi', 123459, 'giulia.rossi@example.com', 'Informatica'),
('s000008', 'password008', 'Marco', 'Neri', 123460, 'marco.neri@example.com', 'Informatica'),
('s000009', 'password009', 'Sara', 'Lombardi', 123461, 'sara.lombardi@example.com', 'Informatica'),
('voto non inserito', 'pw', 'Matteo', 'Ferrari', 123462, 'voto.non.inserito@example.com', 'Informatica'),
('voto inserito', 'pw', 'Elisa', 'Conti', 123463, 'voto.inserito@example.com', 'Informatica'),
('voto pubblicato', 'pw', 'Davide', 'Romano', 123464, 'voto.pubblicato@example.com', 'Informatica'),
('voto rifiutato', 'pw', 'Chiara', 'Esposito', 123465, 'voto.rifiutato@example.com', 'Informatica'),
('voto verbalizzato', 'pw', 'Simone', 'Galli', 123466, 'voto.verbalizzato@example.com', 'Informatica'),
('voto rimandato', 'pw', 'Elena', 'De Luca', 123467, 'voto.rimandato@example.com', 'Informatica'),
('voto assente', 'pw', 'Nicola', 'Mariani', 123468, 'voto.assente@example.com', 'Informatica'),
('voto riprovato', 'pw', 'Ilaria', 'Barbieri', 123469, 'voto.riprovato@example.com', 'Informatica'),
('18','pw','Marco','Bianchi', 123470,'18@example.com','Informatica'),
('19','pw','Lucia','Rossi', 123471,'19@example.com','Informatica'),
('20','pw','Andrea','Greco', 123472,'20@example.com','Informatica'),
('21','pw','Federica','Moretti', 123473,'21@example.com','Informatica');
/*!40000 ALTER TABLE `studente` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella corso
LOCK TABLES `corso` WRITE;
/*!40000 ALTER TABLE `corso` DISABLE KEYS */;
INSERT INTO `corso` (`nome`, `id_docente`)
VALUES
('Analisi Matematica', 1),  -- Corso tenuto da Mario Rossi
('Fisica I', 2),            -- Corso tenuto da Laura Verdi
('Programmazione 1', 3),    -- Corso tenuto da Giuseppe Simone
('TIW', 4),				-- Corso tenuto da Piero Fraternali
('Reti di Calcolatori e Internet', 4),
('Protocolli e Architetture di Rete', 4);
/*!40000 ALTER TABLE `corso` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella partecipazione
LOCK TABLES `partecipazione` WRITE;
/*!40000 ALTER TABLE `partecipazione` DISABLE KEYS */;
INSERT INTO `partecipazione` (`id_studente`, `id_corso`)
VALUES
(1, 1),  -- Alessandro partecipa ad Analisi Matematica
(1, 3),  -- Alessandro partecipa a Programmazione 1
(2, 1),  -- Beatrice partecipa ad Analisi Matematica
(2, 2),  -- Beatrice partecipa a Fisica I
(3, 2),  -- Carlo partecipa a Fisica I
(3, 3),  -- Carlo partecipa a Programmazione 1
(4, 4),  -- Tiziano partecipa a TIW
(5, 4),  -- Francesco partecipa a TIW (ID corso = 4)
(6, 4),  -- Luca partecipa a TIW (ID corso = 4)
(7, 4),  -- Giulia partecipa a TIW (ID corso = 4)
(8, 4),  -- Marco partecipa a TIW (ID corso = 4)
(9, 4),  -- Sara partecipa a TIW (ID corso = 4)
(10, 4),
(11, 4),
(12, 4),
(13, 4),
(14, 4),
(15, 4),
(16, 4),
(17, 4),
(18, 4),
(19, 4),
(20, 5),
(21, 6);

/*!40000 ALTER TABLE `partecipazione` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella appello
LOCK TABLES `appello` WRITE;
/*!40000 ALTER TABLE `appello` DISABLE KEYS */;
INSERT INTO `appello` (`data`, `id_corso`)
VALUES
('2025-06-10', 1),  -- Appello per il corso di Analisi Matematica
('2025-06-15', 2),  -- Appello per il corso di Fisica I
('2025-06-20', 3),  -- Appello per il corso di Programmazione 1
('2025-06-30', 4),  -- Appello per il corso TIW (ID corso = 4)
('2025-07-05', 4),  -- Secondo appello per TIW (ID corso = 4)
('2025-09-01', 4),  -- Terzo appello per TIW (ID corso = 4)
('2025-06-10', 5),
('2025-06-11', 6),
('2025-05-29', 3); 	-- 9
/*!40000 ALTER TABLE `appello` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella verbale
LOCK TABLES `verbale` WRITE;
/*!40000 ALTER TABLE `verbale` DISABLE KEYS */;
INSERT INTO `verbale` (`data_creazione`, `ora_creazione`, `id_appello`)
VALUES
('2025-06-10', '14:00:00', 1),  -- Verbale per l'appello di Analisi Matematica
('2025-06-15', '15:30:00', 2),  -- Verbale per l'appello di Fisica I
('2025-06-20', '16:00:00', 3),  -- Verbale per l'appello di Programmazione 1
('2025-07-01', '10:00:00', 4),  -- Verbale per l'appello di TIW
('2025-06-30', '09:00:00', 4),  -- Verbale per l'appello del 30 giugno 
('2025-07-05', '11:00:00', 5),  -- Verbale per l'appello del 5 luglio 
('2026-01-01','00:00:00', 6),
('2026-01-01','00:00:01', 6);
/*!40000 ALTER TABLE `verbale` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella verbalizzazione
LOCK TABLES `verbalizzazione` WRITE;
/*!40000 ALTER TABLE `verbalizzazione` DISABLE KEYS */;
INSERT INTO `verbalizzazione` (`id_studente`, `id_verbale`)
VALUES
(1, 1),  -- Alessandro ha avuto il verbale per Analisi Matematica
(2, 2),  -- Beatrice ha avuto il verbale per Fisica I
(3, 3),  -- Carlo ha avuto il verbale per Programmazione 1
(4, 4),  -- Tiziano ha avuto il verbale per TIW 
(5, 5),  -- Francesco ha ricevuto il verbale per l'appello del 30 giugno
(6, 6),  -- Luca ha ricevuto il verbale per l'appello del 5 luglio
(7, 5),  -- Giulia ha ricevuto il verbale per l'appello del 30 giugno
(9, 6),  -- Sara ha ricevuto il verbale per l'appello del 5 luglio
(14, 7), -- voto verbalizzato
(15, 8); -- voto rimandato
/*!40000 ALTER TABLE `verbalizzazione` ENABLE KEYS */;
UNLOCK TABLES;

-- Inserimento dati nella tabella esito
LOCK TABLES `esito` WRITE;
/*!40000 ALTER TABLE `esito` DISABLE KEYS */;
INSERT INTO `esito` (`id_studente`, `id_appello`, `voto`, `stato_di_valutazione`)
VALUES
(1, 1, '30', 'verbalizzato'),  -- Alessandro ha preso 30 in Analisi Matematica
(1, 3, '22', 'pubblicato'),	   -- Alessandro ha preso 22 in Programmazione 1
(2, 2, '28', 'verbalizzato'),  -- Beatrice ha preso 28 in Fisica I
(3, 3, '26', 'verbalizzato'),  -- Carlo ha preso 26 in Programmazione 1
(4, 4, '30', 'verbalizzato'),  -- Tiziano ha ricevuto il voto 30 nell'appello di TIW
(5, 4, '25', 'verbalizzato'),  -- Francesco ha preso 25 nell'appello del 30 giugno
(6, 5, '30 e lode', 'verbalizzato'),  -- Luca ha preso 30 e lode nell'appello del 5 luglio
(7, 4, '18', 'verbalizzato'),  -- Giulia ha preso 18 nell'appello del 30 giugno
(8, 4, 'assente', 'verbalizzato'),  -- Marco non ha partecipato all'appello del 30 giugno
(9, 5, '28', 'verbalizzato'),  -- Sara ha preso 28 nell'appello del 5 luglio
(10, 6, '', 'non inserito'), 
(11, 6, '23', 'inserito'), 
(12, 6, '27', 'pubblicato'), 
(13, 6, '18', 'rifiutato'), 
(14, 6, '21', 'verbalizzato'), 
(15, 6, 'rimandato', 'verbalizzato'), 
(16, 6, 'assente', 'inserito'), 
(17, 6, 'riprovato', 'pubblicato'),
(18, 6, '', 'non inserito'), 
(19, 6, '', 'non inserito'), 
(20, 7, '', 'non inserito'), 
(21, 8, '', 'non inserito'),
(1 , 9, '', 'non inserito');
/*!40000 ALTER TABLE `esito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'dbtest'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;



-- creazione view

CREATE VIEW vw_corsi_docente AS
SELECT 
    c.id AS corso_id,
    c.nome AS corso_nome,
    d.nome AS docente_nome,
    d.id AS docente_id,
    d.cognome AS docente_cognome
FROM 
    corso c
JOIN 
    docente d ON c.id_docente = d.id
ORDER BY 
    c.nome DESC;
    

CREATE VIEW vw_appelli_corso AS
SELECT 
    a.id AS appello_id,
    a.data AS appello_data,
    c.id AS corso_id,
    c.nome AS corso_nome
FROM 
    appello a
JOIN 
    corso c ON a.id_corso = c.id
ORDER BY 
    a.data DESC;
    
/*
CREATE VIEW vw_studenti_iscritti_appello AS
SELECT 
    s.id AS studente_id,
    s.matricola AS studente_matricola,
    s.cognome AS studente_cognome,
    s.nome AS studente_nome,
    s.mail AS studente_mail,
    s.corso_di_laurea AS studente_corso_di_laurea,
    a.id AS appello_id,
    a.data AS appello_data,
    c.id AS corso_id,
    c.nome AS corso_nome,
    e.voto AS esito_voto,
    e.stato_di_valutazione AS esito_stato_di_valutazione
FROM 
    studente s
JOIN 
    partecipazione p ON s.id = p.id_studente
JOIN 
    corso c ON p.id_corso = c.id
JOIN 
    appello a ON c.id = a.id_corso
LEFT JOIN 
    esito e ON s.id = e.id_studente AND a.id = e.id_appello;
-- ordinamento è personalizzato dalla servlet
*/

CREATE VIEW vw_studenti_iscritti_appello AS
SELECT 
    s.id AS studente_id,
    s.matricola AS studente_matricola,
    s.cognome AS studente_cognome,
    s.nome AS studente_nome,
    s.mail AS studente_mail,
    s.corso_di_laurea AS studente_corso_di_laurea,
    a.id AS appello_id,
    a.data AS appello_data,
    c.id AS corso_id,
    c.nome AS corso_nome,
    e.voto AS esito_voto,
    e.stato_di_valutazione AS esito_stato_di_valutazione
FROM 
    studente s
JOIN 
    partecipazione p ON s.id = p.id_studente
JOIN 
    corso c ON p.id_corso = c.id
JOIN 
    appello a ON c.id = a.id_corso
JOIN 
    esito e ON s.id = e.id_studente AND a.id = e.id_appello;



CREATE VIEW vw_esito_studente_appello AS
SELECT 
    s.id AS studente_id,
    s.matricola AS studente_matricola,
    s.cognome AS studente_cognome,
    s.nome AS studente_nome,
    s.mail AS studente_mail,
    s.corso_di_laurea AS studente_corso_di_laurea,
    e.voto AS esito_voto,
    e.stato_di_valutazione AS esito_stato,
    a.id AS appello_id,
    a.data AS appello_data,
    c.id AS corso_id,
    c.nome AS corso_nome
FROM 
    studente s
JOIN 
    esito e ON s.id = e.id_studente
JOIN 
    appello a ON e.id_appello = a.id
JOIN 
    corso c ON a.id_corso = c.id;


CREATE VIEW vw_verbali_docente AS
SELECT 
    v.id AS verbale_id,
    v.data_creazione AS verbale_data_creazione,
    v.ora_creazione AS verbale_ora_creazione,
    a.id AS appello_id,
    a.data AS appello_data,
    c.id AS corso_id,
    c.nome AS corso_nome,
    d.id AS docente_id,
    d.nome AS docente_nome,
    d.cognome AS docente_cognome
FROM 
    verbale v
JOIN 
    appello a ON v.id_appello = a.id
JOIN 
    corso c ON a.id_corso = c.id
JOIN 
    docente d ON c.id_docente = d.id
ORDER BY 
    c.nome ASC, a.data ASC;



CREATE VIEW vw_verbale_appello_corso AS
SELECT 
    v.id AS verbale_id,
    v.data_creazione AS verbale_data,
    v.ora_creazione AS verbale_ora,
    a.id AS appello_id,
    a.data AS appello_data,
    c.id AS corso_id,
    c.nome AS corso_nome
FROM 
    verbale v
JOIN 
    appello a ON v.id_appello = a.id
JOIN 
    corso c ON a.id_corso = c.id;


CREATE VIEW vw_corsi_studente AS
SELECT 
    s.id AS studente_id,
    s.nome AS studente_nome,
    s.cognome AS studente_cognome,
    c.id AS corso_id,
    c.nome AS corso_nome
FROM 
    studente s
JOIN 
    partecipazione p ON s.id = p.id_studente
JOIN 
    corso c ON p.id_corso = c.id
ORDER BY 
    c.nome DESC;
    
CREATE VIEW vw_verbale_completo AS
SELECT 
  ve.id AS verbale_id,
  s.id AS studente_id,
  s.nome AS studente_nome,
  s.cognome AS studente_cognome,
  s.matricola AS studente_matricola,
  e.voto AS esame_voto,
  e.id_appello AS appello_id
FROM verbalizzazione v
JOIN studente s ON v.id_studente = s.id
JOIN verbale ve ON v.id_verbale = ve.id
JOIN esito e ON e.id_studente = s.id AND e.id_appello = ve.id_appello;


