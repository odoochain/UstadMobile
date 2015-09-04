/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustadmobile.port.j2me.impl;

import com.ustadmobile.core.impl.UMLog;
import com.ustadmobile.core.util.UMIOUtils;
import com.ustadmobile.port.j2me.app.SerializedHashtable;
import com.ustadmobile.port.j2me.util.J2MEIOUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author mike
 */
public class UMLogJ2ME extends UMLog{

    private PrintStream logOut = null;
    
    private SocketConnection socketConn;
    
    private OutputStream socketOut;
    
    public UMLogJ2ME() {
        logOut = System.out;
    }
    
    public synchronized void setOutputDest(PrintStream dest) {
        this.logOut = dest;
    }
    
    public synchronized void connectLogToSocket(String serverName) throws IOException{
        closeSocketConn();
        SocketConnection socketConnection = null;
        try {
            socketConnection = (SocketConnection)Connector.open("socket://" 
                + serverName);
            socketOut = socketConnection.openOutputStream();
            logOut = new PrintStream(socketOut);
            System.out.println("Connected socket");
        }catch(IOException e) {
            System.out.println("Exception connecting socket!");
            e.printStackTrace();
            closeSocketConn();
            throw e;
        }
        
    }
    
    public synchronized void closeSocketConn() {
        logOut = System.out;
        UMIOUtils.closeOutputStream(socketOut);
        J2MEIOUtils.closeConnection(socketConn);
    }

    public synchronized void l(int level, int code, String message) {
        logOut.print("[");
        logOut.print(new java.util.Date().toString());
        logOut.print("]");
        logOut.println(":codelu:" + code + " : " + message);
    }

    public synchronized void l(int level, int code, String message, Exception exception) {
        logOut.print("[");
        logOut.print(new java.util.Date().toString());
        logOut.print("]");
        logOut.println(":codelu:" + code + " : " + message + " : " + exception.toString());
    }

}