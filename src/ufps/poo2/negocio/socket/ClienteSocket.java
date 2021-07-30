package ufps.poo2.negocio.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSocket{

	

	public ClienteSocket(){
	
	}
public boolean iniciarCliente(int port ,String host,String ubicacion){
	boolean exito=false;
	try {
		Socket cliente;
		cliente = new Socket(host,port);
		cliente.setSoTimeout(2000);
		cliente.setKeepAlive(true); 
		exito=enviarArchivo(cliente,ubicacion);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return exito;
}
	public boolean enviarArchivo(Socket cliente,String ubicacion){
		boolean exito=false;
		File archivo= new File(ubicacion);
		try {
			DataOutputStream escribir=new DataOutputStream(cliente.getOutputStream());
			int tamaño=(int)archivo.length();
			String nombre=archivo.getName();
			escribir.writeUTF(nombre);
			escribir.writeInt(tamaño);
			
			FileInputStream dondeEstaGuardado=new FileInputStream(ubicacion);
			BufferedInputStream leerArchivo=new BufferedInputStream(dondeEstaGuardado);
			
			BufferedOutputStream escribirArchivo=new BufferedOutputStream(cliente.getOutputStream());
			
			byte[] buffer=new byte[tamaño];
			leerArchivo.read(buffer);
			for (int i = 0; i < buffer.length; i++) {
				escribirArchivo.write(buffer[i]);
			}
			leerArchivo.close();
			escribirArchivo.close();
			cliente.close();
			cliente=null;
			exito=true;
			System.out.println("archivo enviado correctamente");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exito;
	}

}
