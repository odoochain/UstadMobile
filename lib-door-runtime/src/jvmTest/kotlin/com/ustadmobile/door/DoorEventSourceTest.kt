package com.ustadmobile.door

import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier
import com.nhaarman.mockitokotlin2.argWhere
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.timeout
import com.nhaarman.mockitokotlin2.verify
import com.ustadmobile.door.sse.DoorEventListener
import com.ustadmobile.door.sse.DoorEventSource
import com.ustadmobile.door.sse.DoorServerSentEvent
import io.ktor.application.call
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Test

class DoorEventSourceTest {

    @Test
    fun givenEventSourceCreated_whenEventSent_thenOnMessageIsCalled() {
        if(!Napier.isEnable(Napier.Level.DEBUG, null)) {
            Napier.base(DebugAntilog())
        }


        val eventChannel = Channel<DoorServerSentEvent>(Channel.UNLIMITED)

        val testServer = embeddedServer(Netty, 8094) {
            routing {
                get("subscribe") {
                    call.respondTextWriter(contentType = io.ktor.http.ContentType.Text.EventStream) {
                        for(notification in eventChannel) {
                            write("id: ${notification.id}\n")
                            write("event: ${notification.event}\n")
                            notification.data.lines().forEach { line ->
                                write("data: $line\n")
                            }
                            write("\n")
                            flush()
                        }
                    }

                }
            }
        }.also {
            it.start()
        }

        val eventListener = mock<DoorEventListener> {}

        val eventSource = DoorEventSource("http://localhost:8094/subscribe", eventListener)
        eventChannel.offer(DoorServerSentEvent("42", "UPDATE", "Hello World"))
        verify(eventListener, timeout(5000)).onMessage(argWhere { it.id == "42" })

        GlobalScope.launch {
            delay(10000)
            eventChannel.offer(DoorServerSentEvent("50", "UPDATE", "Hello World"))
        }

        verify(eventListener, timeout(20000)).onMessage(argWhere { it.id == "50" })
        eventSource.close()

        testServer.stop(5000, 5000)
    }

}