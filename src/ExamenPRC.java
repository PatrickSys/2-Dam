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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


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
    XPath xpath = XPathFactory.newInstance().newXPath();



    static class Alumne {
        String name = "";
        String curs = "";
        String dateOfBirth = "";
        String school = "";



        public Alumne(String name, String curs, String dateOfBirth, String school) {
            this.name = name;
            this.curs = curs;
            this.dateOfBirth = dateOfBirth;
            this.school = school;
        }

        public static String[] toXMLString() {
            return new String[] {"nom_alumne", "curs", "any_naixement", "colegi" };
        }

        @Override
        public String toString() {
            return "Alumne " +
                    "Nom: '" + name + '\'' +
                    ", curs: '" + curs + '\'' +
                    ", Any de naixement: '" + dateOfBirth + '\'' +
                    ", Escola: '" + school + "\n\t"
                    ;
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

    /**
     * Shows the optionpane for the menu
     */
    private String showMainMenu() {
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

    private int showTagMenu() {
        return inputInt("""
                        ******************************
                        Dato a modificar: \s
                        1.- Nombre
                        2.- Curso
                        3.- Año nacimiento
                        4.- Colegio      
                        ******************************
                         """);
    }

    private int showConsultMenu() {
        return inputInt("""
                        ******************************
                        Dato a  consultar: \s
                        1.- Consultar todos los nombres de los alumnos
                        2.- Consultar los alumnos que vayan al colegio Cide
                        3.- Consultar el nombre del alumno con codigo 3
                        4.- Consultar los alumnos nacidos antes de 1990
                        0.- Salir al menú principal      
                        ******************************
                         """);
    }

    /**
     * logic of the menu to capture the choice
     * @param choice
     */
    private void handleChoice(int choice) throws TransformerException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        switch (choice) {
            case 1:
                handleXMLCreation();
                handleMenu();
                break;

            case 2:
                addDataToXML();
                handleMenu();
                break;

            case 3:
                showXML();
                handleMenu();
                break;

            case 4:
                modifyXML();
                handleMenu();
                break;

            case 5:
                handleConsultMenu();
                handleMenu();
                break;

            case 6:
                deleteAlumne();
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

    public void handleXMLCreation() throws IOException, TransformerException, ParserConfigurationException, SAXException {

        // createNewFile will return false if the file already exists
        if(!XMLFILE.createNewFile()) {
            askConfirmation( "el fitxer " + XMLFILE.getName() + " ja existeix, vols sobrescriurer-lo?");
        }
    }


    /**
     * Asks the user if he wants to override the file
     * @param message to prompt the user
     */
    public void askConfirmation(String message) throws TransformerException, ParserConfigurationException, IOException, SAXException {
       int choiceInt = JOptionPane.showConfirmDialog(null,message);
       if(userSaidYes(choiceInt)) {
            createXML();
       }
    }

    /**
     * creates the root xml
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void createXML() throws TransformerException {

        Document doc = docBuilder.newDocument();
        Element rootElelemnt = doc.createElement("registre_alumnes");
        doc.appendChild(rootElelemnt);
        Source source = new DOMSource(doc);
        Result result = new StreamResult(XMLFILE);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    // asks the user to input a code and verifies it's unique
    private int addCodiAlumne(Document doc){
        int codiAlumne = inputInt("codi alumne");
        NodeList nodes = doc.getFirstChild().getChildNodes();

        // iterates over all the alumnes nodes to check whether the code already exists
        for (int i = 0; i < nodes.getLength() -1; i++) {
            if(null == nodes.item(i).getAttributes()) {
                continue;
            }
            if (nodes.item(i).getAttributes().getNamedItem("codi_alumne").getNodeValue().equals(String.valueOf(codiAlumne))) {
                showMessage("Codi ja en ús");
                return addCodiAlumne(doc);
            }
        }
        return codiAlumne;
    }


    public void addDataToXML() throws IOException, SAXException, ParserConfigurationException, TransformerException {

        Document doc = handleFileNotFound();
        int numberOfAlumnes = inputInt(" ¿Quants alumnes vols crear? ");
        createSeveralAlumnes(numberOfAlumnes,doc );

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        Result result = new StreamResult(this.XMLFILE);
        transformer.transform(source, result);
    }

    // Option to create as much alumnes as wanted
    private void createSeveralAlumnes(int times, Document doc) throws IOException, TransformerException, ParserConfigurationException {
        for (int i=0; i<times; i++) {
            createAlumnesElement( doc);
        }
    }

    // Creates an alumne DOM element
    private void createAlumnesElement(Document doc) throws ParserConfigurationException, TransformerException, IOException {

        int codiAlumne;

        // Crea el elemento alumno base
        Element alumnes = doc.createElement("alumnes");
        doc.getFirstChild().appendChild(alumnes);

        for(String field: Alumne.toXMLString()) {
            Element elField = doc.createElement(field);
            String fieldValue = inputString(field);
            elField.setTextContent(fieldValue);
            alumnes.appendChild(elField);
        }
        // We must validate the code to be unique
        codiAlumne = addCodiAlumne(doc);
        alumnes.setAttribute("codi_alumne", String.valueOf(codiAlumne));
    }

    public void showXML() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
        Document doc = handleFileNotFound();
        NodeList alumnesNodeList = (NodeList) xpath.evaluate("//alumnes", doc, XPathConstants.NODESET);
        ArrayList<Alumne> alumnesList = createAlumnesList(alumnesNodeList);
        showMessage(parseAlumnesList(alumnesList));
    }

    private ArrayList<Alumne> createAlumnesList(NodeList alumnesNodeList) throws XPathExpressionException {
        ArrayList<Alumne> alumnesList = new ArrayList<>();
        for (int i = 0; i < alumnesNodeList.getLength(); i++) {
            Element alumneEl = (Element) alumnesNodeList.item(i);
            String name = (String) xpath.evaluate("nom_alumne", alumneEl, XPathConstants.STRING);
            String curs = (String) xpath.evaluate("curs", alumneEl, XPathConstants.STRING);
            String dateOfBirth = (String) xpath.evaluate("any_naixement", alumneEl, XPathConstants.STRING);
            String colegi = (String) xpath.evaluate("colegi", alumneEl, XPathConstants.STRING);
            Alumne alumne = new Alumne(name, curs, dateOfBirth, colegi);
            alumnesList.add(alumne);
        }
        return alumnesList;
    }

    /**
     * Asks for the code of the alumne to modify,
     * looks for it and asks the user the tag to be modified
     */
    public void modifyXML() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        Document doc = handleFileNotFound();
        boolean found = false;

        NodeList nodes = doc.getFirstChild().getChildNodes();
        int codeToBeModified =  inputInt(" Codigo alumno a modificar: ");

        // iterates over all the alumnes
        for (int i = 0; i < nodes.getLength(); i++) {
            // error avoiding
            if(null == nodes.item(i).getAttributes()) {
                continue;
            }
            // if the code of any alumne matches the one to be modified, it will ask for the  tag to be modified
            if (nodes.item(i).getAttributes().getNamedItem("codi_alumne").getNodeValue().equals(String.valueOf(codeToBeModified))){
                found = true;
                NodeList alumneChildNodes = nodes.item(i).getChildNodes();
                String tag = tagToModify();

                // modifies the tag the user wanted
                for (int j = 0; j < alumneChildNodes.getLength(); j++) {
                    if(alumneChildNodes.item(j).getNodeName().equals(tag)){
                        String newData = inputString(" Datos a introducir: ");
                        alumneChildNodes.item(j).setTextContent(newData);
                    }
                }
            }
        }
        Source source = new DOMSource(doc);
        Result result = new StreamResult(XMLFILE);
        transformer.transform(source, result);

        if(!found){
            showMessage("El alumno con código " + codeToBeModified + " no existe" );
        }
    }

    public void handleConsultMenu() throws IOException, TransformerException, ParserConfigurationException, SAXException, XPathExpressionException {

            int choice = showConsultMenu();

            switch (choice){
                case 1:
                    nameConsult();
                    break;
                case 2:
                    consultaAlumnosCide();
                    break;
                case 3:
                    consultAlumneCode3();
                    break;
                case 4:
                    consultAlumnesBornBefore1990();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
            }

    }

    /**
     * Consults the names of all the students
     */
    public void nameConsult() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
        Document doc = handleFileNotFound();
        NodeList alumnesNodeList = (NodeList) xpath.evaluate("//alumnes", doc, XPathConstants.NODESET);

        // String to later prompt the data
        String parsedList = "";

        // iterates over all alumnes to retrieve their data
        for (int i = 0; i < alumnesNodeList.getLength(); i++) {
            Element alumne = (Element) alumnesNodeList.item(i);
            String name = (String) xpath.evaluate("nom_alumne", alumne, XPathConstants.STRING);
            parsedList += " Nombre alumno: " + name + check10Mark(name) + "\n"  ;
        }
        showMessage(parsedList);
    }


    /**
     * Consults the name of the cide students
     */
    public void consultaAlumnosCide() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {


        Document doc = handleFileNotFound();
        NodeList alumnesNodeList = (NodeList) xpath.evaluate("//alumnes[colegi='Cide']", doc, XPathConstants.NODESET);

        // Crea el elemento alumno base
        Element alumnes = doc.createElement("alumnes");
        doc.getFirstChild().appendChild(alumnes);

        String parsedList = "";

        // iterates over all alumnes looking for students whose school is cide
        for(int i = 0; i < alumnesNodeList.getLength(); i++) {
            Element alumne = (Element) alumnesNodeList.item(i);

            for(String field: Alumne.toXMLString()) {
                String value = (String) xpath.evaluate(field, alumne, XPathConstants.STRING);
                String fieldValue = field + ": " + value + " ";
                parsedList += fieldValue;
            }
        }
        if(parsedList.isBlank()){
            parsedList = " No hay ningún alumno del Cide!";
        }
        showMessage(parsedList);
    }

    /**
     * Consults the student with code 3
     */
    public void consultAlumneCode3() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        Document doc = handleFileNotFound();
        NodeList nodes2 = (NodeList) xpath.evaluate("//alumnes[@codi_alumne='3']", doc, XPathConstants.NODESET);

        for (int i = 0; i < nodes2.getLength(); i++) {
            Element alumne = (Element) nodes2.item(i);
            String name = (String) xpath.evaluate("nom_alumne", alumne, XPathConstants.STRING);
            showMessage(" Name: " + name);
        }
    }

    /**
     * Consults the students born before 1990
     */
    public void consultAlumnesBornBefore1990() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(XMLFILE);

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList alumnesNodeList = (NodeList) xpath.evaluate("//alumnes[any_naixement<1990]", doc, XPathConstants.NODESET);

        // iterates over alumnes and checks whose were born before 1990
        String parsedList = "";
        for (int i = 0; i < alumnesNodeList.getLength(); i++) {
            for(String field: Alumne.toXMLString()) {
                Element alumne = (Element) alumnesNodeList.item(i);
                String value = (String) xpath.evaluate(field, alumne, XPathConstants.STRING);
                String fieldValue = field + ": " + value + ", ";
                parsedList += fieldValue;
            }
            parsedList += "\n";
        }
        if(parsedList.isBlank()) {
            parsedList = " No hay ningun alumno nacido antes del 1990!";
        }

        showMessage(parsedList);
    }

    /**
     * deletes an alumne given a code
     */
    public void deleteAlumne() throws IOException, SAXException, TransformerException, ParserConfigurationException {
        Document doc = handleFileNotFound();
        NodeList nodes = doc.getFirstChild().getChildNodes();

        int alumneCodeToDelete = inputInt("Código del alumno a eliminar");
        for (int i = 0; i < nodes.getLength() -1; i++) {
            if(null == nodes.item(i).getAttributes()) {
                continue;
            }
            if (nodes.item(i).getAttributes().getNamedItem("codi_alumne").getNodeValue().equals(String.valueOf(alumneCodeToDelete))){
                nodes.item(i).getParentNode().removeChild(nodes.item(i));
            }
        }
        Source source = new DOMSource(doc);
        Result result = new StreamResult(XMLFILE);
        transformer.transform(source, result);
    }

    private String check10Mark(String name) {
        if (name.equalsIgnoreCase("patrick")){
           return  " y se merece un 10!";
        }
        else {
            return "";
        }
    }


    /**
     * startMenu Is the main method for handling the menu
     * @throws IOException and catches it in case user inputs a wrong choice
     */
    public void startMenu() throws ParserConfigurationException, TransformerException, IOException {
        try {
            handleMenu();
        } catch (NumberFormatException | SAXException | XPathExpressionException e) {
            returnToMenuAfterWrongInput();
        }
    }
    /**
     * Checks for null inputs or non-int input
     */
    private void handleMenu() throws TransformerException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        String inputString;
        int inputInt;

        inputString = showMainMenu();

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
     * returns to handlemenu if the user didn't introduce a valid choice
      */
    private void returnToMenuAfterWrongInput() throws IOException, TransformerException, ParserConfigurationException {
        returnToMenuWithMessage("Opción inválida, por favor,\n introduce un número entero entre 0 y 6 ");
    }


    private void returnToMenuWithMessage(String message) throws IOException, TransformerException, ParserConfigurationException {
        showMessage(message);
        startMenu();
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



    private Document handleFileNotFound() throws ParserConfigurationException, TransformerException, IOException, SAXException {
        try{
            return  docBuilder.parse(XMLFILE);

        }catch(FileNotFoundException | SAXException fe) {
            returnToMenuWithMessage(" El fichero no existe, debes crearlo primero");
            return docBuilder.parse(XMLFILE);
        }
    }

    private String parseAlumnesList(ArrayList<Alumne> alumnesList) {
        String parsedList = "";

        for(Alumne alumne :alumnesList){
            parsedList += alumne.toString();
        }
        return parsedList;
    }


    public String tagToModify() {
            int choice = showTagMenu();

            switch (choice){
                case 1:
                    return "nom_alumne";
                case 2:
                     return "curs";
                case 3:
                    return "any_naixement";
                case 4:
                    return "colegi";
                default:
                    showMessage("Por favor, introduce un número entre 1 y 4");
                    return tagToModify();
            }
        }
}
