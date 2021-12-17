import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    data class Probe(var posX: Int = 0, var posY: Int = 0, var velocityX: Int, var velocityY: Int) {
        var maxY = 0

        fun step() {
            posX += velocityX
            posY += velocityY
            maxY = max(maxY, posY)

            if (velocityX > 0) velocityX -= 1
            else if (velocityX < 0) velocityX += 1
            velocityY -= 1
        }
    }

    data class Target(val x: IntRange, val y: IntRange) {
        fun hits(probe: Probe): Boolean {
            return x.contains(probe.posX) && y.contains(probe.posY)
        }

        fun missed(probe: Probe): Boolean {
            return probe.posX > x.last || probe.posY < min(y.last, y.first)
        }
    }

    fun getHits(input: List<String>): List<Probe> {
        var r = "target area: x=(-?[0-9]+)..(-?[0-9]+), y=(-?[0-9]+)..(-?[0-9]+)".toRegex().find(input[0])
        val t = Target(IntRange(r!!.groupValues[1].toInt(), r!!.groupValues[2].toInt()),
            IntRange(r!!.groupValues[3].toInt(), r!!.groupValues[4].toInt()))

        var hits = mutableListOf<Probe>()
        for (x in 1..t.x.last) {
            for (y in -200 .. 200) {
                val p = Probe(0, 0, x, y)
                while (!t.missed(p)) {
                    p.step()
                    if (t.hits(p)) {
                        hits.add(p)
                        break
                    }
                }
            }
        }
        return hits
    }

    fun part1(input: List<String>): Int {
        return getHits(input).maxOf { it.maxY }
    }

    fun part2(input: List<String>): Int {
        return getHits(input).count()
    }

    val testInput = readInput("Day17_test")

    check2(part1(testInput), 45)
    check2(part2(testInput), 112)

    val input = readInput("Day17")

    println(part1(input))
    println(part2(input))
}
