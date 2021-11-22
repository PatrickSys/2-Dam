import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.JOptionPane;
import javax.xml.parsers.*;
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
    Transformer transformer = TransformerFactory.newInstance().newTransformer();




    public static void main(String[] args) throws TransformerException, ParserConfigurationException, IOException {
        ExamenPRC e1 = new ExamenPRC();
        e1.startMenu();
    }


    public ExamenPRC() throws ParserConfigurationException, TransformerConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docBuilder = this.docFactory.newDocumentBuilder();
    }



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
    private void startMenu() throws ParserConfigurationException, TransformerException, IOException {
        try {
            handleMenu();
        } catch (NumberFormatException e) {
            returnToMenuAfterWrongInput();
        }
    }
    /**
     * Checks for null inputs or non-int input
     */
    private void handleMenu() throws TransformerException, ParserConfigurationException, IOException {
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

    private void handleChoice(int choice) throws TransformerException, ParserConfigurationException, IOException {

        switch (choice) {
            case 1:
                handleXMLCreation();

            default:
                handleMenu();
        }
    }


    private void returnToMenuAfterWrongInput() throws IOException, TransformerException, ParserConfigurationException {
        showMessage("Opción inválida, por favor,\n introduce un número entero entre 0 y 6 ");
        startMenu();
    }

    public void handleXMLCreation() throws IOException, TransformerException, ParserConfigurationException {

        // createNewFile will return false if the file already exists
        if(!XMLFILE.createNewFile()) {
            askOverriding(" El fichero " + XMLFILE.getName() + " ya existe, deseas sobrescribirlo?");
        }

    }


    private void askOverriding(String message) throws TransformerException, ParserConfigurationException {
        int choiceInt = JOptionPane.showConfirmDialog(null,message);
        if(userSaidYes(choiceInt)) {
            createXML();
        }
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


    public void createXML() throws ParserConfigurationException, TransformerException {

        Document doc = docBuilder.newDocument();
        Element rootElelemnt = doc.createElement("registre_alumnes");
        doc.appendChild(rootElelemnt);
        Source source = new DOMSource(doc);
        Result result = new StreamResult(XMLFILE);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
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
