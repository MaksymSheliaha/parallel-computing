import util.Constants.*
import util.Util.readMatrix

import util.sendIntWithAck
import util.sendMatrixWithAck
import util.waitUntilReady

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.random.Random

fun main() {

    val rows = ROWS
    val cols = COLS
    val k = K
    val threadNum = THREAD_NUM

    val matrixA = generateMatrix(rows, cols)
    val matrixB = generateMatrix(rows, cols)

    Socket(SERVER_HOST, SERVER_PORT).use { socket ->

        val input = DataInputStream(socket.getInputStream())
        val output = DataOutputStream(socket.getOutputStream())

        makeRequest(rows, cols, k, threadNum, matrixA, matrixB, input, output)
    }
}

fun generateMatrix(rows: Int, cols: Int): Array<IntArray> =
    Array(rows) { IntArray(cols) { Random.nextInt(100) } }


fun makeRequest(
    rows: Int,
    cols: Int,
    k: Int,
    threadNum: Int,
    matrixA: Array<IntArray>,
    matrixB: Array<IntArray>,
    input: DataInputStream,
    output: DataOutputStream
) {

    output.writeByte(CONNECT.toInt())

    val connection = input.readByte()

    when (connection) {
        DISCONNECT -> return
        CONNECT -> println("Connection established")
        else -> error("Unexpected status")
    }

    sendIntWithAck(rows, output, input, "Rows")
    sendIntWithAck(cols, output, input, "Cols")

    sendMatrixWithAck(matrixA, output, input, "Matrix A")
    sendMatrixWithAck(matrixB, output, input, "Matrix B")

    sendIntWithAck(k, output, input, "K")
    sendIntWithAck(threadNum, output, input, "Thread num")

    waitUntilReady(input, output)

    val result = readMatrix(rows, cols, input, output)
    println("Result received")
    //printResults(matrixA, matrixB, k, result)
}

fun printResults(a: Array<IntArray>, b: Array<IntArray>, k: Int, result: Array<IntArray>) {

    printMatrix(a)
    println("+")
    println("$k * ")
    printMatrix(b)
    println("=")
    printMatrix(result)
}

fun printMatrix(matrix: Array<IntArray>) =
    matrix.forEach { println(it.joinToString(prefix = "[", postfix = "]")) }