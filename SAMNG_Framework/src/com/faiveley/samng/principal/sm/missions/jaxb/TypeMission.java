//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.06 at 03:12:18 PM CET 
//


package com.faiveley.samng.principal.sm.missions.jaxb;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for typeMission complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeMission">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DateDebut" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DateFin" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="OffsetDebut" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="OffsetFin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="IdMessageDebut" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ListeSegment" type="{}typeListeSegment" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="numero" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeMission", propOrder = {
    "dateDebut",
    "dateFin",
    "offsetDebut",
    "offsetFin",
    "idMessageDebut",
    "listeSegment"
})
public class TypeMission {

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateDebut == null) ? 0 : dateDebut.hashCode());
		result = prime * result + ((dateFin == null) ? 0 : dateFin.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((idMessageDebut == null) ? 0 : idMessageDebut.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + (int) (offsetDebut ^ (offsetDebut >>> 32));
		result = prime * result + (int) (offsetFin ^ (offsetFin >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeMission other = (TypeMission) obj;
		if (dateDebut == null) {
			if (other.dateDebut != null)
				return false;
		} else if (!dateDebut.equals(other.dateDebut))
			return false;
		if (dateFin == null) {
			if (other.dateFin != null)
				return false;
		} else if (!dateFin.equals(other.dateFin))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idMessageDebut == null) {
			if (other.idMessageDebut != null)
				return false;
		} else if (!idMessageDebut.equals(other.idMessageDebut))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (offsetDebut != other.offsetDebut)
			return false;
		if (offsetFin != other.offsetFin)
			return false;
		return true;
	}

	@XmlElement(name = "DateDebut", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateDebut;
    @XmlElement(name = "DateFin", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateFin;
    @XmlElement(name = "OffsetDebut")
    protected long offsetDebut;
    @XmlElement(name = "OffsetFin")
    protected long offsetFin;
    @XmlElement(name = "IdMessageDebut", required = true)
    protected BigInteger idMessageDebut;
    @XmlElement(name = "ListeSegment")
    protected TypeListeSegment listeSegment;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(required = true)
    protected BigInteger numero;

    /**
     * Gets the value of the dateDebut property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateDebut() {
        return dateDebut;
    }

    /**
     * Sets the value of the dateDebut property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateDebut(XMLGregorianCalendar value) {
        this.dateDebut = value;
    }

    /**
     * Gets the value of the dateFin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateFin() {
        return dateFin;
    }

    /**
     * Sets the value of the dateFin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateFin(XMLGregorianCalendar value) {
        this.dateFin = value;
    }

    /**
     * Gets the value of the offsetDebut property.
     * 
     */
    public long getOffsetDebut() {
        return offsetDebut;
    }

    /**
     * Sets the value of the offsetDebut property.
     * 
     */
    public void setOffsetDebut(long value) {
        this.offsetDebut = value;
    }

    /**
     * Gets the value of the offsetFin property.
     * 
     */
    public long getOffsetFin() {
        return offsetFin;
    }

    /**
     * Sets the value of the offsetFin property.
     * 
     */
    public void setOffsetFin(long value) {
        this.offsetFin = value;
    }

    /**
	 * @return le idMessageDebut
	 */
	public BigInteger getIdMessageDebut() {
		return idMessageDebut;
	}

	/**
	 * @param idMessageDebut le idMessageDebut � d�finir
	 */
	public void setIdMessageDebut(BigInteger idMessageDebut) {
		this.idMessageDebut = idMessageDebut;
	}

	/**
     * Gets the value of the listeSegment property.
     * 
     * @return
     *     possible object is
     *     {@link TypeListeSegment }
     *     
     */
    public TypeListeSegment getListeSegment() {
        if (listeSegment == null) {
        	listeSegment = new TypeListeSegment();
        }
       return listeSegment;
    }

    /**
     * Sets the value of the listeSegment property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeListeSegment }
     *     
     */
    public void setListeSegment(TypeListeSegment value) {
        this.listeSegment = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the numero property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumero() {
        return numero;
    }

    /**
     * Sets the value of the numero property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumero(BigInteger value) {
        this.numero = value;
    }

}
