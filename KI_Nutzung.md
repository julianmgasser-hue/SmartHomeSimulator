# Protokoll der KI-Nutzung – SmartHomeSimulator

**Projekt:** SmartHomeSimulator (Kotlin), Kompetenznachweis M320  
**Dokumentationsstand:** 2. November 2026  
**Verwendetes KI-Werkzeug:** ChatGPT (OpenAI)

Dieses Protokoll unterscheidet zwischen **Beratung/Review**, **von der KI erstelltem Code** und **durch KI redaktionell erstellter Dokumentation**. Es soll nachvollziehbar machen, welche Beiträge nicht ausschliesslich selbst verfasst wurden.

## 1. KI als Navigator, Erklärhilfe und Code-Reviewer

Während der Projektentwicklung wurde ChatGPT überwiegend für fachliche Rückfragen und zur Überprüfung bereits vorhandener Arbeit eingesetzt. Dazu gehörten:

- Besprechen der Projektidee, der M320-Kompetenzkriterien und möglicher Prioritäten.
- Diskutieren von Architekturentscheidungen, insbesondere Interfaces, Vererbung, Polymorphismus, Frames, Warteschlangen, Automatisierungen und Design Patterns.
- Erklärungen und kleinere Kotlin-Beispiele zu objektorientierten Konzepten.
- Rückmeldungen zu UML-Klassen- und Sequenzdiagrammen; gemäss der bisherigen Projektdokumentation wurde auch ein PlantUML-Entwurf für ein Sequenzdiagramm geliefert.
- Mehrere Reviews von bereitgestellten Projekt-ZIP-Dateien. Dabei wurden unter anderem der `IOManager`, `ProgrammManager`, `Queue<T>`, Sensoren und Commands sowie Fehler bei der Geräteinteraktion besprochen.
- Hinweise darauf, welche ursprünglich geplanten Funktionen noch nicht umgesetzt waren und welche Anforderungen durch den vorhandenen Code nachweisbar sind.

Diese Art von Unterstützung war hauptsächlich **Lernbegleitung, Erklärung und Codekritik**. Sie darf nicht mit einer vollständigen KI-Erstellung des Projekts gleichgesetzt werden.

## 2. Konkrete KI-Codeunterstützung: `DeviceFactory` (1. November 2026)

**Ausgangslage:** Das Command Pattern war bereits mit `Command` und konkreten Command-Klassen im Projekt vorhanden. Als zweites Muster wurde eine Factory zur zentralen Erstellung von IoT-Geräten besprochen.

**Benutzeranfrage (Originalformulierung):**

> schreibe du ein CommandPattern, das ich direkt in mein Programm einfuegen kann, voll funktionsfaehig und erstelle ein 2. file, wo du den AI gebraucht dokumentierst, bis jetzt warst du ja hauptsaechlich mein Navigator

Obwohl in dieser Anfrage „CommandPattern“ steht, bezog sich der unmittelbar vorangehende Austausch auf das fehlende **Factory Pattern**. Der KI-Assistent lieferte deshalb Code für eine **Simple Factory** und legte das erste Protokoll `KI_Nutzung.md` an.

**KI-Beiträge:**

- `src/DeviceFactory.kt` mit `DeviceFactory` und `DeviceType`, um Lampen, Sensoren und Wasserspender anhand eines Typs zu erzeugen.
- Vorschlag für eine Anpassung von `src/ProgrammManager.kt`, sodass die Startgeräte über die Factory instanziiert werden.
- Eine erste Fassung von `KI_Nutzung.md` zur Offenlegung der Unterstützung.

In `DeviceFactory.kt` ist ein Kommentar vorhanden, der auf die KI-Unterstützung und dieses Protokoll hinweist.

**Technische Einordnung:** Die erstellte Lösung ist eine **Simple Factory**, nicht zwingend die klassische GoF-Variante *Factory Method*. Der Name allein ist kein Nachweis für die formale Implementierung dieses GoF-Patterns.

**Integration im aktuellen Projektstand:** Die Factory wird für die Startgeräte verwendet. Zusätzlich existieren mittlerweile der Konsolenbefehl `make` und `CreateDeviceCommand`; diese sind im hochgeladenen Quellcode vorhanden. Aus dem aktuellen Dateistand allein lässt sich die Entstehung jeder späteren Änderung nicht vollständig rekonstruieren. Für diese Dokumentationsaktualisierung wurden daran keine Änderungen vorgenommen.

## 3. Frühere KI-Unterstützung bei der Dokumentation

Bereits während der Planungsphase wurden Entwürfe von `README.md` und `Architecture.md` mithilfe von ChatGPT redaktionell überarbeitet. Grundlage waren die eigenen Überlegungen aus `PlanningThoughts.md`, frühere Dokumentationsentwürfe und die im Gespräch besprochenen Entscheidungen.

Die dabei dokumentierte umfangreichere Architektur (unter anderem zwei Threads, `UserActionQueue`, Observer Pattern, `StorageManager` und JSON-Persistenz) war **eine Planung**. Sie war nicht in jedem Punkt bereits implementiert. Die ursprünglichen Notizen werden weiterhin unverändert aufbewahrt, damit sich die Entwicklung des Konzepts nachvollziehen lässt.

## 4. KI-Aktualisierung der Dokumentation (2. November 2026)

**Benutzeranfrage (Originalformulierung):**

> ok, dann aktualisiere meine Dokumentation und KI nutzungsprotokoll, dass du mir die Doku aktualisiert hast. Basierend auf diesem Stand. Das neue UML mache ich.

**Arbeitsgrundlage:** Das vom Projektverfasser bereitgestellte Archiv `SmartHomeSimulator-main(4).zip`, einschliesslich des Kotlin-Quellcodes und der bisherigen Markdown-Dokumentation.

**In dieser Runde durch ChatGPT bearbeitete Dateien:**

| Datei | Art der KI-Leistung |
| --- | --- |
| `README.md` | Inhaltlich und sprachlich **neu auf den tatsächlichen Implementierungsstand ausgerichtet**; Funktionen, Bedienung, Use Cases, zwei Designmuster und bekannte Einschränkungen dokumentiert. |
| `Architecture.md` | Bestehende, hauptsächlich geplante Architektur durch eine **quellcodebasierte Beschreibung der vorhandenen Klassen, Abläufe und Beziehungen** ersetzt; verworfene oder noch offene Konzepte explizit gekennzeichnet. |
| `KI_Nutzung.md` | Dieses Protokoll **aktualisiert und ergänzt**, einschliesslich der erneuten KI-Dokumentationsarbeit. |

**Wichtige redaktionelle Korrekturen gegenüber dem vorherigen Dokumentationsstand:**

- Keine Behauptung mehr, die Simulation laufe bereits auf einem separaten Thread oder unabhängig von `readln()`.
- `Command Pattern` und `Simple Factory` als vorhandene Entwurfslösungen beschrieben; `Observer Pattern` als **nicht implementiert** gekennzeichnet.
- Vorhandene Automatisierungsstruktur von tatsächlich eingerichteten Automatisierungsregeln unterschieden.
- Nicht vorhandene JSON-Persistenz, Frame-Historie, `StorageManager` und `UserActionQueue` aus der Beschreibung der implementierten Funktionen entfernt.
- `make`, `CreateDeviceCommand`, die eigenen Frames und die generische Queue anhand des Codes dokumentiert.
- Sichtbare Probleme offengelegt: invertierte Ja/Nein-Abfrage, fehlende Aktualisierung der Sensorliste für neu erstellte Sensoren sowie nur teilweise robuste Fehlerbehandlung.

**Abgrenzung dieser konkreten KI-Leistung:** Es wurden **ausschliesslich die drei Markdown-Dateien** angepasst. Es wurden **keine Kotlin-Quellcodedateien, UML-Grafiken, Sequenzdiagramme oder Git-Commits** durch ChatGPT verändert. Die Aktualisierung beruht auf einer Durchsicht des Quellcodes; sie ist **kein Nachweis eines vollständigen Laufzeittests** des zuletzt bereitgestellten Projektstands.

**UML:** Der Projektverfasser hat ausdrücklich erklärt, das neue UML-Diagramm selbst zu erstellen. `ClassUML.png` und `SequenceDiagramm.png` wurden in dieser Runde daher nicht bearbeitet. Eine spätere KI-Unterstützung bei Diagrammen müsste gesondert ergänzt werden.

## 5. Tests und Grenzen der KI-Überprüfung

Das frühere Protokoll hält fest, dass die erste Factory-Version mit einem separaten Kotlin-Test auf die Erzeugung der drei Gerätetypen, die Sensorkonfiguration und das Ablehnen eines leeren Namens geprüft wurde. Auch ein Kompilierungstest mit einem älteren Kotlin-1.9-Compiler wurde damals dokumentiert; dazu war **nur in einer temporären Testkopie** eine Anpassung von `queue.addFirst(item)` nötig. Diese zeitlich frühere Prüfung bezog sich **nicht automatisch auf alle späteren Änderungen**.

Die Aktualisierung vom **2. November 2026** bestand aus einer Quellcode- und Dokumentationsdurchsicht. Die währenddessen notierten Einschränkungen sind keine Behauptung, dass sämtliche Programmabläufe getestet worden wären.

## 6. Eigenleistung, Verantwortung und Offenlegung

- Die Projektidee und die ursprünglichen Überlegungen sind in [PlanningThoughts.md](./PlanningThoughts.md) festgehalten. Die Planung wurde im Gespräch mit ChatGPT weiterentwickelt.
- Die unter Punkt 2 aufgeführten KI-Codebeiträge dürfen **nicht als ausschliesslich selbstgeschriebener Code** ausgewiesen werden.
- Die Fassungen von `README.md`, `Architecture.md` und `KI_Nutzung.md` vom 2. November 2026 sind **inhaltlich und redaktionell von ChatGPT erstellt beziehungsweise überarbeitet** und müssen vor der Abgabe vom Projektverfasser kontrolliert werden.
- Die Verantwortung für die tatsächliche Funktionsfähigkeit, die Richtigkeit der Dokumentation, die eigene Erklärung der angewandten Konzepte und die Abgabe liegt beim Projektverfasser.
- Dieses Protokoll ersetzt nicht die nachvollziehbare Git-Historie und nicht ein persönliches Lernfazit. Falls weitere KI-generierte Codeausschnitte oder Diagramme übernommen werden, ist ihre Herkunft ebenfalls kenntlich zu machen.

**Hinweis zum Kompetenznachweis:** KI-Unterstützung ist erlaubt, muss aber transparent gemacht werden; eine vollständig KI-generierte Anwendung wäre nach den Projektvorgaben nicht zulässig.
