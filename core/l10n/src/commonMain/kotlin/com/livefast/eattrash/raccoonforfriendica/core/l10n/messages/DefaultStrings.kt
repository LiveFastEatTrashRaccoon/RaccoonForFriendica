package com.livefast.eattrash.raccoonforfriendica.core.l10n.messages

internal open class DefaultStrings : Strings {
    override val messageConfirmExit = "Tap 🔙 again to exit"
    override val messageAreYouSure = "Are you sure you want to continue?"
    override val messageSuccess = "Operation completed successfully!"
    override val messageGenericError = "An unexpected error occurred"
    override val messageInvalidField = "Invalid field"
    override val messageMissingField = "Missing field"
    override val messageEmptyList = "There is nothing to display here 🗑️️"
    override val buttonConfirm = "Confirm"
    override val buttonOk = "OK"
    override val buttonClose = "Close"
    override val buttonCancel = "Cancel"
    override val systemDefault = "System"
    override val settingsThemeLight = "Light"
    override val settingsThemeDark = "Dark"
    override val settingsThemeBlack = "Black (AMOLED)"
    override val sectionTitleHome = "Timeline"
    override val sectionTitleExplore = "Explore"
    override val sectionTitleInbox = "Inbox"
    override val sectionTitleProfile = "Profile"
    override val barThemeOpaque = "Opaque"
    override val barThemeTransparent = "Transparent"
    override val timelineAll = "All"
    override val timelineSubscriptions = "Subscriptions"
    override val timelineLocal = "Local"
    override val nodeVia = "via"
    override val timelineEntryInReplyTo = "in reply to"
    override val timelineEntryRebloggedBy = "re-shared by"

    override fun accountFollower(count: Int) =
        when (count) {
            1 -> "follower"
            else -> "followers"
        }

    override fun accountFollowing(count: Int) = "following"

    override val actionOpenInForumMode = "open in forum mode"
    override val accountAge = "account age"
    override val dateYearShort = "y"
    override val dateMonthShort = "m"
    override val dateDayShort = "d"
    override val timeHourShort = "h"
    override val timeMinuteShort = "min"
    override val timeSecondShort = "s"
    override val accountSectionPosts = "Posts"
    override val accountSectionAll = "Posts & Replies"
    override val accountSectionPinned = "Pinned"
    override val accountSectionMedia = "Media"
    override val postTitle = "Post"
    override val languageEn = "English"
    override val languageIt = "Italiano"
    override val settingsTitle = "Settings"
    override val settingsHeaderGeneral = "General"
    override val settingsItemLanguage = "Language"
    override val settingsHeaderLookAndFeel = "Look & feel"
    override val settingsItemTheme = "UI theme"
    override val settingsItemFontFamily = "Font family"
    override val settingsItemDynamicColors = "Material You"
    override val settingsItemDynamicColorsSubtitle = "generate a palette based on your background"
    override val settingsItemThemeColor = "Theme color"
    override val settingsItemThemeColorSubtitle = "only applied if \"Material You\" is disabled"
    override val themeColorPurple = "Oceanic octopus"
    override val themeColorBlue = "Whimsical whale"
    override val themeColorLightBlue = "Distracted dolphin"
    override val themeColorGreen = "Frolicsome frog"
    override val themeColorYellow = "Hilarious hedgehog"
    override val themeColorOrange = "Fiery Fox"
    override val themeColorRed = "Crunchy Crab"
    override val themeColorPink = "Unique Unicorn"
    override val themeColorGray = "Ravenous Raccoon"
    override val themeColorWhite = "Boisterous bear"
    override val messageUserUnlogged = "You need to be logged in to access this section 🪵"
    override val loginTitle = "Login"
    override val fieldNodeName = "Instance name"
    override val fieldUsername = "Username"
    override val fieldPassword = "Password"
    override val actionLogout = "Logout"
    override val relationshipStatusFollowing = "Following"
    override val relationshipStatusFollowsYou = "Follows you"
    override val relationshipStatusMutual = "Mutual"
    override val relationshipStatusRequestedToOther = "Request sent"
    override val relationshipStatusRequestedToYou = "Request pending"
    override val notificationTypeEntry = "published a post"
    override val notificationTypeFavorite = "added one of your posts to favorites"
    override val notificationTypeFollow = "started following you"
    override val notificationTypeFollowRequest = "sent you a follow request"
    override val notificationTypeMention = "mentioned you in a post"
    override val notificationTypePoll = "see the results of a poll you participated in"
    override val notificationTypeReblog = "re-shared one of your posts"
    override val notificationTypeUpdate = "updated a post you re-shared"
    override val exploreSectionHashtags = "Hashtags"
    override val exploreSectionLinks = "Links"
    override val exploreSectionSuggestions = "For you"

    override fun hashtagPeopleUsing(count: Int) =
        when (count) {
            1 -> "person is talking about it"
            else -> "people are talking about it"
        }

    override val feedTypeTitle = "Feed type"
    override val followerTitle = "Followers"
    override val followingTitle = "Following"
    override val actionFollow = "Follow"
    override val actionUnfollow = "Unfollow"
    override val actionDeleteFollowRequest = "Cancel follow request"
    override val sidebarAnonymousTitle = "Anonymous"
    override val sidebarAnonymousMessage =
        "Welcome to Raccoon!\n\nYou can log in to your instance at any time from the Profile screen.\n\nEnjoy Friendica!"
    override val bookmarksTitle = "Bookmarks"
    override val favoritesTitle = "Favorites"
    override val followedHashtagsTitle = "Followed hashtags"
    override val infoEdited = "edited"

    override fun extendedSocialInfoFavorites(count: Int) =
        when (count) {
            1 -> "favorite"
            else -> "favorites"
        }

    override fun extendedSocialInfoReblogs(count: Int) =
        when (count) {
            1 -> "re-share"
            else -> "re-shares"
        }

    override val actionMuteNotifications = "Mute notifications"
    override val createPostTitle = "Create post"
    override val messagePostEmptyText = "Please provide either an attachment or some text"
    override val visibilityPublic = "Public"
    override val visibilityUnlisted = "Unlisted"
    override val visibilityPrivate = "Private"
    override val visibilityDirect = "Only mentioned"
    override val createPostBodyPlaceholder = "Your awesome new post..."
    override val createPostAttachmentsSection = "Attachments"
    override val actionEdit = "Edit"
    override val pictureDescriptionPlaceholder = "Image description"
    override val insertLinkDialogTitle = "Insert link"
    override val insertLinkFieldAnchor = "Anchor"
    override val insertLinkFieldUrl = "URL"
    override val selectUserDialogTitle = "Select a user"
    override val selectUserSearchPlaceholder = "username or handle"
    override val searchSectionUsers = "Users"
    override val searchPlaceholder = "Search the Fediverse"
    override val messageSearchInitialEmpty = "Start typing something to search"
    override val topicTitle = "Topic"
    override val threadTitle = "Thread"
    override val buttonLoadMoreReplies = "Load more replies"
    override val postSensitive = "Sensitive content"
    override val actionCreateThreadInGroup = "Post to"
    override val settingsHeaderNsfw = "NSFW"
    override val settingsItemIncludeNsfw = "Include NSFW contents"
    override val settingsItemBlurNsfw = "Blur NSFW media"
    override val settingsItemDefaultTimelineType = "Default timeline type"
    override val actionDelete = "Delete"
    override val actionShare = "Share"
    override val actionCopyUrl = "Copy link"
    override val messageTextCopiedToClipboard = "Copied to clipboard! 📋"
    override val contentScaleFit = "Aspect fit"
    override val contentScaleFillWidth = "Fill width"
    override val contentScaleFillHeight = "Fill height"
    override val contentScaleTitle = "Scale mode"
    override val shareAsUrl = "Share as URL"
    override val shareAsFile = "Share as file"
    override val actionMute = "Mute"
    override val actionUnmute = "Unmute"
    override val settingsItemBlockedAndMuted = "Manage filters"
    override val manageBlocksSectionMuted = "Muted"
    override val manageBlocksSectionBlocked = "Blocked"
    override val actionBlock = "Block"
    override val actionUnblock = "Unblock"
    override val messageLoginOAuth = "Login with OAuth2 (recommended)"
    override val or = "or"
    override val messageLoginLegacy = "Login with username and password (legacy)"
    override val actionPin = "Pin to profile"
    override val actionUnpin = "Unpin from profile"
    override val settingsSectionDebug = "Debug"
    override val settingsAbout = "App information"
    override val settingsAboutAppVersion = "Version"
    override val settingsAboutChangelog = "Changelog"
    override val settingsAboutViewGithub = "View on GitHub"
    override val settingsAboutReportIssue = "Report an issue"
    override val settingsAboutViewFriendica = "View on Friendica"
    override val manageCirclesTitle = "Circles"
    override val createCircleTitle = "Create circle"
    override val editCircleTitle = "Edit circle"
    override val circleEditFieldName = "Name"
    override val circleAddUsersDialogTitle = "Select users to add"
    override val visibilityCircle = "Circle"
    override val selectCircleDialogTitle = "Select a circle"
    override val messagePostInvalidVisibility = "Please select a valid visibility option"
    override val settingsItemFontScale = "Font size"
    override val fontScaleNormal = "Normal"
    override val fontScaleSmaller = "Smaller"
    override val fontScaleSmallest = "Smallest"
    override val fontScaleLarger = "Larger"
    override val fontScaleLargest = "Largest"
    override val settingsItemUrlOpeningMode = "URL opening mode"
    override val urlOpeningModeExternal = "External"
    override val urlOpeningModeCustomTabs = "Custom tabs"
    override val dialogErrorTitle = "Oops..."
    override val messagePollVoteErrorBody =
        "Unfortunately, I'm just a mobile dev and I can't add missing back-end methods!\nCheck out this issue and put a 👍 so that the devs know it may be worth implementing it."
    override val buttonPollErrorOpenIssue = "View on GitHub"
    override val actionVote = "Vote"
    override val pollExpired = "Expired"
    override val shortUnavailable = "N/A"

    override fun pollVote(count: Int): String =
        when (count) {
            1 -> "vote"
            else -> "votes"
        }

    override val pollExpiresIn = "Expires in"
    override val actionHideResults = "Show results"
    override val actionShowResults = "Hide results"
    override val inboxConfigureFilterDialogTitle = "Configure filters"
    override val inboxConfigureFilterDialogSubtitle =
        "If any value is deselected, the filter will only return unread items"
    override val notificationTypeEntryName = "Post notifications"
    override val notificationTypeFavoriteName = "Favorites"
    override val notificationTypeFollowName = "New followers"
    override val notificationTypeFollowRequestName = "Follow requests"
    override val notificationTypeMentionName = "Mentions & Replies"
    override val notificationTypePollName = "Polls"
    override val notificationTypeReblogName = "Re-shares"
    override val notificationTypeUpdateName = "Post updates"
    override val muteDurationIndefinite = "Indefinite"
    override val selectDurationDialogTitle = "Select duration"
    override val muteDurationItem = "You will not see any posts by this user for"
    override val muteDisableNotificationsItem = "Disable notifications"
    override val actionSendFollowRequest = "Send request"
    override val postBy = "by"
    override val customOption = "Custom"
    override val colorPickerDialogTitle = "Select a color"
    override val followRequestsTitle = "Follow requests"
    override val actionAccept = "Accept"
    override val actionReject = "Reject"
}
