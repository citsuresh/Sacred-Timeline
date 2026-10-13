package com.suresh.sacredtimeline.logic

object CityDatabase {
    private val cities = mapOf(
        "chennai" to Pair(13.0827, 80.2707),
        "coimbatore" to Pair(11.0168, 76.9558),
        "madurai" to Pair(9.9252, 78.1198),
        "tiruchirappalli" to Pair(10.7905, 78.7047),
        "trichy" to Pair(10.7905, 78.7047),
        "salem" to Pair(11.6643, 78.1460),
        "tirunelveli" to Pair(8.7139, 77.7567),
        "erode" to Pair(11.3410, 77.7172),
        "vellore" to Pair(12.9165, 79.1325),
        "thanjavur" to Pair(10.7870, 79.1378),
        "bangalore" to Pair(12.9716, 77.5946),
        "bengaluru" to Pair(12.9716, 77.5946),
        "mumbai" to Pair(19.0760, 72.8777),
        "delhi" to Pair(28.6139, 77.2090),
        "hyderabad" to Pair(17.3850, 78.4867)
    )

    val allCities: Map<String, Pair<Double, Double>> get() = cities

    fun lookupExact(cityName: String): Pair<Double, Double>? {
        val key = cityName.lowercase().trim()
        return cities[key]
    }

    fun lookupPartial(cityName: String): List<String> {
        val key = cityName.lowercase().trim()
        if (key.length < 2) return emptyList()
        return cities.keys.filter { it.contains(key) }.map { cityKey ->
            cityKey.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }
}
