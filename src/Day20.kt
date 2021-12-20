typealias Grid = List<List<Int>>

fun main() {
    fun newValue(i: Int, j: Int, grid: Grid, default: Int): Int {
        val idx = listOf(
            listOf(Pair(i - 1, j - 1), Pair(i - 1, j), Pair(i - 1, j + 1)),
            listOf(Pair(i, j - 1), Pair(i, j), Pair(i, j + 1)),
            listOf(Pair(i + 1, j - 1), Pair(i + 1, j), Pair(i + 1, j + 1))
        )
        val binNum = idx.map { row ->
            row.map {
                grid.getOrElse(it.first, defaultValue = { listOf() }).getOrElse(it.second) { default }
            }.fold("") { acc, i -> acc + i.toString() }
        }.fold("") { acc, s -> acc + s.toString() }

        return binNum.toInt(2)
    }

    fun advance(alg: List<Int>, grid: Grid, default: Int): Grid {
        val newGrid = MutableList(grid.size+2) { MutableList(grid[0].size+2) { 0 } }
        for (i in -1 .. grid.indices.last + 1) {
            for (j in -1 .. grid[0].indices.last + 1) {
                val num = newValue(i, j, grid, default)
                newGrid[i+1][j+1] = alg[num]
            }
        }

        return newGrid
    }

    fun part1(input: List<String>): Int {
        val alg = input[0].map { if (it == '#') 1 else 0 }
        var grid = input.drop(2).map { it.toCharArray().map { it2 -> if (it2 == '#') 1 else 0 } }

        repeat(2) {
            grid = advance(alg, grid, (alg[0] * it) % 2)
        }

        return grid.sumOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        val alg = input[0].map { if (it == '#') 1 else 0 }
        var grid = input.drop(2).map { it.toCharArray().map { it2 -> if (it2 == '#') 1 else 0 } }

        repeat(50) {
            grid = advance(alg, grid, (alg[0] * it) % 2)
        }

        return grid.sumOf { it.sum() }
    }

    val testInput = readInput("Day20_test")
    check2(part1(testInput), 35)
    check2(part2(testInput), 3351)

    val input = readInput("Day20")
    check2(part1(input), 5268)
    check2(part2(input), 16875)
}