val romans = listOf(
    Pair(1000, "M"),
    Pair(900, "CM"),
    Pair(500, "D"),
    Pair(400, "CD"),
    Pair(100, "C"),
    Pair(90, "XC"),
    Pair(50, "L"),
    Pair(40, "XL"),
    Pair(10, "X"),
    Pair(9, "IX"),
    Pair(5, "V"),
    Pair(4, "IV"),
    Pair(1, "I")
)

fun Int.toRoman(): String {
    var remainder = this
    val ret = mutableListOf<String>()

    while (remainder != 0) {
        romans.find { p -> p.first <= remainder }?.let { largest ->
            ret.add(largest.second)
            remainder -= largest.first
        }
    }
    return ret.joinToString("")
}

fun main(args: Array<String>) {
    println(3.toRoman() == "III")
    println(4.toRoman() == "IV")
    println(9.toRoman() == "IX")
    println(58.toRoman() == "LVIII")
    println(1994.toRoman() == "MCMXCIV")
}