package TREN_v10_Compra1Bitllet;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Trajecte entre dues ciutats en un dia i hora concrets.
 *
 * Un trajecte està cobert per UN tren (pot no tindre'n encara: tren == null)
 * i ofereix una o més Categories. Regla de capacitat:
 *     suma de places de tots els vagons de les categories <= capacitat del tren
 */
public class Trajecte {

	//Duració màxima d'un trajecte en minuts (24 hores)
	public static final int MAX_DURACIO = 24 * 60;

	private String idTrajecte;
	private String origen;
	private String desti;
	private Calendar diaHora;
	private int duracio;
	private Tren tren;
	private ArrayList<Categoria> categoriesTrajecte;

	//*****************************************************************
	//Constructor 1: trajecte amb la llista de categories buida
	public Trajecte(String idTrajecte, String origen, String desti, Calendar diaHora, int duracio, Tren tren) {
		this(idTrajecte, origen, desti, diaHora, duracio, tren, new ArrayList<Categoria>());
	}

	//*****************************************************************
	//Constructor 2: trajecte amb una llista de categories donada
	public Trajecte(String idTrajecte, String origen, String desti, Calendar diaHora, int duracio, Tren tren,
			ArrayList<Categoria> categoriesTrajecte) {
		this.idTrajecte = idTrajecte;
		this.origen = origen;
		this.desti = desti;
		this.diaHora = diaHora;
		this.duracio = duracio;
		this.tren = tren;
		this.categoriesTrajecte = categoriesTrajecte;
	}

	//*****************************************************************
	//Constructor INTERACTIU. L'identificador ja arriba validat des del menú.
	public Trajecte(String idTrajecte) {
		System.out.println("Creació del TRAJECTE " + idTrajecte + "\n>>>>>>>>>>>>>>>>>>>");
		this.idTrajecte = idTrajecte;
		this.origen = Validacio.validaCadena("\tCiutat ORIGEN del TRAJECTE? ");
		this.desti = Validacio.validaCadena("\tCiutat DESTÍ del TRAJECTE? ");
		this.diaHora = Validacio.validaData("\tDia d'eixida del TRAJECTE?");
		this.duracio = demanaDuracio(-1);
		this.categoriesTrajecte = new ArrayList<Categoria>();

		//El tren és opcional en crear el trajecte (es pot assignar després)
		this.tren = null;
		if (CompanyiaFerroviaria.llistaTrens() > 0) {
			int opcio = Validacio.validaSencer("\tTria el TREN del TRAJECTE [0 = sense tren]: ",
					CompanyiaFerroviaria.trens.size());
			if (opcio > 0)
				this.tren = CompanyiaFerroviaria.trens.get(opcio - 1);
		}

		while (afegirCategoria("\tVols afegir una CATEGORIA al TRAJECTE? [S/N]") != null)
			;
		System.out.println("<<<<<<<<<<<<<<<<<<<");
		System.out.println(this);
	}

	//*****************************************************************
	//Demana la duració en minuts (major que 0). Si defecte > 0, INTRO el manté.
	private int demanaDuracio(int defecte) {
		if (defecte > 0)
			return Validacio.validaSencerDefecte("\tDuració del TRAJECTE en minuts?", MAX_DURACIO, defecte);
		int minuts;
		do {
			minuts = Validacio.validaSencer("\tDuració del TRAJECTE en minuts? ", MAX_DURACIO);
		} while (minuts == 0);
		return minuts;
	}

	//*****************************************************************
	//Modifica el trajecte. Cada atribut es pot mantindre amb INTRO.
	//El tren només es pot canviar si el trajecte no té reserves i el nou tren té capacitat suficient.
	public void modificaTrajecte() {
		System.out.println("\tModificació del TRAJECTE " + idTrajecte + "\n\tPrem INTRO per mantindre el valor anterior");
		this.origen = Validacio.validaCadenaDefecte("\tCiutat ORIGEN del TRAJECTE?", this.origen);
		this.desti = Validacio.validaCadenaDefecte("\tCiutat DESTÍ del TRAJECTE?", this.desti);
		this.diaHora = Validacio.validaDataDefecte("\tDia d'eixida del TRAJECTE?", this.diaHora);
		this.duracio = demanaDuracio(this.duracio);

		System.out.println("\tTren actual: " + (tren == null ? "cap" : tren));
		if (Validacio.validaBoolea("\tVols canviar de TREN? [S/N]"))
			canviaTren();

		if (Validacio.validaBoolea("\tVols canviar les CATEGORIES del TRAJECTE? [S/N]")) {
			while (modificarCategoria("\tVols modificar una CATEGORIA del TRAJECTE? [S/N]") != null)
				;
			while (llevarCategoria("\tVols llevar una CATEGORIA del TRAJECTE? [S/N]") != null)
				;
			while (afegirCategoria("\tVols afegir una CATEGORIA al TRAJECTE? [S/N]") != null)
				;
		}
		System.out.println(this);
	}

	//*****************************************************************
	//Canvia el tren del trajecte comprovant les dues regles
	private void canviaTren() {
		if (teReserves()) {
			System.out.println("\tERROR: El TRAJECTE té seients reservats o ocupats: no es pot canviar de TREN");
			return;
		}
		if (CompanyiaFerroviaria.llistaTrens() == 0)
			return;
		int opcio = Validacio.validaSencer("\tTria el nou TREN [0 = mantindre l'actual]: ",
				CompanyiaFerroviaria.trens.size());
		if (opcio == 0)
			return;
		Tren nou = CompanyiaFerroviaria.trens.get(opcio - 1);
		if (nou.getCapacitat() < placesTrajecte()) {
			System.out.println("\tERROR: El TREN " + nou.getIdTren() + " té " + nou.getCapacitat()
					+ " places i el TRAJECTE en necessita " + placesTrajecte());
			return;
		}
		this.tren = nou;
	}

	//*****************************************************************
	//Allibera totes les categories del trajecte perquè es puguen assignar a un altre
	public void esborraTrajecte() {
		for (Categoria c : categoriesTrajecte)
			c.setAssignadaCategoria(false);
		categoriesTrajecte.clear();
		System.out.println("TRAJECTE " + idTrajecte + " esborrat!");
	}

	//*****************************************************************
	//Mostra, numerades des d'1, les categories del trajecte i retorna quantes n'hi ha
	public int llistarCategoriesTrajecte() {
		for (int i = 0; i < categoriesTrajecte.size(); i++)
			System.out.println(" " + (i + 1) + ": " + categoriesTrajecte.get(i));
		return categoriesTrajecte.size();
	}

	//*****************************************************************
	//Si l'usuari ho confirma, afegeix una categoria LLIURE al trajecte (sense superar
	//la capacitat del tren), la marca com a assignada i la retorna. Si no, retorna null.
	public Categoria afegirCategoria(String missatge) {
		//Sense tren no es pot comprovar la capacitat: primer cal assignar-ne un
		if (tren == null) {
			System.out.println("\tEl TRAJECTE no té TREN: no se li poden afegir CATEGORIES");
			return null;
		}
		if (!Validacio.validaBoolea(missatge))
			return null;

		ArrayList<Categoria> lliures = CompanyiaFerroviaria.llistaCategoriesLliures();
		if (lliures.isEmpty()) {
			System.out.println("\tNo hi ha CATEGORIES lliures per assignar");
			return null;
		}
		int opcio = Validacio.validaSencer("\tTria una CATEGORIA lliure [0 = cancel·lar]: ", lliures.size());
		if (opcio == 0)
			return null;
		Categoria categoria = lliures.get(opcio - 1);

		if (placesTrajecte() + categoria.placesCategoria() > tren.getCapacitat()) {
			System.out.println("\tERROR: Amb aquesta CATEGORIA el TRAJECTE tindria "
					+ (placesTrajecte() + categoria.placesCategoria()) + " places i el TREN només en té "
					+ tren.getCapacitat());
			return null;
		}

		categoriesTrajecte.add(categoria);
		categoria.setAssignadaCategoria(true);
		System.out.println("\tAfegida la CATEGORIA " + categoria.getIdCategoria());
		return categoria;
	}

	//*****************************************************************
	//Permet modificar una de les categories del trajecte i la retorna (null si no se'n modifica cap)
	public Categoria modificarCategoria(String missatge) {
		if (categoriesTrajecte.isEmpty() || !Validacio.validaBoolea(missatge))
			return null;
		int opcio = Validacio.validaSencer("\tTria la CATEGORIA a modificar [0 = cancel·lar]: ",
				llistarCategoriesTrajecte());
		if (opcio == 0)
			return null;
		Categoria categoria = categoriesTrajecte.get(opcio - 1);
		categoria.modificaCategoria();
		return categoria;
	}

	//*****************************************************************
	//Permet llevar una categoria del trajecte (només si no té reserves) i la retorna
	public Categoria llevarCategoria(String missatge) {
		if (categoriesTrajecte.isEmpty() || !Validacio.validaBoolea(missatge))
			return null;
		int opcio = Validacio.validaSencer("\tTria la CATEGORIA a llevar [0 = cancel·lar]: ",
				llistarCategoriesTrajecte());
		if (opcio == 0)
			return null;
		Categoria categoria = categoriesTrajecte.get(opcio - 1);
		if (categoria.teReserves()) {
			System.out.println("\tERROR: La CATEGORIA té seients reservats o ocupats: no es pot llevar");
			return null;
		}
		categoriesTrajecte.remove(categoria);
		categoria.setAssignadaCategoria(false);
		System.out.println("\tLlevada la CATEGORIA " + categoria.getIdCategoria());
		return categoria;
	}

	//*****************************************************************
	//Retorna si alguna categoria del trajecte té seients reservats o ocupats
	public boolean teReserves() {
		for (Categoria c : categoriesTrajecte)
			if (c.teReserves())
				return true;
		return false;
	}

	//*****************************************************************
	//Retorna la suma de places de tots els vagons del trajecte
	public int placesTrajecte() {
		int places = 0;
		for (Categoria c : categoriesTrajecte)
			places += c.placesCategoria();
		return places;
	}

	//*****************************************************************
	//Retorna el dia i hora d'eixida en format dd/mm/aaaa hh:mm.
	//Calendar.MONTH comença en 0 (gener), per això se suma 1.
	public String mostraDataFormatada() {
		return String.format("%02d/%02d/%04d %02d:%02d",
				diaHora.get(Calendar.DAY_OF_MONTH), diaHora.get(Calendar.MONTH) + 1, diaHora.get(Calendar.YEAR),
				diaHora.get(Calendar.HOUR_OF_DAY), diaHora.get(Calendar.MINUTE));
	}

	//*****************************************************************
	@Override
	public String toString() {
		String categories = "";
		for (Categoria c : categoriesTrajecte)
			categories += (categories.isEmpty() ? "" : ", ") + c.getIdCategoria() + "-" + c.getNomCategoria();
		return "TRAJECTE " + idTrajecte + ": " + origen + " -> " + desti + ", " + mostraDataFormatada()
				+ ", " + duracio + " min, tren " + (tren == null ? "CAP" : tren.getIdTren())
				+ ", categories [" + categories + "], " + placesTrajecte() + " places";
	}

	//GETTERS & SETTERS
	//*****************************************************************
	public String getIdTrajecte() {
		return idTrajecte;
	}

	public void setIdTrajecte(String idTrajecte) {
		this.idTrajecte = idTrajecte;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getDesti() {
		return desti;
	}

	public void setDesti(String desti) {
		this.desti = desti;
	}

	public Calendar getDiaHora() {
		return diaHora;
	}

	public void setDiaHora(Calendar diaHora) {
		this.diaHora = diaHora;
	}

	public int getDuracio() {
		return duracio;
	}

	public void setDuracio(int duracio) {
		this.duracio = duracio;
	}

	public Tren getTren() {
		return tren;
	}

	public void setTren(Tren tren) {
		this.tren = tren;
	}

	public ArrayList<Categoria> getCategoriesTrajecte() {
		return categoriesTrajecte;
	}

	public void setCategoriesTrajecte(ArrayList<Categoria> categoriesTrajecte) {
		this.categoriesTrajecte = categoriesTrajecte;
	}
}
