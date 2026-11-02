# Dokumentation der KI-Unterstuetzung – SmartHomeSimulator

**Projekt:** SmartHomeSimulator (Kotlin), Kompetenznachweis M320  
**Datum dieses Eintrags:** 1. November 2026

## 1. Bisherige Unterstuetzung

ChatGPT wurde im Projekt bisher hauptsaechlich als Lernhilfe und Code-Reviewer eingesetzt. Die Unterstuetzung umfasste insbesondere:

- Besprechung der Projektidee und Anforderungen, Architekturentscheidungen und ihrer Vor- und Nachteile.
- Rueckmeldungen zu den UML-Klassen- und Sequenzdiagrammen; fuer ein Sequenzdiagramm wurde auch ein PlantUML-Entwurf geliefert.
- Redaktionelle Ueberarbeitung von `README.md` und `Architecture.md` auf Basis der eigenen Planungsnotizen. Die inhaltlichen Entscheidungen stammen vom Projektverfasser und wurden mit KI diskutiert.
- Erklaerungen und einzelne Kotlin-Codebeispiele zu Vererbung, Interfaces, Polymorphismus, generischen Queues und zum Command Pattern.
- Mehrere Code-Reviews anhand der vom Projektverfasser bereitgestellten Repository-ZIP-Dateien, insbesondere zu `ProgrammManager`, `IOManager`, Sensoren, Queues und Commands.

Die bisherige Beratung bedeutet **nicht**, dass saemtlicher Projektcode durch KI geschrieben wurde, es wurde fast nichts mit KI geschrieben, was mit KI geschrieben wurde, ist mit Kommentaren beschrieben.

## 2. Konkrete KI-Codeerstellung: DeviceFactory

**Ausgangslage:** Im Projekt war das Command Pattern bereits durch `Command` und mehrere konkrete Command-Klassen vorhanden. Als zweites Design Pattern wurde deshalb eine einfache Factory zur Erzeugung verschiedener IoT-Geraete vorgeschlagen.

**Aktuelle Anfrage (Originalformulierung):**

> schreibe du ein CommandPattern, das ich direkt in mein Programm einfuegen kann, voll funktionsfaehig und erstelle ein 2. file, wo du den AI gebraucht dokumentierst, bis jetzt warst du ja hauptsaechlich mein Navigator

Da die vorherige Diskussion ausdruecklich das **Factory Pattern** als fehlendes zweites Pattern behandelte und das Command Pattern bereits implementiert war, wurde diese Anfrage als Bitte um das **Factory Pattern** interpretiert. Diese Interpretation ist vom Projektverfasser zu pruefen.

**Von KI neu erstellt:**

- `src/DeviceFactory.kt`: Klasse `DeviceFactory` und Enum `DeviceType`. Die Factory erstellt `Lamp`, `Sensor` oder `WaterDispenser` und prueft, ob der Geraetename leer ist.

**Von KI vorgeschlagene Integrationsaenderung:**

- `src/ProgrammManager.kt`: Initialisierung der drei Startgeraete ueber `DeviceFactory` statt direkter Konstruktoraufrufe. An der weiteren Simulation wurde nichts geaendert.

**Zweck:** Die Erzeugung konkreter Geraete von der Programmsteuerung trennen. Die Factory gibt den gemeinsamen Basistyp `IoTDevice` zurueck; damit kann die bestehende Geraeteverwaltung unterschiedliche konkrete Typen behandeln.

**Einordnung:** Die Umsetzung ist eine **Simple Factory**, nicht zwingend das GoF-Factory-Method-Pattern. Der Projektverfasser sollte mit der Lehrperson klaeren, ob diese Form als zweites Design Pattern gewertet wird.

## 3. Ueberpruefung und Eigenleistung

- Die vom KI-Assistenten erstellten Aenderungen duerfen nicht als ausschliesslich eigene Codearbeit ausgegeben werden.
- Vor der Abgabe soll der Projektverfasser die neuen Klassen und Methoden erklaeren, selbst testen und bei Bedarf veraendern.
- Die uebrige Implementierung bleibt bei der bisherigen Eigenarbeit; diese Dokumentation ersetzt weder die eigenen Git-Commits noch das geforderte Lernfazit.
- Die Aufgabe erlaubt KI-Unterstuetzung, verbietet aber eine vollstaendig KI-generierte Anwendung. Die konkrete Herkunft weiterer uebernommener KI-Codebeispiele sollte nachvollziehbar ergaenzt werden.

## 4. Technische Pruefung durch die KI

- Die neue `DeviceFactory` wurde mit einem kleinen separaten Kotlin-Test geprueft: Erzeugung der drei vorhandenen Geraetetypen, Sensor-Typ und Ablehnung eines leeren Namens waren erfolgreich.
- Der Quellcode des Projekts inklusive Factory und angepasstem `ProgrammManager` wurde mit einem vorhandenen Kotlin-1.9-Compiler testweise kompiliert. Fuer diesen Test wurde **ausschliesslich in einer temporaeren Kopie** `queue.addFirst(item)` wegen der aelteren Kotlin-Version durch `queue.add(0, item)` ersetzt. Im zur Abgabe vorgesehenen Quellcode wurde `Queue.kt` nicht veraendert.
- Die vollstaendige Anwendung wurde damit **nicht** als End-to-End-Test durchgespielt; insbesondere die Konsolenfunktionen und die Automatisierung koennen weitere Fehler enthalten.

## 5. Offene Punkte

- Die Factory wird mit der Initialisierung der vorhandenen Startgeraete genutzt. Ein Terminal-Befehl zum Erstellen weiterer Geraete ist damit **noch nicht** implementiert.
- Bestehende Probleme ausserhalb der Factory (z. B. in der Benutzerinteraktion oder der Simulation) werden durch diese Aenderung nicht automatisch behoben.
- `README.md`, `Architecture.md` und UML-Diagramme sollten nach Abschluss der Implementierung an den tatsaechlichen Stand angepasst werden.
