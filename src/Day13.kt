import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    class Data(input: List<String>) {
        val grid: MutableList<MutableList<Int>> = MutableList(1500) { MutableList(1500) { 0 } }
        val folds: MutableList<Pair<String, Int>> = mutableListOf()
        var maxX: Int = 0
        var maxY: Int = 0

        init {
            for (line in input) {
                var fold = "fold along ([a-z])=([0-9]+)".toRegex().find(line)
                if (fold != null) {
                    val components = "fold along ([a-z])=([0-9]+)".toRegex().find(line)
                    folds.add(Pair(fold!!.groupValues[1], fold!!.groupValues[2].toInt()))
                }

                var parts = line.split(",")
                if (parts.size < 2) continue
                grid[parts[0].toInt()][parts[1].toInt()] = 1

                maxX = max(maxX, parts[0].toInt())
                maxY = max(maxY, parts[1].toInt())
            }
        }

        fun doFold() {
            val f = folds.removeFirst()
            if (f.first == "x") maxX = f.second
            if (f.first == "y")  maxY = f.second

            for (x in 0 until grid.size) {
                for (y in 0 until grid[0].size) {
                    if (f.first == "x" && x > f.second) continue
                    if (f.first == "y" && y > f.second) continue

                    if (f.first == "x") {
                        grid[x][y] = min(grid[x][y] + grid[f.second * 2 - x][y], 1)
                    }
                    if (f.first == "y") {
                        grid[x][y] = min(grid[x][y] + grid[x][f.second * 2 - y], 1)
                    }
                }
            }
        }

        fun dots(): Int {
            var count = 0
            for (x in 0..maxX) {
                for (y in 0..maxY) {
                    if (grid[x][y] > 0) count += 1
                }
            }
            return count
        }

        fun printLetters() {
            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    print(if(grid[x][y] == 1) "X" else " ")
                }
                println()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val data = Data(input)
        data.doFold()
        return data.dots()
    }

    fun part2(input: List<String>) {
        val data = Data(input)
        while (data.folds.isNotEmpty()) {
            data.doFold()
        }
        data.printLetters()
    }

    val testInput = readInput("Day13_test")

    check2(part1(testInput), 17)

    val input = readInput("Day13")

    println(part1(input))
    part2(input)
}
