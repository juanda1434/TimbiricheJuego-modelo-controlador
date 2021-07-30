package ufps.poo2.negocio.rmi;

import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;

public interface InterfaceCliente {

	
	public void enviarMensajes(String mensaje)throws Exception;
	
	public void enviarJugadas(JugadaDTO jugada)throws Exception;
	
	public boolean conectarme(String host,int port,String nombre,String directorio)throws Exception;
	
	public void iniciarPartido(int[] inici);
	
    public boolean validarEmpezar(); 
    
    public void enviarArchivo(String emisor,String ubicacion,String receptor,int port)throws Exception;
}
