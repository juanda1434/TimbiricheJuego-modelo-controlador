package ufps.poo2.negocio;

import java.io.File;
import java.util.ArrayList;

import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;

public interface INegocio {

	public void enviarMensaje(String Mensaje)throws Exception;
	
	public boolean iniciarServidor(int puerto)throws Exception;
	
	public boolean crearCliente(String host,int puerto)throws Exception;
	
	public String getChat()throws Exception;
	
	public boolean acabarConexion();
	
	public boolean ingresarLinea(JugadaDTO jugada)throws Exception;//
	
	public ArrayList<int []> getLineas();//
	
	public ArrayList<int []> getCuadrado();//
	
	public boolean actualizarNick(String nombre,String directorio);	
	
	public void iniciarPartido(int tamaño) throws Exception;
	
	public int empezarPartido() throws NumberFormatException, Exception;
	
	public boolean[] actualizarTurno();
	
	public String[] mostrarPuntos();
	
	public String[] sacarNombres();
	
	public String sacarGanador();
	
	public String leerChat(File chat)throws Exception;
	
	public boolean escribirChat(File chat,String s)throws Exception;
	
	public void enviarArchivo(String ubicacion,String nombre,int port)throws Exception;
}
