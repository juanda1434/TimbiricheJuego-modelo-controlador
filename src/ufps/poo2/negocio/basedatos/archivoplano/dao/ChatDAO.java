package ufps.poo2.negocio.basedatos.archivoplano.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ufps.poo2.negocio.basedatos.interfaz.IChatDAO;

public class ChatDAO implements IChatDAO {

	@Override
	public boolean guardarChat(File chat,String s)throws Exception {
		boolean exito=false;
		FileWriter output = null;
        try {
            output = new FileWriter(chat.getAbsolutePath()+".DAT");
            BufferedWriter writer = new BufferedWriter(output);
            writer.write(s);
            writer.close();
            exito=true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {           // Ignore issues during closing        
                }
            }
        }
		return exito;
	}

	@Override
	public String leerChat(File chat)throws Exception {
		String chatt="";
		if (chat!=null) {
			String nombre=chat.getAbsolutePath();
			System.out.println(nombre);
			if (validarArchivo(nombre)) {
				if (chat.canRead()) {
					
					FileReader file = null;
			        try {
			            file = new FileReader(chat);
			            BufferedReader reader = new BufferedReader(file);
			            String line = "";
			            while ((line = reader.readLine()) != null) {
			                chatt += line + "\n";
			            }
			        } catch (Exception e) {
			            throw new RuntimeException(e);
			        } finally {
			            if (file != null) {
			                try {
			                    file.close();
			                } catch (IOException e) {
			                }
			            }
			        }
					
				}else{
					throw new Exception("No se puede leer el archivo");
				}
			}else{
				throw new Exception("El archivo seleccionado no es .chat");
			}
		}
		return chatt;
	}

	
	private boolean validarArchivo(String nombreArchivo){
		return nombreArchivo.toLowerCase().endsWith(".dat");		
	}
}
