package ufps.poo2.negocio.basedatos.derby.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


import ufps.poo2.negocio.basedatos.derby.dto.JugadaDTO;
import ufps.poo2.negocio.basedatos.interfaz.IJugadasDAO;

public class DerbyJugadasDAO implements IJugadasDAO{

	private boolean mantenerConn;
	private Connection conexion;

	public DerbyJugadasDAO(boolean mantenerConn){
		this.mantenerConn=mantenerConn;
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			conexion = DriverManager.getConnection("jdbc:derby://localhost:1527/database;user=ufps;password=ufps;");
			System.out.println("conexion establecida");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean registrarJugada(JugadaDTO jugada) throws Exception {
		boolean exito=false;
		try {
			PreparedStatement stm=conexion.prepareStatement("INSERT INTO JUGADAS(fecha,codigo,jugador,i,j,i2,j2) values (?,?,?,?,?,?,?)");
			stm.setString(1,jugada.getFecha());
			stm.setInt(2, jugada.getcodigo());
			stm.setString(3,jugada.getJugador());
			stm.setInt(4, jugada.getI());
			stm.setInt(5, jugada.getJ());
			stm.setInt(6, jugada.getI2());
			stm.setInt(7, jugada.getJ2());
			int exi=stm.executeUpdate();
			if (exi>0) {
				exito=true;
			}
			stm.close();
		} finally {
			if (!mantenerConn) {
				if (conexion!=null) {
					conexion.close();
				}
			}
		}
		return exito;
	}}
