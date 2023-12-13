package com.example.mqtt_kotlin.MQTT
import com.example.mqtt_kotlin.MainActivity
import com.example.mqtt_kotlin.Model
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import java.net.InetSocketAddress
import java.util.function.BiConsumer

class HiveMQ {
    private var client: Mqtt3AsyncClient? = null
    private var connectOk = false
    private lateinit var main: MainActivity

    constructor(mainActivity: MainActivity){
        main = mainActivity
    }

    init {
        start()
        connect()
    }

    private fun start(){
        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier(CLIENT_ID)
            .serverAddress(InetSocketAddress.createUnresolved(SERVER_URL, SERVER_PORT))
            .buildAsync()
    }

    private fun connect(){
        client!!.connectWith()
            .willPublish()
            .topic(main.binding.editTextText.text.toString())
            .payload("capteur ref: $CLIENT_ID hs".toByteArray())
            .qos(MqttQos.EXACTLY_ONCE)
            .retain(true)
            .applyWillPublish()
            .send()
            .whenComplete{ mqtt3ConnAck, throwable ->
                if (throwable != null) println("échec connection")
                else {
                    println("connexion OK")
                    connectOk = true
                }
            }

    }

    fun subscribe() {
        client!!.subscribeWith()
            .topicFilter(main.binding.editTextText.text.toString())
            .qos(MqttQos.EXACTLY_ONCE)
            .callback { Mqtt3Publish ->
                //Reception
                main.binding.monBinding = Model(Mqtt3Publish.toString())
            }
            .send()
            .whenComplete { mqtt3SubAck, throwable ->
                if (throwable != null) println("MQTT souscription erreur!")
                else {
                    System.out.println("MQTT souscription Ok -> $SERVER_URL:$SERVER_PORT")
                }
            }
    }

    fun unsubscribe(){
        client!!.unsubscribeWith()
            .topicFilter(main.binding.editTextText.text.toString())
            .send()
    }
}
