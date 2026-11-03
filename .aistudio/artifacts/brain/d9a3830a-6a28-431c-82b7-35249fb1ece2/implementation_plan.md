# Doppelkopf Punktezähler – 4 Spieler (Schnell- & Detailmodus)

Ein hochoptimierter, eleganter Doppelkopf-Punktezähler im edlen Filzgrün-Design für 4 Spieler. Die App minimiert die Eingabezeit am Spieltisch durch einen ultraschnellen Standard-Eingabemodus und bietet gleichzeitig ein Geber-Tracking, exponentielle Bockrunden (x2, x4, x8, x16), eine detaillierte Spieltabelle inklusive Spielnummer & Spielwert sowie umfassende Verlaufs- und Exportfunktionen.

---

## Benutzerfeedback & Überarbeitete Kernentscheidungen

> [!IMPORTANT]
> Die folgenden Anpassungen wurden gemäß Benutzeranforderung eingearbeitet:

1. **Zwei Eingabemodi (Default = Schneller Modus)**:
   - **Schneller Modus (Standard)**: Am Tisch wird der Punktwert oft im Kopf zusammengerechnet. Man wählt nur das Gewinnerteam (z. B. Klick auf 2 Spieler im Normalspiel oder 1 Solist) und gibt die errechneten Punkte ein. Maximale Geschwindigkeit, minimale Touch-Interaktion!
   - **Ausführlicher Rechner-Modus**: Optional umschaltbar für Runden, in denen Ansagen (Re, Kontra, keine 90 etc.) und Sonderpunkte (Fuchs, Karlchen, Doppelkopf) Schritt für Schritt per Klickbox zusammengerechnet werden sollen.
2. **Geber-Tracking (Wer gibt als Nächstes?)**:
   - Spieler 1 gibt im 1. Spiel, danach Spieler 2, 3, 4 und wieder rotierend von vorn.
   - Sichtbare Geber-Markierung (Kartenstapel / Geber-Chip) in der Spielerliste und in der Kopfzeile.
3. **Exponentielle Bockrunden (x2, x4, x8, x16)**:
   - Einfache Steuerung: Bockrunden als Tickbox / Schnellzähler.
   - Bei 1 aktiven Bockrunde: Multiplikator **x2**.
   - Bei 2 gleichzeitigen Bockrunden: Multiplikator **x4**.
   - Bei 3 gleichzeitigen Bockrunden: Multiplikator **x8**.
   - Bei 4 gleichzeitigen Bockrunden: Multiplikator **x16**.
   - Die jeweils aktive Bockstufe wird in der Tabelle und bei jedem Spiel exakt vermerkt.
4. **Spielnummer & Spielwert**:
   - Die Historientabelle enthält separate Spalten für:
     - **# (Spielnummer)**
     - **Geber**
     - **Spielwert** (die Punkte der Runde vor/nach Multiplikator)
     - **Punkte der 4 Spieler** (laufender Kontostand und Deltas)
5. **Null-Punkte-Spiele (0 Punkte)**:
   - Erlaubt Spiele mit 0 Punkten (z. B. 120:120 ohne Re/Kontra-Ansage oder unentschiedenes Spaltspiel).
   - Spielnummer rückt vor, Geber wechselt, Bockrunde läuft weiter oder wird ausgelöst.
6. **Solospiel-Verdreifachung**:
   - Der Solist erhält bei Gewinn die 3-fache Punktzahl (`+3 * Spielwert`), die anderen 3 Spieler je `-1 * Spielwert`.
   - Bei Verlust verliert der Solist `-3 * Spielwert`, die anderen 3 Spieler erhalten je `+1 * Spielwert`. (Nullsumme: stets 0).

---

## 1. Benutzeroberfläche & Layout ("Kartentisch")

### Haupt-Dashboard (Spieltisch)
- **Kopfbereich (Spieler & Geber)**:
  - 4 kompakte Spielerkarten nebeneinander.
  - Zeigt Name, aktuellen Gesamtpunktestand (mit Farbcodierung grün/rot) und ein prominentes **Geber-Symbol (🂠 Geber)** beim aktuellen Geber.
- **Bockrunden-Statusleiste**:
  - Zeigt die aktuelle Stufe: z. B. *"Keine Bockrunde (x1)"* oder *"Bockrunde aktiv: 2 Runden parallel (x4)"*.
  - Schnell-Aktion: Button "+1 Bockrunde (4 Spiele)" oder Schnell-Toggle.
- **Schnelle Rundeneingabe (Bottom-Sheet / Eingabefeld)**:
  - **Tabs / Umschalter**: `[ Schnelleingabe (Default) ]` | `[ Detaillierter Rechner ]`
  - **Im Schnellmodus**:
    1. Typ wählen: `[ Normalspiel ]` oder `[ Solo (x3) ]`
    2. Wer hat gewonnen? Klick auf die 2 Sieger (bei Normal) bzw. den 1 Solisten (und Sieg/Niederlage Toggle)
    3. Punkte eintragen: Ziffern-Schnellwahl (`0`, `1`, `2`, `3`, `4`, `5`, `6`, `+`)
    4. Bockrunde aktiv? Checkbox (bereits vorselektiert falls Bockrunde läuft, manuell anpassbar)
    5. Button: **"Spiel werten"** (1 Klick, sofort gebucht!)
- **Rundentabelle & Historie**:
  - Doko-Tabelle im klassischen Notizblock-Stil mit Spalten:
    - **#**: Spielnummer (1, 2, 3...)
    - **Geber**: Name / Initiale
    - **Spiel**: Spielwert (z. B. `+3` oder `+6 (x2)`)
    - **Sp1 | Sp2 | Sp3 | Sp4**: Jeweiliger Zwischenstand nach dem Spiel (mit kleinem `+3` / `-3` Delta darunter).
  - Letzte Runde löschen ("Rückgängig"-Button), falls man sich vertippt hat.

### Visualisierung & Statistik
- **Punkteverlaufsgraph**: Canvas-Liniendiagramm aller 4 Spieler über den gesamten Spielverlauf mit Zoom/Scroll für viele Runden.
- **Statistiktabelle**:
  - Spiele gesamt
  - Gewonnene / verlorene Spiele je Spieler
  - Gespielte Soli & Solo-Erfolgsquote
  - Höchster Punktestand & aktuelle Serie
- **Export-Funktion**:
  - Button "Ergebnisse exportieren": Teilt sauberen Textbericht oder CSV via Android Share Sheet (z. B. für WhatsApp-Gruppe).

---

## 2. Technische Architektur & Datenmodell

```
┌─────────────────────────────────────────────────────────────┐
│                   Doppelkopf Kartentisch UI                 │
│                                                             │
│  ┌───────────────────────┐       ┌───────────────────────┐  │
│  │   4x Spielerkarten    │       │  Bockrunden-Widget    │  │
│  │   mit Geber-Marker    │       │  x2, x4, x8, x16      │  │
│  └───────────┬───────────┘       └───────────┬───────────┘  │
│              │                               │              │
│              ▼                               ▼              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │          Eingabe-Dialog (Schnell / Detailliert)       │  │
│  │  - Schnellauswahl Sieger & Punktzahl                  │  │
│  │  - Solo x3 Logik & 0-Punkte-Support                   │  │
│  │  - Bockrunden-Checkbox                                │  │
│  └───────────────────────────┬───────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  DoppelkopfViewModel                  │  │
│  │  - Geber-Rotations-Logik: dealerIndex = (round - 1) % 4│ │
│  │  - Bock-Multiplikator: 2^(activeBockRounds)           │  │
│  │  - Nullsummen-Berechnung                              │  │
│  │  - Undo-Historie                                      │  │
│  └───────────────────────────┬───────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  Room Local Database                  │  │
│  │  - Session & GameRound Entities                       │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Exponentielle Bockrunden-Formel
- Anzahl aktive Bockrunden $N \in \{0, 1, 2, 3, 4\}$:
  - $N = 0 \rightarrow \times 1$
  - $N = 1 \rightarrow \times 2$
  - $N = 2 \rightarrow \times 4$
  - $N = 3 \rightarrow \times 8$
  - $N = 4 \rightarrow \times 16$
- Jedes gespielte Bockspiel dekrementiert die verbleibenden Bockspiele.

### Geber-Formel
- Geber von Spiel $R$ (1-basiert): $\text{Geber} = \text{Spieler}[(R - 1) \pmod 4]$.

---

## 3. Verifikationsplan

1. **Schnellmodus**: Eingabe von 2 Siegern und 3 Punkten -> Spielwert 3, Sieger je +3, Verlierer je -3. Geber wechselt von Spieler 1 zu Spieler 2.
2. **Solospiel**: Solist gewinnt mit 2 Punkten -> Spielwert 2, Solist erhält +6 (+3x2), die anderen drei je -2.
3. **Null-Punkte-Spiel**: Spiel mit 0 Punkten eintragen -> Spielstand bleibt unverändert, Spielnummer steigt um 1, Geber rückt weiter.
4. **Exponentielle Bockrunde**:
   - 1 Bockrunde aktiv -> Multiplikator x2.
   - 2 Bockrunden parallel -> Multiplikator x4.
   - In der Historientabelle wird "x2" bzw. "x4" beim Spielwert ausgewiesen.
5. **Undo-Funktion**: Letztes Spiel rückgängig machen -> stellt vorherigen Spielstand, Geber und Bockzähler wieder her.
6. **Export**: CSV- und Text-Formatierung testen.
