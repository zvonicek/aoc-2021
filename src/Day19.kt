import java.lang.Integer.max
import kotlin.math.abs

fun main() {
    data class Beacon(val loc: Triple<Int, Int, Int>) {
        // https://stackoverflow.com/questions/16452383/how-to-get-all-24-rotations-of-a-3-dimensional-array
        fun rotations(): List<Beacon> {
            fun roll(b: Beacon) = Beacon(Triple(b.loc.first, b.loc.third, -b.loc.second))
            fun turn(b: Beacon) = Beacon(Triple(-b.loc.second, b.loc.first, b.loc.third))

            val res = mutableListOf<Beacon>()
            var b = this

            for (i in 0..1) {
                for (j in 0..2) {
                    b = roll(b)
                    res.add(b)

                    for (k in 0..2) {
                        b = turn(b)
                        res.add(b)
                    }
                }
                b = roll(turn(roll(b)))
            }

            return res
        }
    }

    data class Scanner(val id: Int, val beacons: List<Beacon>) {
        fun rotations(): List<Scanner> {
            val res = mutableListOf<Scanner>()
            val rotations = beacons.map { it.rotations() }
            for (i in 0 until rotations[0].count()) {
                res.add(Scanner(id, rotations.map { it[i] }))
            }
            return res
        }
    }

    fun load(input: List<String>): List<Scanner> {
        val scanners = mutableListOf<Scanner>()
        var beacons = mutableListOf<Beacon>()
        for (line in input) {
            if (line.startsWith("---")) {
                if (beacons.isNotEmpty()) {
                    scanners.add(Scanner(scanners.count(), beacons))
                    beacons = mutableListOf()
                }
                continue
            }
            if (line.isEmpty()) continue
            val b = "(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)".toRegex().find(line)
            beacons.add(Beacon(Triple(b!!.groupValues[1].toInt(), b.groupValues[2].toInt(), b.groupValues[3].toInt())))
        }
        scanners.add(Scanner(scanners.count(), beacons))
        return scanners
    }

    fun run(input: List<String>): Pair<Int, Int> {
        val scanners = load(input)
        val relativeOrigins = mutableMapOf<Pair<Int, Int>, Triple<Int, Int, Int>>() // key: (fromScanner, toScanner)
        // Scanners that are rotated to the base of the first scanner
        val rotatedScanners = mutableMapOf<Int, Scanner>()
        rotatedScanners[0] = scanners[0]

        val outerScanners = scanners.map { it.id }.toMutableList()
        while (outerScanners.isNotEmpty()) {
            val s1Id = outerScanners.removeFirst()
            if (!rotatedScanners.contains(s1Id)) {
                // Important step: if s1 was not yet rotated to the correct base, we need to skip it and do later.
                // Otherwise, the relativeOrigin derived from it will be based on an incorrect base.
                outerScanners.add(s1Id)
                continue
            }
            val s1 = rotatedScanners[s1Id]!!

            for (s2 in scanners) {
                if (s1.id == s2.id) continue

                for (s2rot in s2.rotations()) {
                    val diffs = mutableListOf<Triple<Int, Int, Int>>()
                    for (b1 in s1.beacons) {
                        for (b2 in s2rot.beacons) {
                            diffs.add(b1.loc - b2.loc)
                        }
                    }
                    val counts = diffs.groupingBy { it }.eachCount()
                    val matches = counts.filter { entry -> entry.value >= 12 }
                    if (matches.isNotEmpty()) {
                        // Keeping the rotated scanners is important as relativeOrigins derived from it must keep
                        // consistent base aligned with the initial base (defined by the first scanner)
                        rotatedScanners[s2.id] = s2rot
                        val origin = matches.keys.first()
                        relativeOrigins[Pair(s1.id, s2.id)] = origin
                    }
                }
            }
        }

        val absoluteOrigins = mutableMapOf<Int, Triple<Int, Int, Int>>()
        absoluteOrigins[0] = Triple(0, 0, 0)
        val toVisit = mutableListOf<Int>(0)
        while (toVisit.isNotEmpty()) {
            val s = toVisit.removeLast()
            for (next in relativeOrigins.keys.filter { it.first == s && !absoluteOrigins.keys.contains(it.second) }) {
                absoluteOrigins[next.second] = relativeOrigins[next]!! + absoluteOrigins[s]!!
                toVisit.add(next.second)
            }
        }

        val uniqueBeacons = mutableSetOf<Beacon>()
        for (s in rotatedScanners.values) {
            for (b in s.beacons) {
                val origin = absoluteOrigins[s.id]!!
                uniqueBeacons.add(Beacon(b.loc + origin))
            }
        }

        var maxDistance = 0
        for (d1 in absoluteOrigins.values) {
            for (d2 in absoluteOrigins.values) {
                maxDistance = max(maxDistance, abs(d1.first-d2.first)+abs(d1.second-d2.second)+abs(d1.third-d2.third))
            }
        }

        return Pair(uniqueBeacons.count(), maxDistance)
    }

    val testInput = readInput("Day19_test")
    val res = run(testInput)
    check2(res.first,79)
    check2(res.second,3621)

    val input = readInput("Day19")
    val res2 = run(input)
    check2(res2.first,467)
    check2(res2.second,12226)
}

private operator fun Triple<Int, Int, Int>.minus(triple: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
    return Triple(first-triple.first, second-triple.second, third-triple.third)
}

private operator fun Triple<Int, Int, Int>.plus(triple: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
    return Triple(first+triple.first, second+triple.second, third+triple.third)
}