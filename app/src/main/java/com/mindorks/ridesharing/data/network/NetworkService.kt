package com.mindorks.ridesharing.data.network

import com.mindorks.ridesharing.simulator.WebSocket
import com.mindorks.ridesharing.simulator.WebSocketListener

class NetworkService {
    //provnf web socket
    fun createwebsocket(webSocketListener: WebSocketListener) : WebSocket{

        return WebSocket(webSocketListener)
    }

}