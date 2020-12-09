package com.example.lightup

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lightup.databinding.ActivityColorBinding
import kotlinx.android.synthetic.main.activity_color.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.IOException
import java.util.*


private var mDefaultColor:Int = 0
private lateinit var binding : ActivityColorBinding

class ColorActivity : AppCompatActivity() {

    companion object {
        var m_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket?= null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        m_address = intent.getStringExtra(ScanActivity.EXTRA_ADDRESS).toString()
        ConnectToDevice(this).execute()
        binding = ActivityColorBinding.inflate(layoutInflater)


        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary)

        //RainbowChaseButton
        rainbowChaseBtn.setOnClickListener{
            val header = 51 // Equals 3 in char
            val wait = 50
            val color_send: IntArray = intArrayOf(header,wait) //Packets send to Arduino
            sendCommand(color_send)
        }
        //Rainbow Button
        rainbowBtn.setOnClickListener{
            val header = 49 // equals 1
            val wait = 50
            val color_send: IntArray = intArrayOf(header,wait) //Packets send to Arduino
            sendCommand(color_send)
        }
        //Chase Button
        chasingBtn.setOnClickListener{
            var colorPicker: AmbilWarnaDialog = AmbilWarnaDialog(this, mDefaultColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    //Do Nothing
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    mDefaultColor = color
                    color_layout.setBackgroundColor(mDefaultColor)
                    val header = 50 // Equals 2 in char for wipecolor function
                    val red = Color.red(mDefaultColor)
                    val green = Color.green(mDefaultColor)
                    val blue = Color.blue(mDefaultColor)
                    val wait = 50
                    val color_send: IntArray = intArrayOf(header,red,green,blue,wait) //Packets send to Arduino
                    sendCommand(color_send)
                }
            })
            colorPicker.show()
        }

        //Picking Colors Btn
        colorBtn.setOnClickListener{
            var colorPicker: AmbilWarnaDialog = AmbilWarnaDialog(this, mDefaultColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    //Do Nothing
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    mDefaultColor = color
                    color_layout.setBackgroundColor(mDefaultColor)
                    val hexColor =
                        java.lang.String.format("#%06X", 0xFFFFFF and mDefaultColor)
                    status_tv.setText("Current Color: " + hexColor)

                    val header = 48 // Equals 0 in char for wipecolor function
                    val red = Color.red(mDefaultColor)
                    val green = Color.green(mDefaultColor)
                    val blue = Color.blue(mDefaultColor)
                    val color_send: IntArray = intArrayOf(header,red,green,blue) //Packets send to Arduino
                    sendCommand(color_send)
                }
            })
            colorPicker.show()
        }
    }

    private fun sendCommand(input: IntArray) {
        if(m_bluetoothSocket != null){
            try {
                for(i in input){
                    m_bluetoothSocket!!.outputStream.write(i)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun disconnect(){
        if(m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }
    private class ConnectToDevice(ctx: Context) : AsyncTask<Void, Void, String> () {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = ctx
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"Connecting ... ","Please Wait")
        }
        override fun doInBackground(vararg params: Void?): String? {
            try {
                if(m_bluetoothSocket == null || !m_isConnected) {
                  m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                  val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                  m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_UUID)
                  BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                  m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess) {
                Toast.makeText(context, "Couldnt Connect", Toast.LENGTH_SHORT).show()
                Log.i("data","couldn't connect")
                val intent = Intent(context, ScanActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            } else  {
                m_isConnected = true
                Toast.makeText(context, "Connection Success", Toast.LENGTH_SHORT).show()
            }
            m_progress.dismiss()
        }
    }
}