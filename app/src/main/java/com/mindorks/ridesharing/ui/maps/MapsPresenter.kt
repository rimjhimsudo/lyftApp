package com.mindorks.ridesharing.ui.maps

import android.util.Log
import com.google.maps.model.LatLng
import com.google.maps.model.TransitAgency
import com.mindorks.ridesharing.data.network.NetworkService
import com.mindorks.ridesharing.simulator.WebSocket
import com.mindorks.ridesharing.simulator.WebSocketListener
import org.json.JSONObject

class MapsPresenter(private val networkService: NetworkService):WebSocketListener{

    companion object{
        private  const val TAG="MapsPresenter"
    }
    private var view:MapsView?=null
    private  lateinit var  webSocket: WebSocket //coz ths can never be null

    fun onAttach(view: MapsView) {
        this.view=view
        webSocket=networkService.createwebsocket(this)
        webSocket.connect()
    }
    fun reqNearByCabs(latLng: LatLng){
        val jsonobject=JSONObject()
        jsonobject.put()
    }
    fun onDetach(){
        webSocket.disconnect()
        view=null
    }
    override fun onConnect() {
        Log.d(TAG,"onConnect")
    }

    override fun onMessage(data: String) {
        Log.d(TAG,"on message:$data")

    }

    override fun onDisconnect() {
        Log.d(TAG,"onDisConnect")

    }

    override fun onError(error: String) {
        Log.d(TAG,"$error")

    }

}

//holds the model and vew.data can hpppen to vew through presenter
