fun getOptions(board: List<List<Char>>, pos: Pair<Int, Int>): List<Pair<Int, Int>> {
    return listOf(
        Pair(pos.first - 1, pos.second),
        Pair(pos.first, pos.second - 1),
        Pair(pos.first, pos.second + 1),
        Pair(pos.first + 1, pos.second)
    ).filter {it.first in board.indices && it.second in board[it.first].indices}
}

fun wordSearch(board: List<List<Char>>, word: String): Boolean {
    if (word.isEmpty()) {
        return true
    }
    if (board.isEmpty()) {
        throw IllegalArgumentException("The board should not be empty")
    }
    if (!board.all {it.size == board[0].size}) {
       throw IllegalArgumentException("The board should be rectangular")
    }

    // initially populate our leads var with starting points.
    // `leads` holds alternate paths to try that haven't been traversed yet
    val leads: MutableList<List<Pair<Int, Int>>> = mutableListOf()
    for ((i, row) in board.withIndex()) {
        for ((j, char) in row.withIndex()) {
            if (char == word[0]) {
                leads.add(mutableListOf(Pair(i, j)))
            }
        }
    }

    if (leads.isEmpty()) {
        return false
    }

    // pop off a position from leads arbitrarily and start there
    val start = leads.last()[0]
    leads.removeAt(leads.size - 1)
    var currPath = mutableListOf(start)
    var visited = mutableSetOf(start)
    var foundWord = false

    while (true) {
        val currPos = currPath.last()
        // `options` are coords we can go to next
        var options = listOf<Pair<Int, Int>>()
        visited.add(currPos)

        if (currPath.size < word.length) {
            // get list of options and filter them to places where we can find the next letter
            options = getOptions(board, currPos).filter {
                !visited.contains(it) && board[it.first][it.second] == word[currPath.size]
            }
        }

        // check to see if we have found the word yet
        if (word == currPath.map {board[it.first][it.second]}.joinToString("")) {
            foundWord = true
            break
        }
        if (options.isEmpty()) {
            // no options to go to next, so either give up if no leads or follow a new lead
            if (leads.isEmpty()) {
                break
            }
            // resetting state for following new lead / path
            currPath = leads.last().toMutableList()
            visited = currPath.toMutableSet()
            leads.removeAt(leads.size - 1)
        } else {
            // there are options, so choose one to follow and store the rest as leads
            for (optionPair in options.subList(0, options.size - 1)) {
                leads.add(currPath + mutableListOf(optionPair))
            }
            currPath.add(options.last())
        }
    }
    return foundWord
}

fun main(args: Array<String>) {
    val board = listOf(
        listOf('A', 'B', 'C', 'E'),
        listOf('S', 'F', 'C', 'S'),
        listOf('A', 'D', 'E', 'E')
    )
    println(wordSearch(board, "ABCCED"))  // true
    println(wordSearch(board, "SEE"))  // true
    println(wordSearch(board, "ABCB"))  // false
}
