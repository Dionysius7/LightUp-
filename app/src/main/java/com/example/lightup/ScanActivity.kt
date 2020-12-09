package com.example.lightup

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lightup.databinding.ActivityScanBinding
import kotlinx.android.synthetic.main.activity_scan.*
import java.util.*


class ScanActivity : AppCompatActivity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mPairedDevice: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        //Define variables that you want to access from other classes
        //We'll be using this var as key for next page
        val EXTRA_ADDRESS: String = "Device_Address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        refreshBtn.setOnClickListener{
            finish();
            startActivity(getIntent());
        }
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "This Device doesn't support bluetooth connection", Toast.LENGTH_SHORT).show()
            return
        }
        if(!mBluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
        }
            pairedDeviceList()
    }
    private fun pairedDeviceList() {
        mPairedDevice = mBluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList ()

        if(!mPairedDevice.isEmpty()){
            for(device: BluetoothDevice in mPairedDevice){
                list.add(device)
                Log.i("device",""+device)
            }
        } else {
            Toast.makeText(this, "No paired bluetooth devices found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        connect_lv.adapter = adapter
        connect_lv.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this,ColorActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode == Activity.RESULT_OK){
                if(mBluetoothAdapter!!.isEnabled){
                    Toast.makeText(this, "Bluetooth has been  enabled", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "Bluetooth has been disabled", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Bluetooth enabling has been canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}