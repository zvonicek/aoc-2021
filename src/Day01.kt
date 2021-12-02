fun main() {
    fun part1(input: List<String>): Int {
        var counter = 0
        for (index in input.indices) {
            if (index > 0 && input[index-1].toInt() < input[index].toInt()) {
                counter++
            }
        }

        return counter
    }

    fun part1b(input: List<String>): Int {
        return input.map { it.toInt() }.windowed(2).count { it[0] < it[1] }
    }

    fun part2(input: List<String>): Int {
        var prev = 0
        var counter = 0
        for (index in input.indices) {
            if (index < input.indices.last-2) {
                val sum = input[index].toInt() + input[index + 1].toInt() + input[index + 2].toInt()
                if (sum > prev) {
                    counter++
                }
                prev = sum
            }
        }

        return counter
    }

    fun part2b(input: List<String>): Int {
        return input.map { it.toInt() }.windowed(4).count { it[0]+it[1]+it[2] < it[1]+it[2]+it[3] }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check2(part1b(testInput), 7)
    check2(part2b(testInput), 5)

    val input = readInput("Day01")
    println(part1b(input))
    println(part2b(input))
}
