import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis

data class Claim(
    val id: Int,
    val pos: Pair<Int, Int>,
    val size: Pair<Int, Int>
) {
    fun iterPoints(fn: ((Int, Int) -> Unit)) {
        for (x in this.pos.first..(this.pos.first + this.size.first - 1)) {
            for (y in this.pos.second..(this.pos.second + this.size.second - 1)) {
                fn(x, y)
            }
        }
    }
}

fun day3() {
    val pat = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)")
    var maxHeight = 0
    var maxWidth = 0
    val claims: MutableList<Claim> = mutableListOf()

    File("input.txt").forEachLine {
        pat.matchEntire(it)?.let { match ->
            val groups = match.groupValues
            val claim = Claim(
                groups[1].toInt(),
                Pair(groups[2].toInt(), groups[3].toInt()),
                Pair(groups[4].toInt(), groups[5].toInt())
            )
            maxWidth = max(claim.pos.first + claim.size.first, maxWidth)
            maxHeight = max(claim.pos.second + claim.size.second, maxHeight)

            claims.add(claim)
        }
    }

    val board = MutableList(maxWidth+1) { MutableList(maxHeight+1) { 0 } }
    val overlapping: MutableSet<Pair<Int, Int>> = mutableSetOf()

    for (claim in claims) {
        claim.iterPoints { x, y ->
            board[x][y] += 1
            if (board[x][y] > 1) {
                overlapping.add(Pair(x, y))
            }
        }
    }
    println("${overlapping.size}")  // Answer to Part 1

    for (claim in claims) {
        var overlap = false
        claim.iterPoints { x, y ->
            overlap = overlap or (board[x][y] > 1)
        }
        if (!overlap) {
            println(claim.id)  // Answer to Part 2
        }
    }
}

fun main(args: Array<String>) {
    println(measureTimeMillis { day3() })
}
