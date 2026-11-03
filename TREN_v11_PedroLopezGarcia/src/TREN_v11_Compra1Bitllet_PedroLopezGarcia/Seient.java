package TREN_v10_Compra1Bitllet;

/**
 * Seient d'un Vagó.
 *
 * Cada seient té un identificador (número de fila amb 2 xifres + lletra, p.e. "07A")
 * i un estat de disponibilitat. El cicle de vida normal d'un seient en una compra és:
 *
 *     LLIURE --reservaSeient()--> RESERVAT --ocupaSeient()-----> OCUPAT
 *                                     |
 *                                     +----alliberaSeient()--> LLIURE  (pagament fallit o cancel·lat)
 *
 * Un seient OCUPAT ja està venut: sobre ell no es permet cap acció.
 */
public class Seient {

	private String idSeient;
	private Estat disponibilitat;

	//Estats possibles d'un seient i la icona que el representa al mapa del vagó
	public enum Estat {
		LLIURE('O'), OCUPAT('X'), RESERVAT('?');

		private final char icona;

		Estat(char icona) {
			this.icona = icona;
		}

		public char getIcona() {
			return icona;
		}
	}

	//*****************************************************************
	//Constructor 1: seient amb un estat inicial concret
	public Seient(String idSeient, Estat disponibilitat) {
		this.idSeient = idSeient;
		this.disponibilitat = disponibilitat;
	}

	//*****************************************************************
	//Constructor 2: tot seient nou naix LLIURE
	public Seient(String idSeient) {
		this(idSeient, Estat.LLIURE);
	}

	//*****************************************************************
	//Retorna si el seient es pot reservar, és a dir, si està LLIURE.
	//No mostra cap missatge: és qui crida el mètode qui decideix què dir a l'usuari.
	public boolean verificaSeient() {
		return this.disponibilitat == Estat.LLIURE;
	}

	//*****************************************************************
	//Retorna el caràcter que representa l'estat del seient (O, X, ?)
	public char iconaSeient() {
		return this.disponibilitat.getIcona();
	}

	//*****************************************************************
	//Canvia l'estat del seient a RESERVAT (compra en curs, encara no pagada)
	public void reservaSeient() {
		this.disponibilitat = Estat.RESERVAT;
	}

	//*****************************************************************
	//Canvia l'estat del seient a OCUPAT (bitllet pagat)
	public void ocupaSeient() {
		this.disponibilitat = Estat.OCUPAT;
	}

	//*****************************************************************
	//Canvia l'estat del seient a LLIURE (torna a estar disponible)
	public void alliberaSeient() {
		this.disponibilitat = Estat.LLIURE;
	}

	//*****************************************************************
	@Override
	public String toString() {
		return "Seient [" + idSeient + ": " + disponibilitat + "]";
	}

	//GETTERS & SETTERS
	//*****************************************************************
	public String getIdSeient() {
		return idSeient;
	}

	public void setIdSeient(String idSeient) {
		this.idSeient = idSeient;
	}

	public Estat getDisponibilitat() {
		return disponibilitat;
	}

	public void setDisponibilitat(Estat disponibilitat) {
		this.disponibilitat = disponibilitat;
	}
}
