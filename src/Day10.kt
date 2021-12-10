fun main() {
    fun rankIncorrect(char: Char): Int {
        return when (char) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }
    }

    fun rankMissing(char: Char): Int {
        return when (char) {
            ')' -> 1
            ']' -> 2
            '}' -> 3
            '>' -> 4
            else -> 0
        }
    }

    val correctMap = mapOf<Char, Char>(Pair('(', ')'), Pair('[', ']'), Pair('{', '}'), Pair('<', '>'))

    fun part1(input: List<String>): Int {
        var totalScore = 0
        for (line in input) {
            var score = 0
            var stack = ArrayDeque(listOf<Char>())
            line.forEach { c: Char ->
                when (c) {
                    '(', '[', '{', '<' -> stack.add(c)
                    ')', ']', '}', '>' -> {
                        var value = stack.removeLast()
                        if (correctMap[value] != c) score += rankIncorrect(c)
                    }
                }
            }

            totalScore += score
        }
        return totalScore
    }

    fun part2(input: List<String>): Long {
        var totalScores = mutableListOf<Long>()
        for (line in input) {
            var score: Long = 0
            val stack = ArrayDeque(listOf<Char>())
            line.forEach { c: Char ->
                when (c) {
                    '(', '[', '{', '<' -> stack.add(c)
                    ')', ']', '}', '>' -> {
                        var value = stack.removeLast()
                        if (correctMap[value] != c) score += rankIncorrect(c)
                    }
                }
            }

            if (score.toInt() == 0) {
                while(stack.isNotEmpty()) {
                    val char = stack.removeLast()
                    score *= 5
                    score += rankMissing(correctMap[char]!!).toLong()
                }

                totalScores.add(score)
            }
        }
        
        return totalScores.sorted()[totalScores.count() / 2]
    }

    val testInput = readInput("Day10_test")

    check2(part1(testInput), 26397)
    check2(part2(testInput),288957)

    val input = readInput("Day10")

    println(part1(input))
    println(part2(input))
}
