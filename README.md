# Protokoll: Durchführung und Fragestellungen

**Name:** Hemida Omar
**Datum:** 02.12.2025

## 1. Durchführung und Tests

### 1.1 Starten der Umgebung
Zuerst muss die Infrastruktur (Zookeeper und Kafka) mittels Docker gestartet werden:

```bash
docker-compose up -d
```

Anschließend werden die beiden Spring-Boot-Instanzen gestartet. Durch die Konfiguration in der `application.yml` und die Nutzung von Spring Profiles laufen diese auf unterschiedlichen Ports:

1.  **Consumer (Zentrale)** starten: `ConsumerMain` (läuft auf Port **8081**).
2.  **Producer (Lager)** starten: `ProducerMain` (läuft auf Port **8080**).

### 1.2 Szenario: Datenübertragung (Lager -> Zentrale)
Ein Lagerstandort sendet Bestandsdaten an die Message Queue. Da der Producer auf Port 8080 läuft, wird der Request dorthin gesendet:

```bash
curl -X POST http://localhost:8080/warehouse/send \
     -H "Content-Type: application/json" \
     -d '{"warehouseId": "Wien-Hauptbahnhof", "quantity": 150}'
```

**Erwartetes Verhalten:**
*   **Producer-Konsole:** Zeigt den Versand der Nachricht und empfängt kurz darauf die Bestätigung (`SUCCESS...`) über das Rückkanal-Topic.
*   **Consumer-Konsole:** Zeigt den Empfang der Nachricht an.
*   **Log-Datei:** Im Projektordner wird automatisch eine Datei `warehouse_log.txt` erstellt/aktualisiert, die den JSON-Datensatz enthält.

### 1.3 Abruf der aggregierten Daten (Zentrale)
Die Zentrale (Port 8081) sammelt alle eingehenden Nachrichten. Diese können nun über die REST-Schnittstelle abgerufen werden.

**Abruf als JSON:**
```bash
curl http://localhost:8081/central/stock
```
*Output:* `[{"warehouseId":"Wien-Hauptbahnhof","quantity":150,"timestamp":"..."}]`

**Abruf als XML:**
```bash
curl http://localhost:8081/central/stock.xml
```

---

## 2. Beantwortung der Fragestellungen

**1. Nennen Sie mindestens 4 Eigenschaften der Message Oriented Middleware?**
1.  **Asynchronität:** Der Sender sendet die Nachricht ab und arbeitet sofort weiter, ohne auf die Antwort des Empfängers warten zu müssen.
2.  **Lose Kopplung:** Sender und Empfänger sind voneinander entkoppelt (kennen sich nicht direkt, laufen auf unterschiedlichen Systemen/Plattformen).
3.  **Persistenz (Zuverlässigkeit):** Nachrichten werden vom Broker zwischengespeichert ("Store and Forward"). Fällt der Empfänger aus, wird die Nachricht zugestellt, sobald er wieder online ist.
4.  **Skalierbarkeit:** Es können einfach weitere Consumer hinzugefügt werden, um die Last zu verteilen (Load Balancing).

**2. Was versteht man unter einer transienten und synchronen Kommunikation?**
*   **Synchron:** Der Sender blockiert und wartet ("handshake"), bis der Empfänger die Nachricht verarbeitet und geantwortet hat (z.B. klassischer HTTP Request oder Telefonanruf).
*   **Transient:** Die Nachricht wird nicht dauerhaft gespeichert. Ist der Empfänger zum Zeitpunkt des Sendens nicht erreichbar oder fällt das Netzwerk aus, geht die Nachricht verloren (Gegenteil von persistenter Kommunikation).

**3. Beschreiben Sie die Funktionsweise einer JMS Queue?**
Eine Queue implementiert das **Point-to-Point (P2P)** Modell.
*   Eine Nachricht wird von einem Sender in die Queue gelegt.
*   Sie wird von genau **einem** Empfänger abgeholt und verarbeitet.
*   Danach wird sie aus der Queue entfernt (Acknowledge).
*   Dies eignet sich gut für Load Balancing (Verteilung von Aufgaben auf mehrere Worker).

**4. JMS Overview - Beschreiben Sie die wichtigsten JMS Klassen und deren Zusammenhang?**
*   **ConnectionFactory:** Fabrik zum Erstellen einer Verbindung zum Message Broker.
*   **Connection:** Eine aktive TCP/IP-Verbindung zum Provider.
*   **Session:** Ein Thread-Kontext zum Senden und Empfangen von Nachrichten.
*   **Destination:** Das Ziel der Nachricht (entweder Queue oder Topic).
*   **MessageProducer:** Objekt zum Senden von Nachrichten an eine Destination.
*   **MessageConsumer:** Objekt zum Empfangen von Nachrichten von einer Destination.

**5. Beschreiben Sie die Funktionsweise eines JMS Topic?**
Ein Topic implementiert das **Publish-Subscribe (Pub/Sub)** Modell.
*   Eine Nachricht wird an ein Topic gesendet ("Publish").
*   **Alle** Consumer, die dieses Topic abonniert haben ("Subscribe"), erhalten eine Kopie der Nachricht.
*   Vergleichbar mit einem Radiosender (Einer sendet, viele hören zu).

**6. Was versteht man unter einem lose gekoppelten verteilten System? Nennen Sie ein Beispiel dazu. Warum spricht man hier von lose?**
*   **Definition:** Ein System, bei dem die Komponenten so wenig wie möglich voneinander wissen müssen, um miteinander zu interagieren.
*   **Beispiel:** Ein E-Mail-System. Ich sende eine Mail an den Server. Ich muss nicht wissen, welchen Computer mein Empfänger nutzt, welches Betriebssystem er hat oder ob er gerade online ist.
*   **Warum "lose"?**
    *   *Zeitlich:* Sender und Empfänger müssen nicht gleichzeitig laufen.
    *   *Räumlich:* Die Komponenten kennen nur die Adresse der Middleware, nicht die IP des Partners.
    *   *Technologisch:* Ein Java-Programm kann Nachrichten an ein Python-Programm senden, solange das Datenformat (z.B. JSON) vereinbart ist.