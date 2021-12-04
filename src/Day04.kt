typealias Cards = MutableList<MutableList<MutableList<Pair<Int, Boolean>>>>

fun main() {
    fun preload(
        input: List<String>,
        cards: Cards
    ): List<Int> {
        var turns: List<Int>? = null
        for (command in input) {
            if (turns == null) {
                turns = command.split(",").map { it.toInt() }
                continue
            }

            if (command == "") {
                cards.add(mutableListOf())
                continue
            }

            val line = command
                .trim()
                .split("\\s+".toRegex())
                .map { Pair(it.toInt(), false) }
            cards.last().add(line.toMutableList())
        }

        return turns!!
    }

    fun playTurn(
        number: Int,
        cards: Cards
    ) {
        for ((index0, card) in cards.iterator().withIndex()) {
            for ((index1, row) in card.iterator().withIndex()) {
                for ((index2, cardNum) in row.iterator().withIndex()) {
                    if (cardNum.first == number) {
                        cards[index0][index1][index2] = Pair(cardNum.first, true)
                    }
                }
            }
        }
    }

    fun claimWinner(
        cardIndex: Int,
        cards: Cards,
        winners: MutableList<Int>
    ): Int {
        winners.add(cardIndex)
        return cards[cardIndex].fold(0) { acc, pairs ->
            acc + pairs.fold(0) { acc, pair -> if (pair.second) acc else acc + pair.first }
        }
    }

    fun checkWinners(
        lastTurn: Int,
        cards: Cards,
        winners: MutableList<Int>
    ): List<Int> {
        val results = mutableListOf<Int>()

        for ((index, card) in cards.iterator().withIndex()) {
            if (winners.contains(index)) {
                continue
            }

            val columnCandidates = MutableList<Boolean>(card[0].size) { true }

            for (row in card) {
                for ((index1, col) in row.iterator().withIndex()) {
                    if (!col.second) {
                        columnCandidates[index1] = false
                    }
                }

                if (row.all { it.second }) {
                    val score = claimWinner(index, cards, winners)
                    results.add(score * lastTurn)
                }
            }

            if (columnCandidates.any { it }) {
                val score = claimWinner(index, cards, winners)
                results.add(score * lastTurn)
            }
        }

        return results
    }

    fun part1(input: List<String>): Int {
        val cards = mutableListOf<MutableList<MutableList<Pair<Int, Boolean>>>>()
        val turns = preload(input, cards)

        for (turn in turns) {
            playTurn(turn,  cards)
            val winner = checkWinners(turn, cards, mutableListOf()).firstOrNull()
            if (winner != null) return winner
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val cards = mutableListOf<MutableList<MutableList<Pair<Int, Boolean>>>>()
        val winners = mutableListOf<Int>()
        val turns = preload(input, cards)

        val winnerScores = mutableListOf<Int>()
        for (turn in turns) {
            playTurn(turn, cards)
            val result = checkWinners(turn, cards, winners)
            winnerScores.addAll(result)
        }

        return winnerScores.last()
    }

    val testInput = readInput("Day04_test")
    check2(part1(testInput), 4512)
    check2(part2(testInput), 1924)

    val input = readInput("Day04")
    check2(part1(input), 44736)
    check2(part2(input), 1827)

    println(part1(input))
    println(part2(input))
}
