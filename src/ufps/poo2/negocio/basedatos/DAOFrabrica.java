package ufps.poo2.negocio.basedatos;


import ufps.poo2.negocio.basedatos.archivoplano.dao.ChatDAO;
import ufps.poo2.negocio.basedatos.derby.dao.DerbyJugadasDAO;
import ufps.poo2.negocio.basedatos.derby.dao.DerbyPartidoDAO;
import ufps.poo2.negocio.basedatos.interfaz.IChatDAO;
import ufps.poo2.negocio.basedatos.interfaz.IJugadasDAO;
import ufps.poo2.negocio.basedatos.interfaz.IPartidoDAO;

public class DAOFrabrica {

	public IJugadasDAO obtenerJugadaDAO(boolean conexion){
		return new DerbyJugadasDAO(conexion);
	}
	
	public IPartidoDAO obtenerPartidoDAO(boolean mantener){
		return new DerbyPartidoDAO(mantener);
	}

	public IChatDAO obtenerArchivoPlanoChatDAO(){
		return new ChatDAO();
	}
}
