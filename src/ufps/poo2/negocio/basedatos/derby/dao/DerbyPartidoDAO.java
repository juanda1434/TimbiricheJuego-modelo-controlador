package ufps.poo2.negocio.basedatos.derby.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ufps.poo2.gui.utilidades.Utilidades;
import ufps.poo2.negocio.basedatos.interfaz.IPartidoDAO;

public class DerbyPartidoDAO implements IPartidoDAO{

	private boolean mantenerConexion;
	private Connection conexion;

	public DerbyPartidoDAO(boolean mantenerCon){
		mantenerConexion = mantenerCon;
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			conexion = DriverManager.getConnection("jdbc:derby://localhost:1527/database;user=ufps;password=ufps;");
			System.out.println("conexion establecida");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean registrarPartido(int codigoPartido)throws Exception {
		boolean exito=false;
		try {
			PreparedStatement pstm= conexion.prepareStatement("INSERT INTO CODIGOS(codigo,fecha) values(?,?)");
			pstm.setInt(1, codigoPartido);
			pstm.setString(2, Utilidades.obtenerFechaActual());
			int con=pstm.executeUpdate();
			if (con>0) {
				exito=true;
			}	
			pstm.close();
		}finally{
			if (!mantenerConexion) {
				if (conexion!=null) {
					conexion.close();
				}
			}
		}
		return exito;
	}

	@Override
	public int obtenerPartido(int codigo) throws Exception{
		int partido=-1;
		try{
			PreparedStatement pstm=conexion.prepareStatement("Select codigo from codigos where codigo= ?");
			pstm.setInt(1, codigo);
			ResultSet rs=pstm.executeQuery();
			if (rs.next()) {
				partido=rs.getInt(1);
			}
			rs.close();
			pstm.close();
		}finally{
			if (!mantenerConexion) {
				if (conexion!=null) {
					conexion.close();
				}
			}
		}

		return partido;
	}
	
	@Override
	public void cerrarConexion() throws Exception {
		if (conexion!=null) {
			conexion.close();
		}

	}

}
