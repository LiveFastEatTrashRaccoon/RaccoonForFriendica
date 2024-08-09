package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.lyricist.LanguageTag
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings

interface Strings {
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
    val accountFollowing: String
    val accountFollower: String
    val accountGroup: String
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
    val postTitleBy: String
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
    val relationshipStatusGeneric: String
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
    val hashtagPeopleUsing: String
    val feedTypeTitle: String
    val followerTitle: String
    val followingTitle: String
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
