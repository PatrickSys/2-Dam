import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;


/************************************************************************
 Made by        PatrickSys
 Date           21/11/2021
 Package
 Description:
 ************************************************************************/



public class ExamenPRC {
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private final File XMLFILE = new File("Alumnes.xml");



    public static void main(String[] args) throws TransformerException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        ExamenPRC e1 = new ExamenPRC();
        e1.handleMenu();
    }


    public ExamenPRC() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docBuilder = this.docFactory.newDocumentBuilder();
    }

    public void handleXMLCreation() throws IOException, TransformerException, ParserConfigurationException {

        // createNewFile will return false if the file already exists
        if(!XMLFILE.createNewFile()) {
            askConfirmation(XMLFILE.getName() + " already exists, do you want to override it?");
        }

    }


    private void askConfirmation(String message) throws TransformerException, ParserConfigurationException {
       int choiceInt = JOptionPane.showConfirmDialog(null,message);
       if(userSaidYes(choiceInt)) {
            createXML();
       }
       else {
       }
    }

    private String showMenu() {
        return  JOptionPane.showInputDialog(
                "***********************************************************\n*" +
                        " Benvinguts al programa de pesca *\n* Menu principal * " +
                        "\n***********************************************************\n"
                        + "1) Donar d'alta un usuari\n2) Donar de baixa un usuari\n3) Pescar en una pesquera\n" +
                        "4) Estadistiques per usuari\n5) Estadistiques globals\ns) Sortir del programa" +
                        "\n***********************************************************\n OPCIO ?");
    }

    /**
     * Checks for null inputs or non-int input
     */
    private void handleMenu() throws TransformerException, ParserConfigurationException, IOException {
        String inputString;
        int inputInt;

        inputString = showMenu();

        if(blankOrNullString(inputString)) {
            handleMenu();
        }

        inputInt = Integer.parseInt(inputString);

        if(wrongInput(inputInt)) {
            showMessage("Wrong choice, please input choose between 0 and 6 ");
            handleMenu();
        }
        handleChoice(inputInt);

    }



    private boolean blankOrNullString(String input) {
        return null == input || input.isBlank();
    }

    private boolean wrongInput(int inputNumber) {
        return 0 > inputNumber || inputNumber > 6;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private boolean userSaidYes(int choiceInt){
        return 0 == choiceInt;
    }

    private void handleChoice(int choice) throws TransformerException, ParserConfigurationException, IOException {

        switch (choice) {
            case 1:
                handleXMLCreation();

            default:
                handleMenu();
        }
    }

    public void createXML() throws ParserConfigurationException, TransformerException {

        DOMImplementation imp = docBuilder.getDOMImplementation();
        Document doc = imp.createDocument(null, "registre_alumnes", null);
        doc.setXmlVersion("1.0");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(doc);
        Result result = new StreamResult(XMLFILE);
        transformer.transform(source, result);
    }

    public void añadirDatosXML() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Document doc = this.docBuilder.parse("src/alumne.xml");

        Element alumnes = doc.createElement("alumnes");
        doc.getFirstChild().appendChild(alumnes);
        String sc;
        Element nom_alumne = doc.createElement("nom_alumne");
        Element curs = doc.createElement("curs");
        Element any_naixement = doc.createElement("any_naixement");
        Element colegi = doc.createElement("colegi");

        alumnes.appendChild(nom_alumne);
        alumnes.appendChild(curs);
        alumnes.appendChild(any_naixement);
        alumnes.appendChild(colegi);

        System.out.println("Codigo del alumno: ");
        sc = new Scanner(System.in).nextLine();

        NodeList nodes = doc.getFirstChild().getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getAttributes().getNamedItem("codi_alumne").getNodeValue().equals(sc)) {
                System.out.println("Codigo no disponible, volviendo al menu");
                // TODO falta la llamada al menu.
                return;
            }
        }

        alumnes.setAttribute("codi_alumne", sc);

        System.out.println("Nombre: ");
        sc = new Scanner(System.in).nextLine();
        nom_alumne.setTextContent(sc);

        System.out.println("Curso: ");
        sc = new Scanner(System.in).nextLine();
        curs.setTextContent(sc);

        System.out.println("Año de nacimiento: ");
        sc = new Scanner(System.in).nextLine();
        any_naixement.setTextContent(sc);

        System.out.println("Colegi: ");
        sc = new Scanner(System.in).nextLine();
        colegi.setTextContent(sc);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(doc);
        Result result = new StreamResult(this.XMLFILE);
        transformer.transform(source, result);

    }

    public void mostrarXML() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        StringWriter sw = new StringWriter();

        Document doc = docBuilder.parse(this.XMLFILE);

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes2 = (NodeList) xpath.evaluate("//alumnes", doc, XPathConstants.NODESET);

        for (int i = 0; i < nodes2.getLength(); i++) {
            Element alumne = (Element) nodes2.item(i);
            String c1 = (String) xpath.evaluate("nom_alumne", alumne, XPathConstants.STRING);
            String c2 = (String) xpath.evaluate("curs", alumne, XPathConstants.STRING);
            String c3 = (String) xpath.evaluate("any_naixement", alumne, XPathConstants.STRING);
            String c4 = (String) xpath.evaluate("colegi", alumne, XPathConstants.STRING);
            System.out.println("Nombre= " + c1);
            System.out.println("Curso= " + c2);
            System.out.println("Año de nacimiento= " + c3);
            System.out.println("Colegio= " + c4);
            System.out.println("----------");
        }
    }

//    public String consultarNombres(Document doc, XPath xpath, int id) {
//        NodeList nodes = doc.getFirstChild().getChildNodes();
//
//        for (int i = 0; i < nodes.getLength(); i++) {
//            if (nodes.item(i).getAttributes().getNamedItem("codi_alumne").getNodeValue().equals(sc)) {
//                System.out.println("Codigo no disponible, volviendo al menu");
//                // TODO falta la llamada al menu.
//                return;
//            }
//        }
//    }
}
