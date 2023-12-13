package com.example.mqtt_kotlin
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
            .topic(TOPIC)
            .payload("capteur ref: $CLIENT_ID hs".encodeToByteArray())
            .qos(MqttQos.EXACTLY_ONCE)
            .retain(true)
            .applyWillPublish()
            .send()
            .whenComplete(BiConsumer<Mqtt3ConnAck, Throwable> { mqtt3ConnAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) println("échec connection")
                else {
                    println("connexion OK")
                    connectOk = true
                }
            })

    }

    fun subscribe() {
        client!!.subscribeWith()
            .topicFilter(TOPIC)
            .qos(MqttQos.EXACTLY_ONCE)
            .callback { mqtt3Publish: Mqtt3Publish ->
                //Reception
                val value = String(mqtt3Publish.payloadAsBytes)
                println(value)
            }
            .send()
            .whenComplete { mqtt3SubAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) println("MQTT souscription erreur!")
                else {
                    System.out.println("MQTT souscription Ok -> $SERVER_URL:$SERVER_PORT")
                }
            }
    }

    fun publish(message: String) {
        if (connectOk) {
            client!!.publishWith()
                .topic(TOPIC)
                .payload(message.toByteArray())
                .qos(MqttQos.EXACTLY_ONCE)
                .send()
                .whenComplete { mqtt3Publish: Mqtt3Publish?, throwable: Throwable? ->
                    if (throwable != null) {
                        println("échec de la configuration de la publication")
                    } else {
                        System.out.println("publication ok -> $SERVER_URL:$SERVER_PORT")
                    }
                }
        }
    }
}
