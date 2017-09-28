package com.jclngrrd.blynk.library.extension

import org.apache.logging.log4j.Logger

fun Logger.d(getMessage: () -> String) {
    if (isDebugEnabled) {
        debug(getMessage.invoke())
    }
}
