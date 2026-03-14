package util

import util.Constants.*
import util.Util.sendMatrix
import java.io.DataInputStream
import java.io.DataOutputStream


fun sendIntWithAck(value: Int, output: DataOutputStream, input: DataInputStream, name: String) {
    do {
        output.writeInt(value)
    } while (input.readByte() != RECEIVED)

    println("$name sent: $value")
}


fun sendMatrixWithAck(matrix: Array<IntArray>, output: DataOutputStream, input: DataInputStream, name: String) {
    do {
        sendMatrix(matrix, output)
    } while (input.readByte() != RECEIVED)

    println("$name sent")
}


fun waitUntilReady(input: DataInputStream, output: DataOutputStream) {

    while (true) {

        Thread.sleep(500)

        output.writeByte(STATUS.toInt())

        when (input.readByte()) {
            READY -> return
            NOT_READY -> continue
            else -> error("Invalid status")
        }
    }
}