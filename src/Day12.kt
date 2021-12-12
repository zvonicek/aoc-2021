typealias Paths = Map<String, List<String>>
fun loadPaths(input: List<String>): Paths {
    val paths = mutableMapOf<String, List<String>>()
    input.forEach {
        var edge = it.split("-")
        paths[edge[0]] = paths.getOrDefault(edge[0], listOf()) + edge[1]
        paths[edge[1]] = paths.getOrDefault(edge[1], listOf()) + edge[0]
    }
    return paths
}

fun cavesFrom(paths: Paths,
              path: List<String>,
              vertexFilter: (List<String>, String) -> Boolean): List<List<String>> {
    val next = paths.getOrDefault(path.last(), listOf())
    if (path.last() == "end") {
        return listOf(path)
    }

    var caves = mutableListOf<List<String>>()
    for (v in next) {
        if (vertexFilter(path, v)) continue

        caves.addAll(cavesFrom(paths, path + v, vertexFilter))
    }

    return caves
}

fun main() {
    fun part1(input: List<String>): Int {
        val paths = loadPaths(input)
        val caves = cavesFrom(paths, listOf("start")) { path, v -> (v[0].isLowerCase() && path.contains(v)) }
        return caves.count()
    }

    fun part2(input: List<String>): Int {
        val paths = loadPaths(input)
        val caves = cavesFrom(paths, listOf("start")) { path, v ->
            if (v == "start" && path.contains("start")) return@cavesFrom true
            if (v == "end" && path.contains("end")) return@cavesFrom true
            val lowerCased = path.filter { it[0].isLowerCase() }
            if (v[0].isLowerCase() &&
                path.contains(v) &&
                lowerCased.count() - lowerCased.toSet().count() > 0) return@cavesFrom true
            return@cavesFrom false
        }
        return caves.count()
    }

    val testInput = readInput("Day12_test")

    check2(part1(testInput), 10)
    check2(part2(testInput),36)

    val input = readInput("Day12")

    check2(part1(input), 4754)
    check2(part2(input),143562)

    println(part1(input))
    println(part2(input))
}
