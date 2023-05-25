package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
    private String address;
    private int port;
    private String postKey;
    private String postValue;

    private String getKey;
    private TextView getValue;
    private Socket socket;
    private String operation;

    public ClientThread(String address, int port, String postKey, String postValue, String getKey, TextView getValue, String operation)  {
        this.address = address;
        this.port = port;
        this.postKey = postKey;
        this.postValue = postValue;
        this.getValue = getValue;
        this.operation = operation;
        this.getKey = getKey;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(operation);
            printWriter.flush();
            switch (operation) {
                case "add":
                    printWriter.println(postKey);
                    printWriter.flush();
                    printWriter.println(postValue);
                    printWriter.flush();
                    break;
                case "get":
                    printWriter.println(getKey);
                    printWriter.flush();
                    String wordDefinitionInformation = bufferedReader.readLine();
                    getValue.post(() -> getValue.setText(wordDefinitionInformation));
                    break;
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
