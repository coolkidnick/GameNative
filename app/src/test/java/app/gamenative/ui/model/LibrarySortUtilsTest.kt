package app.gamenative.ui.model

import app.gamenative.ui.enums.SortOption
import org.junit.Assert.assertEquals
import org.junit.Test

class LibrarySortUtilsTest {

    private data class Entry(
        val name: String,
        val isInstalled: Boolean,
        val lastPlayed: Long,
    )

    @Test
    fun recentlyPlayedComparator_keepsInstalledFirstAndSortsEachGroupByLastPlayed() {
        val entries = listOf(
            Entry(name = "Uninstalled Recent", isInstalled = false, lastPlayed = 9000L),
            Entry(name = "Installed Older", isInstalled = true, lastPlayed = 1000L),
            Entry(name = "Installed Recent", isInstalled = true, lastPlayed = 5000L),
            Entry(name = "Uninstalled Older", isInstalled = false, lastPlayed = 3000L),
        )

        val sorted = entries.sortedWith(
            LibrarySortUtils.recentlyPlayedComparator(
                name = Entry::name,
                isInstalled = Entry::isInstalled,
                lastPlayed = Entry::lastPlayed,
            ),
        )

        assertEquals(
            listOf("Installed Recent", "Installed Older", "Uninstalled Recent", "Uninstalled Older"),
            sorted.map { it.name },
        )
    }

    @Test
    fun recentlyPlayedComparator_usesNameFallbackWhenLastPlayedMatches() {
        val entries = listOf(
            Entry(name = "Beta", isInstalled = false, lastPlayed = 0L),
            Entry(name = "alpha", isInstalled = false, lastPlayed = 0L),
            Entry(name = "Charlie", isInstalled = false, lastPlayed = 0L),
        )

        val sorted = entries.sortedWith(
            LibrarySortUtils.recentlyPlayedComparator(
                name = Entry::name,
                isInstalled = Entry::isInstalled,
                lastPlayed = Entry::lastPlayed,
            ),
        )

        assertEquals(listOf("alpha", "Beta", "Charlie"), sorted.map { it.name })
    }

    @Test
    fun resolveLibrarySortOption_usesRecentlyPlayedWhenNothingIsSaved() {
        assertEquals(
            SortOption.RECENTLY_PLAYED,
            LibrarySortUtils.resolveLibrarySortOption(savedKey = "", legacyOrdinal = null),
        )
    }

    @Test
    fun resolveLibrarySortOption_keepsASavedKeyOrLegacyOrdinal() {
        assertEquals(
            SortOption.NAME_ASC,
            LibrarySortUtils.resolveLibrarySortOption(savedKey = "name_asc", legacyOrdinal = null),
        )
        assertEquals(
            SortOption.NAME_DESC,
            LibrarySortUtils.resolveLibrarySortOption(savedKey = "", legacyOrdinal = SortOption.NAME_DESC.ordinal),
        )
    }

    @Test
    fun openingLibraryIndex_skipsAFeaturedOrSuggestedCard() {
        val featured = listOf(true, false, false)
        assertEquals(1, LibrarySortUtils.openingLibraryIndex(featured.size) { featured[it] })
        assertEquals(0, LibrarySortUtils.openingLibraryIndex(3) { false })
        assertEquals(0, LibrarySortUtils.openingLibraryIndex(0) { true })
    }
}
