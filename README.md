# Smart-Home Simulation & Manager

Eine terminalbasierte Anwendung zur Simulation und Verwaltung verschiedener IoT-Geräte. Benutzer können Geräte anlegen, deren Werte auslesen und steuerbare Geräte bedienen. Zusätzlich reagiert eine automatisierte Logik auf simulierte Sensorwerte und löst passende Aktionen aus.

Die Simulation läuft unabhängig von der Konsoleneingabe: Auf einem eigenen Thread wird **jede Sekunde ein Aktualisierungsframe eingeplant**. Zwischen diesen Aktualisierungen werden möglichst viele Aktionsframes verarbeitet; ein bereits laufender Aktionsframe wird zuerst beendet. Der aktuelle Zustand kann in einer JSON-Datei gespeichert und bei einem Neustart wieder geladen werden.

## Muss-Anforderungen

- Verschiedene Arten von IoT-Geräten anlegen und verwalten können.
- Daten einzelner Geräte (z. B. eines Luftfeuchtigkeitssensors) auslesen können.
- Daten aller Geräte anzeigen können.
- Aktionen an steuerbaren Geräten ausführen können (z. B. eine Lampe ein- oder ausschalten).
- Sensorwerte simulieren und mittels einer **automatisierten Logik-Engine** auf definierte Bedingungen reagieren; passende Geräteaktionen werden automatisch ausgelöst.
- Automatisierungsregeln über die Benutzeroberfläche definieren können.
- Zwei Arten von Frames verwenden: **Aktualisierungsframes** für Sensorwerte und Regelauswertung sowie **Aktionsframes** für vorgemerkte Aktionen.
- Die letzten **120 Frames im Arbeitsspeicher** vorhalten. In der JSON-Datei wird dagegen **nur der aktuelle Zustand** gespeichert.
- Den gespeicherten Zustand beim nächsten Programmstart wiederherstellen können.
- Benutzereingaben validieren und Fehler behandeln, ohne die Simulation unkontrolliert zu beenden.
- Auf einem separaten Simulationsthread jede Sekunde einen Aktualisierungsframe einplanen; dazwischen Aktionsframes möglichst schnell abarbeiten.
- Fällige Aktualisierungsframes gegenüber noch nicht begonnenen Aktionsframes priorisieren; laufende Aktionsframes werden beendet, bevor die Aktualisierung startet.


## Erweiterte Anforderungen (Nice-to-have)

- Protokolle bzw. Simulationsdaten als CSV exportieren.
- Eine spätere Rückspul-Funktion wäre mit der Historie im RAM denkbar, **ist derzeit aber keine zugesagte Funktion**. Nach einem Neustart stehen frühere Frames nicht mehr zur Verfügung.

## Use Cases

1. **Gerät erstellen:** Als Benutzer kann ich eine Geräteart auswählen und ein neues Gerät anlegen.
2. **Einzelnes Gerät auslesen:** Als Benutzer kann ich die verfügbaren Daten eines bestimmten Geräts anzeigen.
3. **Alle Geräte auslesen:** Als Benutzer kann ich die Daten bzw. Zustände aller Geräte anzeigen.
4. **Gerät steuern:** Als Benutzer kann ich einem steuerbaren Gerät einen Befehl geben (z. B. Lampe einschalten).
5. **Automatisierungsregel definieren:** Als Benutzer kann ich Bedingungen und zugehörige Geräteaktionen festlegen.
6. **Automatische Reaktion beobachten:** Als Benutzer kann ich beobachten, wie bei einer erfüllten Bedingung (z. B. zu niedriger Feuchtigkeit) die definierte Geräteaktion ausgelöst wird.
7. **Zustand wiederherstellen:** Als Benutzer kann ich das Programm neu starten und mit dem zuletzt gespeicherten Zustand weiterarbeiten.
8. **Während der Simulation bedienen:** Als Benutzer kann ich Befehle eingeben, ohne die laufende Simulation anzuhalten.

## Geplante Design Patterns

### 1. Factory Pattern (`DeviceFactory`)

Wenn der Benutzer im Menü eine Geräteart auswählt, übernimmt die Factory die Erzeugung des passenden Objekts. Dadurch muss die Benutzeroberfläche die konkreten Konstruktoren der einzelnen Geräte nicht kennen. Die Objekterstellung bleibt von der Ein- und Ausgabe getrennt.

### 2. Observer Pattern (Sensoren und `DeviceController`)

Wenn sich ein relevanter Sensorwert ändert, kann der Sensor seine Beobachter benachrichtigen. Der `DeviceController` wertet die Benachrichtigung im Rahmen der Automatisierungslogik aus. Trifft eine Bedingung zu, wird eine Aktion **für ein späteres Aktionsframe vorgemerkt**, statt sofort eine möglicherweise unkontrollierte Folge von Aktionen auszuführen.

Die Automatisierung und damit dieser Einsatz des Observer Patterns gehören zu den **Muss-Anforderungen**.

## Technischer Überblick

- **Oberfläche:** Terminal / Konsole, verwaltet durch einen `IOManager`.
- **Geräte:** Gemeinsame Basis `IoTDevice`; Fähigkeiten werden über Interfaces wie `Sensor` und `Actuator` abgebildet.
- **Simulation:** Ein eigener Thread plant jede Sekunde einen Aktualisierungsframe ein und verarbeitet dazwischen Aktionsframes.
- **Steuerung:** AnzeigeActions lesen konsistente Snapshots direkt im UI; zustandsverändernde ArbeitsActions gelangen über die `UserActionQueue` in die `ActionQueue`. Auch automatisch ausgelöste Aktionen werden dort verarbeitet.
- **Persistenz:** Ein `StorageManager` verwaltet die begrenzte Frame-Historie im RAM und speichert den aktuellen Zustand als JSON.

Die Begründungen und der geplante Ablauf stehen in [Architecture.md](./Architecture.md). Die ursprünglichen Überlegungen stehen in [PlanningThoughts.md](./PlanningThoughts.md).

## Dokumentation und Umsetzung

Dieses Dokument beschreibt den **Planungsstand**, keine bereits fertiggestellte Implementierung. Für den Kompetenznachweis sind unter anderem noch zu erstellen und zu dokumentieren:

- Ein grobes UML-Klassendiagramm **vor** der Umsetzung und ein aktualisiertes Klassendiagramm zur Abgabe.
- Ein Sequenzdiagramm für einen typischen Use Case.
- Die konkrete Anwendung von Vererbung, Interfaces, Polymorphismus, Delegation, Clean Code und mindestens zwei Design Patterns.
- Eine Trennung von Benutzerschnittstelle, Programmlogik und Persistenz.
- Die Dokumentation der Eingabevalidierung und Fehlerbehandlung.
- Ein Fazit zum Lernprozess sowie nachvollziehbare, zeitlich verteilte Git-Commits.

**Rahmen des Auftrags:** Richtwert mindestens 8 selbstgeschriebene Klassen und 800 Codezeilen. KI-Hilfe muss im Code kenntlich gemacht werden; eine vollständig KI-generierte Anwendung ist nicht zulässig. Die Projektidee ist vor der Umsetzung mit der Lehrperson zu besprechen.

**KI-Unterstützung bei der Dokumentation:** Diese README wurde anhand der eigenen Planung in `PlanningThoughts.md`, des bisherigen README-Entwurfs und der nachträglich präzisierten Entscheidungen mit KI redaktionell überarbeitet. Die fachliche Prüfung durch den Projektverfasser steht noch aus. Falls UML-Diagramme mit KI erstellt werden, werden auch die verwendeten Prompts dokumentiert.
