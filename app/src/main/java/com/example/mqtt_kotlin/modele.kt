package com.example.mqtt_kotlin

import com.example.mqtt_kotlin.MQTT.TOPIC

data class modele(var temperature: String, var topic : String = TOPIC)
