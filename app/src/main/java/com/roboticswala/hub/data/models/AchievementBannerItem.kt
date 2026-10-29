package com.roboticswala.hub.data.models

data class AchievementBannerItem(
    val id: String = "",
    val title: String = "",
    val badge: String = "",
    val category: String = "",
    val team: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val description: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "badge" to badge,
        "category" to category,
        "team" to team,
        "date" to date,
        "imageUrl" to imageUrl,
        "description" to description,
        "updatedAt" to System.currentTimeMillis()
    )

    companion object {
        fun fromSnapshot(doc: com.google.firebase.firestore.DocumentSnapshot): AchievementBannerItem {
            val data = doc.data ?: emptyMap<String, Any?>()
            return fromMap(doc.id, data)
        }

        fun fromMap(id: String, map: Map<String, Any?>): AchievementBannerItem {
            return AchievementBannerItem(
                id = id.ifBlank { map["id"] as? String ?: "" },
                title = map["title"] as? String ?: "",
                badge = map["badge"] as? String ?: "",
                category = map["category"] as? String ?: "",
                team = map["team"] as? String ?: "",
                date = map["date"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String ?: "",
                description = map["description"] as? String ?: ""
            )
        }

        val DEFAULT_BANNERS = listOf(
            AchievementBannerItem(
                id = "default_1",
                title = "National Robotics Championship 2026",
                badge = "1st Place Gold",
                category = "Combat Robotics",
                team = "Team Titan RW",
                date = "2026-03-15",
                imageUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?auto=format&fit=crop&w=800&q=80",
                description = "Secured national champions trophy in the heavyweight combat robotics arena."
            ),
            AchievementBannerItem(
                id = "default_2",
                title = "Autonomous AI Drone Navigation",
                badge = "Excellence Award",
                category = "Aerial Robotics",
                team = "AeroBots Lab",
                date = "2026-02-28",
                imageUrl = "https://images.unsplash.com/photo-1508614589041-895b88991e3e?auto=format&fit=crop&w=800&q=80",
                description = "Complete GPS-denied obstacle detection using edge AI vision."
            ),
            AchievementBannerItem(
                id = "default_3",
                title = "Robotic Arm Computer Vision Sorting",
                badge = "Innovation Trophy",
                category = "Industrial Automation",
                team = "RW Cybernetics",
                date = "2026-01-20",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
                description = "High precision 6-axis inverse kinematics sorting cell."
            )
        )
    }
}

