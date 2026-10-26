# Architektur- und Designentscheidungen

> **Status:** Geplante Architektur, noch keine Bestätigung einer vollständigen Implementierung. Dieses Dokument fasst die Überlegungen aus [PlanningThoughts.md](./PlanningThoughts.md) und die anschliessend getroffenen Entscheidungen zusammen.

## 1. Geräte und Struktur

Die Geräte sollen über gemeinsame Eigenschaften und individuell kombinierbare Fähigkeiten verfügen:

- Eine **abstrakte Basisklasse `IoTDevice`** ist für gemeinsame Eigenschaften wie Geräte-ID und Name vorgesehen.
- **Interfaces** wie `Sensor` (Werte bereitstellen) und `Actuator` (Aktionen ausführen) beschreiben unterschiedliche Fähigkeiten. Je nach Gerät können beide Fähigkeiten kombiniert werden.
- Dadurch muss nicht für jede denkbare Kombination von Fähigkeiten eine zusätzliche Vererbungsstufe geschaffen werden.

**Noch zu prüfen:** Im ursprünglichen Entwurf ist für jedes `IoTDevice` auch ein allgemeiner An-/Aus-Status vorgesehen. Ob dieser für reine Sensoren sinnvoll ist oder nur für bestimmte Geräte gelten sollte, wird beim Klassendiagramm entschieden.

Das Konzept soll Vererbung, Interfaces und Polymorphismus sinnvoll verbinden. Die genaue Klassenstruktur wird im UML-Klassendiagramm festgehalten.

## 2. Simulation: eigener Thread und Taktung

Die Simulation läuft auf einem **separaten Thread**. **Jede Sekunde wird ein Aktualisierungsframe eingeplant**; zwischen den Aktualisierungen verarbeitet derselbe Thread möglichst viele Aktionsframes. Der Hauptthread bleibt für die Benutzereingaben über den `IOManager` zuständig.

**Begründung:** Konsolenfunktionen wie `readln` blockieren, solange der Benutzer nichts eingibt. Würde die Simulation auf demselben Thread laufen, hinge der Fortschritt der Simulation vom Eingabetempo ab. Durch die Trennung läuft die Simulation zeitlich unabhängig von der Konsole.

Ein bereits begonnener Aktionsframe wird nicht mitten in seiner Ausführung unterbrochen. Deshalb kann ein Aktualisierungsframe bei langen Aktionen etwas verspätet beginnen. Die genaue Behandlung solcher Verzögerungen wird bei der Implementierung festgelegt.

## 3. Frame Arten

### Aktualisierungsframes

Ein Aktualisierungsframe wird jede Sekunde ausgeführt und hat gegenüber den Aktionsframes Priorität.

Während eines Aktualisierungsframes werden:

* Neue Sensorwerte generiert.
* Automatisierungsregeln evaluiert.
* Gegebenenfalls neue Aktionen in die ActionQueue eingereiht.

Die Aktualisierungsframes sorgen dafür, dass sich die simulierte Umgebung regelmässig verändert, unabhängig davon, wie viele Aktionen noch auszuführen sind.

### Aktionsframes

Zwischen zwei Aktualisierungsframes werden möglichst viele Aktionsframes verarbeitet.

Ein Aktionsframe verarbeitet die anstehenden Aktionen aus der ActionQueue. Diese Aktionen können durch Automatisierungsregeln oder Benutzerinteraktionen entstehen.

Die Aktionsframes werden nicht durch einen festen Zeittakt begrenzt. Stattdessen verarbeitet die Simulation sie so schnell wie möglich, bis entweder:

* Die ActionQueue leer ist.
* Der nächste Aktualisierungsframe fällig wird.

Wenn nach einer Sekunde noch Aktionen in der Warteschlange vorhanden sind, wird zuerst der nächste Aktualisierungsframe ausgeführt. Anschliessend wird die Verarbeitung der verbleibenden Aktionen fortgesetzt.

Ein bereits begonnener Aktionsframe wird dabei nicht unterbrochen, sondern abgeschlossen. Danach kann der Aktualisierungsframe ausgeführt werden.

### Entstehung von Aktionsframes

Aktionsframes entstehen durch zwei Ereignisse:

* Eine Automatisierungsregel wird während eines Aktualisierungsframes ausgelöst.
* Ein Benutzer führt eine zustandsverändernde Aktion aus.

Beide Arten von Aktionen werden über die ActionQueue verarbeitet.

Die Simulation unterscheidet bei der Ausführung nicht zwischen automatisch und manuell ausgelösten Aktionen.

AnzeigeActions erzeugen hingegen keine Aktionsframes, da sie den Simulationszustand nicht verändern.

### Vermeidung unkontrollierter Kettenreaktionen

Automatisierungen erzeugen neue Aktionen bei der Regelauswertung während eines Aktualisierungsframes. ArbeitsActions des Benutzers werden unabhängig davon in die `ActionQueue` übernommen.

Aktionsframes selbst lösen keine erneute vollständige Evaluation der Automatisierungsregeln aus.

Dadurch können Aktionsframes keine unbegrenzte Kette weiterer Aktionsframes erzeugen.

### Zeitliche Steuerung

Der Simulationsthread verwaltet den Zeitpunkt des nächsten Aktualisierungsframes.

Sobald dieser Zeitpunkt erreicht ist, erhält der Aktualisierungsframe Priorität gegenüber weiteren Aktionen.

Wenn keine Aktionen anstehen, wartet der Simulationsthread bis zum nächsten Aktualisierungsframe oder bis neue Aktionen eintreffen.

Dadurch wird verhindert, dass die Simulation unnötig Rechenleistung beansprucht.

## 4. Automatisierung und Observer Pattern

Die **automatisierte Logik-Engine ist eine Muss-Anforderung**, keine optionale Erweiterung.

Geplant ist das **Observer Pattern** zwischen Sensoren und einem `DeviceController`:

- Sensorwerte können sich während eines Aktualisierungsframes ändern.
- Die relevanten Beobachter werden über diese Änderung informiert.
- Der `DeviceController` überprüft, ob eine hinterlegte Regel zutrifft (beispielsweise: Feuchtigkeit unter einem Grenzwert).
- Trifft sie zu, wird die zugehörige Aktion für ein nachfolgendes Aktionsframe in die `ActionQueue` eingereiht.

Die Benachrichtigung durch das Observer Pattern und die eigentliche Geräteaktion sind damit bewusst **zeitlich entkoppelt**. So soll etwa eine Bewässerungsaktion nicht noch im selben Frame weitere unmittelbare Regelauswertungen auslösen.

## 5. User-Interaktionen, Threading und Queues

Die Anwendung verwendet zwei Threads:

* **UI-Thread:** Verwaltet die Benutzereingaben und die Ausgabe im Terminal.
* **Simulationsthread:** Verarbeitet Aktualisierungsframes und Aktionsframes.

Die Trennung ist notwendig, da blockierende Konsoleneingaben wie `readln()` andernfalls die Simulation anhalten würden.

### UserActionQueue

Benutzerbefehle werden vom `IOManager` in AnzeigeActions und ArbeitsActions unterschieden. **Nur ArbeitsActions** werden über eine eigene `UserActionQueue` an den Simulationsthread übermittelt; AnzeigeActions werden direkt im UI verarbeitet.

Dabei wird zwischen zwei Arten von Benutzeraktionen unterschieden:

#### AnzeigeActions

AnzeigeActions dienen ausschliesslich dem Auslesen und Anzeigen von Informationen.

Beispiele:

* Alle vorhandenen Geräte anzeigen.
* Den aktuellen Zustand eines Geräts anzeigen.
* Sensorwerte auslesen.

Diese Aktionen verändern den Simulationszustand nicht und müssen deshalb nicht als Aktionsframes ausgeführt werden.

Sie werden direkt vom UI anhand eines konsistenten, unveränderlichen Snapshots verarbeitet und benötigen weder `UserActionQueue` noch Aktionsframe.

#### ArbeitsActions

ArbeitsActions verändern den Zustand der Simulation.

Beispiele:

* Eine Lampe einschalten.
* Die Solltemperatur einer Heizung ändern.
* Ein neues Gerät hinzufügen.

ArbeitsActions benötigen keinen eigenen Frame-Typ und müssen nicht zwingend als separate Klassen modelliert werden.

Stattdessen werden die entsprechenden Benutzerbefehle in ausführbare Aktionen übersetzt und über die thread-sichere `UserActionQueue` an den Simulationsthread übergeben. Dieser übernimmt sie in die `ActionQueue`, aus der sie zwischen zwei Aktualisierungsframes als Aktionsframes abgearbeitet werden. Ein eigener UserFrame ist nicht vorgesehen.

### ActionQueue

Die `ActionQueue` enthält alle auszuführenden Aktionen der Simulation.

Aktionen können aus zwei Quellen entstehen:

1. Automatisierungsregeln, die während eines Aktualisierungsframes evaluiert werden.
2. ArbeitsActions, die vom Benutzer über die `UserActionQueue` eingereicht werden.

Die Aktionen werden zwischen den Aktualisierungsframes verarbeitet.

Sobald der nächste Aktualisierungsframe fällig wird, erhält dieser Vorrang vor den noch wartenden Aktionen.

### Thread-Sicherheit

Da UI und Simulation auf unterschiedlichen Threads laufen, muss der Zugriff auf gemeinsam verwendete Daten kontrolliert werden.

Zustandsverändernde Aktionen werden ausschliesslich durch den Simulationsthread ausgeführt.

Die Kommunikation zwischen UI und Simulation erfolgt über thread-sichere Queues.

AnzeigeActions lesen einen **konsistenten Snapshot** des aktuellen Simulationszustands. Der Snapshot wird kontrolliert erzeugt und nach seiner Veröffentlichung nicht mehr verändert; dadurch sieht das UI keine teilweise aktualisierten Gerätezustände. Wie die atomare Erstellung bzw. Veröffentlichung technisch umgesetzt wird, wird beim Klassendiagramm und in der Implementierung konkretisiert. 

## 6. Persistenz und Frame-Historie

Ein zentraler **`StorageManager`** ist für Historie und Speicherung verantwortlich:

- **Im RAM:** Nur die **letzten 120 Frames** werden als begrenzte Historie vorgehalten. Ältere Frames werden verworfen.
- **In der JSON-Datei:** Es wird **ausschliesslich der aktuelle Frame bzw. der zur Wiederherstellung notwendige aktuelle Zustand** gespeichert, nicht der gesamte Verlauf.
- **Beim Programmstart:** Der gespeicherte Zustand wird eingelesen, sodass die Simulation vom letzten gespeicherten Stand fortgesetzt werden kann.
- **Nach einem Neustart:** Frühere Frames sind nicht mehr vorhanden; Zurückspulen über den Neustart hinweg ist nicht vorgesehen.

Die Historie im RAM hält die Möglichkeit offen, später eine Rückspul-Funktion zu ergänzen. **Das Zurückspulen selbst ist aktuell nicht Teil des verbindlichen Funktionsumfangs.**

Um Speicher- und Laufzeitkosten zu begrenzen, sollen Frames nur die zur Wiederherstellung tatsächlich benötigten Daten enthalten. Wie und wann die JSON-Datei geschrieben wird (beispielsweise bei jedem Frame oder bei bestimmten Ereignissen), muss noch festgelegt werden.

## 7. Verantwortlichkeiten und Design Patterns

| Baustein | Geplante Verantwortung |
| --- | --- |
| `IOManager` | Konsolen-Eingaben und -Ausgaben; Aktions- von Anzeigebefehlen unterscheiden |
| `IoTDevice` | Gemeinsame Grundlage der Geräte |
| `Sensor` / `Actuator` | Fähigkeiten als Interfaces bereitstellen |
| `DeviceFactory` | Geräteobjekte gemäss Benutzerauswahl erzeugen (**Factory Pattern**) |
| `DeviceController` | Sensoränderungen beobachten und Automatisierungsregeln auswerten (**Observer Pattern**) |
| Simulationssteuerung (Name offen) | Frame-Takt und Wechsel zwischen Aktualisierungs- und Aktionsframes organisieren |
| `UserActionQueue` / `ActionQueue` | ArbeitsActions vom UI entgegennehmen und zusammen mit automatisierten Aktionen zur Ausführung vorhalten |
| `StorageManager` | Die letzten 120 Frames im RAM verwalten und den aktuellen Zustand als JSON speichern/laden |

Die Zuordnung ist eine **Planung**. Ob die Queues eigene Klassen oder verwendete Bibliothekstypen sind, steht noch nicht fest. Die endgültigen Verantwortlichkeiten werden im Klassendiagramm und im Code präzisiert.

Die getrennten Bereiche **Benutzerschnittstelle**, **Simulations-/Gerätelogik** und **Persistenz** sollen auch die vom Auftrag verlangte Schichtentrennung ermöglichen.

## 8. Noch offene Entscheidungen

- Welche konkreten Gerätetypen umgesetzt werden und welche Eigenschaften/Fähigkeiten sie jeweils haben.
- Ob der An-/Aus-Status tatsächlich in die Basisklasse gehört.
- Wie konsistente Snapshots technisch erstellt und sicher an den UI-Thread übergeben werden.
- Wann und wie ArbeitsActions aus der `UserActionQueue` in die `ActionQueue` übernommen werden sowie in welcher Reihenfolge mehrere Aktionen ausgeführt und Fehler behandelt werden.
- Wann der aktuelle Zustand in die JSON-Datei geschrieben wird.
- Welche Klassenbeziehungen und Interaktionen im UML-Klassen- und Sequenzdiagramm festgehalten werden.

---

**Herkunft / KI-Unterstützung:** Die ursprünglichen Architekturgedanken stehen in [PlanningThoughts.md](./PlanningThoughts.md). Diese Zusammenfassung wurde auf Basis der bisherigen `Architecture.md`, der Planungsnotizen und der nachträglich ausdrücklich festgelegten Entscheidungen mit KI überarbeitet. Der Projektverfasser muss diese Fassung noch kontrollieren und bei Bedarf korrigieren.
