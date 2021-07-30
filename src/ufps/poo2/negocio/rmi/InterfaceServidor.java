package ufps.poo2.negocio.rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.ArrayList;

import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;

public interface InterfaceServidor extends Remote {
	
	
	public boolean iniciarServidor(int port,boolean soyCreador,String nombre,String directorio)throws RemoteException,ExportException;
	
	public void recibirMensaje(String mensaje) throws RemoteException;
	
	public boolean validarNombre(String nombre)throws RemoteException;
	
	public boolean conectar(String host,int ip,String nombre)throws RemoteException;
	
	public void recibirJugada(JugadaDTO jugada)throws RemoteException;
	
	public boolean estoyConectado()throws RemoteException;
    
	public String getIp()throws RemoteException;
	
	public void ActualizarClientes(ArrayList<Registry> clientes)throws RemoteException;
	
	public void ActualizarNombres(ArrayList<String>nombres)throws RemoteException;
	
	public void enviarMensajes(String mensaje)throws RemoteException;
	
	public void enviarJugadas(JugadaDTO jugada)throws RemoteException;
	
	public void clearJugada()throws RemoteException;
	
	public JugadaDTO sacarJugada()throws RemoteException;
	
	public String sacarChat()throws RemoteException;
	
	public void clearChat()throws RemoteException;
	
	public int[] sacarEmpezar()throws RemoteException;
	
	public void empezar(int[] empe)throws RemoteException;
	
	public void clearEmpezar()throws RemoteException;
	
    public int sacarJugadoresActivos()throws RemoteException;
    
    public String[] sacarNombresActivos(int aux)throws RemoteException;
    
    public boolean desconectar(int x)throws RemoteException;
    
    public int sacarDesconectado()throws RemoteException;
    
    public void clearDesconectado()throws RemoteException;
    public boolean generarEspera(int port)throws RemoteException;
}
