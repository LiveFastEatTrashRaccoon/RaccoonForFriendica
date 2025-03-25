package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail

import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.download
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDetailViewModel(
    private val urls: List<String>,
    private val initialIndex: Int = 0,
    private val shareHelper: ShareHelper,
    private val galleryHelper: GalleryHelper,
    private val imagePreloadManager: ImagePreloadManager,
) : DefaultMviModel<ImageDetailMviModel.Intent, ImageDetailMviModel.UiState, ImageDetailMviModel.Effect>(
        initialState = ImageDetailMviModel.UiState(),
    ),
    ImageDetailMviModel {
    init {
        screenModelScope.launch {
            updateState {
                it.copy(
                    currentIndex = initialIndex,
                )
            }
        }
    }

    override fun reduce(intent: ImageDetailMviModel.Intent) {
        when (intent) {
            is ImageDetailMviModel.Intent.ChangeIndex ->
                screenModelScope.launch {
                    updateState { it.copy(currentIndex = intent.index) }
                }
            is ImageDetailMviModel.Intent.ChangeContentScale -> changeContentScale(intent.contentScale)
            ImageDetailMviModel.Intent.SaveToGallery -> downloadAndSave()
            ImageDetailMviModel.Intent.ShareAsUrl -> shareAsUrl()
            ImageDetailMviModel.Intent.ShareAsFile -> shareAsFile()
        }
    }

    private fun changeContentScale(contentScale: ContentScale) {
        val currentState = uiState.value
        val url = urls[currentState.currentIndex]
        imagePreloadManager.remove(url)
        screenModelScope.launch {
            updateState {
                it.copy(contentScale = contentScale)
            }
        }
    }

    private fun downloadAndSave() {
        check(!uiState.value.loading) { return }
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            val currentState = uiState.value
            val url = urls[currentState.currentIndex]
            try {
                val bytes = galleryHelper.download(url)
                val extension = url.extractExtension()
                withContext(Dispatchers.IO) {
                    galleryHelper.saveToGallery(
                        bytes = bytes,
                        name = "${epochMillis()}$extension",
                    )
                }
                updateState { it.copy(loading = false) }
                emitEffect(ImageDetailMviModel.Effect.ShareSuccess)
            } catch (e: Throwable) {
                e.printStackTrace()
                updateState { it.copy(loading = false) }
                emitEffect(ImageDetailMviModel.Effect.ShareFailure)
            }
        }
    }

    private fun shareAsUrl() {
        val currentState = uiState.value
        val url = urls[currentState.currentIndex]
        runCatching {
            shareHelper.share(url)
        }
    }

    private fun shareAsFile() {
        check(!uiState.value.loading) { return }
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            val currentState = uiState.value
            val url = urls[currentState.currentIndex]
            try {
                val bytes = galleryHelper.download(url)
                val extension = url.extractExtension()
                val path =
                    withContext(Dispatchers.IO) {
                        galleryHelper.saveToGallery(
                            bytes = bytes,
                            name = "${epochMillis()}$extension",
                        )
                    }
                updateState { it.copy(loading = false) }

                if (path != null) {
                    shareHelper.shareImage(path)
                } else {
                    emitEffect(ImageDetailMviModel.Effect.ShareFailure)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                updateState { it.copy(loading = false) }
                emitEffect(ImageDetailMviModel.Effect.ShareFailure)
            }
        }
    }
}

private fun String.extractExtension(): String =
    let { s ->
        val idx = s.lastIndexOf(".").takeIf { it >= 0 } ?: s.length
        s.substring(idx).takeIf { it.isNotEmpty() } ?: ".jpeg"
    }
