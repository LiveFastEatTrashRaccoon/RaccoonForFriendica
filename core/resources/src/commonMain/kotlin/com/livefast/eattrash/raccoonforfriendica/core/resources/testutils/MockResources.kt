package com.livefast.eattrash.raccoonforfriendica.core.resources.testutils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

private val mockPainter = ColorPainter(Color.Transparent)
private val mockImageVector = ImageVector.Builder(
    name = "Mock",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).build()

object MockResources : CoreResources {
    override val atkinsonHyperlegible: FontFamily @Composable get() = FontFamily()
    override val exo2: FontFamily @Composable get() = FontFamily()
    override val notoSans: FontFamily @Composable get() = FontFamily()

    override val appIconDefault: Painter @Composable get() = mockPainter
    override val appIconAlt: Painter @Composable get() = mockPainter

    override val videoPlayerConfig: VideoPlayerConfig = VideoPlayerConfig()
    override val audioPlayerConfig: AudioPlayerConfig = AudioPlayerConfig()

    override val activityPubSmallLogo: Painter @Composable get() = mockPainter
    override val blueskySmallLogo: Painter @Composable get() = mockPainter
    override val diasporaSmallLogo: Painter @Composable get() = mockPainter
    override val flipboardSmallLogo: Painter @Composable get() = mockPainter
    override val friendicaLogo: Painter @Composable get() = mockPainter
    override val friendicaSmallLogo: Painter @Composable get() = mockPainter
    override val gnuSocialSmallLogo: Painter @Composable get() = mockPainter
    override val gotoSocialSmallLogo: Painter @Composable get() = mockPainter
    override val kbinSmallLogo: Painter @Composable get() = mockPainter
    override val lemmySmallLogo: Painter @Composable get() = mockPainter
    override val mastodonLogo: Painter @Composable get() = mockPainter
    override val mastodonSmallLogo: Painter @Composable get() = mockPainter
    override val misskeySmallLogo: Painter @Composable get() = mockPainter
    override val peerTubeSmallLogo: Painter @Composable get() = mockPainter
    override val pixelfedSmallLogo: Painter @Composable get() = mockPainter
    override val pleromaSmallLogo: Painter @Composable get() = mockPainter
    override val wordPressSmallLogo: Painter @Composable get() = mockPainter

    override val abc: ImageVector @Composable get() = mockImageVector
    override val accountCircleFill: ImageVector @Composable get() = mockImageVector
    override val add: ImageVector @Composable get() = mockImageVector
    override val addCircle: ImageVector @Composable get() = mockImageVector
    override val addPhotoAlternate: ImageVector @Composable get() = mockImageVector
    override val alternateEmail: ImageVector @Composable get() = mockImageVector
    override val api: ImageVector @Composable get() = mockImageVector
    override val arrowBack: ImageVector @Composable get() = mockImageVector
    override val arrowCircleDown: ImageVector @Composable get() = mockImageVector
    override val arrowCircleUp: ImageVector @Composable get() = mockImageVector
    override val arrowDropDown: ImageVector @Composable get() = mockImageVector
    override val article: ImageVector @Composable get() = mockImageVector
    override val aspectRatio: ImageVector @Composable get() = mockImageVector
    override val barChart: ImageVector @Composable get() = mockImageVector
    override val book: ImageVector @Composable get() = mockImageVector
    override val bookmark: ImageVector @Composable get() = mockImageVector
    override val bookmarksFill: ImageVector @Composable get() = mockImageVector
    override val bookmarkFill: ImageVector @Composable get() = mockImageVector
    override val bugReport: ImageVector @Composable get() = mockImageVector
    override val buildCircle: ImageVector @Composable get() = mockImageVector
    override val calendarMonthFill: ImageVector @Composable get() = mockImageVector
    override val camera: ImageVector @Composable get() = mockImageVector
    override val campaign: ImageVector @Composable get() = mockImageVector
    override val cancel: ImageVector @Composable get() = mockImageVector
    override val changeCircle: ImageVector @Composable get() = mockImageVector
    override val chatFill: ImageVector @Composable get() = mockImageVector
    override val check: ImageVector @Composable get() = mockImageVector
    override val chevronForward: ImageVector @Composable get() = mockImageVector
    override val circle: ImageVector @Composable get() = mockImageVector
    override val code: ImageVector @Composable get() = mockImageVector
    override val close: ImageVector @Composable get() = mockImageVector
    override val computer: ImageVector @Composable get() = mockImageVector
    override val cottage: ImageVector @Composable get() = mockImageVector
    override val darkMode: ImageVector @Composable get() = mockImageVector
    override val darkModeFill: ImageVector @Composable get() = mockImageVector
    override val dashboard: ImageVector @Composable get() = mockImageVector
    override val dnsFill: ImageVector @Composable get() = mockImageVector
    override val doneAll: ImageVector @Composable get() = mockImageVector
    override val download: ImageVector @Composable get() = mockImageVector
    override val edit: ImageVector @Composable get() = mockImageVector
    override val explicitFill: ImageVector @Composable get() = mockImageVector
    override val exploreFill: ImageVector @Composable get() = mockImageVector
    override val favorite: ImageVector @Composable get() = mockImageVector
    override val favoriteFill: ImageVector @Composable get() = mockImageVector
    override val fileOpen: ImageVector @Composable get() = mockImageVector
    override val filterList: ImageVector @Composable get() = mockImageVector
    override val formatBold: ImageVector @Composable get() = mockImageVector
    override val formatItalic: ImageVector @Composable get() = mockImageVector
    override val formatUnderlined: ImageVector @Composable get() = mockImageVector
    override val flaky: ImageVector @Composable get() = mockImageVector
    override val gavel: ImageVector @Composable get() = mockImageVector
    override val group: ImageVector @Composable get() = mockImageVector
    override val handymanFill: ImageVector @Composable get() = mockImageVector
    override val homeFill: ImageVector @Composable get() = mockImageVector
    override val image: ImageVector @Composable get() = mockImageVector
    override val inboxFill: ImageVector @Composable get() = mockImageVector
    override val info: ImageVector @Composable get() = mockImageVector
    override val lightMode: ImageVector @Composable get() = mockImageVector
    override val lightbulb: ImageVector @Composable get() = mockImageVector
    override val link: ImageVector @Composable get() = mockImageVector
    override val lock: ImageVector @Composable get() = mockImageVector
    override val lockOpen: ImageVector @Composable get() = mockImageVector
    override val logout: ImageVector @Composable get() = mockImageVector
    override val menu: ImageVector @Composable get() = mockImageVector
    override val moreVert: ImageVector @Composable get() = mockImageVector
    override val musicNote: ImageVector @Composable get() = mockImageVector
    override val notifications: ImageVector @Composable get() = mockImageVector
    override val notificationsActive: ImageVector @Composable get() = mockImageVector
    override val openInBrowser: ImageVector @Composable get() = mockImageVector
    override val palette: ImageVector @Composable get() = mockImageVector
    override val personAdd: ImageVector @Composable get() = mockImageVector
    override val playArrow: ImageVector @Composable get() = mockImageVector
    override val playCircle: ImageVector @Composable get() = mockImageVector
    override val postAdd: ImageVector @Composable get() = mockImageVector
    override val public: ImageVector @Composable get() = mockImageVector
    override val radioButtonChecked: ImageVector @Composable get() = mockImageVector
    override val repeat: ImageVector @Composable get() = mockImageVector
    override val reply: ImageVector @Composable get() = mockImageVector
    override val rocketLaunch: ImageVector @Composable get() = mockImageVector
    override val rocketLaunchFill: ImageVector @Composable get() = mockImageVector
    override val save: ImageVector @Composable get() = mockImageVector
    override val schedule: ImageVector @Composable get() = mockImageVector
    override val scheduleSend: ImageVector @Composable get() = mockImageVector
    override val share: ImageVector @Composable get() = mockImageVector
    override val search: ImageVector @Composable get() = mockImageVector
    override val send: ImageVector @Composable get() = mockImageVector
    override val settingsFill: ImageVector @Composable get() = mockImageVector
    override val shield: ImageVector @Composable get() = mockImageVector
    override val strikethroughS: ImageVector @Composable get() = mockImageVector
    override val styleFill: ImageVector @Composable get() = mockImageVector
    override val stylusFountainPenFill: ImageVector @Composable get() = mockImageVector
    override val support: ImageVector @Composable get() = mockImageVector
    override val tag: ImageVector @Composable get() = mockImageVector
    override val thumbDown: ImageVector @Composable get() = mockImageVector
    override val thumbDownFill: ImageVector @Composable get() = mockImageVector
    override val thumbUp: ImageVector @Composable get() = mockImageVector
    override val thumbUpFill: ImageVector @Composable get() = mockImageVector
    override val update: ImageVector @Composable get() = mockImageVector
    override val verified: ImageVector @Composable get() = mockImageVector
    override val viewAgenda: ImageVector @Composable get() = mockImageVector
    override val visibility: ImageVector @Composable get() = mockImageVector
    override val visibilityOff: ImageVector @Composable get() = mockImageVector
    override val volunteerActivism: ImageVector @Composable get() = mockImageVector
    override val workspacesFill: ImageVector @Composable get() = mockImageVector
}
