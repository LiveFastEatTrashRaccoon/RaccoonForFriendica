package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig

interface CoreResources {
    // region Fonts
    val atkinsonHyperlegible: FontFamily @Composable get
    val exo2: FontFamily @Composable get
    val notoSans: FontFamily @Composable get
    // endregion

    // region App icons
    val appIconDefault: Painter @Composable get
    val appIconAlt: Painter @Composable get
    // endregion

    // region Media player config
    val videoPlayerConfig: VideoPlayerConfig
    val audioPlayerConfig: AudioPlayerConfig
    // endregion

    // region Platform logos
    val activityPubSmallLogo: Painter @Composable get
    val blueskySmallLogo: Painter @Composable get
    val diasporaSmallLogo: Painter @Composable get
    val flipboardSmallLogo: Painter @Composable get
    val friendicaLogo: Painter @Composable get
    val friendicaSmallLogo: Painter @Composable get
    val gnuSocialSmallLogo: Painter @Composable get
    val gotoSocialSmallLogo: Painter @Composable get
    val kbinSmallLogo: Painter @Composable get
    val lemmySmallLogo: Painter @Composable get
    val mastodonLogo: Painter @Composable get
    val mastodonSmallLogo: Painter @Composable get
    val misskeySmallLogo: Painter @Composable get
    val peerTubeSmallLogo: Painter @Composable get
    val pixelfedSmallLogo: Painter @Composable get
    val pleromaSmallLogo: Painter @Composable get
    val wordPressSmallLogo: Painter @Composable get
    // endregion

    // region Material Icons
    val abc: ImageVector @Composable get
    val accountCircleFill: ImageVector @Composable get
    val add: ImageVector @Composable get
    val addCircle: ImageVector @Composable get
    val addPhotoAlternate: ImageVector @Composable get
    val alternateEmail: ImageVector @Composable get
    val api: ImageVector @Composable get
    val arrowBack: ImageVector @Composable get
    val arrowCircleDown: ImageVector @Composable get
    val arrowCircleUp: ImageVector @Composable get
    val arrowDropDown: ImageVector @Composable get
    val article: ImageVector @Composable get
    val aspectRatio: ImageVector @Composable get
    val barChart: ImageVector @Composable get
    val book: ImageVector @Composable get
    val bookmark: ImageVector @Composable get
    val bookmarksFill: ImageVector @Composable get
    val bookmarkFill: ImageVector @Composable get
    val bugReport: ImageVector @Composable get
    val buildCircle: ImageVector @Composable get
    val calendarMonthFill: ImageVector @Composable get
    val camera: ImageVector @Composable get
    val campaign: ImageVector @Composable get
    val cancel: ImageVector @Composable get
    val changeCircle: ImageVector @Composable get
    val chatFill: ImageVector @Composable get
    val check: ImageVector @Composable get
    val chevronForward: ImageVector @Composable get
    val circle: ImageVector @Composable get
    val code: ImageVector @Composable get
    val close: ImageVector @Composable get
    val computer: ImageVector @Composable get
    val cottage: ImageVector @Composable get
    val darkMode: ImageVector @Composable get
    val darkModeFill: ImageVector @Composable get
    val dashboard: ImageVector @Composable get
    val dnsFill: ImageVector @Composable get
    val doneAll: ImageVector @Composable get
    val download: ImageVector @Composable get
    val edit: ImageVector @Composable get
    val explicitFill: ImageVector @Composable get
    val exploreFill: ImageVector @Composable get
    val favorite: ImageVector @Composable get
    val favoriteFill: ImageVector @Composable get
    val fileOpen: ImageVector @Composable get
    val filterList: ImageVector @Composable get
    val formatBold: ImageVector @Composable get
    val formatItalic: ImageVector @Composable get
    val formatQuoteFill: ImageVector @Composable get
    val formatUnderlined: ImageVector @Composable get
    val flaky: ImageVector @Composable get
    val gavel: ImageVector @Composable get
    val group: ImageVector @Composable get
    val handymanFill: ImageVector @Composable get
    val homeFill: ImageVector @Composable get
    val image: ImageVector @Composable get
    val inboxFill: ImageVector @Composable get
    val info: ImageVector @Composable get
    val lightMode: ImageVector @Composable get
    val lightbulb: ImageVector @Composable get
    val link: ImageVector @Composable get
    val lock: ImageVector @Composable get
    val lockOpen: ImageVector @Composable get
    val logout: ImageVector @Composable get
    val menu: ImageVector @Composable get
    val moreVert: ImageVector @Composable get
    val musicNote: ImageVector @Composable get
    val notifications: ImageVector @Composable get
    val notificationsActive: ImageVector @Composable get
    val openInBrowser: ImageVector @Composable get
    val palette: ImageVector @Composable get
    val personAdd: ImageVector @Composable get
    val playArrow: ImageVector @Composable get
    val playCircle: ImageVector @Composable get
    val postAdd: ImageVector @Composable get
    val public: ImageVector @Composable get
    val radioButtonChecked: ImageVector @Composable get
    val repeat: ImageVector @Composable get
    val reply: ImageVector @Composable get
    val rocketLaunch: ImageVector @Composable get
    val rocketLaunchFill: ImageVector @Composable get
    val save: ImageVector @Composable get
    val schedule: ImageVector @Composable get
    val scheduleSend: ImageVector @Composable get
    val share: ImageVector @Composable get
    val search: ImageVector @Composable get
    val send: ImageVector @Composable get
    val settingsFill: ImageVector @Composable get
    val shield: ImageVector @Composable get
    val strikethroughS: ImageVector @Composable get
    val styleFill: ImageVector @Composable get
    val stylusFountainPenFill: ImageVector @Composable get
    val support: ImageVector @Composable get
    val tag: ImageVector @Composable get
    val thumbDown: ImageVector @Composable get
    val thumbDownFill: ImageVector @Composable get
    val thumbUp: ImageVector @Composable get
    val thumbUpFill: ImageVector @Composable get
    val update: ImageVector @Composable get
    val verified: ImageVector @Composable get
    val viewAgenda: ImageVector @Composable get
    val visibility: ImageVector @Composable get
    val visibilityOff: ImageVector @Composable get
    val volunteerActivism: ImageVector @Composable get
    val workspacesFill: ImageVector @Composable get
    // endregion
}
