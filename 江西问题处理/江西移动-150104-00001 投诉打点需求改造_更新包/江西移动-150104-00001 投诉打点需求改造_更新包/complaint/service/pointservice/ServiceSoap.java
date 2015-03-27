/**
 * ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.boco.eoms.sheet.complaint.service.pointservice;

public interface ServiceSoap extends java.rmi.Remote {
    public  Result sendEomseComp1( InsertEomsMessage message) throws java.rmi.RemoteException;
    public  Result sendEomseComp2( BackEomsMessage message) throws java.rmi.RemoteException;
}
