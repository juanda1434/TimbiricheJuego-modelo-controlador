package ufps.poo2.negocio.basedatos.interfaz;

import java.io.File;

public interface IChatDAO {

	
	public boolean guardarChat(File chat,String s)throws Exception;
	
	public String leerChat(File chat)throws Exception;
	
}
