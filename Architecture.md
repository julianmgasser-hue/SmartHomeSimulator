# Architektur und Designentscheidungen – SmartHomeSimulator

**Dokumentationsstand: 2. November 2026**  
**Grundlage:** Quellcode aus dem bereitgestellten Projektstand `SmartHomeSimulator-main(4).zip`.

> Dieses Dokument beschreibt die **gegenwärtige Implementierung**. Der ursprüngliche, weitergehende Architekturentwurf bleibt in [PlanningThoughts.md](./PlanningThoughts.md) nachvollziehbar. Insbesondere Multithreading, Observer Pattern, JSON-Persistenz und Frame-Historie wurden geplant, aber bisher nicht umgesetzt.

## 1. Aufbau und Verantwortlichkeiten

| Klasse / Bestandteil | Tatsächliche Aufgabe |
| --- | --- |
| `Main.kt` | Erstellt `ProgrammManager` und startet `runSimulation()`. |
| `ProgrammManager` | Verwaltet Geräteliste, separate Sensorliste, `Queue<Frame>` und Automationsliste; führt die Simulationsschleife aus. |
| `IOManager` | Liest und interpretiert Konsolenbefehle, zeigt Daten an, erstellt Frames und ruft für neue Geräte die Factory auf. |
| `IoTDevice` | Offene Basisklasse für gemeinsamen Namen und UUID. |
| `Lamp`, `Sensor`, `WaterDispenser` | Konkrete Gerätetypen mit unterschiedlichen Fähigkeiten. |
| `DeviceFactory` / `DeviceType` | Erzeugen konkrete Geräte anhand eines Typs; **Simple Factory**. |
| `Command` und konkrete Commands | Kapseln auszuführende Aktionen; **Command Pattern**. |
| `Frame`, `ActionFrame`, `DisplayFrame`, `RefreshFrame` | Gemeinsame Frame-Abstraktion sowie Ausführung von Befehlen, Anzeigen und Sensoraktualisierungen. |
| `Queue<T>` | Selbst implementierte generische Warteschlange mit normalem und priorisiertem Einfügen. |
| `Automation` | Prüft eine Bedingung `() -> Boolean` und liefert bei Erfüllung einen zugeordneten `Frame`. |
| `UnrealisticSimulationException` | Eigener Exception-Typ für unrealistische simulierte Sensordaten. |

Die Klassen liegen hauptsächlich in `src/` sowie in den Unterverzeichnissen `devices/`, `commands/`, `frames/`, `functionalities/` und `exceptions/`. Es besteht eine gewisse Aufgabentrennung, allerdings enthält `IOManager` derzeit auch die Zusammenstellung von Commands und die Geräteerstellung; eine eigenständige Persistenzschicht gibt es nicht.

## 2. Gerätehierarchie und Interfaces

### Gemeinsame Basis

`IoTDevice` ist als `open class` implementiert, **nicht als `abstract class`**. Die Klasse besitzt:

- `name: String`
- `id: UUID`, standardmässig zufällig erzeugt
- `displayBasicInfos()` zur Ausgabe von Name und ID

Die konkreten Klassen `Lamp`, `Sensor` und `WaterDispenser` erben von `IoTDevice`. Sie werden gemeinsam in einer `MutableList<IoTDevice>` gehalten. Damit nutzt die Verwaltung den gemeinsamen Basistyp und benötigt keine getrennte Geräteliste für jeden Gerätetyp.

### Fähigkeiten über Interfaces

| Interface | Methoden | Aktuelle Implementierung |
| --- | --- | --- |
| `Switchable` | `turnOn()`, `turnOff()` | `Lamp` |
| `Measurable` | `readData()`, `refreshData()`, `influenceData(Double)` | `Sensor` |
| `LiquidDispensable` | `dispense(List<Sensor>)` | `WaterDispenser` |
| `Displayable` | `displayData()` | `Lamp`, `Sensor` |

Diese Aufteilung ist ein Beispiel für **Komposition von Fähigkeiten über Interfaces**: Der `IOManager` bestimmt die angebotenen Aktionen anhand von `is Switchable`, `is Measurable` beziehungsweise `is LiquidDispensable`. Es wird kein allgemeiner An-/Aus-Status für sämtliche Geräte erzwungen.

### Sensoren und Wasserspender

Ein `Sensor` ist mit einem `SensorType` konfiguriert (`AIR_HUMIDITY`, `EARTH_HUMIDITY`, `AIR_TEMPERATURE`, `WATER_TEMPERATURE`, `BRIGHTNESS`). Jeder Sensortyp hat einen Standardwert und einen maximalen Offset.

Beim `refreshData()` wird ein zufälliger Zielwert innerhalb dieses Bereichs bestimmt. Der vorhandene Wert bewegt sich mit dem Faktor `0.2` in Richtung des Zielwerts. Anschliessend prüft der Sensor, ob das Resultat im zulässigen Bereich liegt, und wirft gegebenenfalls `UnrealisticSimulationException`.

Der `WaterDispenser` beeinflusst ausschliesslich Sensoren des Typs `EARTH_HUMIDITY`, indem er deren aktuellen Wert um `10.0` erhöht. Eine Begrenzung auf einen zulässigen Maximalwert findet dabei nicht statt.

## 3. Design Patterns

### Command Pattern – umgesetzt

`Command` definiert `execute()`. Die konkreten Implementierungen sind:

- `TurnOnCommand` / `TurnOffCommand`
- `InfluenceDataCommand`
- `DispenseLiquidCommand`
- `RefreshDataCommand`
- `CreateDeviceCommand`

Ein Command trägt die für seine Ausführung benötigten Objekte und gegebenenfalls Parameter in sich. Beispielsweise speichert `TurnOnCommand` eine `Switchable`-Referenz, während `CreateDeviceCommand` ein `IoTDevice` und die veränderbare Geräteliste speichert.

`ActionFrame` enthält einen `Command` und **delegiert seine `execute()`-Methode** an `command.execute()`. Dadurch muss der `ProgrammManager` nicht wissen, ob ein Frame eine Lampe einschaltet, Sensordaten beeinflusst oder ein neues Gerät registriert. Der Ablauf lässt sich über einen einheitlichen `Frame`-Typ steuern.

**Abgrenzung:** `RefreshDataCommand` ist vorhanden, wird im aktuellen `RefreshFrame` aber nicht direkt verwendet. `RefreshFrame` ruft selbst `refreshData()` auf den enthaltenen `Measurable`-Objekten auf.

### Simple Factory – umgesetzt

`DeviceFactory.createDevice(type: DeviceType, name: String, sensorType: SensorType)` erstellt je nach Geräteart eine `Lamp`, einen `Sensor` oder einen `WaterDispenser` und gibt `IoTDevice` zurück. Der `sensorType` besitzt einen Standardwert für Bodenfeuchtigkeit.

Die Factory wird an zwei Stellen benutzt:

1. `ProgrammManager` erstellt darüber die drei anfänglichen Geräte.
2. `IOManager.createDevice()` erstellt nach der Auswahl über `make` ein neues Gerät und übergibt es an `CreateDeviceCommand`.

**Begründung:** Die Kenntnis der konkreten Gerätekonstruktoren liegt zentral in der Factory. Die übrige Verwaltung kann Geräte über ihren gemeinsamen Basistyp entgegennehmen. Diese Implementierung ist ausdrücklich eine **Simple Factory** und nicht zwingend das formale GoF-*Factory Method*-Pattern.

### Observer Pattern – nicht umgesetzt

Sensoren verfügen über keine Registrierung von Observern und versenden keine Änderungsbenachrichtigungen. Die `Automation`-Klasse ist eine einfache Regelprüfung. Ihre Existenz allein macht das System **nicht** zu einer Observer-Implementierung.

## 4. Frames und die Simulationsschleife

### Frame-Abstraktion

`Frame` ist eine abstrakte Klasse mit `execute()`. Daraus leiten sich ab:

- **`ActionFrame`**: führt einen gespeicherten `Command` aus.
- **`DisplayFrame`**: ruft `displayData()` auf einer Liste von `Displayable`-Objekten auf.
- **`RefreshFrame`**: ruft `refreshData()` auf seiner Liste von `Measurable`-Objekten auf.

### Ablauf im `ProgrammManager`

Die aktuelle Anwendung besitzt **nur eine Simulationsschleife**, keinen separaten Simulationsthread. Der Ablauf ist vereinfacht:

1. Die Zeit seit dem letzten geplanten Refresh wird geprüft.
2. Sind mindestens `1000` Millisekunden vergangen, wird ein `RefreshFrame` mit hoher Priorität in `actionQueue` eingeordnet. Zuvor gibt `IOManager.displayData()` die derzeit bekannten Sensorwerte aus.
3. Andernfalls fragt `IOManager.userAction()` eine Benutzeraktion ab. Diese kann einen `ActionFrame` oder `DisplayFrame` zurückgeben, der ebenfalls priorisiert eingeordnet wird.
4. Das nächste `Frame`-Objekt wird aus der Queue entfernt und ausgeführt.
5. War es ein `RefreshFrame`, wird anschliessend `checkAllAutomations()` aufgerufen.

**Wichtige Einschränkung:** Da die Schleife bei `readln()` auf eine Eingabe wartet, kann die Sensoraktualisierung nicht unabhängig vom Benutzer alle Sekunden stattfinden. Der Wert `1000` Millisekunden ist hier eine Abfragebedingung, **kein garantierter Takt**. Die Sensorwerte werden im Refresh-Zweig zudem **vor** dem neu eingeplanten Refresh angezeigt.

### Warteschlange `Queue<T>`

Die eigene generische Klasse verwaltet eine `MutableList<T>` und bietet:

- `add(item)`: am Ende einfügen
- `addImportant(item)`: am Anfang einfügen
- `remove()`: erstes Element entfernen oder `null` zurückgeben
- `contains(item)`: nach einem vorhandenen Element suchen

`ProgrammManager` verwendet sie als `Queue<Frame>`. Es gibt **keine separate `UserActionQueue`**. Die Implementierung ist nicht als threadsicher ausgelegt; die innere Liste ist derzeit auch von aussen zugänglich.

## 5. Benutzerinteraktion und Geräteverwaltung

`IOManager.userAction()` unterscheidet vier Befehle:

| Befehl | Ablauf |
| --- | --- |
| `read <name/UUID>` | Sucht das Gerät und erzeugt für `Displayable` ein `DisplayFrame`. |
| `read all` | Gibt lediglich `displayBasicInfos()` für jedes Gerät aus (Name und UUID). |
| `edit <name/UUID>` | Erwartet einen Sensor und einen neuen Zahlenwert; erzeugt ein `ActionFrame` mit `InfluenceDataCommand`. |
| `work <name/UUID>` | Listet passende Aktionen anhand der Interfaces auf; erzeugt den gewählten Command im `ActionFrame`. |
| `make` | Fragt Gerätetyp, Name und gegebenenfalls Sensortyp ab; erzeugt ein `ActionFrame` mit `CreateDeviceCommand`. |

Die Gerätesuche erfolgt über Name oder UUID. Für den normalen Befehlsweg wird die Eingabe in Kleinbuchstaben umgewandelt; bei der separaten Sensoreingabe für den Wasserspender geschieht dies derzeit nicht konsistent.

**Bekannter Bug:** In `userAction()` führt gerade die Antwort `y` auf `Want to take an Action? (y/N)` zu `return null`. Die beabsichtigte Ja/Nein-Logik ist damit vertauscht.

**Bekannte Einschränkung beim Erstellen:** `CreateDeviceCommand` fügt das neue Gerät unmittelbar zur Liste `iotDevices` hinzu. Diese Änderung erfolgt **nicht über** `ProgrammManager.updateDevices()`, welches auch die gesonderte Liste `sensors` aktualisieren würde. Neu angelegte Sensoren können deshalb in der Geräteliste vorhanden sein, ohne an den regelmässigen Refresh-Vorgängen teilzunehmen.

## 6. Automatisierungen: vorbereitete Struktur, keine eingerichteten Regeln

Die Klasse `Automation` speichert:

- `condition: () -> Boolean`
- `action: Frame`

`checkAutomation()` gibt den Aktionsframe zurück, wenn die Bedingung wahr ist, ansonsten `null`.

`ProgrammManager` besitzt eine `MutableList<Automation>` und ruft nach der Ausführung eines `RefreshFrame` die Methode `checkAllAutomations()` auf. Diese kontrolliert die Regeln und reiht erfüllte Aktionen ein, sofern das entsprechende Frame-Objekt nicht schon in der Queue liegt.

**Aktueller Stand:** Die Automationsliste wird nirgends mit Regeln befüllt. Deshalb führt die Regelprüfung im ausgelieferten Programm praktisch keine automatischen Aktionen aus. Eine Benutzerschnittstelle zum Definieren von Regeln fehlt. Dies ist **Polling nach einem Refresh**, kein Observer Pattern.

## 7. Eingabevalidierung und Exception Handling

Vorhandene Massnahmen:

- `toIntOrNull()` und Bereichsprüfungen für nummerierte Optionen.
- `toDoubleOrNull()` für eingegebene Sensorwerte.
- `require(name.isNotBlank())` in der Factory.
- Eigene `UnrealisticSimulationException` beim Erkennen unzulässiger Sensordaten.
- `try/catch` in `ProgrammManager.runSimulation()` für `UnrealisticSimulationException`.

**Grenzen der Umsetzung:** Beim Auffangen einer `UnrealisticSimulationException` erscheint eine Meldung und das Programm beendet sich mit `exitProcess(0)`; die Simulation läuft danach nicht weiter. Leere Gerätenamen führen durch `require(...)` zu einer `IllegalArgumentException`, die an dieser Stelle nicht gesondert abgefangen wird. Eingesetzte Sensorwerte werden beim Schreiben nicht auf den plausiblen Bereich begrenzt. Fehlerbehandlung und Eingabevalidierung sind daher **nur teilweise umgesetzt**.

## 8. Ursprünglich geplant, derzeit nicht umgesetzt

| Ursprüngliches Konzept | Stand im Code vom 2. November 2026 |
| --- | --- |
| Separater UI- und Simulationsthread | Nicht vorhanden: eine blockierende Schleife mit `readln()`. |
| Beobachter bei Sensoränderungen (`Observer Pattern`) | Nicht vorhanden. |
| Vollständige Automatisierungs-Engine mit Regeln aus dem UI | Nur Datenstruktur und Prüfmethode vorhanden; keine Regeln eingerichtet. |
| `UserActionQueue` neben `ActionQueue` | Nicht vorhanden: eine eigene `Queue<Frame>` wird verwendet. |
| `StorageManager` und JSON-Speicherung | Nicht vorhanden. |
| Begrenzte Historie der letzten 120 Frames | Nicht vorhanden. |
| Wiederherstellung des Zustands nach einem Neustart | Nicht vorhanden. |
| Unveränderliche Snapshots zur sicheren UI-Anzeige | Nicht vorhanden. |
| Rückspulfunktion | Nicht vorhanden; war ohnehin als mögliche spätere Erweiterung gedacht. |

Der Umfang wurde gegenüber der ursprünglichen Planung reduziert, um die objektorientierte Grundstruktur und konkrete, demonstrierbare Geräteaktionen umzusetzen. Die fehlenden Komponenten bleiben als offene Anforderungen nachvollziehbar; sie werden **nicht** als bereits implementiert dargestellt.

## 9. Clean Code: nachvollziehbare Entscheidungen und Grenzen

**Positiv erkennbar:**

- Gerätefähigkeiten sind über kleine Interfaces beschrieben.
- Commands kapseln unterschiedliche Aktionen hinter einer gemeinsamen Schnittstelle.
- `DeviceFactory` bündelt die Instanziierung konkreter Geräteklassen.
- `Frame` bildet den gemeinsamen Ausführungstyp für die Warteschlange.
- Der eigene generische Typ `Queue<T>` lässt sich unabhängig von `Frame` verwenden.

**Noch verbesserungswürdig:**

- `IOManager` übernimmt neben Ein-/Ausgabe auch Geräteerzeugung und Command-Zusammenstellung.
- Die Rückfrage nach einer Aktion enthält einen Logikfehler.
- Die allgemeine Geräteliste und die spezielle Sensorliste werden bei `make` nicht synchron gehalten.
- Die innere `Queue`-Liste ist öffentlich zugänglich.
- Fehlerbehandlung und Behandlung unzulässiger Sensorwerte sind nicht durchgehend robust.

Diese Gegenüberstellung dient als ehrliche **Code-Reflexion**; sie behauptet keine vollständige Clean-Code-Konformität.

## 10. UML, Quellenstand und KI-Unterstützung

Die vorhandenen Dateien [ClassUML.png](./ClassUML.drawio.png) und [SequenceDiagramm.png](./SequenceDiagramm.png) wurden bei dieser Aktualisierung **nicht verändert**. Das neue UML-Klassendiagramm erstellt beziehungsweise aktualisiert der Projektverfasser selbst. Dabei müssen die aktuell vorhandenen Klassen und Beziehungen berücksichtigt und nicht umgesetzte Bausteine aus dem ursprünglichen Entwurf entfernt oder als Planung gekennzeichnet werden.

Die ursprünglichen Gedanken stehen in [PlanningThoughts.md](./PlanningThoughts.md). Die redaktionelle Überarbeitung dieser Architekturdatei und von `README.md` am **2. November 2026** erfolgte durch ChatGPT auf Basis des aktuellen Quellcodes. Es wurden in diesem Schritt **keine Kotlin-Dateien und keine Diagramme geändert**. Details dazu stehen in [KI_Nutzung.md](./KI_Nutzung.md).
