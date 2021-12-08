fun main() {

    fun <T> permutations(list: List<T>): List<List<T>> = when {
        list.size <= 1 -> listOf(list)
        else ->
            permutations(list.drop(1)).map { perm ->
                (list.indices).map { i ->
                    perm.subList(0, i) + list.first() + perm.drop(i)
                }
            }.flatten()
    }

    val segments = arrayOf(
        setOf('a', 'b', 'c', 'e', 'f', 'g'),
        setOf('c', 'f'),
        setOf('a', 'c', 'd', 'e', 'g'),
        setOf('a', 'c', 'd', 'f', 'g'),
        setOf('b', 'c', 'd', 'f'),
        setOf('a', 'b', 'd', 'f', 'g'),
        setOf('a', 'b', 'd', 'e', 'f', 'g'),
        setOf('a', 'c', 'f'),
        setOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        setOf('a', 'b', 'c', 'd', 'f', 'g')
    )
    val allSegmentChars = setOf('a', 'b', 'c', 'd', 'e', 'f', 'g')

    fun part1(input: List<String>): Int {
        return input.fold(0) { acc, s ->
            val rightSegmentVals = s.split("|")[1].split(" ")
            acc + rightSegmentVals.count { arrayOf(2, 3, 4, 7).contains(it.length) }
        }
    }

    fun part2(input: List<String>): Int {
        var totalSum = 0

        for (row in input) {
            // key: character in row's wiring (encoding), value: possible matching characters in standard wiring (a,b...g)
            val segmentMap = mutableMapOf<Char, Set<Char>>()
            for (ch in allSegmentChars) {
                segmentMap[ch] = allSegmentChars.toMutableSet()
            }

            // Collect all the possible valid mappings from row wiring to standard wiring (a,b...g)
            for (leftSegment in row.split("|")[0].split(" ")) {
                val candidates = segments.filter { it.size == leftSegment.length }
                    .fold(setOf<Char>()) { acc, strings ->
                        acc.union(strings)
                    }

                for (char in leftSegment) {
                    segmentMap[char] = segmentMap[char]!!.intersect(candidates)
                }
            }

            // Now we still won't have complete solution. There are still many potentially valid mappings, but
            // we can run certain optimizations to reduce those enough. After that, we will be able to brute force the
            // rest.
            var madeChange = true
            while(madeChange) {
                madeChange = false
                for ((key, value) in segmentMap) {
                    // If there are just two options available, it's the case of the digit one ("cc", "ff"), we just
                    // know which is "c" and which is "f". We can thus safely remove occurrences of those characters
                    // from other digits and reduce the search space.
                    if (value.size == 2) {
                        for ((key2, value2) in segmentMap) {
                            if (key != key2 && value == value2) {
                                // key = first character of digit 1. Key2 = second character of digit 1.
                                for ((key3, value3) in segmentMap) {
                                    // Just make sure we don't remove those from the actual two characters of digit 1.
                                    if (key3 != key && key3 != key2 && value3.intersect(value).isNotEmpty()) {
                                        segmentMap[key3] = segmentMap[key3]!!.minus(value)
                                        madeChange = true
                                    }
                                }
                            }
                        }
                    }
                    // Another optimization. If there is just one option available, it's the case of the digit 7.
                    // Do the similar prune operation as above. Yeah, I could generalize those into a single
                    // common operation, but hey, I've already spent enough hours on this today.
                    if (value.size == 1) {
                        for ((key2, value2) in segmentMap) {
                            if (key2 != key && value2.intersect(value).isNotEmpty()) {
                                segmentMap[key2] = segmentMap[key2]!!.minus(value)
                                madeChange = true
                            }
                        }
                    }
                }
            }

            // Allright, here we are in a state when there are only two possible options for each letter available,
            // except that in one case there always is only one. There might be a smarter way to do this but I'm going
            // to simply brute force it.
            var finalNum = ""
            for (rightSegment in row.split("|")[1].strip().split(" ")) {
                fun findNumberWithValidMapping(): Int {
                    val candidates = rightSegment.map { segmentMap[it]!! }
                    for (candidate in segments.filter { it.size == candidates.size }) {
                        for (perm in permutations(candidate.toList())) {
                            var match = true

                            for (a in candidates.zip(perm)) {
                                match = match && a.first.contains(a.second)
                            }

                            // We found a match for every letter in the current number!
                            if (match) {
                                return segments.indexOf(candidate)
                            }
                        }
                    }
                    return 0
                }

                finalNum += findNumberWithValidMapping().toString()
            }

            totalSum += finalNum.toInt()
        }

        return totalSum
    }

    val testInput = readInput("Day08_test")

    check2(part1(testInput), 26)
    check2(part2(testInput), 61229)

    val input = readInput("Day08")

    check2(part1(input), 495)
    check2(part2(input), 1055164)

    println(part1(input))
    println(part2(input))
}
