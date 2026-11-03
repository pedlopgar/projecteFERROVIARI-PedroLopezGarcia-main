package TREN_v10_Compra1Bitllet;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Classe principal del projecte FERROVIARI (v1.0: ESQUELET per a l'alumnat).
 * <p>
 * TREBALL 1: implementa els mètodes pagamentBitllet() i compraBitlletTrajecte().
 * <p>
 * Guarda, en ArrayLists estàtics, tots els objectes de la companyia i
 * gestiona el menú. Relacions entre objectes:
 * <p>
 * Trajecte --(1 tren)--> Tren
 * |
 * +--(1..n)--> Categoria --(1..n)--> Vago --(matriu)--> Seient
 * <p>
 * Una Categoria només pot estar en un Trajecte i un Vagó només en una Categoria.
 */
public class CompanyiaFerroviaria {

    static String nomCompanyia = "RENFE";
    static ArrayList<Trajecte> trajectes = new ArrayList<Trajecte>();
    static ArrayList<Tren> trens = new ArrayList<Tren>();
    static ArrayList<Categoria> categories = new ArrayList<Categoria>();
    static ArrayList<Vago> vagons = new ArrayList<Vago>();

    //Temps de retard que simula el procés de pagament (mil·lisegons)
    static final int TEMPS_PAGAMENT = 3000;

    public static void main(String[] args) {
        carregaDadesInicials();

        int opcio;
        do {
            opcio = menu();
            switch (opcio) {

                //===================== VAGONS =====================
                case 1: //Crear VAGÓ
                    String idVago = demanaIdentificador("VAGÓ");
                    if (idVago != null) {
                        Vago vago = new Vago(idVago);
                        vagons.add(vago);
                        System.out.println(vago);
                    }
                    break;

                case 2: //Modificar VAGÓ
                    int posVagoMod = triaPosicio(llistaVagons(), "VAGÓ", "modificar");
                    Vago vagoMod = (posVagoMod < 0) ? null : vagons.get(posVagoMod);
                    if (vagoMod != null)
                        vagoMod.modificaVago();
                    break;

                case 3: //Eliminar VAGÓ
                    int posVagoEsb = triaPosicio(llistaVagons(), "VAGÓ", "eliminar");
                    Vago vagoEsb = (posVagoEsb < 0) ? null : vagons.get(posVagoEsb);
                    if (vagoEsb != null) {
                        //Un vagó assignat està en ús: primer s'ha de llevar de la seua categoria
                        if (vagoEsb.isAssignatVago())
                            System.out.println("\tERROR: El VAGÓ està assignat a una CATEGORIA: no es pot eliminar");
                        else {
                            vagoEsb.esborraVago();
                            vagons.remove(vagoEsb);
                        }
                    }
                    break;

                //===================== CATEGORIES =====================
                case 4: //Crear CATEGORIA
                    String idCategoria = demanaIdentificador("CATEGORIA");
                    if (idCategoria != null)
                        categories.add(new Categoria(idCategoria));
                    break;

                case 5: //Modificar CATEGORIA
                    int posCatMod = triaPosicio(llistaCategories(), "CATEGORIA", "modificar");
                    Categoria catMod = (posCatMod < 0) ? null : categories.get(posCatMod);
                    if (catMod != null)
                        catMod.modificaCategoria();
                    break;

                case 6: //Eliminar CATEGORIA
                    int posCatEsb = triaPosicio(llistaCategories(), "CATEGORIA", "eliminar");
                    Categoria catEsb = (posCatEsb < 0) ? null : categories.get(posCatEsb);
                    if (catEsb != null) {
                        if (catEsb.isAssignadaCategoria())
                            System.out.println("\tERROR: La CATEGORIA està assignada a un TRAJECTE: no es pot eliminar");
                        else {
                            catEsb.esborraCategoria(); //allibera els seus vagons
                            categories.remove(catEsb);
                        }
                    }
                    break;

                //===================== TRENS =====================
                case 7: //Crear TREN
                    String idTren = demanaIdentificador("TREN");
                    if (idTren != null) {
                        Tren tren = new Tren(idTren);
                        trens.add(tren);
                        System.out.println(tren);
                    }
                    break;

                case 8: //Modificar TREN
                    int posTrenMod = triaPosicio(llistaTrens(), "TREN", "modificar");
                    Tren trenMod = (posTrenMod < 0) ? null : trens.get(posTrenMod);
                    if (trenMod != null)
                        trenMod.modificaTren();
                    break;

                case 9: //Eliminar TREN
                    int posTrenEsb = triaPosicio(llistaTrens(), "TREN", "eliminar");
                    Tren trenEsb = (posTrenEsb < 0) ? null : trens.get(posTrenEsb);
                    if (trenEsb != null) {
                        if (trenEnUs(trenEsb))
                            System.out.println("\tERROR: El TREN cobreix algun TRAJECTE: no es pot eliminar");
                        else {
                            trenEsb.esborraTren();
                            trens.remove(trenEsb);
                        }
                    }
                    break;

                //===================== TRAJECTES =====================
                case 10: //Crear TRAJECTE
                    String idTrajecte = demanaIdentificador("TRAJECTE");
                    if (idTrajecte != null)
                        trajectes.add(new Trajecte(idTrajecte));
                    break;

                case 11: //Modificar TRAJECTE
                    int posTraMod = triaPosicio(llistaTrajectes(), "TRAJECTE", "modificar");
                    Trajecte traMod = (posTraMod < 0) ? null : trajectes.get(posTraMod);
                    if (traMod != null)
                        traMod.modificaTrajecte();
                    break;

                case 12: //Eliminar TRAJECTE
                    int posTraEsb = triaPosicio(llistaTrajectes(), "TRAJECTE", "eliminar");
                    Trajecte traEsb = (posTraEsb < 0) ? null : trajectes.get(posTraEsb);
                    if (traEsb != null) {
                        if (traEsb.teReserves())
                            System.out.println("\tERROR: El TRAJECTE té seients reservats o ocupats: no es pot eliminar");
                        else {
                            traEsb.esborraTrajecte(); //allibera les seues categories
                            trajectes.remove(traEsb);
                        }
                    }
                    break;

                //===================== ASSOCIACIONS I COMPRA =====================
                case 13: //Associar CATEGORIES/VAGONS a TRAJECTE
                    associaCategoriesVagonsATrajecte();
                    break;

                case 14: //Comprar BITLLET
                    compraBitlletTrajecte();
                    break;

                case 0:
                    System.out.println("Eixint de l'aplicació " + nomCompanyia + "...\nPrograma finalitzat!!!");
                    break;
            }
            System.out.println("\n");
        } while (opcio != 0);
    }

    //*****************************************************************
    //S'usa als casos Modificar/Eliminar del menú. "numElements" és el resultat de
    //llistaXXX(), que ja ha mostrat la llista per pantalla.
    //Si la llista està buida mostra l'error; si no, deixa triar un element (0 = anul·lar).
    //Retorna la POSICIÓ de l'element triat a l'ArrayList, o -1 si no se'n tria cap.
    private static int triaPosicio(int numElements, String nom, String accio) {
        nom = ambArticle(nom);
        if (numElements == 0) {
            System.out.println("\tERROR: No hi ha res a " + accio);
            return -1;
        }
        int opcio = Validacio.validaSencer("\tTria " + nom + " a " + accio + " [0 = anul·lar]: ", numElements);
        if (opcio == 0) {
            System.out.println("\tAnul·lant acció");
            return -1;
        }
        return opcio - 1;
    }

    //*****************************************************************
    //Afig l'article al nom de l'objecte per als missatges: "el VAGÓ", "la CATEGORIA"...
    private static String ambArticle(String nom) {
        return (nom.equals("CATEGORIA") ? "la " : "el ") + nom;
    }

    //*****************************************************************
    //ArrayList de VAGONS
    //*****************************************************************

    //Retorna si l'identificador no l'usa cap altre vagó (sense distingir majúscules)
    public static boolean isIdVagoValid(String idVago) {
        for (Vago v : vagons)
            if (v.getIdVago().equalsIgnoreCase(idVago))
                return false;
        return true;
    }

    //Retorna la quantitat de vagons no assignats a cap categoria
    public static int numVagonsLliures() {
        int lliures = 0;
        for (Vago v : vagons)
            if (!v.isAssignatVago())
                lliures++;
        return lliures;
    }

    //Llista tots els vagons i retorna quants n'hi ha
    public static int llistaVagons() {
        if (vagons.isEmpty())
            System.out.println("\tNo hi ha cap VAGÓ registrat");
        for (int i = 0; i < vagons.size(); i++)
            System.out.println(" " + (i + 1) + ": " + vagons.get(i));
        return vagons.size();
    }

    //Llista, numerats des d'1, els vagons lliures i els retorna en un ArrayList.
    //Qui crida el mètode tria sobre aquesta llista: el número mostrat coincideix amb la posició+1.
    public static ArrayList<Vago> llistaVagonsLliures() {
        ArrayList<Vago> lliures = new ArrayList<Vago>();
        for (Vago v : vagons)
            if (!v.isAssignatVago()) {
                lliures.add(v);
                System.out.println(" " + lliures.size() + ": " + v);
            }
        return lliures;
    }

    //*****************************************************************
    //ArrayList de CATEGORIES
    //*****************************************************************

    public static boolean isIdCategoriaValid(String idCategoria) {
        for (Categoria c : categories)
            if (c.getIdCategoria().equalsIgnoreCase(idCategoria))
                return false;
        return true;
    }

    public static int llistaCategories() {
        if (categories.isEmpty())
            System.out.println("\tNo hi ha cap CATEGORIA registrada");
        for (int i = 0; i < categories.size(); i++)
            System.out.println(" " + (i + 1) + ": " + categories.get(i));
        return categories.size();
    }

    //Llista, numerades des d'1, les categories no assignades a cap trajecte i les retorna
    public static ArrayList<Categoria> llistaCategoriesLliures() {
        ArrayList<Categoria> lliures = new ArrayList<Categoria>();
        for (Categoria c : categories)
            if (!c.isAssignadaCategoria()) {
                lliures.add(c);
                System.out.println(" " + lliures.size() + ": " + c);
            }
        return lliures;
    }

    //*****************************************************************
    //ArrayList de TRENS
    //*****************************************************************

    public static boolean isIdTrenValid(String idTren) {
        for (Tren t : trens)
            if (t.getIdTren().equalsIgnoreCase(idTren))
                return false;
        return true;
    }

    public static int llistaTrens() {
        if (trens.isEmpty())
            System.out.println("\tNo hi ha cap TREN registrat");
        for (int i = 0; i < trens.size(); i++)
            System.out.println(" " + (i + 1) + ": " + trens.get(i));
        return trens.size();
    }

    //Retorna si algun trajecte usa el tren
    public static boolean trenEnUs(Tren tren) {
        for (Trajecte t : trajectes)
            if (t.getTren() == tren)
                return true;
        return false;
    }

    //Retorna les places del trajecte més gran que cobreix el tren (0 si no en cobreix cap).
    //La capacitat del tren mai pot baixar d'aquest valor.
    public static int placesMaximesTren(Tren tren) {
        int maxim = 0;
        for (Trajecte t : trajectes)
            if (t.getTren() == tren)
                maxim = Math.max(maxim, t.placesTrajecte());
        return maxim;
    }

    //*****************************************************************
    //ArrayList de TRAJECTES
    //*****************************************************************

    public static boolean isIdTrajecteValid(String idTrajecte) {
        for (Trajecte t : trajectes)
            if (t.getIdTrajecte().equalsIgnoreCase(idTrajecte))
                return false;
        return true;
    }

    public static int llistaTrajectes() {
        if (trajectes.isEmpty())
            System.out.println("\tNo hi ha cap TRAJECTE registrat");
        for (int i = 0; i < trajectes.size(); i++)
            System.out.println(" " + (i + 1) + ": " + trajectes.get(i));
        return trajectes.size();
    }

    //Retorna el trajecte que conté la categoria, o null si està lliure
    public static Trajecte trajecteDeCategoria(Categoria categoria) {
        for (Trajecte t : trajectes)
            if (t.getCategoriesTrajecte().contains(categoria))
                return t;
        return null;
    }

    //Retorna el trajecte on està el vagó (a través de la seua categoria), o null
    public static Trajecte trajecteDeVago(Vago vago) {
        for (Trajecte t : trajectes)
            for (Categoria c : t.getCategoriesTrajecte())
                if (c.getVagonsCategoria().contains(vago))
                    return t;
        return null;
    }

    //*****************************************************************
    //ALTRES MÈTODES
    //*****************************************************************

    //Demana l'identificador d'un objecte nou fins que no existisca.
    //Retorna null si l'usuari cancel·la amb 0.
    public static String demanaIdentificador(String tipus) {
        String id;
        boolean cancelat;
        boolean valid = false;
        do {
            id = Validacio.validaCadena("\tIdentificador " + ambArticle(tipus).replaceFirst("^el ", "del ").replaceFirst("^la ", "de la ") + "? [0 -> cancel·lar]: ");
            cancelat = id.equals("0");
            if (cancelat)
                System.out.println("\tAnul·lant acció");
            else {
                switch (tipus) {
                    case "VAGÓ":
                        valid = isIdVagoValid(id);
                        break;
                    case "CATEGORIA":
                        valid = isIdCategoriaValid(id);
                        break;
                    case "TREN":
                        valid = isIdTrenValid(id);
                        break;
                    default: //TRAJECTE
                        valid = isIdTrajecteValid(id);
                }
                if (!valid)
                    System.out.println("\tERROR: L'identificador " + id + " ja existeix");
            }
        } while (!cancelat && !valid);
        return cancelat ? null : id;
    }

    //*****************************************************************
    //Opció 13: afegeix categories lliures a un trajecte i vagons lliures a les seues
    //categories. La capacitat del tren es comprova dins d'afegirCategoria i afegirVago.
    public static void associaCategoriesVagonsATrajecte() {
        if (trajectes.isEmpty() || categories.isEmpty() || vagons.isEmpty()) {
            System.out.println("\tERROR: Cal que hi haja almenys un TRAJECTE, una CATEGORIA i un VAGÓ");
            return;
        }
        int opcio = Validacio.validaSencer("\tTria el TRAJECTE [0 = anul·lar]: ", llistaTrajectes());
        if (opcio == 0)
            return;
        Trajecte trajecte = trajectes.get(opcio - 1);

        while (trajecte.afegirCategoria("\tVols afegir una nova CATEGORIA al TRAJECTE? [S/N]") != null)
            ;

        if (trajecte.getCategoriesTrajecte().isEmpty()) {
            System.out.println("\tEl TRAJECTE no té CATEGORIES on afegir VAGONS");
            return;
        }
        while (Validacio.validaBoolea("\tVols afegir VAGONS a una CATEGORIA del TRAJECTE? [S/N]")) {
            int numCat = Validacio.validaSencer("\tTria la CATEGORIA [0 = anul·lar]: ",
                    trajecte.llistarCategoriesTrajecte());
            if (numCat == 0)
                break;
            Categoria categoria = trajecte.getCategoriesTrajecte().get(numCat - 1);
            while (categoria.afegirVago("\tVols afegir un VAGÓ a la CATEGORIA " + categoria.getIdCategoria() + "? [S/N]") != null)
                ;
        }
        System.out.println(trajecte);
    }

    //*****************************************************************
    //PAGAMENT D'UN BITLLET
    //Mostra l'import, simula el retard del pagament i demana la confirmació.
    //Retorna si el pagament s'ha fet amb èxit.
    static boolean pagamentBitllet(BigDecimal preu) {
        //TODO TREBALL 1
        //1. Mostra per pantalla l'import a pagar.
        System.out.println("El coste del billete es: " + preu);
        //2. Simula el pagament amb un retard de TEMPS_PAGAMENT ms (Thread.sleep).

        try {
            Thread.sleep(TEMPS_PAGAMENT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //3. Demana la confirmació del pagament (Validacio.validaBoolea) i retorna el resultat.
        if (Validacio.validaBoolea("¿Desea efectuar el pago? [S/N]")) {
            return true;
        }
        return false;
    }

    //*****************************************************************
    //COMPRA INTERACTIVA D'UN BITLLET
    //Ordre lògic: Trajecte -> Categoria -> Vagó -> Seient -> Pagament.
    //En qualsevol pas, si no hi ha opcions o l'usuari tria 0, la compra acaba sense fer res.
    public static void compraBitlletTrajecte() {
        //TODO TREBALL 1
        //1. Mostra els trajectes (llistaTrajectes). Si no n'hi ha, finalitza.

        int posTraElegir = triaPosicio(llistaTrajectes(), "TRAJECTE", "elegir");

        if (posTraElegir < 0) {
            System.out.println("Operación cancelada");
            return;
        }

        //2. L'usuari tria un trajecte i es mostra el seu detall i el del tren.
        //   Si el trajecte no té tren, finalitza.

        Trajecte traElegir = (posTraElegir < 0) ? null : trajectes.get(posTraElegir);
        System.out.println("Trayecto elegido:\nOrigen: " + traElegir.getOrigen() + "\t\tDestino: " + traElegir.getDesti());

        //3. Mostra les categories del trajecte (llistarCategoriesTrajecte) i l'usuari en tria una.
        //   Si no n'hi ha, finalitza.

        if (traElegir == null) {
            System.out.println("No se han encontrado trayectos");
            return;
        }

        int posCatElegir = triaPosicio(traElegir.llistarCategoriesTrajecte(), "CATEGORIA", "elegir");
        Categoria catElegir = (posCatElegir < 0) ? null : traElegir.getCategoriesTrajecte().get(posCatElegir);

        if (catElegir == null) {
            System.out.println("No se han encontrado categorias");
            return;
        }

        System.out.println("Categoría elegida: " + catElegir.getIdCategoria());

        //4. Mostra els vagons de la categoria (llistarVagonsCategoria) i l'usuari en tria un.
        //   Si no n'hi ha o el vagó no té seients lliures (numSeientsLliures), finalitza.

        int posVagElegir = triaPosicio(catElegir.llistarVagonsCategoria(), "VAGONS", "elegir");
        Vago vagElegir = (posVagElegir < 0) ? null : catElegir.getVagonsCategoria().get(posVagElegir);

        if (vagElegir == null) {
            System.out.println("No se han encontrado vagones");
            return;
        }

        int asientosLibres = vagElegir.numSeientsLliures();

        if (asientosLibres == 0) {
            System.out.println("No se han encontrado asientos");
            return;
        }

        System.out.println("Plazas libres: " + asientosLibres);

        System.out.println("Vagón seleccionado: " + vagElegir.toString());

        //5. Mostra el mapa del vagó (mapaVago).

        vagElegir.mapaVago();

        //6. Demana el seient (p.e. "7A") i obtín-lo amb getSeient. Si no existeix o no està
        //   LLIURE (verificaSeient), informa i torna a demanar-lo (0 = cancel·lar la compra).
        //   ATENCIÓ: si el seient no està lliure NO li canvies l'estat, és d'un altre client.
        //   Quan el seient és lliure, reserva'l (reservaSeient).

        boolean salida = false;

        Seient asientoObjetivo;

        do {

            String codigoAsiento = Validacio.validaCadena("Elige tu asiento  (p.e. '7A') [0 = cancel·lar la compra]: \"");

            if (codigoAsiento.equals("0")) {
                salida = true;

                System.out.println("// OPERACIÓN CANCELADA POR EL USUARIO");
                return;
            }

            asientoObjetivo = vagElegir.getSeient(codigoAsiento);

            if (asientoObjetivo == null) {
                System.out.println("No se ha seleccionado un asiento");
                continue;
            }

            if (!asientoObjetivo.verificaSeient()) {
                System.out.println("Este asiento no está libre");
                continue;
            }

            if (asientoObjetivo.getDisponibilitat() == Seient.Estat.LLIURE) {
                System.out.println("Asiento disponible. Reservando...");
                asientoObjetivo.reservaSeient();
                vagElegir.mapaVago();

            }

            //7. Demana el pagament (pagamentBitllet amb el preu de la categoria).

            if (pagamentBitllet(catElegir.getPreu()) == true) {
                System.out.println("Billete pagado.");

                //8. Si s'ha pagat: ocupa el seient (ocupaSeient) i imprimeix el bitllet (imprimirBitllet).

                asientoObjetivo.ocupaSeient();
                vagElegir.imprimirBitllet(asientoObjetivo, catElegir, traElegir);

                salida = true;
            } else {
                //9. Si no s'ha pagat: allibera el seient (alliberaSeient).
                asientoObjetivo.alliberaSeient();
                System.out.println("No se ha completado el pago");

                continue;
            }

            //10. Mostra el mapa resultant del vagó.
            vagElegir.mapaVago();

        } while (!salida);


//        System.out.println("\tPENDENT: compraBitlletTrajecte() encara no està implementat"); ESTA LÍNEA YA NO HACE FALTA
    }

    //*****************************************************************
    //MENÚ PRINCIPAL. Retorna l'opció triada (0-14).
    //Es llig amb Validacio.validaSencer: una lletra ja no fa petar el programa.
    public static int menu() {
        System.out.println("Menú aplicació " + nomCompanyia + ":");
        System.out.println("====================");
        System.out.println(" 1. Crear VAGÓ");
        System.out.println(" 2. Modificar VAGÓ");
        System.out.println(" 3. Eliminar VAGÓ");
        System.out.println(" 4. Crear CATEGORIA");
        System.out.println(" 5. Modificar CATEGORIA");
        System.out.println(" 6. Eliminar CATEGORIA");
        System.out.println(" 7. Crear TREN");
        System.out.println(" 8. Modificar TREN");
        System.out.println(" 9. Eliminar TREN");
        System.out.println("10. Crear TRAJECTE");
        System.out.println("11. Modificar TRAJECTE");
        System.out.println("12. Eliminar TRAJECTE");
        System.out.println("13. Associar CATEGORIES/VAGONS a TRAJECTE");
        System.out.println("14. Comprar BITLLET");
        System.out.println(" 0. Eixir aplicació " + nomCompanyia);
        return Validacio.validaSencer("\nIntrodueix opció de menú: ", 14);
    }

    //*****************************************************************
    //Crea una data concreta. Calendar compta els mesos des de 0, per això "mes - 1".
    private static Calendar data(int dia, int mes, int any, int hora, int minut) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(any, mes - 1, dia, hora, minut, 0);
        return c;
    }

    //*****************************************************************
    //DADES INICIALS per provar l'aplicació sense haver de crear-ho tot.
    //Es respecten totes les regles: places múltiples de 4 i capacitat dels trens.
    public static void carregaDadesInicials() {
        System.out.println("Càrrega INICIAL de DADES...");
        Vago va1, va2, va3, va4, va5;
        vagons.add(va1 = new Vago("vago01", 40));
        vagons.add(va2 = new Vago("vago02", 48));
        vagons.add(va3 = new Vago("vago03", 60));
        vagons.add(va4 = new Vago("vago04", 64));
        vagons.add(va5 = new Vago("vago05", 80));

        Categoria c1, c2, c3, c4;
        categories.add(c1 = new Categoria("c1", Categoria.TipusCategoria.Turista, "Turista", new BigDecimal("16")));
        categories.add(c2 = new Categoria("c2", Categoria.TipusCategoria.TuristaPlus, "Turista Plus", new BigDecimal("30")));
        categories.add(c3 = new Categoria("c3", Categoria.TipusCategoria.Preferent, "Preferent A", new BigDecimal("45")));
        categories.add(c4 = new Categoria("c4", Categoria.TipusCategoria.Preferent, "Preferent B", new BigDecimal("51")));

        Tren t1, t2;
        trens.add(t1 = new Tren("tren01", 200));
        trens.add(t2 = new Tren("tren02", 120));

        Trajecte tj1, tj2;
        trajectes.add(tj1 = new Trajecte("VLC-MAD", "VLC", "MAD", data(1, 6, 2027, 8, 30), 120, t1));
        trajectes.add(tj2 = new Trajecte("VLC-BCN", "VLC", "BCN", data(1, 6, 2027, 10, 15), 180, t2));

        //Associacions: sempre per parelles (afegir a la llista + marcar com a assignat)
        associa(tj1, c1, va2); //VLC-MAD Turista: vago02 (48)
        associa(tj1, c3, va1); //VLC-MAD Preferent: vago01 (40) -> 88 places <= 200
        associa(tj2, c2, va4); //VLC-BCN TuristaPlus: vago04 (64) <= 120

        //c4, va3 i va5 queden lliures per a fer proves
    }

    //Afegeix la categoria al trajecte i el vagó a la categoria, marcant-los com a assignats
    private static void associa(Trajecte trajecte, Categoria categoria, Vago vago) {
        if (!trajecte.getCategoriesTrajecte().contains(categoria)) {
            trajecte.getCategoriesTrajecte().add(categoria);
            categoria.setAssignadaCategoria(true);
        }
        categoria.getVagonsCategoria().add(vago);
        vago.setAssignatVago(true);
    }
}
