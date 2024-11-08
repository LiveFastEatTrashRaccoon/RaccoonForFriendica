<div align="right">
Lingua: <a href="../main.html">🇬🇧</a>
</div>

# Manuale utente

Benvenuto nel manuale utente di Raccoon for Friendica! Questa pagina contiene una spiegazione delle
principali funzionalità dell'app e intende essere una guida il più possibile esaustiva a tutto
ciò che puoi (o non puoi) fare dall'app.

## Indice

- [Introduzione](#introduzione)
- [Struttura generale della UI](#struttura-generale-della-ui)
- [Timeline](#timeline)
- [Dettaglio post](#dettaglio-post)
- [Dettaglio immagine](#dettaglio-immagine)
- [Profilo utente](#profilo-utente)
  - [Modalità classica](#modalità-classica)
  - [Modalità forum](#modalità-forum)
- [Dettaglio conversazione](#dettaglio-discussione)
- [Elenco post contenenti un hashtag](#elenco-post-contententi-un-hashtag)
- [Preferiti & segnalibri](#preferiti--segnalibri)
- [Hashtag seguiti](#hashtag-seguiti)
- [Esplora](#esplora)
- [Ricerca](#ricerca)
- [Notifiche](#notifiche)
- [Profilo](#profilo)
  - [Login](#login)
  - [Il tuo profilo](#il-tuo-profilo)
- [Elenco utenti](#elenco-utenti)
- [Richieste di essere seguito](#richieste-di-essere-seguito)
- [Informazioni istanza](#informazioni-istanza)
- [Informazioni sull'applicazione](#informazioni-sullapplicazione)
- [Impostazioni account](#impostazioni-account)
- [Impostazioni applicazione](#impostazioni-applicazione)
- [Gestione filtri](#gestione-filtri)
- [Creazione post](#creazione-post)
- [Creazione segnalazione](#creazione-segnalazione)
- [Cerchie](#cerchie)
- [Messaggi diretti](#messaggi-diretti-solo-friendica)
- [Galleria](#galleria-solo-friendica)
- [Elementi da pubblicare](#elementi-non-pubblicati)
- [Calendario](#calendario-solo-friendica)

## Introduzione

**Friendica** è una piattaforma social e ha tutte le funzionalità che ci si aspetta da un software
di questo tipo, in particolare permette di:

- visualizzare la timeline dei post creati da altri utenti;
- seguire un hashtag e visualizzare tutti i post che lo contengono;
- creare nuovi post di primo livello o risposte ai post di altri, o pianificarne la pubblicazione
  per un secondo momento (e modificare o eliminare i tuoi post);
- mettere "mi piace" o "non mi piace" ai post;
- salvare un post nei segnalibri;
- ricondividere un post;
- seguire/smettere di seguire altri utenti, visualizzare il loro profilo e iscriversi per ricevere
  notifiche sulla loro attività;
- modificare il tuo profilo e accettare/rifiutare le richieste di essere seguito;
- creare i feed personalizzati;
- visualizzare gli hashtag, post o link di tendenza (e i suggerimenti su chi seguire);
- effettuare una ricerca globale su hashtag, post o utenti;
- disattivare o bloccare gli utenti per filtrare i contenuti indesiderati (e annullare tutte queste
  azioni);
- segnalare post e utenti agli amministratori, ecc.

Oltre a ciò, Friendica può vantare una serie di caratteristiche interessanti che lo distinguono da
altre piattaforme federate simili:

- permette di pubblicare post lunghi e formattati (con una sintassi basata su BBCode) e aggiungere
  un titolo o uno spoiler per ogni post;
- supporta le entità "gruppo" di ActivityPub, ovvero tipi speciali di account che ricondividono
  automaticamente tutti i contenuti dove sono menzionati agli abbonati e si comportano in maniera
  simile a quella di un forum;
- implementa il concetto di gruppi definiti dall'utente o _cerchie_, che possono essere utilizzati
  anche come destinatari per la pubblicazione (in uscita) e non solo come timeline personalizzate
  (in ingresso);
- messaggi diretti: permette di inviare direttamente un messaggio ad uno degli utenti che segui ed
  avere una conversazione privata;
- galleria fotografica: permette di caricare foto e organizzarle in album, inoltre puoi inserire gli
  allegati facilmente nei tuoi post selezionandoli dal catalogo;
- calendario eventi: permette di creare eventi e renderli visibili ai tuoi seguaci oppure
  visualizzare gli venti che sono stati condivisi con te;
- supporta la citazione di post all'interno di altri post (ovvero il _cross-posting_);
- permette di importare feed RSS in modo da poterli seguire come account normali e ricondividere
  i loro post;
- permette di delegare la gestione di un account ad uno o più altri account e creare in modo molto
  semplice degli account indipendenti associati al tuo;
- e, ovviamente, [molto altro ancora](https://friendi.ca/about/features).

È possibile accedere a tutte queste funzionalità utilizzando l'interfaccia web ufficiale, tuttavia
sono disponibili (per molte di queste) anche una serie di API pubbliche che possono essere richimate
da app di terze parti.
E questo è esattamente quello che fa Raccoon: utilizza le API per creare un client Friendica pensato
per facilitarne l'utilizzo da dispositivo mobile.

[Torna su](#indice)

## Struttura generale della UI

L'interfaccia utente dell'applicazione è divisa in tre parti:

- menu laterale di navigazione (accessibile dall'icona hamburger nell'angolo in alto a sinistra o
  con uno swipe da sinistra a destra): se hai effettuato l'accesso mostra il tuo nome utente e
  l'istanza cui sei connesso oppure, in modalità anonima, mstra semplicemente il nome dell'istanza –
  più una serie di scorciatoie a diverse sezioni dell'app (es. le impostazioni o la informazioni
  sull'istanza attuale);
- la barra di navigazione inferiore, con le scorciatoie per le sezioni più importanti dell'app
  (Timeline, Esplora, Notifiche e Profilo), visibile solo nelle schermate di primo livello;
- il contenuto principale, solitamente strutturato nel seguente modo:
  - barra superiore contenente:
    - icona di navigazione (hamburger o pulsante Indietro) nell'angolo a sinistra;
    - il titolo della schermata;
    - una o più azioni (opzionali) nell'angolo a destra o il menu a scomparsa "⋮";
  - il contenuto della schermata;
  - barra inferiore (opzionale, ad esempio la barra degli strumenti di formattazione e/o un
    campo di inserimento testo).

Tutti i componenti utilizzati provengono dal design system [Material 3](https://m3.material.io/).

[Torna su](#indice)

## Timeline

La sezione Timeline contiene una serie di post che appartengono a uno degli elenchi predefiniti
o personalizzati.

Esistono tre tipi di elenchi predefiniti:

- **Locale** (ovvero post che sono stati creati o ricondivisi sull'istanza a cui sei connesso);
- **Tutti** (ovvero post provenienti dalla tua istanza più tutte le istanze federate);
- **Iscrizioni** (solo se hai effettuato l'accesso: post creati o ricondivisi dagli account che stai
  seguendo oppure contenenti uno o più hashtag che stai seguendo).

Gli elenchi personalizzati, d'altra parte, possono essere:

- una delle [liste da te definite](#cerchie);
- un canale, ovvero le aggregazioni predefinite come "Per te", "Scopri", "Seguaci", "Immagini", ecc.
  (specifico di Friendica);
- uno dei gruppi che segui (specifico di Friendica);

Ogni elemento nella timeline ha la seguente struttura:

- indicazione di ricondivisione o risposta (l'utente che ha ricondiviso e l'autore del post
  originale);
- autore (immagine profilo, nome visualizzato e nome utente);
- data di creazione;
- titolo (solo sui tipi di istanze che lo permettono, ad es. Friendica o Lemmy);
- testo dello spoiler (opzionale);
- contenuto testuale (visibile senza spoiler o quando lo spoiler è espanso);
- allegati (video o immagini, opzionali);
- scheda di anteprima (contenuto aggiuntivo o URL esterno).

Se un post contiene hashtag sarà possibile aprire per ognuno di essi
l'[elenco dei post che lo contengono](#elenco-post-contententi-un-hashtag), se invece
contiene menzioni sarè possible aprire la schermata [profilo](#profilo-utente) corrispondente.

Ogni post può essere ricondiviso, aggiunto ai preferiti o aggiunto ai segnalibri; puoi creare una
risposta e visualizzare il numero di risposte / ricondivisioni o se esso è presente nei tuoi
segnalibri o meno.

Inoltre per ogni post è possibile:

- accedere al [profilo](#profilo-utente) di tutti gli utenti coinvolti (autore, autore della
  riconsidivzione
  o autore originale);
- entrare nella schermata di [dettaglio post](#dettaglio-post);
- aprire il menu di azione a scomparsa per:
  - condividerlo tramite il meccanismo del sistema;
  - copiare il suo URL negli appunti;
  - copiare il titolo e il contenuto negli appunti;
  - silenziare l'autore a tempo indeterminato o per un periodo di tempo limitato;
  - bloccare l'autore;
  - segnalare il post o il suo autore agli amministratori per la moderazione;
  - citarlo (ovvero effettuare il cross-post, solo su Friendica);
  - aprire una schermata "Dettagli" con il codice sorgente e una serie informazioni aggiuntive;
  - modificalo o cancellalo (solo se l'autore sei tu).

<div align="center">
  <img width="310" alt="timeline screen" src="../images/timeline.png" />
  <img width="310" alt="timeline with mute user dialog" src="../images/mute_bottom_sheet.png" />
</div>

[Torna su](#indice)

## Dettaglio post

In questa schermata permette di visualizzare un post nel suo contesto (ovvero tutti i post
precedenti fino a quello radice da cui è si originata la conversazione e tutte le risposte che sono
state ricevute).

Inoltre è possibile visualizzare l'elenco degli utenti che lo hanno ricondiviso o aggiunto ai
preferiti.

Comunque, questa schermata è molto simile ad una normale [timeline](#timeline) e consente di
eseguire le stesse azioni su post e aprire profili utente o il dettaglio di altri post.

<div align="center">
  <img width="310" alt="post detail screen" src="../images/post_detail.png" />
</div>

[Torna su](#indice)

## Dettaglio immagine

Da un video o un'immagine allegata è possibile aprire il visualizzatore a schermo intero.
Per le immagini, fai doppio tap per accedere alla modalità "pinch to zoom".

Dal menu azioni della barra superiore, è possibile scaricare l'allegato sul tuo dispositivo,
condividerlo come file o come URL e, per le immagini, regolare le proporzioni nel caso in cui non
venissero visualizzate correttamente.

<div align="center">
  <img width="310" alt="zoomable image detail screen" src="../images/image_detail.png" />
</div>

[Torna su](#indice)

## Profilo utente

Lo scopo del profilo utente è visualizzare informazioni su un utente specifico e accedere
all'insieme dei contenuti che ha creato. Sono disponibili due versioni di questa schermata:
la _modalità classica_ (per account individuali) e la _modalità forum_ (per account di gruppo).

### Modalità classica

Questa schermata si compone di due parti:

- un'intestazione con nome visualizzato, nome utente, immagine profilo e immagine di copertina, il
  numero di seguaci/seguiti (da da cui è possibile aprire la lista utenti), biografia e una serie
  di campi personalizzati;
- l'elenco dei post creati dall'utente, suddiviso nelle seguenti sezioni:
  - **Post** elenco dei post di primo livello creati dall'utente;
  - **Post e risposte** tutti i post comprese le risposte creati dall'utente;
  - **Media** ovvero post contenenti allegati multimediali;
  - **Fissati** ovvero i post fissati dall'utente sul proprio profilo;

Ciascun elemento di questi elenchi ti consente di accedere al
relativo [dettaglio post](#dettaglio-post).

Se hai effettuato l'accesso, l'intestazione mostrerà la tua relazione con questo utente:

- **Reciproci** (se vi seguite a vicenda);
- **Segui già** (se tu segui l'utente ma non segue te);
- **Ti segue** (se l'utente ti segue ma tu non lo segui);
- **Richiesta inviata** (se hai inviato una richiesta di seguirlo in attesa di approvazione);
- **Richiesta in attesa** (se hai ricevuto una richiesta di essere seguito in attesa di
  approvazione);
- nessuna relazione.

Se segui l'utente, vedrai anche lo stato della notifica (abilitata o disabilitata).

Dal menu a scomparsa nella barra superiore, è inoltre possibile:

- bloccare/sbloccare l'utente;
- silenziare (specificando per quanto tempo) e de-silenziare l'utente;
- segnalare l'utente;
- aggiungere una nota personale;
- passare alla [modalità forum](#modalità-forum) (se si tratta di un gruppo).

<div align="center">
  <img width="310" alt="user detail (classic mode) screen" src="../images/user_detail.png" />
</div>

### Modalità forum

In modalità forum vengono presentati tutti i post di primo livello che sono stati
ricondivisi dal gruppo, che possono essere intrpretati come l'elenco degli argomenti del forum.
Si tratta di un tipo speciale di timeline da cui, facendo tap su ciascuno elemento sarà possibile
accedere al [dettaglio discussione](#dettaglio-discussione).

Dal menu a scomparsa nella barra superiore, è inoltre possibile passare
alla [modalità classica](#modalità-classica).

<div align="center">
  <img width="310" alt="user detail (forum mode) screen" src="../images/forum_list.png" />
</div>

[Torna su](#indice)

## Dettaglio discussione

Questa schermata è molto simile a un [dettaglio post](#dettaglio-post) ma i commenti vengono
visualizzati
con un layout in stile Lemmy, ovvero con un rientro a sinistra variabile in base al loro
livello di annidamento e con una barra colorata che rende più facile individuare le relazioni di
discendenza tra ogni post e le sue risposte.

<div align="center">
  <img width="310" alt="thread detail screen" src="../images/thread_detail.png" />
</div>

[Torna su](#indice)

## Elenco post contententi un hashtag

L'elenco post contenenti un hashtag è un tipo speciale di [timeline](#timeline) che consiste
nell'aggregazione di tutti i post con all'interno determinato hashtag. Per il resto, questa
schermata ha la stessa struttura di una timeline.

Dalla barra superiore è possibile seguire o smettere di seguire l'hashtag in questione.

<div align="center">
  <img width="310" alt="hashtag feed screen" src="../images/hashtag_feed.png" />
</div>

[Torna su](#indice)

## Preferiti & segnalibri

I preferiti e i segnalibri sono tipi speciali di [timeline](#timeline) e hanno in tutto e per tutto
la struttura delle timeline.

L'unica differenza è che, se un post viene rimosso dai preferiti o dai segnalibri, esso scomparirà
immediatamente dall'elenco.

<div align="center">
  <img width="310" alt="favorites screen" src="../images/favorites.png" />
  <img width="310" alt="bookmarks screen" src="../images/bookmarks.png" />
</div>

[Torna su](#indice)

## Hashtag seguiti

Questa schermata contiene l'elenco di tutti gli hashtag seguiti in ordine alfabetico e permette di
smettere di seguirli.

Ciascun elemento di questa lista permette di aprire il
relativo [elenco post](#elenco-post-contententi-un-hashtag).

<div align="center">
  <img width="310" alt="followed hashtags screen" src="../images/followed_hashtags.png" />
</div>

[Torna su](#indice)

## Esplora

A partire da questa sezione è possibile vedere i contenuti di tendenza nell'istanza a cui sei
connesso. La schermata è suddivisa nelle seguenti sezioni:

- **Hashtag** contiene l'elenco degli hashtag di tendenza, con il numero di persone che ne parlano
  e un grafico sul suo utilizzo nell'ultima settimana;<a href="#hashtag-usage-disclaimer">*</a>
- **Post** contenente l'elenco dei post di tendenza;
- **Link** visualizzazione aggregata degli URL utilizzati più frequentemente nei post;
- (solo per utenti registrati) **Per te** contiene l'elenco dei suggerimenti su chi seguire per il
  tuo utente.

L'apertura di un hashtag ti porterà all'[elenco dedicato](#elenco-post-contententi-un-hashtag),
facendo tap su un post è possibile accedere al relativo [dettaglio](#post-dettaglio) e facendo tap
su un utente si aprirà il relativo [profilo](#profilo-utente). Invece, al tap su un
collegamento aprirà il browser esterno o una custom tab a seconda dell'opzione "Modalità apertura
URL" selezionata nelle [impostazioni](#impostazioni-applicazione).

<p id="hashtag-usage-disclaimer">
* su server diversi il numero può variare da 1 a 7 giorni a seconda del tipo di istanza
</p>

<div align="center">
  <img width="310" alt="explore screen (hashtags section)" src="../images/explore_hashtags.png" />
  <img width="310" alt="explore screen (links section)" src="../images/explore_links.png" />
</div>

[Torna su](#indice)

## Ricerca

Questa schermata permette di effettuare ricerche nel Fediverso: contiene un campo di ricerca per
inserire il testo da usare come criterio e un selettore a schede per selezionare il tipo di
risultato desiderato (Post, Utenti o Hashtag).

Tieni presente che non è possibile effettuare la ricerca a meno che non sia stata inserito un
criterio non vuoto.

L'apertura di un hashtag ti porterà al relativo [elenco](#elenco-post-contententi-un-hashtag),
facendo tap su un post si aprirà il relativo [dettaglio](#dettaglio-post) e facendo tap su un utente
il relativo [profilo](#profilo-utente).

<div align="center">
  <img width="310" alt="search screen (posts section with 'raccoon' keyword)" src="../images/search.png" />
</div>

[Torna su](#indice)

## Notifiche

Questa schermata contiene l'elenco delle notifiche per gli eventi a cui sei iscritto, ed è
disponibile unicamente per gli utenti registrati.

Il contenuto principale presenta l'elenco delle notifiche, che possono essere delle seguenti
tipologie:

- **Post** un utente per cui hai abilitato le notifiche ha pubblicato un nuovo post;
- **Modifica** un post che hai ricondiviso è stato modificato dal suo autore;
- **Menzione** sei stato menzionato in un post;
- **Ricondivisione** uno dei tuoi post è stato ricondiviso;
- **Preferito** uno dei tuoi post è stato aggiunto ai preferiti;
- **Seguace** qualcuno ha iniziato a seguirti;
- **Richiesta di essere seguito** qualcuno ti ha inviato una richiesta di essere seguito;
- **Sondaggio** un sondaggio a cui hai partecipato è scaduto.

Dal menù della barra superiore è possibile selezionare/deselezionare specifiche categorie di
notifiche
e filtrare i risultati visualizzati nell'elenco.

Inoltre, in questa schermata è possibile:

- contrassegnare tutte le notifiche come lette, semplicemente aggiornando la pagina (che azzererà il
  contatore delle non lette);
- eliminare tutte le notifiche utilizzando il pulsante "Checkmark" nella barra in alto.

(Attenzione: dopo essere state eliminate, le notifiche verranno cancellate dal server e non sarà
più possible visualizzarne l'elenco!)

Facendo tap su ciascuna voce della lista è possibile aprire il
relativo [profilo utente](#profilo-utente) o [dettaglio post](#dettaglio-post).

<div align="center">
  <img width="310" alt="inbox screen" src="../images/inbox.png" />
</div>

[Torna su](#indice)

## Profilo

Se stai eseguendo l'app in modalità anonima, la schermata Profilo contiene il pulsante Accedi per
avviare il flusso di autenticazione. Se invece hai già effettuato l'accesso, verrà utilizzata una
schermata simile a un normale [profilo utente](#profilo-utente), con alcune azioni aggiuntive
specifiche per il tuo account.

Se disponi di più account, nella barra superiore apparirà un pulsante "Gestisci account" per
effettuare il passaggio da uno all'altro.

### Login

Il metodo più comune per accedere alle piattaforme federate come Friendica o Mastodon è OAuth2,
ovvero un flusso basato sul browser grazie al quale all'app viene concesso un token temporaneo con
cui eseguire una serie di operazioni specifiche per conto dell'utente (ad esempio creare post,
seguire un utente, ottenere l'elenco dei post da chi si segue, ecc.).

Questo metodo è preferibile in quanto:

- il tuo nome utente/password non lasciano mai dal browser e rimangono sconosciuto a tutti i
  soggetti terzi (inclusa l'app Raccoon);
- il livello di accesso è più granulare, il che significa che è possibile controllare i vari ambiti
  (o _scope_) per cui ogni singolo token può essere utilizzato;
- i token possono essere revocati in qualsiasi momento, facilitando la mitigazione degli accessi
  potenzialmente indesiderati.

I server Friendica, d'altro canto, consentono agli utenti di autenticare le chiamate utilizzando
lo standard HTTP Basic, che richiede l'immissione delle credenziali (nome utente e password)
nell'app di terze parti che intende utilizzarle.

Il flusso di accesso prevede due tra questi passaggi:

- una schermata introduttiva dove si sceglie la piattaforma e la modalità di login (la doppia scelta
  OAuth2/HTTP Basic è disponibile solo su Friendica)
- la selezione dell'istanza (solo per OAuth2) dove specificare il server a cui connettersi; per
  Friendica è possbile scegliere da un menù a tendina o inserire manualmente il nome di dominio se
  non presente, per tutte le altre piattaforme dovrai inserire manualmente il dominio del server (ad
  es. `mastodon.social`);
- inserire il dominio dell'istanza e le credenziali (solo per HTTP Basic), dove dovrai selezionare
  la tua istanza Friendica (da un elenco a tendina o inserendolo manualmente se non presente) e
  immettere le tue credenziali.

Se non disponi di un account, è necessario prima di tutto crearlo dal sito web dell'istanza, quindi
effettuare il login nell'app mobile.

<div align="center">
  <img width="310" alt="login intro screen" src="../images/login_1.png" />
  <img width="310" alt="select instance screen" src="../images/login_2.png" />
  <img width="310" alt="select instance screen" src="../images/login_3.png" />
</div>

### Il tuo profilo

Come affermato in precedenza, questa schermata è molto simile a un
normale [profilo utente](#profilo-utente). Nell'intestazione, al posto del pulsante per seguire o
attivare le notifiche sarà presente un pulsante "Modifica profilo"
che permette di accedere alle [preferenze del profilo](#profile-settings).

<div align="center">
  <img width="310" alt="profile screen" src="../images/profile.png" />
</div>

[Torna su](#indice)

## Elenco utenti

Questa schermata contiene un elenco generico di utenti; può essere aperto sia
dal [dettaglio post](#dettaglio-post) (per visualizzare chi ha aggiunto un post ai preferiti o lo ha
ricondiviso) o dal [profilo utente](#profilo-utente) (per visualizzare chi sta seguendo o è seguito
da un determinato utente). Vengono inoltre mostrati l'immagine profilo, il nome visualizzato e il
nome utente, oltre al corrispondente stato di relazione con il tuo account.

Puoi utilizzare il pulsante Segui/Invia richiesta/Reciproci per modificare la tua relazione con
questo utente.

<div align="center">
  <img width="310" alt="user list screen (profile following)" src="../images/user_list.png" />
</div>

[Torna su](#indice)

## Richieste di essere seguito

Se nelle tue [impostazioni del profilo](#impostazioni-account) hai abilitato l'approvazione manuale
per le richieste di essere seguito, questa schermata contiene l'elenco delle richieste da te
ricevute che risultano in attesa di approvazione.

Per ciascuna delle voci dell'elenco è possibile accettare o rifiutare la richiesta.

<div align="center">
  <img width="310" alt="follow request screen" src="../images/follow_requests.png" />
</div>

[Torna su](#indice)

## Informazioni istanza

Questa schermata contiene una serie di informazioni relative all'istanza cui si connesso, in
particolare:

- immagine di copertina;
- nome di dominio;
- descrizione;
- contatto utente principale;
- lista di regole che devono essere rispettate dai membri dell'istanza;
- tipologia di backend e versione del software in esecuzione sul server.

<div align="center">
  <img width="310" alt="node info screen" src="../images/node_info.png" />
</div>

[Torna su](#indice)

## Informazioni sull'applicazione

Questa finestra di dialogo contiene alcune informazioni aggiuntive a proposito dell'app:

- nome e codice di versione;
- link all'elenco delle modifiche;
- un pulsante per aprire un form di inserimento commenti;
- il link alla pagina principale dell'app su GitHub;
- una scorciatoia di accesso al gruppo Friendica dedicato all'app;
- un link alla room Matrix dedicata al progetto;
- l'accesso alla lista delle licenze relative alle librerie e alle risorse usate nell'app.

<div align="center">
  <img width="310" alt="app information dialog" src="../images/app_info.png" />
  <img width="310" alt="licenses screen" src="../images/licenses.png" />
</div>

[Torna su](#indice)

## Impostazioni account

Questa schermata ti consente di modificare i dati del tuo profilo e configurare (in una certa
misura) la rilevabilità
e visibilità del tuo profilo.

I dati del profilo che possono essere modificati sono:

- nome visualizzato;
- biografia;
- immagine profilo;<a href="#user-profile-experimental-disclaimer">*</a>
- immagine copertina;<a href="#profilo-utente-disclaimer-sperimentale">*</a>
- campi personalizzati;<a href="#user-profile-experimental-disclaimer">*</a>
- bot (per contrassegnare l'account come bot);
- abilita approvazione manuale delle richieste di essere seguito (`blocked`);
- rendi l'account visibile nelle ricerche (`discoverable`);
- rendi private le liste dei seguaci e seguiti (`hide_collections`);
- includi i post creati da questo account nella timeline pubblica (`indexable`).

<p id="user-profile-experimental-disclaimer">
* a seconda del backend questi campi potrebbero non essere modificabili, per es. ci sono alcuni 
problemi di compatibilità noti in alcune versioni di Friendica
</p>

<div align="center">
  <img width="310" alt="edit profile screen" src="../images/edit_profile.png" />
</div>

[Torna su](#indice)

## Impostazioni applicazione

Questa schermata consente di personalizzare l'aspetto e il comportamento dell'applicazione. È
costituita dalle seguenti sezioni:

- **Generali**
  - **Lingua** configura la lingua dell'interfaccia utente;
  - **Tipo di feed predefinito** configura il tipo di elenco predefinito della schermata Timeline;
  - **Visibilità predefinita post** configura la visibilità predefinita (pubblica, non in lista,
    solo seguaci o solo menzioni) usata per i nuovi post;
  - **Visibilità predefinita risposte** configura la visibilità predefinita delle risposte;
  - **Modalità apertura URL** configura la modalità di apertura degli URL (sul browser di sistema
    oppure tramite "custom tab");
  - **Escludi risposte dalla timeline** determina se le risposte vengono incluse o meno nella
    Timeline principale;
  - **Apri i gruppi in modalità forum di default** determina se gli account di gruppo vengono aperti
    di default in modalità forum (invece che in modalità classica);
  - **Carica immagini automaticamente** se abilitata le immagini vengono caricate automaticamente,
    se disabilitata le immagini vengono caricate su richiesta (per risparmare utilizzo di dati
    mobili);
  - **Markup per la composizione** determina la sintassi di formattazione per i nuovi post (testo
    semplice — ovvero senza formattazione — BBCode — specifico di Friendica — HTML o Markdown —
    specifico di Mastodon;<a href="#markdown-formatting">*</a>
  - **Numero massimo righe testo post** configura il numero massimo di righe che verrà visualizzato
    negli elenchi per ciascun post;
  - **Modalità notifiche** permtte di configurare la strategia da usare per le notifiche scegliendo
    tra i seguenti valori:
    - **Push** ricevi le notifiche push attraverso
      UnifiedPush;<a href="#notifications-unifiedpush">*</a>
    - **Pull** controlla periodicamente in background la presenza di nuove
      notifiche;<a href="#notifications-background-check">**</a>
    - **Disabilitate** le notifiche saranno ricevute all'avvio dell'app o facendo refresh della
      lista manualmente;
  - **Stato notifiche push** visualizza lo stato dell'integrazione UnifiedPush attuale (es. "Attivo"
    o "Inizializzazione…", se sul dispositivo sono disposnibili più distributori la voce sarà "
    Seleziona distributore" e permetterà di aprire una bottom sheet per sceglierne uno);
  - **Controlla notifiche in background** (se la strategia selezionata è "Pull") configura
    l'intervallo di tempo tra le verifiche periodiche delle notifiche in arrivo;
- **Aspetto**
  - **Tema UI** configura il tema di colori(chiaro, scuro o scuro ottimizzato per display AMOLED);
  - **Tipo di carattere** configura il tipo di carattere usato nell'interfaccia utente;
  - **Dimensione testo** configura il fattore di scala che sarà applicato ai font nell'interfaccia
    utente;
  - **Colore tema** permette di selezionare un colore a partire dal quale verrà generata una palette
    di colori Material 3;
  - **Material You** genera una palette di colori a partire dall'immagine di sfondo;
- **NSFW**
  - **Gestione filtri** apre la schermata di [gestione](#gestione-filtri) degli utenti bloccati e
    silenziati;
  - **Includi contenuti NSFW** abilita un filtro lato client sui contenuti contrassegnati come
    sensibili;
  - **Offusca media NSFW** permette, se i contenuti sensibili sono inclusi, di offuscare le immagini
    e nascondere i video all'interno delle timeline.

<p id="markdown-formatting">
* scgliere con cautela: <em>Markdown</em> è supportato solo da alcune versioni di Mastodon (es.
glitch-soc), se non sei sicuro/a di quel che stai facendo usa HTML o testo semplice, altrimenti i 
tuoi post potrebbero non essere formattati correttamente
</p>

<p id="notifications-unifiedpush">
** affinché UnifiedPush funzioni, sul tuo dispositivo deve essere installato un distributore, ad es.
<a href="https://unifiedpush.org/users/distributors/nextpush/">NextPush</a> che deve essere abbinato
all'app NextPush ospitata in Cloud dove hai effettuato l'accesso e configurato l'account remoto che
stati utilizzando sulla tua istanza
</p>

<p id="notifications-background-check">
*** per funzionare, è necessario che all'app non sia applicata alcuna restrizione per l'attività in
background, controlla le impostazioni di risparmio energetico di Raccoon nelle impostazioni di sistema
</p>

<div align="center">
  <img width="310" alt="edit profile screen" src="../images/settings.png" />
</div>

[Torna su](#indice)

## Gestione filtri

Questa schermata consente di revocare le restrizioni (silenziare o bloccare) attualmente applicate
ad altri account. È suddivisa in due sezioni:

- **Silenziati** dedicata agli utenti che sono stati silenziati;
- **Bloccati** dedicata agli utenti bloccati.

<div align="center">
  <img width="310" alt="manage bans screen" src="../images/manage_bans.png" />
</div>

[Torna su](#indice)

## Creazione post

This screen allows to create new posts or replies. The top bar contains the Submit button which can
have different icons depending on the publishing type:

- a Send icon for regular publication;
- a Save icon for drafts;
- a Schedule icon for scheduled posts;

whereas the action menu contains the following items:

- **Save draft** changes the publishing type from regular to draft;
- **Set schedule** changes the publishing type from regular to scheduled posts;
- **Insert emoji** allows to insert a custom emoji;
- **Open preview** opens a preview of the post (only if "Markup for compositing" option in Settings
  is
  _not_ plain text);
- **Add title**/**Remove title** to add or remove a title for the post;
- **Add image (media gallery)** adds an image from an album in the Friendica media gallery;
- **Insert list** adds an itemized list;
- **Add spoiler**/**Remove spoiler** (only if "Markup for compositing" option in Settings is plain
  text) to add or remove a spoiler for the post;
- **Add image** (only if "Markup for compositing" option in Settings is plain text) adds an image
  from
  the device gallery;

Below the top bar there is a header containing:

- an indication of the current user (who will be the author of the post);
- the visibility (`public`, `unlisted`, `private`, `direct` or a Friendica circle);
- the schedule date and time (for scheduled posts);
- the current character count / character limit according to instance configuration.

Below the header you can find the main text field for the post body. In the bottom part of the
screen, only if "Markup for compositing" option in Settings is _not_ plain text, you will find a
formatting toolbar with the following buttons:

- **Add image** to add an image from the device gallery;
- **Add link** to add a hyperlink
- **Bold** to insert some text in bold;
- **Italic** to insert some text in italic;
- **Underline** to insert some underlined text;
- **Strikethrough** to insert some text with a strikethrough effect;
- **Code** to insert monospaced font;
- **Toggle spoiler** to add or remove a spoiler for the post.

<div align="center">
  <img width="310" alt="composer screen" src="../images/composer.png" />
</div>

[Torna su](#indice)

## Creazione segnalazione

This screen allows to create a report for either a user or a specific post.

It contains a selector to choose the kind of violation among:

- **Spam** the post is spam or user is spamming;
- **Legal** issue the post or user infringes some existing legislation;
- **Rule** (Mastodon-only) the post or user does not follow the instance rules (in this case you
  will have to select which rule is violated);<a href="#report-rule-disclaimer">*</a>
- **Other** any other kind of issue.

Below you can write the report body in a text field and, finally, the "Forward report" switch allows
you to select whether this report should only be delivered to your instance admins or, if you are
reporting a content coming from a federated instance, it should be handled by the admins of the
source instance too.<a href="#report-forward-disclaimer">**</a>

<p id="report-rule-disclaimer">
* on Friendica rule violation are not supported yet and if they are submitted they get rejected by
the server, so this option is hidden
</p>
<p id="report-forward-disclaimer">
** on Friendica this switch is supported (does not cause the request to be rejected by the server)
but it has no effect
</p>

<div align="center">
  <img width="310" alt="create report screen" src="../images/report.png" />
</div>

[Torna su](#indice)

## Lascia un commento

This form allows you to submit your feedback to the developers. Apart from writing your comment, you
can optionally specify an email address (e.g. if you wish to be contacted for clarifications). This
is just a utility screen for those who prefer not to use the issue tracker.

<div align="center">
  <img width="310" alt="submit user feedback screen" src="../images/user_feedback.png" />
</div>

[Torna su](#indice)

## Cerchie

This screens contains all the custom feeds that can be used in the timeline, which comprise:

- user-defined lists (like Mastodon lists or
  Friendica [circles](https://wiki.friendi.ca/docs/groups-and-privacy));
- Friendica [channels](https://wiki.friendi.ca/docs/channel);
- one item for each group among the accounts you follow.

> The reason why all these heterogeneous elements are in the same list is because they are all
> returned by the same API for compatibility with Mastodon clients.

Among these three categories, the only one which allow to be modified is the first one, for which:

- you can use the "more" button to edit the name or delete it
- you can enter a circle detail screen to see the contacts that belong to it.

Please remember that in Friendica by default all non-group contacts are added to the "Friends"
circle and all group contacts are added to "Group" but, albeit being there by default, these are
regulars circles that can be changed or deleted.

<div align="center">
  <img width="310" alt="circle list screen" src="../images/circles_list.png" />
  <img width="310" alt="circle detail screen" src="../images/circle_detail.png" />
</div>

[Torna su](#indice)

## Messaggi diretti (solo Friendica)

This section contains the list of private conversations with your contacts. For each
item in the list, you can see:

- the contact avatar, username and display name;
- the title of the conversation (defaulting to the first message);
- some lines of the last message;
- the total number of messages;
- the time elapsed since the last message was received.

Tapping on each item you can open the conversation detail, in the form of a traditional hat with
message bubbles and a text field in the bottom part of the screen to send new ones.

Messages are being downloaded as long as they arrive while you are in this screen, otherwise you
have to manually refresh the conversation list.

<div align="center">
  <img width="310" alt="conversation list screen" src="../images/dm_list.png" />
  <img width="310" alt="conversation  detail screen" src="../images/dm_detail.png" />
</div>

[Torna su](#indice)

## Galleria (solo Friendica)

This screen contains the list of albums in your multimedia gallery. For each album you can either
edit its name or delete it or, tapping on the list item, access the pictures contained in it.

For each picture, you will have the possibility to:

- edit its description;
- delete it from the Gallery;
- move it to another album.

<div align="center">
  <img width="310" alt="gallery list screen" src="../images/gallery_list.png" />
  <img width="310" alt="gallery  detail screen" src="../images/gallery_detail.png" />
</div>

[Torna su](#indice)

## Elementi da pubblicare

This screen contains all the items awaiting publication and it is divided into two sections:

- scheduled i.e. the list scheduled posts;
- drafts i.e. the list of drafts you created;

each item can be deleted or, by tapping on it, opened in edit mode.

Beware that on most instances scheduled posts can **not** be modified except for their schedule
date, so the preferred way to save a post and edit for later is:

- create a post with the "Only mentions" (`direct`) visibility without mentioning any user so that
  it is only visible to you;
- create a draft, keeping in mind that draft are just **local records** saved in the application
  database and are not stored anywhere remotely, so will lose them if you change device or clear the
  application storage.

<div align="center">
  <img width="310" alt="unpublished items screen" src="../images/drafts.png" />
</div>

[Torna su](#indice)

## Calendario (solo Friendica)

This screen shows the event calendar, i.e. the list of events divided by month with, for each item:

- event title
- start date;
- (optional) end date;
- (optional) location.

For each item, you can export it to your device calendar and, by tapping on the item, access a
detail screen where more details (like a more extended description) are displayed.

<div align="center">
  <img width="310" alt="calendar event list screen" src="../images/calendar_list.png" />
  <img width="310" alt="event detail screen" src="../images/calendar_detail.png" />
</div>

[Torna su](#indice)
