package com.livefast.eattrash.raccoonforfriendica.feature.imagedetail

import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.epochMillis
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.GalleryHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.gallery.download
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

@AssistedInject
class ImageDetailViewModel(
    @Assisted private val urls: List<String>,
    @Assisted private val initialIndex: Int,
    private val shareHelper: ShareHelper,
    private val galleryHelper: GalleryHelper,
    private val imagePreloadManager: ImagePreloadManager,
) : ViewModel(),
    MviDelegate<ImageDetailMvi.Intent, ImageDetailMvi.UiState, ImageDetailMvi.Effect>
    by DefaultMviDelegate(initialState = ImageDetailMvi.UiState()),
    ImageDetailMvi {

    init {
        viewModelScope.launch {
            updateState {
                it.copy(
                    currentIndex = initialIndex,
                )
            }
        }
    }

    override fun reduce(intent: ImageDetailMvi.Intent) {
        when (intent) {
            is ImageDetailMvi.Intent.ChangeIndex -> handleChangeIndex(intent.index)
            is ImageDetailMvi.Intent.ChangeContentScale -> handleChangeContentScale(intent.contentScale)
            ImageDetailMvi.Intent.SaveToGallery -> handleSaveToGallery()
            ImageDetailMvi.Intent.ShareAsUrl -> handleShareAsUrl()
            ImageDetailMvi.Intent.ShareAsFile -> handleShareAsFile()
        }
    }

    private fun handleChangeIndex(index: Int) {
        viewModelScope.launch {
            updateState { it.copy(currentIndex = index) }
        }
    }

    private fun handleChangeContentScale(contentScale: ContentScale) {
        val currentState = uiState.value
        val url = urls[currentState.currentIndex]
        imagePreloadManager.remove(url)
        viewModelScope.launch {
            updateState {
                it.copy(contentScale = contentScale)
            }
        }
    }

    private fun handleSaveToGallery() {
        if (uiState.value.loading) {
            return
        }
        viewModelScope.launch {
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
                emitEffect(ImageDetailMvi.Effect.ShareSuccess)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                updateState { it.copy(loading = false) }
                emitEffect(ImageDetailMvi.Effect.ShareFailure)
            }
        }
    }

    private fun handleShareAsUrl() {
        val currentState = uiState.value
        val url = urls[currentState.currentIndex]
        try {
            shareHelper.share(url)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    private fun handleShareAsFile() {
        if (uiState.value.loading) {
            return
        }
        viewModelScope.launch {
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
                    emitEffect(ImageDetailMvi.Effect.ShareFailure)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                updateState { it.copy(loading = false) }
                emitEffect(ImageDetailMvi.Effect.ShareFailure)
            }
        }
    }

    companion object {
        fun getExtras(urls: List<String>, initialIndex: Int) = CreationExtras {
            this[KEY_URLS] = urls
            this[KEY_INITIAL_INDEX] = initialIndex
        }
    }
}

private val KEY_URLS = CreationExtras.Key<List<String>>()
private val KEY_INITIAL_INDEX = CreationExtras.Key<Int>()

private fun String.extractExtension(): String {
    val idx = lastIndexOf(".").takeIf { it >= 0 } ?: length
    return substring(idx).takeIf { it.isNotEmpty() } ?: ".jpeg"
}

@AssistedFactory
@ViewModelAssistedFactoryKey(ImageDetailViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface ImageDetailViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ImageDetailViewModel = create(
        extras[KEY_URLS] ?: error("urls argument not found"),
        extras[KEY_INITIAL_INDEX] ?: error("initialIndex argument not found"),
    )

    fun create(@Assisted urls: List<String>, @Assisted initialIndex: Int): ImageDetailViewModel
}
