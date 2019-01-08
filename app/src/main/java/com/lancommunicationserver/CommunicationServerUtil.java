package com.lancommunicationserver;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationServerUtil {
    private static final int socketPort = 8888;
    private SocketAcceptThread mSocketAcceptThread;
    private mIOnAcceptMessListener mIOnAcceptMessListener;

    public CommunicationServerUtil(mIOnAcceptMessListener l) {
        this.mIOnAcceptMessListener = l;
    }

    public interface mIOnAcceptMessListener {
        void onMess(String mess);
    }

    public void openSocket() {

        if (null == this.mSocketAcceptThread) {
            this.mSocketAcceptThread = new SocketAcceptThread();
            this.mSocketAcceptThread.start();
        } else {
            this.mSocketAcceptThread.interrupt();
            this.mSocketAcceptThread.start();
        }
    }

    class SocketAcceptThread extends Thread {
        @Override
        public void run() {
            ServerSocket mServerSocket = null;
            try {
                mServerSocket = new ServerSocket(socketPort);
            } catch (IOException e) {
                Log.e("tag", "server fail");
            }
            while (true) {
                Socket mSocket;
                try {
                    Log.e("tag", "accept await");
                    mSocket = mServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("tag", "run accept error");
                    return;
                }
                if (null != mSocket) {
                    Log.e("tag", "accept success");
                    startReader(mSocket);
                }
            }
        }
    }

    private void startReader(final Socket socket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream reader;
                try {
                    reader = new DataInputStream(socket.getInputStream());
                    while (true) {
                        String msg = reader.readUTF();
                        Message message = handler.obtainMessage();
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                } catch (Exception ex) {
                    Log.e("tag", "end session" + ex.getMessage());
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != mIOnAcceptMessListener) {
                mIOnAcceptMessListener.onMess((String) msg.obj);
            }
        }
    };
}
