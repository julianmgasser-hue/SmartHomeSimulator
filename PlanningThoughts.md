# Planningthoughts
*PS: This is a File of all my thoughts,
written down for Planning the Architecture of this Project*

Was will ich?
Ich habe eine Objekt, dass ich möglichst gut verwalten muss.... 
es kann ja eine IoTDevice klasse geben, diese hat dann 2 unterklassen,
eine, die Hauptsächlich aktionen ausführt (und daten beeinflusst) und eine,
die Daten generiert/hat. (z.b. ein sensor), es gibt auch mischformen, wie eine Lampe,
bei der man sowohl wissen muss, was ihr status ist, als auch eine aktion ausführen kann.
Entweder mache ich es mit Interfaces, was eigentlich gut wäre,
weil dann müsste man nicht für jede neue Geräteart eine Child klasse zu machen,
und kann jedem, der eine bestimmte Funktion machen muss, das Interface mitgeben,
im Auftrag steht ja, wenn man es begründen kann, muss man nicht Vererbung nutzen.
d.h. wir haben eine IoTGerät klasse, von der alle Geräte erben? ich glaube,
dass macht sinn, weil trotz den interfaces will man e.v. mal alle Geräte ansprechen...
wobei, wenn man z.b. UC alle Daten anzeigen lassen nimmt, dann wären das ja nur die,
die das Daten auslesen Interface hätten... Ja, ich glaube,
ich brauche gar keine Vererbung. Ich packe einfach alle IoT Geräte in ein package.
Dann gibt es einen IOManager, der die User schnittstelle verwaltet,
also auch alle printstatements. Hmm.. wir haben ja noch frames... wir müssen schauen,
dass jedes Frame eigentständig reproduzierbar ist,
vielleicht gibt es da was effizienteres als ein JSON, einfach eine simple serilization?
Jedes Frame sollte stateless sein, ich glaube, ich speichere jedes Frame auf einem 
Stack und der neuste wird jeweils in die Speicherdatei gespeichert.
Das ist ziemlich performance lastig, ich kann schauen, dass ich möglichst wenige daten
speichern muss und den rest aus möglichst wenigen Daten evaluieren kann.
Es soll sicher einen speichermanager geben, der zum einen den Stack managed 
und zum anderen das Speicherfile. Es gibt dann immer einen Rerender.
Dort werden jegliche Geräte einmal laufen gelassen, und am evaluiert,
ob eine Aktion darauf reagiert werden muss. Wie mache ich das mit dem Evaluieren?
Wenn ich die Daten zu beginn verändere durch simulierte werte und dann bereits
evaluiere, ob etwas passiert und das auch noch ausgeführt wird 
(angenommen folgender usecase: wir haben eine pflanze, 
die eine feuchtigkeitssensor hat. Die feuchtigkeit ist zu tief,
also wird ein prozess ausgeführt, dass die Pflanze bewässert wird,
jetzt ist der state der pflanze anders), muss ich jetzt nochmals kontrollieren,
was passiert ist? und muss ich dann nochmals aktionen ausführen?
oder mache ich eine Aufgabenliste und ein aktor  darf erst im nächsten Frame 
etwas durchführen? Es gibt sicherlich mal eine Hauptklasse, die den Ablauf managed.
Und wie mache ich das mit den User Inputs? Sind die asynchron mit der Simulation?
muss ich ncoh eine schreibschützung machen, dass ich nicht daten korrupte? 
(also nur wenn es asynchron ist). Zum Ablauf in einem Tick: ich glaube,
ich mache es wie folgt: es gibt 2 arten von Frames. Aktualisierungsframes und 
Aktionsframes. Wenn es Aufgaben in der ActionQueue hat, dann gibt es ein Aktionsframe,
wenn es keine gibt, wird eine Aktualisierungsframe ausgeführt. 
Ein Aktionsframe führt alle aufgaben aus, die basierend auf Aktionsevaluationen 
oder spielern aufgetragen wurden. Aktualisierungsframes aktualisieren frames generieren 
neue (sensor) daten und führen anschliessend eine evalierung durch und suchen für User
Inputs. Da User Inputs asynchron sind, gibt es auch eine Queue für User Inputs.
Da nur neue Aktionen in die Queue kommen können, wenn man evaluiert und die User Queue
in die Aktionsqueue führt, wird sichergestellt, dass es regelmässig
Aktualisierungsframes gibt. UserActions gehen immer direkt in die UserActionQueue,
damit sie direkt im nächsten (passenden) Frame behandelt werden. Oh!
Users brauchen zusätzlich noch 2 arten von actions!
1. Aktionsbefehle und 2. Anzeigebefehle (1 für eingriff in die Simulation),
2. 2 für anzeigen der Daten der simulation
Hm... ich muss auch Multithreaden, wenn ich will, dass der User Inputs geben kann,
unabhängig von der Simulation, muss das in verschiedenen Prozessen sein. Wenn der user 
immer mega schnell eingibt würde es auch ohne gehen, und wenn man eine lausige 
Simulation macht, auch aber readln sollte die Simulation nicht beeinflussen.

## Anpassung der Frame-Verwaltung
PS: Die gesamte Architektur wurde von mir geplant, dieser Abschnitt wurde aber von KI zusammengefasst. 

Ursprünglich war geplant, pro Sekunde entweder einen Aktualisierungsframe oder einen Aktionsframe auszuführen.

Dieses Konzept wurde angepasst, da dadurch die Aktualisierung der Sensorwerte von der Anzahl auszuführender Aktionen abhängig wäre.

Neu wird jede Sekunde ein Aktualisierungsframe eingeplant. Zwischen den Aktualisierungsframes werden möglichst viele Aktionsframes verarbeitet.

Sollte die Verarbeitung der Aktionen länger dauern, bleiben die noch nicht begonnenen Aktionen in der Warteschlange.

Der nächste Aktualisierungsframe erhält Priorität, sobald der aktuell ausgeführte Aktionsframe abgeschlossen ist.

Damit wird sichergestellt, dass die Simulation regelmässig neue Daten generiert und trotzdem möglichst viele Aktionen ausführen kann.
