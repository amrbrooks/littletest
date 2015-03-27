/**
 * Result.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.boco.eoms.sheet.complaint.service.pointservice;

public class Result  implements java.io.Serializable {
    private java.lang.String isSuccess;

    private java.lang.String errorInfo;

    private java.lang.String attachRef;

    public Result() {
    }

    public Result(
           java.lang.String isSuccess,
           java.lang.String errorInfo,
           java.lang.String attachRef) {
           this.isSuccess = isSuccess;
           this.errorInfo = errorInfo;
           this.attachRef = attachRef;
    }


    /**
     * Gets the isSuccess value for this Result.
     * 
     * @return isSuccess
     */
    public java.lang.String getIsSuccess() {
        return isSuccess;
    }


    /**
     * Sets the isSuccess value for this Result.
     * 
     * @param isSuccess
     */
    public void setIsSuccess(java.lang.String isSuccess) {
        this.isSuccess = isSuccess;
    }


    /**
     * Gets the errorInfo value for this Result.
     * 
     * @return errorInfo
     */
    public java.lang.String getErrorInfo() {
        return errorInfo;
    }


    /**
     * Sets the errorInfo value for this Result.
     * 
     * @param errorInfo
     */
    public void setErrorInfo(java.lang.String errorInfo) {
        this.errorInfo = errorInfo;
    }


    /**
     * Gets the attachRef value for this Result.
     * 
     * @return attachRef
     */
    public java.lang.String getAttachRef() {
        return attachRef;
    }


    /**
     * Sets the attachRef value for this Result.
     * 
     * @param attachRef
     */
    public void setAttachRef(java.lang.String attachRef) {
        this.attachRef = attachRef;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Result)) return false;
        Result other = (Result) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.isSuccess==null && other.getIsSuccess()==null) || 
             (this.isSuccess!=null &&
              this.isSuccess.equals(other.getIsSuccess()))) &&
            ((this.errorInfo==null && other.getErrorInfo()==null) || 
             (this.errorInfo!=null &&
              this.errorInfo.equals(other.getErrorInfo()))) &&
            ((this.attachRef==null && other.getAttachRef()==null) || 
             (this.attachRef!=null &&
              this.attachRef.equals(other.getAttachRef())));
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
        if (getIsSuccess() != null) {
            _hashCode += getIsSuccess().hashCode();
        }
        if (getErrorInfo() != null) {
            _hashCode += getErrorInfo().hashCode();
        }
        if (getAttachRef() != null) {
            _hashCode += getAttachRef().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Result.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "Result"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isSuccess");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "isSuccess"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "errorInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attachRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "attachRef"));
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
