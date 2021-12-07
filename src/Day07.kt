import kotlin.math.abs
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val values = input[0].split(",").map { it.toInt() }
        val maxVal = values.maxByOrNull { it }!!
        val minVal = values.minByOrNull { it }!!

        var minCost = Int.MAX_VALUE
        for (number in minVal..maxVal) {
            val cost = values.fold(0) { acc, i -> acc + abs(number-i) }
            minCost = min(cost, minCost)
        }

        return minCost
    }

    fun part2(input: List<String>): Int {
        val values = input[0].split(",").map { it.toInt() }
        val maxVal = values.maxByOrNull { it }!!
        val minVal = values.minByOrNull { it }!!

        var minCost = Int.MAX_VALUE
        for (number in minVal..maxVal) {
            val cost = values.fold(0) { acc, i ->
                val stepLength = abs(number-i)
                acc + stepLength * (stepLength + 1) / 2
            }
            minCost = min(cost, minCost)
        }

        return minCost
    }

    val testInput = readInput("Day07_test")

    check2(part1(testInput), 37)
    check2(part2(testInput), 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
