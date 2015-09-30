package instagram.unimelb.edu.au.networking;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import instagram.unimelb.edu.au.utils.Globals;

/**
 * Handles the Bluetooth connection
 */
public class Bluetooth  {

    static String TAG = Bluetooth.class.getSimpleName();
    static BluetoothAdapter mBluetoothAdapter = null;
    //static HashMap<String,String> mapPairedDevices = new HashMap<>();
    public static int REQUEST_ENABLE_BT = 1;
    static Handler bluetoothIn;

    static final int handlerState = 0;        				 //used to identify handler message
    private static BluetoothSocket mBluetoothSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private static ConnectedThread mConnectedThread;
    private static Activity mActivity;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static ProgressDialog progressDialog;
    // String for MAC address
   // private static String address;



    /**
     * Checks if the Bluetooth is enabled on the device
     * @return true if enable false otherwise
     */
    public static void checkBluetoothEnabled(Activity activity) {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //No Bluetooth in the device
            Toast.makeText(activity, "You device does not support Bluetooth",Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(activity, "Please share again in case you accepted enabling Bluetooth...",Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        //In case bluetooth is already enabled then
        mActivity = activity;


        //handleIn();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Sharing post with bluetooth-in range devices");
        progressDialog.show();

        queryPairedDevices();

        for(Map.Entry<String, String> device : Globals.mapPairedDevices.entrySet()) {
            String key = device.getKey();
            String value = device.getValue();

            Log.i(TAG,"in device" + value);

            startConnection(key);
        }

        progressDialog.dismiss();

        mConnectedThread.write("hola");


    }



    /**
     * Ask user permission to send Post over Bluetooth
     * @param activity
     */
    public static void displayPromptForSendingPhoto(final Activity activity)
    {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String message = "Do you want to share this post to bluetooth-in range available devices?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                checkBluetoothEnabled(activity);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }




    /**
     * Adds paired devices to the Map mapPairedDevices
     */
    public static void queryPairedDevices() {

        Log.i(TAG, "in querypaireddevices");

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.i(TAG, "recovering devices");
                Globals.mapPairedDevices.put(device.getAddress(), device.getName());
            }
        }



    }




    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        ParcelUuid list[] = device.getUuids();

        return device.createRfcommSocketToServiceRecord(list[0].getUuid());
        //return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }


    public static void startConnection(String address){

        //create device and set the MAC address
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try {
            mBluetoothSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(mActivity, "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            mBluetoothSocket.connect();
        } catch (IOException e) {
            try
            {
                mBluetoothSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(mBluetoothSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("x");


    }


    public static void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) { }
    }


////    private class AcceptThread extends Thread {
////        private final BluetoothServerSocket mmServerSocket;
////
////        public AcceptThread() {
////            // Use a temporary object that is later assigned to mmServerSocket,
////            // because mmServerSocket is final
////            BluetoothServerSocket tmp = null;
////            try {
////                // MY_UUID is the app's UUID string, also used by the client code
////                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
////            } catch (IOException e) { }
////            mmServerSocket = tmp;
////        }
////
////        public void run() {
////            BluetoothSocket socket = null;
////            // Keep listening until exception occurs or a socket is returned
////            while (true) {
////                try {
////                    socket = mmServerSocket.accept();
////                } catch (IOException e) {
////                    break;
////                }
////                // If a connection was accepted
////                if (socket != null) {
////                    // Do work to manage the connection (in a separate thread)
////                    manageConnectedSocket(socket);
////                    try {
////                        mmServerSocket.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////                }
////            }
////        }
////
////        /** Will cancel the listening socket, and cause the thread to finish */
////        public void cancel() {
////            try {
////                mmServerSocket.close();
////            } catch (IOException e) { }
////        }
////    }
//
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//
//        public ConnectThread(BluetoothDevice device) {
//            // Use a temporary object that is later assigned to mmSocket,
//            // because mmSocket is final
//            BluetoothSocket tmp = null;
//            mmDevice = device;
//
//            // Get a BluetoothSocket to connect with the given BluetoothDevice
//            try {
//                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) { }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            // Cancel discovery because it will slow down the connection
//            mBluetoothAdapter.cancelDiscovery();
//
//            try {
//                // Connect the device through the socket. This will block
//                // until it succeeds or throws an exception
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                // Unable to connect; close the socket and get out
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) { }
//                return;
//            }
//
//            // Do work to manage the connection (in a separate thread)
//            manageConnectedSocket(mmSocket);
//        }
//
//        /** Will cancel an in-progress connection, and close the socket */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//    }


//    private static void handleIn(){
//        bluetoothIn = new Handler() {
//            public void handleMessage(android.os.Message msg) {
//                if (msg.what == handlerState) {										//if message is what we want
//                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
//                    recDataString.append(readMessage);      								//keep appending to string until ~
//                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
//                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
//                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
//                       // txtString.setText("Data Received = " + dataInPrint);
//                        int dataLength = dataInPrint.length();							//get length of data received
//                        //txtStringLength.setText("String Length = " + String.valueOf(dataLength));
//
//                        if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
//                        {
//                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
//                            String sensor1 = recDataString.substring(6, 10);            //same again...
//                            String sensor2 = recDataString.substring(11, 15);
//                            String sensor3 = recDataString.substring(16, 20);
//
////                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");	//update the textviews with sensor values
////                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
////                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
////                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
//                        }
//                        recDataString.delete(0, recDataString.length()); 					//clear all string data
//                        // strIncom =" ";
//                        dataInPrint = " ";
//                    }
//                }
//            }
//        };
//
//    }


    public static class ConnectedThread extends Thread {
        private static BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();

                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    //bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    Log.i(TAG,readMessage);
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) { }
//        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
                Toast.makeText(mActivity, "succesull at writing " + input, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(mActivity, "Connection Failure", Toast.LENGTH_LONG).show();
                //finish();

            }
        }

        /* Call this from the main activity to shutdown the connection */
        public static void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
