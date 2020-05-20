package com.mindorks.ridesharing.ui.maps

import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.model.TransitAgency
import com.mindorks.ridesharing.data.network.NetworkService
import com.mindorks.ridesharing.simulator.WebSocket
import com.mindorks.ridesharing.simulator.WebSocketListener
import com.mindorks.ridesharing.utils.Constants
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
        jsonobject.put(Constants.TYPE,Constants.NEAR_BY_CABS)
        jsonobject.put(Constants.LAT,latLng.latitude)
        jsonobject.put(Constants.LNG,latLng.longitude)
        webSocket.sendMessage(jsonobject.toString())

    }

    fun requestCab(pickUpLatLng: LatLng, dropLatLng: LatLng) {
        val jsonObject = JSONObject()
        jsonObject.put("type", "requestCab")
        jsonObject.put("pickUpLat", pickUpLatLng.latitude)
        jsonObject.put("pickUpLng", pickUpLatLng.longitude)
        jsonObject.put("dropLat", dropLatLng.latitude)
        jsonObject.put("dropLng", dropLatLng.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }
    private fun handleOnMessageNearbyCabs(jsonObject: JSONObject) {
        val nearbyCabLocations = arrayListOf<LatLng>()
        val jsonArray = jsonObject.getJSONArray(Constants.LOCATIONS)
        for (i in 0 until jsonArray.length()) {
            val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
            val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
            val latLng = LatLng(lat, lng)
            nearbyCabLocations.add(latLng)
        }
        view?.showNearByCabs(nearbyCabLocations)
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
        val jsonObject = JSONObject(data)
        when (jsonObject.getString(Constants.TYPE)) {
            Constants.NEAR_BY_CABS -> {
                handleOnMessageNearbyCabs(jsonObject)
            }
            Constants.CAB_BOOKED->{
                view?.informCabBooked()
            }
            Constants.PICKUP_PATH->{
                val jsonArray = jsonObject.getJSONArray("path")
                val pickUpPath = arrayListOf<LatLng>()
                for (i in 0 until jsonArray.length()) {
                    val lat = (jsonArray.get(i) as JSONObject).getDouble("lat")
                    val lng = (jsonArray.get(i) as JSONObject).getDouble("lng")
                    val latLng = LatLng(lat, lng)
                    pickUpPath.add(latLng)
                }
                view?.showPath(pickUpPath)
            }
            Constants.LOCATION -> {
                val latCurrent = jsonObject.getDouble("lat")
                val lngCurrent = jsonObject.getDouble("lng")
                view?.updateCabLocation(LatLng(latCurrent, lngCurrent))
            }
            Constants.CAB_IS_ARRIVING -> {
                view?.informCabIsArriving()
            }
            Constants.CAB_ARRIVED -> {
                view?.informCabArrived()
            }
            Constants.TRIP_START -> {
                view?.informTripStart()
            }
            Constants.TRIP_END -> {
                view?.informTripEnd()
            }

        }
    }


    override fun onDisconnect() {
        Log.d(TAG,"onDisConnect")

    }

    override fun onError(error: String) {
        Log.d(TAG,"$error")

    }

}

//holds the model and vew.data can hpppen to vew through presenter
