package ufps.poo2.negocio.rmi.servidor;



import java.io.File;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import ufps.poo2.gui.utilidades.Utilidades;
import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;
import ufps.poo2.negocio.rmi.InterfaceCliente;
import ufps.poo2.negocio.rmi.InterfaceServidor;
import ufps.poo2.negocio.socket.GestorSocket;

public class RmiServidor extends UnicastRemoteObject implements InterfaceServidor,InterfaceCliente{

	private static final long serialVersionUID = 1L;
	private ArrayList<Registry> clientes;
	private ArrayList<String>nombresActivos;
	private String chat;
	private Object[] semaforo;
	private JugadaDTO jugada;
	private int[] empezar;
	private boolean enJuego;
	private int desconectado;
	private int port;
	private boolean aceptarConexiones;
	private GestorSocket gestorEnvios=new GestorSocket();
	private String directorio;

	public boolean generarEspera(int port)throws RemoteException{
		boolean exito=false;
		try{
			exito= gestorEnvios.iniciarServidorSocket(port, directorio);
		}catch(Exception e){
			e.printStackTrace();
			throw new RemoteException();
		}
		return exito;
	}

	public void enviarArchivo(String emisor,String ubicacion,String receptor,int port)throws Exception{
		int indiceNombre=lugarNombre(receptor);
		if (indiceNombre==-1) {
			throw new Exception("el jugador seleccionado ya no se encuentra conectado");
		}
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				File archivo=new File(ubicacion);
				try{ 
					Registry registro= clientes.get(indiceNombre);
					InterfaceServidor servidor=(InterfaceServidor)registro.lookup("servidor");
					boolean aux=servidor.generarEspera(port);					
					if (aux) {
						if (gestorEnvios.enviarArchivo(port, servidor.getIp(),ubicacion)) {
							enviarMensajes(emisor+"("+Utilidades.obtenerHoraActual()+")> Envio archivo "+archivo.getName()+" a "+receptor+"\n");
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					try {
						enviarMensajes(emisor+"("+Utilidades.obtenerHoraActual()+")> Error al enviar archivo <"+archivo.getName()+">\n\nEl archivo se intento enviar a--> ("+receptor+")\nintenta cambiar el puerto de envio\n");
					} catch (RemoteException e1) {}
				}
			}
		});
		hilo.start();
	}

	public int lugarNombre(String nombre){
		int aux=-1;
		for (int i = 0; i < nombresActivos.size(); i++) {
			if (nombresActivos.get(i).equalsIgnoreCase(nombre)) {
				aux=i;
				break;
			}
		}
		return aux;
	}
	public RmiServidor() throws RemoteException {
		clientes=new ArrayList<>();		
		chat="";
		desconectado=-1;
		semaforo=new Object[4];
		semaforo[0]=new Object();
		semaforo[1]=new Object();
		semaforo[2]=new Object();
		semaforo[3]=new Object();
		nombresActivos=new ArrayList<>();
		aceptarConexiones=false;


	}

	//METODOS DE SERVIDOR//////////


	public boolean iniciarServidor(int port,boolean soyCreador,String nombre,String directorio)throws ExportException{	
		boolean exito=false;
		Registry registro=null;

		try {
			registro= LocateRegistry.createRegistry(port);	
			registro.bind("servidor", (InterfaceServidor)this);			
			this.port=port;

			System.out.println("mi ip es "+getIp());
			if (soyCreador) {				
				clientes.add(registro);
				nombresActivos.add(nombre);
				enJuego=true;
				exito=true;		
				this.desconexiones();
				aceptarConexiones=true;
			}
			exito=true;
			this.directorio=directorio;
		} catch (ExportException e) {			
			throw new ExportException("el puerto "+port+" de tu pc esta ocupado. Utiliza otro o reinicia la aplicacion para intentar desocuparlo");
		}catch(Exception e){
			e.printStackTrace();
		}

		return exito;
	}

	@Override
	public void recibirMensaje(String mensaje) throws RemoteException {		
		chat+=mensaje;

		synchronized (semaforo[0]) {
			semaforo[0].notify();
		}



	}

	@Override
	public boolean validarNombre(String nom) throws RemoteException {
		boolean exito=false;
		System.out.println("metodo validar nombre recibe "+nom);
		for (String nombre : nombresActivos) {
			System.out.println("en el for va a comparar con "+nombre);
			if (nombre.equalsIgnoreCase(nom)) {
				exito=true;
				break;
			}
		}		
		return exito;
	}

	@Override
	public boolean conectar(String host,int port,String nombre) throws RemoteException {		
		boolean exito=false;
		if (!aceptarConexiones || clientes.size()==4) {
			throw new RemoteException("no se permiten mas conexiones");
		}
		try {
			Registry registro= LocateRegistry.getRegistry(host,port);
			InterfaceServidor servidor =(InterfaceServidor)registro.lookup("servidor");
			if (servidor.estoyConectado()) {
				System.out.println("en el metodo conectar estoy conectado al que se conecto a mi :v");
				clientes.add(registro);
				nombresActivos.add(nombre);
				actualizarTodos();
				exito=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exito;
	}

	private void actualizarTodos()throws RemoteException{
		for (int i = 0; i < clientes.size(); i++) {
			InterfaceServidor servidor=null;
			try {
				servidor = (InterfaceServidor)clientes.get(i).lookup("servidor");
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
			servidor.ActualizarClientes(clientes);
			servidor.ActualizarNombres(nombresActivos);
		}
	}

	@Override
	public void recibirJugada(JugadaDTO jugada) throws RemoteException {	
		synchronized (semaforo[1]) {
			this.jugada=jugada;
			semaforo[1].notify();

		}
	}

	@Override
	public boolean estoyConectado() {
		return true;
	}

	@Override
	public String getIp() throws RemoteException {
		String ip=null;

		try {
			ip=(InetAddress.getLocalHost()).getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	@Override
	public void ActualizarClientes(ArrayList<Registry> clientes) throws RemoteException {
		this.clientes=clientes;
		System.out.println("acutalizando clientes");

	}

	@Override
	public void ActualizarNombres(ArrayList<String> nombres) throws RemoteException {
		this.nombresActivos=nombres;		
		System.out.println("acutalizando nombres");

	}	




	////METODOS DE CLIENTE///////////////////////////
	@Override
	public void enviarMensajes(String mensaje) throws RemoteException {
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				for (Registry cliente : clientes) {
					try {
						InterfaceServidor servidor=null;
						try {
							servidor = (InterfaceServidor) cliente.lookup("servidor");
						} catch (AccessException e1) {
							e1.printStackTrace();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
						try {
							servidor.recibirMensaje(mensaje);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					} catch (NotBoundException e) {
						e.printStackTrace();
					}
				}

			}
		});
		hilo.start();

	}

	@Override
	public void enviarJugadas(JugadaDTO jugada) throws RemoteException {
		for (int i = 0; i < clientes.size(); i++) {
			try {
				InterfaceServidor servidor=(InterfaceServidor) clientes.get(i).lookup("servidor");
				servidor.recibirJugada(jugada);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean puedoConectarme(){
		boolean si=false;
		if (clientes.size()<4) {
			si=true;
		}

		return si;
	}

	public boolean conectarme(String host,int port,String nombre,String directorio)throws Exception{
		boolean exito=false;	
		Registry registro= null;
		InterfaceServidor servidor=null;
		try {
			registro=LocateRegistry.getRegistry(host,port);
			servidor =(InterfaceServidor)registro.lookup("servidor");
		} catch (NotBoundException e) {		
			e.printStackTrace();
			throw new NotBoundException("No exite partida en el puerto "+port+" del host "+host);
		}catch(ConnectException ea){
			throw new ConnectException("Error al conectar con el host "+host);
		}
		if (servidor.estoyConectado()) {
			System.out.println("en METODO CONECTARME ESTOY CONECTADO");
			if (!servidor.validarNombre(nombre)) {
				System.out.println("en el metodo conectarme ya valide el nombre alv");	
				boolean crearServidor=this.iniciarServidor(port, false, nombre,directorio);
				if (crearServidor) {
					exito=servidor.conectar(getIp(),port,nombre);
					enJuego=true;
					this.desconexiones();
				}
			}else{
				throw new Exception("En la partida ya hay un jugador con tu nickname");
			}
		}else{
			throw new Exception("No se pudo conectar");
		}
		return exito;

	}

	public String sacarChat(){
		if (chat=="") {
			synchronized (semaforo[0]) {
				if (chat=="") {
					try {
						semaforo[0].wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return chat;
	}

	public void clearChat(){
		chat="";
	}


	public void empezar(int[]empezar){
		synchronized (semaforo[2]) {
			this.empezar=empezar;
			semaforo[2].notify();

		}
	}

	public void clearEmpezar(){
		empezar=null; 
	}

	@Override
	public JugadaDTO sacarJugada() {
		if (jugada==null) {
			synchronized (semaforo[1]) {
				if (jugada==null) {
					try {
						semaforo[1].wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return jugada;

	}

	@Override
	public void clearJugada() {
		jugada=null;		
	}

	@Override
	public void iniciarPartido(int[] iniciar) {
		iniciar[2]=clientes.size();
		for (int i = 0; i < clientes.size(); i++) {
			try {
				InterfaceServidor servidor=(InterfaceServidor) clientes.get(i).lookup("servidor");
				servidor.empezar(iniciar);
			} catch (AccessException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}
		aceptarConexiones=false;
	}

	@Override
	public int[] sacarEmpezar(){
		if (empezar==null) {
			synchronized (semaforo[2]) {
				if (empezar==null) {
					try {
						semaforo[2].wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return empezar;
	}

	@Override
	public int sacarJugadoresActivos() throws RemoteException {
		return clientes.size();
	}

	private void desconexiones(){

		Thread hilo=new Thread(new Runnable(){

			public void run(){
				while (enJuego) {
					System.out.println("entrando a buscar desconectados");
					for (int i = 0; i < clientes.size(); i++) {
						boolean exito=false;						

						try {
							InterfaceServidor servidor=(InterfaceServidor)clientes.get(i).lookup("servidor");
							exito=servidor.estoyConectado();
						} catch (Exception e) {
						}
						if (!exito) {
							clientes.remove(i);
							nombresActivos.remove(i);
							synchronized (semaforo[3]) {
								desconectado=i;
								semaforo[3].notify();
							}
							System.out.println("Se desconecto alguien");

						}
					}
					if (enJuego) {
						try {
							Thread.sleep(2500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}

		});
		hilo.start();
	}


	public String[] sacarNombresActivos(int aux){

		String nombres[]=new String[this.nombresActivos.size()+aux];
		//System.out.println("metodo sacar nombres en servidor tamanio de nombres = "+nombres.length);
		for (int i = 0; i < nombres.length-aux; i++) {
			nombres[i]=nombresActivos.get(i);
		}	 
		try {
			if (aux==1) {
				nombres[nombres.length-aux]=" | host: "+this.getIp()+" - puerto : "+port +" |";
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return nombres;
	}

	@Override
	public boolean desconectar(int w) throws RemoteException {
		boolean exito=true;
		empezar=new int[1];
		
		if (w==0) {
			enJuego=false;
			try {
				LocateRegistry.getRegistry(port).unbind("servidor");
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < semaforo.length; i++) {
				synchronized (semaforo[i]) {
					semaforo[i].notifyAll();
				}

			}
			exito=true;
		}else{
			synchronized (semaforo[3]) {
				semaforo[3].notifyAll();
			}

		}

		return exito;
	}

	public void clearDesconectado(){
		desconectado=-1;
	}
	@Override
	public int sacarDesconectado() throws RemoteException {
		if (desconectado==-1) {
			synchronized (semaforo[3]) {
				if (desconectado==-1) {
					try {
						semaforo[3].wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return desconectado;
	}

	@Override
	public boolean validarEmpezar() {
		boolean exito=false;
		if (this.clientes.size()>1) {
			exito=true;
		}
		return exito;
	}




}
