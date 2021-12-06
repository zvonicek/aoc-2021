fun main() {
    var cache: MutableMap<Int, Long> = mutableMapOf()

    fun simulate(timer: Int, days: Int): Long {
        return if (days == 0) {
            1
        } else if (timer == 0) {
            return if (cache[days] != null) {
                cache[days]!!
            } else {
                val res = simulate(6, days - 1) + simulate(8, days - 1)
                cache[days] = res
                res
            }
        } else {
            simulate(timer - 1, days - 1)
        }
    }

    fun part1(input: List<String>): Long {
        var chars = input.first().split(",").map { it.toInt() }
        return chars.fold(0) { acc, i -> acc + simulate(i, 80) }
    }

    fun part2(input: List<String>): Long {
        var chars = input.first().split(",").map { it.toInt() }
        return chars.fold(0) { acc, i -> acc + simulate(i, 256) }
    }

    val testInput = readInput("Day06_test")

    check2(part1(testInput), 5934)
    check2(part2(testInput), 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
