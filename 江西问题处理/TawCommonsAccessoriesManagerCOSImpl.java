// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TawCommonsAccessoriesManagerCOSImpl.java

package com.boco.eoms.commons.accessories.service.impl;

import com.boco.eoms.base.service.impl.BaseManager;
import com.boco.eoms.base.util.*;
import com.boco.eoms.commons.accessories.dao.TawCommonsAccessoriesDao;
import com.boco.eoms.commons.accessories.exception.AccessoriesConfigException;
import com.boco.eoms.commons.accessories.exception.AccessoriesException;
import com.boco.eoms.commons.accessories.model.TawCommonsAccessories;
import com.boco.eoms.commons.accessories.model.TawCommonsAccessoriesConfig;
import com.boco.eoms.commons.accessories.service.ITawCommonsAccessoriesConfigManager;
import com.boco.eoms.commons.accessories.service.ITawCommonsAccessoriesManager;
import com.boco.eoms.commons.accessories.util.*;
import com.boco.eoms.commons.loging.BocoLog;
import com.boco.eoms.commons.util.xml.XMLProperties;
import com.boco.eoms.commons.util.xml.XmlManage;
import com.oreilly.servlet.multipart.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpLoginException;

public class TawCommonsAccessoriesManagerCOSImpl extends BaseManager
	implements ITawCommonsAccessoriesManager
{

	private TawCommonsAccessoriesDao dao;
	private ITawCommonsAccessoriesConfigManager configManager;
	private String filePath;
	FtpClient fc;

	public TawCommonsAccessoriesManagerCOSImpl()
	{
		fc = new FtpClient();
	}

	public TawCommonsAccessoriesDao getDao()
	{
		return dao;
	}

	public void setDao(TawCommonsAccessoriesDao dao)
	{
		this.dao = dao;
	}

	public ITawCommonsAccessoriesConfigManager getConfigManager()
	{
		return configManager;
	}

	public void setConfigManager(ITawCommonsAccessoriesConfigManager configManager)
	{
		this.configManager = configManager;
	}

	public List getTawCommonsAccessoriess()
	{
		return dao.getTawCommonsAccessoriess();
	}

	public TawCommonsAccessories getTawCommonsAccessories(String id)
	{
		return dao.getTawCommonsAccessories(new String(id));
	}

	public void saveTawCommonsAccessories(TawCommonsAccessories tawCommonsAccessories)
	{
		dao.saveTawCommonsAccessories(tawCommonsAccessories);
	}

	public void removeTawCommonsAccessories(String id)
	{
		dao.removeTawCommonsAccessories(new String(id));
	}

	public List saveFile(HttpServletRequest request, String appCode, String accesspriesFileNames)
		throws AccessoriesException
	{
		List list = new ArrayList();
		File tempFile = null;
		File currentFile = null;
		accesspriesFileNames = StaticMethod.nullObject2String(accesspriesFileNames);
		try
		{
			if (request.getContentType() != null && request.getContentType().indexOf("multipart/form-data") >= 0)
			{
				TawCommonsAccessoriesConfig config = configManager.getAccessoriesConfigByAppcode(appCode);
				Integer maxSize = config.getMaxSize();
				MultipartParser mp = new MultipartParser(request, maxSize.intValue() * 1024 * 1024, true, true, "UTF-8");
				String rootFilePath = AccessoriesMgrLocator.getAccessoriesAttributes().getUploadPath();
				String path = this.filePath.substring(this.filePath.indexOf(":") + 1, this.filePath.length()) + config.getPath();
				String filePath = rootFilePath + path;
				AccessoriesUtil.createFile(filePath, "/");
				File file = new File(filePath);
				if (!file.exists())
					file.mkdir();
				Part part;
				while ((part = mp.readNextPart()) != null) 
					if (part.isFile())
					{
						FilePart filePart = (FilePart)part;
						String fileCnName = filePart.getFileName();
						if (fileCnName != null)
						{
							filePart.writeTo(file);
							String fileName = StaticMethod.getCurrentDateTime("yyyyMMddHHmmss") + randomKey(4);
							currentFile = new File(file, fileCnName);
							fileName = fileName + fileCnName.substring(fileCnName.lastIndexOf("."));
							tempFile = new File(file, fileName);
							if (currentFile.isFile())
								currentFile.renameTo(tempFile);
							TawCommonsAccessories accessories = new TawCommonsAccessories();
							accessories.setAccessoriesCnName(fileCnName);
							accessories.setAccessoriesName(fileName);
							accessories.setAccessoriesPath(path);
							accessories.setAccessoriesSize(tempFile.length());
							accessories.setAccessoriesUploadTime(StaticMethod.getLocalTime());
							accessories.setAppCode(config.getAppCode());
							dao.saveTawCommonsAccessories(accessories);
							accesspriesFileNames = accesspriesFileNames + ",'" + fileName + "'";
						}
					}
			}
			if (!accesspriesFileNames.equals(""))
			{
				if (accesspriesFileNames.indexOf(",") == 0)
					accesspriesFileNames = accesspriesFileNames.substring(1);
				list = dao.getAllFileByName(accesspriesFileNames);
			}
		}
		catch (Exception lEx)
		{
			lEx.printStackTrace();
			BocoLog.error(this, "文件上传错误");
			throw new AccessoriesException("文件上传错误");
		}
		return list;
	}

	public List getAllFileById(String fileIds)
		throws AccessoriesException
	{
		List list = dao.getAllFileByName(fileIds);
		return list;
	}

	public String[] getFilePathByName(String fileNames)
		throws AccessoriesException
	{
		List list = dao.getAllFileByName(fileNames);
		String filePaths[] = new String[list.size()];
		TawCommonsAccessories accessories = null;
		for (int i = 0; i < list.size(); i++)
		{
			accessories = (TawCommonsAccessories)list.get(i);
			String filePath = accessories.getAccessoriesPath() + "\\" + accessories.getAccessoriesName();
			filePaths[i] = filePath;
		}

		return filePaths;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public String getFilePath(String appId)
		throws AccessoriesConfigException
	{
		TawCommonsAccessoriesConfig config = configManager.getAccessoriesConfigByAppcode(appId);
		String rootFilePath = AccessoriesMgrLocator.getAccessoriesAttributes().getUploadPath();
		String path = filePath.substring(filePath.indexOf(":") + 1, filePath.length()) + config.getPath();
		return rootFilePath + path + "/";
	}

	public String getUrlById(String id)
	{
		return ((EOMSAttributes)ApplicationContextHolder.getInstance().getBean("eomsAttributes")).getEomsUrl() + "/accessories/tawCommonsAccessoriesConfigs.do?method=download&id=" + id;
	}

	public String downFromOtherSystem(String cnName, String strRemoteAddr, String code)
	{
		String fileName = "";
		URL url = null;
		String errFtp = "";
		try
		{
			TawCommonsAccessoriesConfig tc = configManager.getAccessoriesConfigByAppcode(code);
			String rootFilePath = AccessoriesMgrLocator.getAccessoriesAttributes().getUploadPath();
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@filrPath=" + filePath);
			String path = filePath.substring(filePath.indexOf(":") + 1, filePath.length()) + tc.getPath();
			String physicalPath = rootFilePath + path + "/";
			if (strRemoteAddr.indexOf("&amp;") >= 0)
				strRemoteAddr = strRemoteAddr.replace("&amp;", "&");
			url = new URL(strRemoteAddr);
			String realFileName = (new File(url.getFile())).getName();
			String sysFilename = "";
			System.out.println("@@@@@@@@@@@@@@@@@@@@cnName = " + cnName);
			if (cnName.equals(""))
				sysFilename = StaticMethod.getCurrentDateTime("yyyyMMddHHmmss") + randomKey(4) + realFileName.substring(realFileName.indexOf("."));
			else
			if (cnName.indexOf(".") >= 0)
				sysFilename = StaticMethod.getCurrentDateTime("yyyyMMddHHmmss") + randomKey(4) + cnName.substring(cnName.indexOf("."));
			else
				sysFilename = StaticMethod.getCurrentDateTime("yyyyMMddHHmmss") + randomKey(4) + realFileName.substring(realFileName.indexOf("."));
			System.out.println("@@@@@@@@@@@@@@@@@@@@sysFilename = " + sysFilename);
			String downType = strRemoteAddr.substring(0, 1);
			System.out.println("@@@@@@@@@@@@@@@physicalPath=" + physicalPath);
			System.out.println("@@@@@@@@@@@@@@@strRemoteAddr=" + strRemoteAddr);
			System.out.println("@@@@@@@@@@@@@@@sysFilename=" + sysFilename);
			if (downType.equals("h"))
			{
				System.out.println();
				Thread downThread = new Thread(new FileDownLoad(strRemoteAddr, physicalPath, sysFilename));
				downThread.start();
			} else
			if (downType.equals("f"))
			{
				String ftpserver = "";
				if (code.equals("newmission")){
					ftpserver = StaticMethod.nullObject2String(XmlManage.getFile("/config/newaccessoriesServer.xml").getProperty("ftp.ip"));
				}else{
					if(strRemoteAddr.contains("10.180.25.82")){
						ftpserver = "10.180.25.82";
					}else{
						ftpserver = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.ip"));
					}
				}
				String serverPath = "";
				if (strRemoteAddr.indexOf(":21") >= 0)
					serverPath = strRemoteAddr.substring(strRemoteAddr.indexOf(ftpserver) + ftpserver.length() + 3, strRemoteAddr.length());
				else
					serverPath = strRemoteAddr.substring(strRemoteAddr.indexOf(ftpserver) + ftpserver.length(), strRemoteAddr.length());
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@serverPath = " + serverPath);
				if (code.equals("newmission")){
					errFtp = downloadFileByFtps(sysFilename, physicalPath, serverPath);
				}else{
					if(strRemoteAddr.contains("10.180.25.82")){
						errFtp = downloadFileByFtpp(sysFilename, physicalPath, serverPath);
					}else{
						errFtp = downloadFileByFtp(sysFilename, physicalPath, serverPath);
					}
					
				}
					
				if (!errFtp.equals(""))
				{
					BocoLog.error(this, errFtp);
					System.out.println(errFtp);
				}
			}
			TawCommonsAccessories accessories = new TawCommonsAccessories();
			if (!cnName.equals(""))
			{
				if (cnName.indexOf(".") >= 0)
					accessories.setAccessoriesCnName(cnName);
				else
					accessories.setAccessoriesCnName(cnName + sysFilename.substring(sysFilename.indexOf(".")));
			} else
			{
				accessories.setAccessoriesCnName(realFileName);
			}
			accessories.setAccessoriesEnName(sysFilename);
			accessories.setAccessoriesName(sysFilename);
			String fileEnd = filePath.substring(filePath.indexOf(":") + 1, filePath.length()) + tc.getPath();
			accessories.setAccessoriesPath(fileEnd);
			File file = new File(physicalPath);
			File systemFile = new File(file, sysFilename);
			accessories.setAccessoriesSize(systemFile.length());
			accessories.setAccessoriesUploadTime(StaticMethod.getLocalTime());
			accessories.setAppCode(code);
			dao.saveTawCommonsAccessories(accessories);
			fileName = sysFilename;
			System.out.println("文件下载成功！");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
		return fileName;
	}

	public String downloadFileByFtps(String fileName, String filepath, String serverPath)
	{
		String retMessage;
		retMessage = "";
		String ftpserver = "";
		String userLogin = "";
		String pwdLogin = "";
		String ftpserver = StaticMethod.nullObject2String(XmlManage.getFile("/config/newaccessoriesServer.xml").getProperty("ftp.ip"));
		String userLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/newaccessoriesServer.xml").getProperty("ftp.username"));
		String pwdLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/newaccessoriesServer.xml").getProperty("ftp.password"));
		retMessage = connectToFtpServer(ftpserver, userLogin, pwdLogin);
		if (!retMessage.equals(""))
			return "下载时文件：" + fileName + "时无法和FTP服务器连接!" + retMessage;
		try
		{
			TelnetInputStream is = fc.get(serverPath);
			File file_out = new File(filepath + "/" + fileName);
			FileOutputStream os = new FileOutputStream(file_out);
			byte bytes[] = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) 
				os.write(bytes, 0, c);
			is.close();
			os.close();
			fc.closeServer();
		}
		catch (Exception e)
		{
			retMessage = "下载文件：" + fileName + "时发生文件读写错误：" + e.getMessage();
			System.out.println("获取文件时发生错误：" + e.getMessage());
		}
		return retMessage;
	}

		public String downloadFileByFtpp(String fileName, String filepath, String serverPath)
	{
		String retMessage;
		retMessage = "";
		String ftpserver = "";
		String userLogin = "";
		String pwdLogin = "";
		String ftpserver = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftpp.ip"));
		String userLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftpp.username"));
		String pwdLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftpp.password"));
		retMessage = connectToFtpServer(ftpserver, userLogin, pwdLogin);
		if (!retMessage.equals(""))
			return "下载时文件：" + fileName + "时无法和FTP服务器连接!" + retMessage;
		try
		{
			TelnetInputStream is = fc.get(serverPath);
			File file_out = new File(filepath + "/" + fileName);
			FileOutputStream os = new FileOutputStream(file_out);
			byte bytes[] = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) 
				os.write(bytes, 0, c);
			is.close();
			os.close();
			fc.closeServer();
		}
		catch (Exception e)
		{
			retMessage = "下载文件：" + fileName + "时发生文件读写错误：" + e.getMessage();
			System.out.println("获取文件时发生错误：" + e.getMessage());
		}
		return retMessage;
	}
	
	public String downloadFileByFtp(String fileName, String filepath, String serverPath)
	{
		String retMessage;
		retMessage = "";
		String ftpserver = "";
		String userLogin = "";
		String pwdLogin = "";
		String ftpserver = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.ip"));
		String userLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.username"));
		String pwdLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.password"));
		retMessage = connectToFtpServer(ftpserver, userLogin, pwdLogin);
		if (!retMessage.equals(""))
			return "下载时文件：" + fileName + "时无法和FTP服务器连接!" + retMessage;
		try
		{
			TelnetInputStream is = fc.get(serverPath);
			File file_out = new File(filepath + "/" + fileName);
			FileOutputStream os = new FileOutputStream(file_out);
			byte bytes[] = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) 
				os.write(bytes, 0, c);
			is.close();
			os.close();
			fc.closeServer();
		}
		catch (Exception e)
		{
			retMessage = "下载文件：" + fileName + "时发生文件读写错误：" + e.getMessage();
			System.out.println("获取文件时发生错误：" + e.getMessage());
		}
		return retMessage;
	}

	public void uploadFileByFtp(String fileName, String filepath, String serverPath)
	{
		String retMessage = "";
		String ftpserver = "";
		String userLogin = "";
		String pwdLogin = "";
		String ceshipot1 = "";
		String ceshipot2 = "";
		String ceshipot3 = "";
		String ceshipot4 = "";
		try
		{
			ftpserver = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.ip"));
			userLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.username"));
			pwdLogin = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("ftp.password"));
			retMessage = connectToFtpServer(ftpserver, userLogin, pwdLogin);
			if (!retMessage.equals(""))
				System.out.println("上传文件：" + fileName + "时无法和FTP服务器连接");
			ceshipot1 = "@@@@@@@@open serverfile";
			TelnetOutputStream os = fc.put(serverPath + fileName);
			ceshipot2 = "@@@@@@@@open localfile";
			File file_in = new File(filepath + fileName);
			ceshipot3 = "@@@@@@@@open file";
			FileInputStream is = new FileInputStream(file_in);
			ceshipot4 = "@@@@@@@@open readfile";
			byte bytes[] = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) 
				os.write(bytes, 0, c);
			is.close();
			os.close();
			fc.closeServer();
		}
		catch (IOException e)
		{
			retMessage = "上传文件：" + fileName + "时发生文件读写错误：" + e.getMessage();
			System.out.println("上传文件：" + fileName + "时发生文件读写错误：" + e.getMessage());
			System.out.println("@@@@@@ceshipot1 " + ceshipot1);
			System.out.println("@@@@@@ceshipot2 " + ceshipot2);
			System.out.println("@@@@@@ceshipot3 " + ceshipot3);
			System.out.println("@@@@@@ceshipot4 " + ceshipot4);
		}
	}

	private String connectToFtpServer(String ftpserver, String userLogin, String pwdLogin)
	{
		if (ftpserver == null || ftpserver.equals(""))
			return "FTP服务器名设置不正确!";
		try
		{
			fc.openServer(ftpserver);
			fc.login(userLogin, pwdLogin);
			fc.binary();
		}
		catch (FtpLoginException e)
		{
			return "没有与FTP服务器连接的权限,或用户名密码设置不正确!";
		}
		catch (IOException e)
		{
			return "与FTP服务器连接失败!";
		}
		catch (SecurityException e)
		{
			return "没有权限与FTP服务器连接";
		}
		return "";
	}

	public TawCommonsAccessories getSystemToOther(String id, String uploadType)
	{
		TawCommonsAccessories Accessories = new TawCommonsAccessories();
		try
		{
			List list = dao.getFileByName(id);
			TawCommonsAccessories commonsAccessories = (TawCommonsAccessories)list.get(0);
			String rootFilePath = AccessoriesMgrLocator.getAccessoriesAttributes().getUploadPath();
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@rootFilePath = " + rootFilePath);
			String physicalPath = rootFilePath + commonsAccessories.getAccessoriesPath() + "/";
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@physicalPath = " + physicalPath);
			String temp[] = commonsAccessories.getAccessoriesName().split(",");
			String contentPath = "";
			if (uploadType.equals("ftp"))
			{
				contentPath = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("rootpath.ftp"));
				String url = contentPath + "/" + commonsAccessories.getAccessoriesPath() + "/";
				for (int k = 0; k < temp.length; k++)
				{
					uploadFileByFtp(temp[k], physicalPath, contentPath);
					Accessories.setAccessoriesCnName(commonsAccessories.getAccessoriesCnName());
					Accessories.setAccessoriesSize(commonsAccessories.getAccessoriesSize());
					Accessories.setAccessoriesPath(url);
				}

			} else
			if (uploadType.equals("http"))
			{
				contentPath = StaticMethod.nullObject2String(XmlManage.getFile("/config/accessoriesServer.xml").getProperty("rootpath.http"));
				String url = contentPath + commonsAccessories.getAccessoriesPath() + "/";
				System.out.println("@@@@@@@@@@@@@@@@@@@@url=" + url);
				for (int j = 0; j < temp.length; j++)
					if (temp[j].indexOf(id) >= 0)
					{
						url = url + temp[j];
						Accessories.setAccessoriesCnName(commonsAccessories.getAccessoriesCnName());
						Accessories.setAccessoriesSize(commonsAccessories.getAccessoriesSize());
						Accessories.setAccessoriesPath(url);
						System.out.println("!!!!!!!!!!!AccessoriesCnName=" + commonsAccessories.getAccessoriesCnName());
					}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			BocoLog.error(this, "外部接口调用错误");
		}
		return Accessories;
	}

	public TawCommonsAccessories getTawCommonsAccessoriesByName(String id)
	{
		List list = dao.getFileByName(id);
		TawCommonsAccessories accessories = new TawCommonsAccessories();
		if (list.size() > 0)
			accessories = (TawCommonsAccessories)list.get(0);
		return accessories;
	}

	public String randomKey(int sLen)
	{
		String base = "1234567890";
		String temp = "";
		for (int i = 0; i < sLen; i++)
		{
			int p = (int)(Math.random() * 10D);
			temp = temp + base.substring(p, p + 1);
		}

		return temp;
	}

	public List getNameByDateName(String name)
	{
		return dao.getNameByDateName(name);
	}
}
