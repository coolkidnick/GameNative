package app.gamenative.ui.model

import app.gamenative.ui.enums.SortOption

internal object LibrarySortUtils {

    /**
     * Saved sort wins. With neither the current key nor the legacy ordinal stored,
     * open on installed games ordered by last played.
     */
    fun resolveLibrarySortOption(savedKey: String, legacyOrdinal: Int?): SortOption {
        if (savedKey.isNotEmpty()) return SortOption.fromKey(savedKey)
        if (legacyOrdinal != null) {
            @Suppress("DEPRECATION")
            return SortOption.fromOrdinal(legacyOrdinal)
        }
        return SortOption.RECENTLY_PLAYED
    }

    /** First real library game, skipping a featured or suggested card at the front. */
    fun openingLibraryIndex(count: Int, isFeaturedOrSuggested: (Int) -> Boolean): Int {
        if (count <= 0) return 0
        for (index in 0 until count) {
            if (!isFeaturedOrSuggested(index)) return index
        }
        return 0
    }

    fun <T> recentlyPlayedComparator(
        name: (T) -> String,
        isInstalled: (T) -> Boolean,
        lastPlayed: (T) -> Long,
    ): Comparator<T> {
        return compareBy<T> { entry ->
            if (isInstalled(entry)) 0 else 1
        }.thenByDescending { entry ->
            lastPlayed(entry)
        }.thenBy { entry ->
            name(entry).lowercase()
        }
    }
}
