package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

internal val EsStrings =
    object : DefaultStrings() {
        override val messageConfirmExit = "Pulsar de nuevo 🔙 para salir"
        override val messageAreYouSure = "Estás seguro/a de que deseas continuar?"
        override val messageSuccess = "¡Operación completada con éxito!"
        override val messageGenericError = "Se ha producido un error inesperado"
        override val messageInvalidField = "Campo no válido"
        override val messageMissingField = "Falta un campo"
        override val messageEmptyList = "No hay nada que mostrar aquí 🗑️️"
        override val buttonConfirm = "Confirmar"
        override val buttonOk = "OK"
        override val buttonClose = "Cerrar"
        override val buttonCancel = "Cancelar"
        override val systemDefault = "Sistema"
        override val settingsThemeLight = "Claro"
        override val settingsThemeDark = "Oscuro"
        override val settingsThemeBlack = "Oscuro (AMOLED)"
        override val sectionTitleHome = "Publicaciones"
        override val sectionTitleExplore = "Explorar"
        override val sectionTitleInbox = "Notificaciones"
        override val sectionTitleProfile = "Perfil"
        override val barThemeOpaque = "Opaco"
        override val barThemeTransparent = "Transparente"
        override val timelineAll = "Todos"
        override val timelineSubscriptions = "Suscripciones"
        override val timelineLocal = "Local"
        override val nodeVia = "vía"
        override val timelineEntryInReplyTo = "en respuesta a"
        override val timelineEntryRebloggedBy = "redistribuido por"

        override fun accountFollower(count: Int) =
            when (count) {
                1 -> "seguidor"
                else -> "seguidores"
            }

        override fun accountFollowing(count: Int) =
            when (count) {
                1 -> "seguido"
                else -> "seguidos"
            }

        override val accountAge = "edad cuenta"
        override val dateYearShort = "a"
        override val dateMonthShort = "m"
        override val dateDayShort = "d"
        override val timeHourShort = "h"
        override val timeMinuteShort = "min"
        override val timeSecondShort = "s"
        override val accountSectionPosts = "Publicaciones"
        override val accountSectionAll = "Publicaciones & Respuestas"
        override val accountSectionPinned = "Fijados"
        override val accountSectionMedia = "Multimediales"
        override val postTitle = "Publicar"
        override val settingsTitle = "Ajustes"
        override val settingsHeaderGeneral = "General"
        override val settingsItemLanguage = "Idioma"
        override val settingsHeaderLookAndFeel = "Aspecto"
        override val settingsItemTheme = "Tema de interfaz"
        override val settingsItemFontFamily = "Familia de fuentes"
        override val settingsItemDynamicColors = "Material You"
        override val settingsItemDynamicColorsSubtitle = "genera una paleta basada en su fondo"
        override val settingsItemThemeColor = "Color del tema"
        override val settingsItemThemeColorSubtitle =
            "sólo se aplica si «Material You» está desactivado"
        override val themeColorPurple = "Pulpo portentoso"
        override val themeColorBlue = "Ballena bailarina"
        override val themeColorLightBlue = "Delfín distraído"
        override val themeColorGreen = "Rana relajada"
        override val themeColorYellow = "Erizo errante"
        override val themeColorOrange = "Zorro zancudo"
        override val themeColorRed = "Cangrejo crujiente"
        override val themeColorPink = "Unicornio único"
        override val themeColorGray = "Mapache maloliente"
        override val themeColorWhite = "Panda peludo"
        override val messageUserUnlogged = "Necesitas iniciar una sesión para acceder a esta sección 🪵"
        override val buttonLogin = "Iniciar sesión"
        override val fieldNodeName = "Nombre de instancia"
        override val fieldUsername = "Nombre de usuario"
        override val fieldPassword = "Contraseña"
        override val actionLogout = "Cerrar sesión"
        override val relationshipStatusFollowing = "Después de"
        override val relationshipStatusFollowsYou = "Te sigue"
        override val relationshipStatusMutual = "Mutuo"
        override val relationshipStatusRequestedToOther = "Solicitud enviada"
        override val relationshipStatusRequestedToYou = "Solicitud pendiente"
        override val notificationTypeEntry = "ha publicado un post"
        override val notificationTypeFavorite = "ha añadido tu post a favoritos"
        override val notificationTypeFollow = "ha empezado a seguirte"
        override val notificationTypeFollowRequest = "te ha enviado una solicitud de seguimiento"
        override val notificationTypeMention = "te ha mencionado"
        override val notificationTypePoll = "se ha cerrado una encuesta en la que has participado"
        override val notificationTypeReblog = "ha redistribuido tu mensaje"
        override val notificationTypeUpdate =
            "ha actualizado una publicación que has redistribuido"
        override val exploreSectionHashtags = "Hashtags"
        override val exploreSectionLinks = "Enlaces"
        override val exploreSectionSuggestions = "Para ti"

        override fun hashtagPeopleUsing(count: Int) =
            when (count) {
                1 -> "persona está hablando de ello"
                else -> "personas están hablando de ello"
            }

        override val feedTypeTitle = "Tipo de línea temporal"
        override val followerTitle = "Seguidores"
        override val followingTitle = "Después de"
        override val actionFollow = "Seguir"
        override val actionUnfollow = "Dejar de seguir"
        override val actionDeleteFollowRequest = "Cancelar solicitud de seguimiento"
        override val sidebarAnonymousTitle = "Anónimo"
        override val sidebarAnonymousMessage =
            "¡Bienvenido/a a Raccoon!\n\nPuedes iniciar sesión en tu instancia en cualquier momento desde la pantalla Perfil.\n\n¡Disfruta de Friendica!"
        override val bookmarksTitle = "Marcados"
        override val favoritesTitle = "Favoritos"
        override val followedHashtagsTitle = "Hashtags seguidos"
        override val infoEdited = "editado"

        override fun extendedSocialInfoFavorites(count: Int) =
            when (count) {
                1 -> "favorito"
                else -> "favoritos"
            }

        override fun extendedSocialInfoReblogs(count: Int) =
            when (count) {
                1 -> "redistribución"
                else -> "redistribuciones"
            }

        override val actionMuteNotifications = "Silenciar notificaciones"
        override val createPostTitle = "Crear mensaje"
        override val messagePostEmptyText =
            "Por favor, proporcionar un archivo adjunto o una encuesta o algún texto"
        override val visibilityPublic = "Público"
        override val visibilityUnlisted = "Sin clasificar"
        override val visibilityPrivate = "Sólo seguidores"
        override val visibilityDirect = "Sólo menciones"
        override val createPostBodyPlaceholder = "Tu impresionante nuevo post… 🪄"
        override val createPostAttachmentsSection = "Archivos adjuntos"
        override val actionEdit = "Editar"
        override val pictureDescriptionPlaceholder = "Descripción de la imagen"
        override val insertLinkDialogTitle = "Insertar enlace"
        override val insertLinkFieldAnchor = "Ancla"
        override val insertLinkFieldUrl = "URL"
        override val selectUserDialogTitle = "Seleccionar un usuario"
        override val selectUserSearchPlaceholder = "nombre de usuario"
        override val searchSectionUsers = "Usuarios"
        override val searchPlaceholder = "Buscar en el Fediverso"
        override val messageSearchInitialEmpty = "Empieza a escribir algo"
        override val topicTitle = "Tema"
        override val threadTitle = "Conversación"
        override val buttonLoadMoreReplies = "Cargar más respuestas"
        override val postSensitive = "Contenido sensible"
        override val actionCreateThreadInGroup = "Publicar en"
        override val settingsHeaderNsfw = "NSFW"
        override val settingsItemIncludeNsfw = "Incluir contenidos NSFW"
        override val settingsItemBlurNsfw = "Difuminar contenido NSFW"
        override val settingsItemDefaultTimelineType = "Tipo de línea temporal por defecto"
        override val actionDelete = "Borrar"
        override val actionShare = "Compartir"
        override val actionCopyUrl = "Copiar enlace"
        override val messageTextCopiedToClipboard = "¡Copiado al portapapeles! 📋"
        override val contentScaleFit = "Ajustar el tamaño"
        override val contentScaleFillWidth = "Llenar el ancho"
        override val contentScaleFillHeight = "Altura de llenado"
        override val contentScaleTitle = "Modo de escala"
        override val shareAsUrl = "Compartir como URL"
        override val shareAsFile = "Compartir como archivo"
        override val actionMute = "Silenciar"
        override val actionUnmute = "Anular el silencio"
        override val settingsItemBlockedAndMuted = "Gestionar filtros"
        override val manageBlocksSectionMuted = "Silenciado"
        override val manageBlocksSectionBlocked = "Bloqueado"
        override val actionBlock = "Bloquear"
        override val actionUnblock = "Desbloquear"
        override val loginMethodBasic = "legado"
        override val actionPin = "Fijar al perfil"
        override val actionUnpin = "Desenganchar del perfil"
        override val settingsSectionDebug = "Debug"
        override val settingsAbout = "Información sobre la aplicación"
        override val settingsAboutAppVersion = "Versión"
        override val settingsAboutChangelog = "Registro de cambios"
        override val settingsAboutViewGithub = "Ver en GitHub"
        override val settingsAboutReportIssue = "Informar de un problema"
        override val settingsAboutViewFriendica = "Ver en Friendica"
        override val manageCirclesTitle = "Círculos"
        override val createCircleTitle = "Crear círculo"
        override val editCircleTitle = "Editar círculo"
        override val circleEditFieldName = "Nombrar"
        override val circleAddUsersDialogTitle = "Seleccionar usuarios para añadir"
        override val visibilityCircle = "Círculo"
        override val selectCircleDialogTitle = "Seleccionar un círculo"
        override val messagePostInvalidVisibility = "Seleccionar una opción de visibilidad válida"
        override val settingsItemFontScale = "Tamaño de letra"
        override val fontScaleNormal = "Normal"
        override val fontScaleSmaller = "Más pequeño"
        override val fontScaleSmallest = "Pequeñísimo"
        override val fontScaleLarger = "Más grande"
        override val fontScaleLargest = "Grandísimo"
        override val settingsItemUrlOpeningMode = "Modo de apertura de URL"
        override val urlOpeningModeExternal = "Navegador externo"
        override val urlOpeningModeCustomTabs = "Pestañas personalizadas"
        override val urlOpeningModeInternal = "Vista web interna"
        override val dialogErrorTitle = "Ups…"
        override val messagePollVoteErrorBody =
            "Por desgracia, sólo soy un desarrollador móvil y no puedo añadir los métodos back-end que faltan."
        override val buttonPollErrorOpenIssue =
            "Revisar este issue y pon un 👍 para que los devs sepan que puede valer la pena implementarlo."
        override val actionVote = "Ver en GitHub"
        override val pollExpired = "Vota"
        override val shortUnavailable = "Expirado"

        override fun pollVote(count: Int): String =
            when (count) {
                1 -> "voto"
                else -> "votos"
            }

        override val pollExpiresIn = "Caduca en"
        override val actionHideResults = "Mostrar resultados"
        override val actionShowResults = "Ocultar resultados"
        override val inboxConfigureFilterDialogTitle = "Configurar filtros"
        override val inboxConfigureFilterDialogSubtitle =
            "Si se deselecciona algún valor, el filtro sólo devolverá los elementos no leídos"
        override val notificationTypeEntryName = "Notificaciones de publicaciones"
        override val notificationTypeFavoriteName = "Favoritos"
        override val notificationTypeFollowName = "Nuevos seguidores"
        override val notificationTypeFollowRequestName = "Solicitudes de seguimiento"
        override val notificationTypeMentionName = "Menciones y respuestas"
        override val notificationTypePollName = "Encuestas"
        override val notificationTypeReblogName = "Redistribuir"
        override val notificationTypeUpdateName = "Publicar actualizaciones"
        override val muteDurationIndefinite = "Indefinido"
        override val selectDurationDialogTitle = "Seleccionar duración"
        override val muteDurationItem = "No verás ninguna publicación de este usuario durante"
        override val muteDisableNotificationsItem = "Desactivar notificaciones"
        override val actionSendFollowRequest = "Enviar solicitud"
        override val postBy = "por"
        override val customOption = "Personalizar"
        override val colorPickerDialogTitle = "Seleccionar un color"
        override val followRequestsTitle = "Solicitudes de seguimiento"
        override val actionAccept = "Aceptar"
        override val actionReject = "Rechazar"
        override val actionHideContent = "Ocultar contenido"
        override val messageEmptyInbox = "🎉 ¡Ya estás al día! 🎉\n\nVerás las nuevas notificaciones en esta página en cuanto lleguen"
        override val createPostSpoilerPlaceholder = "Texto del spoiler"
        override val createPostTitlePlaceholder = "Título (opcional)"
        override val actionSwitchAccount = "Cambiar de cuenta"
        override val actionDeleteAccount = "Eliminar cuenta"
        override val editProfileTitle = "Editar perfil"
        override val editProfileSectionPersonal = "Datos personalesa"
        override val editProfileItemDisplayName = "Nombre para mostrar"
        override val editProfileItemBio = "Bio"
        override val editProfileSectionFlags = "Visibilidad y permisos"
        override val editProfileItemBot = "Soy un bot"
        override val editProfileItemLocked = "Requerir aprobación manual para las solicitudes de seguimiento"
        override val editProfileItemDiscoverable = "Hacerme visible en las búsquedas"
        override val editProfileItemNoIndex = "Excluir mis publicaciones de los timelines públicos"
        override val unsavedChangesTitle = "Cambios no guardados"
        override val messageAreYouSureExit = "¿Estás seguro/a de que quieres salir?"
        override val buttonSave = "Guardar"
        override val editProfileSectionFields = "Campos personalizados (experimental)"
        override val editProfileItemFieldKey = "Clave"
        override val editProfileItemFieldValue = "Valor"
        override val editProfileSectionImages = "Imágenes (experimental)"
        override val editProfileItemAvatar = "Avatar"
        override val editProfileItemHeader = "Banner"
        override val nodeInfoTitle = "Información sobre la instancia"
        override val nodeInfoSectionRules = "Reglas"
        override val nodeInfoSectionContact = "Contacto"
        override val actionAddNew = "Añadir nuevo"
        override val directMessagesTitle = "Mensajes directos"

        override fun messages(count: Int): String =
            when (count) {
                1 -> "mensaje"
                else -> "mensajes"
            }

        override val messageEmptyConversation = "Este es el comienzo de tu épica conversación con"
        override val followRequiredMessage =
            "Debes seguir al otro usuario para poder enviarle un mensaje directo."
        override val galleryTitle = "Galería"

        override fun items(count: Int): String =
            when (count) {
                1 -> "elemento"
                else -> "elementos"
            }

        override val galleryFieldAlbumName = "Nombre del álbum"
        override val messageEmptyAlbum = "Parece que este álbum está vacío… ✨"
        override val actionMove = "Mover"
        override val pickFromGalleryDialogTitle = "Seleccionar de la galería"

        override fun unreadMessages(count: Int): String =
            when (count) {
                1 -> "leído"
                else -> "leídos"
            }

        override val messageCharacterLimitExceeded =
            "Has superado el número máximo de caracteres permitido"
        override val userFieldPersonalNote = "Nota personal"
        override val actionEditPersonalNote = "Editar nota"
        override val actionCancelEditPersonalNote = "Terminar de editar nota"
        override val messageVideoNsfw = "Este vídeo puede contener contenido sensible 🙈"
        override val buttonLoad = "Cargar"
        override val messageAreYouSureReblog =
            "Este post tiene más de un mes. ¿Estás seguro/a de que quieres redistribuirlo?"
        override val unpublishedTitle = "Artículos no publicados"
        override val unpublishedSectionScheduled = "Programados"
        override val unpublishedSectionDrafts = "Borradores"
        override val actionSetScheduleDate = "Establecer horario"
        override val actionUpdateScheduleDate = "Cambiar horario"
        override val actionPublishDefault = "Publicar ahora"
        override val scheduleDateIndication = "Programado para:"
        override val messageScheduleDateInThePast = "Seleccionar una fecha futura"
        override val actionSaveDraft = "Guardar borrador"
        override val settingsItemDefaultPostVisibility = "Visibilidad por defecto de los mensajes"
        override val settingsItemDefaultReplyVisibility =
            "Visibilidad por defecto de las respuestas"
        override val reportCategoryLegal = "Cuestión legal"
        override val itemOther = "Otros"
        override val reportCategorySpam = "Spam"
        override val reportCategoryViolation = "Violación de las normas del servidor"
        override val messageMissingRules = "Por favor, seleccionar al menos una regla"
        override val createReportTitleUser = "Denunciar"
        override val createReportTitleEntry = "Denunciar mensaje por"
        override val createReportItemCategory = "Categoría"
        override val createReportCommentPlaceholder = "Describe el problema que has encontrado"
        override val createReportItemRules = "Normas infringidas"

        override fun createReportSelectedRules(count: Int) =
            when (count) {
                1 -> "regla seleccionada"
                else -> "reglas seleccionadas"
            }

        override val createReportItemForward = "Enviar informe"
        override val actionReportUser = "Denunciar usuario"
        override val actionReportEntry = "Denunciar publicación"
        override val actionViewDetails = "Detalles"
        override val actionAddImage = "Añadir imagen"
        override val actionAddImageFromGallery = "Añadir imagen (galería)"
        override val actionAddPoll = "Añadir encuesta"
        override val actionRemovePoll = "Eliminar encuesta"
        override val createPostPollSection = "Encuesta"
        override val createPostPollOptionLabel = "Opción"
        override val createPostPollItemMultiple = "Permitir opción múltiple"
        override val createPostPollItemExpirationDate = "Fecha de caducidad"
        override val messageInvalidPollError =
            "Encuesta no válida, compruebe las opciones y la fecha de caducidad"
        override val userFeedbackFieldEmail = "Correo electrónico o nombre de usuario (opcional)"
        override val userFeedbackFieldComment = "Comentarios"
        override val userFeedbackCommentPlaceholder =
            "Describe el problema que has encontrado o simplemente deja un comentario 🖋️"
        override val changeNodeDialogTitle = "Cambiar instancia"
        override val actionQuote = "Citar"
        override val actionAddSpoiler = "Añadir spoiler"
        override val actionRemoveSpoiler = "Eliminar spoiler"
        override val actionAddTitle = "Añadir título"
        override val actionRemoveTitle = "Eliminar título"
        override val actionRevealContent = "Mostrar contenido"
        override val settingsItemExcludeRepliesFromTimeline =
            "Excluir respuestas de la línea de tiempo"
        override val insertEmojiTitle = "Insertar emoji"
        override val messageLoadingUsers = "Cargar usuarios"
        override val actionOpenPreview = "Abrir vista previa"
        override val actionSwitchToClassicMode = "Cambiar al modo clásico"
        override val actionSwitchToForumMode = "Cambiar al modo foro"
        override val settingsItemOpenGroupsInForumModeByDefault =
            "Abrir grupos en modo foro por defecto"
        override val actionInsertList = "Insertar lista"
        override val actionDismissAllNotifications = "Desechar todas las notificaciones"
        override val settingsItemMarkupMode = "Marcas de composición"
        override val markupModePlainText = "Texto sin formato"
        override val messageAltTextMissingError =
            "Algunos archivos adjuntos no tienen un texto alternativo, insertarlo puede mejorar la accesibilidad"
        override val buttonPublishAnyway = "Publicar de todos modos"
        override val actionCopyToClipboard = "Copiar al portapapeles"
        override val actionSaveToCalendar = "Guardar en el calendario"
        override val settingsItemMaxPostBodyLines = "Número máximo líneas cuerpo post"
        override val settingsOptionUnlimited = "Ilimitado"
        override val settingsAboutLicences = "Licencias"
        override val settingsOptionBackgroundNotificationCheck = "Comprobar notificaciones en segundo plano"
        override val settingsSubtitleBackgroundNotificationRestricted =
            "Activar actividad en segundo plano para la app en los ajustes de sistema"
        override val settingsSubtitleBackgroundNotificationNotRestricted =
            "Sin restricción del sistema para la actividad en segundo plano "
        override val durationNever = "Nunca"
        override val unreadNotificationTitle = "¡Comprueba tus notificaciones!"

        override fun unreadNotificationBody(count: Int) =
            when (count) {
                1 -> "Hay $count artículo en tu 🗑️"
                else -> "Hay $count artículos en tu 🗑️"
            }

        override val settingsAboutUserManual = "Manual del usuario"
        override val editProfileItemHideCollections =
            "Hacer privadas las listas de seguidores y seguidos"
        override val settingsAboutMatrix = "Unirse a la sala Matrix"
        override val settingsAutoloadImages = "Modo de carga imágenes"
        override val circleTypeGroup = "Grupos"
        override val circleTypePredefined = "Canales"
        override val circleTypeUserDefined = "Tus listas"
        override val settingsItemNotificationMode = "Modo notificaciones"
        override val settingsNotificationModeDisabled = "Desactivado"
        override val settingsNotificationModePull = "Tirar"
        override val settingsNotificationModePullExplanation = "en segundo plano"
        override val settingsNotificationModePush = "Empujar"
        override val settingsNotificationModePushExplanation = "UnifiedPush"
        override val settingsItemPushNotificationState = "Estado notificaciones push"
        override val settingsPushNotificationStateUnsupported = "No soportadas"
        override val settingsPushNotificationStateInitializing = "Inicializando…"
        override val settingsPushNotificationStateNoDistributors = "Seleccionar distribuidor"
        override val settingsPushNotificationStateNoDistributorSelected = "Select distributor"
        override val settingsPushNotificationStateIdle = "Inactivo"
        override val settingsPushNotificationStateEnabled = "Activo"
        override val experimental = "experimental"
        override val loginTitle = "🦝 ¡Bienvenido/a! 🦝"
        override val loginSubtitle =
            "Conéctate a una instancia para empezar a seguir a otras personas, crear nuevas publicaciones o respuestas y gestionar tus favoritos o marcadores."
        override val moreInfo = "Descubre más"
        override val loginMoreInfoBottomSheetContent =
            "El flujo de inicio de sesión comenzará en la aplicación seleccionando la instancia, " +
                "tras lo cual continuarás el flujo OAuth2 en un navegador web." +
                "\n\nSi no tienes una cuenta, elige una instancia y regístrate primero en tu navegador."
        override val loginFriendicaHeader = "Estoy en Friendica"
        override val loginMastodonHeader = "Estoy en Mastodon"
        override val helpMeChooseAnInstance = "Ayúdame a elegir una instancia"
        override val exempliGratia = "ej."
        override val newAccountTitle = "Añadir cuenta"
        override val imageLoadingModeAlways = "Siempre"
        override val imageLoadingModeOnDemand = "A petición"
        override val imageLoadingModeOnWiFi = "En WiFi"
        override val messageReplyVisibilityGreaterThanParentError =
            "Estás publicando una respuesta con mayor visibilidad que el mensaje original"
    }
