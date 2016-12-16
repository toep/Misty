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
	public static TileMap createTileMap(String str) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(str);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		Element info = (Element)doc.getFirstChild();
		int width = Integer.valueOf(info.getAttribute("width"));
		int height = Integer.valueOf(info.getAttribute("height"));
		NodeList nList = doc.getElementsByTagName("layer");
		NodeList tilesets = doc.getElementsByTagName("tileset");
		List<Tileset> tss = new ArrayList<Tileset>();
		for(int i = 0; i < tilesets.getLength(); i++) {
			Element e = (Element)tilesets.item(i);
			int tileWidth, tileHeight;
			String source;
			Element img = (Element)(e.getElementsByTagName("image").item(0));
			tileWidth = Integer.valueOf(e.getAttribute("tilewidth"));
			tileHeight = Integer.valueOf(e.getAttribute("tileheight"));
			//tileCount = Integer.valueOf(e.getAttribute("tilecount"));
			//columns = Integer.valueOf(e.getAttribute("columns"));
			source = img.getAttribute("source");
			tss.add(new Tileset(source, tileWidth, tileHeight));
		}
		
		
		//System.out.println("width: " + ((Element)doc.getFirstChild()).getAttribute("width"));
		
		
		//System.out.println("----------------------------");

		int[][] data = new int[nList.getLength()][];
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			//System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				//System.out.println("Layer : " + eElement.getAttribute("name"));
				//System.out.println("Data : " + eElement.getElementsByTagName("tileset").item(0).getTextContent());
				String[] st = eElement.getElementsByTagName("data").item(0).getTextContent().split(",");
				data[temp] = new int[st.length];
				for(int i =0; i < st.length; i++) {
					data[temp][i] = Integer.valueOf(st[i].trim());
				}
				//System.out.println("Data : " + eElement.getElementsByTagName("data").item(0).getTextContent());
				//System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
				//System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
				//System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

			}
		}
		Tileset[] set = new Tileset[tss.size()];
		tss.toArray(set);
		System.out.println("successfully loaded " + str);
		return new TileMap(set, data, width, height);
	}
}
