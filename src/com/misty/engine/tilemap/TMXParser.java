package com.misty.engine.tilemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TMXParser {

	/**
	 * 
	 * @param str file name of the .tmx file you're trying to load
	 * @return TileMap object that Renderer can draw
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static TileMap createTileMap(String str) throws IOException {
		File fXmlFile = new File(str);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc;

			doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			Element info = (Element) doc.getFirstChild();
			int width = Integer.valueOf(info.getAttribute("width"));
			int height = Integer.valueOf(info.getAttribute("height"));
			NodeList nList = doc.getElementsByTagName("layer");
			NodeList tilesets = doc.getElementsByTagName("tileset");
			List<Tileset> tss = new ArrayList<Tileset>();
			for (int i = 0; i < tilesets.getLength(); i++) {
				Element e = (Element) tilesets.item(i);
				int tileWidth, tileHeight;
				String source;
				Element img = (Element) (e.getElementsByTagName("image").item(0));
				tileWidth = Integer.valueOf(e.getAttribute("tilewidth"));
				tileHeight = Integer.valueOf(e.getAttribute("tileheight"));
				// tileCount = Integer.valueOf(e.getAttribute("tilecount"));
				// columns = Integer.valueOf(e.getAttribute("columns"));
				source = img.getAttribute("source");
				tss.add(new Tileset(source, tileWidth, tileHeight));
			}

			int[][] data = new int[nList.getLength()][];
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String[] st = eElement.getElementsByTagName("data").item(0).getTextContent().split(",");
					data[temp] = new int[st.length];
					for (int i = 0; i < st.length; i++) {
						data[temp][i] = Integer.valueOf(st[i].trim());
					}

				}
			}
			Tileset[] set = new Tileset[tss.size()];
			tss.toArray(set);
			System.out.println("successfully loaded " + str);
			return new TileMap(set, data, width, height);

		} catch (ParserConfigurationException e1) {
			throw new IOException("Parser config exception " + e1.getLocalizedMessage());
		} catch (SAXException e1) {
			throw new IOException("SAXException " + e1.getLocalizedMessage());
		}
	}
}
