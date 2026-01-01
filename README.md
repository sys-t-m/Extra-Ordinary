# Extra-Ordinary

Extra-ordinary è un'applicazione Android progettata per tracciare le tue ore di lavoro extra in modo semplice ed elegante. Offre un registro pulito delle tue attività, calcoli automatici dei compensi e un'interfaccia moderna che si adatta alle tue preferenze.

## 📥 Download
Scarica subito l'ultima versione installabile (APK) per il tuo telefono Android:

👉 [**Scarica l'ultima versione dell'app**](https://github.com/sys-t-m/Extra-Ordinary/releases/latest)

---

## Funzionalità

* **📅 Gestione Ore:** Inserisci e visualizza facilmente le tue ore lavorate giorno per giorno.
* **💰 Calcoli Automatici:** L'app calcola istantaneamente il totale delle ore e il compenso mensile basandosi sulla tua tariffa.
* **📱 Widget Home Screen:** Aggiungi ore e guadagni della giornata odierna direttamente dalla schermata home, senza aprire l'app.
* **👁️ Privacy Mode:** Nascondi gli importi economici con un tocco quando sei in pubblico.
* **🎨 Temi Dinamici:** Interfaccia moderna che si adatta automaticamente al tema Chiaro o Scuro del sistema.

## Struttura del Progetto

Il progetto segue l'architettura raccomandata per le app Android moderne, utilizzando il pattern **Model-View-ViewModel (MVVM)**.

* **`data`**: Contiene il data layer dell'applicazione, incluso il modello `WorkEntry`.
* **`ui`**: Il layer dell'interfaccia utente, che include le schermate (`screens`), i `viewmodel` e i componenti UI riutilizzabili.
* **`utils`**: Funzioni di utilità e classi helper (es. calcolo date e festività).
* **`widget`**: Codice e logica per il widget della schermata home.

## Per Iniziare (Sviluppo)

Per compilare ed eseguire il progetto in locale, avrai bisogno di:

* Android Studio
* Un dispositivo Android o un emulatore

Clona la repository e aprila in Android Studio. Il progetto potrà poi essere compilato ed eseguito sul tuo dispositivo o emulatore.

## Contribuire

I contributi sono benvenuti! Sentiti libero di inviare una pull request o aprire una "issue" se hai suggerimenti o se trovi dei bug.