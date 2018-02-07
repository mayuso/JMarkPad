package io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import utilities.VariablesToSave;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class XML {

    private Document document;

    public XML(String filePath) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        try {
            document = documentBuilder.parse(new File(filePath));
        } catch (FileNotFoundException | SAXParseException e) {
            new File(filePath).delete();
            document = documentBuilder.newDocument();

        }
    }

    public String loadVariable(String tagName) {
        return document.getElementsByTagName(tagName).item(0).getTextContent();
    }

    public String[] loadVariables(String tagName) {

        NodeList nl = document.getElementsByTagName(tagName);

        String[] variablesList = new String[nl.getLength()];
        for (int i = 0; i < variablesList.length; i++) {
            variablesList[i] = nl.item(i).getTextContent();
        }
        return variablesList;
    }


    public void writeVariables(VariablesToSave variablesToSave) {

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("JMarkPad");
            document.appendChild(rootElement);

            Element posXElement = document.createElement("posX");
            posXElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.posX)));
            rootElement.appendChild(posXElement);
            Element posYElement = document.createElement("posY");
            posYElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.posY)));
            rootElement.appendChild(posYElement);

            Element widthElement = document.createElement("width");
            widthElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.width)));
            rootElement.appendChild(widthElement);
            Element heightElement = document.createElement("height");
            heightElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.height)));
            rootElement.appendChild(heightElement);

            Element redElement = document.createElement("red");
            redElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.red)));
            rootElement.appendChild(redElement);
            Element greemElement = document.createElement("green");
            greemElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.green)));
            rootElement.appendChild(greemElement);
            Element blueElement = document.createElement("blue");
            blueElement.appendChild(document.createTextNode(String.valueOf(variablesToSave.blue)));
            rootElement.appendChild(blueElement);


            for (String pathName : variablesToSave.paths) {
                Element file = document.createElement("file");
                file.appendChild(document.createTextNode(pathName));
                rootElement.appendChild(file);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("config.xml"));


            transformer.transform(source, result);


        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }


}
