package ufps.poo2.negocio.socket;

public class GestorSocket{

	
	private ClienteSocket cliente;
	private ServidorSocket servidor;
	
	
	public GestorSocket(){
		cliente=new ClienteSocket();
		servidor=new ServidorSocket();
	}
	public boolean iniciarServidorSocket(int port,String directorio)throws Exception{		
		boolean exito=false;		
		exito=servidor.iniciarServidor(port, directorio);
		return exito;
	}
	
	public boolean enviarArchivo(int port,String host,String ubicacion)throws Exception{
		boolean exito=false;
		exito=cliente.iniciarCliente(port, host,ubicacion);
		return exito;
	}
	
}
