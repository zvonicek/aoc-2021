import java.util.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

enum class CellType {
    A, B, C, D;

    fun stepCost(): Int {
        return when(this) {
            A -> 1
            B -> 10
            C -> 100
            D -> 1000
        }
    }
}

data class Configuration(
    var hallwayIndexesSet: Set<Int>,
    var room1Indexes: IntRange,
    var room2Indexes: IntRange,
    var room3Indexes: IntRange,
    var room4Indexes: IntRange
) {
    var roomIndexesSet: Set<Int>
    var maxHallwayIndex: Int

    init {
        roomIndexesSet = room1Indexes.toSet().union(room2Indexes.toSet()).union(room3Indexes.toSet()).union(room4Indexes.toSet())
        maxHallwayIndex = hallwayIndexesSet.maxOrNull()!!
    }

    fun targetField(): Field {
        val indexes = room1Indexes.map { Pair(it, CellType.A) } + room2Indexes.map { Pair(it, CellType.B) } +
                room3Indexes.map { Pair(it, CellType.C) } + room4Indexes.map { Pair(it, CellType.D) }
        return Field(indexes.associate { it.first to it.second })
    }
}

class Environment(var config: Configuration) {
    // one of the indices in range is expected to be in the hallway, one in the room
    fun distance(between: IntRange): Int {
        val hallwayIndexesBetween = between.subtract(config.roomIndexesSet).count()
        // line below assumes that all rooms are equally big
        val roomsBetween = ceil(between.intersect(config.roomIndexesSet).count() / config.room1Indexes.count().toDouble()).toInt()
        val roomMovements = listOf(config.room1Indexes, config.room2Indexes, config.room3Indexes, config.room4Indexes)
            .mapNotNull {
                if (it.contains(between.first)) between.first - it.first
                else if (it.contains(between.last)) between.last - it.first
                else null
            }.first() // assumes that there is one and only one end inside the room
        return hallwayIndexesBetween + roomsBetween + roomMovements
    }

    fun isInFinalPosition(locations: Map<Int, CellType>, index: Int): Boolean {
        val roomIndex = listOf(config.room1Indexes, config.room2Indexes, config.room3Indexes, config.room4Indexes)
            .first { it.contains(index) } // assumes that rooms indexes are disjunct

        if (roomIndex.last != index && !isInFinalPosition(locations, index+1)) return false
        return hasValidRoom(index, locations[index]!!)
    }

    fun hasValidRoom(index: Int, cellType: CellType): Boolean {
        return (config.room1Indexes.contains(index) && cellType == CellType.A) ||
                (config.room2Indexes.contains(index) && cellType == CellType.B) ||
                (config.room3Indexes.contains(index) && cellType == CellType.C) ||
                (config.room4Indexes.contains(index) && cellType == CellType.D)
    }

    fun canMoveFromRoom(locations: Map<Int, CellType>, index: Int): Boolean {
        val roomIndex = listOf(config.room1Indexes, config.room2Indexes, config.room3Indexes, config.room4Indexes)
            .first { it.contains(index) }
        val noItemsAbove = if (roomIndex.first == index) true else locations[index-1] == null
        return locations[index] != null && noItemsAbove && !isInFinalPosition(locations, index)
    }

    fun canMoveToRoom(locations: Map<Int, CellType>, index: Int, cellType: CellType): Boolean {
        val roomIndex = listOf(config.room1Indexes, config.room2Indexes, config.room3Indexes, config.room4Indexes)
            .first { it.contains(index) }
        val noItemsAbove = if (roomIndex.first == index) true else locations[index-1] == null
        val allBelowInFinalPosition = if (roomIndex.last == index) true else locations[index+1] != null && isInFinalPosition(locations, index+1)
        return hasValidRoom(index, cellType) && noItemsAbove && allBelowInFinalPosition
    }

    fun nextMovementsFor(field: Field): List<Movement> {
        val movements = mutableListOf<Movement>()

        for ((key, value) in field.locations) {
            val hallwayMin = field.locations.keys.filter { config.hallwayIndexesSet.contains(it) && it < key }.maxOrNull()?.plus(1) ?: 0
            val hallwayMax = field.locations.keys.filter { config.hallwayIndexesSet.contains(it) && it > key }.minOrNull()?.minus(1) ?: config.maxHallwayIndex

            if (config.hallwayIndexesSet.contains(key)) {
                val emptyRoomIndexes = (hallwayMin..hallwayMax).intersect(config.roomIndexesSet).subtract(field.locations.keys)
                    .filter { canMoveToRoom(field.locations, it, value) }
                emptyRoomIndexes.forEach { newIndex ->
                    val newLocations = field.locations.toMutableMap()
                    val dist = distance(min(key, newIndex)..max(key, newIndex)) * value.stepCost()
                    newLocations.remove(key)
                    newLocations[newIndex] = value
                    return listOf(Movement(Field(newLocations), dist))
                }
            } else if (config.roomIndexesSet.contains(key) && canMoveFromRoom(field.locations, key)) {
                val emptyHallwayIndexes = (hallwayMin..hallwayMax).intersect(config.hallwayIndexesSet)
                emptyHallwayIndexes.forEach { newIndex ->
                    val newLocations = field.locations.toMutableMap()
                    val dist = distance(min(key, newIndex)..max(key, newIndex)) * value.stepCost()
                    newLocations.remove(key)
                    newLocations[newIndex] = value
                    movements.add(Movement(Field(newLocations), dist))
                }
            }
        }

        return movements
    }

    fun buildFieldStateGraph(initialField: Field): Map<Field, List<Movement>> {
        val fieldStateGraph = mutableMapOf<Field, List<Movement>>()
        val fieldStatesToProcess = mutableSetOf<Field>(initialField)

        while (fieldStatesToProcess.isNotEmpty()) {
            val field = fieldStatesToProcess.iterator().next()
            fieldStatesToProcess.remove(field)

            val movements = nextMovementsFor(field)
            fieldStateGraph[field] = movements

            val newMovements = movements.map { it.field }.filter { !fieldStateGraph.contains(it) }
            fieldStatesToProcess.addAll(newMovements)
        }

        return fieldStateGraph
    }
}

data class Field(var locations: Map<Int, CellType>)
data class Movement(var field: Field, var cost: Int)

fun dijkstra(start: Field, graph: Map<Field, List<Movement>>, target: Field): Int {
    val dist = mutableMapOf<Field, Int>()
    val prev = mutableMapOf<Field, Field>()
    val pQ = PriorityQueue<Field>() { a, b -> dist[a]!! - dist[b]!! }

    for (v in graph.keys) {
        dist[v] = Int.MAX_VALUE
        pQ.offer(v)
    }

    dist[start] = 0

    while (pQ.isNotEmpty()) {
        val u = pQ.poll()
        if (u == target) {
            break
        }

        for (v in graph[u]!!) {
            val alt = dist[u]!! + v.cost
            if (alt < dist[v.field]!!) {
                dist[v.field] = alt
                prev[v.field] = u
                // recompute value `v` in prio queue
                pQ.remove(v.field)
                pQ.offer(v.field)

                if (prev.count() % 5000 == 0) {
                    println("path size: " + prev.count().toString())
                }
            }
        }
    }

    return dist[target]!!
}

fun main() {
    fun part1(field: Field): Int {
        // Locations: 0 1 [2 3] 4 [5 6] 7 [8 9] 10 [11 12] 13 14
        val cfg = Configuration(setOf(0, 1, 4, 7, 10, 13, 14), 2..3, 5..6, 8..9, 11..12)
        val env = Environment(cfg)

        val graph = env.buildFieldStateGraph(field)
        println("graph done, nodes:" + graph.keys.count())
        return dijkstra(field, graph, cfg.targetField())
    }

    fun part2(field: Field): Int {
        val cfg = Configuration(setOf(0, 1, 6, 11, 16, 21, 22), 2..5, 7..10, 12..15, 17..20)
        val env = Environment(cfg)

        val graph = env.buildFieldStateGraph(field)
        println("graph done, nodes:" + graph.keys.count())
        return dijkstra(field, graph, cfg.targetField())
    }

    val testField1 = Field(mapOf(Pair(2, CellType.B), Pair(3, CellType.A),
        Pair(5, CellType.C), Pair(6, CellType.D),
        Pair(8, CellType.B), Pair(9, CellType.C),
        Pair(11, CellType.D), Pair(12, CellType.A)))
    check2(part1(testField1), 12521)

    val testField2 = Field(mapOf(Pair(2, CellType.B), Pair(3, CellType.D), Pair(4, CellType.D), Pair(5, CellType.A),
        Pair(7, CellType.C), Pair(8, CellType.C), Pair(9, CellType.B), Pair(10, CellType.D),
        Pair(12, CellType.B), Pair(13, CellType.B), Pair(14, CellType.A), Pair(15, CellType.C),
        Pair(17, CellType.D), Pair(18, CellType.A), Pair(19, CellType.C), Pair(20, CellType.A))
    )
    check2(part2(testField2), 44169)

    val field1 = Field(mapOf(Pair(2, CellType.C), Pair(3, CellType.D),
        Pair(5, CellType.C), Pair(6, CellType.A),
        Pair(8, CellType.B), Pair(9, CellType.B),
        Pair(11, CellType.D), Pair(12, CellType.A)))
    check2(part1(field1), 15299)

    val field2 = Field(mapOf(Pair(2, CellType.C), Pair(3, CellType.D), Pair(4, CellType.D), Pair(5, CellType.D),
        Pair(7, CellType.C),  Pair(8, CellType.C), Pair(9, CellType.B), Pair(10, CellType.A),
        Pair(12, CellType.B), Pair(13, CellType.B), Pair(14, CellType.A), Pair(15, CellType.B),
        Pair(17, CellType.D), Pair(18, CellType.A), Pair(19, CellType.C), Pair(20, CellType.A)))
    check2(part2(field2), 47193)
}
