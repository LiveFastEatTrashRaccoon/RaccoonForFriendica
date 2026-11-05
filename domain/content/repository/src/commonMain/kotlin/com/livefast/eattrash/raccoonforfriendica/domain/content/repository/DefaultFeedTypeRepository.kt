package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.FeedTypeDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.FeedTypeEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FeedTypeModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultFeedTypeRepository(private val dao: FeedTypeDao) : FeedTypeRepository {

    override suspend fun getAll(accountId: Long): List<FeedTypeModel> = dao.getAll(accountId).map { it.toModel() }

    override fun getAllAsFlow(accountId: Long): Flow<List<FeedTypeModel>> =
        dao.getAllAsFlow(accountId).map { it.map { entity -> entity.toModel() } }

    override suspend fun getBy(accountId: Long, type: Int, info: String?): FeedTypeModel? {
        val entity = if (info == null) {
            dao.getBy(accountId = accountId, type = type)
        } else {
            dao.getBy(accountId = accountId, type = type, info = info)
        }
        return entity?.toModel()
    }

    override suspend fun create(item: FeedTypeModel) {
        dao.insert(item.toEntity())
    }

    override suspend fun create(items: List<FeedTypeModel>) {
        val entities = items.map { it.toEntity() }.toTypedArray()
        dao.insertMany(*entities)
    }

    override suspend fun update(item: FeedTypeModel) {
        dao.update(item.toEntity())
    }

    override suspend fun update(items: List<FeedTypeModel>) {
        val entities = items.map { it.toEntity() }.toTypedArray()
        dao.updateMany(*entities)
    }

    override suspend fun delete(item: FeedTypeModel) {
        dao.delete(item.toEntity())
    }

    private fun FeedTypeEntity.toModel() = FeedTypeModel(
        id = id,
        accountId = accountId,
        type = type.toTimelineType(),
        index = index,
        info = info,
    )

    private fun FeedTypeModel.toEntity() = FeedTypeEntity(
        id = id,
        accountId = accountId,
        type = type.toInt(),
        index = index,
        info = info,
    )
}
