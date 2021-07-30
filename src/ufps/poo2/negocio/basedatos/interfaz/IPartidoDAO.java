package ufps.poo2.negocio.basedatos.interfaz;



public interface IPartidoDAO {

	public boolean registrarPartido(int codigoPartido) throws Exception;

    public int obtenerPartido(int codigo)throws Exception;
    
    public void cerrarConexion()throws Exception;
}
