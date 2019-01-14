package com.baselet.diagram.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;

import javax.swing.filechooser.FileFilter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.baselet.control.config.Config;
import com.baselet.control.constants.Constants;
import com.baselet.control.enums.Program;
import com.baselet.diagram.DiagramHandler;
import com.baselet.element.NewGridElement;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.old.custom.CustomElement;

public class DiagramFileHandler {

	private static final Logger log = LoggerFactory.getLogger(DiagramFileHandler.class);

	private final DiagramHandler handler;
	private String source;

	protected DiagramFileHandler(DiagramHandler diagramHandler, String source) {
		handler = diagramHandler;
		this.source = source;
	}

	public static DiagramFileHandler createInstance(DiagramHandler diagramHandler, String source) {
		return new DiagramFileHandler(diagramHandler, source);
	}

	private void createXMLOutputDoc(Document doc, Collection<GridElement> elements, Element current) {
		for (GridElement e : elements) {
			appendRecursively(doc, current, e);
		}
	}

	private void appendRecursively(Document doc, Element parentXmlElement, GridElement e) {
		parentXmlElement.appendChild(createXmlElementForGridElement(doc, e));
	}

	private Element createXmlElementForGridElement(Document doc, GridElement e) {
		// insert normal entity element
		java.lang.Class<? extends GridElement> c = e.getClass();
		String sElType = c.getName();
		String sElPanelAttributes = e.getPanelAttributes();
		String sElAdditionalAttributes = e.getAdditionalAttributes();

		Element el = doc.createElement("element");

		if (e instanceof NewGridElement) {
			Element elType = doc.createElement("id");
			elType.appendChild(doc.createTextNode(((NewGridElement) e).getId().toString()));
			el.appendChild(elType);
		}
		else { // OldGridElement
			Element elType = doc.createElement("type");
			elType.appendChild(doc.createTextNode(sElType));
			el.appendChild(elType);
		}

		Element elCoor = doc.createElement("coordinates");
		el.appendChild(elCoor);

		Element elX = doc.createElement("x");
		elX.appendChild(doc.createTextNode("" + e.getRectangle().x));
		elCoor.appendChild(elX);

		Element elY = doc.createElement("y");
		elY.appendChild(doc.createTextNode("" + e.getRectangle().y));
		elCoor.appendChild(elY);

		Element elW = doc.createElement("w");
		elW.appendChild(doc.createTextNode("" + e.getRectangle().width));
		elCoor.appendChild(elW);

		Element elH = doc.createElement("h");
		elH.appendChild(doc.createTextNode("" + e.getRectangle().height));
		elCoor.appendChild(elH);

		Element elPA = doc.createElement("panel_attributes");
		elPA.appendChild(doc.createTextNode(sElPanelAttributes));
		el.appendChild(elPA);

		Element elAA = doc.createElement("additional_attributes");
		elAA.appendChild(doc.createTextNode(sElAdditionalAttributes));
		el.appendChild(elAA);

		if (e instanceof CustomElement) {
			Element elCO = doc.createElement("custom_code");
			elCO.appendChild(doc.createTextNode(((CustomElement) e).getCode()));
			el.appendChild(elCO);
		}
		return el;
	}

	protected String createStringToBeSaved() {
		DocumentBuilder db = null;
		String returnString = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element root = doc.createElement("diagram");
			root.setAttribute("program", Program.getInstance().getProgramName().toLowerCase());
			root.setAttribute("version", String.valueOf(Program.getInstance().getVersion()));
			doc.appendChild(root);

			// save helptext
			String helptext = handler.getHelpText();
			if (!helptext.equals(Constants.getDefaultHelptext())) {
				Element help = doc.createElement("help_text");
				help.appendChild(doc.createTextNode(helptext));
				root.appendChild(help);
			}

			// save zoom
			Element zoom = doc.createElement("zoom_level");
			zoom.appendChild(doc.createTextNode(String.valueOf(handler.getGridSize())));
			root.appendChild(zoom);

			createXMLOutputDoc(doc, handler.getDrawPanel().getGridElements(), root);

			// output the stuff...
			DOMSource source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			StreamResult result = new StreamResult(stringWriter);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(source, result);

			returnString = stringWriter.toString();
		} catch (Exception e) {
			log.error("Error saving XML.", e);
		}

		return returnString;

	}

	public void parse(InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		if (Config.getInstance().isSecureXmlProcessing()) {
			// use secure xml processing (see https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#JAXP_DocumentBuilderFactory.2C_SAXParserFactory_and_DOM4J)
			spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		}
		SAXParser parser = spf.newSAXParser();
		InputHandler xmlhandler = new InputHandler(handler);
		parser.parse(inputStream, xmlhandler);
		inputStream.close();
	}

	public void doParse() {
		if (source == null || source.trim().isEmpty()) {
			return;
		}
		try {
			parse(new ByteArrayInputStream(source.getBytes()));
		} catch (SAXException | ParserConfigurationException | IOException e) {
			throw new RuntimeException("Unable to parse the source", e);
		}
	}

	protected static class OwnFileFilter extends FileFilter {
		private final String format;
		private final String description;

		protected OwnFileFilter(String format, String description) {
			this.format = format;
			this.description = description;
		}

		@Override
		public boolean accept(File f) {
			return f.getName().endsWith("." + format) || f.isDirectory();
		}

		@Override
		public String getDescription() {
			return description + " (*." + format + ")";
		}

		public String getFormat() {
			return format;
		}
	}
}
