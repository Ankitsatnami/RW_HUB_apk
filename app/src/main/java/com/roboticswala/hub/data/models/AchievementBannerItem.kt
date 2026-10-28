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
    companion object {
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
