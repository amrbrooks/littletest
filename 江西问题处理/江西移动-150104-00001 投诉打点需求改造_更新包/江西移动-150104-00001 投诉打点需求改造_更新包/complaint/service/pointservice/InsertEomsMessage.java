/**
 * InsertEomsMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.boco.eoms.sheet.complaint.service.pointservice;

public class InsertEomsMessage  implements java.io.Serializable {
    private java.lang.String lac;

    private java.lang.String ci;

    private java.util.Calendar complain_time;

    private double longitude;

    private double latitude;

    private java.lang.String customer_phone_number;

    private java.lang.String complain_serial_no;

    private java.lang.String related_cell;

    public InsertEomsMessage() {
    }

    public InsertEomsMessage(
           java.lang.String lac,
           java.lang.String ci,
           java.util.Calendar complain_time,
           double longitude,
           double latitude,
           java.lang.String customer_phone_number,
           java.lang.String complain_serial_no,
           java.lang.String related_cell) {
           this.lac = lac;
           this.ci = ci;
           this.complain_time = complain_time;
           this.longitude = longitude;
           this.latitude = latitude;
           this.customer_phone_number = customer_phone_number;
           this.complain_serial_no = complain_serial_no;
           this.related_cell = related_cell;
    }


    /**
     * Gets the lac value for this InsertEomsMessage.
     * 
     * @return lac
     */
    public java.lang.String getLac() {
        return lac;
    }


    /**
     * Sets the lac value for this InsertEomsMessage.
     * 
     * @param lac
     */
    public void setLac(java.lang.String lac) {
        this.lac = lac;
    }


    /**
     * Gets the ci value for this InsertEomsMessage.
     * 
     * @return ci
     */
    public java.lang.String getCi() {
        return ci;
    }


    /**
     * Sets the ci value for this InsertEomsMessage.
     * 
     * @param ci
     */
    public void setCi(java.lang.String ci) {
        this.ci = ci;
    }


    /**
     * Gets the complain_time value for this InsertEomsMessage.
     * 
     * @return complain_time
     */
    public java.util.Calendar getComplain_time() {
        return complain_time;
    }


    /**
     * Sets the complain_time value for this InsertEomsMessage.
     * 
     * @param complain_time
     */
    public void setComplain_time(java.util.Calendar complain_time) {
        this.complain_time = complain_time;
    }


    /**
     * Gets the longitude value for this InsertEomsMessage.
     * 
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }


    /**
     * Sets the longitude value for this InsertEomsMessage.
     * 
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    /**
     * Gets the latitude value for this InsertEomsMessage.
     * 
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }


    /**
     * Sets the latitude value for this InsertEomsMessage.
     * 
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    /**
     * Gets the customer_phone_number value for this InsertEomsMessage.
     * 
     * @return customer_phone_number
     */
    public java.lang.String getCustomer_phone_number() {
        return customer_phone_number;
    }


    /**
     * Sets the customer_phone_number value for this InsertEomsMessage.
     * 
     * @param customer_phone_number
     */
    public void setCustomer_phone_number(java.lang.String customer_phone_number) {
        this.customer_phone_number = customer_phone_number;
    }


    /**
     * Gets the complain_serial_no value for this InsertEomsMessage.
     * 
     * @return complain_serial_no
     */
    public java.lang.String getComplain_serial_no() {
        return complain_serial_no;
    }


    /**
     * Sets the complain_serial_no value for this InsertEomsMessage.
     * 
     * @param complain_serial_no
     */
    public void setComplain_serial_no(java.lang.String complain_serial_no) {
        this.complain_serial_no = complain_serial_no;
    }


    /**
     * Gets the related_cell value for this InsertEomsMessage.
     * 
     * @return related_cell
     */
    public java.lang.String getRelated_cell() {
        return related_cell;
    }


    /**
     * Sets the related_cell value for this InsertEomsMessage.
     * 
     * @param related_cell
     */
    public void setRelated_cell(java.lang.String related_cell) {
        this.related_cell = related_cell;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InsertEomsMessage)) return false;
        InsertEomsMessage other = (InsertEomsMessage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.lac==null && other.getLac()==null) || 
             (this.lac!=null &&
              this.lac.equals(other.getLac()))) &&
            ((this.ci==null && other.getCi()==null) || 
             (this.ci!=null &&
              this.ci.equals(other.getCi()))) &&
            ((this.complain_time==null && other.getComplain_time()==null) || 
             (this.complain_time!=null &&
              this.complain_time.equals(other.getComplain_time()))) &&
            this.longitude == other.getLongitude() &&
            this.latitude == other.getLatitude() &&
            ((this.customer_phone_number==null && other.getCustomer_phone_number()==null) || 
             (this.customer_phone_number!=null &&
              this.customer_phone_number.equals(other.getCustomer_phone_number()))) &&
            ((this.complain_serial_no==null && other.getComplain_serial_no()==null) || 
             (this.complain_serial_no!=null &&
              this.complain_serial_no.equals(other.getComplain_serial_no()))) &&
            ((this.related_cell==null && other.getRelated_cell()==null) || 
             (this.related_cell!=null &&
              this.related_cell.equals(other.getRelated_cell())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getLac() != null) {
            _hashCode += getLac().hashCode();
        }
        if (getCi() != null) {
            _hashCode += getCi().hashCode();
        }
        if (getComplain_time() != null) {
            _hashCode += getComplain_time().hashCode();
        }
        _hashCode += new Double(getLongitude()).hashCode();
        _hashCode += new Double(getLatitude()).hashCode();
        if (getCustomer_phone_number() != null) {
            _hashCode += getCustomer_phone_number().hashCode();
        }
        if (getComplain_serial_no() != null) {
            _hashCode += getComplain_serial_no().hashCode();
        }
        if (getRelated_cell() != null) {
            _hashCode += getRelated_cell().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InsertEomsMessage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "InsertEomsMessage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lac");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "lac"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ci");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "ci"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("complain_time");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "complain_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "longitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "latitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_phone_number");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "customer_phone_number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("complain_serial_no");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "complain_serial_no"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("related_cell");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "related_cell"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
