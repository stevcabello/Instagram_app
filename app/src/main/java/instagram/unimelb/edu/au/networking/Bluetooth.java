package instagram.unimelb.edu.au.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import instagram.unimelb.edu.au.MainActivity;
import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.Comments;
import instagram.unimelb.edu.au.models.Likes;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Handles the Bluetooth connection
 */
public class Bluetooth  {

    static String TAG = Bluetooth.class.getSimpleName();

    public static int REQUEST_ENABLE_BT = 1; //To request enabling bluetooth, the number should be larger than 0

    static final int handlerState = 0;      //To identify handler message

    private StringBuilder recDataString = new StringBuilder();

    private static ConnectedThread mConnectedThread;
    private static Activity mActivity;

    // A Universal Unique Identifier, used to identify the application bluetooth service
    // Client and Server should have the same UUID when openning a RFC comm channel
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static ProgressDialog progressDialog;




    /**
     * Checks if the Bluetooth is enabled on the device
     * @return true if enable, false otherwise
     */
    public static void checkBluetoothEnabled(Activity activity, UserFeed userFeed) {

        if (Globals.mBluetoothAdapter == null) {
            //No Bluetooth in the device
            Toast.makeText(activity, "You device does not support Bluetooth",Toast.LENGTH_SHORT).show();
        } else {
            if (!Globals.mBluetoothAdapter.isEnabled()) {
                Toast.makeText(activity, "Please share again in case you accepted enabling Bluetooth...",Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        //In case bluetooth is already enabled then

        mActivity = activity;

        Globals.mPairedDevicesArrAdapter = new ArrayAdapter<String>(activity, R.layout.row_btdevice);

        LayoutInflater inflater = (LayoutInflater)activity.getSystemService
                (activity.LAYOUT_INFLATER_SERVICE);

        View view = (View)inflater.inflate(R.layout.list_btpaireddevices, null,false);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Sharing post with bluetooth-in range devices");
        progressDialog.show();

        //Initiates a 12second window to find nearby devices
        if (Globals.mBluetoothAdapter.isDiscovering()){
            Globals.mBluetoothAdapter.cancelDiscovery();
        }
        Globals.mBluetoothAdapter.startDiscovery(); //To discover new devices around

        queryPairedDevices(); //To fill the paired devices arrayadapter

        progressDialog.dismiss();

        //Just when there are available devices to connect with we show them
        if (Globals.bluetoothDevices.size() > 0)
            displayBTpairedDevices(activity, view, userFeed);
        else
            Toast.makeText(activity,"No devices paired or visible",Toast.LENGTH_SHORT).show();

    }



    /**
     * Ask user permission to send Post over Bluetooth
     * @param activity
     */
    public static void displayPromptForSendingPhoto(final Activity activity, final UserFeed userFeed)
    {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String message = "Do you want to share this post to bluetooth-in range available devices?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                                checkBluetoothEnabled(activity,userFeed);
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
     * Shows the list of paired devices
     * @param activity
     */
    public static void displayBTpairedDevices(final Activity activity, View view, final UserFeed userFeed)
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //Get the list view from the view
        ListView pairedlistView = (ListView)view.findViewById(R.id.lv_btdevices);

        //Set the paired devices array into the listview
        pairedlistView.setAdapter(Globals.mPairedDevicesArrAdapter);


        pairedlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                //With the MAC address we can get the bluetooth device object
                BluetoothDevice bluetoothDevice = Globals.mBluetoothAdapter.getRemoteDevice(address);

                //Send of the user's post will be executed on the ConnectThread
                ConnectThread mconnectthread = new ConnectThread(bluetoothDevice, userFeed);
                mconnectthread.start();
            }
        });
        builder.setView(view);
        builder.create().show();
    }




    /**
     * Adds paired devices to the paired devices adapter and in the BT devices arraylist
     */
    public static void queryPairedDevices() {
        Set<BluetoothDevice> pairedDevices = Globals.mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.i(TAG, device.getAddress()+" " +device.getName());
                Globals.mPairedDevicesArrAdapter.add(device.getName() + "\n" + device.getAddress());
                Globals.bluetoothDevices.add(device);
            }
        }



    }


    /**
     * Check if a discover device isn't already in the paired devices' list
     * @param arrDevices
     * @param deviceName
     * @return true if the device is not in the list, false otherwise.
     */
    public static boolean isDeviceNew(ArrayList<BluetoothDevice> arrDevices, String deviceName){
        for(BluetoothDevice bluetoothDevice : arrDevices) {
            if (bluetoothDevice.getName().equals(deviceName)) return false;
        }
        return true;
    }


    /**
     * Manages the Client-side operation (send of user's post to (server) device
     */
    private static class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private UserFeed mUserFeed;

        public ConnectThread(BluetoothDevice device, UserFeed userFeed) {
            Log.i(TAG, "inside connect thread for device " + device.getName());

            BluetoothSocket tmp = null;
            mmDevice = device;

            mUserFeed = userFeed;

            // get a Bluetooth socket
            try {

                Log.i(TAG, "inside try creating rfcomm for " + device.getName());
                tmp = device.createRfcommSocketToServiceRecord(BTUUID);

                Log.i(TAG, "after socket tmp assignment");
            } catch (IOException e) {
                Log.i(TAG, "Error: " +e.getMessage());
            }
            mmSocket = tmp;

            Log.i(TAG,"after assigning tmp to mmSocket");
        }

        public void run() {

            Log.i(TAG, "before cancel discovery");
            // Cancel discovery because it will slow down the connection
            Globals.mBluetoothAdapter.cancelDiscovery();

            Log.i(TAG, "after cancel discovery");

            try {
                Log.i(TAG,"before socket connect");
                //This will block until the device can be connected through the socket
                mmSocket.connect();
            } catch (IOException connectException) {

                Log.i(TAG,"Is clientSocket open" + String.valueOf(mmSocket != null));
                Log.i(TAG, "Error 2: " + connectException.getMessage());

                String errMessage = "Connecton Failed...be sure remote device has Bluetooth ON";
                MainActivity.bluetoothErr.obtainMessage(handlerState, errMessage).sendToTarget();

                try {
                    Log.i(TAG, "closing socket");
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.i(TAG,"Error 3: " + closeException.getMessage());
                }
                return;
            }

            Log.i(TAG,"before entering to connected thread");

            //A new connection to handle the communication
            mConnectedThread = new ConnectedThread(mmSocket);
            String userFeedJsonStr = UserFeedToJson(mUserFeed);
            mConnectedThread.write(userFeedJsonStr);
            //mConnectedThread.write("x");

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                Log.i(TAG, "connnection canceled");
                mmSocket.close();
            } catch (IOException e) {
            Log.i(TAG, "client connection cancelled due to: " + e.getMessage() );
            }
        }
    }



    /**
     * Opens a lister thread on the device that will act as server (the one that receives the user's post)
     */
    public static class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            Log.i(TAG, "bluetooth server on");
            BluetoothServerSocket tmp = null;
            try {

                tmp = Globals.mBluetoothAdapter.listenUsingRfcommWithServiceRecord("InstagramAppBluetooth", BTUUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.i(TAG,"accepthread run");
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                Log.i(TAG,"listenning for incoming connections");
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                   Log.i(TAG,"server cancelled");
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    Log.i(TAG, "before creating connectedthread");
                    //manage a new Connection
                    new ConnectedThread(socket).start();
                    try {
                        mmServerSocket.close(); //close the socket due to just one connection is allowed per channel
                        new AcceptThread().start(); //to keep listening for incoming connections
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }


        public void cancel() {
            try {
                Log.i(TAG,"Cancel 1");
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }




    /**
     * Parses into String the UserFeed's item
     *
     * @param userFeed
     * @return String representation of the userFeed's item
     */
    private static String UserFeedToJson(UserFeed userFeed) {

        //Create the jsonArray for the Comments
        JSONArray jsonArrayComments = new JSONArray();

        ArrayList<Comments> arrComments = userFeed.getComments();
        for (Comments comment: arrComments) {
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username",comment.getUsername());
                jsonObject.put("text",comment.getText());
                jsonObject.put("created_time",comment.getCreated_time());
                jsonObject.put("profilepic_url",comment.getProfilepic_url());

                jsonArrayComments.put(jsonObject); //Add each jsonobject's comment
            }catch (JSONException e){
                e.printStackTrace();
            }
        }


        //Create the jsonArray for the Likes
        JSONArray jsonArrayLikes = new JSONArray();

        ArrayList<Likes> arrLikes = userFeed.getLikes();
        for (Likes like: arrLikes) {
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username",like.getUsername());
                jsonObject.put("full_name", like.getFull_name());
                jsonObject.put("profilepic_url",like.getProfilepic_url());

                jsonArrayLikes.put(jsonObject); //Add each jsonobject's like
            }catch (JSONException e){
                e.printStackTrace();
            }
        }



        //Create the jsonObject for the userfeed's item
        JSONObject jsonObjectUserFeed = new JSONObject();
        try{
            jsonObjectUserFeed.put("photo_url",userFeed.getPhoto_url());
            jsonObjectUserFeed.put("profilepic_url",userFeed.getProfilepic_url());
            jsonObjectUserFeed.put("username",userFeed.getUsername());

            //Instead of set the original creation time, we set as time the time when
            //user share this post through bluetooth "In Range Post"
            Long current_time = System.currentTimeMillis() / 1000L;
            jsonObjectUserFeed.put("created_time",String.valueOf(current_time));

            jsonObjectUserFeed.put("numlikes",String.valueOf(userFeed.getNumLikes()));
            jsonObjectUserFeed.put("description",userFeed.getDescription());
            jsonObjectUserFeed.put("numcomments",String.valueOf(userFeed.getNumcomments()));
            jsonObjectUserFeed.put("latitude",String.valueOf(userFeed.getLatitude()));
            jsonObjectUserFeed.put("longitude",String.valueOf(userFeed.getLongitude()));
            jsonObjectUserFeed.put("comments",jsonArrayComments);
            jsonObjectUserFeed.put("likes",jsonArrayLikes);
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Convert tje jsonObject to String
        String jsonStr = jsonObjectUserFeed.toString();

        return jsonStr;
    }


    /**
     * Class that Manages a Connection between two devices.
     * Reading from inputstream and writing through outputstream
     */
    public static class ConnectedThread extends Thread {
        private static BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            Log.i(TAG,"Problem in getInOutStream from socket due to: "+ e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;

            Log.i(TAG,"in Connected run");

            while (true) {
                try {
                    // Read from the InputStream
                    Log.i("Bluetooth","before reading");
                    //Blocks until it receives data
                    bytes = mmInStream.read(buffer);
                    Log.i(TAG,"After Reading");

                    String readMessage = new String(buffer, 0, bytes);
                    Log.i(TAG, "Message received: " + readMessage);

                    // Send the obtained bytes to the UI Activity via handler
                    MainActivity.bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    Log.i(TAG,"before break");

                } catch (IOException e) {
                    Log.i(TAG,"Exception listening input stream due to: " + e.getMessage());
                    break;
                }

            }

        }


        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
                Log.i(TAG, "SUCCESS");
                cancel(); //close the socket once it has been used

            } catch (IOException e) {
                //if you cannot write, close the application
                Log.i(TAG,"Connection failure due to: " + e.getMessage());
                Toast.makeText(mActivity, "Connection Failure", Toast.LENGTH_LONG).show();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public static void cancel() {
            try {
                if (mmSocket != null)
                    mmSocket.close();
            } catch (IOException e) {
                Log.i(TAG,"cancel connected thread");
            }
        }
    }

}
