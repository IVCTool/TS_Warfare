/**
 * Copyright 2017, QinetiQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qinetiq.msg134.etc.tc_lib_warfare;

import hla.rti1516e.encoding.*;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.qinetiq.msg134.etc.tc_lib_warfare.decode.RTIobjectIdDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores files (FOM definitions) and will generate decoders for interactions or
 * objects
 *
 * @author rjjones3
 * @since 20/09/2016
 */
public class DecoderGenerator
{
    private final Logger logger;
    private EncoderFactory encoder;
    private List<Document> docs;
    
    /**
     * Constructor
     * 
     * @param urls
     *            The URLs of the FOM modules to use
     * @param encoderFactory
     *            A reference to the EncoderFactory to use
     * @param logger
     *            A reference to the logger
     * @throws ParserConfigurationException
     *             If a module failed to parse
     * @throws IOException
     *             If there was an IO exception whilst reading the module
     * @throws SAXException
     *             If there was a SAXException whilst trying to parse the XML
     */
    public DecoderGenerator(URL[] urls, EncoderFactory encoderFactory, final Logger logger)
            throws ParserConfigurationException, SAXException, IOException
    {
        this.encoder = encoderFactory;
        this.logger = logger;
        
        /**
         * parse all the files from TcParam (or whereever) and put them in an arraylist.
         */
        docs = new ArrayList<>();
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        
        for (URL url : urls)
        {
            docs.add(dBuilder.parse(url.getFile()));
        }
        
    }
    
    /**
     * Search a document for the given interaction name.
     *
     * @param document
     *            the document to search
     * @param interactionName
     *            the interaction name to search for
     * @return the node containing the interaction definition
     */
    private Node searchFileInteraction(Document document, String interactionName)
    {
        /**
         * Grab all interactionClass
         */
        NodeList nl = document.getElementsByTagName("interactions");
        
        NodeList interactions = null;
        if (nl.getLength() > 0)
        {
            Node topInteractions = nl.item(0);
            logger.info("Interactions found in file" + document.getDocumentURI());
            
            if (topInteractions.getNodeType() == Node.ELEMENT_NODE)
            {
                interactions = ((Element) topInteractions).getElementsByTagName("interactionClass");
            }
            else
            {
                logger.error("Interactions not defined correctly.");
            }
        }
        else
        {
            // no interactions
            logger.debug("No interactions in file.");
        }
        
        // The node to return.
        Node theNode = null;
        
        if (interactions != null)
        {
            logger.info("Number of interactions defined in file: " + interactions.getLength());
            
            SEARCH_LOOP:
            for (int temp = 0; temp < interactions.getLength(); temp++)
            {
                Node node = interactions.item(temp);
                
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    /** grab the first element, which (for interactionClass) should be the name */
                    NodeList name = element.getElementsByTagName("name");
                    
                    if (name.getLength() > 0)
                    {
                        /** first and only node should be the name */
                        Node n = name.item(0);
                        String givenName = n.getTextContent();
                        if (givenName.equalsIgnoreCase(interactionName))
                        {
                            NodeList subTags = element.getElementsByTagName("sharing");
                            if (subTags.getLength() > 0)
                            {
                                logger.info("Match successfully found in document " + document.getDocumentURI());
                                theNode = node;
                                break SEARCH_LOOP;
                            }
                            else
                            {
                                logger.info("Parentage indicator, not actual definition.");
                            }
                        }
                        else
                        {
                            logger.debug("Name not a match");
                        }
                    }
                    else
                    {
                        logger.error("Incorrect number of name tags");
                    }
                }
            }
        }
        
        if (theNode == null)
        {
            logger.debug("InteractionClass with given name not found.");
        }
        
        return theNode;
    }
    
    /**
     * Search a document for the given object name.
     *
     * @param document
     *            the document to search
     * @param objectName
     *            the interaction name to search for
     * @return the node containing the object definition
     */
    private Node searchFileObject(Document document, String objectName)
    {
        /**
         * Grab all interactionClass
         */
        NodeList nl = document.getElementsByTagName("objects");
        NodeList objects = null;
        
        if (nl.getLength() > 0)
        {
            Node topObjects = nl.item(0);
            
            if (topObjects.getNodeType() == Node.ELEMENT_NODE)
            {
                objects = ((Element) topObjects).getElementsByTagName("objectClass");
            }
            else
            {
                logger.error("Objects not defined correctly.");
            }
        }
        else
        {
            // no objects
            logger.debug("No objects in file.");
        }
        
        // The node to return.
        Node theNode = null;
        
        if (objects != null)
        {
            logger.info("Number of objects defined in file: " + objects.getLength());
            
            SEARCH_LOOP:
            for (int temp = 0; temp < objects.getLength(); temp++)
            {
                Node node = objects.item(temp);
                Element element = (Element) node;
                
                /** grab the first element, which (for interactionClass) should be the name */
                NodeList name = element.getElementsByTagName("name");
                
                if (name.getLength() > 0)
                {
                    /** first and only node should be the name */
                    Node n = name.item(0);
                    String givenName = n.getTextContent();
                    if (givenName.equalsIgnoreCase(objectName))
                    {
                        NodeList subTags = element.getElementsByTagName("sharing");
                        if (subTags.getLength() > 0)
                        {
                            logger.info("Match successfully found in document " + document.getDocumentURI());
                            theNode = node;
                            break SEARCH_LOOP;
                        }
                        else
                        {
                            logger.info("Parentage indicator, not actual definition.");
                        }
                    }
                    else
                    {
                        logger.debug("Name not a match");
                    }
                }
                else
                {
                    logger.error("Incorrect number of name tags");
                }
            }
        }
        
        if (theNode == null)
        {
            logger.debug("objectClass with given name not found.");
        }
        return theNode;
    }
    
    /**
     * Search by the name of the object in all files and return the node once it is
     * found.
     * 
     * @param objectName
     *            the object name to search for.
     * @return the node containing the definition
     */
    private Node searchFOMObject(String objectName)
    {
        // The node to return.
        Node theNode = null;
        
        SEARCH_LOOP:
        for (Document d : docs)
        {
            Node n = searchFileObject(d, objectName);
            if (n == null)
            {
                logger.debug(String.join(" ", "Object class with name", objectName, "not found in document:",
                        d.getDocumentURI()));
            }
            else
            {
                theNode = n;
                break SEARCH_LOOP;
            }
        }
        
        return theNode;
    }
    
    /**
     * Search by the name of the object in all files and return the node once it is
     * found.
     * 
     * @param interactionName
     *            the interaction name to search for.
     * @return the node containing the definition
     */
    private Node searchFOMInteraction(String interactionName)
    {
        // The node to return.
        Node theNode = null;
        
        SEARCH_LOOP:
        for (Document d : docs)
        {
            Node n = searchFileInteraction(d, interactionName);
            if (n == null)
            {
                logger.debug(String.join(" ", "Interaction with name", interactionName, "not found in document:",
                        d.getDocumentURI()));
            }
            else
            {
                theNode = n;
                break SEARCH_LOOP;
            }
            
        }
        return theNode;
        
    }
    
    /**
     * Searches an object node for the given attribute.
     * 
     * @param object
     *            the node with an object definition.
     * @param attribute
     *            the attribute to search for.
     * @return a node containing the attribute definition
     */
    private Node getAttribute(Element object, String attribute)
    {
        // The node to return.
        Node theNode = null;
        
        /** interpret the node as an Element, and get attributes from it */
        NodeList attributes = object.getElementsByTagName("attribute");
        
        /** get the attribute we need */
        SEARCH_LOOP:
        for (int temp = 0; temp < attributes.getLength(); temp++)
        {
            Node node = attributes.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                /** grab the first element, which should be the name */
                NodeList name = element.getElementsByTagName("name");
                
                if (name.getLength() > 0)
                {
                    /** first and only node should be the name */
                    Node n = name.item(0);
                    String givenName = n.getTextContent();
                    if (givenName.equalsIgnoreCase(attribute))
                    {
                        theNode = node;
                        break SEARCH_LOOP;
                    }
                    else
                    {
                        logger.debug("Name not a match");
                    }
                }
                else
                {
                    logger.error("Incorrect number of name tags");
                }
            }
        }
        
        if (theNode == null)
        {
            logger.debug("Attribute with given name not found.");
        }
        return theNode;
    }
    
    /**
     * Searches an interaction node for the given parameter.
     * 
     * @param interaction
     *            the node with an interaction definition.
     * @param parameter
     *            the parameter to search for.
     * @return a node containing the parameter definition
     */
    private Node getParameter(Element interaction, String parameter)
    {
        // The node to return.
        Node theNode = null;
        
        /** interpret the node as an Element, and get parameters from it */
        NodeList parameters = interaction.getElementsByTagName("parameter");
        
        /** get the parameter we need */
        SEARCH_LOOP:
        for (int temp = 0; temp < parameters.getLength(); temp++)
        {
            Node node = parameters.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                /** grab the first element, which should be the name */
                NodeList name = element.getElementsByTagName("name");
                
                if (name.getLength() > 0)
                {
                    /** first and only node should be the name */
                    Node n = name.item(0);
                    String givenName = n.getTextContent();
                    if (givenName.equalsIgnoreCase(parameter))
                    {
                        theNode = node;
                        break SEARCH_LOOP;
                    }
                    else
                    {
                        logger.debug("Name not a match");
                    }
                }
                else
                {
                    logger.debug("Incorrect number of name tags");
                }
            }
        }
        
        if (theNode == null)
        {
            logger.debug("Attribute with given name not found.");
        }
        return theNode;
    }
    
    /**
     * Generates a decoder for the given parameter.
     *
     * Search all files for the interaction, then search within that for the
     * parameter, and finally returns a DataElement constructed from the parameter
     * definition.
     * 
     * @param interaction
     *            the interaction that contains the parameter
     * @param parameter
     *            the parameter to decode
     * @return The decoder for this parameter, or null if one could not be found
     */
    public DataElement generateParameterDecoder(String interaction, String parameter)
    {
        /** search for the interaction */
        Node parameterNode = null;
        
        Node interactionNode = searchFOMInteraction(interaction);
        if (interactionNode == null)
        {
            logger.debug("Failed while searching for interaction.");
        }
        else
        {
            /** within the interaction search for the parameter */
            parameterNode = getParameter((Element) interactionNode, parameter);
            if (parameterNode == null)
            {
                logger.debug("Failed while searching for parameter.");
            }
        }
        
        // The data element to return.
        DataElement theDataElement = null;
        
        if (parameterNode != null)
        {
            // we have found both the object and attribute, get to building
            
            // check datatype field
            NodeList dataType = ((Element) parameterNode).getElementsByTagName("dataType");
            if (dataType.getLength() != 1)
            {
                logger.error("Error: Too many datatype tags.");
            }
            else
            {
                Element dtE = (Element) dataType.item(0);
                String dt = dtE.getTextContent();
                
                /**
                 * lookup data types in datatypes section search simple data types first
                 */
                theDataElement = getDataType(dt);
            }
            
            if (theDataElement == null)
            {
                logger.error("Resolving data type failed.");
            }
        }
        
        return theDataElement;
        
    }
    
    /**
     * Generates a decoder for the given attribute.
     *
     * Search all files for the object, then search within that for the attribute,
     * and finally returns a DataElement constructed from the attribute definition.
     * 
     * @param object
     *            the object that contains the attribute
     * @param attribute
     *            the attribute to decode
     * @return The decoder for this attribute, or null if one could not be found
     */
    public DataElement generateAttributeDecoder(String object, String attribute)
    {
        /** search for the object */
        Node attributeNode = null;
        Node objectNode = searchFOMObject(object);
        if (objectNode == null)
        {
            logger.debug("Failed while searching for object.");
        }
        else
        {
            /** within the object search for the attribute */
            attributeNode = getAttribute((Element) objectNode, attribute);
            if (attributeNode == null)
            {
                logger.debug("Failed while searching for attribute.");
            }
        }
        
        // The data element to return.
        DataElement theDataElement = null;
        
        if (attributeNode != null)
        {
            /**
             * we have found both the object and attribute, get to building
             */
            
            // check datatype field
            NodeList dataType = ((Element) attributeNode).getElementsByTagName("dataType");
            if (dataType.getLength() != 1)
            {
                logger.error("Too many datatype tags.");
            }
            else
            {
                Element dtE = (Element) dataType.item(0);
                String dt = dtE.getTextContent();
                
                /**
                 * lookup data types in datatypes section search simple data types first
                 */
                theDataElement = getDataType(dt);
            }
            
            if (theDataElement == null)
            {
                logger.error("Resolving data type failed.");
            }
        }
        
        return theDataElement;
    }
    
    /**
     * Generates a list of parameters within an interaction definition.
     * 
     * @param interaction
     *            the interaction to describe
     * @return a list of strings of all the parameters
     */
    public List<String> generateParameterList(String interaction)
    {
        /** search for the interaction */
        ArrayList<String> list;
        Element interactionNode = (Element) searchFOMInteraction(interaction);
        if (interactionNode == null)
        {
            logger.debug("Failed while searching for interaction.");
            list = null;
        }
        else
        {
            list = new ArrayList<>();
            /** interpret the node as an Element, and get attributes from it */
            NodeList parameters = interactionNode.getElementsByTagName("parameter");
            
            /** get the parameter we need */
            for (int temp = 0; temp < parameters.getLength(); temp++)
            {
                Node node = parameters.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    
                    /** grab the first element, which should be the name */
                    NodeList name = element.getElementsByTagName("name");
                    
                    if (name.getLength() > 0)
                    {
                        /** first and only node should be the name */
                        Node n = name.item(0);
                        String givenName = n.getTextContent();
                        list.add(givenName);
                    }
                    else
                    {
                        logger.error("Incorrect number of name tags");
                    }
                }
            }
            list.trimToSize();
            logger.info("Successfully generated parameter list.");
        }
        return list;
    }
    
    /**
     * Generates a list of attributes within an object definition.
     * 
     * @param object
     *            the object to describe
     * @return a list of strings of all the attributes
     */
    public List<String> generateAttributeList(String object)
    {
        /** search for the object */
        ArrayList<String> list;
        
        Element objectNode = (Element) searchFOMObject(object);
        if (objectNode == null)
        {
            logger.debug("Failed while searching for object.");
            list = null;
        }
        else
        {
            list = new ArrayList<>();
            /** interpret the node as an Element, and get attributes from it */
            NodeList parameters = objectNode.getElementsByTagName("attribute");
            
            /** get the attribute we need */
            for (int temp = 0; temp < parameters.getLength(); temp++)
            {
                Node node = parameters.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    
                    /** grab the first element, which should be the name */
                    NodeList name = element.getElementsByTagName("name");
                    
                    if (name.getLength() > 0)
                    {
                        /** first and only node should be the name */
                        Node n = name.item(0);
                        String givenName = n.getTextContent();
                        list.add(givenName);
                    }
                    else
                    {
                        logger.error("Incorrect number of name tags");
                    }
                }
            }
            
            list.trimToSize();
            logger.info("Successfully generated attribute list.");
        }
        return list;
    }
    
    /**
     * Tries to resolve datatypes down to complete objects of type dataElement.
     *
     * First checks against a lookup of BasicDataTypes and returns instantiated HLA
     * types. Then checks by searching the FOM for simple data types for composite
     * definitions which can be broken down into BasicDataTypes. Then checks array
     * types ... and so on in this manner.
     *
     * The individual sub 'get....datatype' methods all recursively call this
     * method.
     *
     * At this time I am not convinced this isn't very stupid.
     * 
     * @param representation
     *            the datatype to represent
     * @return a Data element that contains all appropriate sub containers as
     *         appropriate.
     */
    private DataElement getDataType(String representation)
    {
        /**
         * lookup data types in datatypes section search basic data types first
         */
        DataElement de = basicDataTypeLookup(representation);
        if (de != null)
        {
            logger.info("Success: " + representation + " found as BasicDataType.");
        }
        
        /** search enum datatype */
        if (de == null)
        {
            de = getEnumeratedDataType(representation);
            if (de != null)
            {
                logger.info("Success: " + representation + " found as EnumeratedDataType.");
            }
        }
        
        /** search simple datatype */
        if (de == null)
        {
            de = getSimpleDataType(representation);
            if (de != null)
            {
                logger.info("Success: " + representation + " found as SimpleDataType.");
            }
        }
        
        /** search array datatype */
        if (de == null)
        {
            de = getArrayDataType(representation);
            if (de != null)
            {
                logger.info("Success: " + representation + " found as ArrayDataType.");
            }
        }
        
        /** search fixedRecord datatype */
        if (de == null)
        {
            de = getFixedRecordDataType(representation);
            if (de != null)
            {
                logger.info("Success: " + representation + " found as FixedRecordDataType.");
            }
        }
        
        /** search variantRecord datatype */
        if (de == null)
        {
            de = getVariantRecordDataType(representation);
            if (de != null)
            {
                logger.info("Success: " + representation + " found as VariantRecordDataType.");
            }
        }
        
        if (de == null)
        {
            logger.error("Final Error: " + representation + " not defined not found.");
        }
        
        return de;
    }
    
    /**
     * Private method that tries to search all enumerated datatypes in all documents
     * known to the decoder
     * 
     * @param representation
     *            the datatype to represent
     * @return a data element that decodes the data
     */
    private DataElement getEnumeratedDataType(String representation)
    {
        return getDataType(representation, "enumeratedDataTypes", "enumeratedData", "representation");
    }
    
    /**
     * Private method that tries to search all simple datatypes in all documents
     * known to the decoder
     * 
     * @param representation
     *            the datatype to represent
     * @return a data element that decodes the data
     */
    private DataElement getSimpleDataType(String representation)
    {
        return getDataType(representation, "simpleDataTypes", "simpleData", "representation");
    }
    
    /**
     * Private method that tries to search all array datatypes in all documents
     * known to the decoder
     * 
     * @param representation
     *            the datatype to represent
     * @return a data element that decodes the data
     */
    private DataElement getArrayDataType(String representation)
    {
        return getDataType(representation, "arrayDataTypes", "arrayData", "dataType");
    }
    
    /**
     * Private method that tries to search all array datatypes in all documents
     * known to the decoder
     * 
     * @param representation
     *            the data type to represent
     * @param tag1
     *            First level tag
     * @param tag2
     *            Second level level tag
     * @param tag3
     *            Third level tag
     * @return a data element that decodes the data
     */
    private DataElement getDataType(final String representation, final String tag1, final String tag2,
            final String tag3)
    {
        DataElement dataElement = null;
        NodeList dataNodes = null;
        for (Document d : docs)
        {
            NodeList dt = d.getElementsByTagName("dataTypes");
            if (dt.getLength() < 1)
            {
                logger.debug(String.join(" ", "No data definitions in file:", d.getDocumentURI()));
            }
            else
            {
                Node TopDt = dt.item(0);
                NodeList adt = ((Element) TopDt).getElementsByTagName(tag1);
                if (adt.getLength() < 1)
                {
                    logger.debug(String.join(" ", "No", tag1, "definitions in file:", d.getDocumentURI()));
                }
                else
                {
                    Node TopAdt = adt.item(0);
                    dataNodes = ((Element) TopAdt).getElementsByTagName(tag2);
                }
            }
            
            if (dataNodes == null || dataNodes.getLength() < 1)
            {
                logger.info(String.join(" ", tag1, "definitions not found in document:", d.getDocumentURI()));
            }
            else
            {
                logger.info(String.join(" ", tag1, "definitions found in file:", d.getDocumentURI()));
                
                SEARCH_LOOP:
                for (int temp = 0; temp < dataNodes.getLength(); temp++)
                {
                    Node node = dataNodes.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element = (Element) node;
                        /** grab the first element, which should be the name */
                        NodeList name = element.getElementsByTagName("name");
                        
                        if (name.getLength() > 0)
                        {
                            /** first and only node should be the name */
                            Node n = name.item(0);
                            String givenName = n.getTextContent();
                            if (givenName.equalsIgnoreCase(representation))
                            {
                                // Found the representation
                                Element repr = (Element) (element.getElementsByTagName(tag3)).item(0);
                                String reprName = repr.getTextContent();
                                dataElement = getDataType(reprName);
                                break SEARCH_LOOP;
                            }
                        }
                        else
                        {
                            logger.error("Incorrect number of name tags");
                        }
                    }
                }
            }
        }
        
        if (dataElement == null)
        {
            logger.debug(String.join(" ", tag1, "with given name not found."));
        }
        return dataElement;
    }
    
    /**
     * Private method that tries to search all fixed record datatypes in all
     * documents known to the decoder
     * 
     * @param representation
     *            the datatype to represent
     * @return a data element that decodes the data
     */
    private DataElement getFixedRecordDataType(String representation)
    {
        // The data element to return
        DataElement theDataElement = null;
        
        DOC_SEARCH_LOOP:
        for (Document d : docs)
        {
            NodeList frData = null;
            
            NodeList dt = d.getElementsByTagName("dataTypes");
            if (dt.getLength() < 1)
            {
                logger.debug("No data definitions in file: " + d.getDocumentURI());
            }
            else
            {
                Node TopDt = dt.item(0);
                NodeList frdt = ((Element) TopDt).getElementsByTagName("fixedRecordDataTypes");
                if (frdt.getLength() < 1)
                {
                    logger.debug("No fixed record data definitions in file: " + d.getDocumentURI());
                }
                else
                {
                    Node TopFrdt = frdt.item(0);
                    frData = ((Element) TopFrdt).getElementsByTagName("fixedRecordData");
                }
            }
            
            if (frData == null || frData.getLength() < 1)
            {
                logger.info("Fixed record definitions not found in document: " + d.getDocumentURI());
            }
            else
            {
                logger.info("Fixed Record data definitions found in file: " + d.getDocumentURI());
                
                for (int temp = 0; temp < frData.getLength(); temp++)
                {
                    Node node = frData.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element = (Element) node;
                        
                        /** grab the first element, which should be the name */
                        NodeList name = element.getElementsByTagName("name");
                        
                        if (name.getLength() > 0)
                        {
                            /** first and only node should be the name */
                            Node n = name.item(0);
                            String givenName = n.getTextContent();
                            if (givenName.equalsIgnoreCase(representation))
                            {
                                NodeList fields = ((Element) node).getElementsByTagName("field");
                                NodeList encoding = ((Element) node).getElementsByTagName("encoding");
                                
                                Element enc = (Element) encoding.item(0);
                                String encName = enc.getTextContent();
                                if (encName.equalsIgnoreCase("HLAfixedRecord"))
                                {
                                    theDataElement = processFRFields(fields);
                                }
                                else
                                {
                                    logger.info("Unknown encoding of Fixed Record");
                                }
                                break DOC_SEARCH_LOOP;
                            }
                        }
                        else
                        {
                            logger.error("Incorrect number of name tags");
                        }
                    }
                }
            }
        }
        
        if (theDataElement == null)
        {
            logger.debug("Fixed record data type with given name not found.");
        }
        
        return theDataElement;
    }
    
    /**
     * Private method that tries to search all variant record datatypes in all
     * documents known to the decoder
     * 
     * @param representation
     *            the datatype to represent
     * @return a data element that decodes the data
     */
    private DataElement getVariantRecordDataType(String representation)
    {
        // The data element to return
        DataElement theDataElement = null;
        
        DOC_SEARCH_LOOP:
        for (Document d : docs)
        {
            NodeList vrData = null;
            NodeList dt = d.getElementsByTagName("dataTypes");
            if (dt.getLength() < 1)
            {
                logger.debug("No data definitions in file: " + d.getDocumentURI());
            }
            else
            {
                Node TopDt = dt.item(0);
                NodeList vrdt = ((Element) TopDt).getElementsByTagName("variantRecordDataTypes");
                if (vrdt.getLength() < 1)
                {
                    logger.debug("No variant record data definitions in file: " + d.getDocumentURI());
                }
                else
                {
                    Node TopVrdt = vrdt.item(0);
                    vrData = ((Element) TopVrdt).getElementsByTagName("variantRecordData");
                }
            }
            
            if (vrData == null || vrData.getLength() < 1)
            {
                logger.debug("Variant record data definitions not found in document: " + d.getDocumentURI());
            }
            else
            {
                logger.info("Variant Record data definitions found in file: " + d.getDocumentURI());
                for (int temp = 0; temp < vrData.getLength(); temp++)
                {
                    Node node = vrData.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element = (Element) node;
                        /** grab the first element, which should be the name */
                        NodeList name = element.getElementsByTagName("name");
                        
                        if (name.getLength() > 0)
                        {
                            /** first and only node should be the name */
                            Node n = name.item(0);
                            String givenName = n.getTextContent();
                            if (givenName.equalsIgnoreCase(representation))
                            {
                                /**
                                 * Grab the discriminant name and datatype.
                                 */
                                NodeList alternatives = ((Element) node).getElementsByTagName("alternative");
                                
                                theDataElement = processVRAttributes(alternatives);
                                
                                break DOC_SEARCH_LOOP;
                            }
                        }
                        else
                        {
                            logger.debug("First child should be 'name' tag");
                        }
                    }
                }
            }
        }
        
        if (theDataElement == null)
        {
            logger.debug("Array data type with given name not found.");
        }
        
        return theDataElement;
    }
    
    /**
     * Processes a node list of alternatives and packages them into a
     * HLAvariantRecord discriminated by HLAoctets
     * 
     * @param alternatives
     * @return
     */
    private HLAvariantRecord<HLAoctet> processVRAttributes(NodeList alternatives)
    {
        int length = alternatives.getLength();
        HLAoctet[] Discriminator = new HLAoctet[length];
        HLAoctet discriminator = this.encoder.createHLAoctet();
        HLAvariantRecord<HLAoctet> vr = this.encoder.createHLAvariantRecord(discriminator);
        
        for (int temp = 0; temp < alternatives.getLength(); temp++)
        {
            Node node = alternatives.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                NodeList repr = element.getElementsByTagName("dataType");
                String representation = repr.item(0).getTextContent();
                
                Discriminator[temp] = this.encoder.createHLAoctet();
                Discriminator[temp].setValue((byte) temp);
                
                vr.setVariant(Discriminator[temp], getDataType(representation));
            }
            else
            {
                logger.error("Attribute not an element, processing may produce unexpected results.");
            }
        }
        return vr;
    }
    
    /**
     * Process a node list of fields and adds them to a HLAfixedrecord.
     * 
     * @param fields
     * @return The fixed record type pertaining to these fields
     */
    private HLAfixedRecord processFRFields(NodeList fields)
    {
        HLAfixedRecord hlaFR = this.encoder.createHLAfixedRecord();
        for (int temp = 0; temp < fields.getLength(); temp++)
        {
            Node node = fields.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                NodeList dataType = element.getElementsByTagName("dataType");
                String dataTypeName = (dataType.item(0)).getTextContent();
                hlaFR.add(getDataType(dataTypeName));
            }
        }
        return hlaFR;
    }
    
    /**
     * A lookup for HLA datatypes that we know about.
     * 
     * @param representation
     * @return The decoder for this representation, or null if one could not be
     *         found
     */
    private DataElement basicDataTypeLookup(String representation)
    {
        DataElement theDataElement;
        
        switch (representation)
        {
            case "HLAfloat32BE":
                theDataElement = encoder.createHLAfloat32BE();
                break;
            
            case "HLAfloat64BE":
                theDataElement = encoder.createHLAfloat64BE();
                break;
            
            case "HLAoctet":
                theDataElement = encoder.createHLAoctet();
                break;
            
            case "HLAASCIIchar":
                theDataElement = encoder.createHLAASCIIchar();
                break;
            
            case "RTIobjectId":
                theDataElement = new RTIobjectIdDecoder();
                break;
            
            case "RPRunsignedInteger16BE":
                theDataElement = encoder.createHLAinteger16BE();
                break;
            
            case "RPRunsignedInteger32BE":
                theDataElement = encoder.createHLAinteger32BE();
                break;
            
            default:
                logger.error("Basic Data Error: failed to find representation for " + representation);
                theDataElement = null;
                break;
        }
        
        return theDataElement;
    }
}
