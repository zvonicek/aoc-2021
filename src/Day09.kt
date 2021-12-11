data class Point(val x: Int, val y: Int) {
    fun neighboursIndexes(): List<Point> {
        return listOf(
            Point(x-1, y),
            Point(x+1, y),
            Point(x, y-1),
            Point(x, y+1)
        )
    }

    fun neighboursValues(grid: List<List<Int>>): List<Int> {
        return neighboursIndexes().map {
            grid.getOrNull(it.x)?.getOrNull(it.y)
        }.filterNotNull()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val rows = input.map { row -> row.toCharArray().map { it.toString().toInt() } }
        var lowPointsSum = 0
        for ((rIndex, row) in rows.iterator().withIndex()) {
            for ((cIndex, col) in row.iterator().withIndex()) {
                val point = Point(rIndex, cIndex)

                if (point.neighboursValues(rows).all { it > col }) {
                    lowPointsSum += col + 1
                }
            }
        }
        return lowPointsSum
    }

    fun part2(input: List<String>): Int {
        val rows = input.map { row -> row.toCharArray().map { it.toString().toInt() } }

        fun findBasin(row: Int, col: Int): Int {
            var toVisit = mutableListOf(Point(row, col))
            var visited = mutableListOf<Point>()
            while (toVisit.isNotEmpty()) {
                val point = toVisit.first()
                toVisit.removeAt(0)
                if (visited.contains(point)) continue

                visited.add(point)
                toVisit.addAll(point.neighboursIndexes().filter {
                    val nPoint = rows.getOrNull(it.x)?.getOrNull(it.y)
                    if (nPoint != null) nPoint > rows[point.x][point.y] && nPoint != 9 else false
                })
            }

            return visited.count()
        }

        var counts = mutableListOf<Int>()
        for ((rIndex, row) in rows.iterator().withIndex()) {
            for ((cIndex, col) in row.iterator().withIndex()) {
                val point = Point(rIndex, cIndex)

                if (point.neighboursValues(rows).all { it > col }) {
                    counts.add(findBasin(rIndex, cIndex))
                }
            }
        }

        return counts.sortedDescending().take(3).fold(1) { acc, i -> acc * i }
    }


    val testInput = readInput("Day09_test")

    check2(part1(testInput), 15)
    check2(part2(testInput),1134)

    val input = readInput("Day09")

    check2(part2(input),1558722)

    println(part1(input))
    println(part2(input))
}
