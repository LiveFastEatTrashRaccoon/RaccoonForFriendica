package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.autofill(contentTypes: List<ContentType>): Modifier {
    return this then Modifier.semantics {
        contentType = contentTypes.reduce { acc, item -> acc + item }
    }
}
