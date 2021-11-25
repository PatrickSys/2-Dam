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
import java.util.ArrayList;
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
    Transformer transformer = TransformerFactory.newInstance().newTransformer();


    abstract static class Alumne {
        String name = "";
        String curs = "";
        String dateOfBirth = "";
        String school = "";

        public static String[] toXMLString() {
            return new String[] {"nom_alumne", "curs", "any_naixement", "colegi" };
        }
    }


    public static void main(String[] args) throws TransformerException, ParserConfigurationException, IOException {
        ExamenPRC e1 = new ExamenPRC();
        e1.startMenu();
    }


    public ExamenPRC() throws ParserConfigurationException, TransformerConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docBuilder = this.docFactory.newDocumentBuilder();
    }

    public void handleXMLCreation() throws IOException, TransformerException, ParserConfigurationException, SAXException {

        // createNewFile will return false if the file already exists
        if(!XMLFILE.createNewFile()) {
            askConfirmation( "el fitxer " + XMLFILE.getName() + " ja existeix, vols sobrescriurer-lo?");
        }
    }


    /**
     * Asks the user if he wants to override the file
     * @param message to prompt the user
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private void askConfirmation(String message) throws TransformerException, ParserConfigurationException, IOException, SAXException {
       int choiceInt = JOptionPane.showConfirmDialog(null,message);
       if(userSaidYes(choiceInt)) {
            createXML();
       }
    }

    /**
     * Shows the optionpane for the menu
     * @return
     */
    private String showMenu() {
        return  JOptionPane.showInputDialog(null,
                """
                        ******************************
                        Elige opción: \s
                        1.- Crear el fichero XML
                        2.- Introducir datos en el fichero XML
                        3.- Mostrar el contenido del fichero XML
                        4.- Modificar datos        
                        5.- Consultas           
                        6.- Eliminar un registro
                        0.- Salir
                        ******************************
                         """, "AAD_U1EXAMEN02", 3);
    }

    /**
     * startMenu Is the main method fr handling the menu
     * @throws IOException and catches it in case user inputs a wrong choice
     */
    public void startMenu() throws ParserConfigurationException, TransformerException, IOException {
        try {
            handleMenu();
        } catch (NumberFormatException | SAXException e) {
            returnToMenuAfterWrongInput();
        }
    }
    /**
     * Checks for null inputs or non-int input
     */
    private void handleMenu() throws TransformerException, ParserConfigurationException, IOException, SAXException {
        String inputString;
        int inputInt;

        inputString = showMenu();

        if(blankOrNullString(inputString)) {
            returnToMenuAfterWrongInput();
        }

        inputInt = Integer.parseInt(inputString);

        if(wrongInput(inputInt)) {
            returnToMenuAfterWrongInput();
        }
        handleChoice(inputInt);
    }


    /**
     * logic of the menu to capture the choice
     * @param choice
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private void handleChoice(int choice) throws TransformerException, ParserConfigurationException, IOException, SAXException {

        switch (choice) {
            case 1:
                handleXMLCreation();
                handleMenu();
                break;

            case 2:
                añadirDatosXML();
                handleMenu();
                break;

            case 0:
                System.exit(0);
                break;

            default:
                handleMenu();
                break;
        }

    }


    /**
     *     returns to handlemenu if the user didn't introduce a valid choice
      */
    private void returnToMenuAfterWrongInput() throws IOException, TransformerException, ParserConfigurationException {
        returnToMenuWithMessage("Opción inválida, por favor,\n introduce un número entero entre 0 y 6 ");
    }


    private void returnToMenuWithMessage(String message) throws IOException, TransformerException, ParserConfigurationException {
        showMessage(message);
        startMenu();
    }
    /**
     * creates the root xml
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void createXML() throws ParserConfigurationException, TransformerException {

        Document doc = docBuilder.newDocument();
        Element rootElelemnt = doc.createElement("registre_alumnes");
        doc.appendChild(rootElelemnt);
        Source source = new DOMSource(doc);
        Result result = new StreamResult(XMLFILE);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);

    }
    /**
     * Checks if the input is either blank or null
     * @param input
     * @return
     */
    private boolean blankOrNullString(String input) {
        return null == input || input.isBlank();
    }

    /**
     * checks the user introduced a correct choice
     * @param inputNumber
     * @return
     */
    private boolean wrongInput(int inputNumber) {
        return 0 > inputNumber || inputNumber > 6;
    }

    /**
     * Creates a JOP for input strings
     * @param message
     */
    private String inputString(String message) {
        return JOptionPane.showInputDialog(message);
    }

    private int inputInt(String message) {
        String inputInt;
        int parsedInt = 0;

        inputInt = inputString(message);
        try {
            parsedInt = Integer.parseInt(inputInt);
        }catch (Exception e) {
            showMessage(" Per favor introdueix un nombre sencer ");
            inputInt(message);
        }
        return parsedInt;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }



    private boolean userSaidYes(int choiceInt){
        return 0 == choiceInt;
    }


    private void createAlumnesElements(Document doc) throws ParserConfigurationException, TransformerException, IOException {

        String codiAlumne;

        // Crea el elemento alumno base
        Element alumnes = doc.createElement("alumnes");
        doc.getFirstChild().appendChild(alumnes);

        for(String field: Alumne.toXMLString()) {
            Element elField = doc.createElement(field);
            String fieldValue = inputString(field);
            elField.setTextContent(fieldValue);
            alumnes.appendChild(elField);
        }
        // Añadimos lógica del código ya que necesita validarse como único
        codiAlumne = addCodiAlumne(doc);
        alumnes.setAttribute("codi_alumne", codiAlumne);

    }

    private String addCodiAlumne(Document doc){
        String codiAlumne = inputString("codi alumne");
        NodeList nodes = doc.getFirstChild().getChildNodes();

        for (int i = 0; i < nodes.getLength()-1; i++) {

            if (nodes.item(i).getAttributes().getNamedItem("codi_alumne").getNodeValue().equals(codiAlumne)) {
                showMessage("Codi ja en ús");
                return addCodiAlumne(doc);
            }
        }
        return codiAlumne;
    }

    private void createSeveralAlumnes(int times, Document doc) throws IOException, TransformerException, ParserConfigurationException {
        for (int i=0; i<times; i++) {
            createAlumnesElements( doc);
        }
    }

    public void añadirDatosXML() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Document doc = this.docBuilder.parse(XMLFILE);
        int numberOfAlumnes = inputInt(" ¿Quants alumnes vols crear? ");
        createSeveralAlumnes(numberOfAlumnes,doc );

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        Result result = new StreamResult(this.XMLFILE);
        transformer.transform(source, result);
    }

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
