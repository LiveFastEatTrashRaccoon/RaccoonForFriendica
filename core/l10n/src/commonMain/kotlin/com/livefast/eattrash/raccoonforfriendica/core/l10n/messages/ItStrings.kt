package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

internal val ItStrings =
    object : DefaultStrings() {
        override val messageConfirmExit = "Premi di nuovo 🔙 per uscire"
        override val messageAreYouSure = "Sei sicuro/a di voler continuare"
        override val messageSuccess = "Operazione completata con successo!"
        override val messageGenericError = "Si è verificato un errore inaspettato"
        override val messageInvalidField = "Campo non valido"
        override val messageMissingField = "Campo obbligatorio"
        override val messageEmptyList = "Qui non c'è nulla da visualizzare 🗑️"
        override val buttonConfirm = "Conferma"
        override val buttonOk = "OK"
        override val buttonClose = "Chiudi"
        override val buttonCancel = "Annulla"
        override val systemDefault = "Sistema"
        override val settingsThemeLight = "Chiaro"
        override val settingsThemeDark = "Scuro"
        override val settingsThemeBlack = "Nero (AMOLED)"
        override val sectionTitleHome = "Home"
        override val sectionTitleExplore = "Esplora"
        override val sectionTitleInbox = "Inbox"
        override val sectionTitleProfile = "Profilo"
        override val barThemeOpaque = "Opaco"
        override val barThemeTransparent = "Trasparente"
        override val timelineAll = "Tutti"
        override val timelineSubscriptions = "Iscrizioni"
        override val timelineLocal = "Locale"
        override val nodeVia = "via"
        override val timelineEntryInReplyTo = "in risposta a"
        override val timelineEntryRebloggedBy = "ricondiviso da"

        override fun accountFollower(count: Int) =
            when (count) {
                1 -> "seguace"
                else -> "seguaci"
            }

        override fun accountFollowing(count: Int) =
            when (count) {
                1 -> "seguito"
                else -> "seguiti"
            }

        override val actionOpenInForumMode = "apri in modalità forum"
        override val accountAge = "età account"
        override val dateYearShort = "a"
        override val dateMonthShort = "m"
        override val dateDayShort = "g"
        override val timeHourShort = "h"
        override val timeMinuteShort = "min"
        override val timeSecondShort = "s"
        override val accountSectionPosts = "Post"
        override val accountSectionAll = "Post & risposte"
        override val accountSectionPinned = "Fissati"
        override val accountSectionMedia = "Media"
        override val postTitleBy = "Post di"
        override val settingsTitle = "Impostazioni"
        override val settingsHeaderGeneral = "Generali"
        override val settingsItemLanguage = "Lingua"
        override val settingsHeaderLookAndFeel = "Aspetto"
        override val settingsItemTheme = "Tema UI"
        override val settingsItemFontFamily = "Tipo di carattere"
        override val settingsItemDynamicColors = "Material You"
        override val settingsItemDynamicColorsSubtitle =
            "genera palette in base al colore dello sfondo"
        override val settingsItemThemeColor = "Colore tema"
        override val settingsItemThemeColorSubtitle =
            "applicato solo se \"Material You\" è disabilitato"
        override val themeColorPurple = "Piovra portentosa"
        override val themeColorBlue = "Balena ballerina"
        override val themeColorLightBlue = "Delfino distratto"
        override val themeColorGreen = "Rana rilassata"
        override val themeColorYellow = "Riccio rampante"
        override val themeColorOrange = "Volpe virtuosa"
        override val themeColorRed = "Granchio galante"
        override val themeColorPink = "Unicorno unico"
        override val themeColorGray = "Procione pasticcione"
        override val themeColorWhite = "Orso originale"
        override val messageUserUnlogged = "È necessario effettuare il login per poter accedere a questa sezione 🪵"
        override val loginTitle = "Login"
        override val fieldNodeName = "Nome istanza"
        override val fieldUsername = "Username"
        override val fieldPassword = "Password"
        override val actionLogout = "Logout"
        override val relationshipStatusFollowing = "Stai seguendo"
        override val relationshipStatusFollowsYou = "Ti segue"
        override val relationshipStatusMutual = "Reciproci"
        override val relationshipStatusRequestedToOther = "Richiesta inviata"
        override val relationshipStatusRequestedToYou = "Richiesta in attesa"
        override val notificationTypeEntry = "ha pubblicato un post"
        override val notificationTypeFavorite = "ha aggiunto ai preferiti un tuo post"
        override val notificationTypeFollow = "ha iniziato a seguirti"
        override val notificationTypeFollowRequest = "ha inviato la richiesta di seguirti"
        override val notificationTypeMention = "ti ha menzionato in un post"
        override val notificationTypePoll = "vedi i risultati di un sondaggio cui hai partecipato"
        override val notificationTypeReblog = "ha ricondiviso un tuo post"
        override val notificationTypeUpdate = "ha aggiornato un post che hai ricondiviso"
        override val exploreSectionHashtags = "Hashtag"
        override val exploreSectionLinks = "Link"
        override val exploreSectionSuggestions = "Per te"

        override fun hashtagPeopleUsing(count: Int) =
            when (count) {
                1 -> "persona ne sta parlando"
                else -> "persone ne stanno parlando"
            }

        override val feedTypeTitle = "Tipo di feed"
        override val followerTitle = "Seguaci"
        override val followingTitle = "Seguiti"
        override val actionFollow = "Segui"
        override val actionUnfollow = "Non seguire più"
        override val actionDeleteFollowRequest = "Annulla richiesta di seguire"
        override val sidebarAnonymousTitle = "Anonimo"
        override val sidebarAnonymousMessage =
            "Benvenuto/a su Raccoon!\n\nPuoi effettuare il login sulla tua istanza in qualsiasi momento dalla schermata Profilo.\n\nDivertiti su Friendica!"
        override val bookmarksTitle = "Segnalibri"
        override val favoritesTitle = "Preferiti"
        override val followedHashtagsTitle = "Hashtag che segui"
        override val infoEdited = "modificato"

        override fun extendedSocialInfoFavorites(count: Int) =
            when (count) {
                1 -> "preferito"
                else -> "preferiti"
            }

        override fun extendedSocialInfoReblogs(count: Int) =
            when (count) {
                1 -> "ricondivisione"
                else -> "ricondivisioni"
            }

        override val actionMuteNotifications = "Silenzia notifiche"
        override val createPostTitle = "Nuovo post"
        override val messagePostEmptyText = "È necessario postare almeno un allegato o del testo"
        override val visibilityPublic = "Pubblico"
        override val visibilityUnlisted = "Non in lista"
        override val visibilityPrivate = "Privato"
        override val visibilityDirect = "Solo menzioni"
        override val createPostBodyPlaceholder = "Un magnifico nuovo post..."
        override val createPostAttachmentsSection = "Allegati"
        override val actionEdit = "Modifica"
        override val pictureDescriptionPlaceholder = "Descrizione immagine"
        override val insertLinkDialogTitle = "Inserisci link"
        override val insertLinkFieldAnchor = "Ancora"
        override val insertLinkFieldUrl = "URL"
        override val selectUserDialogTitle = "Seleziona utente"
        override val selectUserSearchPlaceholder = "nome utente or identificativo"
        override val searchSectionUsers = "Utenti"
        override val searchPlaceholder = "Cerca nel Fediverso"
        override val messageSearchInitialEmpty = "Inserisci una nuova espressione di ricerca 🔦"
        override val topicTitle = "Argomento"
        override val threadTitle = "Conversazione"
        override val buttonLoadMoreReplies = "Carica altre risposte"
        override val postSensitive = "Contenuto sensibile"
        override val actionCreateThreadInGroup = "Posta su"
        override val settingsHeaderNsfw = "NSFW"
        override val settingsItemIncludeNsfw = "Includi contenuti NSFW"
        override val settingsItemBlurNsfw = "Offusca media NSFW"
        override val settingsItemDefaultTimelineType = "Tipo di feed predefinito"
        override val actionDelete = "Elimina"
        override val actionShare = "Condividi"
        override val actionCopyUrl = "Copia link"
        override val messageTextCopiedToClipboard = "Copiato negli appunti! 📋"
        override val contentScaleFit = "Adatta alla dimensione"
        override val contentScaleFillWidth = "Adatta alla larghezza"
        override val contentScaleFillHeight = "Adatta alla altezza"
        override val contentScaleTitle = "Modalità ridimensionamento"
        override val shareAsUrl = "Condividi come URL"
        override val shareAsFile = "Condividi come file"
    }
