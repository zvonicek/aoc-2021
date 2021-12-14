fun main() {
    fun part1(input: List<String>): Int {
        var template = input[0]
        var rules = mutableMapOf<String, String>()
        for (line in input.drop(2)) {
            var parts = line.split(" -> ")
            rules[parts[0]] = parts[1]
        }

        repeat(10) {
            template = template
                .windowed(2, 1)
                .fold(template[0].toString()) { acc, s -> acc + rules[s] + s[1].toString() }
        }

        var counts = template.groupingBy { it }.eachCount()
        return counts.values.maxOrNull()!! - counts.values.minOrNull()!!
    }

    fun part2(input: List<String>): Long {
        var template = input[0]
        var rules = mutableMapOf<String, String>()
        for (line in input.drop(2)) {
            var parts = line.split(" -> ")
            rules[parts[0]] = parts[1]
        }

        var cache = mutableMapOf<Pair<String, Int>, Map<Char, Long>>()
        fun counts(pair: String, count: Int): Map<Char, Long> {
            if (cache.contains(Pair(pair, count))) {
                return cache[Pair(pair, count)]!!
            }

            if (count == 0) {
                return pair.groupingBy { it }.eachCount().mapValues { it.value.toLong() }
            }

            val exp = rules[pair]
            val dict1 = counts(pair[0].toString() + exp, count-1)
            val dict2 = counts(exp + pair[1].toString(), count-1).toMutableMap()
            dict2[exp!![0]] = dict2[exp!![0]]!! - 1

            val result = (dict1.keys + dict2.keys).associateWith { (dict1[it] ?: 0) + (dict2[it] ?: 0) }
            cache[Pair(pair, count)] = result
            return result
        }

        val res = template
            .windowed(2, 1)
            .fold(mapOf<Char, Long>()) { acc, s ->
                val res = counts(s, 40)
                (acc.keys + res.keys).associateWith { (acc[it] ?: 0) + (res[it] ?: 0) }
            }

        return res.values.maxOrNull()!! - res.values.minOrNull()!!
    }

    val testInput = readInput("Day14_test")

    check2(part1(testInput), 1588)
    check2(part2(testInput), 2188189693529)

    val input = readInput("Day14")

    println(part1(input))
    println(part2(input))
}
