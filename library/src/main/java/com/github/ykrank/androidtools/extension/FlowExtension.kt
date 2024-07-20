package com.github.ykrank.androidtools.extension

import android.annotation.SuppressLint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.util.Timer
import java.util.TimerTask

fun <T> Flow<T>.throttleFirst(duration: Long = 1000L) = this.throttleFirstImpl(duration)

fun <T> Flow<T>.throttleLatest(duration: Long = 1000L) = this.throttleLatestImpl(duration)

fun <T> Flow<T>.throttleFirstImpl(periodMillis: Long): Flow<T> {
    return flow {
        var lastTime = 0L
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= periodMillis) {
                lastTime = currentTime
                emit(value)
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
internal fun <T> Flow<T>.throttleLatestImpl(periodMillis: Long): Flow<T> {
    return channelFlow {
        var lastValue: T?
        var timer: Timer? = null
        onCompletion { timer?.cancel() }
        collect { value ->
            lastValue = value
            if (timer == null) {
                timer = Timer()
                timer?.scheduleAtFixedRate(
                    object : TimerTask() {
                        override fun run() {
                            val value1 = lastValue
                            lastValue = null
                            if (value1 != null) {
                                launch {
                                    send(value1 as T)
                                }
                            } else {
                                timer?.cancel()
                                timer = null
                            }
                        }
                    },
                    0,
                    periodMillis
                )
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> Flow<T>.throttleLatestKotlin(periodMillis: Long): Flow<T> {
    require(periodMillis > 0) { "period should be positive" }

    return channelFlow {
        val done = Any()
        val values = produce(capacity = Channel.CONFLATED) {
            collect { value -> send(value) }
        }

        var lastValue: Any? = null
        val ticker = Ticker(periodMillis)
        while (lastValue !== done) {
            select<Unit> {
                values.onReceiveCatching {
                    val value = it.getOrNull()
                    if (value == null) {
                        ticker.cancel()
                        lastValue = done
                    } else {
                        lastValue = value
                        if (!ticker.isStarted) {
                            ticker.start(this@channelFlow)
                        }
                    }

                }

                ticker.getTicker().onReceive {
                    if (lastValue !== null) {
                        val value = lastValue
                        lastValue = null
                        send(value as T)
                    } else {
                        ticker.stop()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class Ticker(private val delay: Long) {

    private var channel: ReceiveChannel<Unit> = Channel()

    var isStarted: Boolean = false
        private set

    fun getTicker(): ReceiveChannel<Unit> {
        return channel
    }

    fun start(scope: CoroutineScope) {
        isStarted = true
        channel.cancel()
        channel = scope.produce(capacity = 0) {
            while (true) {
                channel.send(Unit)
                delay(delay)
            }
        }
    }

    fun stop() {
        isStarted = false
        channel.cancel()
        channel = Channel()
    }

    fun cancel() {
        isStarted = false
        channel.cancel()
    }
}