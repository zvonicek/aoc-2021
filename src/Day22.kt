import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Box(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
        fun area(): Long {
            if (xRange.last < xRange.first || yRange.last < yRange.first || zRange.last < zRange.first) return 0

            return abs(xRange.last - xRange.first + 1).toLong() *
                    abs(yRange.last - yRange.first + 1).toLong() *
                    abs(zRange.last - zRange.first + 1).toLong()
        }

        fun overlap(box: Box): Box {
            return Box(max(xRange.first, box.xRange.first)..min(xRange.last, box.xRange.last),
                max(yRange.first, box.yRange.first)..min(yRange.last, box.yRange.last),
                max(zRange.first, box.zRange.first)..min(zRange.last, box.zRange.last))
        }
    }
    data class Step(var mode: Int, var coord: Box)

    fun load(input: List<String>): List<Step> {
        return input.map {
            val r = "(on|off) x=(-?[0-9]+)..(-?[0-9]+),y=(-?[0-9]+)..(-?[0-9]+),z=(-?[0-9]+)..(-?[0-9]+)".toRegex().find(it)
            var mode = if (r!!.groupValues[1] == "on") 1 else 0
            val rVals = r!!.groupValues.drop(2).map { v -> v.toInt() }
            Step(mode, Box(rVals[0]..rVals[1], rVals[2]..rVals[3], rVals[4]..rVals[5]))
        }
    }

    fun part1(input: List<String>): Int {
        val regions = MutableList(101) { MutableList(101) { MutableList(101) { 0 } } }
        val data = load(input)

        for (d in data) {
            for (x in d.coord.xRange) {
                if (x < -50 || x > 50) continue
                for (y in d.coord.yRange) {
                    if (y < -50 || y > 50) continue
                    for (z in d.coord.zRange) {
                        if (z < -50 || z > 50) continue

                        regions[x+50][y+50][z+50] = d.mode
                    }
                }
            }
        }

        return regions.sumOf { it.sumOf { it.sum() } }
    }

    fun part2(input: List<String>): Long {
        val data = load(input)

        var cache = mutableMapOf<Triple<Box, List<Step>, List<Box>>, Long>()
        // Recursively find overlap of `item` one by one with `withItems`. `previousOverlaps` keeps list of overlaps
        // in previous (already matched) items. This is important because same overlap may have been counted already and
        // we don't want to count it again.
        fun overlaps(item: Box, withItems: List<Step>, previousOverlaps: List<Box> = listOf()): Long {
            if (withItems.isEmpty()) return 0
            if (cache.contains(Triple(item, withItems, previousOverlaps))) {
                return cache[Triple(item, withItems, previousOverlaps)]!!
            }

            val intersectedItem = withItems.last()
            val mainOverlap = item.overlap(intersectedItem.coord)

            val result: Long
            if (intersectedItem.mode == 0) {
                result = 0 + overlaps(item, withItems.dropLast(1), previousOverlaps + mainOverlap)
            } else {
                // important optimization: only recursively compute overlaps if there is any overlap at all, otherwise
                // return zero.
                val hasAnyOverlapWithPreviousOverlaps = previousOverlaps.firstOrNull() { it.overlap(mainOverlap).area() > 0 } != null
                val previousIntersectionsArea: Long = if (hasAnyOverlapWithPreviousOverlaps) {
                    // computing overlaps may have cascading effects so we have to recursively compute it. Doing simple
                    // "previousIntersections OVERLAP mainOverlap" is not enough.
                    // Transforming `previousOverlaps` to Step with mode 1 is just a way to comply with the method arg
                    // structure, the mode is always 1 in this case (we only increase and never decrease).
                    overlaps(mainOverlap, previousOverlaps.map { Step(1, it) })
                } else 0

                result = mainOverlap.area() - previousIntersectionsArea + overlaps(item, withItems.dropLast(1), previousOverlaps + mainOverlap)
            }

            cache[Triple(item, withItems, previousOverlaps)] = result
            return result
        }

        val cubes = mutableMapOf<Int, Long>()
        for (i in data.indices) {
            val area = data[i].coord.area()
            val overlapArea = overlaps(data[i].coord, data.subList(0, i))
            if (data[i].mode == 1) {
                cubes[i] = area - overlapArea
            } else {
                cubes[i] = -overlapArea
            }
        }

        return cubes.values.sum()
    }

    val testInput0 = readInput("Day22_test0")
    check2(part1(testInput0), 590784)

    val testInput = readInput("Day22_test")
    check2(part2(testInput), 2758514936282235)

    val input = readInput("Day22")
    check2(part1(input), 583641)
    check2(part2(input), 1182153534186233)
}