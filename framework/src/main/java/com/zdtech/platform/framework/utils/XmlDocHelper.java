package com.zdtech.platform.framework.utils;

import com.zdtech.platform.framework.entity.SimSysInsMessage;
import com.zdtech.platform.framework.entity.SimSysInsMessageField;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XmlDocHelper
 *
 * @author panli
 * @date 2016/9/7
 */
public class XmlDocHelper {
    public static Document getXmlFromStr(String strXml){
        try {
            Document dom = DocumentHelper.parseText(strXml);
            return dom;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Document getXmlFromFile(String file){
        SAXReader reader = new SAXReader();
        reader.setIgnoreComments(true);
        reader.setStripWhitespaceText(true);
        try {
            return reader.read(new File(file));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasAttribute(Document doc,String xPath){
        if (doc == null || StringUtils.isEmpty(xPath))
            return false;
        String ns = doc.getRootElement().getNamespaceURI();
        if (StringUtils.isNotEmpty(ns)){
            HashMap map = new HashMap();
            map.put("temp",ns);

            xPath = getXMLNameSpaceFixed(xPath,"temp");
            XPath docXPath = doc.createXPath(xPath);
            docXPath.setNamespaceURIs(map);
            Node node = docXPath.selectSingleNode(doc);
            if (node!=null){
                return true;
            }
            return false;
        }
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            return true;
        }
        return false;
    }
    public static String getNodeValue(Document doc,String xPath){
        if (doc == null || StringUtils.isEmpty(xPath))
            return "";
        String ns = doc.getRootElement().getNamespaceURI();
        if (StringUtils.isNotEmpty(ns)){
            HashMap map = new HashMap();
            map.put("temp",ns);

            xPath = getXMLNameSpaceFixed(xPath,"temp");
            XPath docXPath = doc.createXPath(xPath);
            docXPath.setNamespaceURIs(map);
            Node node = docXPath.selectSingleNode(doc);
            if (node!=null){
                return node.getText().trim();
            }
            return "";
        }
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            return node.getText().trim();
        }
        return "";
    }

    public static void setNodeValue(Document doc,String xPath,String value){
        if (doc == null || StringUtils.isEmpty(xPath) || value == null)
            return;
        String ns = doc.getRootElement().getNamespaceURI();
        if (StringUtils.isNotEmpty(ns)){
            HashMap map = new HashMap();
            map.put("temp",ns);

            xPath = getXMLNameSpaceFixed(xPath,"temp");
            XPath docXPath = doc.createXPath(xPath);
            docXPath.setNamespaceURIs(map);
            Node node = docXPath.selectSingleNode(doc);
            if (node!=null){
                node.setText(value);
            }
            return;
        }
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            node.setText(value);
        }
    }
    public static String getNodeContent(Document doc,String xPath){
        if (doc == null || StringUtils.isEmpty(xPath))
            return null;
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            return node.asXML();
        }
        return null;
    }
    public static void setNodeContent(Document doc,String xPath,String value){
        if (doc == null || StringUtils.isEmpty(xPath))
            return ;
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            Element element = (Element)node;

            element.add(XmlDocHelper.getXmlFromStr(value).getRootElement());
        }
    }

    public static void setNodeElements(Document doc,String xPath,List<Element> elements){
        if (doc == null || StringUtils.isEmpty(xPath))
            return ;
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            for (Element element:elements){
                ((Element)node).add(element);
            }
        }
    }
    public static Element getNodeElement(Document doc,String xPath){
        if (doc == null || StringUtils.isEmpty(xPath))
            return null;
        Node node = doc.selectSingleNode(xPath);
        return ((Element)node).createCopy();
    }

    public static void setNodeElement(Document doc,String xPath,Element element){
        if (doc == null || StringUtils.isEmpty(xPath))
            return ;
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            ((Element)node).add(element);
        }
    }
    public static List<Element> getNodeElements(Document doc, String xPath) {
        if (doc == null || StringUtils.isEmpty(xPath))
            return null;
        String ns = doc.getRootElement().getNamespaceURI();
        if (StringUtils.isNotEmpty(ns)){
            HashMap map = new HashMap();
            map.put("temp",ns);

            xPath = getXMLNameSpaceFixed(xPath,"temp");
            XPath docXPath = doc.createXPath(xPath);
            docXPath.setNamespaceURIs(map);
            return docXPath.selectNodes(doc);
        }
        List list = doc.selectNodes(xPath);
        return list;
    }
    public static void setCDATA(Document doc, String xPath, String orgMsg) {
        if (doc == null || StringUtils.isEmpty(xPath))
            return ;
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            ((Element)node).addCDATA(orgMsg);
        }
    }

    public static String getXMLNameSpaceFixed(String xpath,String ns)
    {
        xpath= xpath.replaceAll("/(\\w)", "/"+ns+":$1");
        xpath= xpath.replaceAll("^(\\w)", ns+":$1");
        return xpath;
    }

    public static void deleteNull(Element element){
        List elements = element.elements();
        if(elements.size() == 0){
            if(element.getTextTrim().startsWith("$null")){
                Element e = element.getParent();
                e.remove(element);
                deleteEmpty(e);
            }
        } else {
            Iterator iterator = elements.iterator();
            while (iterator.hasNext()){
                deleteNull((Element) iterator.next());
            }
        }
    }

    public static void deleteEmpty(Element element){
        List elements = element.elements();
        if(elements.size() == 0){
            if(element.getTextTrim().isEmpty()){
                Element e = element.getParent();
                e.remove(element);
                deleteEmpty(e);
            }
        }
    }

    public static void deleteElement(Document doc,String xPath){
        if (doc == null || StringUtils.isEmpty(xPath))
            return;
        Node node = doc.selectSingleNode(xPath);
        if (node!=null){
            node.getParent().remove(node);
        }
    }

    public static String xmlZuZhuang(String msg, SimSysInsMessage simSysInsMessage){
        Document srcdoc = XmlDocHelper.getXmlFromStr(msg);
        Document targetdoc = XmlDocHelper.getXmlFromStr(simSysInsMessage.getModelFileContent());
        List<SimSysInsMessageField> fieldList = simSysInsMessage.getMsgFields();
        for(SimSysInsMessageField field : fieldList){
            String xpath = field.getMsgField().getFieldId();
            String value = XmlDocHelper.getNodeValue(srcdoc, xpath);
            if(value == null){
                XmlDocHelper.deleteElement(targetdoc, xpath);
                continue;
            }
            XmlDocHelper.setNodeValue(targetdoc, xpath, value);
        }
        return targetdoc.asXML();
    }

    public static String getXmlByMap(Map<String, String> map, Document doc){
        for(String fieldId : map.keySet()){
            XmlDocHelper.setNodeValue(doc, fieldId, map.get(fieldId)==null?"":map.get(fieldId));
        }
        return doc.asXML();
    }

    public static void main(String[] args) {
        String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<transaction xmlns=\"urn:ncs2:std:nps:2012:tech:xsd:NPS.903.001.01\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  \n" +
                "    <header> \n" +
                "        <ver>02</ver>  \n" +
                "        <msg> \n" +
                "            <msgCd>NPS.903.001.01</msgCd>  \n" +
                "            <sndAppCd>MPS</sndAppCd>  \n" +
                "            <sndMbrCd>402002000011</sndMbrCd>  \n" +
                "            <sndDt>20180412</sndDt>  \n" +
                "            <sndTm>112747</sndTm>  \n" +
                "            <rcvAppCd>NPS</rcvAppCd>  \n" +
                "            <rcvMbrCd>100000000000</rcvMbrCd>  \n" +
                "            <seqNb>000000002081332</seqNb>  \n" +
                "            <structType>XML</structType>  \n" +
                "            <callTyp>ASYN</callTyp> \n" +
                "        </msg> \n" +
                "    </header>  \n" +
                "    <body> \n" +
                "        <CertNtfctn> \n" +
                "            <GrpHdr> \n" +
                "                <MsgId>201903180002081331</MsgId>  \n" +
                "                <CreDtTm>2018-04-12T11:27:30</CreDtTm>  \n" +
                "                <InstgPty> \n" +
                "                    <InstgDrctPty>402002000011</InstgDrctPty>  \n" +
                "                    <InstgPty>402002000011</InstgPty> \n" +
                "                </InstgPty>  \n" +
                "                <InstdPty> \n" +
                "                    <InstdDrctPty>100000000000</InstdDrctPty>  \n" +
                "                    <InstdPty>100000000000</InstdPty> \n" +
                "                </InstdPty>  \n" +
                "                <TranChnlTyp>06</TranChnlTyp> \n" +
                "            </GrpHdr>  \n" +
                "            <CertNtfctnInf> \n" +
                "                <ChgTp>CC00</ChgTp> \n" +
                "            </CertNtfctnInf> \n" +
                "        </CertNtfctn> \n" +
                "    </body>  \n" +
                "    <signature></signature> \n" +
                "</transaction>";
        Document doc = getXmlFromStr(xmlStr);
        String path = "ver";
        String value ;
        if(path.startsWith("/")){
            value = getNodeValue(doc, path);
        } else {
            value = getNodeValue(doc, "//" + path);
        }
        System.out.println(value);
    }

}
