fun main() {
    class Data(input: List<String>) {
        val zeros: MutableList<Int>
        val ones: MutableList<Int>

        init {
            zeros = MutableList<Int>(input[0].length) { 0 }
            ones = MutableList<Int>(input[0].length) { 0 }

            for (command in input) {
                for ((index, bit) in command.iterator().withIndex()) {
                    if (bit == '0') {
                        zeros[index] += 1
                    } else if (bit == '1') {
                        ones[index] += 1
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        var data = Data(input)

        val res = data.zeros.zip(data.ones).fold("") { acc, pair ->
            if (pair.first > pair.second) acc + "0" else acc + "1"
        }

        val res2 = data.zeros.zip(data.ones).fold("") { acc, pair ->
            if (pair.first > pair.second)  acc + "1" else acc + "0"
        }

        return res.toInt(2) * res2.toInt(2)
    }

    fun part2(input: List<String>): Int {
        var oxyVals = input
        for (i in 0 until input[0].length) {
            if (oxyVals.size == 1) break

            val data = Data(oxyVals)
            oxyVals = oxyVals.filter {
                if (data.zeros[i] <= data.ones[i]) it[i] == '1' else it[i] == '0'
            }
        }

        var co2Vals = input
        for (i in 0 until input[0].length) {
            if (co2Vals.size == 1) break

            val data = Data(co2Vals)
            co2Vals = co2Vals.filter {
                if (data.zeros[i] <= data.ones[i]) it[i] == '0' else it[i] == '1'
            }
        }

        return oxyVals[0].toInt(2) * co2Vals[0].toInt(2)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check2(part1(testInput), 198)
    check2(part2(testInput), 230)

    val input = readInput("Day03")
    check2(part1(input), 3277364)
    check2(part2(input), 5736383)

    println(part1(input))
    println(part2(input))
}
