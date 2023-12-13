package com.example.mqtt_kotlin.MQTT

import java.util.UUID

val SERVER_URL="10.0.0.10"
val SERVER_PORT=1883
val CLIENT_ID = UUID.randomUUID().toString()
var TOPIC = "temp/b212/mortreux"