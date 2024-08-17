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
    private val url: String,
    private val shareHelper: ShareHelper,
    private val galleryHelper: GalleryHelper,
    private val imagePreloadManager: ImagePreloadManager,
) : DefaultMviModel<ImageDetailMviModel.Intent, ImageDetailMviModel.UiState, ImageDetailMviModel.Effect>(
        initialState = ImageDetailMviModel.UiState(),
    ),
    ImageDetailMviModel {
    override fun reduce(intent: ImageDetailMviModel.Intent) {
        when (intent) {
            is ImageDetailMviModel.Intent.ChangeContentScale -> changeContentScale(intent.contentScale)
            ImageDetailMviModel.Intent.SaveToGallery -> downloadAndSave()
            ImageDetailMviModel.Intent.ShareAsUrl -> shareAsUrl()
            ImageDetailMviModel.Intent.ShareAsFile -> shareAsFile()
        }
    }

    private fun changeContentScale(contentScale: ContentScale) {
        imagePreloadManager.remove(url)
        screenModelScope.launch {
            updateState {
                it.copy(contentScale = contentScale)
            }
        }
    }

    private fun downloadAndSave() {
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            try {
                val bytes = galleryHelper.download(url)
                val extension =
                    url.let { s ->
                        val idx = s.lastIndexOf(".").takeIf { it >= 0 } ?: s.length
                        s.substring(idx).takeIf { it.isNotEmpty() } ?: ".jpeg"
                    }
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
        runCatching {
            shareHelper.share(url)
        }
    }

    private fun shareAsFile() {
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            try {
                val bytes = galleryHelper.download(url)
                val extension =
                    url.let { s ->
                        val idx = s.lastIndexOf(".").takeIf { it >= 0 } ?: s.length
                        s.substring(idx).takeIf { it.isNotEmpty() } ?: ".jpeg"
                    }
                withContext(Dispatchers.IO) {
                    val path =
                        galleryHelper.saveToGallery(
                            bytes = bytes,
                            name = "${epochMillis()}$extension",
                        )

                    withContext(Dispatchers.Main) {
                        updateState { it.copy(loading = false) }

                        if (path != null) {
                            shareHelper.shareImage(path)
                        } else {
                            emitEffect(ImageDetailMviModel.Effect.ShareFailure)
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                updateState { it.copy(loading = false) }
                emitEffect(ImageDetailMviModel.Effect.ShareFailure)
            }
        }
    }
}
