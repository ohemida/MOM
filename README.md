# Protokoll: Lager-Kommunikation mit Apache Kafka

**Name:** Hemida Omar

**Datum:** 02.12.2025

## 1. Was wurde gemacht? 

### 1.1 Starten der Systeme

Zuerst habe ich die Infrastruktur mit Docker gestartet:

```bash
docker-compose up -d

```

Danach habe ich die beiden Programme gestartet:

1. **Die Zentrale** (Empfänger): Läuft auf Port **8081**.
2. **Das Lager** (Sender): Läuft auf Port **8080**.

### 1.2 Daten vom Lager an die Zentrale schicken

Wenn ein Lager seine Bestände meldet, passiert folgendes:

1. Das Lager schickt die Daten per HTTP-Befehl ab:

```bash
curl -X POST http://localhost:8080/warehouse/send \
     -d '{"warehouseId": "Wien-Nord", "quantity": 500}'

```

2. Die Nachricht geht über Kafka an die Zentrale.
3. Die Zentrale schickt sofort ein **"SUCCESS"** zurück an das Lager.
4. Alle empfangenen Daten werden automatisch in einer Textdatei (`warehouse_log.txt`) mitgeschrieben.

### 1.3 Alle Daten in der Zentrale abrufen

Man kann sich in der Zentrale jederzeit eine Liste aller gemeldeten Lagerstände ansehen:

* **Als Liste (JSON):** `http://localhost:8081/central/stock`
* **Als Dokument (XML):** `http://localhost:8081/central/stock.xml`

---

## 2. Fragen & Antworten

**1. Nennen Sie 4 Eigenschaften von Message Oriented Middleware (MOM):**

* **Asynchron:** Der Sender muss nicht warten, bis der Empfänger fertig ist.
* **Lose Kopplung:** Die Programme müssen sich nicht direkt kennen.
* **Speichern:** Nachrichten gehen nicht verloren, wenn der Empfänger kurz offline ist.
* **Skalierbar:** Man kann einfach weitere Lager oder Empfänger hinzufügen.

**2. Was ist synchrone und transiente Kommunikation?**

* **Synchron:** Wie ein Telefonat – beide müssen gleichzeitig Zeit haben und einer wartet auf die Antwort des anderen.
* **Transient:** Die Nachricht wird nicht gespeichert. Wenn der Empfänger nicht da ist, ist die Nachricht weg.

**3. Wie funktioniert eine JMS Queue?**

Eine Queue arbeitet nach dem Prinzip **"Einer an Einen"**. Eine Nachricht wird in die Schlange gestellt und von genau einem Empfänger abgeholt. Danach ist die Nachricht weg.

**4. Was sind die wichtigsten JMS-Bausteine?**

* **ConnectionFactory:** Erstellt die Verbindung zum System.
* **Connection & Session:** Der "Kanal", über den geredet wird.
* **Destination:** Das Ziel (Queue oder Topic).
* **Producer:** Derjenige, der die Nachricht schreibt.
* **Consumer:** Derjenige, der die Nachricht liest.

**5. Wie funktioniert ein JMS Topic?**

Ein Topic arbeitet nach dem Prinzip **"Einer an Viele"**. Wie bei einem Radio-Sender: Ein Sender schickt die Nachricht raus, und alle, die zuhören (Abonnenten), bekommen sie gleichzeitig.

**6. Was ist ein "lose gekoppeltes" System?**
Das bedeutet, dass die Teile eines Systems unabhängig voneinander funktionieren.

* **Beispiel:** Eine Bestellung im Online-Shop. Ich gebe die Bestellung auf (Sender). Der Verpacker im Lager (Empfänger) sieht das vielleicht erst Stunden später.
* **Warum "lose"?** Weil es egal ist, wann der Empfänger arbeitet oder welche Technik er nutzt – die Nachricht (Bestellung) wartet einfach in der Mitte, bis sie gebraucht wird.
