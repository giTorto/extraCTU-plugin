package org.extraction.services;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This class allows to extract different kind of object from a string and returns an arraylist with
 * the finded objects. First of all the class must be initialized with the object you want to extract.
 * You can extract telephone numbers, e-mails and URLs.
 * If the extract function returns null in case of wrong initialization or in case of no objects are finded
 * @author Giuliano Tortoreto
 */
public class Operazione {
    private  String country ="";
    private String operazione;
    private String DEFAULT = "--"; //every country
    private String prefisso = DEFAULT;// the dial-code for the country we are looking for country
    private Integer minimaLunghezzaNum = 9;

    /**
     * Never used
     * This is a constructor where you can choose the smallest telephone number you will find
     * @param operazione
     * @param minlunghezzaNumero
     */
    public Operazione(String operazione, int minlunghezzaNumero) {
        this.operazione = operazione;
        this.minimaLunghezzaNum = minlunghezzaNumero;
    }

    /**
     * The only Operazione constructor the plugin is using. Every operation will choose the user
     * the plugin will call this constructor with prefisso and country empty in the case of
     * the extraction to perform is about e-mails and urls
     * @param operazione this is the parameter that determines which kind of object the function will extract
     * @param prefisso contains the value of the country dial code -> this is useless in case of the extraction to perform is e-mails and urls extraction, but basic for telephone numbers extraction
     * @param country contains the country code(2 letters) -> this is useless in case of the extraction to perform is e-mails and urls extraction, but basic for telephone numbers extraction
     */
    public Operazione(String operazione, String prefisso, String country) {
        this.operazione = operazione;
        this.prefisso = prefisso;
        this.country = country;
    }

    public Operazione(String operazione) {
        this.operazione = operazione;
    }

    /**
     * This function choose which function to call depending on the chosen operation
     * @param text the given text to check
     * @return  an object containing the list of the found object
     */
    public Oggetto extract(String text) {
        Oggetto risultato = null;

        if (this.operazione.equals("E-mails")) {
            risultato = getEmails(text);
        } else if (this.operazione.equals("numbers")) {
            risultato = getNumbers(text);
        } else if (this.operazione.equals("URLs")) {
            risultato = getSites(text);
        }
        return risultato;
    }

    /**
     * This function finds all url in a given text
     * @param text the given text to check
     * @return an object Oggetto containing all finded URL
     */
    private Oggetto getSites(String text) {
        Oggetto risultati = new Oggetto();

        Pattern ptr = Pattern.compile("(((^|(\\G)|[\\n\\s,])[w]{3}[\\.])|(http:(//)?)|(https:(//)?))([\\w]+[\\.][\\w]+)+(/(\\w|[\\W&&[^,\\s]])*)*([\\n\\s,]|$)");
        Matcher isSite = ptr.matcher(text);
        String site;
        while(isSite.find()){
            site = isSite.group();
            site = cleanSite(site);
            risultati.addOggettoTrovato(site);
        }

        return risultati;
    }

    /**
     * This function cleans out the finded url
     * @param site the site to clean from comma and white spaces
     * @return the cleaned URL
     */
    private String cleanSite(String site) {
        Pattern ptr = Pattern.compile("[,\\s]");
        Matcher cleaner = ptr.matcher(site);
        return cleaner.replaceAll("");
    }


    /**
     * This function looks for telephone numbers in a given text.
     * Non-italian numbers are found using a google library
     * @param text the given text to check
     * @return  an object containing the list of the found object
     */
    private Oggetto getNumbers(String text) {
        Oggetto risultati = new Oggetto();

        PhoneNumberUtil gUtil = PhoneNumberUtil.getInstance();


        if (country.equals("IT")){
            //with this regex we find telephone numbers of potentially variable length(but for now it's only from 9 to 13)
            //telephone numbers of 13 digits are the old telephone numbers (12 digits) with extension (1 digit)
            //this regex doesn't accept numbers with a alfanumeric character before and after
            Pattern ptr = Pattern.compile("(([\\D][\\W])|^|\\A|\\G)((\\+|00)[1-9]{1,4})?([\\s()\\-./]{0,2}[0-9]){" + (this.minimaLunghezzaNum).toString() + ",13}($|[\\W&&[^\\s()\\-./]]|([\\s()\\-./]([\\D]|$)))"); //versione con minima lunghezza num settabile
            Matcher isnumber = ptr.matcher(text);
            while (isnumber.find()) {
                String numero = isnumber.group();
                    numero = cleanNumber(numero);
                    if (isRightCountry(numero)){
                        //risultati.addOggettoTrovato(numero);
                        try {
                                //it's really slow because of the parsing of the number
                                //but is the easiest way to format a telephon number
                                Phonenumber.PhoneNumber numb = gUtil.parse(numero,"IT");
                                risultati.addOggettoTrovato(gUtil.format(numb, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }else{
            Iterable<PhoneNumberMatch> numeri = gUtil.findNumbers(text, country);
            for(PhoneNumberMatch numb: numeri){
                risultati.addOggettoTrovato(gUtil.format(numb.number(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
                System.out.println(numb.rawString());
            }
        }


        return risultati;
    }

    /**
     * First of all this function checks if the dial_code is correct depending on the country we are looking for,
     * after that it calls checkPrefix for area code checking and things like that
     * nb. for it works online for italian numbers
     * @param number is the number to verify
     */
    private boolean isRightCountry(String number) {
        Boolean result = false;

        //tolgo tutti gli spazi o simboli in modo da facilitare la verifica del numero
        Pattern ptr = Pattern.compile("[\\D&&[^+]]");
        String numero = ptr.matcher(number).replaceAll("");


        if (prefisso.equals("--"))
        if (prefisso.equals("--"))
            return true;

        //controllo prefisso internazionale
        if (numero.startsWith("+")) {
            if (!numero.startsWith(prefisso)) {
                return numero.startsWith("+4191");//comune di campione d'italia
            } else {
                numero = numero.substring(prefisso.length());
            }
        }

        if (numero.startsWith("00")) {
            if (!(numero.substring(1)).startsWith(prefisso.substring(1))) {
                return numero.startsWith("004191");  //eccezione per campione d'italia
            } else {
                numero = numero.substring(prefisso.length() + 1);//00 instead of +
            }
        }

        //ora il numero è privo di prefisso devo vedere se è un numero corretto
        result = checkPrefixForCountries(numero);


        return result;
    }

    /**
     * This function check if the area code of the number exist and
     * if the mobile telephone number exists
     * @param numero the number to verify
     * @return true if the area code exists in that country
     */
    private Boolean checkPrefixForCountries(String numero) {

        if (prefisso.equals("+39")) { //italia
            if (numero.startsWith("800")) {//numero verde
                return true;
            } else if (numero.startsWith("199") ||numero.startsWith("144")||numero.startsWith("166")||numero.startsWith("709")||numero.startsWith("892")||numero.startsWith("899")) {
                return true; //numero a pagamento Italia
            } else if (numero.startsWith("0")) { //numero fisso
                return true;
            } else if(numero.startsWith("3")) { //numero di cellulare
               String[] mobileNumbers = {"3","4","13","73","77","70","2","8","9","6"};//prefissi cellulare esistenti al 27/02
                Boolean isCorrect = false;
                for(int i = 0; i<mobileNumbers.length;i++){
                   isCorrect= isCorrect || numero.substring(1).startsWith(mobileNumbers[i]);
                }

                if (isCorrect)return true;
                else return false;

            }else{
                return false;
            }
        }else{ //actually no checking on code of other nations
            return true;
        }

    }

    /**
     * This function cleans out the number. It drops non-digit prefix and non-digit suffix.
     * @param numero the number to clean out
     * @return the clean number
     */
    private String cleanNumber(String numero) {
        Pattern ptr = Pattern.compile("[\\D&&[^+]]");
        Matcher cleanfirst = ptr.matcher(numero.substring(0, 3));


        while (cleanfirst.find()) {
            numero = numero.substring(1);

        }

        ptr = Pattern.compile("[^0-9]");
        Matcher isLastNot = ptr.matcher(numero.substring(numero.length() - 2));



        while (isLastNot.find()) {
            char[] last = numero.toCharArray();

            if(!Character.isDigit(last[last.length-1]))
                numero = numero.substring(0, numero.length() - 1);
        }

        return numero;
    }

    /**
     * This function looks for e-mail in a given text
     * @param text the given text to check
     * @return an object containing the list of the found object
     */
    private Oggetto getEmails(String text) {
        // TODO Auto-generated method stub
        Oggetto risultati = new Oggetto();
        if (text == null) {
            return risultati;
        }
        int n = 0;
        int start = 0;
        try {
            Pattern ptr = Pattern.compile("[_a-zA-Z0-9-+_]+(\\.[_a-zA-Z0-9-+_]+)*(@|'chiocciola')[a-zA-Z0-9-%]+(\\.[a-zA-Z0-9-%]+)*(\\.[a-zA-Z]{2,4})");

            String[] parole;
            //prima divido le parole per " "
            parole = text.split(" ");

            Matcher isemail;
            for (String word : parole) {
                //divido le parole per \n
                ArrayList<String> otherWords = new ArrayList(Arrays.asList(word.split("\n")));
                start = 0;
                for (String parola : otherWords) {
                    //controllo se la parola è una mail
                    isemail = ptr.matcher(parola);
                    while (isemail.find()) {
                        String se = isemail.group();
                        se = se.replaceAll("('chiocciola')","@");
                        risultati.addOggettoTrovato(se);
                    }


                }

            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return risultati;
    }


}
