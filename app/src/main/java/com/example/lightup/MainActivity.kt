package com.example.lightup

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lightup.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanBtn.setOnClickListener{
            startActivity(Intent(this,ScanActivity::class.java))
        }
    }
}