//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.06 at 03:12:18 PM CET 
//


package com.faiveley.samng.principal.sm.missions.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeListeRegroupementTemps complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeListeRegroupementTemps">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RegroupementTemps" type="{}typeRegroupementTemps" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeListeRegroupementTemps", propOrder = {
    "regroupementTemps"
})
public class TypeListeRegroupementTemps {

    @XmlElement(name = "RegroupementTemps", required = true)
    protected List<TypeRegroupementTemps> regroupementTemps;

    /**
     * Gets the value of the regroupementTemps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regroupementTemps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegroupementTemps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeRegroupementTemps }
     * 
     * 
     */
    public List<TypeRegroupementTemps> getRegroupementTemps() {
        if (regroupementTemps == null) {
            regroupementTemps = new ArrayList<TypeRegroupementTemps>();
        }
        return this.regroupementTemps;
    }

}
