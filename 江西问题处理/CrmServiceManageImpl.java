// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CrmServiceManageImpl.java

package com.boco.eoms.sheet.complaint.service.bo;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.boco.eoms.base.util.ApplicationContextHolder;
import com.boco.eoms.base.util.StaticMethod;
import com.boco.eoms.base.util.UUIDHexGenerator;
import com.boco.eoms.commons.loging.BocoLog;
import com.boco.eoms.commons.system.area.model.TawSystemArea;
import com.boco.eoms.commons.system.area.service.ITawSystemAreaManager;
import com.boco.eoms.commons.system.dict.model.TawSystemDictType;
import com.boco.eoms.commons.system.dict.service.ID2NameService;
import com.boco.eoms.commons.system.dict.service.ITawSystemDictTypeManager;
import com.boco.eoms.commons.system.role.model.TawSystemSubRole;
import com.boco.eoms.commons.system.user.model.TawSystemUser;
import com.boco.eoms.commons.system.user.service.ITawSystemUserManager;
import com.boco.eoms.commons.util.xml.XmlManage;
import com.boco.eoms.fullcomplaint.client.WS_FullComplaintsLocator;
import com.boco.eoms.fullcomplaint.client.WS_FullComplaintsSoapStub;
import com.boco.eoms.sheet.base.adapter.service.wps.WPSEngineServiceMethod;
import com.boco.eoms.sheet.base.model.BaseMain;
import com.boco.eoms.sheet.base.service.ILinkService;
import com.boco.eoms.sheet.base.service.IMainService;
import com.boco.eoms.sheet.base.service.ITaskService;
import com.boco.eoms.sheet.base.task.ITask;
import com.boco.eoms.sheet.base.util.Constants;
import com.boco.eoms.sheet.base.util.SheetBeanUtils;
import com.boco.eoms.sheet.base.util.SheetUtils;
import com.boco.eoms.sheet.complaint.model.ComplaintInfo;
import com.boco.eoms.sheet.complaint.model.ComplaintLink;
import com.boco.eoms.sheet.complaint.model.ComplaintMain;
import com.boco.eoms.sheet.complaint.service.IComplaintFlowManager;
import com.boco.eoms.sheet.complaint.service.IComplaintInfoManager;
import com.boco.eoms.sheet.complaint.service.pointservice.InsertEomsMessage;
import com.boco.eoms.sheet.complaint.service.pointservice.Result;
import com.boco.eoms.sheet.complaint.service.pointservice.ServiceLocator;
import com.boco.eoms.sheet.complaint.service.pointservice.ServiceSoapStub;
import com.boco.eoms.sheet.complaint.util.ComplaintConstants;
import com.boco.eoms.sheet.complaint.util.ComplaintUtils;
import com.boco.eoms.sheet.interfaceBase.service.IInterfaceServiceManage;
import com.boco.eoms.sheet.interfaceBase.service.impl.InterfaceServiceManageAbstract;
import com.boco.eoms.sheet.interfaceBase.util.InterfaceUtilProperties;
import com.boco.eoms.util.InterfaceUtil;

public class CrmServiceManageImpl extends InterfaceServiceManageAbstract
	implements IInterfaceServiceManage
{

	public CrmServiceManageImpl()
	{
	}

	public String getLinkBeanId()
	{
		return "iComplaintLinkManager";
	}

	public String getMainBeanId()
	{
		return "iComplaintMainManager";
	}

	public String getOperateName()
	{
		return "newWorksheet";
	}

	public String getProcessTemplateName()
	{
		return "ComplaintProcess";
	}

	public String getSendUser(Map map)
	{
		String userId = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.InterfaceUser"));
		return userId;
	}

	public String newWorkSheet(HashMap interfaceMap, List attach)
	throws Exception
	{
		HashMap columnMap = new HashMap();
		columnMap.put("selfSheet", setNewInterfacePara());
		String districtCounty = StaticMethod.nullObject2String(interfaceMap.get("districtCounty"));
		Map map = initMap(interfaceMap, attach, "newWorkSheet");
		map.put("distriCtcounty", districtCounty);
		map = setBaseMap(map);
		System.out.println("setBaseMap complete");
		map.put("correlationKey", new String[] {
			UUIDHexGenerator.getInstance().getID()
		});
		WPSEngineServiceMethod sm = new WPSEngineServiceMethod();
		String processTemplateName = getProcessTemplateName();
		String operateName = getOperateName();
		String userId = getSendUser(map);
		if (userId == null || userId.equals(""))
			throw new Exception("userId is null");
		System.out.println("userId=" + userId);
		map.put("sendUserId", userId);
		map.put("operateUserId", userId);
		map.put("operateRoleId", map.get("sendRoleId"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		map.put("sendTime", new String[] {
			dateFormat.format(date)
		});
		ITawSystemUserManager userMgr = (ITawSystemUserManager)ApplicationContextHolder.getInstance().getBean("itawSystemUserManager");
		TawSystemUser user = userMgr.getUserByuserid(userId);
		if (user != null)
		{
			map.put("sendDeptId", user.getDeptid());
			map.put("sendContact", user.getMobile());
			map.put("operateDeptId", user.getDeptid());
			map.put("operaterContact", user.getMobile());
		}

		System.out.println("prepareMap start");
		HashMap WpsMap = sm.prepareMap(map, columnMap);
		System.out.println("start addpara");
		WpsMap = addPara(WpsMap);
		if (WpsMap.get("corrKey") != null)
			System.out.println("add corrKey:" + WpsMap.get("corrKey").toString());
		Map mainMap = sm.sendNewSheet(WpsMap, userId, processTemplateName, operateName);
		System.out.println("sendNewSheet over");
		BaseMain main = new BaseMain();
		SheetBeanUtils.populate(main, mainMap);
		finishNew(main, interfaceMap);
		
		ComplaintMain complaintMain = new ComplaintMain();
		SheetBeanUtils.populate(complaintMain, mainMap);
		//投诉全量需求调用客服支撑系统投诉单实时接口  --add by liukun -2011-10-17
		try{
			System.out.println("开始调用客服支撑系统eoms投诉单实时接口");
			boolean result = this.complaintsSingle(complaintMain, new ComplaintLink(), ComplaintConstants.NEW_WORKSHEET);
			System.out.println("eoms投诉单实时接口调用结果：result=" + result);
		}catch(Exception err){
			System.out.println("eoms投诉单实时接口调用失败");
			err.printStackTrace();
		}
		
//第一次EOMS与网络支撑客服系统接口交互，在EOMS接收到CRM系的统接口派单后，新增投诉工单之后自动调用此接口  add by weichao 20150203 begin--
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
		        InsertEomsMessage em = new InsertEomsMessage();
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
		        Date dd = SheetUtils.stringToDate(StaticMethod.nullObject2String(map.get("complaintTime")));
		        Calendar cal=Calendar.getInstance();
		        cal.setTime(dd);
		        em.setComplain_time(cal);
		        BocoLog.info(this, "complain_time===="+StaticMethod.nullObject2String(map.get("complaintTime")));
		        
		        String phonenum = StaticMethod.nullObject2String(map.get("complaintNum"));
		        BocoLog.info(this, "Customer_phone_number===="+phonenum);
		        em.setCustomer_phone_number(phonenum);
		        String mainCommunity = StaticMethod.nullObject2String(map.get("mainCommunity"));
		        BocoLog.info(this, "mainCommunity===="+mainCommunity);
		        em.setRelated_cell(mainCommunity);
		        String sheetId = mainMap.get("sheetId").toString();
		        BocoLog.info(this, "sheetId===="+sheetId);
		        em.setComplain_serial_no(sheetId);
		        Result value = null;
		        value = binding.sendEomseComp1(em);//调接口
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
				        String aliveAttach = StaticMethod.nullObject2String(map.get("sheetAccessories"));
				        //将对端返回的附件保存到main信息中
				        IMainService iMainService = (IMainService)ApplicationContextHolder.getInstance().getBean(getMainBeanId());
				        ComplaintMain mainObject = (ComplaintMain)iMainService.getMainBySheetId(sheetId);
				        if(null!=aliveAttach&&!"".equals(aliveAttach)){
				        	String att = aliveAttach+","+attachId;
				        	mainObject.setSheetAccessories(att);
				        }else{
				        	mainObject.setSheetAccessories(attachId);
				        }
				        iMainService.saveOrUpdateMain(mainObject);
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
//add by weichao 20150203  --end		
		return mainMap.get("sheetId").toString();
	}
	
	public String checkinWorkSheet(HashMap interfaceMap, List attach)
		throws Exception
	{
		Map map = initMap(interfaceMap, attach, "checkinWorkSheet");
		if (map.get("phaseId") == null)
			throw new Exception("phaseId为空");
		IMainService iMainService = (IMainService)ApplicationContextHolder.getInstance().getBean(getMainBeanId());
		ILinkService iLinkService = (ILinkService)ApplicationContextHolder.getInstance().getBean(this.getLinkBeanId());
		map = setBaseMap(map);
		String sheetNo = StaticMethod.nullObject2String(map.get("serialNo"));
		System.out.println("serialNo=" + sheetNo);
		List list = iMainService.getMainListByParentSheetId(sheetNo);
		if (list.size() > 0)
		{
			BaseMain main = (BaseMain)list.get(0);
			map.put("id", main.getId());
			map.put("operateUserId", main.getSendUserId());
			map.put("endUserId", main.getSendUserId());
			map.put("endDeptId", main.getSendDeptId());
			map.put("endRoleId", main.getSendRoleId());
			map.put("status", Constants.SHEET_HOLD);
			ComplaintMain complaintMain = (ComplaintMain)list.get(0);
			
			//投诉全量投诉单实时接口 --begin add by liukun 2011-10-18
			ComplaintLink complaintLink = null;
			List linkLists = iLinkService.getLinksBycondition(" link.mainId='" + main.getId() + "' and link.operateType=46 order by link.operateTime desc", "ComplaintLink link");
			if(linkLists != null && linkLists.size() != 0){
				complaintLink = (ComplaintLink)linkLists.get(0);
			}else{
				complaintLink = new ComplaintLink();
			}
			try{
				System.out.println("开始调用客服支撑系统eoms投诉单实时接口");
				boolean result = this.complaintsSingle(complaintMain, complaintLink, ComplaintConstants.HOLD_WORKSHEET);
				System.out.println("eoms投诉单实时接口调用结果：result=" + result);
			}catch(Exception err){
				System.out.println("eoms投诉单实时接口调用失败");
				err.printStackTrace();
			}
			//投诉全量投诉单实时接口  ---end
			return dealSheet(main, map, attach);
		} else
		{
			throw new Exception("没找到sheetNo＝" + sheetNo + "对应的工单");
		}
	}
	
	public Map initMap(Map map, List attach, String type)
		throws Exception
	{
		String districtCounty = StaticMethod.nullObject2String(map.get("districtCounty"));
		String faultSite = StaticMethod.nullObject2String(map.get("faultSite"));
		map = loadDefaultMap(map, "config/complaint-crm.xml", type);
		if (type.equals("newWorkSheet"))
		{
			String lanAttr = StaticMethod.nullObject2String(map.get("lanAttr"));
			String equipment = StaticMethod.nullObject2String(map.get("equipment"));
			String lanAttrdict = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.lanAttrdict"));
			String equipmentdict = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.equipmentdict"));
			if (lanAttr != null || lanAttr != "")
				map.put("lanAttr", lanAttrdict + lanAttr);
			if (equipment != null || equipment != "")
				map.put("equipment", equipmentdict + equipment);

			
			String subRoleId = "";
			String complaintType = StaticMethod.nullObject2String(map.get("complaintType"));
			String consultCode = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultCode"));
			String title = StaticMethod.nullObject2String(map.get("title"));

			String sendRoleId = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.SendRoleId"));
			map.put("sendRoleId", sendRoleId);
			if (complaintType.equals(""))
			{
				subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultRoleId");
				complaintType = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultType"));
				//title = "用户号码" + StaticMethod.nullObject2String(map.get("complaintNum")) + "的咨询工单";
			} else
			if (complaintType.length() > 2)
			{
				String tempStr = complaintType.substring(0, 3);
				if (consultCode.indexOf(tempStr) >= 0)
				{
					subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultRoleId");
					complaintType = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultType"));
					//title = "用户号码" + StaticMethod.nullObject2String(map.get("complaintNum")) + "的咨询工单";
				}
			}
			WPSEngineServiceMethod sm = new WPSEngineServiceMethod();
			map = setComplaintType(map, complaintType);
			map = setSheetAcceptLimit(map);
			if (subRoleId.equals(""))
			{
				if (!districtCounty.equals(""))
				{
					String tempName = faultSite + districtCounty.trim();
					System.out.println("tempName===" + tempName);
					try
					{
						ITawSystemAreaManager areaMgr = (ITawSystemAreaManager)ApplicationContextHolder.getInstance().getBean("ItawSystemAreaManager");
						TawSystemArea area = areaMgr.getAreaByCode(tempName);
						if (area != null)
							map.put("toSubDeptId", area.getAreaid());
						else
							System.out.println("没有找到映射的告警区县");
						String bigRole = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptGroupId"));
						String toDeptId = StaticMethod.nullObject2String(area.getAreaid());
						TawSystemSubRole subRole = sm.getMatchRoles("ComplaintProcess", toDeptId, bigRole, map);
						if (subRole == null || subRole.getId() == null || subRole.getId().length() == 0)
						{
							toDeptId = StaticMethod.nullObject2String(map.get("toDeptId"));
							subRole = sm.getMatchRoles("ComplaintProcess", toDeptId, bigRole, map);
							if (subRole == null || subRole.getId() == null || subRole.getId().length() == 0)
							{
								System.out.println("未找到匹配角色，使用默认角色");
								subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptRoleId");
							} else
							{
								subRoleId = subRole.getId();
							}
						} else
						if (subRole.getArea() == null || subRole.getArea() != null && subRole.getArea().length() < 5)
						{
							toDeptId = StaticMethod.nullObject2String(map.get("toDeptId"));
							subRole = sm.getMatchRoles("ComplaintProcess", toDeptId, bigRole, map);
							if (subRole == null || subRole.getId() == null || subRole.getId().length() == 0)
							{
								System.out.println("未找到匹配角色，使用默认角色");
								subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptRoleId");
							} else
							{
								subRoleId = subRole.getId();
							}
						} else
						{
							subRoleId = subRole.getId();
						}
					}
					catch (Exception err)
					{
						System.out.println("没有找到映射的告警区县");
					}
				} else{

				String bigRole = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptGroupId"));
				String toDeptId = StaticMethod.nullObject2String(map.get("toDeptId"));
				TawSystemSubRole subRole = sm.getMatchRoles("ComplaintProcess", toDeptId, bigRole, map);
				if (subRole == null || subRole.getId() == null || subRole.getId().length() == 0)
				{
					System.out.println("未找到匹配角色，使用默认角色");
					subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptRoleId");
				} else
				{
					subRoleId = subRole.getId();
				}
				}
			}
			map = sm.setAcceptRole(subRoleId, map);
			map.put("title", StaticMethod.nullObject2String(map.get("complaintNum")) + title);

			String attachId = getAttach(attach);
			System.out.println("attachId=" + attachId);
			if (attachId != null && attachId.length() > 0)
				map.put("sheetAccessories", attachId);
		} else
		if (type.equals("renewWorkSheet"))
		{
			String sheetId = StaticMethod.nullObject2String(map.get("serialNo"));
			if (sheetId == null || sheetId.equals(""))
				throw new Exception("sheetId为空");
			BaseMain main = null;
			IMainService iMainService = (IMainService)ApplicationContextHolder.getInstance().getBean(getMainBeanId());
			ITaskService iTaskService = (ITaskService)ApplicationContextHolder.getInstance().getBean(getTaskBeanId());
			List list = iMainService.getMainListByParentSheetId(sheetId);
			if (list.size() > 0)
			{
				boolean b = false;
				main = (BaseMain)list.get(0);
				List taskList = iTaskService.getCurrentUndoTask(main.getId());
				if (taskList != null)
				{
					for (int i = 0; i < taskList.size(); i++)
					{
						ITask task = (ITask)taskList.get(i);
						if (!task.getTaskOwner().equals(main.getSendRoleId()) && !task.getTaskOwner().equals(main.getSendUserId()) && !task.getTaskOwner().equals(main.getSendDeptId()))
							continue;
						b = true;
						break;
					}

				}
				if (!b)
					throw new Exception("工单未被驳回，不能重派");
				IComplaintFlowManager iComplaintFlowManager = (IComplaintFlowManager)ApplicationContextHolder.getInstance().getBean("iComplaintFlowManager");
				iComplaintFlowManager.clearControlPreExcutePerformer(main.getPiid());
			} else
			{
				throw new Exception("没找到sheetNo＝" + sheetId + "对应的工单");
			}
			String complaintType = StaticMethod.nullObject2String(map.get("complaintType"));
			String consultCode = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultCode"));
			String title = StaticMethod.nullObject2String(map.get("title"));

			String subRoleId = "";
			if (complaintType.equals(""))
			{
				subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultRoleId");
				complaintType = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultType"));
				//title = "用户号码" + StaticMethod.nullObject2String(map.get("complaintNum")) + "的咨询工单";
			} else
			if (complaintType.length() > 2)
			{
				String tempStr = complaintType.substring(0, 3);
				if (consultCode.indexOf(tempStr) >= 0)
				{
					subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultRoleId");
					complaintType = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ConsultType"));
					//title = "用户号码" + StaticMethod.nullObject2String(map.get("complaintNum")) + "的咨询工单";
				}
			}
			map = setComplaintType(map, complaintType);
			map = setSheetAcceptLimit(map);
			map.put("title", StaticMethod.nullObject2String(map.get("complaintNum")) + title);

			String sendRoleId = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.SendRoleId"));
			map.put("sendRoleId", sendRoleId);
			WPSEngineServiceMethod sm = new WPSEngineServiceMethod();
			if (subRoleId.equals(""))
			{
				String bigRole = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptGroupId"));
				String toDeptId = StaticMethod.nullObject2String(map.get("toDeptId"));
				TawSystemSubRole subRole = sm.getMatchRoles("ComplaintProcess", toDeptId, bigRole, map);
				if (subRole == null || subRole.getId() == null || subRole.getId().length() == 0)
				{
					System.out.println("未找到匹配角色，使用默认角色");
					subRoleId = XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.AcceptRoleId");
				} else
				{
					subRoleId = subRole.getId();
				}
			}
			map = sm.setAcceptRole(subRoleId, map);
			String attachId = getAttach(attach);
			System.out.println("attachId=" + attachId);
			if (attachId != null && attachId.length() > 0)
				map.put("sheetAccessories", attachId);
		} else
		if (type.equals("untreadWorkSheet"))
		{
			String sheetId = StaticMethod.nullObject2String(map.get("serialNo"));
			if (sheetId == null || sheetId.equals(""))
				throw new Exception("sheetId为空");
			BaseMain main = null;
			IMainService iMainService = (IMainService)ApplicationContextHolder.getInstance().getBean(getMainBeanId());
			ITaskService iTaskService = (ITaskService)ApplicationContextHolder.getInstance().getBean(getTaskBeanId());
			List list = iMainService.getMainListByParentSheetId(sheetId);
			if (list.size() > 0)
			{
				boolean b = false;
				main = (BaseMain)list.get(0);
				List taskList = iTaskService.getCurrentUndoTask(main.getId());
				if (taskList != null)
				{
					for (int i = 0; i < taskList.size(); i++)
					{
						ITask task = (ITask)taskList.get(i);
						if (!task.getTaskOwner().equals(main.getSendRoleId()) && !task.getTaskOwner().equals(main.getSendUserId()) && !task.getTaskOwner().equals(main.getSendDeptId()))
							continue;
						b = true;
						break;
					}

				}
				if (!b)
					throw new Exception("工单未回复，不能退回");
			} else
			{
				throw new Exception("没找到sheetNo＝" + sheetId + "对应的工单");
			}
		}
		return map;
	}

	public String getTaskBeanId()
	{
		return "iComplaintTaskManager";
	}

	public String getSheetAttachCode()
	{
		return "complaint";
	}

	public static void main(String arg[])
	{
		try
		{
			String serviceType = InterfaceUtilProperties.getInstance().getDictIdByInterfaceCode("yesOrNo", "是");
			System.out.println(serviceType);
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}

	public Map setComplaintType(Map map, String code)
		throws Exception
	{
		try
		{
			System.out.println("complaintType=" + code);
			if (code.equals(""))
				throw new Exception("没有找到映射的投诉分类:not found complaintType");
			ITawSystemDictTypeManager dictMgr = (ITawSystemDictTypeManager)ApplicationContextHolder.getInstance().getBean("ItawSystemDictTypeManager");
			String parentDictId = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ComplaintTypeDictId"));
			String complaintName = "";
			for (int i = 1; i * 2 <= code.length(); i++)
			{
				System.out.println("parentDictId" + i + "=" + parentDictId);
				String subcode = code.substring(0, i * 2);
				System.out.println("subcode" + i + "=" + subcode);
				TawSystemDictType dict = dictMgr.getDictByDictType(subcode, parentDictId);
				if (dict != null && dict.getDictId() != null)
				{
					String dictId = dict.getDictId();
					System.out.println("complaintType" + i + "=" + dictId);
					if (i == 3)
						map.put("complaintType", dictId);
					else
						map.put("complaintType" + i, dictId);
					parentDictId = dictId;
					if (i == 2 || i == 4)
						complaintName = complaintName + dict.getDictName();
					continue;
				}
				if (i == 1)
				{
					String complaintType1 = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ComplaintType1"));
					String complaintType2 = StaticMethod.nullObject2String(XmlManage.getFile("/config/complaint-crm.xml").getProperty("base.ComplaintType2"));
					map.put("complaintType1", complaintType1);
					map.put("complaintType2", complaintType2);
				}
				break;
			}

			map.put("complaintName", complaintName);
		}
		catch (Exception err)
		{
			System.out.println("没有找到映射的投诉分类:not fount complaintType");
			err.printStackTrace();
			throw err;
		}
		return map;
	}

	private Map setSheetAcceptLimit(Map map)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar c = new GregorianCalendar();
		int hour = c.getTime().getHours();
		if (hour >= 8 && hour < 16)
			c.add(10, 2);
		else
		if (hour >= 16 && hour < 24)
		{
			c.add(5, 1);
			c.set(11, 10);
			c.set(12, 0);
		} else
		if (hour < 8)
		{
			c.set(11, 10);
			c.set(12, 0);
		}
		String datestr = dateFormat.format(c.getTime());
		System.out.println("sheetAcceptLimit:" + datestr);
		map.put("sheetAcceptLimit", datestr);
		return map;
	}

	public void finishNew(String sheetkey, Map interfaceMap)
	{
		finishNew(sheetkey, interfaceMap);
		String complaintInfocontent = StaticMethod.nullObject2String(interfaceMap.get("complaintInfocontent"));
		if (complaintInfocontent.length() > 0)
		{
			IComplaintInfoManager mgr = (IComplaintInfoManager)ApplicationContextHolder.getInstance().getBean("IComplaintInfoManager");
			ComplaintInfo info = new ComplaintInfo();
			List list = mgr.getComplaintInfoListBySheetKey(sheetkey);
			if (list.size() > 0)
				info = (ComplaintInfo)list.get(0);
			else
				info.setSheetkey(sheetkey);
			info.setContent(complaintInfocontent);
			mgr.saveComplaintInfo(info);
		}
	}


	public void finishDeal(String s1, Map map1)
	{
	}
	
	public Map setBaseMap(Map map)
	{
		try
		{
			IMainService iMainService = (IMainService)ApplicationContextHolder.getInstance().getBean(getMainBeanId());
			ILinkService iLinkService = (ILinkService)ApplicationContextHolder.getInstance().getBean(getLinkBeanId());
			String mainBeanId = getMainBeanId();
			map.put("beanId", new String[] {
				mainBeanId
			});
			System.out.println("mainClassName=" + iMainService.getMainObject().getClass().getName());
			System.out.println("linkClassName=" + iLinkService.getLinkObject().getClass().getName());
			map.put("mainClassName", new String[] {
				iMainService.getMainObject().getClass().getName()
			});
			map.put("linkClassName", new String[] {
				iLinkService.getLinkObject().getClass().getName()
			});
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
		return map;
	}
	
	/**
	 * eoms投诉单实时接口（调用客服支撑系统接口）
	 * @param main 工单基本信息
	 * @param link	处理完成步骤的数据
	 * @param opType 操作类型：新增、归档
	 * @return
	 */
	public boolean complaintsSingle(ComplaintMain main,ComplaintLink link,String opType){
		List contentList = new ArrayList();
		ID2NameService service = (ID2NameService) ApplicationContextHolder.getInstance().getBean("ID2NameGetServiceCatch");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//SerialNo：客服工单流水号
		contentList.add(main.getParentCorrelation());
		//EOMS_serialNo：省EOMS工单流水号
		contentList.add(main.getSheetId());
		//EOMS_distributeDate：派单EOMS时间
		contentList.add(sdf.format(main.getSendTime()));
		//EOMS_acceptDate:EOMS受理时间
		contentList.add(sdf.format(main.getSendTime()));
		//LimitDate：工单处理时限
		contentList.add(sdf.format(main.getSheetCompleteLimit()));
		//Complain_BusinessType：七级投诉类型，汉字 ->隔开
		contentList.add(ComplaintUtils.getFullComplaintType(main.getComplaintType1(), main.getComplaintType2(), main.getComplaintType(), main.getComplaintType4(), main.getComplaintType5(), main.getComplaintType6(), main.getComplaintType7()));
		//Customer_OwnerCity：用户归属地市
		contentList.add(main.getCustomAttribution());
		//Customer_PhoneNumber:受理号码
		contentList.add(main.getComplaintNum());
		//Customer_Grade：客户级别，目前无，待绿网传（不清楚是否为字典，直接取值）
		contentList.add(main.getCustomLevel());
		//Customer_Band：客户品牌
		contentList.add(service.id2Name(main.getCustomBrand(), "ItawSystemDictTypeDao"));
		//Complain_Content：诉求内容
		contentList.add(main.getComplaintDesc());
		//IsRepeat:重复投诉，目前无，传空
		contentList.add("");
		//Complain_DataType：投诉类型，目前无，传空
		contentList.add("");
		
		//故障地点拆成两部分：其中第1级和第2级：投诉区域所属地市/县 ；第3级：问题区域
		String faultSite = StaticMethod.nullObject2String(main.getFaultSite()).trim();
		if(!"".equals(faultSite)){
			String[] sites = faultSite.split("\\|");
			if(sites.length >= 3){
				//Complain_AreaCity：投诉区域所属地市/县
				contentList.add(sites[0] + "|" + sites[1]);
				//Complain_Area：问题区域
				contentList.add(sites[2]);
			}else{
				contentList.add(faultSite);
				contentList.add("");
			}
		}else{
			contentList.add("");
			contentList.add("");
		}
		
		//Complain_Scene：网络覆盖投诉场景，目前无，传空
		contentList.add("");
		//Longitude：投诉位置经度
		contentList.add("");
		//Latitude：投诉位置纬度
		contentList.add("");
		//TerminalType：终端类型型号
		contentList.add(main.getTerminalType());
		
		//工单新增时，如下字段为空
		if(opType.equals(ComplaintConstants.NEW_WORKSHEET)){
			//Network_ProcRecord：网络侧处理记录
			contentList.add("");
			//Network_ReasonType：网络侧归因类别
			contentList.add("");
			//Network_SolveFlag：网络侧判断是否解决
			contentList.add("");
			//FinalProcDept：最终处理部门
			contentList.add("");
			//Network_PersonJobNo：网络侧处理人工号
			contentList.add("");
			//Network_SerialNoStatus：网络侧工单流转状态
			contentList.add("0");
			//Network_archiveDate：网络侧归档时间
			contentList.add("");
			//Reply_Date：回复时间
			contentList.add("");
			contentList.add("0");
		}else{
			contentList.add("处理过程：" + link.getDealDesc() + "\n处理结果：" + service.id2Name(link.getDealResult(), "ItawSystemDictTypeDao"));
			//网络侧归因类别 ：责任原因分类及编码规范汉字，多级用“->”连接
			String faultReason1 = StaticMethod.nullObject2String(link.getLinkFaultReasonOne());
			String faultReason2 = StaticMethod.nullObject2String(link.getLinkFaultReasonTwo());
			String faultReason3 = StaticMethod.nullObject2String(link.getFaultCause());
			String faultReason = "";
			if (!"".equals(faultReason1))
				faultReason = service.id2Name(faultReason1, "ItawSystemDictTypeDao");
			if (!"".equals(faultReason2))
				faultReason = faultReason + "->" + service.id2Name(faultReason2, "ItawSystemDictTypeDao");
			if (!"".equals(faultReason3))
				faultReason = faultReason + "->" + service.id2Name(faultReason3, "ItawSystemDictTypeDao");
			contentList.add(faultReason);

			String solveFlag = link.getDealResult();
			if("101030601".equals(solveFlag) || "101030603".equals(solveFlag)){
				solveFlag = "1";
			}else if("101030602".equals(solveFlag)){
				solveFlag = "2";
			}else{
				solveFlag = "0";
			}
			contentList.add(solveFlag);
			contentList.add(service.id2Name(link.getOperateRoleId(), "tawSystemSubRoleDao"));
			contentList.add(link.getOperateUserId());
			contentList.add("1");
			//Network_archiveDate：网络侧归档时间，因为main表状态还未更新，因此就用调用归档时的时间代替
			contentList.add(sdf.format(new Date()));
			contentList.add(sdf.format(link.getOperateTime()));
			
			long completeLimit = main.getSheetCompleteLimit().getTime();
			long replyTime = link.getOperateTime().getTime();
			int ifLate = 0;
			if (completeLimit < replyTime)
				ifLate = 1;
			contentList.add(Integer.valueOf(ifLate));

		}
		//User_ComplainDate：用户投诉时间
		contentList.add(sdf.format(main.getComplaintTime()));
		//IsWeakCover：是否弱覆盖区域 无此字段，直接传空
		contentList.add("");
		boolean result = false;
		try{
			String opDetail = this.getXmlFromList(contentList, StaticMethod.getFilePathForUrl("classpath:config/fullcomplaint-utils.xml"), "complaintsSingle");
			String serviceStr = this.getFullComplaintServiceUrl();
			URL url = new URL(serviceStr);
			WS_FullComplaintsSoapStub binding = (WS_FullComplaintsSoapStub)new WS_FullComplaintsLocator().getWS_FullComplaintsSoap(url);
			binding.setTimeout(60000);
			result = binding.getEOMSComplaintsSingle(opDetail);	
		}catch(Exception err){
			err.printStackTrace();
			return false;
		}
		return result;
	}
	
	/**
	 * 根据工单内容生成XML
	 * @param contentList
	 * @param filePath
	 * @param nodePath
	 * @return
	 * @throws Exception
	 */
	public String getXmlFromList(List contentList,String filePath,String nodePath) throws Exception{
		try{
			List chNameList = new ArrayList();
			List enNameList = new ArrayList();
			
			SAXBuilder dc=new SAXBuilder();
			Document doc = dc.build(new File(filePath));
			
			Element element = doc.getRootElement();
			element = element.getChild(nodePath);
			
			List list = element.getChildren();
			for(int i=0;i<list.size();i++){
				Element node = (Element)list.get(i);
				String interfaceCnName = node.getAttribute("interfaceCnName").getValue();
				String interfaceEnName = node.getAttribute("interfaceEnName").getValue();
				String columnName = node.getAttribute("columnName").getValue();
				
				System.out.println("interfaceCnName="+interfaceCnName);
				System.out.println("interfaceEnName="+interfaceEnName);
				System.out.println("columnName="+columnName);
				chNameList.add(interfaceCnName);
				enNameList.add(interfaceEnName); 
			}
			String opDetail = "<?xml version=\"1.0\" encoding=\"gb2312\"?>" + this.createOpDetailXml(chNameList, enNameList,
					contentList);
			BocoLog.info(this, nodePath+" opDetail="+opDetail);
			return opDetail;
		}catch(Exception err){
			err.printStackTrace();
			throw new Exception("生成xml出错："+err.getMessage());	
		}
	}
	
	public String getFullComplaintServiceUrl() throws Exception{
		String filePath = StaticMethod.getFilePathForUrl("classpath:config/fullcomplaint-utils.xml");
		SAXBuilder dc=new SAXBuilder();
		Document doc = dc.build(new File(filePath));
		
		Element element = doc.getRootElement();
		element = element.getChild("ServiceUrl");
		String url = element.getText();
		System.out.println("serviceUrl="+url);
		return url;
	}
	
	/**
	 * 生成全量投诉的opdetail
	 * @param chNameList
	 * @param enNameList
	 * @param contentList
	 * @return
	 */
	public String createOpDetailXml(List chNameList,List enNameList,List contentList){
		org.dom4j.Document dom4jDoc = DocumentHelper.createDocument();		
		org.dom4j.Element opDetailElement = dom4jDoc.addElement("opDetail");
		org.dom4j.Element recordInfo = opDetailElement.addElement("recordInfo");
		for(int i=0;i<chNameList.size();i++){
			org.dom4j.Element contentElement = recordInfo.addElement(StaticMethod.nullObject2String(enNameList.get(i)));
			contentElement.setText(StaticMethod.nullObject2String(contentList.get(i)));
		}
		String xml = dom4jDoc.asXML();
		String opt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		int index = xml.indexOf(opt);
		if(index>=0){
			xml = xml.substring(index+opt.length());
		}
		return xml;
	}
}
