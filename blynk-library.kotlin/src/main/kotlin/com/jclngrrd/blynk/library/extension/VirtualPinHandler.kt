package com.jclngrrd.blynk.library.extension

import com.jclngrrd.blynk.library.pin.VirtualPin

fun VirtualPin.write(value: Any?) = this.write(this.pin, value)