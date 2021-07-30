package ufps.poo2.negocio;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import ufps.poo2.negocio.basedatos.DAOFrabrica;
import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;
import ufps.poo2.negocio.basedatos.interfaz.IJugadasDAO;
import ufps.poo2.negocio.basedatos.interfaz.IPartidoDAO;
import ufps.poo2.negocio.rmi.InterfaceCliente;
import ufps.poo2.negocio.rmi.InterfaceServidor;
import ufps.poo2.negocio.rmi.servidor.RmiServidor;

public class Jugador {

	private InterfaceServidor servidor;
	private InterfaceCliente cliente;
	private String nombre;
	private Partido partido;
	private boolean juegoActivo;
	private int miTurno;
	private Object semaforo;
	private DAOFrabrica fabrica;
	private String directorio;
	public Jugador() throws Exception{
		semaforo=new Object();
		RmiServidor servidor=new RmiServidor();
		this.servidor=servidor;
		this.cliente=servidor;		
		juegoActivo=false;
		fabrica=new DAOFrabrica();

	}

	public void enviarArchivo(String ubicacion,String nombre,int port)throws Exception{
		cliente.enviarArchivo(this.nombre,ubicacion, nombre, port);

	}

	public boolean setNombre(String nombre,String directorio){
		this.nombre=nombre;
		this.directorio=directorio;
		return true;
	}

	public boolean iniciarServidor(int port,boolean soyCreador) throws Exception{
		boolean exito=false;
		exito=servidor.iniciarServidor(port, soyCreador,nombre,directorio);
		if (exito && soyCreador) {
			miTurno=servidor.sacarJugadoresActivos();

		}
		return exito;
	}

	public boolean conectarCliente(String host,int port) throws Exception{
		boolean exito=false;
		exito=cliente.conectarme(host, port, nombre,directorio);


		if (exito) {
			miTurno=servidor.sacarJugadoresActivos();
		}
		return exito;
	}

	public String sacarChat() throws RemoteException{
		String chat = servidor.sacarChat();

		if (servidor!=null) {
			servidor.clearChat();
		}
		return chat;
	}

	public void enviarMensaje(String mensaje) throws Exception{
		Calendar k=Calendar.getInstance();
		int h=k.get(Calendar.HOUR);
		int m=k.get(Calendar.MINUTE);
		cliente.enviarMensajes(nombre+" ("+h+":"+m+")>"+mensaje+"\n");
	}

	private void ingresarJugada(){
		Thread hilo=new Thread(new Runnable(){			
			public void run(){
				JugadaDTO jugada=null;
				while (juegoActivo){
					try {
						jugada=servidor.sacarJugada();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if (juegoActivo) {
						partido.ingresarLinea(jugada.getI(), jugada.getJ(),jugada.getI2(), jugada.getJ2());
						partido.ingresarLinea();
						synchronized (semaforo) {
							semaforo.notifyAll();
						}
						try {
							servidor.clearJugada();
						} catch (RemoteException e) {
							e.printStackTrace();
						}

						IJugadasDAO jugadas= fabrica.obtenerJugadaDAO(false);
						try {
							jugada.setCodigo(partido.getCodigo());
							jugadas.registrarJugada(jugada);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

				}				
			}
		});
		hilo.start();
	}

	private boolean iniciarPartido(int tamanio,int jugadores,int codigoPartido) throws Exception{
		boolean exito=false;
		System.out.println("en el metodo iniciar partido "+tamanio+"  jugadores"+jugadores+" codigoPartido "+codigoPartido);
		partido=new Partido(tamanio,jugadores,codigoPartido,servidor.sacarNombresActivos(0));
		if (partido!=null) {
			juegoActivo=true;
			this.ingresarJugada();
			this.hiloValiDesco();
			exito=true;
			IPartidoDAO conexion=fabrica.obtenerPartidoDAO(false);
			conexion.registrarPartido(codigoPartido);
		}
		return exito;
	}

	public int empezarPartido() throws NumberFormatException, Exception{
		int[] empezar=servidor.sacarEmpezar();	
		if (empezar.length==1) {
			return 0;
		}
		empezar[1]=this.generarCodigo2(null);
		boolean exito=iniciarPartido(empezar[0],empezar[2],empezar[1]);
		int tamanio=-1; 
		if (exito) {
			tamanio=empezar[0];
		}
		return tamanio;
	}

	public void iniciarPartido(int tamanio)throws Exception{		
		if (cliente.validarEmpezar()) {
			
			int[]iniciarPartido=new int [3];
			iniciarPartido[0]=tamanio;
			iniciarPartido[1]=0;
			cliente.iniciarPartido(iniciarPartido);
		}else{
			throw new Exception("Necesita almenos 2 jugadores para empezar");
		}

	}



	private int generarCodigo2(IPartidoDAO par){
		int numero=0;
		IPartidoDAO partido=null;
		if (par!=null) {
			partido=par;
		}else{
			partido=fabrica.obtenerPartidoDAO(true);
		}

		int aleatorio=(int)(Math.random()*(10000-1+1)+1);
		try {
			int buscado=partido.obtenerPartido(aleatorio);
			if (buscado>0) {
				numero=generarCodigo2(partido);
			}else{
				numero=aleatorio;
				partido.cerrarConexion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numero;
	}

	public ArrayList<int[]> getLineas() {
		return partido.getLineas();

	}

	public ArrayList<int[]> getCuadrado() {
		return partido.getCuadrados();
	}

	public boolean ingresarLinea(JugadaDTO jugada) throws Exception{
		partido.ingresarLinea(jugada.getI(),jugada.getJ(),jugada.getI2(),jugada.getJ2());
		boolean exito=partido.validarJugada();
		if (exito) {
			jugada.setJugador(nombre);
			cliente.enviarJugadas(jugada);			
		}
		return exito;
	}

	private int ini=1;
	public boolean[] esMiTurno(){
		boolean esMiTurno[]=new boolean[2];
		esMiTurno[0]=false;
		esMiTurno[1]=juegoActivo;
		System.out.println("mi turno es "+miTurno);
		System.out.println("ini vale  "+ini);
		if (ini==-2) {
			esMiTurno[0]=false;
			esMiTurno[1]=false;
		}else if (miTurno==ini) {
			ini=2;			
			esMiTurno[0]=true;
			esMiTurno[1]=true;
		}else{
			synchronized (semaforo) {
				try {
					System.out.println("metodo esperando para el turno");
					semaforo.wait();
					System.out.println("MI TURNO ES ="+miTurno);
					System.out.println("el turno de partido es "+ partido.esMiTurno());
					if (miTurno==partido.esMiTurno()) {
						System.out.println("es mi turno juegoactivo vale "+juegoActivo);
						esMiTurno[0]=true;
						esMiTurno[1]=juegoActivo;
					}
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}	
			}
		}

		return esMiTurno;
	}

	public String[]mostrarPuntos(){
		return partido.sacarPuntos();		
	}



	public String[]sacarNombres(){
		String[] aux=null;
		try {
			aux=servidor.sacarNombresActivos(1);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return aux;
	}

	private void hiloValiDesco(){
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				while(juegoActivo){					
					int desconectado=-1;
					try {
						desconectado = servidor.sacarDesconectado();
						if (juegoActivo) {
							servidor.clearDesconectado();
						}						

					} catch (RemoteException e) {}
					if (desconectado!=-1) {
						partido.removerActivo(desconectado);
						synchronized (semaforo) {
							semaforo.notifyAll();	
						}
					}
				}


			}
		});
		hilo.start();
	}	

	public String sacarGanador(){
		String ganador="";
		ganador=partido.sacarGanador();

		if (ganador.equalsIgnoreCase(nombre)) {
			ganador="Eres el ganador del juego";
			acabarPartido();
		}else if(ganador.equalsIgnoreCase("estofueunempateynadiepuedetenerestenombre")){
			ganador="El partido termino con un empate";
			acabarPartido();			
		}else if(ganador!=""){
			ganador="El ganador es "+ganador;
			acabarPartido();
		}

		return ganador;
	}

	public boolean acabarTodo(){
		boolean exito=false;
		try {		
			juegoActivo=false;
			ini=-2;
			synchronized (semaforo) {
				semaforo.notifyAll();	
			}
			exito=servidor.desconectar(0);
			servidor=null;
			RmiServidor servidor=new RmiServidor();
			this.servidor=servidor;
			this.cliente=servidor;
			if (partido!=null) {
				partido.acabar();
				partido=null;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		ini=1;
		return exito;
	}

	private void acabarPartido(){
		juegoActivo=false;
		System.out.println("METODO ACABAR PARTIDO ACTIVO CSM");
		ini=-2;
		synchronized (semaforo) {
			semaforo.notifyAll();
		}
		try {
			servidor.desconectar(1);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
