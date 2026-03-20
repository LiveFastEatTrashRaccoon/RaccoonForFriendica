package com.livefast.eattrash.raccoonforfriendica.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.model.VideoPlayerConfig
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import raccoonforfriendica.shared.generated.resources.Res
import raccoonforfriendica.shared.generated.resources.abc
import raccoonforfriendica.shared.generated.resources.account_circle_fill
import raccoonforfriendica.shared.generated.resources.activitypub_small
import raccoonforfriendica.shared.generated.resources.add
import raccoonforfriendica.shared.generated.resources.add_circle
import raccoonforfriendica.shared.generated.resources.add_photo_alternate
import raccoonforfriendica.shared.generated.resources.alternate_email
import raccoonforfriendica.shared.generated.resources.api
import raccoonforfriendica.shared.generated.resources.arrow_back
import raccoonforfriendica.shared.generated.resources.arrow_circle_down
import raccoonforfriendica.shared.generated.resources.arrow_circle_up
import raccoonforfriendica.shared.generated.resources.arrow_drop_down
import raccoonforfriendica.shared.generated.resources.article
import raccoonforfriendica.shared.generated.resources.aspect_ratio
import raccoonforfriendica.shared.generated.resources.atkinsonhyperlegible_bold
import raccoonforfriendica.shared.generated.resources.atkinsonhyperlegible_italic
import raccoonforfriendica.shared.generated.resources.atkinsonhyperlegible_regular
import raccoonforfriendica.shared.generated.resources.bar_chart
import raccoonforfriendica.shared.generated.resources.bluesky_small
import raccoonforfriendica.shared.generated.resources.book
import raccoonforfriendica.shared.generated.resources.bookmark
import raccoonforfriendica.shared.generated.resources.bookmark_fill
import raccoonforfriendica.shared.generated.resources.bookmarks_fill
import raccoonforfriendica.shared.generated.resources.bug_report
import raccoonforfriendica.shared.generated.resources.build_circle
import raccoonforfriendica.shared.generated.resources.calendar_month_fill
import raccoonforfriendica.shared.generated.resources.camera
import raccoonforfriendica.shared.generated.resources.campaign
import raccoonforfriendica.shared.generated.resources.cancel
import raccoonforfriendica.shared.generated.resources.change_circle
import raccoonforfriendica.shared.generated.resources.chatFill
import raccoonforfriendica.shared.generated.resources.check
import raccoonforfriendica.shared.generated.resources.chevron_forward
import raccoonforfriendica.shared.generated.resources.circle
import raccoonforfriendica.shared.generated.resources.close
import raccoonforfriendica.shared.generated.resources.code
import raccoonforfriendica.shared.generated.resources.computer
import raccoonforfriendica.shared.generated.resources.cottage
import raccoonforfriendica.shared.generated.resources.dark_mode
import raccoonforfriendica.shared.generated.resources.dark_mode_fill
import raccoonforfriendica.shared.generated.resources.dashboard
import raccoonforfriendica.shared.generated.resources.diaspora_small
import raccoonforfriendica.shared.generated.resources.dns_fill
import raccoonforfriendica.shared.generated.resources.done_all
import raccoonforfriendica.shared.generated.resources.download
import raccoonforfriendica.shared.generated.resources.edit
import raccoonforfriendica.shared.generated.resources.exo_bold
import raccoonforfriendica.shared.generated.resources.exo_italic
import raccoonforfriendica.shared.generated.resources.exo_light
import raccoonforfriendica.shared.generated.resources.exo_regular
import raccoonforfriendica.shared.generated.resources.explicit_fill
import raccoonforfriendica.shared.generated.resources.explore_fill
import raccoonforfriendica.shared.generated.resources.favorite
import raccoonforfriendica.shared.generated.resources.favorite_fill
import raccoonforfriendica.shared.generated.resources.file_open
import raccoonforfriendica.shared.generated.resources.filter_list
import raccoonforfriendica.shared.generated.resources.flaky
import raccoonforfriendica.shared.generated.resources.flipboard_small
import raccoonforfriendica.shared.generated.resources.format_bold
import raccoonforfriendica.shared.generated.resources.format_italic
import raccoonforfriendica.shared.generated.resources.format_underlined
import raccoonforfriendica.shared.generated.resources.friendica_logo
import raccoonforfriendica.shared.generated.resources.friendica_small
import raccoonforfriendica.shared.generated.resources.gavel
import raccoonforfriendica.shared.generated.resources.gnusocial_small
import raccoonforfriendica.shared.generated.resources.gotosocial_small
import raccoonforfriendica.shared.generated.resources.group
import raccoonforfriendica.shared.generated.resources.handyman_fill
import raccoonforfriendica.shared.generated.resources.home_fill
import raccoonforfriendica.shared.generated.resources.ic_alt
import raccoonforfriendica.shared.generated.resources.ic_default
import raccoonforfriendica.shared.generated.resources.image
import raccoonforfriendica.shared.generated.resources.inbox_fill
import raccoonforfriendica.shared.generated.resources.info
import raccoonforfriendica.shared.generated.resources.kbin_small
import raccoonforfriendica.shared.generated.resources.lemmy_small
import raccoonforfriendica.shared.generated.resources.light_mode
import raccoonforfriendica.shared.generated.resources.lightbulb
import raccoonforfriendica.shared.generated.resources.link
import raccoonforfriendica.shared.generated.resources.lock
import raccoonforfriendica.shared.generated.resources.lock_open
import raccoonforfriendica.shared.generated.resources.logout
import raccoonforfriendica.shared.generated.resources.mastodon_logo
import raccoonforfriendica.shared.generated.resources.mastodon_small
import raccoonforfriendica.shared.generated.resources.menu
import raccoonforfriendica.shared.generated.resources.misskey_small
import raccoonforfriendica.shared.generated.resources.more_vert
import raccoonforfriendica.shared.generated.resources.music_note
import raccoonforfriendica.shared.generated.resources.notifications
import raccoonforfriendica.shared.generated.resources.notifications_active
import raccoonforfriendica.shared.generated.resources.notosans_bold
import raccoonforfriendica.shared.generated.resources.notosans_italic
import raccoonforfriendica.shared.generated.resources.notosans_medium
import raccoonforfriendica.shared.generated.resources.notosans_regular
import raccoonforfriendica.shared.generated.resources.open_in_browser
import raccoonforfriendica.shared.generated.resources.palette
import raccoonforfriendica.shared.generated.resources.peertube_small
import raccoonforfriendica.shared.generated.resources.person_add
import raccoonforfriendica.shared.generated.resources.pixelfed_small
import raccoonforfriendica.shared.generated.resources.play_arrow
import raccoonforfriendica.shared.generated.resources.play_circle
import raccoonforfriendica.shared.generated.resources.pleroma_small
import raccoonforfriendica.shared.generated.resources.post_add
import raccoonforfriendica.shared.generated.resources.public
import raccoonforfriendica.shared.generated.resources.radio_button_checked
import raccoonforfriendica.shared.generated.resources.repeat
import raccoonforfriendica.shared.generated.resources.reply
import raccoonforfriendica.shared.generated.resources.rocket_launch
import raccoonforfriendica.shared.generated.resources.rocket_launch_fill
import raccoonforfriendica.shared.generated.resources.save
import raccoonforfriendica.shared.generated.resources.schedule
import raccoonforfriendica.shared.generated.resources.schedule_send
import raccoonforfriendica.shared.generated.resources.search
import raccoonforfriendica.shared.generated.resources.send
import raccoonforfriendica.shared.generated.resources.settings_fill
import raccoonforfriendica.shared.generated.resources.share
import raccoonforfriendica.shared.generated.resources.shield
import raccoonforfriendica.shared.generated.resources.strikethrough_s
import raccoonforfriendica.shared.generated.resources.style_fill
import raccoonforfriendica.shared.generated.resources.stylus_fountain_pen_fill
import raccoonforfriendica.shared.generated.resources.support
import raccoonforfriendica.shared.generated.resources.tag
import raccoonforfriendica.shared.generated.resources.thumb_down
import raccoonforfriendica.shared.generated.resources.thumb_down_fill
import raccoonforfriendica.shared.generated.resources.thumb_up
import raccoonforfriendica.shared.generated.resources.thumb_up_fill
import raccoonforfriendica.shared.generated.resources.update
import raccoonforfriendica.shared.generated.resources.verified
import raccoonforfriendica.shared.generated.resources.view_agenda
import raccoonforfriendica.shared.generated.resources.visibility
import raccoonforfriendica.shared.generated.resources.visibility_off
import raccoonforfriendica.shared.generated.resources.volunteer_activism
import raccoonforfriendica.shared.generated.resources.wordpress_small
import raccoonforfriendica.shared.generated.resources.workspaces_fill

internal class SharedResources : CoreResources {
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
    // endregion

    // region Media player config
    override val videoPlayerConfig: VideoPlayerConfig =
        VideoPlayerConfig(isFullScreenEnabled = false)

    override val audioPlayerConfig: AudioPlayerConfig =
        AudioPlayerConfig(controlsBottomPadding = Spacing.s)
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
