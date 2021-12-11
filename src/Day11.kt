class Data(input: List<String>) {
    val grid: MutableList<MutableList<Int>>
    var flashCandidates = mutableSetOf<Pair<Int, Int>>()

    init {
        grid = input.map { it.toCharArray().map { it.toString().toInt() }.toMutableList() }.toMutableList()
    }

    fun increase(x: Int, y: Int) {
        grid[x][y] += 1
        if (grid[x][y] > 9) {
            if (flashCandidates.contains(Pair(x, y)))
                return

            flashCandidates.add(Pair(x, y))
            for (neighbour in (x-1..x+1).flatMap { a -> (y-1..y+1).map { b-> Pair(a, b) } }) {
                if (neighbour.first < 0 || neighbour.second < 0 || neighbour.first >= grid.size || neighbour.second >= grid.size)
                    continue

                increase(neighbour.first, neighbour.second)
            }
        }
    }

    fun increaseAll()  {
        for (x in grid.indices) {
            for (y in grid[0].indices) {
                increase(x, y)
            }
        }
    }

    fun performFlash() {
        for (c in flashCandidates) {
            grid[c.first][c.second] = 0
        }

        flashCandidates.clear()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var data = Data(input)
        var acc = 0

        repeat(100) {
            data.increaseAll()
            acc += data.flashCandidates.count()
            data.performFlash()
        }

        return acc
    }

    fun part2(input: List<String>): Int {
        var data = Data(input)
        var gen = 0

        while(true) {
            gen += 1
            data.increaseAll()
            if (data.flashCandidates.count() == data.grid.count() * data.grid[0].count()) {
                return gen
            }
            data.performFlash()
        }
    }

    val testInput = readInput("Day11_test")

    check2(part1(testInput), 1656)
    check2(part2(testInput),195)

    val input = readInput("Day11")

    println(part1(input))
    println(part2(input))
}
