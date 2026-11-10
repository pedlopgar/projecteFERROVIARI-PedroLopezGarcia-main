package TREN_v21_Compra1Bitllet_Threads_PedroLopezGarcia;

/**
 * Vagó d'un tren.
 *
 * Els seients es guarden en una matriu [fila][columna]. Totes les files tenen 4 seients:
 *     A (finestra)  B (corredor)  ||  C (corredor)  D (finestra)
 * Per això el nombre de places ha de ser múltiple de 4: nFiles = nPlaces / 4.
 *
 * Simplificació del model: els seients (i les reserves) són del Vagó.
 * Per això un Vagó només es pot assignar a UNA Categoria (atribut assignatVago).
 */
public class Vago {

	//Nombre màxim de places que pot tindre un vagó (25 files)
	public static final int MAX_PLACES = 100;

	private String idVago;
	private int nPlaces;
	private boolean assignatVago;
	private Seient[][] seients;
	//Seients per filera: constant per a tots els vagons
	private final int nColumnes = 4;

	//Lletres dels seients d'una filera. L'ordinal (A=0, B=1...) és la columna de la matriu.
	public enum LletraSeient {A, B, C, D}

	//*****************************************************************
	//Constructor 1: vagó amb tots els seients LLIURES (s'usa a les dades inicials)
	public Vago(String idVago, int nPlaces) {
		this.idVago = idVago;
		this.nPlaces = nPlaces;
		this.assignatVago = false;
		creaSeients();
	}

	//*****************************************************************
	//Constructor INTERACTIU. L'identificador ja arriba validat des del menú
	//(CompanyiaFerroviaria.demanaIdentificador), la resta de dades es demanen ací.
	public Vago(String idVago) {
		System.out.println("Creació del VAGÓ " + idVago + "\n>>>>>>>>>>>>>>>>>>>");
		this.idVago = idVago;
		this.nPlaces = demanaPlaces(-1);
		this.assignatVago = false;
		creaSeients();
		System.out.println("<<<<<<<<<<<<<<<<<<<");
	}

	//*****************************************************************
	//Crea la matriu de seients, tots LLIURES, amb identificadors 01A, 01B... nnD
	private void creaSeients() {
		int nFiles = nPlaces / nColumnes;
		this.seients = new Seient[nFiles][nColumnes];
		for (int fila = 0; fila < nFiles; fila++)
			for (LletraSeient lletra : LletraSeient.values())
				this.seients[fila][lletra.ordinal()] = new Seient(String.format("%02d", fila + 1) + lletra);
	}

	//*****************************************************************
	//Demana el nombre de places fins que siga un múltiple de 4 entre 4 i MAX_PLACES.
	//Si defecte > 0, prement INTRO es manté eixe valor.
	private int demanaPlaces(int defecte) {
		int places;
		boolean valid;
		do {
			places = (defecte > 0)
					? Validacio.validaSencerDefecte("\tNombre de places del VAGÓ (múltiple de 4)?", MAX_PLACES, defecte)
					: Validacio.validaSencer("\tNombre de places del VAGÓ (múltiple de 4)? ", MAX_PLACES);
			valid = places > 0 && places % nColumnes == 0;
			if (!valid)
				System.out.println("\tERROR: Les places han de ser un múltiple de 4 entre 4 i " + MAX_PLACES);
		} while (!valid);
		return places;
	}

	//*****************************************************************
	//Modifica el nombre de places. Només es pot fer si el vagó no té cap seient
	//RESERVAT ni OCUPAT, perquè la matriu de seients es torna a crear (es perdrien les vendes).
	public void modificaVago() {
		System.out.println("\tModificació del VAGÓ " + idVago + "\n\tPrem INTRO per mantindre el valor anterior");

		if (teReserves()) {
			System.out.println("\tERROR: El VAGÓ té seients reservats o ocupats: no es pot modificar");
			return;
		}

		//Si el vagó està en un trajecte, el nou nombre de places no pot fer
		//que el trajecte supere la capacitat del seu tren.
		int maxPlaces = MAX_PLACES;
		Trajecte trajecte = CompanyiaFerroviaria.trajecteDeVago(this);
		if (trajecte != null && trajecte.getTren() != null) {
			int placesAltresVagons = trajecte.placesTrajecte() - this.nPlaces;
			maxPlaces = Math.min(MAX_PLACES, trajecte.getTren().getCapacitat() - placesAltresVagons);
		}

		int novesPlaces;
		do {
			novesPlaces = demanaPlaces(this.nPlaces);
			if (novesPlaces > maxPlaces)
				System.out.println("\tERROR: Amb " + novesPlaces + " places el trajecte " + trajecte.getIdTrajecte()
						+ " supera la capacitat del tren (màxim per a aquest vagó: " + maxPlaces + ")");
		} while (novesPlaces > maxPlaces);

		if (novesPlaces != this.nPlaces) {
			this.nPlaces = novesPlaces;
			creaSeients();
		}
		System.out.println(this);
	}

	//*****************************************************************
	public void esborraVago() {
		System.out.println("VAGÓ " + idVago + " esborrat!");
	}

	//*****************************************************************
	//Mostra el mapa de seients del vagó amb la icona de l'estat de cada seient
	public void mapaVago() {
		System.out.println("\n\t=============  MAPA VAGO " + idVago + "  ================");
		System.out.print("\t Seient-> ");
		for (LletraSeient lletra : LletraSeient.values())
			System.out.print(" " + lletra + " ");
		System.out.println();

		for (int fila = 0; fila < seients.length; fila++) {
			System.out.print("\t Fila " + String.format("%02d", fila + 1) + ": ");
			for (int col = 0; col < nColumnes; col++)
				System.out.print(" " + seients[fila][col].iconaSeient() + " ");
			System.out.println();
		}
		System.out.println("\n\t SIMBOLOGIA: X=ocupat; O=lliure; ?=reservat");
		System.out.println("\t==============================================");
	}

	//*****************************************************************
	//Mostra el bitllet de tren amb totes les dades de la compra.
	//El tren i el vagó s'obtenen del trajecte i de l'objecte actual (this).
	public void imprimirBitllet(Seient s, Categoria c, Trajecte tra) {
		System.out.println("Imprimint el seu Bitllet de tren...");
		System.out.println("******************************************");
		System.out.println("* ********** BITLLET TREN ************** *");
		System.out.println("* Origen TRAJECTE:  " + tra.getOrigen());
		System.out.println("* Destí  TRAJECTE:  " + tra.getDesti());
		System.out.println("* Horari TRAJECTE:  " + tra.mostraDataFormatada());
		System.out.println("* Duració TRAJECTE: " + tra.getDuracio() + " min");
		System.out.println("* --------- DADES DEL SEIENT ------------ *");
		System.out.println("* TREN:      " + tra.getTren().getIdTren());
		System.out.println("* CATEGORIA: " + c.getNomCategoria());
		System.out.println("* VAGÓ:      " + this.idVago);
		System.out.println("* Seient:    " + s.getIdSeient());
		System.out.println("* Preu:      " + c.getPreu() + " €");
		System.out.println("******************************************");
	}

	//*****************************************************************
	//Retorna el seient a partir del seu identificador ("7A", "07a"...),
	//o null si el format no és correcte o el seient no existeix al vagó.
	public Seient getSeient(String idSeient) {
		String id = idSeient.trim().toUpperCase();
		//1 o 2 xifres per a la fila + una lletra entre A i D
		if (!id.matches("^\\d{1,2}[A-D]$"))
			return null;
		int fila = Integer.parseInt(id.substring(0, id.length() - 1));
		int columna = LletraSeient.valueOf(id.substring(id.length() - 1)).ordinal();
		if (fila < 1 || fila > seients.length)
			return null;
		return seients[fila - 1][columna];
	}

	//*****************************************************************
	//Retorna quants seients LLIURES té el vagó
	public int numSeientsLliures() {
		int lliures = 0;
		for (Seient[] fila : seients)
			for (Seient s : fila)
				if (s.verificaSeient())
					lliures++;
		return lliures;
	}

	//*****************************************************************
	//Retorna si algun seient està RESERVAT o OCUPAT (és a dir, si no tots estan lliures)
	public boolean teReserves() {
		return numSeientsLliures() < nPlaces;
	}

	//*****************************************************************
	@Override
	public String toString() {
		return "VAGÓ " + idVago + ": " + nPlaces + " places (" + seients.length + " files), "
				+ numSeientsLliures() + " lliures, " + (assignatVago ? "assignat" : "LLIURE");
	}

	//GETTERS & SETTERS
	//*****************************************************************
	public String getIdVago() {
		return idVago;
	}

	public void setIdVago(String idVago) {
		this.idVago = idVago;
	}

	public int getnPlaces() {
		return nPlaces;
	}

	public int getnColumnes() {
		return nColumnes;
	}

	public boolean isAssignatVago() {
		return assignatVago;
	}

	public void setAssignatVago(boolean assignatVago) {
		this.assignatVago = assignatVago;
	}

	public Seient[][] getSeients() {
		return seients;
	}
}
