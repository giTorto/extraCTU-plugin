package org.extraction.services;

import org.junit.Test;

import java.util.ArrayList;


/**
 * There are many test for the Operazione class
 * @author Giuliano Tortoreto
 */
public class OperazioneTest {
    Operazione opmails = new Operazione("E-mails");
    Operazione opUrls;
    Oggetto risultato;

    /**
     * The test attempt to find an e-mail at the beginning of the given text and another at the end
     * @throws Exception if it recognize an email that doesn't belong to the accepted email set and if it recognize more than 2 emails
     */
    @Test
    public void testExtractSimpleMail() throws Exception {

        String testo = "email.sdf@sdfs.it  email.mia@miamail.it";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();
        assert(mails.size()==2);
        for (String email : mails) {
            //System.out.print(email);
            assert(email.equals("email.sdf@sdfs.it") ||
            email.equals("email.mia@miamail.it"));
           //System.out.print(" ACCEPTED\n");
        }
    }

    /**
     * This test checks if it recognize the e-mails with dashes and dots
     * @throws Exception if it recognize an email that doesn't belong to the given set
     */
    @Test
    public void testExtractMoreMailFromText() throws Exception{
        String testo = "\"isdfsdfuytrew<email.sdf@sdfs.it>.sdfsdf.s.d.good-sdf-sf.sss@it.it \" ";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();

        for (String email : mails){
          //  System.out.print(email);
            assert(email.equals("email.sdf@sdfs.it") || email.equals("sdfsdf.s.d.good-sdf-sf.sss@it.it")) ;
          //  System.out.print(" ACCEPTED\n");
        }
    }

    /**
     * This test attempts to find emails with Upper and Lower case.
     * In the local part Upper case is accepted, after @ is casted to lower
     * @throws Exception if it recognize emails that doesn't belong to the given set
     */
    @Test
    public void testExtractMailMaiusc() throws Exception{
        String testo = "\"isdFSdfuytrew<eSmaSSil.sdf@sdfSDs.it>..gooHAHAHd-sdf-sf.sss@it.it \" ";
        //uppercase letter are accepted also in
        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();

        for (String email : mails){
      //      System.out.print(email); //questa va considerata un email buona??
            assert( email.equals("gooHAHAHd-sdf-sf.sss@it.it") ||
                    email.equals("eSmaSSil.sdf@sdfSDs.it")
            ) ;
        //    System.out.println(" ACCEPTED\n");
        }
    }

    /**
     * This test checks if emails with number and plus are recognized
     * @throws Exception  if it recognize emails that doesn't belong to the given set
     */
    @Test
    public void testExtractMailNumber() throws Exception{
        String testo = "\"isdFSdfuytrew<1eSma555SSil.sdf343+1@sdfs.it>.sdfSdf.s.d.gooHA1HAHd-sdf-sf.sss123@it.it \" ";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();

        for (String email : mails){
            //System.out.print(email);
            assert(email.equals("1eSma555SSil.sdf343+1@sdfs.it") ||
                    email.equals("sdfSdf.s.d.gooHA1HAHd-sdf-sf.sss123@it.it")) ;
           // System.out.println(" ACCEPTED\n");
        }
    }


    /**
     * This test checks if the emails separeted by '<','>' are founded and if there
     * are problem with e-mails starting with underscore
     * @throws Exception if underscore in an e-mail are not accepted
     */
    @Test
    public void testExtractMailUnder() throws Exception{
        String testo = "\"isdfsdfuytrew<_e_il.sdf@sdfs.it>._sdfsdf.s.d.good-sdf-sf.sss@it.it \" ";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();

        for (String email : mails){
           // System.out.print(email);
            assert(email.equals("_e_il.sdf@sdfs.it") ||
                    email.equals("_sdfsdf.s.d.good-sdf-sf.sss@it.it")) ;
           // System.out.println(" ACCEPTED\n");
        }
    }

    /**
     * This test checks if there are problem with ' " '
     * @throws Exception if it accepts e-mail beginning with ' " '
     */
    @Test
    public void testExtractOneMail() throws Exception{
        String testo = "\".f-sf.sss@it.it \" ";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();

        for (String email : mails){
          //  System.out.print(email);
            assert(email.equals("f-sf.sss@it.it")) ;
          //  System.out.print(" ACCEPTED\n");

        }
    }

    /**
     * This test attempts to extract some e-mails belonging to the non-spaced text, the e-mails are separate by using different kind of parenteses
     * @throws Exception if it doesn't extract the expected e-mails
     */
    @Test
    public void testExtractMailParenteses() throws Exception{
        String testo = "\".(.good-sdf-sf.sss@it.it)sdfsfafsa{email@good.it}[let'smail.sdf@evviva.it] \" ";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();

        for (String email : mails){
            //System.out.print(email);
            assert(email.equals("good-sdf-sf.sss@it.it") ||
                    email.equals("email@good.it")||
                    email.equals("smail.sdf@evviva.it")) ;
            //System.out.print(" ACCEPTED \n");

        }
    }


    /**
     * This test attempts to extract an e-mail with % in the second part(in italian %C3%AC = ì)
     * @throws Exception if it doesn't extracts the e-mail correctly
     */
    @Test
    public void testExtractMailAccentLetters() throws Exception{
        //easy to solve, but I have to accept it?
        String testo = "\" m.greco@comune.canicatt%C3%AC.ag.it \" ";
        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();
        assert(mails.size()==1);
        for (String email : mails){
             System.out.print(email);
            assert(email.equals("m.greco@comune.canicatt%C3%AC.ag.it") ) ;
            System.out.print(" ACCEPTED \n");

        }
    }

    /**
     * This test attempts to extract 2 e-mails with 'chiocciola' instead of @
     * @throws Exception if it doesn't extract e-mails correctly
     */
    @Test
    public void testExtractMailReplaceChiocciola() throws Exception{
        //easy to solve, but I have to accept it?
        String testo = "\" biblsem'chiocciola'virgilio.it al0077'chiocciola'biblioteche.ruparpiemonte.it archivio'chiocciola'diocesidicasale.191.it \" ";
        risultato = opmails.extract(testo);        ArrayList<String> mails = risultato.getOggettiTrovati();
        assert(mails.size()==3);
        for (String email : mails){
            //System.out.print(email);
            assert(email.equals("biblsem@virgilio.it") ||
                    email.equals("al0077@biblioteche.ruparpiemonte.it") ||
                    email.equals("archivio@diocesidicasale.191.it")
            ) ;
            //System.out.print(" ACCEPTED \n");

        }
    }

    /**
     * The test attempts to extract some e-mails from the text
     * @throws Exception if extracted e-mails doesn't belong to the accepted set or extracted e-mails aren't equal to the expected ones
     */
    @Test
    public void testExtractMailMessyText() throws Exception{
        String testo = "\" isdkflsdkl ldlk l  lksdfkja a " +
                " c'era una volta un re(re@delmondo.world) seduto" +
                "sul suo divano (ordinabile scrivendo a divanireali@lusso.mai.abbastanza.com)" +
                "interferenze asfdffsd .(.good-sdf-sf+ciao.sss@it.it)sdfsfafsa email23@good.it [let'smail.sdf@evviva.it] " +
                "sdfsdfsd,here.com+1es.the.crazyOnes.think@different.ap,sfsdfasfsdfasd\" ";

        risultato = opmails.extract(testo);
        ArrayList<String> mails = risultato.getOggettiTrovati();
        assert(mails.size()>2);
        for (String email : mails){
           // System.out.print(email);
            assert(email.equals("re@delmondo.worl") ||
                    email.equals("divanireali@lusso.mai.abbastanza.com")||
                    email.equals("good-sdf-sf+ciao.sss@it.it") ||
                    email.equals("email23@good.it") ||
                    email.equals("smail.sdf@evviva.it")||
                    email.equals("here.com+1es.the.crazyOnes.think@different.ap")
            ) ;
          //  System.out.print(" ACCEPTED \n");

        }
    }


    /**
     * Tenta di estrarre diversi numeri separati da testo
     * The test attempts to extract some telephone numbers from the given text, telephone numbers are separated by words
     * @throws Exception if extracted telephone numbers don't match the expected ones
     */
    @Test
    public void testExtractSomeNumbers() throws Exception {

        String testo = "\"+34 99899 this +343495669229 and 9394959691 9 or 0039 1234567891 when +3912345678901\" ";
        Operazione opNumbers = new Operazione("numbers");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();

        for (String value : numbers) {
            //System.out.print(value);
            assert(value.contains("+343495669229")||
                    value.contains("9394959691 9")||
                    value.contains("0039 1234567891")||
                    value.contains("+3912345678901")
            );
          // System.out.print(" ACCEPTED\n");
        }
    }

    /**
     * The test attempts to extract telephone numbers attached to words
     * @throws Exception if the test extracts the second telephone number
     */
    @Test
    public void testExtractNumbersAttachedToText() throws Exception {

        String testo = "\"+34 99899 here is+343495669229\\hahahaha9394959691\" ";
        Operazione opNumbers = new Operazione("numbers");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
//        assert(numbers.size()>0);
        for (String value : numbers) {
            System.out.print(value);
            assert(value.equals("+343495669229"));
            System.out.print(" ACCEPTED\n");
        }
    }


    /**
     * The test attempts to extract some telephone numbers separated by letters and commas
     * @throws Exception if the test extracts unexpected telephone numbers
     */
    @Test
    public void testExtractDifferentNearNumber() throws Exception {
        String testo = "\"0464/454154-108 (+34 349.566.9229)\\939-4959-691 934567890()2324.234 5 2,0039 (123) 4567891 dsf +39   12345678901\" ";

        Operazione opNumbers = new Operazione("numbers");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
        //assert(numbers.size()>0);

        for (String value : numbers) {
           System.out.print(value);
            assert(value.equals("0464/454154-108")||
                    value.equals("+34 349.566.9229")||
                    value.equals("939-4959-691")||
                    value.equals("0039 (123) 4567891")||
                    value.equals("12345678901")||
                    value.equals(" 934567890") ||
                    value.equals("2324.234 5 2")
            );
         System.out.print(" ACCEPTED\n");
        }
    }

    /**
     * The test attempts to extract a telephone number within a URL
     * @throws Exception if the test extracts a telephone number
     */
    @Test
    public void testExtractNumberToAvoid() throws Exception {
        String testo = "\"http://www.crushsite.it/it/cinema/2013/flight_6009_6058-6134-6160.html,\" ";
        Operazione opNumbers = new Operazione("numbers");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();

        assert(numbers.size()==0);
        for (String value : numbers) {
            //System.out.print(value);
            assert( !value.equals("6058-6134-6160"));
        }
    }

    /**
     * Tenta di riconoscere 2 numeri separati da più di 2 caratteri
     * The test attempts to extract 2 italian telephone numbers separated by 3 symbols
     * @throws Exception if the test doesn't extract both telephone numbers
     */
    @Test
    public void testExtractNumberToSplit() throws Exception {

        String testo = "\",tel. 0461.235331 - 349/8673463\" ";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();

        assert(numbers.size()>0);
        for (String value : numbers) {
            //System.out.print(value);
            assert( value.equals("+39 0461 235331") ||
                    value.equals("+39 349 867 3463")
            );
           //System.out.print("ACCEPTED\n");
        }
    }

    /**
     * The test attempts to extract an old telephone italian number with extension, 12 digit + extension(1)
     * @throws Exception if it doesn't extract the telephone number
     */
    @Test
    public void testExtractNumberOf13() throws Exception {

        String testo = "   \"c9f61b08f06764438220745483a253789117af0d  obbligatoria 0464/454154-108 ";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
        assert(numbers.size()==1);
        for (String value : numbers) {
            //System.out.print(value);
            assert( value.contains("+39 0464454154108")
            //System.out.print("ACCEPTED\n");
            );
        }
    }


    /**
     * The test attempts to extract an italian telephone number from the given text
     * @throws Exception if the test extract the telephone number (telephone numbers starting with 200 aren't italian telephone numbers)
     */
    @Test
    public void testExtractNumberWrongDate() throws Exception {

        String testo = "   \"obbligatoria (2006-2011).12";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
        assert(numbers.size()==0);

    }

    /**
     * The test attempts to extract italian telephone numbers that aren't well-separated(3 non-digit)
     * @throws Exception if the test extracts at least one telephone number
     */
    @Test
    public void testExtractNumberSplitted() throws Exception{
        String testo = "this is 0461/568626-339.5669229, 051.3452396-321/9332229;";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
       assert(numbers.size()==0);


    }

    /**
     * An italian telephone number could start with +41 91, (Campione d'Italia town is in a swiss district, but it's italian)
     * So, the test attempts to extract an italian telephone number
     * @throws Exception if the test doesn't extract the telephone number
     */
    @Test
    public void testExtractEccezioneNumeroItaliano() throws Exception{
        String testo = "this is a real number +41 91 641 91 41 ";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
        assert(numbers.size()==1);
        for (String value : numbers) {
            // System.out.print(value);
            assert( value.contains("+41 91 641 91 41"));
            //System.out.print("ACCEPTED\n");
        }

    }

    /**
     * The test attempts to extract an italian telephone number(the telephone numbers starting with 199 in italy are with fee)
     * @throws Exception if it doesn't extract the telephone number
     */
    @Test
    public void testExtractNumero199Apagamento() throws Exception{
        String testo = "(1996-2012).8";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
        assert(numbers.size()>0);
        for (String value : numbers) {
            //System.out.print(value);
            assert( value.contains("+39 199 620 128")
              //System.out.print("ACCEPTED\n");
            );
        }
    }

    /**
     * The test attempts to extract some 5-figure telephone number
     * @throws Exception if it extracts one or more telephone numbers
     */
    @Test
    public void testExtractNumeriServizi() throws Exception{
        String testo = "45592 48118 ";
        Operazione opNumbers = new Operazione("numbers","+39","IT");
        risultato = opNumbers.extract(testo);
        ArrayList<String> numbers = risultato.getOggettiTrovati();
        assert(numbers.size()==0);

        for (String value : numbers) {
            // System.out.print(value);
            assert( value.equals("45592") ||
            value.equals("48118"));
            System.out.print("ACCEPTED\n");
        }

    }


    /**
     * This test attempts to extract 7 URL separated in many different ways
     * @throws Exception if finded URL are less than 7, or finded URL are not extracted in the expected way
     */
    @Test
    public void testExtractURL() throws Exception{
        String testo = "www.google.com/en http:google.com  http://google.com https://google.com,www.google.com\n" +
                "http://www.google.com/it, https://www.google.com/de";

        opUrls = new Operazione("URLs");
        risultato = opUrls.extract(testo);
        assert(risultato.getOggettiTrovati().size()==7);

        for(String URL: risultato.getOggettiTrovati()){
           // System.out.print(URL);
            assert( URL.equals("www.google.com/en")||
                    URL.equals("http:google.com")||
                    URL.equals("http://google.com")||
                    URL.equals("https://google.com")||
                    URL.equals("www.google.com")||
                    URL.equals("http://www.google.com/it")||
                    URL.equals("https://www.google.com/de")
            );
            //System.out.print(" ACCEPTED\n");
        }
    }

    /**
     * This test attempts to extract 5 URL containing many symbols
     * @throws Exception if finded URL are less than 5, or finded URL are not extracted in the expected way
     */
    @Test
    public void testExtractURLwithSymbols() throws Exception{
        String testo = "http://www.youtube.com/watch?v=LV1Ymtz1a9w&list=RD8HrmAgYE-6k, http://blogs.independent.co.uk/wp-content/uploads/2012/12/google-zip.jpg" +
                "\n http://cdn2.business2community.com/wp-content/uploads/2013/04/google-.jpg " +
                "http://www.joomla.it/images/stories/articoli/2012/google_penguin_update.jpg," +
                "http://upload.wikimedia.org/wikipedia/commons/2/20/Ursus_maritimus_us_fish.jpg ";

        opUrls = new Operazione("URLs");
        risultato = opUrls.extract(testo);
        assert(risultato.getOggettiTrovati().size()==5);

        for(String URL: risultato.getOggettiTrovati()){
            //System.out.print(URL);
            assert( URL.equals("http://www.youtube.com/watch?v=LV1Ymtz1a9w&list=RD8HrmAgYE-6k")||
                    URL.equals("http://blogs.independent.co.uk/wp-content/uploads/2012/12/google-zip.jpg")||
                    URL.equals("http://cdn2.business2community.com/wp-content/uploads/2013/04/google-.jpg")||
                    URL.equals("http://www.joomla.it/images/stories/articoli/2012/google_penguin_update.jpg")||
                    URL.equals("http://upload.wikimedia.org/wikipedia/commons/2/20/Ursus_maritimus_us_fish.jpg")
            );
            //System.out.print(" ACCEPTED\n");
        }
    }

    /**
     * TODO: find some test and understand what do Davide whant
     * @throws Exception
     */
    @Test
    public void testIdentificationNumber() throws Exception{
        String testo = "bla bla bla bla 567890234";
        opUrls = new Operazione("Identif");
        risultato = opUrls.extract(testo);

        for(String identifNumber: risultato.getOggettiTrovati()){

        }

    }
}
