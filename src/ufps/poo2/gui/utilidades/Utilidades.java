package ufps.poo2.gui.utilidades;

import java.util.Calendar;


public class Utilidades {

	public static String obtenerFechaActual(){
		Calendar c=Calendar.getInstance();		
		int dia=c.get(Calendar.DATE);
		int mes=c.get(Calendar.MONTH);
		int anio=c.get(Calendar.YEAR);
		return dia+"//"+mes+"//"+anio;
	}

	public static String obtenerHoraActual(){
		Calendar c=Calendar.getInstance();
		int hora=c.get(Calendar.HOUR);
		int minuto=c.get(Calendar.MINUTE);
		return hora+"::"+minuto;
	}

}
