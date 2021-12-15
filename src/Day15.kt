import java.util.*

fun main() {
    class Data(input: List<String>, extended: Boolean) {
        val grid = input.map { row -> row.toCharArray().map { it.toString().toInt() } }
        val extended = extended

        fun xIndices(): IntRange {
            return if (extended) IntRange(0, (grid.indices.last+1)*5 - 1) else grid.indices
        }

        fun yIndices(): IntRange {
            return if (extended) IntRange(0, (grid[0].indices.last+1)*5 - 1) else grid[0].indices
        }

        fun value(x: Int, y: Int): Int {
            if (grid.indices.contains(x) && grid[0].indices.contains(y)) {
                return grid[x][y]
            }
            val realX = x.mod(grid.indices.last+1)
            val realY = y.mod(grid[0].indices.last+1)
            val factorX = x.div(grid.size)
            val factorY = y.div(grid[0].size)
            val newWeight = grid[realX][realY] + factorX + factorY
            return if (newWeight <= 9) newWeight else newWeight - 9
        }

        fun value(v: Pair<Int, Int>): Int {
            return value(v.first, v.second)
        }
    }

    fun dijkstra(grid: Data): Int {
        val dist = mutableMapOf<Pair<Int, Int>, Int>()
        val prev = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        //val Q = mutableSetOf<Pair<Int, Int>>()
        val pQ = PriorityQueue<Pair<Int, Int>>() { a, b -> dist[a]!! - dist[b]!! }
        var target = Pair(grid.xIndices().last, grid.yIndices().last)

        for (i in grid.xIndices()) {
            for (j in grid.yIndices()) {
                dist[Pair(i, j)] = Int.MAX_VALUE
                //Q.add(Pair(i, j))
                pQ.offer(Pair(i, j))
            }
        }

        dist[Pair(0, 0)] = 0

        while (pQ.isNotEmpty()) {
            //val u = Q.minOfWith(compareBy { dist[it]!! }) { it }
            //Q.remove(u)
            val u = pQ.poll()

            if (u == target) {
                break
            }

            for (v in listOf(Pair(u.first+1, u.second), Pair(u.first, u.second+1), Pair(u.first-1, u.second), Pair(u.first, u.second-1))) {
                if (v.first > grid.xIndices().last || v.second > grid.yIndices().last || v.first < 0 || v.second < 0) continue
                val alt = dist[u]!! + grid.value(v)
                if (alt < dist[v]!!) {
                    dist[v] = alt
                    prev[v] = u
                    // recompute value `v` in prio queue
                    pQ.remove(v)
                    pQ.offer(v)
                }
            }
        }

        var sum = 0
        var u = target
        while (u != Pair(0, 0)) {
            sum += grid.value(u)
            u = prev[u]!!
        }

        return sum
    }

    fun part1(input: List<String>): Int {
        val data = input.map { row -> row.toCharArray().map { it.toString().toInt() } }
        return dijkstra(Data(input, false))
    }

    fun part2(input: List<String>): Int {
        val data = input.map { row -> row.toCharArray().map { it.toString().toInt() } }
        return dijkstra(Data(input, true))
    }

    val testInput = readInput("Day15_test")

    check2(part1(testInput), 40)
    check2(part2(testInput), 315)

    val input = readInput("Day15")

    println(part1(input))
    println(part2(input))
}
