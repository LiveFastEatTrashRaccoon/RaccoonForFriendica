package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.getStrings

interface Strings {
    val accountAge: String @Composable get
    val accountSectionAll: String @Composable get
    val accountSectionMedia: String @Composable get
    val accountSectionPinned: String @Composable get
    val accountSectionPosts: String @Composable get
    val actionAccept: String @Composable get
    val actionAddImage: String @Composable get
    val actionAddImageFromGallery: String @Composable get
    val actionAddNew: String @Composable get
    val actionAddPoll: String @Composable get
    val actionAddReaction: String @Composable get
    val actionAddSpoiler: String @Composable get
    val actionAddTitle: String @Composable get
    val actionAddToBookmarks: String @Composable get
    val actionAddToFavorites: String @Composable get
    val actionBlock: String @Composable get
    val actionCancelEditPersonalNote: String @Composable get
    val actionChangeMarkupMode: String @Composable get
    val actionChangeVisibility: String @Composable get
    val actionClear: String @Composable get
    val actionCopyToClipboard: String @Composable get
    val actionCopyUrl: String @Composable get
    val actionCreateThreadInGroup: String @Composable get
    val actionDelete: String @Composable get
    val actionDeleteAccount: String @Composable get
    val actionDeleteFollowRequest: String @Composable get
    val actionDismissAllNotifications: String @Composable get
    val actionDownload: String @Composable get
    val actionEdit: String @Composable get
    val actionEditMembers: String @Composable get
    val actionEditPersonalNote: String @Composable get
    val actionExport: String @Composable get
    val actionFilter: String @Composable get
    val actionFollow: String @Composable get
    val actionGoBack: String @Composable get
    val actionHideContent: String @Composable get
    val actionHideResults: String @Composable get
    val actionInsertLink: String @Composable get
    val actionInsertList: String @Composable get
    val actionLogout: String @Composable get
    val actionMove: String @Composable get
    val actionMute: String @Composable get
    val actionMuteNotifications: String @Composable get
    val actionOpenDetail: String @Composable get
    val actionOpenFullScreen: String @Composable get
    val actionOpenLink: String @Composable get
    val actionOpenOptions: String @Composable get
    val actionOpenPreview: String @Composable get
    val actionOpenSideMenu: String @Composable get
    val actionPin: String @Composable get
    val actionPublishDefault: String @Composable get
    val actionQuote: String @Composable get
    val actionReblog: String @Composable get
    val actionReject: String @Composable get
    val actionRemoveFromBookmarks: String @Composable get
    val actionRemoveFromFavorites: String @Composable get
    val actionRemovePoll: String @Composable get
    val actionRemoveSpoiler: String @Composable get
    val actionRemoveTitle: String @Composable get
    val actionReply: String @Composable get
    val actionReportEntry: String @Composable get
    val actionReportUser: String @Composable get
    val actionRevealContent: String @Composable get
    val actionSave: String @Composable get
    val actionSaveDraft: String @Composable get
    val actionSaveToCalendar: String @Composable get
    val actionSearch: String @Composable get
    val actionSelect: String @Composable get
    val actionSendFollowRequest: String @Composable get
    val actionSetScheduleDate: String @Composable get
    val actionShare: String @Composable get
    val actionShowContentDescription: String @Composable get
    val actionShowResults: String @Composable get
    val actionSubmit: String @Composable get
    val actionSwitchAccount: String @Composable get
    val actionSwitchToClassicMode: String @Composable get
    val actionSwitchToForumMode: String @Composable get
    val actionToggleReveal: String @Composable get
    val actionUnblock: String @Composable get
    val actionUnfollow: String @Composable get
    val actionUnmute: String @Composable get
    val actionUnpin: String @Composable get
    val actionUpdateScheduleDate: String @Composable get
    val actionViewDetails: String @Composable get
    val actionVote: String @Composable get
    val announcementsTitle: String @Composable get
    val appIconClassical: String @Composable get
    val appIconDefault: String @Composable get
    val barThemeOpaque: String @Composable get
    val barThemeSolid: String @Composable get
    val barThemeTransparent: String @Composable get
    val bookmarksTitle: String @Composable get
    val buttonCancel: String @Composable get
    val buttonClose: String @Composable get
    val buttonConfirm: String @Composable get
    val buttonLoad: String @Composable get
    val buttonLoadMoreReplies: String @Composable get
    val buttonLogin: String @Composable get
    val buttonOk: String @Composable get
    val buttonPollErrorOpenIssue: String @Composable get
    val buttonPublishAnyway: String @Composable get
    val buttonSave: String @Composable get
    val calendarTitle: String @Composable get
    val changeNodeDialogTitle: String @Composable get
    val circleAddUsersDialogTitle: String @Composable get
    val circleEditFieldName: String @Composable get
    val circleTypeGroup: String @Composable get
    val circleTypePredefined: String @Composable get
    val circleTypeUserDefined: String @Composable get
    val colorPickerDialogTitle: String @Composable get
    val confirmChangeMarkupMode: String @Composable get
    val contentScaleFillHeight: String @Composable get
    val contentScaleFillWidth: String @Composable get
    val contentScaleFit: String @Composable get
    val contentScaleTitle: String @Composable get
    val createCircleTitle: String @Composable get
    val createPostAttachmentsSection: String @Composable get
    val createPostBodyPlaceholder: String @Composable get
    val createPostPollItemExpirationDate: String @Composable get
    val createPostPollItemMultiple: String @Composable get
    val createPostPollOptionLabel: String @Composable get
    val createPostPollSection: String @Composable get
    val createPostSpoilerPlaceholder: String @Composable get
    val createPostTitle: String @Composable get
    val createPostTitlePlaceholder: String @Composable get
    val createReportCommentPlaceholder: String @Composable get
    val createReportItemCategory: String @Composable get
    val createReportItemForward: String @Composable get
    val createReportItemRules: String @Composable get
    val createReportTitleEntry: String @Composable get
    val createReportTitleUser: String @Composable get
    val creationDate: String @Composable get
    val customOption: String @Composable get
    val dateDayShort: String @Composable get
    val dateMonthShort: String @Composable get
    val dateYearShort: String @Composable get
    val dialogErrorTitle: String @Composable get
    val directMessagesTitle: String @Composable get
    val durationNever: String @Composable get
    val editCircleTitle: String @Composable get
    val editProfileItemAvatar: String @Composable get
    val editProfileItemBio: String @Composable get
    val editProfileItemBot: String @Composable get
    val editProfileItemDiscoverable: String @Composable get
    val editProfileItemDisplayName: String @Composable get
    val editProfileItemFieldKey: String @Composable get
    val editProfileItemFieldValue: String @Composable get
    val editProfileItemHeader: String @Composable get
    val editProfileItemHideCollections: String @Composable get
    val editProfileItemLocked: String @Composable get
    val editProfileItemNoIndex: String @Composable get
    val editProfileSectionFields: String @Composable get
    val editProfileSectionFlags: String @Composable get
    val editProfileSectionImages: String @Composable get
    val editProfileSectionPersonal: String @Composable get
    val editProfileTitle: String @Composable get
    val exempliGratia: String @Composable get
    val experimental: String @Composable get
    val exploreSectionHashtags: String @Composable get
    val exploreSectionLinks: String @Composable get
    val exploreSectionSuggestions: String @Composable get
    val favoritesTitle: String @Composable get
    val feedTypeTitle: String @Composable get
    val fieldNodeName: String @Composable get
    val fieldPassword: String @Composable get
    val fieldUsername: String @Composable get
    val followRequestsTitle: String @Composable get
    val followRequiredMessage: String @Composable get
    val followedHashtagsTitle: String @Composable get
    val followerTitle: String @Composable get
    val followingTitle: String @Composable get
    val fontScaleLarger: String @Composable get
    val fontScaleLargest: String @Composable get
    val fontScaleNormal: String @Composable get
    val fontScaleSmaller: String @Composable get
    val fontScaleSmallest: String @Composable get
    val formatBold: String @Composable get
    val formatItalic: String @Composable get
    val formatMonospace: String @Composable get
    val formatStrikethrough: String @Composable get
    val formatUnderlined: String @Composable get
    val galleryFieldAlbumName: String @Composable get
    val galleryTitle: String @Composable get
    val helpMeChooseAnInstance: String @Composable get
    val highestScore: String @Composable get
    val imageLoadingModeAlways: String @Composable get
    val imageLoadingModeOnDemand: String @Composable get
    val imageLoadingModeOnWiFi: String @Composable get
    val inboxConfigureFilterDialogSubtitle: String @Composable get
    val inboxConfigureFilterDialogTitle: String @Composable get
    val infoEdited: String @Composable get
    val insertEmojiTitle: String @Composable get
    val insertLinkDialogTitle: String @Composable get
    val insertLinkFieldAnchor: String @Composable get
    val insertLinkFieldUrl: String @Composable get
    val itemOther: String @Composable get
    val languageDe: String @Composable get
    val languageEn: String @Composable get
    val languageEs: String @Composable get
    val languageFi: String @Composable get
    val languageFr: String @Composable get
    val languageIt: String @Composable get
    val languagePl: String @Composable get
    val languagePt: String @Composable get
    val languageUa: String @Composable get
    val loginFriendicaHeader: String @Composable get
    val loginMastodonHeader: String @Composable get
    val loginMethodBasic: String @Composable get
    val loginMoreInfoBottomSheetContent: String @Composable get
    val loginSubtitle: String @Composable get
    val loginTitle: String @Composable get
    val manageBlocksSectionBlocked: String @Composable get
    val manageBlocksSectionMuted: String @Composable get
    val manageCirclesTitle: String @Composable get
    val markupModeBBCode: String @Composable get
    val markupModeHTML: String @Composable get
    val markupModeMarkdown: String @Composable get
    val markupModePlainText: String @Composable get
    val messageAltTextMissingError: String @Composable get
    val messageAreYouSure: String @Composable get
    val messageAreYouSureExit: String @Composable get
    val messageAreYouSureReblog: String @Composable get
    val messageAuthIssue: String @Composable get
    val messageAuthIssueHint1: String @Composable get
    val messageAuthIssueHint2: String @Composable get
    val messageAuthIssueHint3: String @Composable get
    val messageAuthIssueHintsTitle: String @Composable get
    val messageCharacterLimitExceeded: String @Composable get
    val messageConfirmExit: String @Composable get
    val messageEmptyAlbum: String @Composable get
    val messageEmptyConversation: String @Composable get
    val messageEmptyInbox: String @Composable get
    val messageEmptyList: String @Composable get
    val messageGenericError: String @Composable get
    val messageInvalidField: String @Composable get
    val messageInvalidPollError: String @Composable get
    val messageLoadingUsers: String @Composable get
    val messageMissingField: String @Composable get
    val messageMissingRules: String @Composable get
    val messagePollVoteErrorBody: String @Composable get
    val messagePostEmptyText: String @Composable get
    val messagePostInvalidVisibility: String @Composable get
    val messageReplyVisibilityGreaterThanParentError: String @Composable get
    val messageRestartToApplyChanges: String @Composable get
    val messageScheduleDateInThePast: String @Composable get
    val messageSearchInitialEmpty: String @Composable get
    val messageSuccess: String @Composable get
    val messageTextCopiedToClipboard: String @Composable get
    val messageUserUnlogged: String @Composable get
    val messageVideoNsfw: String @Composable get
    val moreInfo: String @Composable get
    val muteDisableNotificationsItem: String @Composable get
    val muteDurationIndefinite: String @Composable get
    val muteDurationItem: String @Composable get
    val newAccountTitle: String @Composable get
    val nodeInfoSectionContact: String @Composable get
    val nodeInfoSectionRules: String @Composable get
    val nodeInfoTitle: String @Composable get
    val nodeVia: String @Composable get
    val notificationTypeEntry: String @Composable get
    val notificationTypeEntryName: String @Composable get
    val notificationTypeFavorite: String @Composable get
    val notificationTypeFavoriteName: String @Composable get
    val notificationTypeFollow: String @Composable get
    val notificationTypeFollowName: String @Composable get
    val notificationTypeFollowRequest: String @Composable get
    val notificationTypeFollowRequestName: String @Composable get
    val notificationTypeMention: String @Composable get
    val notificationTypeMentionName: String @Composable get
    val notificationTypePoll: String @Composable get
    val notificationTypePollName: String @Composable get
    val notificationTypeReblog: String @Composable get
    val notificationTypeReblogName: String @Composable get
    val notificationTypeUpdate: String @Composable get
    val notificationTypeUpdateName: String @Composable get
    val pickFromGalleryDialogTitle: String @Composable get
    val pictureDescriptionPlaceholder: String @Composable get
    val pollExpired: String @Composable get
    val pollExpiresIn: String @Composable get
    val postBy: String @Composable get
    val postSensitive: String @Composable get
    val postTitle: String @Composable get
    val previewImage: String @Composable get
    val relationshipStatusFollowing: String @Composable get
    val relationshipStatusFollowsYou: String @Composable get
    val relationshipStatusMutual: String @Composable get
    val relationshipStatusRequestedToOther: String @Composable get
    val relationshipStatusRequestedToYou: String @Composable get
    val reportCategoryLegal: String @Composable get
    val reportCategorySpam: String @Composable get
    val reportCategoryViolation: String @Composable get
    val scheduleDateIndication: String @Composable get
    val searchPlaceholder: String @Composable get
    val searchSectionUsers: String @Composable get
    val sectionTitleExplore: String @Composable get
    val sectionTitleHome: String @Composable get
    val sectionTitleInbox: String @Composable get
    val sectionTitleProfile: String @Composable get
    val selectCircleDialogTitle: String @Composable get
    val selectDurationDialogTitle: String @Composable get
    val selectUserDialogTitle: String @Composable get
    val selectUserSearchPlaceholder: String @Composable get
    val settingsAbout: String @Composable get
    val settingsAboutAppVersion: String @Composable get
    val settingsAboutChangelog: String @Composable get
    val settingsAboutLicences: String @Composable get
    val settingsAboutMatrix: String @Composable get
    val settingsAboutReportIssue: String @Composable get
    val settingsAboutUserManual: String @Composable get
    val settingsAboutViewFriendica: String @Composable get
    val settingsAboutViewGithub: String @Composable get
    val settingsAutoloadImages: String @Composable get
    val settingsHeaderGeneral: String @Composable get
    val settingsHeaderLookAndFeel: String @Composable get
    val settingsHeaderNsfw: String @Composable get
    val settingsItemAppIcon: String @Composable get
    val settingsItemBarTheme: String @Composable get
    val settingsItemBlockedAndMuted: String @Composable get
    val settingsItemBlurNsfw: String @Composable get
    val settingsItemCrashReportEnabled: String @Composable get
    val settingsItemDefaultPostVisibility: String @Composable get
    val settingsItemDefaultReplyVisibility: String @Composable get
    val settingsItemDefaultTimelineType: String @Composable get
    val settingsItemDynamicColors: String @Composable get
    val settingsItemDynamicColorsSubtitle: String @Composable get
    val settingsItemExcludeRepliesFromTimeline: String @Composable get
    val settingsItemExport: String @Composable get
    val settingsItemFontFamily: String @Composable get
    val settingsItemFontScale: String @Composable get
    val settingsItemHideNavigationBarWhileScrolling: String @Composable get
    val settingsItemImport: String @Composable get
    val settingsItemIncludeNsfw: String @Composable get
    val settingsItemLanguage: String @Composable get
    val settingsItemMarkupMode: String @Composable get
    val settingsItemMaxPostBodyLines: String @Composable get
    val settingsItemNotificationMode: String @Composable get
    val settingsItemOpenGroupsInForumModeByDefault: String @Composable get
    val settingsItemPushNotificationState: String @Composable get
    val settingsItemTheme: String @Composable get
    val settingsItemThemeColor: String @Composable get
    val settingsItemThemeColorSubtitle: String @Composable get
    val settingsItemUrlOpeningMode: String @Composable get
    val settingsNotificationModeDisabled: String @Composable get
    val settingsNotificationModePull: String @Composable get
    val settingsNotificationModePullExplanation: String @Composable get
    val settingsNotificationModePush: String @Composable get
    val settingsNotificationModePushExplanation: String @Composable get
    val settingsOptionBackgroundNotificationCheck: String @Composable get
    val settingsOptionUnlimited: String @Composable get
    val settingsPushNotificationStateEnabled: String @Composable get
    val settingsPushNotificationStateIdle: String @Composable get
    val settingsPushNotificationStateInitializing: String @Composable get
    val settingsPushNotificationStateNoDistributorSelected: String @Composable get
    val settingsPushNotificationStateNoDistributors: String @Composable get
    val settingsPushNotificationStateUnsupported: String @Composable get
    val settingsSectionDebug: String @Composable get
    val settingsSubtitleBackgroundNotificationNotRestricted: String @Composable get
    val settingsSubtitleBackgroundNotificationRestricted: String @Composable get
    val settingsThemeBlack: String @Composable get
    val settingsThemeDark: String @Composable get
    val settingsThemeLight: String @Composable get
    val settingsTitle: String @Composable get
    val shareAsFile: String @Composable get
    val shareAsUrl: String @Composable get
    val shortUnavailable: String @Composable get
    val sidebarAnonymousMessage: String @Composable get
    val sidebarAnonymousTitle: String @Composable get
    val systemDefault: String @Composable get
    val themeColorBlue: String @Composable get
    val themeColorGray: String @Composable get
    val themeColorGreen: String @Composable get
    val themeColorLightBlue: String @Composable get
    val themeColorOrange: String @Composable get
    val themeColorPink: String @Composable get
    val themeColorPurple: String @Composable get
    val themeColorRed: String @Composable get
    val themeColorWhite: String @Composable get
    val themeColorYellow: String @Composable get
    val threadTitle: String @Composable get
    val timeHourShort: String @Composable get
    val timeMinuteShort: String @Composable get
    val timeSecondShort: String @Composable get
    val timelineAll: String @Composable get
    val timelineEntryInReplyTo: String @Composable get
    val timelineEntryRebloggedBy: String @Composable get
    val timelineLocal: String @Composable get
    val timelineSubscriptions: String @Composable get
    val topicTitle: String @Composable get
    val unpublishedSectionDrafts: String @Composable get
    val unpublishedSectionScheduled: String @Composable get
    val unpublishedTitle: String @Composable get
    val unsavedChangesTitle: String @Composable get
    val updateDate: String @Composable get
    val urlOpeningModeCustomTabs: String @Composable get
    val urlOpeningModeExternal: String @Composable get
    val urlOpeningModeInternal: String @Composable get
    val userFeedbackCommentPlaceholder: String @Composable get
    val userFeedbackFieldComment: String @Composable get
    val userFeedbackFieldEmail: String @Composable get
    val userFieldPersonalNote: String @Composable get
    val verifiedField: String @Composable get
    val visibilityCircle: String @Composable get
    val visibilityDirect: String @Composable get
    val visibilityPrivate: String @Composable get
    val visibilityPublic: String @Composable get
    val visibilityUnlisted: String @Composable get

    @Composable fun accountFollower(count: Int): String

    @Composable fun accountFollowing(count: Int): String

    @Composable fun createReportSelectedRules(count: Int): String

    @Composable fun dislikesCount(count: Int): String

    @Composable fun extendedSocialInfoFavorites(count: Int): String

    @Composable fun extendedSocialInfoReblogs(count: Int): String

    @Composable fun hashtagPeopleUsing(count: Int): String

    @Composable fun items(count: Int): String

    @Composable fun messages(count: Int): String

    @Composable fun pollVote(count: Int): String

    @Composable fun unreadMessages(count: Int): String

    suspend fun unreadNotificationBody(count: Int): String

    suspend fun unreadNotificationTitle(): String
}

object Locales {
    const val EN = "en"
    const val DE = "de"
    const val ES = "es"
    const val FI = "fi"
    const val FR = "fr"
    const val IT = "it"
    const val PL = "pl"
    const val PT = "pt"
    const val UA = "ua"

    val AVAILABLE_LANGUAGES =
        listOf(
            EN,
            DE,
            ES,
            FI,
            FR,
            IT,
            PL,
            PT,
            UA,
        )
}

val LocalStrings: ProvidableCompositionLocal<Strings> =
    staticCompositionLocalOf { getStrings(Locales.EN) }

@Composable
fun ProvideStrings(
    lang: String,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalStrings provides getStrings(lang),
        content = content,
    )
}
