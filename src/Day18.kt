sealed class Tree {
    class Leaf(var value: Int) : Tree()
    class Branch(var left: Tree, var right: Tree) : Tree()

    fun str(): String = when (this) {
        is Leaf -> value.toString()
        is Branch -> "[" + left.str() + "," + right.str() + "]"
    }

    fun magnitude(): Int = when (this) {
        is Leaf -> value
        is Branch -> left.magnitude() * 3 + right.magnitude() * 2
    }
}

fun makeTree(string: String): Tree {
    var pendingNodes = mutableListOf<Tree>()

    for (char in string.toCharArray()) {
        if (char in '0'..'9') {
            pendingNodes.add(Tree.Leaf(char.toString().toInt()))
        } else if (char == ']') {
            val right = pendingNodes.removeLast()
            val left = pendingNodes.removeLast()
            pendingNodes.add(Tree.Branch(left, right))
        }
    }

    return pendingNodes.first()
}

fun add(left: Tree, right: Tree): Tree {
    return Tree.Branch(left, right)
}

fun explode(tree: Tree) {
    val stack = mutableListOf<Tree>(tree) // DFS

    var lastVisitedLeaf: Tree.Leaf? = null
    var pendingExplodeRight: Int? = null

    val levels = mutableMapOf<Tree, Int>()
    val parents = mutableMapOf<Tree, Tree.Branch>()
    levels[tree] = 0

    while (stack.isNotEmpty()) {
        val v = stack.removeLast()
        when (v) {
            is Tree.Leaf -> {
                if (pendingExplodeRight != null) {
                    v.value += pendingExplodeRight
                    return // only one change at the time
                }
                lastVisitedLeaf = v
            }
            is Tree.Branch -> {
                if (levels[v] == 4 && pendingExplodeRight == null) {
                    // Explode
                    if (v == parents[v]!!.left) {
                        parents[v]!!.left = Tree.Leaf(0)
                    } else if (v == parents[v]!!.right) {
                        parents[v]!!.right = Tree.Leaf(0)
                    }
                    if (lastVisitedLeaf != null) {
                        lastVisitedLeaf.value += (v.left as Tree.Leaf).value
                    }

                    pendingExplodeRight = (v.right as Tree.Leaf).value
                    continue
                }

                stack.add(v.right)
                stack.add(v.left)
                levels[v.right] = levels[v]!! + 1
                levels[v.left] = levels[v]!! + 1
                parents[v.right] = v
                parents[v.left] = v
            }
        }
    }
}

fun split(tree: Tree) {
    val stack = mutableListOf<Tree>(tree) // DFS
    val parents = mutableMapOf<Tree, Tree.Branch>()

    while (stack.isNotEmpty()) {
        val v = stack.removeLast()
        when (v) {
            is Tree.Leaf -> {
                if (v.value >= 10) {
                    val newNode = Tree.Branch(
                        Tree.Leaf(kotlin.math.floor(v.value /2.0).toInt()),
                        Tree.Leaf(kotlin.math.ceil(v.value / 2.0).toInt())
                    )
                    parents[newNode.left] = newNode
                    parents[newNode.right] = newNode

                    if (v == parents[v]!!.left) {
                        parents[v]!!.left = newNode
                    } else if (v == parents[v]!!.right) {
                        parents[v]!!.right = newNode
                    }
                    return
                }
            }
            is Tree.Branch -> {
                stack.add(v.right)
                stack.add(v.left)
                parents[v.right] = v
                parents[v.left] = v
            }
        }
    }
}

fun loadInput(input: List<String>): Tree {
    var t: Tree? = null
    for (line in input) {
        t = if (t == null) {
            makeTree(line)
        } else {
            add(t, makeTree(line))
        }

        var outerStr: String = ""
        do {
            outerStr = t.str()
            var innerStr = ""
            do {
                innerStr = t.str()
                explode(t)
            } while (innerStr != t.str())

            split(t)
        } while (outerStr != t.str())
    }

    return t!!
}

fun main() {
    fun part1(input: List<String>): Int {
        val t = loadInput(input)
        return t.magnitude()
    }

    fun part2(input: List<String>): Int {
        var maxMagnitude = 0
        for (i in input.indices) {
            for (j in input.indices) {
                if (i == j) continue

                val res = loadInput(listOf(input[i], input[j]))
                maxMagnitude = kotlin.math.max(maxMagnitude, res.magnitude())
            }
        }

        return maxMagnitude
    }

    val testInput = readInput("Day18_test")

    check2(part1(testInput), 4140)
    check2(part2(testInput), 3993)

    val input = readInput("Day18")

    check2(part1(input), 4137)
    check2(part2(input), 4573)
}
