package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.lyricist.LanguageTag
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings

interface Strings {
    val messageConfirmExit: String
    val messageSuccess: String
    val messageAreYouSure: String
    val messageGenericError: String
    val messageInvalidField: String
    val messageMissingField: String
    val messageEmptyList: String
    val buttonConfirm: String
    val buttonClose: String
    val buttonOk: String
    val buttonCancel: String
    val systemDefault: String
    val settingsThemeLight: String
    val settingsThemeDark: String
    val settingsThemeBlack: String
    val sectionTitleHome: String
    val sectionTitleExplore: String
    val sectionTitleInbox: String
    val sectionTitleProfile: String
    val barThemeTransparent: String
    val barThemeOpaque: String
    val timelineAll: String
    val timelineSubscriptions: String
    val timelineLocal: String
    val nodeVia: String
    val timelineEntryRebloggedBy: String
    val timelineEntryInReplyTo: String

    fun accountFollowing(count: Int): String

    fun accountFollower(count: Int): String

    val actionOpenInForumMode: String
    val accountAge: String
    val dateYearShort: String
    val dateMonthShort: String
    val dateDayShort: String
    val timeHourShort: String
    val timeMinuteShort: String
    val timeSecondShort: String
    val accountSectionPosts: String
    val accountSectionAll: String
    val accountSectionPinned: String
    val accountSectionMedia: String
    val postTitle: String
    val languageEn: String
    val languageIt: String
    val settingsTitle: String
    val settingsHeaderGeneral: String
    val settingsItemLanguage: String
    val settingsHeaderLookAndFeel: String
    val settingsItemTheme: String
    val settingsItemFontFamily: String
    val settingsItemDynamicColors: String
    val settingsItemDynamicColorsSubtitle: String
    val settingsItemThemeColor: String
    val settingsItemThemeColorSubtitle: String
    val themeColorPurple: String
    val themeColorBlue: String
    val themeColorLightBlue: String
    val themeColorGreen: String
    val themeColorYellow: String
    val themeColorOrange: String
    val themeColorRed: String
    val themeColorPink: String
    val themeColorGray: String
    val themeColorWhite: String
    val messageUserUnlogged: String
    val loginTitle: String
    val fieldNodeName: String
    val fieldUsername: String
    val fieldPassword: String
    val actionLogout: String
    val actionFollow: String
    val relationshipStatusFollowing: String
    val relationshipStatusFollowsYou: String
    val relationshipStatusMutual: String
    val relationshipStatusRequestedToOther: String
    val relationshipStatusRequestedToYou: String
    val notificationTypeEntry: String
    val notificationTypeFavorite: String
    val notificationTypeFollow: String
    val notificationTypeFollowRequest: String
    val notificationTypeMention: String
    val notificationTypePoll: String
    val notificationTypeReblog: String
    val notificationTypeUpdate: String
    val exploreSectionHashtags: String
    val exploreSectionLinks: String
    val exploreSectionSuggestions: String
    val messageLoginOAuth: String
    val or: String
    val messageLoginLegacy: String

    fun hashtagPeopleUsing(count: Int): String

    val feedTypeTitle: String
    val followerTitle: String
    val followingTitle: String
    val actionUnfollow: String
    val actionDeleteFollowRequest: String
    val sidebarAnonymousTitle: String
    val sidebarAnonymousMessage: String
    val bookmarksTitle: String
    val favoritesTitle: String
    val followedHashtagsTitle: String
    val infoEdited: String

    fun extendedSocialInfoFavorites(count: Int): String

    fun extendedSocialInfoReblogs(count: Int): String

    val actionMuteNotifications: String
    val createPostTitle: String
    val messagePostEmptyText: String
    val visibilityPublic: String
    val visibilityPrivate: String
    val visibilityDirect: String
    val visibilityUnlisted: String
    val createPostBodyPlaceholder: String
    val createPostAttachmentsSection: String
    val actionEdit: String
    val pictureDescriptionPlaceholder: String
    val insertLinkDialogTitle: String
    val insertLinkFieldAnchor: String
    val insertLinkFieldUrl: String
    val selectUserDialogTitle: String
    val selectUserSearchPlaceholder: String
    val messageSearchInitialEmpty: String
    val searchSectionUsers: String
    val searchPlaceholder: String
    val topicTitle: String
    val threadTitle: String
    val buttonLoadMoreReplies: String
    val postSensitive: String
    val actionCreateThreadInGroup: String
    val settingsHeaderNsfw: String
    val settingsItemIncludeNsfw: String
    val settingsItemBlurNsfw: String
    val settingsItemDefaultTimelineType: String
    val actionDelete: String
    val actionShare: String
    val actionCopyUrl: String
    val messageTextCopiedToClipboard: String
    val contentScaleTitle: String
    val contentScaleFillHeight: String
    val contentScaleFillWidth: String
    val contentScaleFit: String
    val shareAsUrl: String
    val shareAsFile: String
    val actionMute: String
    val actionUnmute: String
    val settingsItemBlockedAndMuted: String
    val manageBlocksSectionMuted: String
    val manageBlocksSectionBlocked: String
    val actionBlock: String
    val actionUnblock: String
    val actionPin: String
    val actionUnpin: String
    val settingsSectionDebug: String
    val settingsAbout: String
    val settingsAboutAppVersion: String
    val settingsAboutChangelog: String
    val settingsAboutViewGithub: String
    val settingsAboutReportIssue: String
    val settingsAboutViewFriendica: String
    val manageCirclesTitle: String
    val createCircleTitle: String
    val editCircleTitle: String
    val circleEditFieldName: String
    val circleAddUsersDialogTitle: String
    val visibilityCircle: String
    val selectCircleDialogTitle: String
    val messagePostInvalidVisibility: String
    val settingsItemFontScale: String
    val fontScaleNormal: String
    val fontScaleSmaller: String
    val fontScaleSmallest: String
    val fontScaleLarger: String
    val fontScaleLargest: String
    val settingsItemUrlOpeningMode: String
    val urlOpeningModeExternal: String
    val urlOpeningModeCustomTabs: String
    val dialogErrorTitle: String
    val messagePollVoteErrorBody: String
    val buttonPollErrorOpenIssue: String
    val actionVote: String
    val pollExpired: String
    val shortUnavailable: String

    fun pollVote(count: Int): String

    val pollExpiresIn: String
    val actionShowResults: String
    val actionHideResults: String
    val inboxConfigureFilterDialogTitle: String
    val inboxConfigureFilterDialogSubtitle: String
    val notificationTypeEntryName: String
    val notificationTypeFavoriteName: String
    val notificationTypeFollowName: String
    val notificationTypeFollowRequestName: String
    val notificationTypeMentionName: String
    val notificationTypePollName: String
    val notificationTypeReblogName: String
    val notificationTypeUpdateName: String
    val muteDurationIndefinite: String
    val selectDurationDialogTitle: String
    val muteDurationItem: String
    val muteDisableNotificationsItem: String
    val actionSendFollowRequest: String
    val postBy: String
    val customOption: String
    val colorPickerDialogTitle: String
    val followRequestsTitle: String
    val actionAccept: String
    val actionReject: String
    val actionHideContent: String
    val messageEmptyInbox: String
    val createPostSpoilerPlaceholder: String
    val createPostTitlePlaceholder: String
    val actionSwitchAccount: String
    val actionDeleteAccount: String
    val editProfileTitle: String
    val editProfileSectionPersonal: String
    val editProfileItemDisplayName: String
    val editProfileItemBio: String
    val editProfileSectionFlags: String
    val editProfileItemBot: String
    val editProfileItemLocked: String
    val editProfileItemDiscoverable: String
    val editProfileItemNoIndex: String
    val unsavedChangesTitle: String
    val messageAreYouSureExit: String
    val buttonSave: String
    val editProfileSectionFields: String
    val editProfileItemFieldKey: String
    val editProfileItemFieldValue: String
    val editProfileSectionImages: String
    val editProfileItemAvatar: String
    val editProfileItemHeader: String
}

object Locales {
    const val EN = "en"
    const val IT = "it"
}

internal val localizableStrings: Map<LanguageTag, Strings> =
    mapOf(
        Locales.EN to EnStrings,
        Locales.IT to ItStrings,
    )

val LocalStrings: ProvidableCompositionLocal<Strings> =
    staticCompositionLocalOf { EnStrings }

@Composable
fun ProvideStrings(
    lyricist: Lyricist<Strings>,
    content: @Composable () -> Unit,
) {
    ProvideStrings(lyricist, LocalStrings, content)
}
