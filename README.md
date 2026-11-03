# Doppelkopf – Punktestand & Runden-Tracker

Eine moderne, elegante Android-App zum Erfassen und Verwalten des Punktestands beim Kartenspiel **Doppelkopf** für 4 Spieler. Entwickelt mit **Jetpack Compose** und **Material Design 3** im klassischen Kartentisch-Stil (edles Filzgrün mit goldenen Akzenten).

> **Hinweis:** Dieses Projekt wurde mit **[Google AI Studio](https://aistudio.google.com/)** erstellt.

---

## ✨ Features

- **Zwei Erfassungsmodi:**
  - ⚡ **Schnellmodus (Standard):** Für schnellen Spielfluss am Tisch. Einfach Spielwert eingeben, Siegerteam auswählen und speichern.
  - 📋 **Detailmodus:** Schrittweise Erfassung von Re/Kontra, Absagen (*Keine 90*, *Keine 60*, etc.) und Sonderpunkten (*Doppelkopf*, *Fuchs gefangen*, *Karlchen*, etc.).
- **Plus-Minus-Wertung (Nullsummenspiel):**
  - Gewinner erhalten Pluspunkte, Verlierer erhalten Minuspunkte. Die Summe aller Punkte nach jedem Spiel beträgt exakt 0.
- **Solospiele:**
  - Automatische Dreifach-Wertung für Solospieler (+3x bei Sieg, -3x bei Niederlage).
  - Unterstützung typischer Soli (Farbsolo, Damen-Solo, Buben-Solo, Fleischlos/Trumplos, Köhler).
- **Nullspiele:**
  - Volle Unterstützung für 0-Punkte-Spiele.
- **Intelligente Bockrunden-Verwaltung:**
  - Manuelle Erfassung einer Bockrunde per einfacher Checkbox.
  - **Automatischer Wirkungsbereich:** Gilt ab dem nächsten Spiel für jeweils 4 Spiele.
  - **Kumulative Verdopplung:** Bei Überschneidung aktiver Bockrunden exponentieller Multiplikator ($x2, x4, x8, x16$).
- **Automatisches Geber-Tracking:**
  - Spieler 1 gibt im 1. Spiel, danach reihum Spieler 2, 3, 4 und wieder von vorn. Der nächste Geber wird jederzeit im Header sowie im Dialog angezeigt.
- **Multi-Listen-Verwaltung:**
  - Beliebig viele parallele Spielrunden verwalten (z. B. *„Freitagsrunde“*, *„Familie“*, *„Turnier“*).
  - Schneller Wechsel, Umbenennen und Anlegen neuer Runden über das Menü.
- **Historie & Tabelle:**
  - Antichronologische Historie (neueste Runde immer ganz oben).
  - Maximierter Tabellenfokus: Siegerpunkte in Grün (`+X`), Verliererpunkte in Rot (`-X`).
  - Runden löschen oder korrigieren.
- **Statistik & Visualisierung:**
  - Interaktives Punktekurven-Diagramm über alle Runden.
  - Spielerstatistiken (Gesamtpunkte, Siege, Siegquote, Solo-Erfolge).
- **Vielseitiger Export:**
  - 📄 **PDF-Export:** Druckfertige PDF-Tabelle mit Spielübersicht, Gebern, Bockrunden und Zwischenständen via Android `PdfDocument`.
  - 📊 **CSV-Export:** Strukturierte CSV-Datei für Excel, Google Sheets und Numbers.
  - 💬 **Text-Export:** Schnelles Teilen der Spielstände via Messenger / WhatsApp.
- **Barrierefreiheit & Filter-Optimierung:**
  - Optimiert für **Rotlicht- und Nachtfilter** am Smartphone: Spielernamen und Zwischenstände nutzen hochkontrastreiche, neutrale Textfarben kombiniert mit Farbindikatoren.
  - Vollständiges Edge-to-Edge Design mit dynamischen `WindowInsets`.

---

## 🛠 Tech-Stack & Architektur

- **Sprache:** [Kotlin](https://kotlinlang.org/)
- **UI-Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% deklarative UI)
- **Design System:** Material Design 3 (M3) mit angepasstem Kartentisch-Theme
- **Architektur:** MVVM (Model-View-ViewModel) mit StateFlow & Coroutines
- **Lokale Persistenz:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite) zur dauerhaften Speicherung aller Listen und Runden
- **Testing:** Local JVM Tests & [Robolectric](https://robolectric.org/)

---

## 🚀 Build & Ausführung

### Voraussetzungen
- Android SDK (API 26+)
- JDK 17 oder 21
- Gradle (Kotlin DSL) oder Android Studio Ladybug / Meerkat / neuere Version

### Debug-APK bauen
```bash
gradle assembleDebug
```
Die erstellte APK-Datei befindet sich nach dem Build unter:
`app/build/outputs/apk/debug/app-debug.apk`

### Vollständiger Build
```bash
gradle build
```

---

## 🧪 Tests ausführen

Das Projekt enthält automatisierte Unit- und Logiktests (u. a. für Nullsummen-Berechnung, Solo-Multiplikatoren, Bockrunden-Lebenszyklus und Geber-Rotation):

```bash
gradle :app:testDebugUnitTest
```

Zur Ausführung mit detailliertem Test-Report:
```bash
gradle test
```
Der HTML-Testreport ist anschließend unter `app/build/reports/tests/testDebugUnitTest/index.html` einsehbar.

---

## 📁 Projektstruktur

```text
app/src/main/java/com/example/
├── DoppelkopfApp.kt          # Application-Klasse & Initialisierung
├── MainActivity.kt           # Haupteinstiegspunkt & Edge-to-Edge Setup
├── data/
│   ├── database/             # Room Database, DAOs und TypeConverters
│   ├── export/               # CSV-, Text- und PDF-Export-Generatoren
│   ├── model/                # Datenmodelle (GameSession, RoundRecord, Player)
│   └── repository/           # DoppelkopfRepository für Datenzugriff
├── ui/
│   ├── DoppelkopfAppContent.kt # Haupt-Screen & Routing
│   ├── DoppelkopfViewModel.kt  # State-Management & Spiellogik
│   ├── components/           # Compose-Komponenten (Tabelle, Dialoge, Stats)
│   │   ├── AddRoundDialog.kt           # Runden-Eingabe (Schnell- & Detailmodus)
│   │   ├── EditPlayersDialog.kt        # Spielernamen bearbeiten
│   │   ├── ManageSessionsDialog.kt     # Multi-Listen-Verwaltung
│   │   ├── PlayerCardsHeader.kt        # Kompakter Header mit Geber-Anzeige
│   │   ├── PlayerChartAndStatsView.kt  # Punktekurven-Diagramm & Statistiken
│   │   └── RoundHistoryTable.kt        # Antichronologische Rundentabelle
│   └── theme/                # Farben, Filzgrün-Theme, Typografie
```

---

## 📄 Lizenz

Dieses Projekt steht unter der [MIT License](LICENSE).

---

## 🤖 Erstellt mit Google AI Studio

Diese Anwendung wurde mit Unterstützung von **Google AI Studio** konzipiert, entwickelt und verfeinert.
