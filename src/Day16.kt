import java.lang.Long.max
import java.lang.Long.min

fun main() {
    fun hexToBin(hex: String): String {
        var res = hex
        res = res.replace("0", "0000");
        res = res.replace("1", "0001");
        res = res.replace("2", "0010");
        res = res.replace("3", "0011");
        res = res.replace("4", "0100");
        res = res.replace("5", "0101");
        res = res.replace("6", "0110");
        res = res.replace("7", "0111");
        res = res.replace("8", "1000");
        res = res.replace("9", "1001");
        res = res.replace("A", "1010");
        res = res.replace("B", "1011");
        res = res.replace("C", "1100");
        res = res.replace("D", "1101");
        res = res.replace("E", "1110");
        res = res.replace("F", "1111");
        return res
    }

    data class Packet(val inputBin: String) {
        // 3 bits
        val version: Int
        // 3 bits
        var type: Int

        var literal: Long? = null

        var subpackets: MutableList<Packet> = mutableListOf()

        var remainder: String = ""

        init {
            var inputBin = inputBin

            version = Integer.parseInt(inputBin.substring(0, 3), 2)
            inputBin = inputBin.drop(3)
            type = Integer.parseInt(inputBin.substring(0, 3), 2)
            inputBin = inputBin.drop(3)

            if (type == 4) {
                var isLast = false
                var number = ""
                while (!isLast) {
                    isLast = inputBin.first() == '0'
                    number += inputBin.substring(1, 5)
                    inputBin = inputBin.drop(5)
                }
                literal = java.lang.Long.parseLong(number, 2)
            } else {
                val length = inputBin.first()
                inputBin = inputBin.drop(1)

                if (length == '0') {
                    val nextTotalLength = Integer.parseInt(inputBin.substring(0, 15), 2)
                    inputBin = inputBin.drop(15)

                    var subpacketBits = inputBin.substring(0, nextTotalLength)
                    while (subpacketBits.any { it != '0' }) {
                        val nextNested = Packet(subpacketBits)
                        subpackets.add(nextNested)
                        subpacketBits = nextNested.remainder
                    }
                    inputBin = inputBin.drop(nextTotalLength)
                } else {
                    var nextCount = Integer.parseInt(inputBin.substring(0, 11), 2)
                    inputBin = inputBin.drop(11)

                    while (nextCount > 0 && inputBin.any { it != '0' }) {
                        val nextNested = Packet(inputBin)
                        subpackets.add(nextNested)
                        inputBin = nextNested.remainder
                        nextCount -= 1
                    }
                }
            }

            remainder = inputBin
        }

        fun versionSum(): Int {
            return version + subpackets.sumOf { it.versionSum() }
        }

        fun value(): Long {
            return when (type) {
                0 -> subpackets.sumOf { it.value() }
                1 -> subpackets.fold(1) { acc, packet -> acc * packet.value() }
                4 -> literal!!.toLong()
                2 -> subpackets.fold(Long.MAX_VALUE) { acc, packet -> min(acc, packet.value()) }
                3 -> subpackets.fold(Long.MIN_VALUE) { acc, packet -> max(acc, packet.value()) }
                5 -> if (subpackets[0]!!.value() > subpackets[1]!!.value()) 1 else 0
                6 -> if (subpackets[0]!!.value() < subpackets[1]!!.value()) 1 else 0
                7 -> if (subpackets[0]!!.value() == subpackets[1]!!.value()) 1 else 0
                else -> 0
            }
        }
    }


    fun part1(input: List<String>): Int {
        val p = Packet(hexToBin("20546C8802538E136091C1802689BCD7DA45948D319D1B100747A009C97696E8B4ABFCA6AB8F4F26C401964A6271C80F802D392C01CEDDCE6E5CB829802F600A00021B14E34C361006E0AC418BB2CA6800BE4599BB6A73507002A52BEEB14D201802F600849E64D3369D37C74100866785B3D0ADFD8E601E5EB9DE2366D93ECB8B040142CB8ACE07CCB5CF34CA89380410B6134CE6FEF104A2B200243396976A00401A45004313D68435DBDDDA61CE6428C01491AEBF0C7E580AE00CCC401B86514216880370EE3443D2013DF003750004361343D88800084C4C8B116A679018300740010C8571BA32080350DA0D42800043A3044189AE0174B314D76E1F3ACF3BDAE3EE7298FF134002EF9DBCD0644127E3CAE7FCBA9A80393544F9A927C973DF1A500965A5CEA94C4DDA5658B94C6C3002A798A629CF21280532BAB4F4C7271E45EE6E71D8143A9BC7948804AB94D1D6006AC200EC1E8A10C00010985316A35C3620061E641644D661A4C012993E99208FC60097802F28F528F738606008CA47205400814C89CC8890064D400AB4BE0A66F2BF253E73AE8401424A7BFB16C0037E06CE0641E0013B08010A8930CE2B980351161DC3730066274188B020054A5E16965940057895ADEB5BF56A635ADE2354191D70566273A6F5B078266008D8022200D46E8291C4401A8CF0CE33CEDE55E9F9802BA00B4BD44A5EA2D10CC00B40316800BAE1003580A6D6026F00090E50024007C9500258068850035C00A4012ED8040B400D71002AF500284009700226336CA4980471D655E25D4650888023AB00525CAE5CBA5E428600BE003993778CB4732996E9887AE3F311C291004BD37517C0041E780A7808802AF8C1C00D0CDBE4ACAD69B3B004E13BDF23CAE7368C9F62448F64546008E0034F3720192A67AD9254917454200DCE801C99ADF182575BBAACAC7F8580"))
        return p.versionSum()
    }

    fun part2(input: List<String>): Long {
        val p = Packet(hexToBin("20546C8802538E136091C1802689BCD7DA45948D319D1B100747A009C97696E8B4ABFCA6AB8F4F26C401964A6271C80F802D392C01CEDDCE6E5CB829802F600A00021B14E34C361006E0AC418BB2CA6800BE4599BB6A73507002A52BEEB14D201802F600849E64D3369D37C74100866785B3D0ADFD8E601E5EB9DE2366D93ECB8B040142CB8ACE07CCB5CF34CA89380410B6134CE6FEF104A2B200243396976A00401A45004313D68435DBDDDA61CE6428C01491AEBF0C7E580AE00CCC401B86514216880370EE3443D2013DF003750004361343D88800084C4C8B116A679018300740010C8571BA32080350DA0D42800043A3044189AE0174B314D76E1F3ACF3BDAE3EE7298FF134002EF9DBCD0644127E3CAE7FCBA9A80393544F9A927C973DF1A500965A5CEA94C4DDA5658B94C6C3002A798A629CF21280532BAB4F4C7271E45EE6E71D8143A9BC7948804AB94D1D6006AC200EC1E8A10C00010985316A35C3620061E641644D661A4C012993E99208FC60097802F28F528F738606008CA47205400814C89CC8890064D400AB4BE0A66F2BF253E73AE8401424A7BFB16C0037E06CE0641E0013B08010A8930CE2B980351161DC3730066274188B020054A5E16965940057895ADEB5BF56A635ADE2354191D70566273A6F5B078266008D8022200D46E8291C4401A8CF0CE33CEDE55E9F9802BA00B4BD44A5EA2D10CC00B40316800BAE1003580A6D6026F00090E50024007C9500258068850035C00A4012ED8040B400D71002AF500284009700226336CA4980471D655E25D4650888023AB00525CAE5CBA5E428600BE003993778CB4732996E9887AE3F311C291004BD37517C0041E780A7808802AF8C1C00D0CDBE4ACAD69B3B004E13BDF23CAE7368C9F62448F64546008E0034F3720192A67AD9254917454200DCE801C99ADF182575BBAACAC7F8580"))
        return p.value()
    }

    val testInput = readInput("Day16_test")

    //check2(part1(testInput), 1)
    check2(part2(testInput), 2)

    //val input = readInput("Day16")

    //println(part1(input))
    //println(part2(input))
}
