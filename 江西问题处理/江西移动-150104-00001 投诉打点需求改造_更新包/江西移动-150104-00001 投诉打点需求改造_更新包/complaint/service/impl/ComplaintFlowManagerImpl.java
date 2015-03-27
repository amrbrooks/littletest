/*
 * Created on 2008-4-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.boco.eoms.sheet.complaint.service.impl;

import java.security.PrivilegedAction;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.boco.eoms.base.util.ApplicationContextHolder;
import com.boco.eoms.base.util.StaticMethod;
import com.boco.eoms.commons.loging.BocoLog;
import com.boco.eoms.commons.util.xml.XmlManage;
import com.boco.eoms.sheet.base.adapter.service.wps.WPSEngineServiceMethod;
import com.boco.eoms.sheet.base.adapter.service.wps.WPSSecutiryServiceImpl;
import com.boco.eoms.sheet.base.adapter.service.wps.WPSStaticMethod;
import com.boco.eoms.sheet.base.service.ILinkService;
import com.boco.eoms.sheet.base.service.impl.BusinessFlowServiceImpl;
import com.boco.eoms.sheet.complaint.model.ComplaintLink;
import com.boco.eoms.sheet.complaint.service.IComplaintFlowManager;
import com.boco.eoms.sheet.complaint.service.pointservice.BackEomsMessage;
import com.boco.eoms.sheet.complaint.service.pointservice.Result;
import com.boco.eoms.sheet.complaint.service.pointservice.ServiceLocator;
import com.boco.eoms.sheet.complaint.service.pointservice.ServiceSoapStub;
import com.boco.eoms.util.InterfaceUtil;
import com.ibm.bpe.api.BusinessFlowManagerService;
import com.ibm.bpe.api.ClientObjectWrapper;
import com.ibm.websphere.security.auth.WSSubject;
import commonj.sdo.DataObject;

/**
 * @author panlong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ComplaintFlowManagerImpl extends BusinessFlowServiceImpl implements
        IComplaintFlowManager {
	
	 public void clearControlPreExcutePerformer(final String piid)
		throws Exception
	{
		String userId = StaticMethod.nullObject2String("admin");
		String password = StaticMethod.nullObject2String("admin");
		WPSSecutiryServiceImpl safeService = new WPSSecutiryServiceImpl();
		javax.security.auth.Subject subject = safeService.logIn(userId, password);
		PrivilegedAction getProcessStarted = new PrivilegedAction() {

			public Object run()
			{
				try
				{
					BusinessFlowManagerService businessFlowManagerService = (BusinessFlowManagerService)ApplicationContextHolder.getInstance().getBean("WorkflowManager");
					ClientObjectWrapper clientObjectWrapper = null;
					HashMap objectMap = new HashMap();
					try
					{
						clientObjectWrapper = businessFlowManagerService.getVariable(piid, "control");
						if (clientObjectWrapper.getObject() != null && (clientObjectWrapper.getObject() instanceof DataObject))
						{
							DataObject variable = (DataObject)clientObjectWrapper.getObject();
							objectMap = WPSStaticMethod.createHashMap(variable);
							String firstExcutePerformer = StaticMethod.nullObject2String(objectMap.get("firstExcutePerformer"));
							BocoLog.info(this, "==ComplaintFlowManagerImpl.clearControlPreExcutePerformer==firstExcutePerformer====" + firstExcutePerformer);
							objectMap.put("preExcutePerformer", firstExcutePerformer);
							WPSStaticMethod.createDataObject(variable, objectMap);
							ClientObjectWrapper newClient = new ClientObjectWrapper(variable);
							businessFlowManagerService.setVariable(piid, "control", newClient);
						} else
						{
							objectMap.put("control", clientObjectWrapper.getObject());
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}

		};
		Integer response = (Integer)WSSubject.doAs(subject, getProcessStarted);
	}
	 
	 /**
	  * 第二次EOMS与网络支撑客服系统接口交互，为工单处理回复完成之后，流转到质检环节时，自动调用此接口并返回相应的值，附件为异步上传到工单上。
	  * add by weichao 20150204
	  */
	 public String completeHumanTask(String activityId, HashMap parameters,HashMap sessionMap) throws Exception{
		String rs = "";
	
		HashMap map = (HashMap)parameters.get("main");
		HashMap linkMap = (HashMap)parameters.get("link");
		String parentCorrelation = StaticMethod.nullObject2String(map.get("parentCorrelation"));
		String operateType = StaticMethod.nullObject2String(linkMap.get("operateType"));
		
		rs = super.completeHumanTask(activityId, parameters, sessionMap);
		//判断如果是接口派单并且在回复的时候 调用客服系统接口
		if((null!=parentCorrelation&&!"".equals(parentCorrelation)) && operateType.equals("46")){
			BocoLog.info(this, "=====回复成功，调用客服接口开始====");
			String m5 = StaticMethod.nullObject2String(map.get("complaintType5"));
			BocoLog.info(this, "投诉五级分类===="+m5);
			String i5 = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-util.xml").getProperty("pointService.new"));
			if(i5.contains(m5)){//符合条件则调 网络支撑客服系统			
				try {
					ServiceSoapStub binding;
			        try {
			            binding = (ServiceSoapStub)new ServiceLocator().getServiceSoap();
			        }
			        catch (javax.xml.rpc.ServiceException jre) {
			            if(jre.getLinkedCause()!=null)
			                jre.getLinkedCause().printStackTrace();
			            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
			        }
			        //Time out after a minute
			        int timeOut = StaticMethod.null2int(XmlManage.getFile("/config/complaint-util.xml").getProperty("pointService.wsdlTimeOut"));
			        binding.setTimeout(timeOut);
			        //准备调接口的数据
			        BackEomsMessage em = new BackEomsMessage();
			        String mainLAC = StaticMethod.nullObject2String(map.get("mainLAC"));
			        BocoLog.info(this, "mainLAC===="+mainLAC);
			        em.setLac(mainLAC);
			        String ci = StaticMethod.nullObject2String(map.get("mainCI"));
			        BocoLog.info(this, "mainCI===="+ci);
			        em.setCi(ci);
			        double latitude = Double.valueOf(StaticMethod.nullObject2String(map.get("mainLatitude"))).doubleValue();
			        BocoLog.info(this, "latitude===="+latitude);
			        em.setLatitude(latitude);
			        double longitude = Double.valueOf(StaticMethod.nullObject2String(map.get("mainLongitude"))).doubleValue();
			        BocoLog.info(this, "longitude===="+longitude);
			        em.setLongitude(longitude);
			        Date dd = (Date)(linkMap.get("operateTime"));
			        Calendar cal=Calendar.getInstance();
			        cal.setTime(dd);
			        em.setLast_time(cal);
			        BocoLog.info(this, "Last_time===="+cal.getTimeInMillis());
			        
			        String phonenum = StaticMethod.nullObject2String(map.get("complaintNum"));
			        BocoLog.info(this, "Customer_phone_number===="+phonenum);
			        em.setCustomer_phone_number(phonenum);
			        String mainCommunity = StaticMethod.nullObject2String(map.get("mainCommunity"));
			        BocoLog.info(this, "mainCommunity===="+mainCommunity);
			        em.setRelated_cell(mainCommunity);
			        String sheetId = map.get("sheetId").toString();
			        BocoLog.info(this, "sheetId===="+sheetId);
			        em.setComplain_serial_no(sheetId);
			        Result value = null;
			        value = binding.sendEomseComp2(em);//调接口
			        if(null!=value){
			        	BocoLog.info(this, "接口返回===IsSuccess===="+value.getIsSuccess());
			        	BocoLog.info(this, "接口返回===ErrorInfo===="+value.getErrorInfo());
			        	BocoLog.info(this, "接口返回===AttachRef===="+value.getAttachRef());
			        	if(null!=value.getIsSuccess()&&"1".equals(value.getIsSuccess())){//返回成功
			        		BocoLog.info(this, "===success and now explaining====");
			        		InterfaceUtil interfaceUtil = new InterfaceUtil();
					        List attachs = interfaceUtil.getAttachRefFromXml(value.getAttachRef());//解析xml文件
					        String attachId = this.getAttach(attachs);//下载附件
					        BocoLog.info(this, "===success and now explain over===="+attachId);
					        String aliveAttach = StaticMethod.nullObject2String(linkMap.get("nodeAccessories"));
					        //将对端返回的附件保存到link信息中
					        ILinkService iLinkService = (ILinkService)ApplicationContextHolder.getInstance().getBean("iComplaintLinkManager");     
					        String id = StaticMethod.nullObject2String(linkMap.get("id"));
					        ComplaintLink linkObject = (ComplaintLink)iLinkService.getSingleLinkPO(id);
					        if(null!=aliveAttach&&!"".equals(aliveAttach)){
					        	String att = aliveAttach+","+attachId;
					        	linkObject.setNodeAccessories(att);
					        }else{
					        	linkObject.setNodeAccessories(attachId);
					        }
					        iLinkService.addLink(linkObject);
					        BocoLog.info(this, "===saving ok====");
			        	}
			        }else{
			        	BocoLog.info(this, "接口返回错误===="+value);
			        }
			       
				} catch (Exception e) {
					System.out.println("网络支撑客服系统接口异常");
					e.printStackTrace();				
				}
			}
		}
		
		return rs;
		
	 }

	 public String getAttach(List attachList){
		WPSEngineServiceMethod wm = new WPSEngineServiceMethod();
		return wm.getAttach(attachList, "complaint");
	 }
}
