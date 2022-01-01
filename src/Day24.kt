import kotlin.math.abs

sealed class Instruction {
    class Inp(val to: String) : Instruction()
    class Add(val to: String, val from: String) : Instruction()
    class Mul(val to: String, val from: String) : Instruction()
    class Div(val to: String, val from: String) : Instruction()
    class Mod(val to: String, val from: String) : Instruction()
    class Eql(val to: String, val from: String) : Instruction()
}

class Interpreter(val instrutions: List<Instruction>, val input: String) {
    var registers = mutableMapOf<String, Int>(Pair("w", 0), Pair("x", 0), Pair("y", 0), Pair("z", 0))

    fun setRegister(key: String, value: Int) {
        registers[key] = value
    }

    fun execute() {
        val inputIterator = input.iterator()
        for (instr in instrutions) {
            when (instr) {
                is Instruction.Inp -> setRegister(instr.to, inputIterator.next().digitToInt())
                is Instruction.Add -> setRegister(instr.to, registers[instr.to]!! + registers.getOrElse(instr.from) { instr.from.toInt() })
                is Instruction.Mul -> setRegister(instr.to, registers[instr.to]!! * registers.getOrElse(instr.from) { instr.from.toInt() })
                is Instruction.Div -> setRegister(instr.to, registers[instr.to]!! / registers.getOrElse(instr.from) { instr.from.toInt() })
                is Instruction.Mod -> setRegister(instr.to, registers[instr.to]!! % registers.getOrElse(instr.from) { instr.from.toInt() })
                is Instruction.Eql -> setRegister(instr.to, if (registers[instr.to]!! == registers.getOrElse(instr.from) { instr.from.toInt() }) 1 else 0)
            }
        }
    }
}

fun run(number: String): List<Boolean> {
    var z = 0

    var l4 = listOf(1, 1, 1, 26, 1, 1, 1, 26, 1, 26, 26, 26, 26, 26)
    var l5 = listOf(10, 12, 15, -9, 15, 10, 14, -5, 5, -7, -12, -10, -1, -11)
    var l15 = listOf(15, 8, 2, 6, 13, 4, 1, 9, 5, 13, 9, 6, 2, 2)

    var iter = 0
    var list = mutableListOf<Boolean>()
    for (ch in number) {
        val w = ch.digitToInt()
        val x = z % 26 + l5[iter] - w != 0
        z /= l4[iter]
        if (x) {
            z *= 26
            z += w + l15[iter]
        }
        
        list.add(!(x && l4[iter] == 26))
        iter += 1
    }

    return list
}

fun loadInstructions(input: List<String>): List<Instruction> {
    return input.mapNotNull {
        val parts = it.split(" ")
        when (parts[0]) {
            "inp" -> Instruction.Inp(parts[1])
            "add" -> Instruction.Add(parts[1], parts[2])
            "mul" -> Instruction.Mul(parts[1], parts[2])
            "div" -> Instruction.Div(parts[1], parts[2])
            "mod" -> Instruction.Mod(parts[1], parts[2])
            "eql" -> Instruction.Eql(parts[1], parts[2])
            else -> null
        }
    }
}

fun main(args: Array<String>) {
    fun part1(): Long {
        var num = MutableList(14) { 9 }
        var res = run(num.fold("") { acc, i -> acc + i.toString() })
        while (!res.all { it }) {
            var firstWrong = res.indexOfFirst { !it }
            while (num[firstWrong] == 1) {
                num[firstWrong] = 9
                firstWrong -= 1
            }
            num[firstWrong] -= 1

            res = run(num.fold("") { acc, i -> acc + i.toString() })
        }

        return num.fold("") { acc, i -> acc + i.toString() }.toLong()
    }

    fun part2(): Long {
        var num = MutableList(14) { 9 }
        var res = run(num.fold("") { acc, i -> acc + i.toString() })
        while (!res.all { it }) {
            var firstWrong = res.indexOfFirst { !it }
            while (num[firstWrong] == 9) {
                num[firstWrong] = 1
                firstWrong -= 1
            }
            num[firstWrong] += 1

            res = run(num.fold("") { acc, i -> acc + i.toString() })
        }

        return num.fold("") { acc, i -> acc + i.toString() }.toLong()
    }

    check2(part1(), 52926995971999)
    check2(part2(), 11811951311485)
}
