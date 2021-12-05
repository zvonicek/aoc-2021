infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

fun main() {
    class Space(
        val grid: MutableList<MutableList<Int>>
    ) {
        fun markLine(x1: Int, y1: Int, x2: Int, y2: Int) {
            val xIterator = (x1 toward x2).iterator()
            val yIterator = (y1 toward y2).iterator()
            var xCurrent: Int? = null
            var yCurrent: Int? = null

            while(xIterator.hasNext() || yIterator.hasNext()) {
                xCurrent = if (xIterator.hasNext()) xIterator.next() else xCurrent
                yCurrent = if (yIterator.hasNext()) yIterator.next() else yCurrent
                grid[xCurrent!!][yCurrent!!] += 1
            }
        }

        fun overlaps(): Int {
            return grid.fold(0) { acc, ints -> acc + ints.count { it >= 2 } }
        }
    }

    fun getOverlaps(input: List<String>, straightOnly: Boolean): Int {
        val space = Space(MutableList(1000) { MutableList(1000) { 0 } })

        for (line in input) {
            val components = "([0-9]+),([0-9]+) -> ([0-9]+),([0-9]+)".toRegex().find(line)
            val coords = components!!.groupValues.mapNotNull { it.toIntOrNull() }

            if (straightOnly && coords[0] != coords[2] && coords[1] != coords[3]) {
                continue
            }

            space.markLine(coords[0], coords[1], coords[2], coords[3])
        }

        return space.overlaps()
    }

    fun part1(input: List<String>): Int {
        return getOverlaps(input, true)
    }

    fun part2(input: List<String>): Int {
        return getOverlaps(input, false)
    }

    val testInput = readInput("Day05_test")

    check2(part1(testInput), 5)
    check2(part2(testInput), 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
