package com.suresh.sacredtimeline.model

object Metadata {
    val PLANET_TAMIL_NAMES = mapOf(
        "Sun" to "சூரியன்",
        "Moon" to "சந்திரன்",
        "Mars" to "செவ்வாய்",
        "Mercury" to "புதன்",
        "Jupiter" to "குரு",
        "Venus" to "சுக்கிரன்",
        "Saturn" to "சனி"
    )

    val GOWRI_TAMIL_NAMES = mapOf(
        "AMRIDHA" to "அமிர்தம்",
        "UTHI" to "உத்தி",
        "LABAM" to "லாபம்",
        "DHANAM" to "தனம்",
        "SUGAM" to "சுகம்",
        "SORAM" to "சோரம்",
        "VISHAM" to "விஷம்",
        "ROGAM" to "ரோகம்"
    )

    val SPECIAL_TAMIL_NAMES = mapOf(
        "Nalla" to "நல்ல நேரம்",
        "Rahu" to "ராகு",
        "Yama" to "எமகண்டம்",
        "Kuli" to "குளிகை",
        "Kuli Dawn" to "குளிகை",
        "Kuli Dusk" to "குளிகை"
    )

    val GOWRI_DESCRIPTIONS = mapOf(
        "AMRIDHA" to "Extremely auspicious. Best for starting any work, travels, and new ventures.",
        "UTHI" to "Progressive and favorable. Good for growth, expansion, and administrative work.",
        "LABAM" to "Profitable. Ideal for business deals, financial transactions, and trade.",
        "DHANAM" to "Wealth creating. Favorable for investments and purchasing assets.",
        "SUGAM" to "Comfortable. Good for peace-related activities and domestic matters.",
        "SORAM" to "Theft/Loss risk. Avoid handling large cash or starting high-risk work.",
        "VISHAM" to "Poisonous/Toxic. Avoid medical treatments or starting new projects.",
        "ROGAM" to "Disease/Sickness. Inauspicious; avoid health-related activities or travel."
    )

    val SPECIAL_DESCRIPTIONS = mapOf(
        "Rahu" to "Inauspicious period. Avoid starting new ventures, major purchases, or important meetings.\n\nDo: Routine tasks, cleaning, spiritual practices.\nDon't: Travel, signing contracts, buying assets.",
        "Yama" to "Inauspicious period. Often associated with loss or obstacles. Avoid beginning anything new.\n\nDo: Repaying debts, finishing pending work, letting go.\nDon't: New projects, medical treatments, long journeys.",
        "Kuli" to "Expansion period. What is done during this time tends to repeat. Good for buying assets, not for bad deeds.\n\nDo: Charity, investments, starting positive habits.\nDon't: Incurring debt, funeral rituals, negative behaviors.",
        "Kuli Dawn" to "Expansion period. What is done during this time tends to repeat. Good for buying assets.\n\nDo: Charity, investments, starting positive habits.\nDon't: Incurring debt, funeral rituals, negative behaviors.",
        "Kuli Dusk" to "Expansion period. What is done during this time tends to repeat. Good for buying assets.\n\nDo: Charity, investments, starting positive habits.\nDon't: Incurring debt, funeral rituals, negative behaviors."
    )
    
    val PLANET_QUALITIES = mapOf(
        "Sun" to "Authority, Vitality, Leadership. Focus on administration and meetings with superiors.",
        "Moon" to "Emotion, Nurture, Change. Good for creative work and home-related matters.",
        "Mars" to "Energy, Action, Courage. Focus on physical tasks, but watch for conflicts.",
        "Mercury" to "Intellect, Communication, Trade. Ideal for study, writing, and business.",
        "Jupiter" to "Wisdom, Expansion, Luck. Best for learning, teaching, and rituals.",
        "Venus" to "Beauty, Harmony, Luxury. Good for arts, romance, and socializing.",
        "Saturn" to "Discipline, Delay, Hard work. Focus on routine tasks and clearing backlogs."
    )

    fun getHoraGuidance(planet: String, compatibility: HoraCompatibility): String {
        return when (planet) {
            "Sun" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "Highly auspicious for leadership and power. Best for meetings with authorities, starting new roles, and seeking government favors."
                HoraCompatibility.CONFLICTING -> "Conflicting solar energy. Ego clashes possible. Avoid stubbornness or confrontations with superiors. Stick to individual tasks."
                HoraCompatibility.NEUTRAL -> "Steady vitality. Good for administration and planning. Focus on your health and maintaining a disciplined schedule."
            }
            "Moon" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "Emotionally harmonious. Ideal for domestic work, creative arts, and connecting with family. Mind is calm and focused."
                HoraCompatibility.CONFLICTING -> "Fluctuating moods. Avoid making major decisions based on emotion. Not a good time for deep emotional conversations."
                HoraCompatibility.NEUTRAL -> "Gentle energy. Suitable for regular chores, cooking, and minor changes. Good for small social interactions."
            }
            "Mars" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "High energy and courage. Best for physical labor, sports, and competitive tasks. Bold actions bring success."
                HoraCompatibility.CONFLICTING -> "High friction. Risk of accidents or arguments. Avoid rushing, sharp tools, or aggressive negotiations. Be patient."
                HoraCompatibility.NEUTRAL -> "Active energy. Focus on technical work or home repairs. Keep physical activity moderate to avoid burnout."
            }
            "Mercury" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "Intellectually sharp. Excellent for business deals, writing, learning new skills, and all forms of communication."
                HoraCompatibility.CONFLICTING -> "Information overload. Communication errors likely. Double-check all emails and documents. Avoid starting new studies now."
                HoraCompatibility.NEUTRAL -> "Mental clarity. Good for organization, record-keeping, and catching up on correspondence. A practical time for logic."
            }
            "Jupiter" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "Highly auspicious. Best for starting new education, major financial investments, or spiritual ceremonies. Expansion is favored."
                HoraCompatibility.CONFLICTING -> "Conflicting energies. Venus (pleasure) and Jupiter (wisdom) may clash. Avoid over-indulgence or making impulsive luxury purchases."
                HoraCompatibility.NEUTRAL -> "A good time for learning and general growth. Focus on steady progress rather than big leaps."
            }
            "Venus" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "Harmonious and creative. Perfect for arts, social gatherings, buying jewelry/clothes, and romantic activities."
                HoraCompatibility.CONFLICTING -> "Emotional extravagance. Risk of overspending on luxuries. Aesthetic choices made now might be regretted later."
                HoraCompatibility.NEUTRAL -> "Pleasant atmosphere. Good for minor beautification and relaxing. Focus on building relationships gently."
            }
            "Saturn" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> "Solid foundation. Best for long-term planning, clearing debts, and tasks requiring intense discipline and endurance."
                HoraCompatibility.CONFLICTING -> "Heavy delays. Frustration likely with slow progress. Avoid starting anything that needs quick results. Rest is advised."
                HoraCompatibility.NEUTRAL -> "Pragmatic energy. Focus on routine work, cleaning, and organized labor. Persistence will pay off later."
            }
            else -> "A time for balanced action and mindfulness."
        }
    }

    fun getHoraStrategicActivities(planet: String, compatibility: HoraCompatibility): List<String> {
        return when (planet) {
            "Sun" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Meet with supervisors", "Apply for permits", "Lead a team project")
                HoraCompatibility.CONFLICTING -> listOf("Solo focused work", "Meditation", "Reviewing documents")
                HoraCompatibility.NEUTRAL -> listOf("Organize schedule", "Health checkup", "Administrative tasks")
            }
            "Moon" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Family dinner", "Creative writing", "Gardening")
                HoraCompatibility.CONFLICTING -> listOf("Routine chores", "Quiet reflection", "Light reading")
                HoraCompatibility.NEUTRAL -> listOf("Kitchen organization", "Planning social events", "Shopping for home")
            }
            "Mars" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Intense workout", "Starting construction", "Competitive sports")
                HoraCompatibility.CONFLICTING -> listOf("Safety audits", "Cleaning equipment", "Yoga")
                HoraCompatibility.NEUTRAL -> listOf("Home repairs", "Logical problem solving", "Technical drafting")
            }
            "Mercury" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Sign contracts", "Give a presentation", "Start a course")
                HoraCompatibility.CONFLICTING -> listOf("Proofread documents", "Data entry", "Cleaning desk")
                HoraCompatibility.NEUTRAL -> listOf("Archive emails", "Balance accounts", "Short distance travel")
            }
            "Jupiter" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Invest in stocks", "Spiritual worship", "Educational enrollment")
                HoraCompatibility.CONFLICTING -> listOf("Review budget", "Philosophical reading", "Internal audit")
                HoraCompatibility.NEUTRAL -> listOf("Consulting a mentor", "General research", "Long-term goal setting")
            }
            "Venus" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Art exhibition", "Date night", "Shopping for fashion")
                HoraCompatibility.CONFLICTING -> listOf("Financial planning", "Self-care", "Interior cleaning")
                HoraCompatibility.NEUTRAL -> listOf("Listening to music", "Flower arrangement", "Social networking")
            }
            "Saturn" -> when (compatibility) {
                HoraCompatibility.FAVORABLE -> listOf("Legacy planning", "Paying off loans", "Deep research")
                HoraCompatibility.CONFLICTING -> listOf("Routine maintenance", "Early sleep", "Filing taxes")
                HoraCompatibility.NEUTRAL -> listOf("Cleaning storage", "Manual labor", "Endurance training")
            }
            else -> listOf("Mindful breathing", "Planning", "Routine tasks")
        }
    }
}
