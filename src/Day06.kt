import kotlin.math.min

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

    fun simulateDP(init: List<Int>, days: Int): Long {
        var daysVals = MutableList<Long>(days + 1) { 0 }

        // Initialize the days values with one full generation
        var initList = init
        for (day in 0..min(days, 9)) {
            daysVals[day] = initList.count().toLong()
            val zeros = initList.count { it == 0 }
            initList = initList.map { if ((it - 1) >= 0) it - 1 else 6 } + List<Int>(zeros) { 8 }
        }

        // Run DP with the following function: day[n] = day[n-7] + day[n-9]
        for (day in 10..days) {
            daysVals[day] = daysVals[day - 7] + daysVals[day - 9]
        }

        return daysVals[days]
    }

    fun part1(input: List<String>): Long {
        var chars = input.first().split(",").map { it.toInt() }
        return simulateDP(chars, 80)
    }

    fun part2(input: List<String>): Long {
        var chars = input.first().split(",").map { it.toInt() }
        return simulateDP(chars, 256)
    }

    val testInput = readInput("Day06_test")

    check2(part1(testInput), 5934)
    check2(part2(testInput), 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
