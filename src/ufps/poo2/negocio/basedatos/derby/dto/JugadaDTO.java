package ufps.poo2.negocio.basedatos.derby.dto;

import java.io.Serializable;

import ufps.poo2.gui.utilidades.Utilidades;

public  class JugadaDTO implements Serializable{

	
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int i;
      private int j;
      private int i2;
      private int j2;
      private String jugador;
      private String fecha;
      private int codigo;
     
	public JugadaDTO(int i,int j,int i2,int j2){
		this.i=i;
		this.j=j;
		this.i2=i2;
		this.j2=j2;		
		fecha=Utilidades.obtenerFechaActual()+"//"+Utilidades.obtenerHoraActual();		
	}
	public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getI2() {
        return i2;
    }

    public void setI2(int i2) {
        this.i2 = i2;
    }

    public int getJ2() {
        return j2;
    }

    public void setJ2(int j2) {
        this.j2 = j2;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
	public String getFecha(){
		return fecha;
	}
	public int getcodigo(){
		return codigo;
	}
}
