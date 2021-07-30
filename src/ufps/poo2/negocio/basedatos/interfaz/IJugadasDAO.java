package ufps.poo2.negocio.basedatos.interfaz;


import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;

public interface IJugadasDAO {

	public boolean registrarJugada(JugadaDTO jugada)throws Exception;
	
}
