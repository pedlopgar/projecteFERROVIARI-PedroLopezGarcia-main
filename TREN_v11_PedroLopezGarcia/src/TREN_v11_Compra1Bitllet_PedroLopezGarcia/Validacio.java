package TREN_v10_Compra1Bitllet;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Classe auxiliar estàtica amb tots els mètodes de lectura i validació de dades per teclat.
 *
 * IMPORTANT: tots els mètodes llegeixen d'un ÚNIC Scanner compartit.
 * Si cada mètode creara el seu propi "new Scanner(System.in)", cada Scanner guardaria
 * en el seu buffer part de l'entrada i un altre Scanner ja no la podria llegir
 * (es "perden" línies). A més, a la v2.x (fils) caldrà sincronitzar l'accés al teclat,
 * i això només és possible si hi ha un únic punt d'entrada.
 *
 * Tots els mètodes llegeixen línies senceres (nextLine) per no barrejar next() i nextLine().
 */
public class Validacio {

	//Únic Scanner de tota l'aplicació
	private static final Scanner teclat = new Scanner(System.in);

	//Patrons (expressions regulars) reutilitzats
	private static final String PATRO_SENCER = "^\\d{1,9}$";
	private static final String PATRO_DATA = "^\\d{2}/\\d{2}/\\d{4}$";
	private static final String PATRO_HORA = "^\\d{2}:\\d{2}$";
	private static final String PATRO_MONEDA = "^\\d{1,6}([.,]\\d{1,2})?$";

	//*****************************************************************
	//Mostra el missatge i retorna la línia introduïda (sense espais als extrems)
	private static String llegeixLinia(String missatge) {
		System.out.print(missatge);
		return teclat.nextLine().trim();
	}

	//*****************************************************************
	//Retorna un sencer entre 0 i maxSencer.
	//El 0 es reserva per a "cancel·lar / tornar enrere": qui crida el mètode l'ha de tractar.
	public static int validaSencer(String missatge, int maxSencer) {
		int sencer = -1;
		boolean valid = false;
		do {
			String cadena = llegeixLinia(missatge);
			if (cadena.matches(PATRO_SENCER)) {
				sencer = Integer.parseInt(cadena);
				valid = sencer <= maxSencer;
				if (!valid)
					System.out.println("\tERROR: Número fora de rang [0-" + maxSencer + "]");
			} else
				System.out.println("\tERROR: Valor no numèric");
		} while (!valid);
		return sencer;
	}

	//*****************************************************************
	//Retorna un sencer entre 1 i maxSencer o, si es prem INTRO, el valor per defecte
	public static int validaSencerDefecte(String missatge, int maxSencer, int defecte) {
		int sencer;
		boolean valid = false;
		do {
			sencer = defecte;
			String cadena = llegeixLinia(missatge + " [" + defecte + "] ");
			if (cadena.isEmpty())
				valid = true; //es manté el valor per defecte
			else if (cadena.matches(PATRO_SENCER)) {
				sencer = Integer.parseInt(cadena);
				valid = sencer >= 1 && sencer <= maxSencer;
				if (!valid)
					System.out.println("\tERROR: Número fora de rang [1-" + maxSencer + "]");
			} else
				System.out.println("\tERROR: Valor no numèric");
		} while (!valid);
		return sencer;
	}

	//*****************************************************************
	//Retorna true si es respon S i false si es respon N (majúscules o minúscules)
	public static boolean validaBoolea(String missatge) {
		String cadena;
		boolean valid;
		do {
			cadena = llegeixLinia(missatge + " ").toUpperCase();
			valid = cadena.equals("S") || cadena.equals("N");
			if (!valid)
				System.out.println("\tERROR: Respon S o N");
		} while (!valid);
		return cadena.equals("S");
	}

	//*****************************************************************
	//Com validaBoolea, però si es prem INTRO retorna el valor per defecte
	public static boolean validaBooleaDefecte(String missatge, boolean defecte) {
		String cadena;
		boolean valid;
		do {
			cadena = llegeixLinia(missatge + " [" + (defecte ? "S" : "N") + "] ").toUpperCase();
			valid = cadena.isEmpty() || cadena.equals("S") || cadena.equals("N");
			if (!valid)
				System.out.println("\tERROR: Respon S o N");
		} while (!valid);
		return cadena.isEmpty() ? defecte : cadena.equals("S");
	}

	//*****************************************************************
	//Retorna una cadena NO buida
	public static String validaCadena(String missatge) {
		String cadena;
		do {
			cadena = llegeixLinia(missatge);
			if (cadena.isEmpty())
				System.out.println("\tERROR: El valor no pot estar buit");
		} while (cadena.isEmpty());
		return cadena;
	}

	//*****************************************************************
	//Retorna la cadena introduïda o, si es prem INTRO, el valor per defecte
	public static String validaCadenaDefecte(String missatge, String defecte) {
		String cadena = llegeixLinia(missatge + " [" + defecte + "] ");
		return cadena.isEmpty() ? defecte : cadena;
	}

	//*****************************************************************
	//Converteix dia, mes, any, hora i minut en un Calendar.
	//Retorna null si la data no existeix (p.e. 31/02/2027).
	//ATENCIÓ: a Calendar els mesos van de 0 (gener) a 11 (desembre), per això es resta 1.
	private static Calendar creaData(int dia, int mes, int any, int hora, int minut) {
		Calendar data = Calendar.getInstance();
		data.setLenient(false); //no "corregeix" dates impossibles: llança una excepció
		data.clear();
		data.set(any, mes - 1, dia, hora, minut, 0);
		try {
			data.getTime(); //força la validació
			return data;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	//*****************************************************************
	//Demana una data (dd/mm/aaaa) i una hora (hh:mm) i retorna el Calendar corresponent
	public static Calendar validaData(String missatge) {
		Calendar data;
		do {
			data = null;
			String strData = llegeixLinia(missatge + " (dd/mm/aaaa) ");
			if (strData.matches(PATRO_DATA)) {
				String strHora = llegeixLinia("\tHora? (hh:mm) ");
				if (strHora.matches(PATRO_HORA))
					data = construeixData(strData, strHora);
			}
			if (data == null)
				System.out.println("\tERROR: Data o hora no vàlides");
		} while (data == null);
		return data;
	}

	//*****************************************************************
	//Com validaData però, si es prem INTRO, es manté la data (o l'hora) anterior
	public static Calendar validaDataDefecte(String missatge, Calendar antiga) {
		String dataAntiga = String.format("%02d/%02d/%04d", antiga.get(Calendar.DAY_OF_MONTH),
				antiga.get(Calendar.MONTH) + 1, antiga.get(Calendar.YEAR));
		String horaAntiga = String.format("%02d:%02d", antiga.get(Calendar.HOUR_OF_DAY),
				antiga.get(Calendar.MINUTE));
		Calendar data;
		do {
			data = null;
			String strData = llegeixLinia(missatge + " (dd/mm/aaaa) [" + dataAntiga + "] ");
			if (strData.isEmpty())
				strData = dataAntiga;
			String strHora = llegeixLinia("\tHora? (hh:mm) [" + horaAntiga + "] ");
			if (strHora.isEmpty())
				strHora = horaAntiga;
			if (strData.matches(PATRO_DATA) && strHora.matches(PATRO_HORA))
				data = construeixData(strData, strHora);
			if (data == null)
				System.out.println("\tERROR: Data o hora no vàlides");
		} while (data == null);
		return data;
	}

	//*****************************************************************
	//Passa les cadenes "dd/mm/aaaa" i "hh:mm" (ja comprovades amb el patró) a Calendar
	private static Calendar construeixData(String strData, String strHora) {
		String[] d = strData.split("/");
		String[] h = strHora.split(":");
		return creaData(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]),
				Integer.parseInt(h[0]), Integer.parseInt(h[1]));
	}

	//*****************************************************************
	//Retorna un import positiu (accepta 12, 12.5, 12,50)
	public static BigDecimal validaMoneda(String missatge) {
		BigDecimal import_;
		do {
			String cadena = llegeixLinia(missatge);
			import_ = converteixImport(cadena);
			if (import_ == null)
				System.out.println("\tERROR: Import no vàlid (p.e. 16 o 16.50)");
		} while (import_ == null);
		return import_;
	}

	//*****************************************************************
	//Com validaMoneda, però si es prem INTRO retorna l'import anterior
	public static BigDecimal validaMonedaDefecte(String missatge, BigDecimal antiga) {
		BigDecimal import_;
		do {
			String cadena = llegeixLinia(missatge + " [" + antiga + "] ");
			import_ = cadena.isEmpty() ? antiga : converteixImport(cadena);
			if (import_ == null)
				System.out.println("\tERROR: Import no vàlid (p.e. 16 o 16.50)");
		} while (import_ == null);
		return import_;
	}

	//*****************************************************************
	//Retorna l'import com a BigDecimal, o null si no és vàlid o no és positiu
	private static BigDecimal converteixImport(String cadena) {
		if (!cadena.matches(PATRO_MONEDA))
			return null;
		BigDecimal import_ = new BigDecimal(cadena.replace(',', '.'));
		return import_.signum() > 0 ? import_ : null;
	}
}
