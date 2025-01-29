package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface OptionId {
    data object Add : OptionId

    data object Edit : OptionId

    data object Delete : OptionId

    data object Share : OptionId

    data object CopyUrl : OptionId

    data object Mute : OptionId

    data object Unmute : OptionId

    data object Block : OptionId

    data object Unblock : OptionId

    data object Pin : OptionId

    data object Unpin : OptionId

    data object ReportUser : OptionId

    data object ReportEntry : OptionId

    data object ViewDetails : OptionId

    data object Quote : OptionId

    data object CopyToClipboard : OptionId

    data object Translate : OptionId

    interface Custom : OptionId
}

@Composable
private fun OptionId.toReadableName(): String =
    when (this) {
        OptionId.Add -> LocalStrings.current.actionAddNew
        OptionId.Edit -> LocalStrings.current.actionEdit
        OptionId.Delete -> LocalStrings.current.actionDelete
        OptionId.Share -> LocalStrings.current.actionShare
        OptionId.CopyUrl -> LocalStrings.current.actionCopyUrl
        OptionId.Mute -> LocalStrings.current.actionMute
        OptionId.Unmute -> LocalStrings.current.actionUnmute
        OptionId.Block -> LocalStrings.current.actionBlock
        OptionId.Unblock -> LocalStrings.current.actionUnblock
        OptionId.Pin -> LocalStrings.current.actionPin
        OptionId.Unpin -> LocalStrings.current.actionUnpin
        OptionId.ReportUser -> LocalStrings.current.actionReportUser
        OptionId.ReportEntry -> LocalStrings.current.actionReportEntry
        OptionId.ViewDetails -> LocalStrings.current.actionViewDetails
        OptionId.Quote -> LocalStrings.current.actionQuote
        OptionId.CopyToClipboard -> LocalStrings.current.actionCopyToClipboard
        else -> ""
    }

@Composable
fun OptionId.toOption() = Option(id = this, label = toReadableName())

fun OptionId.Custom.toOption(label: String) = Option(id = this, label = label)

data class Option(
    val id: OptionId,
    val label: String,
)
