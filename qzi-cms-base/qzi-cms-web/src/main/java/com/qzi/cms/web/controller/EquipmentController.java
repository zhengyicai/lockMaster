/* 
 * 文件名：EquipmentController.java  
 * 版权：Copyright 2016-2017 炎宝网络科技  All Rights Reserved by
 * 修改人：邱深友  
 * 创建时间：2017年7月27日
 * 版本号：v1.0
*/
package com.qzi.cms.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.qzi.cms.common.po.UseCommunityPo;
import com.qzi.cms.common.po.UseEquipmentNowStatePo;
import com.qzi.cms.common.po.UseEquipmentPortPo;
import com.qzi.cms.common.po.UseUnlockEquRecordPo;
import com.qzi.cms.common.util.ToolUtils;
import com.qzi.cms.common.vo.CommunityAdminVo;
import com.qzi.cms.common.vo.UseLockRecordVo;
import com.qzi.cms.server.mapper.UseEquipmentNowStateMapper;
import com.qzi.cms.server.mapper.UseEquipmentPortMapper;
import com.qzi.cms.server.service.web.CommunityService;
import com.qzi.cms.server.service.web.WebLockRecordService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.*;

import com.qzi.cms.common.annotation.SystemControllerLog;
import com.qzi.cms.common.enums.RespCodeEnum;
import com.qzi.cms.common.exception.CommException;
import com.qzi.cms.common.resp.Paging;
import com.qzi.cms.common.resp.RespBody;
import com.qzi.cms.common.util.LogUtils;
import com.qzi.cms.common.vo.UseEquipmentVo;
import com.qzi.cms.server.service.web.EquipmentService;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * 设备管理控制器
 * @author qsy
 * @version v1.0
 * @date 2017年7月27日
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController {
	@Resource
	private EquipmentService equipmentService;

	@Resource
	private CommunityService communityService;


	@Resource
	private WebLockRecordService webLockRecordService;


	private String imagepath = "/data/page/uploadImages/";





	@GetMapping("/findCommunitys")
	public RespBody findCommunitys(){
		RespBody respBody = new RespBody();
		try {
			//查找数据并返回
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户小区信息成功",equipmentService.findCommunitys());
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "获取用户小区信息异常");
			LogUtils.error("获取用户小区信息异常！",ex);
		}
		return respBody;
	}
	
	@GetMapping("/findBuildings")
	public RespBody findBuildings(String communityId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找楼栋数据成功", equipmentService.findBuildings(communityId));
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找楼栋数据失败");
			LogUtils.error("查找楼栋数据失败！",ex);
		}
		return respBody;
	}
	
	@GetMapping("/findUnits")
	public RespBody findUnits(String unitNo,String buildingId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找单元数据成功", equipmentService.findUnits(unitNo,buildingId));
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找单元数据失败");
			LogUtils.error("查找单元数据失败！",ex);
		}
		return respBody;
	}
	
	@GetMapping("/findAll")
	public RespBody findAll(Paging paging,String criteria){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

		  List<UseEquipmentVo> list =  equipmentService.findAll(paging,criteria);
		  for(UseEquipmentVo vo:list){
			  if(vo.getNowDate()!=null){
				  if((new Date().getTime()-vo.getNowDate().getTime())/1000>60){
				  				  vo.setNowDateStatus("离线");
				  			  }else{
				  				  vo.setNowDateStatus("在线");
				   }
			  }
		  }

			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有设备数据成功", list);
			//保存分页对象
			paging.setTotalCount(equipmentService.findCount(criteria));
			respBody.setPage(paging);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有设备数据失败");
			LogUtils.error("查找所有设备数据失败！",ex);
		}
		return respBody;
	}


	@GetMapping("/findOne")
	public RespBody findOne(String id){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有设备数据成功", communityService.findOne(id));

		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有设备数据失败");
			LogUtils.error("查找所有设备数据失败！",ex);
		}
		return respBody;
	}





	@PostMapping("/add")
	@SystemControllerLog(description="新增设备信息")
	public RespBody add(@RequestBody UseEquipmentVo equipmentVo){
		RespBody respBody = new RespBody();
		try {

			//判断当前的设备号是否超过小区设定的总数
			//			UseCommunityPo communityPo =  communityService.findOne(equipmentVo.getCommunityId());
			//
			//			int count = equipmentService.findCommunityCount(equipmentVo.getCommunityId());
			//			 if((count+1)>  communityPo.getMasterNum()){
			//				 respBody.add(RespCodeEnum.ERROR.getCode(), "设备已超出该小区设置的主机数");
			//				 return respBody;
			//			 }



			equipmentService.add(equipmentVo);








			respBody.add(RespCodeEnum.SUCCESS.getCode(), "设备数据保存成功");
		} catch (CommException ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "云之讯调用异常");
			LogUtils.error("云之讯调用异常！",ex);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "设备据保存失败");
			LogUtils.error("设备据保存失败！",ex);
		}
		return respBody;
	}



	@ResponseBody
	@RequestMapping(value = "/addUpload",method = RequestMethod.POST)
	public RespBody addEquRecord(@RequestPart("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IllegalStateException, IOException {
		RespBody respBody=new RespBody();



		if (file!=null) {// 判断上传的文件是否为空
			String path=null;// 文件路径
			String type=null;// 文件类型
			String fileName=file.getOriginalFilename();// 文件原名称
			System.out.println("上传的文件原名称:"+fileName);
			// 判断文件类型
			type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
			if (type!=null) {// 判断文件类型是否为空
				if ("XML".equals(type.toUpperCase())||"xml".equals(type.toUpperCase())) {




					// 项目在容器中实际发布运行的根路径
					String realPath=request.getSession().getServletContext().getRealPath("/");

					// 自定义的文件名称
					String trueFileName=String.valueOf(System.currentTimeMillis())+"."+type.toUpperCase();
					// 设置存放图片文件的路径



					//path = "/data/page/uploadImages/"+po.getUserName()+"/"+/*System.getProperty("file.separator")+*/trueFileName;
					path = realPath+/*System.getProperty("file.separator")+*/trueFileName;


					//保存文件
					file.transferTo(new File(path));

					//liunx密令
					// Runtime.getRuntime().exec("chmod 777 -R " +"/data/page/uploadImages/"+trueFileName);



					SAXReader reader = new SAXReader();
					File file1 = new File(path);
					Document document = null;
					try {
						document = reader.read(file1);
						Element root = document.getRootElement();
						List<Element> childElements = root.elements();
						for (Element child : childElements) {




							if("item".equals(child.getName())){
								//已知属性名情况下
								System.out.println("name: " + child.elementText("name"));
								System.out.println("ename: " + child.elementText("ename"));
								System.out.println("code" + child.elementText("code"));
								System.out.println("ip" + child.elementText("ip"));
								System.out.println("gate" + child.elementText("gate"));
								System.out.println("mask" + child.elementText("mask"));
								//这行是为了格式化美观而存在
								System.out.println();

							}

						}
					} catch (DocumentException e) {
						e.printStackTrace();
					}






						System.out.println("存放图片文件的路径:"+path);

					respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加成功",trueFileName);
				}else {
					System.out.println("不是我们想要的文件类型,请按要求重新上传");
					respBody.add(RespCodeEnum.ERROR.getCode(), "文件类型不对，请重新上传");
					return respBody;
				}
			}else {
				System.out.println("文件类型为空");
				respBody.add(RespCodeEnum.ERROR.getCode(), "文件类型为空");
				return respBody;
			}
		}else {
			System.out.println("没有找到相对应的文件");
			respBody.add(RespCodeEnum.ERROR.getCode(), "没有找到相对应的文件");
			return respBody;
		}
		return respBody;
	}



	@PostMapping("/delete")
	@SystemControllerLog(description="删除设备")
	public RespBody delete(@RequestBody UseEquipmentVo equipmentVo){
		RespBody respBody = new RespBody();
		try {
			equipmentService.delete(equipmentVo);
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "设备删除成功");
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "设备删除失败");
			LogUtils.error("设备删除失败！",ex);
		}
		return respBody;
	}


	@PostMapping("/update")
	@SystemControllerLog(description="修改设备")
	public RespBody update(@RequestBody UseEquipmentVo equipmentVo){
		RespBody respBody = new RespBody();
		try {
			equipmentService.update(equipmentVo);
			respBody.add(RespCodeEnum.SUCCESS.getCode(), "修改设备成功");
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "修改设备失败");
			LogUtils.error("修改设备失败！",ex);
		}
		return respBody;
	}




	/**
	 * equipmentId and nowState
	 * @param equipmentVo
	 * @return
	 */
	@PostMapping("/nowStatus")
	@SystemControllerLog(description="当前设备门磁状态")
	public RespBody nowStatus(@RequestBody UseEquipmentVo equipmentVo){
			RespBody respBody = new RespBody();
			try {
				equipmentService.update(equipmentVo);
				respBody.add(RespCodeEnum.SUCCESS.getCode(), "设备门磁状态修改成功");
			} catch (Exception ex) {
				respBody.add(RespCodeEnum.ERROR.getCode(), "设备门磁状态修改成功");
				LogUtils.error("设备删除失败！",ex);
			}
			return respBody;
		}

	@GetMapping("/lockFindAll")
	public RespBody lockFindAll(Paging paging,String criteria,String sysUserId,String communityId){
		RespBody respBody = new RespBody();
		try {
			//保存返回数据

			UseLockRecordVo vo = new UseLockRecordVo();
			vo.setMobile(criteria);
			vo.setUserId(sysUserId);
			vo.setCommunityId(communityId);

			List<UseLockRecordVo> list =  webLockRecordService.findAll(vo,paging);


			respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有开锁记录成功", list);
			//保存分页对象
			paging.setTotalCount(webLockRecordService.findCount(vo));
			respBody.setPage(paging);
		} catch (Exception ex) {
			respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有开锁记录失败");
			LogUtils.error("查找所有开锁记录失败！",ex);
		}
		return respBody;
	}



}
