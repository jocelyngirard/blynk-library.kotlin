package com.jclngrrd.blynk.library

import com.jclngrrd.blynk.library.extension.write
import org.junit.Test

class BlynkTest {

    private var x: Int = 0
    private var y: Int = 0

    @Test
    fun invalidToken() {
        Blynk("invalidToken").connect()
    }

    @Test
    fun connect() { // 238a68c301d44b18a270ee59cf90d867 -- Prod
        val blynk = Blynk("b5421adce7474b7caaa8ba7f73b0b234", "localhost")

        val v0 = blynk.virtualPin(0)
        v0.onRead = { "read: $x $y" }

        val v1 = blynk.virtualPin(1)
        v1.onWrite = { value ->
            v0.write("V1 = $value")
        }

        val v2 = blynk.virtualPin(2)
        val v6 = blynk.virtualPin(6)
        v2.onRead = { "128" }
        v2.onWrite = { value ->
            println("V2 values = $value")
            v6.write(value?.first())
        }

        val v4 = blynk.virtualPin(4)
        v4.onWrite = { value ->
            when (value) {
                is List<*> -> {
                    println("V$ ---> ${value.joinToString()}")
                }
            }
        }



        blynk.connect()
    }

}
