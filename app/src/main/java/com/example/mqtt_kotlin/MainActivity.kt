package com.example.mqtt_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.mqtt_kotlin.MQTT.HiveMQ
import com.example.mqtt_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var subscriber = HiveMQ(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        var flag = false
        binding.buttonSubscribe.setOnClickListener{
            if (!flag && binding.editTextText.text!=null){
                subscriber.subscribe(binding.editTextText.text.toString())
            }
        }
    }
}