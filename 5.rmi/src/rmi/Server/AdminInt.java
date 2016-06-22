package rmi.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface AdminInt extends Remote {
    ArrayList<String> listUsers() throws RemoteException;
    void deleteUser(String name) throws RemoteException;
    void changePass(String name, String newPass) throws RemoteException;
    //void terminateSession(String name);
    //ArrayList<String> getStatistics(String name);
}
