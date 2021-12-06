import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.contracts.contract

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun check2(lhs: Int, rhs: Int): Unit {
    check(lhs == rhs) { "Check failed. Wanted ${rhs}, got ${lhs}." }
}

fun check2(lhs: Long, rhs: Long): Unit {
    check(lhs == rhs) { "Check failed. Wanted ${rhs}, got ${lhs}." }
}
