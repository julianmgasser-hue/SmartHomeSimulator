# Smart-Home Simulation & Manager
- eine Simulation mehrerer IoT-Geräte und deren Management, alles im Terminal.
## Muss-Anforderungen
- Verschiedene Arten von IoT-Geräten speichern können
- Daten von Geräten (z.b. einem Luftfeuchtigkeitssensor) auslesen können
- Aktionen von Geräten ausführen können (z.b. eine Lampe ein/aus schalten)
- Stand in einem JSON speichern

## Erweiterte Anforderungen
- Automatisierte Logik-Engine, die aktionen ausführt, 
    wenn z.b. Sensorwerte einen gewissen Wert erreichen.
- Exportieren von Protokollen/Daten in ein CSV.

## Use Cases
- Als Nutzer kann ich das Programm neu starten und meine Daten werden gespeichert.
- Als Nutzer kann ich die Daten von einem Gerät auslesen.
- Als Nutzer kann ich die Daten von jeglichen Geräten auslesen.
- Als Nutzer kann ich bestimmte Geräte dazu auffordern, eine Aktion auszuführen.

## Design Patterns
1. Factory Pattern (DeviceFactory):

Wofür: Wenn der Benutzer im Menü ein neues Gerät anlegen will 
(z. B. "1. Lampe", "2. Heizung"), übernimmt eine Factory die Objekterzeugung 
basierend auf der Auswahl. Das hält den Code übersichtlich und trennt 
die UI/Eingabe von der Objekterstellung.

2. Observer Pattern (Sensor & DeviceController):

Wofür: Sensoren überwachen Werte. Wenn sich ein Messwert ändert 
(z. B. Temperatur sinkt unter 18 Grad), benachrichtigt der Sensor 
automatisch den DeviceController, welcher daraufhin gekoppelte Aktionen auslösen kann.