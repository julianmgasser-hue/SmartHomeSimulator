# SmartHomeSimulator – Smart-Home-Simulation und Manager

**Stand der Implementierung: 2. November 2026**  
**Sprache:** Kotlin (JVM) | **Bedienung:** Terminal/Konsole

## Projektidee und Ziel

Der SmartHomeSimulator bildet unterschiedliche IoT-Geräte in einer einfachen Simulation ab. Benutzer können Geräte anzeigen, Sensorwerte verändern, Lampen schalten und neue Geräte erstellen. Die Anwendung soll dabei zentrale Konzepte der objektorientierten Programmierung demonstrieren: Vererbung, Interfaces, Polymorphismus, Delegation, Generics und Design Patterns.

Die ursprünglichen Ideen und weitergehenden Ziele stehen in [PlanningThoughts.md](./PlanningThoughts.md). Dieses README beschreibt dagegen **den tatsächlich vorhandenen Stand**, nicht sämtliche ursprünglich geplanten Funktionen.

## Umgesetzte Funktionen

- Drei Gerätearten: `Lamp`, `Sensor` und `WaterDispenser`.
- Gemeinsame Gerätebasis `IoTDevice` mit Name und UUID sowie gerätespezifische Interfaces.
- Startgeräte: `Desktop Lamp`, `Plant Earth-Humidity Sensor` und `Plant Water Dispenser`.
- `read <Gerätename/UUID>` zum Anzeigen unterstützter Gerätedaten; `read all` listet die Namen und UUIDs aller Geräte auf.
- `work <Gerätename/UUID>` zur Auswahl der für ein Gerät verfügbaren Aktionen, beispielsweise Lampe ein-/ausschalten, Sensorwert beeinflussen oder Wasser auf einen Bodenfeuchtigkeitssensor anwenden.
- `edit <Gerätename/UUID>` zum direkten Setzen eines Sensorwertes.
- `make` zur interaktiven Erstellung einer Lampe, eines Sensors (mit Sensortyp) oder eines Wasserspenders.
- Frames für Geräteaktionen, Anzeigen und die Aktualisierung von Sensoren; eine eigene generische Warteschlange `Queue<T>`.
- Eine vorbereitete Automatisierungsstruktur (`Automation`, `checkAllAutomations()`), die zurzeit **keine eingerichteten Regeln** besitzt.

**Sensortypen:** `AIR_HUMIDITY`, `EARTH_HUMIDITY`, `AIR_TEMPERATURE`, `WATER_TEMPERATURE` und `BRIGHTNESS`. Die Sensorwerte starten mit einem Standardwert und bewegen sich bei der Aktualisierung schrittweise auf einen simulierten Zielwert zu.

## Start und Bedienung

Das Projekt verwendet Gradle und das Kotlin-JVM-Plugin in Version `2.1.10`. Die Anwendung kann beispielsweise in IntelliJ IDEA über die `main()`-Funktion in `src/Main.kt` gestartet werden. Der Gradle-Wrapper liegt im Repository; mit `./gradlew build` lässt sich das Projekt bauen. Ein separater `run`-Task ist im aktuellen Gradle-Skript nicht eingerichtet.

Nach dem Start werden Benutzereingaben über den `IOManager` abgefragt. Beispiele für die Befehlszeile nach der Frage **`What action?`**:

```text
read all
read desktop lamp
work desktop lamp
edit plant earth-humidity sensor
work plant water dispenser
make
```

Bei `work` und `make` erscheinen weitere Auswahl- und Eingabeaufforderungen. Gerätenamen werden beim normalen Befehl zunächst in Kleinbuchstaben umgewandelt.

**Bekannter Eingabefehler:** Die Rückfrage `Want to take an Action? (y/N)` ist im aktuellen Code verkehrt implementiert: Eine Antwort `y` überspringt die Aktionsauswahl. Für die Demonstration führt zurzeit beispielsweise `n` zur Frage `What action?`. Das entspricht nicht der beabsichtigten Bedienung und sollte korrigiert werden.

## Use Cases und aktueller Umsetzungsstand

| Use Case | Status | Erläuterung |
| --- | --- | --- |
| Neues Gerät erstellen | Umgesetzt, mit Einschränkung | `make` nutzt `DeviceFactory` und `CreateDeviceCommand`. Neu erstellte Sensoren werden jedoch nicht in die separate Sensorliste für periodische Updates übernommen. |
| Einzelnes Gerät auslesen | Umgesetzt | `read` funktioniert für Geräte, die `Displayable` implementieren, etwa Lampen und Sensoren. |
| Alle Geräte anzeigen | Teilweise | `read all` zeigt Name und UUID, nicht sämtliche Gerätewerte und -zustände. |
| Geräte steuern | Umgesetzt | Lampen schalten, Sensorwerte beeinflussen und Bodenfeuchtigkeit über den Wasserspender erhöhen. |
| Sensorwerte simulieren | Teilweise | `RefreshFrame` und Sensoraktualisierung sind vorhanden; der Aktualisierungstakt ist wegen blockierender Konsoleneingaben nicht zuverlässig. |
| Automatisierungsregeln definieren und ausführen | Vorbereitet, nicht nutzbar | `Automation` und die Regelauswertung existieren, aber es gibt weder eingerichtete Regeln noch einen Befehl zum Anlegen solcher Regeln. |
| Zustand speichern und wiederherstellen | Nicht umgesetzt | Keine JSON-Persistenz und kein `StorageManager`. |
| Simulation unabhängig von Benutzereingaben ausführen | Nicht umgesetzt | Die Anwendung läuft derzeit in einer einzigen Schleife; `readln()` blockiert sie. |
| Historie der letzten 120 Frames | Nicht umgesetzt | Keine Frame-Historie oder Rückspulfunktion. |

## Objektorientiertes Design

### Geräte, Vererbung und Interfaces

`IoTDevice` ist eine **offene (`open`) Basisklasse**, keine abstrakte Klasse. `Lamp`, `Sensor` und `WaterDispenser` erben davon. Die Fähigkeiten werden über `Switchable`, `Measurable`, `LiquidDispensable` und `Displayable` beschrieben. So kann die Steuerung Geräte nach ihren Fähigkeiten behandeln, statt jede konkrete Klasse einzeln abzufragen.

### 1. Command Pattern

Das Interface `Command` definiert `execute()`. Konkrete Commands wie `TurnOnCommand`, `InfluenceDataCommand`, `DispenseLiquidCommand` und `CreateDeviceCommand` kapseln eine Aktion samt Zielobjekt. `ActionFrame` **delegiert** die Ausführung an den zugehörigen Command. Dadurch bleiben Benutzerinteraktion und konkrete Geräteoperationen voneinander getrennt.

### 2. Simple Factory

`DeviceFactory.createDevice()` erstellt abhängig von `DeviceType` und gegebenenfalls `SensorType` eine `Lamp`, einen `Sensor` oder einen `WaterDispenser` und gibt jeweils `IoTDevice` zurück. Die Factory wird für die Startgeräte und über den Befehl `make` verwendet. Damit ist die Erstellung der konkreten Geräte an einer Stelle zusammengefasst.

**Einordnung:** Dies ist eine **Simple Factory**. Sie entspricht nicht zwingend dem klassischen GoF-*Factory Method*-Pattern. Ob die Lehrperson sie als zweites gefordertes Design Pattern anerkennt, muss geklärt werden. Das früher geplante **Observer Pattern ist nicht implementiert**.

### Weitere OOP-Konzepte

- **Polymorphismus:** Geräteliste als `MutableList<IoTDevice>`; Aktionen werden anhand implementierter Fähigkeiten ausgewählt.
- **Generics:** Selbst implementierte `Queue<T>` für `Frame`-Objekte.
- **Abstraktion:** `Frame` ist eine abstrakte Klasse mit der Methode `execute()`.
- **Delegation:** `ActionFrame` ruft den gekapselten `Command` auf; die Geräteerstellung wird an `DeviceFactory` delegiert.
- **Fehlerbehandlung:** Einige ungültige Eingaben werden mit `toIntOrNull()`, `toDoubleOrNull()` und Indexprüfungen abgefangen. Für unrealistische Sensorwerte existiert `UnrealisticSimulationException`.

Weitere Details stehen in [Architecture.md](./Architecture.md).

## Bekannte Einschränkungen und offene Anforderungen

1. **Konsoleneingabe:** Die oben beschriebene Ja/Nein-Abfrage ist invertiert. Ausserdem blockiert `readln()` die Simulation; es gibt keinen separaten Simulationsthread.
2. **Neu erstellte Sensoren:** `CreateDeviceCommand` ergänzt die allgemeine Geräteliste. Die im `ProgrammManager` separat gespeicherte Liste `sensors` wird dabei nicht aktualisiert; neue Sensoren nehmen deshalb nicht automatisch an den Aktualisierungsframes teil.
3. **Unvollständige Fehlerbehandlung:** Bei `UnrealisticSimulationException` gibt der `ProgrammManager` eine Meldung aus und beendet das Programm mit `exitProcess(0)`. Ein leerer Name in der Factory löst eine `IllegalArgumentException` aus, für die keine durchgängige Behandlung vorhanden ist.
4. **Sensor- und Wasserspenderwerte:** Direkte Änderungen erlauben auch unrealistische Werte. Der Wasserspender erhöht Bodenfeuchtigkeit um `10.0`; dadurch kann ein Sensor später die Plausibilitätsprüfung verletzen.
5. **Anzeige und Zeitsteuerung:** Die regelmässige Sensorausgabe erfolgt im `ProgrammManager` vor dem Ausführen des jeweiligen `RefreshFrame`. Die angezeigten Werte können daher noch vom vorherigen Stand stammen.
6. **Automatisierung, Persistenz und Historie:** Die ursprünglichen Erweiterungsziele sind nicht fertiggestellt. Auch eine robuste Trennung von Ein-/Ausgabe, Logik und Datenspeicherung fehlt teilweise.

Im Projektauftrag sind die Speicherung von Daten, die Eingabevalidierung und die nachvollziehbare Dokumentation relevante Anforderungen. Diese Punkte sind mit dem aktuellen Stand **nicht vollständig erfüllt**. Die genannten 800 Codezeilen sind ein Richtwert, der mit dem aktuellen Kotlin-Quellcode noch nicht erreicht wird.

## Diagramme und Projektdokumentation

- [Architecture.md](./Architecture.md): Beschreibung der **aktuellen** Klassen, Abläufe und Entwurfsentscheidungen.
- [PlanningThoughts.md](./PlanningThoughts.md): Ursprüngliche eigene Planungsnotizen; sie bleiben bewusst als historischer Entwurf erhalten und stimmen nicht in jedem Punkt mit der Implementierung überein.
- [KI_Nutzung.md](./KI_Nutzung.md): Dokumentation der KI-Unterstützung einschliesslich der KI-Erstellung und -Überarbeitung von Projektdateien.
- [ClassUML.png](./ClassUML.drawio.png): Vorliegendes Klassendiagramm; **wird vom Projektverfasser noch an den aktuellen Code angepasst**.
- [SequenceDiagramm.png](./SequenceDiagramm.png): Vorliegendes Sequenzdiagramm; vor der Abgabe ebenfalls mit einem tatsächlich umgesetzten Use Case abgleichen.

**Abgabehinweis:** Der Projektauftrag verlangt ein aktuelles Klassendiagramm, ein Sequenzdiagramm, nachvollziehbare Commits sowie eine Demonstration der vorhandenen Funktionen. Diese Dokumentationsaktualisierung ändert weder Diagramme noch Quellcode und prüft keine Git-Historie.

## KI-Unterstützung

ChatGPT wurde über weite Teile des Projekts als Navigator, Erklärhilfe und Code-Reviewer verwendet. Die Datei `DeviceFactory.kt` und ein Vorschlag zur Initialisierung der Startgeräte stammen aus einer konkreten KI-Codeunterstützung. **Am 2. November 2026 hat ChatGPT dieses README und `Architecture.md` anhand des bereitgestellten Quellcode-Stands redaktionell aktualisiert und die Änderung in `KI_Nutzung.md` protokolliert.** Die Diagramme werden vom Projektverfasser selbst aktualisiert. Einzelheiten und die Abgrenzung zur Eigenleistung stehen in [KI_Nutzung.md](./KI_Nutzung.md).
