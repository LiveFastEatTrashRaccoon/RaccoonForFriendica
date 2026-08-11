package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal abstract class BasePaginationManager<T, S>(private val idSelector: (T) -> String) {
    private var specification: S? = null
    private var pageCursor: String? = null
    private var _canFetchMore: Boolean = true
    private val _history = mutableListOf<T>()
    private val historyIds = mutableSetOf<String>()
    private val mutex = Mutex()

    protected val currentSpecification: S? get() = specification
    protected val currentPageCursor: String? get() = pageCursor

    val canFetchMore: Boolean get() = _canFetchMore
    val history: List<T> get() = _history

    open suspend fun reset(specification: S) {
        mutex.withLock {
            this.specification = specification
            _history.clear()
            historyIds.clear()
            pageCursor = null
            _canFetchMore = true
        }
    }

    protected suspend fun <R> withPaginationLock(block: suspend () -> R): R = mutex.withLock { block() }

    protected fun updateHistoryItem(id: String, transform: (T) -> T) {
        val idx = _history.indexOfFirst { idSelector(it) == id }
        if (idx >= 0) {
            _history[idx] = transform(_history[idx])
        }
    }

    protected fun removeHistoryItem(id: String) {
        val idx = _history.indexOfFirst { idSelector(it) == id }
        if (idx >= 0) {
            _history.removeAt(idx)
            historyIds.remove(id)
        }
    }

    protected fun restorePaginationState(specification: S?, pageCursor: String?, history: List<T>) {
        this.specification = specification
        this.pageCursor = pageCursor
        _history.clear()
        historyIds.clear()
        _history.addAll(history)
        history.forEach { historyIds.add(idSelector(it)) }
        // We assume that if we are restoring history, we should enable fetching more
        // if the cursor is present, or just default to true if unsure.
        _canFetchMore = true
    }

    protected suspend fun updateHistory(
        results: ListWithPageCursor<T>?,
        distinctBy: (T) -> Any = idSelector,
        transform: suspend (List<T>) -> List<T> = { it },
    ): List<T> {
        val cursor = results?.cursor
        val rawItems = results?.list.orEmpty()

        return mutex.withLock {
            pageCursor = cursor
            _canFetchMore = cursor != null

            val newItems = rawItems
                .filter { idSelector(it) !in historyIds }
                .distinctBy(distinctBy)

            val processedItems = transform(newItems)

            _history.addAll(processedItems)
            processedItems.forEach { historyIds.add(idSelector(it)) }

            _history.toList()
        }
    }

    protected suspend fun updateHistory(
        items: List<T>?,
        distinctBy: (T) -> Any = idSelector,
        transform: suspend (List<T>) -> List<T> = { it },
    ): List<T> {
        val rawItems = items.orEmpty()
        return updateHistory(
            results = ListWithPageCursor(
                list = rawItems,
                cursor = rawItems.lastOrNull()?.let(idSelector),
            ),
            distinctBy = distinctBy,
            transform = transform,
        )
    }
}
