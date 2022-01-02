fun main() {
    fun step(input: List<List<Char>>): Pair<List<List<Char>>, Boolean> {
        var moved = false

        var input = input
        var output = input.map { it.map { it }.toMutableList() }.toMutableList()

        for (i in output.indices) {
            for (j in output[i].indices) {
                var item = input[i][j]
                val nextIndex = (j+1) % output[i].size

                if (item == '>' && input[i][nextIndex] == '.') {
                    output[i][j] = '.'
                    output[i][nextIndex] = '>'
                    moved = true
                }
            }
        }

        input = output
        output = input.map { it.map { it }.toMutableList() }.toMutableList()

        for (i in output.indices) {
            for (j in output[i].indices) {
                var item = input[i][j]
                val nextIndex = (i+1) % output.size

                if (item == 'v' && input[nextIndex][j] == '.') {
                    output[i][j] = '.'
                    output[nextIndex][j] = 'v'
                    moved = true
                }
            }
        }

        return Pair(output, moved)
    }

    fun part1(input: List<String>): Int {
        var vals = input.map { it.toCharArray().toList() }
        var i = 1
        while(true) {
            val result = step(vals)
            if (!result.second) {
                return i
            }

            vals = result.first
            i += 1
        }
    }

    val testInput = readInput("Day25_test")
    check2(part1(testInput), 58)

    val input = readInput("Day25")
    check2(part1(input), 504)
}
