package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.roboticswala.hub.data.models.AchievementBannerItem
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreBannerRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : BannerRepository {

    private val bannersCollection = firestore.collection("banners")

    override fun observeBanners(): Flow<List<AchievementBannerItem>> = callbackFlow {
        val listener = bannersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val banners = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AchievementBannerItem::class.java)?.copy(id = doc.id)
                }
                trySend(banners)
            }
        }
        awaitClose { listener.remove() }
    }

    override fun addBanner(banner: AchievementBannerItem): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val newBannerRef = bannersCollection.document()
            val bannerWithId = banner.copy(id = newBannerRef.id)
            newBannerRef.set(bannerWithId).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add banner"))
        }
    }

    override fun updateBanner(banner: AchievementBannerItem): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            if (banner.id.isBlank()) {
                emit(Resource.Error("Banner ID is missing"))
                return@flow
            }
            bannersCollection.document(banner.id).set(banner).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update banner"))
        }
    }

    override fun deleteBanner(bannerId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            bannersCollection.document(bannerId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete banner"))
        }
    }
}
