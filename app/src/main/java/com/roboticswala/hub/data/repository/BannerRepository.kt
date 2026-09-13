package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.AchievementBannerItem
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    fun observeBanners(): Flow<List<AchievementBannerItem>>
    fun addBanner(banner: AchievementBannerItem): Flow<Resource<Unit>>
    fun updateBanner(banner: AchievementBannerItem): Flow<Resource<Unit>>
    fun deleteBanner(bannerId: String): Flow<Resource<Unit>>
}
