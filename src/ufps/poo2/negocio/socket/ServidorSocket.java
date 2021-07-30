package ufps.poo2.negocio.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {


	String directorio;
	Object semaforo;
	boolean recibiendoArchivo;

	public ServidorSocket(){	
		semaforo=new Object();

	}

	public boolean iniciarServidor(int port,String directorio)throws Exception{
		boolean exito=false;
		ServerSocket servidor;
		try {
			this.directorio=directorio;

			servidor=new ServerSocket(port);			
			validarRecibiendo(servidor);
			recibirArchivo(servidor);

			exito=true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error al intentar recibir el archivo");
		}finally{
			servidor=null;
		}
		return exito;
	}

	public void validarRecibiendo(ServerSocket servidor){
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				try {
					synchronized (semaforo) {
						semaforo.wait(5000);
						if (!recibiendoArchivo) {
							servidor.close();							
						}
					}
				} catch (Exception e) {			
				}
			}
		});
		hilo.start();
	}
	public void recibirArchivo(ServerSocket servidor){
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				try {
					Socket cliente= servidor.accept();					
					recibir(cliente,servidor);
				} catch (Exception e) {	
					System.out.println("error el recibir archivo");
				}
			}
		});
		hilo.start();
	}

	public void recibir(Socket cliente,ServerSocket servidor){
		try{
			DataInputStream leer = new DataInputStream(cliente.getInputStream());
			String nombreArchivo=leer.readUTF();
			int tamañoArchivo=leer.readInt();		
			String sep=System.getProperty("file.separator");
			FileOutputStream dondeVoyAGuardar=new FileOutputStream(directorio+sep+nombreArchivo);
			BufferedOutputStream escribirArchivo= new BufferedOutputStream(dondeVoyAGuardar);
			BufferedInputStream leerArchivo=new BufferedInputStream(cliente.getInputStream());

			byte[] buffer = new byte[tamañoArchivo];
			recibiendoArchivo=true;
			synchronized (semaforo) {
				semaforo.notify();	
			}
			for (int i = 0; i < buffer.length; i++) {
				buffer[i]= (byte)leerArchivo.read();
			}

			escribirArchivo.write(buffer);
			escribirArchivo.flush();
			leerArchivo.close();
			escribirArchivo.close();
			System.out.println("RECIBI EL ARCHIVO CORRECTAMENTE");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if (cliente!=null) {
					cliente.close();
				}
				if (servidor!=null) {
					servidor.close();
				}
			}catch(Exception e){

			}


		}

	}
}
