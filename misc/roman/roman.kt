val romans = mapOf(
    1000 to "M",
    900 to "CM",
    500 to "D",
    400 to "CD",
    100 to "C",
    90 to "XC",
    50 to "L",
    40 to "XL",
    10 to "X",
    9 to "IX",
    5 to "V",
    4 to "IV",
    1 to "I"
).toSortedMap(Comparator { a, b -> b - a})

fun Int.toRoman(): String {
    var remainder = this
    val ret = mutableListOf<String>()

    while (remainder != 0) {
        romans.toList().find { p -> p.first <= remainder }?.let { largest ->
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