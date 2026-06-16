package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig
import raccoonforfriendica.core.resources.generated.resources.Res
import raccoonforfriendica.core.resources.generated.resources.abc
import raccoonforfriendica.core.resources.generated.resources.account_circle_fill
import raccoonforfriendica.core.resources.generated.resources.activitypub_small
import raccoonforfriendica.core.resources.generated.resources.add
import raccoonforfriendica.core.resources.generated.resources.add_circle
import raccoonforfriendica.core.resources.generated.resources.add_photo_alternate
import raccoonforfriendica.core.resources.generated.resources.alternate_email
import raccoonforfriendica.core.resources.generated.resources.api
import raccoonforfriendica.core.resources.generated.resources.arrow_back
import raccoonforfriendica.core.resources.generated.resources.arrow_circle_down
import raccoonforfriendica.core.resources.generated.resources.arrow_circle_up
import raccoonforfriendica.core.resources.generated.resources.arrow_drop_down
import raccoonforfriendica.core.resources.generated.resources.article
import raccoonforfriendica.core.resources.generated.resources.aspect_ratio
import raccoonforfriendica.core.resources.generated.resources.atkinsonhyperlegible_bold
import raccoonforfriendica.core.resources.generated.resources.atkinsonhyperlegible_italic
import raccoonforfriendica.core.resources.generated.resources.atkinsonhyperlegible_regular
import raccoonforfriendica.core.resources.generated.resources.bar_chart
import raccoonforfriendica.core.resources.generated.resources.bluesky_small
import raccoonforfriendica.core.resources.generated.resources.book
import raccoonforfriendica.core.resources.generated.resources.bookmark
import raccoonforfriendica.core.resources.generated.resources.bookmark_fill
import raccoonforfriendica.core.resources.generated.resources.bookmarks_fill
import raccoonforfriendica.core.resources.generated.resources.bug_report
import raccoonforfriendica.core.resources.generated.resources.build_circle
import raccoonforfriendica.core.resources.generated.resources.calendar_month_fill
import raccoonforfriendica.core.resources.generated.resources.camera
import raccoonforfriendica.core.resources.generated.resources.campaign
import raccoonforfriendica.core.resources.generated.resources.cancel
import raccoonforfriendica.core.resources.generated.resources.change_circle
import raccoonforfriendica.core.resources.generated.resources.chatFill
import raccoonforfriendica.core.resources.generated.resources.check
import raccoonforfriendica.core.resources.generated.resources.chevron_forward
import raccoonforfriendica.core.resources.generated.resources.circle
import raccoonforfriendica.core.resources.generated.resources.close
import raccoonforfriendica.core.resources.generated.resources.code
import raccoonforfriendica.core.resources.generated.resources.computer
import raccoonforfriendica.core.resources.generated.resources.cottage
import raccoonforfriendica.core.resources.generated.resources.dark_mode
import raccoonforfriendica.core.resources.generated.resources.dark_mode_fill
import raccoonforfriendica.core.resources.generated.resources.dashboard
import raccoonforfriendica.core.resources.generated.resources.diaspora_small
import raccoonforfriendica.core.resources.generated.resources.dns_fill
import raccoonforfriendica.core.resources.generated.resources.done_all
import raccoonforfriendica.core.resources.generated.resources.download
import raccoonforfriendica.core.resources.generated.resources.edit
import raccoonforfriendica.core.resources.generated.resources.exo_bold
import raccoonforfriendica.core.resources.generated.resources.exo_italic
import raccoonforfriendica.core.resources.generated.resources.exo_light
import raccoonforfriendica.core.resources.generated.resources.exo_regular
import raccoonforfriendica.core.resources.generated.resources.explicit_fill
import raccoonforfriendica.core.resources.generated.resources.explore_fill
import raccoonforfriendica.core.resources.generated.resources.favorite
import raccoonforfriendica.core.resources.generated.resources.favorite_fill
import raccoonforfriendica.core.resources.generated.resources.file_open
import raccoonforfriendica.core.resources.generated.resources.filter_list
import raccoonforfriendica.core.resources.generated.resources.flaky
import raccoonforfriendica.core.resources.generated.resources.flipboard_small
import raccoonforfriendica.core.resources.generated.resources.format_bold
import raccoonforfriendica.core.resources.generated.resources.format_italic
import raccoonforfriendica.core.resources.generated.resources.format_quote_fill
import raccoonforfriendica.core.resources.generated.resources.format_underlined
import raccoonforfriendica.core.resources.generated.resources.friendica_logo
import raccoonforfriendica.core.resources.generated.resources.friendica_small
import raccoonforfriendica.core.resources.generated.resources.gavel
import raccoonforfriendica.core.resources.generated.resources.gnusocial_small
import raccoonforfriendica.core.resources.generated.resources.gotosocial_small
import raccoonforfriendica.core.resources.generated.resources.group
import raccoonforfriendica.core.resources.generated.resources.handyman_fill
import raccoonforfriendica.core.resources.generated.resources.home_fill
import raccoonforfriendica.core.resources.generated.resources.ic_alt
import raccoonforfriendica.core.resources.generated.resources.ic_default
import raccoonforfriendica.core.resources.generated.resources.ic_loading
import raccoonforfriendica.core.resources.generated.resources.image
import raccoonforfriendica.core.resources.generated.resources.inbox_fill
import raccoonforfriendica.core.resources.generated.resources.info
import raccoonforfriendica.core.resources.generated.resources.kbin_small
import raccoonforfriendica.core.resources.generated.resources.lemmy_small
import raccoonforfriendica.core.resources.generated.resources.light_mode
import raccoonforfriendica.core.resources.generated.resources.lightbulb
import raccoonforfriendica.core.resources.generated.resources.link
import raccoonforfriendica.core.resources.generated.resources.lock
import raccoonforfriendica.core.resources.generated.resources.lock_open
import raccoonforfriendica.core.resources.generated.resources.logout
import raccoonforfriendica.core.resources.generated.resources.mastodon_logo
import raccoonforfriendica.core.resources.generated.resources.mastodon_small
import raccoonforfriendica.core.resources.generated.resources.menu
import raccoonforfriendica.core.resources.generated.resources.misskey_small
import raccoonforfriendica.core.resources.generated.resources.more_vert
import raccoonforfriendica.core.resources.generated.resources.music_note
import raccoonforfriendica.core.resources.generated.resources.notifications
import raccoonforfriendica.core.resources.generated.resources.notifications_active
import raccoonforfriendica.core.resources.generated.resources.notosans_bold
import raccoonforfriendica.core.resources.generated.resources.notosans_italic
import raccoonforfriendica.core.resources.generated.resources.notosans_medium
import raccoonforfriendica.core.resources.generated.resources.notosans_regular
import raccoonforfriendica.core.resources.generated.resources.open_in_browser
import raccoonforfriendica.core.resources.generated.resources.palette
import raccoonforfriendica.core.resources.generated.resources.peertube_small
import raccoonforfriendica.core.resources.generated.resources.person_add
import raccoonforfriendica.core.resources.generated.resources.pixelfed_small
import raccoonforfriendica.core.resources.generated.resources.play_arrow
import raccoonforfriendica.core.resources.generated.resources.play_circle
import raccoonforfriendica.core.resources.generated.resources.pleroma_small
import raccoonforfriendica.core.resources.generated.resources.post_add
import raccoonforfriendica.core.resources.generated.resources.public
import raccoonforfriendica.core.resources.generated.resources.radio_button_checked
import raccoonforfriendica.core.resources.generated.resources.repeat
import raccoonforfriendica.core.resources.generated.resources.reply
import raccoonforfriendica.core.resources.generated.resources.rocket_launch
import raccoonforfriendica.core.resources.generated.resources.rocket_launch_fill
import raccoonforfriendica.core.resources.generated.resources.save
import raccoonforfriendica.core.resources.generated.resources.schedule
import raccoonforfriendica.core.resources.generated.resources.schedule_send
import raccoonforfriendica.core.resources.generated.resources.search
import raccoonforfriendica.core.resources.generated.resources.send
import raccoonforfriendica.core.resources.generated.resources.settings_fill
import raccoonforfriendica.core.resources.generated.resources.share
import raccoonforfriendica.core.resources.generated.resources.shield
import raccoonforfriendica.core.resources.generated.resources.strikethrough_s
import raccoonforfriendica.core.resources.generated.resources.style_fill
import raccoonforfriendica.core.resources.generated.resources.stylus_fountain_pen_fill
import raccoonforfriendica.core.resources.generated.resources.support
import raccoonforfriendica.core.resources.generated.resources.tag
import raccoonforfriendica.core.resources.generated.resources.thumb_down
import raccoonforfriendica.core.resources.generated.resources.thumb_down_fill
import raccoonforfriendica.core.resources.generated.resources.thumb_up
import raccoonforfriendica.core.resources.generated.resources.thumb_up_fill
import raccoonforfriendica.core.resources.generated.resources.update
import raccoonforfriendica.core.resources.generated.resources.verified
import raccoonforfriendica.core.resources.generated.resources.view_agenda
import raccoonforfriendica.core.resources.generated.resources.visibility
import raccoonforfriendica.core.resources.generated.resources.visibility_off
import raccoonforfriendica.core.resources.generated.resources.volunteer_activism
import raccoonforfriendica.core.resources.generated.resources.wordpress_small
import raccoonforfriendica.core.resources.generated.resources.workspaces_fill

internal class DefaultCoreResources : CoreResources {

    // region Fonts
    override val atkinsonHyperlegible: FontFamily
        @Composable get() =
            FontFamily(
                Font(Res.font.atkinsonhyperlegible_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.atkinsonhyperlegible_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.atkinsonhyperlegible_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val exo2: FontFamily
        @Composable get() =
            FontFamily(
                Font(Res.font.exo_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.exo_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.exo_light, FontWeight.Light, FontStyle.Normal),
                Font(Res.font.exo_italic, FontWeight.Normal, FontStyle.Italic),
            )

    override val notoSans: FontFamily
        @Composable get() =
            FontFamily(
                Font(Res.font.notosans_regular, FontWeight.Normal, FontStyle.Normal),
                Font(Res.font.notosans_bold, FontWeight.Bold, FontStyle.Normal),
                Font(Res.font.notosans_medium, FontWeight.Medium, FontStyle.Normal),
                Font(Res.font.notosans_italic, FontWeight.Normal, FontStyle.Italic),
            )
    // endregion

    // region App icons
    override val appIconDefault: Painter
        @Composable get() = painterResource(Res.drawable.ic_default)

    override val appIconAlt: Painter
        @Composable get() = painterResource(Res.drawable.ic_alt)


    override val loadingIcon: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_loading)
    // endregion

    // region Media player config
    override val videoPlayerConfig: VideoPlayerConfig =
        VideoPlayerConfig(isFullScreenEnabled = false)

    override val audioPlayerConfig: AudioPlayerConfig =
        AudioPlayerConfig(controlsBottomPadding = 4.dp)
    // endregion

    // region Platform logos
    override val activityPubSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.activitypub_small)

    override val blueskySmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.bluesky_small)

    override val diasporaSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.diaspora_small)

    override val flipboardSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.flipboard_small)

    override val friendicaLogo: Painter
        @Composable get() = painterResource(Res.drawable.friendica_logo)

    override val friendicaSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.friendica_small)

    override val gnuSocialSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.gnusocial_small)

    override val gotoSocialSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.gotosocial_small)

    override val kbinSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.kbin_small)

    override val lemmySmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.lemmy_small)

    override val mastodonLogo: Painter
        @Composable get() = painterResource(Res.drawable.mastodon_logo)

    override val mastodonSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.mastodon_small)

    override val misskeySmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.misskey_small)

    override val peerTubeSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.peertube_small)

    override val pixelfedSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.pixelfed_small)

    override val pleromaSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.pleroma_small)

    override val wordPressSmallLogo: Painter
        @Composable get() = painterResource(Res.drawable.wordpress_small)

    // endregion

    // region Material Icons
    override val abc: ImageVector
        @Composable get() = vectorResource(Res.drawable.abc)

    override val accountCircleFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.account_circle_fill)

    override val add: ImageVector
        @Composable get() = vectorResource(Res.drawable.add)

    override val addCircle: ImageVector
        @Composable get() = vectorResource(Res.drawable.add_circle)

    override val addPhotoAlternate: ImageVector
        @Composable get() = vectorResource(Res.drawable.add_photo_alternate)

    override val alternateEmail: ImageVector
        @Composable get() = vectorResource(Res.drawable.alternate_email)

    override val api: ImageVector
        @Composable get() = vectorResource(Res.drawable.api)

    override val arrowBack: ImageVector
        @Composable get() = vectorResource(Res.drawable.arrow_back)

    override val arrowCircleDown: ImageVector
        @Composable get() = vectorResource(Res.drawable.arrow_circle_down)

    override val arrowCircleUp: ImageVector
        @Composable get() = vectorResource(Res.drawable.arrow_circle_up)

    override val arrowDropDown: ImageVector
        @Composable get() = vectorResource(Res.drawable.arrow_drop_down)

    override val article: ImageVector
        @Composable get() = vectorResource(Res.drawable.article)

    override val aspectRatio: ImageVector
        @Composable get() = vectorResource(Res.drawable.aspect_ratio)

    override val barChart: ImageVector
        @Composable get() = vectorResource(Res.drawable.bar_chart)

    override val book: ImageVector
        @Composable get() = vectorResource(Res.drawable.book)

    override val bookmark: ImageVector
        @Composable get() = vectorResource(Res.drawable.bookmark)

    override val bookmarksFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.bookmarks_fill)

    override val bookmarkFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.bookmark_fill)

    override val bugReport: ImageVector
        @Composable get() = vectorResource(Res.drawable.bug_report)

    override val buildCircle: ImageVector
        @Composable get() = vectorResource(Res.drawable.build_circle)

    override val calendarMonthFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.calendar_month_fill)

    override val camera: ImageVector
        @Composable get() = vectorResource(Res.drawable.camera)

    override val campaign: ImageVector
        @Composable get() = vectorResource(Res.drawable.campaign)

    override val cancel: ImageVector
        @Composable get() = vectorResource(Res.drawable.cancel)

    override val changeCircle: ImageVector
        @Composable get() = vectorResource(Res.drawable.change_circle)

    override val chatFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.chatFill)

    override val check: ImageVector
        @Composable get() = vectorResource(Res.drawable.check)

    override val chevronForward: ImageVector
        @Composable get() = vectorResource(Res.drawable.chevron_forward)

    override val circle: ImageVector
        @Composable get() = vectorResource(Res.drawable.circle)

    override val code: ImageVector
        @Composable get() = vectorResource(Res.drawable.code)

    override val close: ImageVector
        @Composable get() = vectorResource(Res.drawable.close)

    override val computer: ImageVector
        @Composable get() = vectorResource(Res.drawable.computer)

    override val cottage: ImageVector
        @Composable get() = vectorResource(Res.drawable.cottage)

    override val darkMode: ImageVector
        @Composable get() = vectorResource(Res.drawable.dark_mode)

    override val darkModeFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.dark_mode_fill)

    override val dashboard: ImageVector
        @Composable get() = vectorResource(Res.drawable.dashboard)

    override val dnsFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.dns_fill)

    override val doneAll: ImageVector
        @Composable get() = vectorResource(Res.drawable.done_all)

    override val download: ImageVector
        @Composable get() = vectorResource(Res.drawable.download)

    override val edit: ImageVector
        @Composable get() = vectorResource(Res.drawable.edit)

    override val explicitFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.explicit_fill)

    override val exploreFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.explore_fill)

    override val favorite: ImageVector
        @Composable get() = vectorResource(Res.drawable.favorite)

    override val favoriteFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.favorite_fill)

    override val fileOpen: ImageVector
        @Composable get() = vectorResource(Res.drawable.file_open)

    override val filterList: ImageVector
        @Composable get() = vectorResource(Res.drawable.filter_list)

    override val formatBold: ImageVector
        @Composable get() = vectorResource(Res.drawable.format_bold)

    override val formatItalic: ImageVector
        @Composable get() = vectorResource(Res.drawable.format_italic)

    override val formatQuoteFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.format_quote_fill)

    override val formatUnderlined: ImageVector
        @Composable get() = vectorResource(Res.drawable.format_underlined)

    override val flaky: ImageVector
        @Composable get() = vectorResource(Res.drawable.flaky)

    override val gavel: ImageVector
        @Composable get() = vectorResource(Res.drawable.gavel)

    override val group: ImageVector
        @Composable get() = vectorResource(Res.drawable.group)

    override val handymanFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.handyman_fill)

    override val homeFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.home_fill)

    override val image: ImageVector
        @Composable get() = vectorResource(Res.drawable.image)

    override val inboxFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.inbox_fill)

    override val info: ImageVector
        @Composable get() = vectorResource(Res.drawable.info)

    override val lightMode: ImageVector
        @Composable get() = vectorResource(Res.drawable.light_mode)

    override val lightbulb: ImageVector
        @Composable get() = vectorResource(Res.drawable.lightbulb)

    override val link: ImageVector
        @Composable get() = vectorResource(Res.drawable.link)

    override val lock: ImageVector
        @Composable get() = vectorResource(Res.drawable.lock)

    override val lockOpen: ImageVector
        @Composable get() = vectorResource(Res.drawable.lock_open)

    override val logout: ImageVector
        @Composable get() = vectorResource(Res.drawable.logout)

    override val menu: ImageVector
        @Composable get() = vectorResource(Res.drawable.menu)

    override val moreVert: ImageVector
        @Composable get() = vectorResource(Res.drawable.more_vert)

    override val musicNote: ImageVector
        @Composable get() = vectorResource(Res.drawable.music_note)

    override val notifications: ImageVector
        @Composable get() = vectorResource(Res.drawable.notifications)

    override val notificationsActive: ImageVector
        @Composable get() = vectorResource(Res.drawable.notifications_active)

    override val openInBrowser: ImageVector
        @Composable get() = vectorResource(Res.drawable.open_in_browser)

    override val palette: ImageVector
        @Composable get() = vectorResource(Res.drawable.palette)

    override val personAdd: ImageVector
        @Composable get() = vectorResource(Res.drawable.person_add)

    override val playArrow: ImageVector
        @Composable get() = vectorResource(Res.drawable.play_arrow)

    override val playCircle: ImageVector
        @Composable get() = vectorResource(Res.drawable.play_circle)

    override val postAdd: ImageVector
        @Composable get() = vectorResource(Res.drawable.post_add)

    override val public: ImageVector
        @Composable get() = vectorResource(Res.drawable.public)

    override val radioButtonChecked: ImageVector
        @Composable get() = vectorResource(Res.drawable.radio_button_checked)

    override val repeat: ImageVector
        @Composable get() = vectorResource(Res.drawable.repeat)

    override val reply: ImageVector
        @Composable get() = vectorResource(Res.drawable.reply)

    override val rocketLaunch: ImageVector
        @Composable get() = vectorResource(Res.drawable.rocket_launch)

    override val rocketLaunchFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.rocket_launch_fill)

    override val save: ImageVector
        @Composable get() = vectorResource(Res.drawable.save)

    override val schedule: ImageVector
        @Composable get() = vectorResource(Res.drawable.schedule)

    override val scheduleSend: ImageVector
        @Composable get() = vectorResource(Res.drawable.schedule_send)

    override val share: ImageVector
        @Composable get() = vectorResource(Res.drawable.share)

    override val search: ImageVector
        @Composable get() = vectorResource(Res.drawable.search)

    override val send: ImageVector
        @Composable get() = vectorResource(Res.drawable.send)

    override val settingsFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.settings_fill)

    override val shield: ImageVector
        @Composable get() = vectorResource(Res.drawable.shield)

    override val strikethroughS: ImageVector
        @Composable get() = vectorResource(Res.drawable.strikethrough_s)

    override val styleFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.style_fill)

    override val stylusFountainPenFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.stylus_fountain_pen_fill)

    override val support: ImageVector
        @Composable get() = vectorResource(Res.drawable.support)

    override val tag: ImageVector
        @Composable get() = vectorResource(Res.drawable.tag)

    override val thumbDown: ImageVector
        @Composable get() = vectorResource(Res.drawable.thumb_down)

    override val thumbDownFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.thumb_down_fill)

    override val thumbUp: ImageVector
        @Composable get() = vectorResource(Res.drawable.thumb_up)

    override val thumbUpFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.thumb_up_fill)

    override val update: ImageVector
        @Composable get() = vectorResource(Res.drawable.update)

    override val verified: ImageVector
        @Composable get() = vectorResource(Res.drawable.verified)

    override val viewAgenda: ImageVector
        @Composable get() = vectorResource(Res.drawable.view_agenda)

    override val visibility: ImageVector
        @Composable get() = vectorResource(Res.drawable.visibility)

    override val visibilityOff: ImageVector
        @Composable get() = vectorResource(Res.drawable.visibility_off)

    override val volunteerActivism: ImageVector
        @Composable get() = vectorResource(Res.drawable.volunteer_activism)

    override val workspacesFill: ImageVector
        @Composable get() = vectorResource(Res.drawable.workspaces_fill)
}
