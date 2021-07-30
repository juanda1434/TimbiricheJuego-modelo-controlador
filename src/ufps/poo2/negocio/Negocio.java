package ufps.poo2.negocio;


import java.io.File;
import java.util.ArrayList;

import ufps.poo2.negocio.basedatos.DAOFrabrica;
import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;
import ufps.poo2.negocio.basedatos.interfaz.IChatDAO;


public class Negocio implements INegocio {

	private Jugador jugador;
	private DAOFrabrica fabrica;
	

	public Negocio()throws Exception{	
		jugador=new Jugador();
		fabrica=new DAOFrabrica();
	}
	@Override
	public void enviarMensaje(String mensaje) throws Exception {
		try{
			jugador.enviarMensaje(mensaje);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("error al enviar Mensaje");
		}

	}

	@Override
	public boolean iniciarServidor(int puerto) throws Exception {
		boolean exito=jugador.iniciarServidor(puerto, true);	

		return exito;
	}

	@Override
	public boolean crearCliente(String host, int puerto) throws Exception {
		boolean exito2=false;
		exito2=jugador.conectarCliente(host, puerto);	
		return exito2;
	}

	@Override
	public String getChat() throws Exception {
		return jugador.sacarChat();
	}

	@Override
	public boolean acabarConexion() {
		return jugador.acabarTodo();

	}

	@Override
	public boolean ingresarLinea(JugadaDTO jugada) throws Exception {		
		return jugador.ingresarLinea(jugada);

	}

	@Override
	public ArrayList<int[]> getLineas() {
		return jugador.getLineas();

	}

	@Override
	public ArrayList<int[]> getCuadrado() {
		return jugador.getCuadrado();

	}

	@Override
	public boolean actualizarNick(String nombre,String directorio) {		
		boolean exito=false;
		exito=jugador.setNombre(nombre,directorio);
		return exito;
	}
	@Override
	public void iniciarPartido(int tamanio) throws Exception{
		jugador.iniciarPartido(tamanio);

	}
	@Override
	public int empezarPartido() throws NumberFormatException, Exception {
		return jugador.empezarPartido();		
	}

	@Override
	public boolean[] actualizarTurno() {
		return jugador.esMiTurno();	
	}
	@Override
	public String[] mostrarPuntos() {
		return jugador.mostrarPuntos();
	}
	@Override
	public String[] sacarNombres() {
		return jugador.sacarNombres();
	}
	@Override
	public String sacarGanador() {
		return jugador.sacarGanador();
	}
	
	@Override
	public String leerChat(File chat)throws Exception {
		IChatDAO conexion= fabrica.obtenerArchivoPlanoChatDAO();
		String chatt=null;
		chatt=conexion.leerChat(chat);
		return chatt;
	}
	
	@Override
	public boolean escribirChat(File chat,String s) throws Exception {
		IChatDAO conexion= fabrica.obtenerArchivoPlanoChatDAO();
		boolean escrito=false;
		escrito=conexion.guardarChat(chat,s);
		return escrito;
	}
	
	@Override
	public void enviarArchivo(String ubicacion,String nombre,int port)throws Exception{
		jugador.enviarArchivo(ubicacion, nombre, port);
		
	}
}
