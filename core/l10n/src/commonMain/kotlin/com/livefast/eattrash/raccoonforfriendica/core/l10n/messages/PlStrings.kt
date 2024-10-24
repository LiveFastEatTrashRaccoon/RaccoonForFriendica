package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

internal val PlStrings =
    object : DefaultStrings() {
        override val messageConfirmExit = "Dotknij 🔙 ponownie, aby wyjść"
        override val messageAreYouSure = "Czy na pewno chcesz kontynuować?"
        override val messageSuccess = "Operacja zakończona pomyślnie!"
        override val messageGenericError = "Wystąpił nieoczekiwany błąd"
        override val messageInvalidField = "Nieprawidłowe pole"
        override val messageMissingField = "Brakujące pole"
        override val messageEmptyList = "Nie ma tu nic do wyświetlenia 🗑️️"
        override val buttonConfirm = "Potwierdź"
        override val buttonOk = "OK"
        override val buttonClose = "Zamknij"
        override val buttonCancel = "Anuluj"
        override val systemDefault = "System"
        override val settingsThemeLight = "Jasny"
        override val settingsThemeDark = "Ciemny"
        override val settingsThemeBlack = "Czarny (AMOLED)"
        override val sectionTitleHome = "Oś czasu"
        override val sectionTitleExplore = "Eksploruj"
        override val sectionTitleInbox = "Skrzynka odbiorcza"
        override val sectionTitleProfile = "Profil"
        override val barThemeOpaque = "Nieprzezroczysty"
        override val barThemeTransparent = "Przezroczysty"
        override val timelineAll = "Wszystkie"
        override val timelineSubscriptions = "Subskrypcje"
        override val timelineLocal = "Lokalny"
        override val nodeVia = "przez"
        override val timelineEntryInReplyTo = "w odpowiedzi na"
        override val timelineEntryRebloggedBy = "udostępniony ponownie przez"

        override fun accountFollower(count: Int) =
            when (count) {
                1 -> "zwolennik"
                else -> "zwolennicy"
            }

        override fun accountFollowing(count: Int) = "śledzący"

        override val accountAge = "wiek konta"
        override val dateYearShort = "y"
        override val dateMonthShort = "m"
        override val dateDayShort = "d"
        override val timeHourShort = "h"
        override val timeMinuteShort = "min"
        override val timeSecondShort = "s"
        override val accountSectionPosts = "Posty"
        override val accountSectionAll = "Posty i odpowiedzi"
        override val accountSectionPinned = "Przypięty"
        override val accountSectionMedia = "Media"
        override val postTitle = "Post"
        override val settingsTitle = "Ustawienia"
        override val settingsHeaderGeneral = "Ogólne"
        override val settingsItemLanguage = "Język"
        override val settingsHeaderLookAndFeel = "Wygląd i działanie"
        override val settingsItemTheme = "Motyw interfejsu użytkownika"
        override val settingsItemFontFamily = "Rodzina czcionek"
        override val settingsItemDynamicColors = "Material You"
        override val settingsItemDynamicColorsSubtitle = "generuje paletę na podstawie tła"
        override val settingsItemThemeColor = "Kolor motywu"
        override val settingsItemThemeColorSubtitle =
            "stosowane tylko wtedy, gdy opcja „Material You” jest wyłączona"
        override val themeColorPurple = "Oceaniczna ośmiornica"
        override val themeColorBlue = "Wesoły wieloryb"
        override val themeColorLightBlue = "Dziwaczny delfin"
        override val themeColorGreen = "Żenujący żaba"
        override val themeColorYellow = "Jajcarski jeż"
        override val themeColorOrange = "Laluśny lis"
        override val themeColorRed = "Kapryśny krab"
        override val themeColorPink = "Jazgotliwy jednorożec"
        override val themeColorGray = "Jowialny jenot"
        override val themeColorWhite = "Misiowaty miś"
        override val messageUserUnlogged =
            "Musisz być zalogowany, aby uzyskać dostęp do tej sekcji 🪵"
        override val loginTitle = "Login"
        override val fieldNodeName = "Nazwa instancji"
        override val fieldUsername = "Nazwa użytkownika"
        override val fieldPassword = "Hasło"
        override val actionLogout = "Wylogowanie"
        override val relationshipStatusFollowing = "Śledzenie"
        override val relationshipStatusFollowsYou = "Obserwuje użytkownika"
        override val relationshipStatusMutual = "Wzajemny"
        override val relationshipStatusRequestedToOther = "Żądanie wysłane"
        override val relationshipStatusRequestedToYou = "Prośba w toku"
        override val notificationTypeEntry = "opublikowałeś post"
        override val notificationTypeFavorite = "dodał Twój post do ulubionych"
        override val notificationTypeFollow = "zaczął Cię obserwować"
        override val notificationTypeFollowRequest = "wysłał prośbę o obserwowanie"
        override val notificationTypeReply = "odpowiedział ci"
        override val notificationTypePoll = "ankieta, w której brałeś udział, została zamknięta"
        override val notificationTypeReblog = "ponownie udostępniłeś swój post"
        override val notificationTypeUpdate = "zaktualizowano ponownie udostępniony post"
        override val exploreSectionHashtags = "Hashtagi"
        override val exploreSectionLinks = "Linki"
        override val exploreSectionSuggestions = "Dla Ciebie"

        override fun hashtagPeopleUsing(count: Int) =
            when (count) {
                1 -> "osoba o tym mówi"
                else -> "osoby o tym mówią"
            }

        override val feedTypeTitle = "Rodzaj paszy"
        override val followerTitle = "Zwolennicy"
        override val followingTitle = "Śledzący"
        override val actionFollow = "Śledź"
        override val actionUnfollow = "Nie obserwuj"
        override val actionDeleteFollowRequest = "Anuluj prośbę o śledzenie"
        override val sidebarAnonymousTitle = "Anonimowy"
        override val sidebarAnonymousMessage =
            "Witamy w Raccoon!\n\nMożesz zalogować się do swojej instancji w dowolnym momencie z ekranu profilu.\n\nMiłego korzystania z Friendica!"
        override val bookmarksTitle = "Zakładki"
        override val favoritesTitle = "Ulubione"
        override val followedHashtagsTitle = "Obserwowane hashtagi"
        override val infoEdited = "edytowany"

        override fun extendedSocialInfoFavorites(count: Int) =
            when (count) {
                1 -> "ulubiony"
                else -> "ulubione"
            }

        override fun extendedSocialInfoReblogs(count: Int) =
            when (count) {
                1 -> "udostępnianie"
                else -> "udostępnienia"
            }

        override val actionMuteNotifications = "Wycisz powiadomienia"
        override val createPostTitle = "Utwórz post"
        override val messagePostEmptyText = "Prosimy o dostarczenie załącznika, ankiety lub tekstu."
        override val visibilityPublic = "Publiczny"
        override val visibilityUnlisted = "Bez listy"
        override val visibilityPrivate = "Tylko obserwujący"
        override val visibilityDirect = "Tylko wzmianki"
        override val createPostBodyPlaceholder = "Twój niesamowity nowy post… 🪄"
        override val createPostAttachmentsSection = "Załączniki"
        override val actionEdit = "Edytuj"
        override val pictureDescriptionPlaceholder = "Opis obrazu"
        override val insertLinkDialogTitle = "Wstaw link"
        override val insertLinkFieldAnchor = "Kotwica"
        override val insertLinkFieldUrl = "URL"
        override val selectUserDialogTitle = "Wybierz użytkownika"
        override val selectUserSearchPlaceholder = "nazwa użytkownika lub uchwyt"
        override val searchSectionUsers = "Użytkownicy"
        override val searchPlaceholder = "Przeszukiwanie Fediverse"
        override val messageSearchInitialEmpty = "Zacznij coś wpisywać"
        override val topicTitle = "Temat"
        override val threadTitle = "Wątek"
        override val buttonLoadMoreReplies = "Załaduj więcej odpowiedzi"
        override val postSensitive = "Wrażliwa zawartość"
        override val actionCreateThreadInGroup = "Post do"
        override val settingsHeaderNsfw = "NSFW"
        override val settingsItemIncludeNsfw = "Dołączanie treści NSFW"
        override val settingsItemBlurNsfw = "Rozmycie mediów NSFW"
        override val settingsItemDefaultTimelineType = "Domyślny typ osi czasu"
        override val actionDelete = "Usuń"
        override val actionShare = "Udostępnianie"
        override val actionCopyUrl = "Kopiuj łącze"
        override val messageTextCopiedToClipboard = "Skopiowane do schowka! 📋"
        override val contentScaleFit = "Aspekt dopasowania"
        override val contentScaleFillWidth = "Szerokość wypełnienia"
        override val contentScaleFillHeight = "Wysokość wypełnienia"
        override val contentScaleTitle = "Tryb skalowania"
        override val shareAsUrl = "Udostępnij jako URL"
        override val shareAsFile = "Udostępnianie jako plik"
        override val actionMute = "Wyciszenie"
        override val actionUnmute = "Wyłącz wyciszenie"
        override val settingsItemBlockedAndMuted = "Zarządzanie filtrami"
        override val manageBlocksSectionMuted = "Wyciszony"
        override val manageBlocksSectionBlocked = "Zablokowany"
        override val actionBlock = "Blokuj"
        override val actionUnblock = "Odblokuj"
        override val messageLoginOAuth = "Logowanie za pomocą OAuth2 (zalecane)"
        override val or = "lub"
        override val messageLoginLegacy =
            "Zaloguj się za pomocą nazwy użytkownika i hasła (starsza wersja)"
        override val actionPin = "Przypnij do profilu"
        override val actionUnpin = "Usuń przypięcie z profilu"
        override val settingsSectionDebug = "Debugowanie"
        override val settingsAbout = "Informacje o aplikacji"
        override val settingsAboutAppVersion = "Wersja"
        override val settingsAboutChangelog = "Dziennik zmian"
        override val settingsAboutViewGithub = "Wyświetl na GitHub"
        override val settingsAboutReportIssue = "Zgłoś problem"
        override val settingsAboutViewFriendica = "Wyświetl na Friendica"
        override val manageCirclesTitle = "Kręgi"
        override val createCircleTitle = "Tworzenie kręgu"
        override val editCircleTitle = "Edytuj krąg"
        override val circleEditFieldName = "Nazwa"
        override val circleAddUsersDialogTitle = "Wybierz użytkowników do dodania"
        override val visibilityCircle = "Krąg"
        override val selectCircleDialogTitle = "Wybierz okrąg"
        override val messagePostInvalidVisibility = "Wybierz odpowiednią opcję widoczności"
        override val settingsItemFontScale = "Rozmiar czcionki"
        override val fontScaleNormal = "Normalna"
        override val fontScaleSmaller = "Mniejsza"
        override val fontScaleSmallest = "Najmniejszy"
        override val fontScaleLarger = "Większa"
        override val fontScaleLargest = "Największa"
        override val settingsItemUrlOpeningMode = "Tryb otwierania adresu URL"
        override val urlOpeningModeExternal = "Zewnętrzny"
        override val urlOpeningModeCustomTabs = "Zakładki niestandardowe"
        override val dialogErrorTitle = "Ups…"
        override val messagePollVoteErrorBody =
            "Niestety, jestem tylko deweloperem mobilnym i nie mogę dodać brakujących metod back-endowych!\nSprawdź tę kwestię i umieść 👍, aby deweloperzy wiedzieli, że warto ją wdrożyć."
        override val buttonPollErrorOpenIssue = "Zobacz na GitHub"
        override val actionVote = "Głosowanie"
        override val pollExpired = "Wygasł"
        override val shortUnavailable = "N/D"

        override fun pollVote(count: Int): String =
            when (count) {
                1 -> "głos"
                else -> "głosy"
            }

        override val pollExpiresIn = "Wygasa w"
        override val actionHideResults = "Pokaż wyniki"
        override val actionShowResults = "Ukryj wyniki"
        override val inboxConfigureFilterDialogTitle = "Konfiguracja filtrów"
        override val inboxConfigureFilterDialogSubtitle =
            "Jeśli jakakolwiek wartość zostanie odznaczona, filtr zwróci tylko nieprzeczytane elementy."
        override val notificationTypeEntryName = "Powiadomienia o postach"
        override val notificationTypeFavoriteName = "Ulubione"
        override val notificationTypeFollowName = "Nowi obserwujący"
        override val notificationTypeFollowRequestName = "Prośby o obserwowanie"
        override val notificationTypeMentionName = "Wzmianki i odpowiedzi"
        override val notificationTypePollName = "Ankiety"
        override val notificationTypeReblogName = "Ponowne udostępnianie"
        override val notificationTypeUpdateName = "Aktualizacje postów"
        override val muteDurationIndefinite = "Nieokreślony"
        override val selectDurationDialogTitle = "Wybierz czas trwania"
        override val muteDurationItem = "Nie będziesz widzieć żadnych postów tego użytkownika przez"
        override val muteDisableNotificationsItem = "Wyłącz powiadomienia"
        override val actionSendFollowRequest = "Wyślij żądanie"
        override val postBy = "przez"
        override val customOption = "Niestandardowy"
        override val colorPickerDialogTitle = "Wybierz kolor"
        override val followRequestsTitle = "Śledź prośby"
        override val actionAccept = "Akceptuj"
        override val actionReject = "Odrzuć"
        override val actionHideContent = "Ukryj zawartość"
        override val messageEmptyInbox =
            "🎉 Wszyscy jesteście dogonieni! 🎉\n\nNa tej stronie zobaczysz nowe powiadomienia, gdy tylko się pojawią"
        override val createPostSpoilerPlaceholder = "Tekst spoilera"
        override val createPostTitlePlaceholder = "Tytuł (opcjonalnie)"
        override val actionSwitchAccount = "Przełącz konto"
        override val actionDeleteAccount = "Usuń konto"
        override val editProfileTitle = "Edytuj profil"
        override val editProfileSectionPersonal = "Dane osobowe"
        override val editProfileItemDisplayName = "Wyświetlana nazwa"
        override val editProfileItemBio = "Bio"
        override val editProfileSectionFlags = "Widoczność i uprawnienia"
        override val editProfileItemBot = "Jestem botem"
        override val editProfileItemLocked = "Wymagaj ręcznego zatwierdzania próśb o obserwowanie"
        override val editProfileItemDiscoverable =
            "Spraw, by można mnie było znaleźć w wynikach wyszukiwania"
        override val editProfileItemNoIndex = "Wyklucz moje posty z publicznych osi czasu"
        override val unsavedChangesTitle = "Niezapisane zmiany"
        override val messageAreYouSureExit = "Czy na pewno chcesz wyjść?"
        override val buttonSave = "Zapisz"
        override val editProfileSectionFields = "Pola niestandardowe (eksperymentalne)"
        override val editProfileItemFieldKey = "Klucz"
        override val editProfileItemFieldValue = "Wartość"
        override val editProfileSectionImages = "Obrazy (eksperymentalne)"
        override val editProfileItemAvatar = "Awatar"
        override val editProfileItemHeader = "Baner"
        override val nodeInfoTitle = "Informacje o węźle"
        override val nodeInfoSectionRules = "Zasady"
        override val nodeInfoSectionContact = "Kontakt"
        override val actionAddNew = "Dodaj nowy"
        override val directMessagesTitle = "Bezpośrednie wiadomości"

        override fun messages(count: Int): String =
            when (count) {
                1 -> "wiadomość"
                else -> "wiadomości"
            }

        override val messageEmptyConversation = "To jest początek epickiej rozmowy z"
        override val followRequiredMessage =
            "Musisz obserwować drugiego użytkownika, aby móc wysłać bezpośrednią wiadomość!"
        override val galleryTitle = "Galeria"

        override fun items(count: Int): String =
            when (count) {
                1 -> "sztuka"
                else -> "sztuki"
            }

        override val galleryFieldAlbumName = "Nazwa albumu"
        override val messageEmptyAlbum = "Wygląda na to, że ten album jest nadal pusty... ✨"
        override val actionMove = "Przesuń"
        override val pickFromGalleryDialogTitle = "Wybierz z galerii"

        override fun unreadMessages(count: Int): String =
            when (count) {
                1 -> "nieprzeczytany"
                else -> "nieprzeczytane"
            }

        override val messageCharacterLimitExceeded =
            "Przekroczono maksymalną dozwoloną liczbę znaków"
        override val userFieldPersonalNote = "Notatka osobista"
        override val actionEditPersonalNote = "Edytuj notatkę"
        override val actionCancelEditPersonalNote = "Anuluj edycję notatki"
        override val messageVideoNsfw = "Ten film może zawierać treści wrażliwe 🙈"
        override val buttonLoad = "Obciążenie"
        override val messageAreYouSureReblog =
            "Ten post ma ponad miesiąc. Czy na pewno chcesz go ponownie udostępnić?"
        override val unpublishedTitle = "Niepublikowane elementy"
        override val unpublishedSectionScheduled = "Zaplanowane"
        override val unpublishedSectionDrafts = "Wersje robocze"
        override val actionSetScheduleDate = "Ustawianie harmonogramu"
        override val actionUpdateScheduleDate = "Zmień harmonogram"
        override val actionPublishDefault = "Opublikuj teraz"
        override val scheduleDateIndication = "Zaplanowane na:"
        override val messageScheduleDateInThePast = "Wybierz datę harmonogramu w przyszłości"
        override val actionSaveDraft = "Zapisz wersję roboczą"
        override val settingsItemDefaultPostVisibility = "Domyślna widoczność postów"
        override val settingsItemDefaultReplyVisibility = "Domyślna widoczność odpowiedzi"
        override val reportCategoryLegal = "Kwestia prawna"
        override val itemOther = "Inne"
        override val reportCategorySpam = "Spam"
        override val reportCategoryViolation = "Naruszenie reguły serwera"
        override val messageMissingRules = "Wybierz co najmniej jedną regułę"
        override val createReportTitleUser = "Zgłoś"
        override val createReportTitleEntry = "Zgłoś post według"
        override val createReportItemCategory = "Kategoria"
        override val createReportCommentPlaceholder = "Opisz napotkany problem"
        override val createReportItemRules = "Naruszone zasady"

        override fun createReportSelectedRules(count: Int) =
            when (count) {
                1 -> "wybrana reguła"
                else -> "wybrane reguły"
            }

        override val createReportItemForward = "Przekazywanie raportu"
        override val actionReportUser = "Zgłoś użytkownika"
        override val actionReportEntry = "Zgłoś wpis"
        override val actionViewDetails = "Szczegóły"
        override val actionAddImage = "Dodaj obraz"
        override val actionAddImageFromGallery = "Dodaj obraz (galeria multimediów)"
        override val actionAddPoll = "Dodaj ankietę"
        override val actionRemovePoll = "Usuń ankietę"
        override val createPostPollSection = "Ankieta"
        override val createPostPollOptionLabel = "Opcja"
        override val createPostPollItemMultiple = "Zezwalaj na wielokrotny wybór"
        override val createPostPollItemExpirationDate = "Data wygaśnięcia"
        override val messageInvalidPollError =
            "Nieprawidłowa ankieta, sprawdź opcje i datę wygaśnięcia"
        override val userFeedbackFieldEmail = "E-mail lub nazwa użytkownika (opcjonalnie)"
        override val userFeedbackFieldComment = "Opinia"
        override val userFeedbackCommentPlaceholder =
            "Opisz napotkany problem lub po prostu zostaw opinię 🖋️"
        override val changeNodeDialogTitle = "Zmień instancję"
        override val actionQuote = "Cytat"
        override val actionAddSpoiler = "Dodaj spoiler"
        override val actionRemoveSpoiler = "Usuń spoiler"
        override val actionAddTitle = "Dodaj tytuł"
        override val actionRemoveTitle = "Usuń tytuł"
        override val actionRevealContent = "Ujawnij zawartość"
        override val settingsItemExcludeRepliesFromTimeline = "Wyklucz odpowiedzi z osi czasu"
        override val insertEmojiTitle = "Wstawianie emoji"
        override val messageLoadingUsers = "Ładowanie użytkowników"
        override val actionOpenPreview = "Otwieranie podglądu"
        override val actionSwitchToClassicMode = "Przełączanie do trybu klasycznego"
        override val actionSwitchToForumMode = "Przełączanie do trybu forum"
        override val settingsItemOpenGroupsInForumModeByDefault =
            "Domyślne otwieranie grup w trybie forum"
        override val actionInsertList = "Wstawianie listy"
        override val actionDismissAllNotifications = "Odrzuć wszystkie powiadomienia"
        override val settingsItemMarkupMode = "Znaczniki dla kompozycji"
        override val markupModeBBCode = "BBCode"
        override val markupModeHTML = "HTML"
        override val markupModeMarkdown = "Markdown"
        override val markupModePlainText = "Zwykły tekst"
        override val messageAltTextMissingError =
            "Niektóre załączniki nie mają alternatywnego tekstu, rozważ wstawienie go w celu ułatwienia dostępu."
        override val buttonPublishAnyway = "Opublikuj mimo to"
        override val actionCopyToClipboard = "Kopiuj do schowka"
        override val calendarTitle = "Kalendarz"
        override val actionSaveToCalendar = "Zapisz do kalendarza"
        override val settingsItemMaxPostBodyLines = "Maksymalna liczba linii treści posta"
        override val settingsOptionUnlimited = "Bez ograniczeń"
        override val settingsAboutLicences = "Licencje"
        override val settingsOptionBackgroundNotificationCheck = "Sprawdzanie powiadomień w tle"
        override val settingsSubtitleBackgroundNotificationRestricted =
            "Włącz aktywność aplikacji w tle w ustawieniach systemu"
        override val settingsSubtitleBackgroundNotificationNotRestricted =
            "Brak ograniczeń systemowych dla aktywności w tle"
        override val durationNever = "Nigdy"
        override val unreadNotificationTitle = "Sprawdź swoje powiadomienia!"

        override fun unreadNotificationBody(count: Int) =
            when (count) {
                1 -> "W koszu na 🗑️ znajduje się $count przedmiot"
                else -> "W koszu na 🗑️ znajdują się $count przedmioty"
            }
    }
