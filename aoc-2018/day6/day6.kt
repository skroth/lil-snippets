import java.io.File
import kotlin.math.abs

typealias Coord = Pair<Int, Int>

fun manDistance(coord1: Coord, coord2: Coord): Int {
    return abs(coord1.first - coord2.first) + abs(coord1.second - coord2.second)
}

fun closest(coord: Coord, board: List<Coord>): Coord? {
    val dists = board.map { Pair(it, manDistance(coord, it)) }.sortedBy { it.second }
    if (dists[0].second == dists[1].second) {
        // don't include if it is just as close to more than one point
        return null
    }
    return dists[0].first
}

fun isRegion(coord: Coord, board: List<Coord>): Boolean = board.map { manDistance(coord, it) }.sum() < 10000

fun main(args: Array<String>) {
    val points = File("day6.txt").readLines().map {
        it.split(", ")
    }.map { Pair(it[0].toInt(), it[1].toInt()) }

    val maxX = points.maxBy { it.first }?.first ?: 0
    val maxY = points.maxBy { it.second }?.second ?: 0
    val mostCommon = mutableMapOf<Coord, Int>()
    var regionSize = 0
    val grid = (0..maxX).map { x ->
        (0..maxY).map { y ->
            val coord = Pair(x, y)
            if (isRegion(coord, points)) {
                regionSize += 1
            }
            val closest = closest(coord, points)?.also {
                mostCommon[it] = mostCommon.getOrDefault(it, 0) + 1
            }
            closest
        }
    }

    // Remove the infinites. Any point that is considered closest on one of the edges
    // means that point extends off the grid for infinity. First check the
    // left and right edges, and then the top and bottom edges.
    grid.forEach {
        mostCommon.remove(it.first())
        mostCommon.remove(it.last())
    }
    grid.first().forEach { mostCommon.remove(it) }
    grid.last().forEach { mostCommon.remove(it) }

    println(mostCommon.maxBy { it.value })  // Part 1
    println(regionSize)  // Part 2
}