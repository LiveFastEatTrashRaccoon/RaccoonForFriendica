package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultSupportedFeatureRepositoryTest {
    private val nodeInfoRepository = mock<NodeInfoRepository>()
    private val sut = DefaultSupportedFeatureRepository(nodeInfoRepository)

    @Test
    fun `when initial then result is as expected`() = runTest {
        val expected = NodeFeatures()
        val res = sut.features.value

        assertEquals(expected, res)
        verifySuspend(VerifyMode.not) {
            nodeInfoRepository.getInfo()
        }
    }

    @Test
    fun `given Mastodon when refresh then result is as expected`() = runTest {
        everySuspend { nodeInfoRepository.getInfo() } returns NodeInfoModel(version = "2.8")
        sut.refresh()
        val res = sut.features.value

        assertFalse(res.supportsPhotoGallery)
        assertFalse(res.supportsDirectMessages)
        assertFalse(res.supportsEntryTitles)
        assertFalse(res.supportsCustomCircles)
        assertTrue(res.supportReportCategoryRuleViolation)
        assertTrue(res.supportsPolls)
        assertFalse(res.supportsBBCode)
        assertTrue(res.supportsMarkdown)
        assertFalse(res.supportsEntryShare)
        assertFalse(res.supportsCalendar)
        assertTrue(res.supportsAnnouncements)
        assertFalse(res.supportsDislike)
        assertTrue(res.supportsTranslation)
        assertFalse(res.supportsLocalVisibility)
        verifySuspend {
            nodeInfoRepository.getInfo()
        }
    }

    @Test
    fun `given Friendica when refresh then result is as expected`() = runTest {
        everySuspend { nodeInfoRepository.getInfo() } returns
            NodeInfoModel(
                version = "2.8.0 (compatible; Friendica 2024.09)",
                software = "friendica",
            )
        sut.refresh()
        val res = sut.features.value

        assertTrue(res.supportsPhotoGallery)
        assertTrue(res.supportsDirectMessages)
        assertTrue(res.supportsEntryTitles)
        assertTrue(res.supportsCustomCircles)
        assertFalse(res.supportReportCategoryRuleViolation)
        assertFalse(res.supportsPolls)
        assertTrue(res.supportsBBCode)
        assertTrue(res.supportsMarkdown)
        assertTrue(res.supportsEntryShare)
        assertTrue(res.supportsCalendar)
        assertFalse(res.supportsAnnouncements)
        assertTrue(res.supportsDislike)
        assertFalse(res.supportsTranslation)
        assertFalse(res.supportsLocalVisibility)
        verifySuspend {
            nodeInfoRepository.getInfo()
        }
    }

    @Test
    fun `given Friendica RC when refresh then result is as expected`() = runTest {
        everySuspend { nodeInfoRepository.getInfo() } returns
            NodeInfoModel(
                version = "2.8.0 (compatible; Friendica 2024.09-rc)",
                software = "friendica",
            )
        sut.refresh()
        val res = sut.features.value

        assertTrue(res.supportsPhotoGallery)
        assertTrue(res.supportsDirectMessages)
        assertTrue(res.supportsEntryTitles)
        assertTrue(res.supportsCustomCircles)
        assertFalse(res.supportReportCategoryRuleViolation)
        assertFalse(res.supportsPolls)
        assertTrue(res.supportsBBCode)
        assertTrue(res.supportsMarkdown)
        assertTrue(res.supportsEntryShare)
        assertTrue(res.supportsCalendar)
        assertFalse(res.supportsAnnouncements)
        assertTrue(res.supportsDislike)
        assertFalse(res.supportsTranslation)
        assertFalse(res.supportsLocalVisibility)
        verifySuspend {
            nodeInfoRepository.getInfo()
        }
    }

    @Test
    fun `given GoToSocial when refresh then result is as expected`() = runTest {
        everySuspend { nodeInfoRepository.getInfo() } returns
            NodeInfoModel(
                version = "0.18.2-SNAPSHOT+git-4686217",
                software = "gotosocial",
            )
        sut.refresh()
        val res = sut.features.value

        assertFalse(res.supportsPhotoGallery)
        assertFalse(res.supportsDirectMessages)
        assertFalse(res.supportsEntryTitles)
        assertFalse(res.supportsCustomCircles)
        assertTrue(res.supportReportCategoryRuleViolation)
        assertTrue(res.supportsPolls)
        assertFalse(res.supportsBBCode)
        assertTrue(res.supportsMarkdown)
        assertFalse(res.supportsEntryShare)
        assertFalse(res.supportsCalendar)
        assertTrue(res.supportsAnnouncements)
        assertFalse(res.supportsDislike)
        assertTrue(res.supportsTranslation)
        assertTrue(res.supportsLocalVisibility)
        verifySuspend {
            nodeInfoRepository.getInfo()
        }
    }
}
