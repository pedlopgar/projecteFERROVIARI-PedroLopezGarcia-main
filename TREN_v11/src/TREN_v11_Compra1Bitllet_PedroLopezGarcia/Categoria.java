package TREN_v10_Compra1Bitllet;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Categoria d'un trajecte (el que habitualment s'anomena "Classe": Turista, Preferent...).
 * L'anomenem Categoria per no confondre-la amb el concepte de classe de Java.
 *
 * Una Categoria fixa el preu dels seients i inclou un o més Vagons sencers.
 * Cada Categoria només es pot assignar a un Trajecte (atribut assignadaCategoria).
 */
public class Categoria {

	private String idCategoria;
	private TipusCategoria nomCategoria;
	private String descCategoria;
	private BigDecimal preu;
	private boolean assignadaCategoria;
	private ArrayList<Vago> vagonsCategoria;

	public enum TipusCategoria {Turista, TuristaPlus, Preferent}

	//*****************************************************************
	//Constructor 1: categoria sense vagons (s'usa a les dades inicials)
	public Categoria(String idCategoria, TipusCategoria nomCategoria, String descCategoria, BigDecimal preu) {
		this.idCategoria = idCategoria;
		this.nomCategoria = nomCategoria;
		this.descCategoria = descCategoria;
		this.preu = preu;
		this.assignadaCategoria = false;
		this.vagonsCategoria = new ArrayList<Vago>();
	}

	//*****************************************************************
	//Constructor INTERACTIU. L'identificador ja arriba validat des del menú.
	public Categoria(String idCategoria) {
		System.out.println("Creació de la CATEGORIA " + idCategoria + "\n>>>>>>>>>>>>>>>>>>>");
		this.idCategoria = idCategoria;
		this.nomCategoria = demanaTipus(null);
		this.descCategoria = Validacio.validaCadena("\tDescripció de la CATEGORIA? ");
		this.preu = Validacio.validaMoneda("\tPreu del seient de la CATEGORIA? ");
		this.assignadaCategoria = false;
		this.vagonsCategoria = new ArrayList<Vago>();

		//Es poden afegir tants vagons lliures com es vulga; afegirVago retorna null per acabar
		while (afegirVago("\tVols afegir un VAGÓ a la CATEGORIA? [S/N]") != null)
			;
		System.out.println("<<<<<<<<<<<<<<<<<<<");
		System.out.println(this);
	}

	//*****************************************************************
	//Demana el tipus de categoria. Si actual != null, INTRO el manté.
	//Es fa servir la posició a l'enum (1..3) en lloc d'un switch: així no s'oblida cap valor.
	private TipusCategoria demanaTipus(TipusCategoria actual) {
		TipusCategoria[] tipus = TipusCategoria.values();
		String missatge = "\tTipus de CATEGORIA? (1: Turista, 2: TuristaPlus, 3: Preferent)";
		if (actual != null)
			return tipus[Validacio.validaSencerDefecte(missatge, tipus.length, actual.ordinal() + 1) - 1];
		int opcio;
		do {
			opcio = Validacio.validaSencer(missatge + " ", tipus.length);
		} while (opcio == 0);
		return tipus[opcio - 1];
	}

	//*****************************************************************
	//Modifica la categoria: cada atribut amb valor per defecte (INTRO) i,
	//després, permet llevar i afegir vagons.
	public void modificaCategoria() {
		System.out.println("\tModificació de la CATEGORIA " + idCategoria + "\n\tPrem INTRO per mantindre el valor anterior");
		this.nomCategoria = demanaTipus(this.nomCategoria);
		this.descCategoria = Validacio.validaCadenaDefecte("\tDescripció de la CATEGORIA?", this.descCategoria);
		this.preu = Validacio.validaMonedaDefecte("\tPreu del seient de la CATEGORIA?", this.preu);

		while (llevarVago("\tVols llevar un VAGÓ de la CATEGORIA? [S/N]") != null)
			;
		while (afegirVago("\tVols afegir un VAGÓ a la CATEGORIA? [S/N]") != null)
			;
		System.out.println(this);
	}

	//*****************************************************************
	//Allibera tots els vagons de la categoria perquè es puguen assignar a una altra
	public void esborraCategoria() {
		for (Vago v : vagonsCategoria)
			v.setAssignatVago(false);
		vagonsCategoria.clear();
		System.out.println("CATEGORIA " + idCategoria + " esborrada!");
	}

	//*****************************************************************
	//Mostra, numerats des d'1, els vagons de la categoria i retorna quants n'hi ha
	public int llistarVagonsCategoria() {
		for (int i = 0; i < vagonsCategoria.size(); i++)
			System.out.println(" " + (i + 1) + ": " + vagonsCategoria.get(i));
		return vagonsCategoria.size();
	}

	//*****************************************************************
	//Si l'usuari ho confirma, assigna un vagó LLIURE a la categoria, el marca com a
	//assignat i el retorna. Retorna null si no s'afegeix cap vagó.
	public Vago afegirVago(String missatge) {
		if (!Validacio.validaBoolea(missatge))
			return null;

		//Només s'oferixen els vagons lliures. Es tria sobre ESTA llista, no sobre la general:
		//així és impossible triar un vagó que ja està assignat a una altra categoria.
		ArrayList<Vago> lliures = CompanyiaFerroviaria.llistaVagonsLliures();
		if (lliures.isEmpty()) {
			System.out.println("\tNo hi ha VAGONS lliures per assignar");
			return null;
		}
		int opcio = Validacio.validaSencer("\tTria un VAGÓ lliure [0 = cancel·lar]: ", lliures.size());
		if (opcio == 0)
			return null;
		Vago vago = lliures.get(opcio - 1);

		//Si la categoria ja està en un trajecte, el vagó no pot fer superar la capacitat del tren
		int disponibles = placesDisponibles();
		if (vago.getnPlaces() > disponibles) {
			System.out.println("\tERROR: El VAGÓ té " + vago.getnPlaces() + " places i el tren només en pot admetre "
					+ disponibles + " més");
			return null;
		}

		vagonsCategoria.add(vago);
		vago.setAssignatVago(true);
		System.out.println("\tAfegit el " + vago);
		return vago;
	}

	//*****************************************************************
	//Si l'usuari ho confirma, lleva un vagó de la categoria (només si no té reserves),
	//el marca com a lliure i el retorna. Retorna null si no es lleva cap vagó.
	public Vago llevarVago(String missatge) {
		if (vagonsCategoria.isEmpty() || !Validacio.validaBoolea(missatge))
			return null;
		int opcio = Validacio.validaSencer("\tTria el VAGÓ a llevar [0 = cancel·lar]: ", llistarVagonsCategoria());
		if (opcio == 0)
			return null;
		Vago vago = vagonsCategoria.get(opcio - 1);
		if (vago.teReserves()) {
			System.out.println("\tERROR: El VAGÓ té seients reservats o ocupats: no es pot llevar");
			return null;
		}
		vagonsCategoria.remove(vago);
		vago.setAssignatVago(false);
		System.out.println("\tLlevat el VAGÓ " + vago.getIdVago());
		return vago;
	}

	//*****************************************************************
	//Places que encara es poden afegir a la categoria sense superar la capacitat
	//del tren del seu trajecte. Si no està en cap trajecte (o no té tren), no hi ha límit.
	private int placesDisponibles() {
		Trajecte trajecte = CompanyiaFerroviaria.trajecteDeCategoria(this);
		if (trajecte == null || trajecte.getTren() == null)
			return Integer.MAX_VALUE;
		return trajecte.getTren().getCapacitat() - trajecte.placesTrajecte();
	}

	//*****************************************************************
	//Retorna si algun vagó de la categoria té seients reservats o ocupats
	public boolean teReserves() {
		for (Vago v : vagonsCategoria)
			if (v.teReserves())
				return true;
		return false;
	}

	//*****************************************************************
	//Retorna la suma de places de tots els vagons de la categoria
	public int placesCategoria() {
		int places = 0;
		for (Vago v : vagonsCategoria)
			places += v.getnPlaces();
		return places;
	}

	//*****************************************************************
	@Override
	public String toString() {
		String vagons = "";
		for (Vago v : vagonsCategoria)
			vagons += (vagons.isEmpty() ? "" : ", ") + v.getIdVago();
		return "CATEGORIA " + idCategoria + ": " + nomCategoria + " (" + descCategoria + "), "
				+ preu + " €, vagons [" + vagons + "], " + placesCategoria() + " places, "
				+ (assignadaCategoria ? "assignada" : "LLIURE");
	}

	//GETTERS & SETTERS
	//*****************************************************************
	public String getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(String idCategoria) {
		this.idCategoria = idCategoria;
	}

	public TipusCategoria getNomCategoria() {
		return nomCategoria;
	}

	public void setNomCategoria(TipusCategoria nomCategoria) {
		this.nomCategoria = nomCategoria;
	}

	public String getDescCategoria() {
		return descCategoria;
	}

	public void setDescCategoria(String descCategoria) {
		this.descCategoria = descCategoria;
	}

	public BigDecimal getPreu() {
		return preu;
	}

	public void setPreu(BigDecimal preu) {
		this.preu = preu;
	}

	public boolean isAssignadaCategoria() {
		return assignadaCategoria;
	}

	public void setAssignadaCategoria(boolean assignadaCategoria) {
		this.assignadaCategoria = assignadaCategoria;
	}

	public ArrayList<Vago> getVagonsCategoria() {
		return vagonsCategoria;
	}
}
