package TREN_v21_Compra1Bitllet_Threads_PedroLopezGarcia;

/**
 * Tren que cobreix trajectes.
 * La capacitat és el màxim de places: la suma de places dels vagons de cada
 * trajecte que cobreix el tren no la pot superar.
 */
public class Tren {

	//Capacitat màxima admesa per a un tren
	public static final int MAX_CAPACITAT = 1000;

	private String idTren;
	private int capacitat;

	//*****************************************************************
	//Constructor NO interactiu
	public Tren(String idTren, int capacitat) {
		this.idTren = idTren;
		this.capacitat = capacitat;
	}

	//*****************************************************************
	//Constructor INTERACTIU. L'identificador ja arriba validat des del menú.
	public Tren(String idTren) {
		System.out.println("Creació del TREN " + idTren + "\n>>>>>>>>>>>>>>>>>>>");
		this.idTren = idTren;
		do {
			this.capacitat = Validacio.validaSencer("\tCapacitat (places) del TREN? ", MAX_CAPACITAT);
			if (capacitat == 0)
				System.out.println("\tERROR: La capacitat ha de ser major que 0");
		} while (capacitat == 0);
		System.out.println("<<<<<<<<<<<<<<<<<<<");
	}

	//*****************************************************************
	//Modifica la capacitat. La nova capacitat no pot ser menor que les places
	//que ja ocupen els vagons del trajecte més gran que cobreix aquest tren.
	public void modificaTren() {
		System.out.println("\tModificació del TREN " + idTren + "\n\tPrem INTRO per mantindre el valor anterior");
		int minim = CompanyiaFerroviaria.placesMaximesTren(this);
		int nova;
		do {
			nova = Validacio.validaSencerDefecte("\tCapacitat del TREN?", MAX_CAPACITAT, this.capacitat);
			if (nova < minim)
				System.out.println("\tERROR: Algun trajecte d'aquest tren ja té " + minim + " places en vagons");
		} while (nova < minim);
		this.capacitat = nova;
		System.out.println(this);
	}

	//*****************************************************************
	public void esborraTren() {
		System.out.println("TREN " + idTren + " esborrat!");
	}

	//*****************************************************************
	@Override
	public String toString() {
		return "TREN " + idTren + ": capacitat " + capacitat + " places";
	}

	//GETTERS & SETTERS
	//*****************************************************************
	public String getIdTren() {
		return idTren;
	}

	public void setIdTren(String idTren) {
		this.idTren = idTren;
	}

	public int getCapacitat() {
		return capacitat;
	}

	public void setCapacitat(int capacitat) {
		this.capacitat = capacitat;
	}
}
