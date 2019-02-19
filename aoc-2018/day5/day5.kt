import java.io.File
import kotlin.system.measureTimeMillis

fun isPair(c1: Char, c2: Char): Boolean {
    return c1.isUpperCase() != c2.isUpperCase() && c1.toUpperCase() == c2.toUpperCase()
}

// Slower solution
fun part1(polymer: MutableList<Char>) {
    var didWork = true  // if we do a whole loop without doing work, we know we're done
    while (didWork) {
        var idx = 0
        didWork = false
        while (idx < polymer.size - 1) {
            val thisChar = polymer[idx]
            val nextChar = polymer[idx + 1]

            if (isPair(thisChar, nextChar)) {
                didWork = true
                polymer.removeAt(idx)
                polymer.removeAt(idx)
            } else {
                idx += 1
            }
        }
    }
    println(polymer.size)
}

// Faster solution
fun part12(polymer: MutableList<Char>, filterOut: Char? = null): Int {
    return if (filterOut == null) {
        polymer
    } else {
        polymer.filter { c -> c.toUpperCase() != filterOut.toUpperCase() }
    }.fold(mutableListOf<Char>()) { acc, c ->
        if (acc.size > 0 && isPair(acc.last(), c)) {
            acc.removeAt(acc.lastIndex)
        } else {
            acc.add(c)
        }
        acc
    }.size
}

/*
    rough python version of the above:
    >>> is_pair = lambda x, y: x.isupper() != y.isupper() and x.upper() == y.upper()
    >>> def reducer(acc, x):
    ...  if len(acc) and is_pair(acc[-1], x):
    ...   acc.pop()
    ...  else:
    ...   acc.append(x)
    ...  return acc
    ...
    >>> reduce(reducer, l, [])
 */

fun main(args: Array<String>) {
    val lines = File("inputday5.txt").readText().trim().toMutableList()

    assert(isPair('c', 'C'))
    assert(isPair('B', 'b'))
    assert(!isPair('B', 'B'))
    assert(!isPair('b', 'b'))
    assert(!isPair('b', 'C'))
    assert(!isPair('A', 'T'))

    println(measureTimeMillis {
        part1(lines.toMutableList())
    })
    println(measureTimeMillis {
        part12(lines.toMutableList())
    })

    println(('A'..'Z').map { l ->
        part12(lines.toMutableList(), l)
    }.min())  // Answer to part 2
}

