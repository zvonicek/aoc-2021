fun main() {
    fun part1(input: List<String>): Int {
        var horizontal: Int = 0
        var depth: Int = 0

        for (command in input) {
            val dir = command.split(" ")[0]
            val num = command.split(" ")[1].toInt()

            when (dir) {
                "forward" -> {
                    horizontal += num
                }
                "down" -> {
                    depth += num
                }
                "up" -> {
                    depth -= num
                }
            }
        }

        return horizontal * depth
    }

    fun part1b(input: List<String>): Int {
        data class Command(val horizontal: Int = 0, val depth: Int = 0)

        val res = input.map {
            val parts = it.split(" ")
            Pair(parts[0], parts[1].toInt())
        }.fold(Command()) { acc, e ->
            when (e.first) {
                "forward" -> acc.copy(depth = acc.depth + e.second)
                "down" -> acc.copy(horizontal = acc.horizontal + e.second)
                "up" -> acc.copy(horizontal = acc.horizontal - e.second)
                else -> acc
            }
        }

        return res.horizontal * res.depth
    }

    fun part2(input: List<String>): Int {
        var horizontal: Int = 0
        var depth: Int = 0
        var aim: Int = 0

        for (command in input) {
            val dir = command.split(" ")[0]
            val num = command.split(" ")[1].toInt()

            when (dir) {
                "forward" -> {
                    horizontal += num
                    depth += aim * num
                }
                "down" -> {
                    aim += num
                }
                "up" -> {
                    aim -= num
                }
            }
        }

        return horizontal * depth
    }

    fun part2b(input: List<String>): Int {
        data class Command(val horizontal: Int = 0, val depth: Int = 0, val aim: Int = 0)

        val res = input.map {
            val parts = it.split(" ")
            Pair(parts[0], parts[1].toInt())
        }.fold(Command()) { acc, e ->
            when (e.first) {
                "forward" -> {
                    acc.copy(horizontal = acc.horizontal + e.second, depth = acc.depth + acc.aim * e.second)
                }
                "down" -> acc.copy(aim = acc.aim + e.second)
                "up" -> acc.copy(aim = acc.aim - e.second)
                else -> acc
            }
        }

        return res.horizontal * res.depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check2(part1b(testInput), 150)
    check2(part2b(testInput), 900)

    val input = readInput("Day02")
    check2(part1b(input), 1936494)
    check2(part2b(input), 1997106066)

    println(part1b(input))
    println(part2b(input))
}
