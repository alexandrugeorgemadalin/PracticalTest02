package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;

    private EditText putKeyEditText = null;
    private EditText putValueEditText = null;

    private EditText getKeyEditText = null;
    private TextView getValueTextView = null;
    Button postKeyValueButton = null;
    Button getKeyValueButton = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Could not create server thread!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread.start();
        }
    }

    private clientButtonClickListener clientButtonClickListener = new clientButtonClickListener();

    private class clientButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            String putKey = putKeyEditText.getText().toString();
            String putValue = putValueEditText.getText().toString();
            String getKey= "";
            String operation = "";
            if (clientAddress == null || clientAddress.isEmpty() ||
                    clientPort == null || clientPort.isEmpty() ||
                    putKey == null || putKey.isEmpty() ||
                    putValue == null || putValue.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (view.getId()) {
                case R.id.add_button:
                    operation = "add";
                    break;
                case R.id.get_button:
                    operation = "get";
                    getKey = getKeyEditText.getText().toString();

                    break;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            getValueTextView.setText(Constants.EMPTY_STRING);
            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), putKey, putValue, getKey, getValueTextView, operation
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);


        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        putKeyEditText = findViewById(R.id.client_key_edit_text);
        putValueEditText = findViewById(R.id.client_value_edit_text);
        getKeyEditText = findViewById(R.id.client_get_key_edit_text);
        getValueTextView = findViewById(R.id.client_get_value_edit_text);

        postKeyValueButton = findViewById(R.id.add_button);
        postKeyValueButton.setOnClickListener(clientButtonClickListener);
        getKeyValueButton = findViewById(R.id.get_button);
        getKeyValueButton.setOnClickListener(clientButtonClickListener);

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}