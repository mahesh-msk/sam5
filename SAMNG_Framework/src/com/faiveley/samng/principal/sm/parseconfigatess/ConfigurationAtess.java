//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.22 at 10:26:01 AM CET 
//


package com.faiveley.samng.principal.sm.parseconfigatess;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}fichier-description"/>
 *         &lt;element ref="{}liste-identifiants-etendus"/>
 *         &lt;element ref="{}liste-maximum-compteur"/>
 *         &lt;element ref="{}table-evenements-variables"/>
 *         &lt;element ref="{}signature"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fichierDescription",
    "listeIdentifiantsEtendus",
    "listeMaximumCompteur",
    "tableEvenementsVariables",
    "signature"
})
@XmlRootElement(name = "configuration-atess")
public class ConfigurationAtess {

    @XmlElement(name = "fichier-description", required = true)
    protected FichierDescription fichierDescription;
    @XmlElement(name = "liste-identifiants-etendus", required = true)
    protected ListeIdentifiantsEtendus listeIdentifiantsEtendus;
    @XmlElement(name = "liste-maximum-compteur", required = true)
    protected ListeMaximumCompteur listeMaximumCompteur;
    @XmlElement(name = "table-evenements-variables", required = true)
    protected TableEvenementsVariables tableEvenementsVariables;
    @XmlElement(required = true)
    protected Signature signature;

    /**
     * Gets the value of the fichierDescription property.
     * 
     * @return
     *     possible object is
     *     {@link FichierDescription }
     *     
     */
    public FichierDescription getFichierDescription() {
        return fichierDescription;
    }

    /**
     * Sets the value of the fichierDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link FichierDescription }
     *     
     */
    public void setFichierDescription(FichierDescription value) {
        this.fichierDescription = value;
    }

    /**
     * Gets the value of the listeIdentifiantsEtendus property.
     * 
     * @return
     *     possible object is
     *     {@link ListeIdentifiantsEtendus }
     *     
     */
    public ListeIdentifiantsEtendus getListeIdentifiantsEtendus() {
        return listeIdentifiantsEtendus;
    }

    /**
     * Sets the value of the listeIdentifiantsEtendus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListeIdentifiantsEtendus }
     *     
     */
    public void setListeIdentifiantsEtendus(ListeIdentifiantsEtendus value) {
        this.listeIdentifiantsEtendus = value;
    }

    /**
     * Gets the value of the listeMaximumCompteur property.
     * 
     * @return
     *     possible object is
     *     {@link ListeMaximumCompteur }
     *     
     */
    public ListeMaximumCompteur getListeMaximumCompteur() {
        return listeMaximumCompteur;
    }

    /**
     * Sets the value of the listeMaximumCompteur property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListeMaximumCompteur }
     *     
     */
    public void setListeMaximumCompteur(ListeMaximumCompteur value) {
        this.listeMaximumCompteur = value;
    }

    /**
     * Gets the value of the tableEvenementsVariables property.
     * 
     * @return
     *     possible object is
     *     {@link TableEvenementsVariables }
     *     
     */
    public TableEvenementsVariables getTableEvenementsVariables() {
        return tableEvenementsVariables;
    }

    /**
     * Sets the value of the tableEvenementsVariables property.
     * 
     * @param value
     *     allowed object is
     *     {@link TableEvenementsVariables }
     *     
     */
    public void setTableEvenementsVariables(TableEvenementsVariables value) {
        this.tableEvenementsVariables = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link Signature }
     *     
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link Signature }
     *     
     */
    public void setSignature(Signature value) {
        this.signature = value;
    }

}
