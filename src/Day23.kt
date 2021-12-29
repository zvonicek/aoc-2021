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
    var hallwayIndexes: List<Int>,
    var room1Indexes: List<Int>,
    var room2Indexes: List<Int>,
    var room3Indexes: List<Int>,
    var room4Indexes: List<Int>
) {
    var roomIndexesSet: Set<Int>
    var hallwayIndexesSet: Set<Int>

    init {
        roomIndexesSet = (room1Indexes + room2Indexes + room3Indexes + room4Indexes).toSet()
        hallwayIndexesSet = hallwayIndexes.toSet()
    }
}

//class Environment(var config: Configuration) {
//
//}

// Locations: 0 1 [2 3] 4 [5 6] 7 [8 9] 10 [11 12] 13 14
val task1config = Configuration(listOf(0, 1, 4, 7, 10, 13, 14), listOf(2, 3), listOf(5, 6), listOf(8, 9), listOf(11, 12))

val hallwayIndexes = setOf(0, 1, 4, 7, 10, 13, 14)
val room1Indexes = setOf(2, 3)
val room2Indexes = setOf(5, 6)
val room3Indexes = setOf(8, 9)
val room4Indexes = setOf(11, 12)
val roomIndexes = room1Indexes.union(room2Indexes).union(room3Indexes).union(room4Indexes)

// one of the indices in range is expected to be in the hallway, one in the room
fun distance(between: IntRange): Int {
    val hallwayIndexesBetween = between.subtract(roomIndexes).count()
    val roomsBetween = ceil(between.intersect(roomIndexes).count() / 2.0).toInt()
    val roomMovements = setOf(3,6,9,12).intersect(setOf(between.first, between.last)).count()
    return hallwayIndexesBetween + roomsBetween + roomMovements
}

fun hasValidRoom(index: Int, cellType: CellType): Boolean {
    return ((index == 2 || index == 3) && cellType == CellType.A) ||
            ((index == 5 || index == 6) && cellType == CellType.B) ||
            ((index == 8 || index == 9) && cellType == CellType.C) ||
            ((index == 11 || index == 12) && cellType == CellType.D)
}

data class Field(var locations: Map<Int, CellType>) {
    fun completeRoomPositions(): Int {
        return roomIndexes.fold(0) { acc, index ->
            val lowerPositionComplete = setOf(3, 6, 9, 12).contains(index) && locations[index] != null && hasValidRoom(index, locations[index]!!)
            val upperPositionComplete =  setOf(2, 5, 8, 11).contains(index) && locations[index] != null && hasValidRoom(index, locations[index]!!) &&
                    hasValidRoom(index+1, locations[index+1]!!)

            acc + if (lowerPositionComplete || upperPositionComplete) 1 else 0
        }
    }

    fun canMoveFromRoom(index: Int): Boolean {
        val validRoom = hasValidRoom(index, locations[index]!!)
        fun validUpperPosition(position: Int): Boolean = index == position && (!validRoom || !hasValidRoom(position+1, locations[position+1]!!))
        fun validLowerPosition(position: Int): Boolean = index == position && locations[position-1] == null && !validRoom

        return validUpperPosition(2) || validLowerPosition(3) ||
                validUpperPosition(5) || validLowerPosition(6) ||
                validUpperPosition(8) || validLowerPosition(9) ||
                validUpperPosition(11) || validLowerPosition(12)
    }

    fun canMoveToRoom(index: Int, cellType: CellType): Boolean {
        val validRoom = hasValidRoom(index, cellType)
        val validIndex = index == 3 || (index == 2 && locations[3] != null && hasValidRoom(3, locations[3]!!)) ||
                index == 6 || (index == 5 && locations[6] != null && hasValidRoom(6, locations[6]!!)) ||
                index == 9 || (index == 8 && locations[9] != null && hasValidRoom(9, locations[9]!!)) ||
                index == 12 || (index == 11 && locations[12] != null  && hasValidRoom(12, locations[12]!!))
        return validIndex && validRoom
    }
}

data class Movement(var field: Field, var cost: Int)

fun nextMovementsFor(field: Field): List<Movement> {
    val movements = mutableListOf<Movement>()

    for ((key, value) in field.locations) {
        val hallwayMin = field.locations.keys.filter { hallwayIndexes.contains(it) && it < key }.maxOrNull()?.plus(1) ?: 0
        val hallwayMax = field.locations.keys.filter { hallwayIndexes.contains(it) && it > key }.minOrNull()?.minus(1) ?: 14

        if (hallwayIndexes.contains(key)) {
            val emptyRoomIndexes = (hallwayMin..hallwayMax).intersect(roomIndexes).subtract(field.locations.keys)
                .filter { field.canMoveToRoom(it, value) }
            emptyRoomIndexes.forEach { newIndex ->
                val newLocations = field.locations.toMutableMap()
                val dist = distance(min(key, newIndex)..max(key, newIndex)) * value.stepCost()
                newLocations.remove(key)
                newLocations[newIndex] = value
                movements.add(Movement(Field(newLocations), dist))
            }
        } else if (roomIndexes.contains(key) && field.canMoveFromRoom(key)) {
            val emptyHallwayIndexes = (hallwayMin..hallwayMax).intersect(hallwayIndexes)
            emptyHallwayIndexes.forEach { newIndex ->
                val newLocations = field.locations.toMutableMap()
                val dist = distance(min(key, newIndex)..max(key, newIndex)) * value.stepCost()
                newLocations.remove(key)
                newLocations[newIndex] = value
                movements.add(Movement(Field(newLocations), dist))
            }
        }
    }

   val maxMovements = movements.fold(listOf<Movement>()) { acc, movement ->
        val previousMaxComplete = acc.firstOrNull()?.field?.completeRoomPositions() ?: 0
        if (movement.field.completeRoomPositions() > previousMaxComplete) {
            listOf(movement)
        } else {
            acc + movement
        }
    }
    return maxMovements
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

fun dijkstra(start: Field, graph: Map<Field, List<Movement>>): Int {
    val dist = mutableMapOf<Field, Int>()
    val prev = mutableMapOf<Field, Field>()
    val pQ = PriorityQueue<Field>() { a, b -> dist[a]!! - dist[b]!! }
    var target = Field(mapOf(Pair(2, CellType.A), Pair(3, CellType.A), Pair(5, CellType.B), Pair(6, CellType.B),
        Pair(8, CellType.C), Pair(9, CellType.C), Pair(11, CellType.D), Pair(12, CellType.D)))

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
            }
        }
    }

    return dist[target]!!
}

fun main() {
    fun part1(field: Field): Int {
        val graph = buildFieldStateGraph(field)
        println("graph done, nodes:" + graph.keys.count())
        return dijkstra(field, graph)
    }

    fun part2(input: List<String>): Int {
        return 2
    }

    // test if implementation meets criteria from the description, like:
    val testField = Field(mapOf(Pair(2, CellType.B), Pair(3, CellType.A), Pair(5, CellType.C), Pair(6, CellType.D), Pair(8, CellType.B), Pair(9, CellType.C), Pair(11, CellType.D), Pair(12, CellType.A)))
    //check2(part1(testField), 12521)
    //check2(part2(testInput), 2)

    val field = Field(mapOf(Pair(2, CellType.C), Pair(3, CellType.D), Pair(5, CellType.C), Pair(6, CellType.A), Pair(8, CellType.B), Pair(9, CellType.B), Pair(11, CellType.D), Pair(12, CellType.A)))
    //check2(part1(input), 15299)
    //check2(part2(input), 2)

    println(part1(field))
    //println(part2(input))
}
