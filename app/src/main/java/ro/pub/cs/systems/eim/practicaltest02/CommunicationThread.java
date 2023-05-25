package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;



public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;


    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public int getTimeStampFromServer() throws IOException, JSONException {
        HttpClient httpClient=new DefaultHttpClient();
        String pageSourceCode="";
        HttpGet httpGet=new HttpGet(Constants.WEB_SERVICE_ADDRESS);
        HttpResponse httpGetResponse=httpClient.execute(httpGet);
        HttpEntity httpGetEntity=httpGetResponse.getEntity();
        if(httpGetEntity!=null){
            pageSourceCode=EntityUtils.toString(httpGetEntity);
        }else{
            Log.e(Constants.TAG,pageSourceCode);
        }

        String unixtimestamp=new JSONObject(pageSourceCode).getString("unixtime");
        return Integer.parseInt(unixtimestamp);
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            String clientResponse = "";
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client");

            String operation = bufferedReader.readLine();
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters: operation = " + operation);
            switch (operation) {
                case "add":

                    String key = bufferedReader.readLine();
                    String value = bufferedReader.readLine();
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters: key = " + key);

                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters: value = " + value);
                    if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / value!");
                        return;
                    }
                    if (serverThread.getData().containsKey(key)) {
                        Log.i(Constants.TAG, "[COMMUNICATION THREAD] Key already exists!");
                    } else {

                        int unixtimestamp = getTimeStampFromServer();

                        Model model = new Model(value, unixtimestamp);
                        serverThread.setData(key, model);
                    }
                    break;
                case "get":
                    String keyGet = bufferedReader.readLine();

                    if (keyGet == null || keyGet.isEmpty()) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / value!");
                        return;
                    }
                    if (serverThread.getData().containsKey(keyGet)) {
                        Model model = serverThread.getData().get(keyGet);
                        int unixtimestamp = getTimeStampFromServer();
                        if(unixtimestamp - model.getUnixTimestamp() > 10) {
                            serverThread.getData().remove(keyGet);
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Key expired!");
                            printWriter.println("expired");
                            printWriter.flush();
                        }else{
                            printWriter.println(model.getValue());
                            printWriter.flush();
                        }
                    } else {

                        Log.i(Constants.TAG, "[COMMUNICATION THREAD] Key= " + keyGet + " does not exist!");
                    }
                    break;
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
