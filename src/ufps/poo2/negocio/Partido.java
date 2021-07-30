package ufps.poo2.negocio;

import java.util.ArrayList;

public class Partido {

	private ArrayList<int[]> lineas;   
	private int[]linea;
	private ArrayList<int[]> cuadrados;
	private int turnoActual;
	private int tamanio;
	private int[] tablaPuntos;
	private ArrayList<Integer>jugadoresActivos;
	private int codigoPartido;
	private String[]nombres;
	private Object[]semaforo;
	private String ganador;
	private boolean hiloActivo;
	public Partido(int tamanio,int jugadores,int codigo,String[]nombres){
		hiloActivo=true;
		ganador ="";
		this.nombres=nombres;
		this.turnoActual=0;
		this.tamanio=tamanio;
		this.codigoPartido=codigo;
		lineas = new ArrayList<>();
		linea= new int[5];
		cuadrados = new ArrayList<>();
		jugadoresActivos=new ArrayList<>();
		semaforo=new Object[3];
		semaforo[0]=new Object();
		semaforo[1]=new Object();
		semaforo[2]=new Object();
		for (int i = 1; i <= jugadores; i++) {
			jugadoresActivos.add(i);
		}
		iniciarTabla(jugadores);
		this.validarGanadorDesconexiones();
		this.validarGanadorPuntos();
	}	

	public int getCodigo(){
		return codigoPartido;
	}

	public void removerActivo(int i){	
		if (i==turnoActual && i==jugadoresActivos.size()-1) {
			turnoActual=0;
		}else if (i<turnoActual) {
			turnoActual--;
		}		
		jugadoresActivos.remove(i);
		synchronized (semaforo[1]) {
			semaforo[1].notify();
		}
	}
	public String[] sacarPuntos(){
		synchronized (semaforo[2]) {
			if (lineas.size()!=0) {
				try {
					semaforo[2].wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		String puntos[]=new String[tablaPuntos.length];
		int j=0;
		for (int i = 0; i < puntos.length; i+=2,j++) {
			puntos[i]=nombres[j];
			puntos[i+1]=tablaPuntos[i+1]+"";
		}	
		return puntos;
	}

	public int esMiTurno(){
		if (jugadoresActivos.size()==0) {
			return -1;
		}	
		return jugadoresActivos.get(turnoActual);
	}
	private void iniciarTabla(int jugadores){
		tablaPuntos=new int[jugadores*2];
		int a=1;
		for(int i=1;i<tablaPuntos.length;i+=2,a++){
			tablaPuntos[i-1]=a;
			tablaPuntos[i]=0;
		}
	}

	public void agregarPunto(){
		for (int i = 0; i < tablaPuntos.length; i+=2) {
			if (tablaPuntos[i]==esMiTurno()) {
				tablaPuntos[i+1]+=1;
				
			}	
		}
	}

	public void ingresarLinea(){         
		boolean c1=agregarCuadrado(-1, 0);
		boolean c2=agregarCuadrado(1, 0);
		boolean c3=agregarCuadrado(0, -1);
		boolean c4=agregarCuadrado(0, 1); 		
		lineas.add(linea);
		System.out.println("se ingreso la linea a partido");	
		if (c1||c2||c3||c4) {
			synchronized (semaforo[2]) {
				semaforo[2].notifyAll();
			}
			return;			
		}
		turnoActual++;
		if (turnoActual>=jugadoresActivos.size()) {
			turnoActual=0;
		}

	}

	public boolean agregarCuadrado(int a, int b) {
		int[] cuadrado = validarCuadrado(a, b);
		if (cuadrado != null) {
			this.cuadrados.add(cuadrado);
			this.agregarPunto();
			return true;
		}
		return false;
	}

	private  int[] validarCuadrado(int a, int b) {
		int[] cuadrado = null;
		int i = linea[0];
		int j = linea[1];
		int i2 = linea[2];
		int j2 = linea[3];
		//este if valida que que no busque cuadrados fuera de los limites de los botones
		if ((i + a >= 0 && i2 + a >= 0) && (i + a <= tamanio  && i2 + a <= tamanio ) && (j + b >= 0 && j2 + b >= 0) && (j + b <= tamanio  && j2 + b <= tamanio )) {
			int arribaI = i + a;
			int arribaJ = j + b;
			int arribaSI = i2 + a;
			int arribaSJ = j2 + b;

			//aqui busca dependiendo de los valores de la a,b arriba abajo izquierda o derecha los cuadrados
			//si se traza una linea horizontal a un cuadrado seleccionando 2 puntos y esta es la inferior y la ultima para conformarlo :
			for (int l = 0; l < lineas.size(); l++) {
				//este if buscara en la variable lineas que guarda todas las lineas la linea equivalente a : la formada por la union del primer punto pulsado y su pulso supeior;
				if ((i == lineas.get(l)[0] && j == lineas.get(l)[1] && arribaI == lineas.get(l)[2] && arribaJ == lineas.get(l)[3]) || (i == lineas.get(l)[2] && j == lineas.get(l)[3] && arribaI == lineas.get(l)[0] && arribaJ == lineas.get(l)[1])) {
					for (int k = 0; k < lineas.size(); k++) {
						//este if buscara en la variable lineas que guarda todas las lineas la linea equivalente a : la formada por la union del segundo punto pulsado y su pulso supeior;
						if ((i2 == lineas.get(k)[0] && j2 == lineas.get(k)[1] && arribaSI == lineas.get(k)[2] && arribaSJ == lineas.get(k)[3]) || (i2 == lineas.get(k)[2] && j2 == lineas.get(k)[3] && arribaSI == lineas.get(k)[0] && arribaSJ == lineas.get(k)[1])) {
							for (int m = 0; m < lineas.size(); m++) {
								//este if buscara en la variable lineas que guarda todas las lineas la linea equivalente a : la formada por la union de los puntos superiores del primer y segundo punto pulsado
								if ((arribaI == lineas.get(m)[0] && arribaJ == lineas.get(m)[1] && arribaSI == lineas.get(m)[2] && arribaSJ == lineas.get(m)[3]) || (arribaI == lineas.get(m)[2] && arribaJ == lineas.get(m)[3] && arribaSI == lineas.get(m)[0] && arribaSJ == lineas.get(m)[1])) {
									// if encuentra todas esas lineas se guardara generara un cuadrado
									cuadrado = cuadrado(i, i2, arribaI, j, j2, arribaJ);
									System.out.println("LOGRASTE INGRESAR UN CUADRADO WOW");
									return cuadrado;
								}
							}
						}
					}
				}
			}
		}
		return cuadrado;
	}

	int[] cuadrado(int i, int i2, int i3, int j, int j1, int j2) {
		int[] x = new int[3];
		int[] y = new int[3];
		x[0] = i;
		x[1] = i2;
		x[2] = i3;
		y[0] = j;
		y[1] = j1;
		y[2] = j2;
		burbuja(x);
		burbuja(y);
		int cuadrado[] = new int[3];
		cuadrado[0] = x[0];
		cuadrado[1] = y[0];
		cuadrado[2] = esMiTurno();
		return cuadrado;
	}

	public void burbuja(int[]A){
		int i, j, aux;
		for (i = 0; i < A.length - 1; i++) {
			for (j = 0; j < A.length - i - 1; j++) {
				if (A[j + 1] < A[j]) {
					aux = A[j + 1];
					A[j + 1] = A[j];
					A[j] = aux;
				}
			}
		}
	}

	public boolean validarJugada() {    	
		boolean w = false;
		int indiceI = linea[0];
		int indiceJ = linea[1];
		int indiceI2 = linea[2];
		int indiceJ2 = linea[3];
		System.out.println("metodo validadJugada "+indiceI+","+indiceJ+","+indiceI2+","+indiceJ2);
		if ((indiceI == indiceI2 && (indiceJ + 1 == indiceJ2 || indiceJ - 1 == indiceJ2)) || ((indiceI + 1 == indiceI2 || indiceI - 1 == indiceI2) && indiceJ == indiceJ2)) {
			w = true;
			for (int i = 0; i < lineas.size(); i++) {
				if ((lineas.get(i)[0] == linea[0] && lineas.get(i)[1] == linea[1] && lineas.get(i)[2] == linea[2] && lineas.get(i)[3] == linea[3]) || (lineas.get(i)[0] == linea[2] && lineas.get(i)[1] == linea[3] && lineas.get(i)[2] == linea[0] && lineas.get(i)[3] == linea[1])) {
					w = false;
					break;
				}
			}
		}
		return w;
	}

	public void ingresarLinea(int x,int y,int x1,int y1){		
		int []lin=new int[5];
		lin[0]=x;
		lin[1]=y;
		lin[2]=x1;
		lin[3]=y1;
		lin[4]=esMiTurno();
		linea=lin;	
	}

	public ArrayList<int []> getLineas(){
		return this.lineas;
	}

	public ArrayList<int []> getCuadrados(){
		return this.cuadrados;
	}

	public void validarGanadorPuntos(){
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				while(hiloActivo){
					synchronized (semaforo[2]) {
						try {
							semaforo[2].wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (hiloActivo) {
						int ganador=ganador();
						System.out.println("en el metodo validar ganador puntos el index ganador es "+ganador);
						if(ganador==-2){
							Partido.this.ganador="estofueunempateynadiepuedetenerestenombre";
							synchronized (semaforo[0]) {
								semaforo[0].notify();
							}
						}else if (ganador!=-1) {
							Partido.this.ganador=nombres[ganador-1];
							synchronized (semaforo[0]) {
								semaforo[0].notify();
							}
						}

					}

				}
			}
		});
		hilo.start();

	}

	private boolean validarEmpate(int cuadradosTotales,int mayor,int indexMayor){
		boolean empate=false;
			for (int i = 1; i < tablaPuntos.length; i+=2) {
				if (jugadoresActivos.contains(tablaPuntos[i-1])&&indexMayor!=tablaPuntos[i-1]&&mayor==tablaPuntos[i]) {
					return true;
				}
			}
		
		return empate;
	}
	private int ganador(){
		int cuadradosTotales=tamanio*tamanio;
		System.out.println("metodo ganador cuadradostotales = "+cuadradosTotales);
		int cuadradosFaltantes=cuadradosTotales-this.cuadrados.size();
		System.out.println("metodo ganador cuadradosfaltandes = "+cuadradosFaltantes);
		int mayor=0;
		int indexMayor=-1;
		for (int i = 1; i < tablaPuntos.length; i+=2) {
			if (jugadoresActivos.contains(tablaPuntos[i-1])&&tablaPuntos[i]>mayor) {
				mayor=tablaPuntos[i];
				indexMayor=tablaPuntos[i-1];
			}
		}		
		System.out.println("index mayor "+indexMayor);
		if (indexMayor!=-1) {
			if (cuadradosTotales==cuadrados.size()&&validarEmpate(cuadradosTotales,mayor,indexMayor)) {
				return -2; 
			}
			for (int i = 1; i < tablaPuntos.length; i+=2) {
				if (jugadoresActivos.contains(tablaPuntos[i-1])&&indexMayor!=tablaPuntos[i-1]) {
					if ((tablaPuntos[i]+cuadradosFaltantes)>=mayor) {
						indexMayor=-1;																			
					}
				}
			}
		}

		System.out.println("al final el index mayor es "+indexMayor);

		return indexMayor;
	}

	public void validarGanadorDesconexiones(){
		Thread hilo=new Thread(new Runnable(){
			public void run(){
				while(hiloActivo){
					synchronized (semaforo[1]) {
						try {
							semaforo[1].wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (jugadoresActivos.size()==1) {
						System.out.println("ganador por desconexin el ultimo es "+jugadoresActivos.get(0));
						ganador=nombres[jugadoresActivos.get(0)-1];
						synchronized (semaforo[0]) {
							semaforo[0].notify();
						}
					}
				}
			}
		});
		hilo.start();
	}

	public String sacarGanador(){
		if (ganador=="") {
			synchronized (semaforo[0]) {
				if (ganador=="") {
					try {
						semaforo[0].wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
			}
		}

		if (ganador!="") {			
			notificarHilos();			
		}
		return ganador;
	}

	private void notificarHilos(){
		hiloActivo=false;
		jugadoresActivos.clear();
		for (int i = 0; i < semaforo.length; i++) {
			synchronized (semaforo[i]) {
				semaforo[i].notifyAll();

			}
		}
	}
	public void acabar(){
		notificarHilos();

	}

}
