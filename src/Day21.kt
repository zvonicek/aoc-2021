import java.lang.Long.max
import kotlin.math.min

fun main() {
    // Part 1
    
    data class Game(var p1: Int, var p2: Int, var score1: Int = 0, var score2: Int = 0, var dice: Int = 1)
    fun playRound(game: Game, playerIndex: Int) {
        val roll = 3 * game.dice + 3
        game.dice = ((game.dice + 3 - 1) % 100) + 1

        if (playerIndex == 0) {
            game.p1 = ((game.p1 + roll - 1) % 10) + 1
            game.score1 += game.p1
        } else if (playerIndex == 1) {
            game.p2 = ((game.p2 + roll - 1) % 10) + 1
            game.score2 += game.p2
        }
    }

    fun part1(g: Game): Int {
        var rounds = 0
        while (g.score1 < 1000 && g.score2 < 1000) {
            playRound(g, playerIndex = rounds % 2)
            rounds += 1
        }

        return rounds * 3 * min(g.score1, g.score2)
    }

    // Part 2

    data class GameQ(var p1: Int, var p2: Int, var score1: Int = 0, var score2: Int = 0, var lastPlayer: Int = 1)
    var cache = mutableMapOf<GameQ, Pair<Long, Long>>()
    fun playRoundQ(game: GameQ): Pair<Long, Long> {
        if (game.score1 >= 21) return Pair(1, 0)
        if (game.score2 >= 21) return Pair(0, 1)
        if (cache.contains(game)) return cache[game]!!

        var sum = Pair(0.toLong(), 0.toLong())
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    var game2 = game.copy()
                    game2.lastPlayer = (game.lastPlayer + 1) % 2
                    var roll = i+j+k

                    if (game2.lastPlayer == 0) {
                        game2.p1 = ((game2.p1 + roll - 1) % 10) + 1
                        game2.score1 += game2.p1
                    } else if (game2.lastPlayer == 1) {
                        game2.p2 = ((game2.p2 + roll - 1) % 10) + 1
                        game2.score2 += game2.p2
                    }

                    val res = playRoundQ(game2)
                    sum = Pair(sum.first + res.first, sum.second + res.second)
                }
            }
        }

        cache[game] = sum
        return sum
    }

    fun part2(g: GameQ): Long {
        val res = playRoundQ(g)
        return max(res.first, res.second)
    }


    check2(part1(Game(4,8)), 739785)
    check2(part2(GameQ(4,8)), 444356092776315)

    check2(part1(Game(5,8)), 1067724)
    check2(part2(GameQ(5,8)), 630947104784464)
}