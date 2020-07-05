package com.qzi.cms.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.qzi.cms.common.annotation.SystemControllerLog;
import com.qzi.cms.common.enums.RespCodeEnum;
import com.qzi.cms.common.po.*;
import com.qzi.cms.common.resp.RespBody;
import com.qzi.cms.common.service.RedisService;
import com.qzi.cms.common.util.HttpClientManager;
import com.qzi.cms.common.util.LogUtils;
import com.qzi.cms.common.util.SoundwavUtils;
import com.qzi.cms.common.util.ToolUtils;
import com.qzi.cms.common.vo.*;
import com.qzi.cms.server.mapper.*;
import com.qzi.cms.server.service.web.ResidentService;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019/2/25.
 */



@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    @Resource
    private UseRoomCardMapper useRoomCardMapper;
    @Resource
    private UseEquipmentMapper useEquipmentMapper;

    @Resource
    private UseCardEquipmentMapper useCardEquipmentMapper;

    @Resource
    private UseUserCardEquipmentMapper useUserCardEquipmentMapper;

    @Resource
    private UseResidentMapper useResidentMapper;

    @Resource
    private UseCommunityResidentMapper useCommunityResidentMapper;



    @Resource
    private UseEquipmentPortMapper useEquipmentPortMapper;

    @Resource
    private  UseEquipmentNowStateMapper useEquipmentNowStateMapper;


    @Resource
    private  UseResidentCardMapper useResidentCardMapper;


    @Resource
    private UseResidentUnlockRecordMapper useResidentUnlockRecordMapper;

    @Resource
    private UseResidentEquipmentMapper useResidentEquipmentMapper;


    @Resource
    private UseUnlockEquRecordMapper useUnlockEquRecordMapper;


    @Resource
    private UseCommunityMapper useCommunityMapper;

    @Resource
    private RedisService redisService;


    @Resource
    private UseLockRecordMapper useLockRecordMapper;

    public  DatagramSocket  socket = null;


    @Resource
    private UseLockUdpMapper useLockUdpMapper;





    private String imagepath = "/data/page/uploadImages/";
    private   String appid = "wx23bfac0f706f04ac";
    private   String appsecret = "098f8f3795cc7d4c2e51ecc95bf88b41";


    @GetMapping("/getCommunitylist")
    public RespBody getCommunitylist(){
        RespBody respBody = new RespBody();
        try {


            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取小区列表成功",useCommunityMapper.findList());

        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取小区列表失败");
            LogUtils.error("获取小区列表失败！",ex);
        }
        return respBody;
    }

    @GetMapping("/findCommunitys")
    public RespBody findCommunitys(String communityId){
        RespBody respBody = new RespBody();
        try {
            //查找数据并返回
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户小区信息成功",useEquipmentMapper.selectUserAll(communityId));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取用户小区信息异常");
            LogUtils.error("获取用户小区信息异常！",ex);
        }
        return respBody;
    }


    @PostMapping("/addUser")
    @SystemControllerLog(description="新增住户信息")
    public RespBody add(@RequestBody UseResidentVo useResidentVo){
        RespBody respBody = new RespBody();
        Object obj = redisService.getObj(useResidentVo.getMobile());
        String smsCode="";
        if (obj != null && obj instanceof Map) {
            Map<String, String> data = (Map<String, String>) obj;
            smsCode = data.get("smsCode");
        }
        if (!smsCode.equals(useResidentVo.getSmsCode())) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "验证码输入有误");
            return  respBody;
        }
        try {

            //添加用户
            UseResidentPo useResidentPo = new UseResidentPo();
            String id = ToolUtils.getUUID();
            useResidentPo.setId(id);
            useResidentPo.setWxId("");
            useResidentPo.setCreateTime(new Date());
            useResidentPo.setFingerUrl("");
            useResidentPo.setIdentityId("");
            useResidentPo.setPassword(useResidentVo.getPassword());

            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date myDate2 = dateFormat2.parse("2099-12-31 23:59:59");
            useResidentPo.setLastTime(myDate2);

            useResidentPo.setResidentType("");
            useResidentPo.setState("30");
            useResidentPo.setIdentityNo("");
            useResidentPo.setMobile(useResidentVo.getMobile());
            useResidentPo.setName(useResidentVo.getName());
            useResidentPo.setPassword("");
            useResidentPo.setWxId("");
            useResidentPo.setImgUrl("");
            useResidentPo.setSalt("");
            useResidentPo.setClientNumber("");
            useResidentPo.setClientPwd("");
            useResidentPo.setLoginToken("");
            useResidentPo.setOpenPwd("");

            useResidentPo.setEquipmentId(useResidentVo.getEquipmentId());
            useResidentPo.setUnitNo(useResidentVo.getUnitNo());
            useResidentPo.setRemark(useResidentVo.getRemark());


//            if(useResidentMapper.findMobile(useResidentPo.getMobile())!=null){
//                respBody.add(RespCodeEnum.ERROR.getCode(), "该手机号已经绑定过");
//                return respBody;
//            }

            useResidentPo.setCommunityId(useResidentVo.getCommunityId());



            UseEquipmentVo useEquipmentPo =   useEquipmentMapper.findId(useResidentPo.getEquipmentId());

            if(useEquipmentPo==null){
                respBody.add(RespCodeEnum.SUCCESS.getCode(), "没有找到该小区对应的设备");
                return  respBody;
            }
            UseCommunityPo useCommunityPo  =   useCommunityMapper.findOne(useResidentVo.getCommunityId());

            useResidentPo.setIdentityNo(useCommunityPo.getCommunityName()+useEquipmentPo.getEquId()+useResidentPo.getUnitNo());

            useResidentMapper.insert(useResidentPo);

            UseResidentEquipmentPo useResidentEquipmentPo = new UseResidentEquipmentPo();
            //添加公共设备
            List<UseEquipmentVo> list1 =    useEquipmentMapper.selectUserPublic(useEquipmentPo.getEquCode(),useResidentPo.getCommunityId());
            for(int i = 0;i<list1.size();i++){
                useResidentEquipmentPo.setId(ToolUtils.getUUID());
                useResidentEquipmentPo.setCommunityId(useResidentPo.getCommunityId());
                useResidentEquipmentPo.setState("10");
                useResidentEquipmentPo.setResidentId(useResidentPo.getId());
                useResidentEquipmentPo.setEquipmentId(list1.get(i).getId());
                useResidentEquipmentMapper.insert(useResidentEquipmentPo);
            }

            respBody.add(RespCodeEnum.SUCCESS.getCode(), "绑定住户成功,请等待管理员审核",useResidentPo);
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "住户据保存失败");
            LogUtils.error("住户据保存失败！",ex);
        }
        return respBody;
    }


    /**
     * 获取设备列表
     * @return
     */
    @GetMapping("/getlist")
    public RespBody getlist(HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {

            List<UseEquipmentVo> list = useEquipmentMapper.appFindUseEquipmentNo(homeUserCommunityVo.getDefaultRoomId(),homeUserCommunityVo.getRoomId().substring(0,10));
            List<UseEquipmentVo> list1 = useEquipmentMapper.appFindUseEquipmentNo1(homeUserCommunityVo.getDefaultRoomId(),homeUserCommunityVo.getRoomId().substring(0,6));


            if(list1.size()>0){
                for(UseEquipmentVo epo:list1){
                    list.add(epo);
                }
            }


            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取设备列表成功",list);

        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取设备列表失败");
            LogUtils.error("获取设备列表失败！",ex);
        }
        return respBody;
    }

    /**
     * 获取卡号列表
     * @return
     */
    @GetMapping("/getCardlist")
    public RespBody getCardlist(HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useCardEquipmentMapper.findCardList(homeUserCommunityVo.getDefaultRoomId(),homeUserCommunityVo.getEquipmentId()));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取卡号列表失败");
            LogUtils.error("获取卡号列表失败！",ex);
        }
        return respBody;
    }


    @PostMapping("/updateCardState")
    public RespBody updateCardState(@RequestBody HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {
            useCardEquipmentMapper.updateCardEquipment(homeUserCommunityVo.getCardId(),homeUserCommunityVo.getEquipmentId(),homeUserCommunityVo.getLinkState());
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "发卡成功");
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;

    }


    //获取小区对应的设备
    @GetMapping("/getCommunity")
    public RespBody getCommunity(String  communityId){
        RespBody respBody = new RespBody();

        try {
            List<UseEquipmentVo> useEquipmentPoList =   useEquipmentMapper.communityIdList(communityId);
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useEquipmentPoList);
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;
    }




    //获取小区对应的设备
    @GetMapping("/test")
    public RespBody test(){
        RespBody respBody = new RespBody();

        String str=  "{'deviceCode':'200311','cmd':'heart','communityId':'123456'}";

        JSONObject jsStr = JSONObject.parseObject(str+"");



        String  equNo1 = jsStr.getString("deviceCode");
        String cmd1  = jsStr.getString("cmd");
        String communityNo1 = jsStr.getString("communityId");
        UseCommunityVo useCommunityVo =  useCommunityMapper.findCommunityNo(communityNo1);

        UseEquipmentVo useEquipmentVo =    useEquipmentMapper.selectEquipmentNo(equNo1,useCommunityVo.getId());

        useEquipmentPortMapper.update("1232","8080",useEquipmentVo.getId());

        UseEquipmentNowStatePo nowStatePo =   useEquipmentNowStateMapper.findOne(equNo1,useEquipmentVo.getId());

        try {
            //List<UseEquipmentVo> useEquipmentPoList =   useEquipmentMapper.communityIdList(communityId);
          //  respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useEquipmentPoList);
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;
    }

    //获取该设备的房卡列表

    @GetMapping("/getUserCardList")
    public RespBody getUserCardList(String  equipmentId){
        RespBody respBody = new RespBody();

        try {
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取卡号列表成功",useUserCardEquipmentMapper.findCardList(equipmentId));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;
    }


    @PostMapping("/updateUserCardState")
    public RespBody updateUserCardState(@RequestBody HomeUserCommunityVo homeUserCommunityVo){
        RespBody respBody = new RespBody();
        try {
            useUserCardEquipmentMapper.updateUserCardEquipment(homeUserCommunityVo.getCardId(),homeUserCommunityVo.getLinkState());
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "发卡成功");
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "发卡失败");
            LogUtils.error("发卡失败！",ex);
        }
        return respBody;

    }



    /**
     * 添加人脸开锁记录（带开锁图片）
     */
    @ResponseBody
    @RequestMapping(value = "/addEquRecord",method = RequestMethod.POST)
    public RespBody addEquRecord(MultipartFile file, UseUnlockEquRecordPo po, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IllegalStateException, IOException {
        RespBody respBody=new RespBody();

        if (file!=null) {// 判断上传的文件是否为空
            String path=null;// 文件路径
            String type=null;// 文件类型
            String fileName=file.getOriginalFilename();// 文件原名称
            System.out.println("上传的文件原名称:"+fileName);
            // 判断文件类型
            type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type!=null) {// 判断文件类型是否为空
                if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {




                    // 项目在容器中实际发布运行的根路径
                    String realPath=request.getSession().getServletContext().getRealPath("/");
                    // 自定义的文件名称
                    String trueFileName=String.valueOf(System.currentTimeMillis())+fileName;
                    // 设置存放图片文件的路径


                    path = imagepath+/*System.getProperty("file.separator")+*/trueFileName;
                    //path = realPath+/*System.getProperty("file.separator")+*/trueFileName;

                    System.out.println("存放图片文件的路径:"+path);
                    // 转存文件到指定的路径
                    file.transferTo(new File(path));


                    Runtime.getRuntime().exec("chmod 777 -R " +imagepath+trueFileName);
                    //添加设备记录

                    UseUnlockEquRecordPo recordPo = new UseUnlockEquRecordPo();
                    recordPo.setId(ToolUtils.getUUID());
                    recordPo.setWxId(po.getWxId());
                    recordPo.setEquipmentNo(po.getEquipmentNo());
                    recordPo.setState(po.getState());
                    recordPo.setCreateTime(new Date());
                    recordPo.setImgUrl(trueFileName);
                    recordPo.setRemark(po.getRemark());
                    useUnlockEquRecordMapper.insert(recordPo);


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




    @ResponseBody
    @RequestMapping(value = "/unlockCallback",method = RequestMethod.POST)
    public RespBody unlockCallback(@RequestBody UnlockRecord1 unlockRecord1, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IllegalStateException, IOException {
        RespBody respBody=new RespBody();

        //System.out.print("++++++++++++++++"+unlockRecord1.getDeviceCode()+",,,,"+unlockRecord1.getRoomNumber());
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加成功");


        UseEquipmentVo useEquipmentVo =   useEquipmentMapper.selectEquipmentOne(unlockRecord1.getDeviceCode(),unlockRecord1.getCommunityId());

        if(useEquipmentVo==null){
            respBody.add(RespCodeEnum.ERROR.getCode(), "该设备不存在，请联系管理员");
            return  respBody;
        }

        UseLockRecordPo useLockRecordPo = new UseLockRecordPo();
        useLockRecordPo.setId(ToolUtils.getUUID());
        useLockRecordPo.setCreateTime(new Date());
        useLockRecordPo.setState("10");
        useLockRecordPo.setEquipmentId(useEquipmentVo.getId());
        useLockRecordPo.setCardId("");
        useLockRecordPo.setUserId("");
        useLockRecordPo.setUserName("");


        useLockRecordPo.setDeviceCode(unlockRecord1.getDeviceCode());
        useLockRecordPo.setCommunityId(unlockRecord1.getCommunityId());
        useLockRecordPo.setFile(unlockRecord1.getFile());
        useLockRecordPo.setType(unlockRecord1.getType());
        useLockRecordPo.setResult(unlockRecord1.getResult());
        useLockRecordPo.setUnlockTime(unlockRecord1.getUnlockTime());
        useLockRecordPo.setRoomNumber(unlockRecord1.getRoomNumber());
        useLockRecordPo.setPhone(unlockRecord1.getPhone());
        useLockRecordPo.setFile(unlockRecord1.getFile());

        useLockRecordMapper.insert(useLockRecordPo);




        return  respBody;

    }






        /**
         * 添加人脸记录数据
         */
    @ResponseBody
    @RequestMapping(value = "/photoUpload",method = RequestMethod.POST)
    public RespBody photoUpload(MultipartFile file, UseResidentVo useResidentVo, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IllegalStateException, IOException {
        RespBody respBody=new RespBody();

        if (file!=null) {// 判断上传的文件是否为空
            String path=null;// 文件路径
            String type=null;// 文件类型
            String fileName=file.getOriginalFilename();// 文件原名称
            System.out.println("上传的文件原名称:"+fileName);
            // 判断文件类型
            type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type!=null) {// 判断文件类型是否为空
                if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {





                    // 项目在容器中实际发布运行的根路径
                    String realPath=request.getSession().getServletContext().getRealPath("/");
                    // 自定义的文件名称
                    String trueFileName=String.valueOf(System.currentTimeMillis())+fileName;
                    // 设置存放图片文件的路径


                    path=imagepath+/*System.getProperty("file.separator")+*/trueFileName;
                   // path=realPath+trueFileName;  //本地服务器图片目录
                    System.out.println("存放图片文件的路径:"+path);
                    // 转存文件到指定的路径
                    file.transferTo(new File(path));
                    Runtime.getRuntime().exec("chmod 777 -R " +imagepath+trueFileName);
                    UseResidentPo wxUseResidentPo = useResidentMapper.findWxId(useResidentVo.getWxId());
                    if(wxUseResidentPo!=null){


                        wxUseResidentPo.setImgUrl(trueFileName);
                        wxUseResidentPo.setMobile(useResidentVo.getMobile());
                        wxUseResidentPo.setName(useResidentVo.getName());
                        wxUseResidentPo.setCreateTime(new Date());
                        wxUseResidentPo.setIdentityNo(useResidentVo.getIdentityNo());
                        useResidentMapper.updateByPrimaryKey(wxUseResidentPo);

                        List<UseResidentEquipmentPo> epo =  useResidentEquipmentMapper.findResidentState(wxUseResidentPo.getId());
                        if(epo !=null){
                            for(UseResidentEquipmentPo epo1:epo){
                                useEquipmentNowStateMapper.update("20",epo1.getEquipmentId());
                            }
                        }
                        //respBody.add(RespCodeEnum.ERROR.getCode(), "该微信已绑定过小区");

                        return respBody;
                    }


                    //添加用户
                    UseResidentPo useResidentPo = new UseResidentPo();
                    String id = ToolUtils.getUUID();
                    useResidentPo.setId(id);
                    useResidentPo.setWxId(useResidentVo.getWxId());
                    useResidentPo.setCreateTime(new Date());
                    useResidentPo.setFingerUrl("");
                    useResidentPo.setIdentityId("");

                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {

                        if("10".equals(useResidentVo.getResidentType())){
                            Date myDate2 = dateFormat2.parse("2099-12-31 23:59:59");
                            useResidentPo.setLastTime(myDate2);
                        }else{
                           Date   date=new Date(); //取时间
                            Calendar   calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动
                            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
                            useResidentPo.setLastTime(date);
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    useResidentPo.setResidentType(useResidentVo.getResidentType());
                    useResidentPo.setState("10");
                    useResidentPo.setIdentityNo(useResidentVo.getIdentityNo());
                    useResidentPo.setMobile(useResidentVo.getMobile());
                    useResidentPo.setName(useResidentVo.getName());
                    useResidentPo.setWxId(useResidentVo.getWxId());
                    useResidentPo.setImgUrl(trueFileName);
                    useResidentMapper.insert(useResidentPo);



                    //添加用户和小区的关联
                    UseCommunityResidentPo residentPo = new UseCommunityResidentPo();
                    residentPo.setCommunityId(useResidentVo.getCommunityId());
                    residentPo.setResidentId(id);
                    residentPo.setState("10");
                    useCommunityResidentMapper.insert(residentPo);




                    respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加成功");
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


    /**
     * 用户设备列表
     */


    @GetMapping("/userEquipment")
    public RespBody userEquipment(String  wxid){
        RespBody respBody = new RespBody();
        List<UseEquipmentVo > list =   useResidentEquipmentMapper.findWxId(wxid);
        if(list == null){
            respBody.add(RespCodeEnum.ERROR.getCode(),"没有设备授权");
        }else{
            respBody.add(RespCodeEnum.SUCCESS.getCode(),"设备授权列表",list);
        }
        return respBody;
    }




    @GetMapping("/findResident")
    public RespBody findResident(String  wxid){
        RespBody respBody = new RespBody();
        UseResidentVo vo =   useResidentMapper.findWxIds(wxid);
        if(vo == null){
            respBody.add(RespCodeEnum.ERROR.getCode(),"该用户没有绑定");
        }else{


            respBody.add(RespCodeEnum.SUCCESS.getCode(),"用户数据获取成功",vo);
        }
        return respBody;
    }






    /**
     * 远程开锁
     */

    @ResponseBody
    @RequestMapping(value = "/onlineUnlock",method = RequestMethod.POST)
    public RespBody onlineUnlock(@RequestBody UseEquipmentPortPo po) throws  Exception {

        UseEquipmentPortPo  portPo =   useEquipmentPortMapper.findOne(po.getEquipmentNo(),po.getEquipmentId());
        RespBody respBody = new RespBody();
        //UseResidentPo usePo =  useResidentMapper.findWxId(po.getId());

        UseResidentVo useResidentVo = new UseResidentVo();

        useResidentVo.setMobile(po.getId());
//        useResidentVo.setWxId(usePo.getWxId());
        useResidentVo.setCmd("unlock");
        useResidentVo.setDeviceId(po.getEquipmentId());

        UseCommunityPo useCommunityPo =  useResidentMapper.findCommunitys(po.getIps());

        UseResidentPo useResidentPo = useResidentMapper.findOne(po.getIps());
        if(useCommunityPo==null){
            respBody.add(RespCodeEnum.ERROR.getCode(),"该用户没有绑定");
            return respBody;
        }

        useResidentVo.setEquipmentNo(po.getEquipmentNo());




        String id = ToolUtils.getUUID();
        OnlockVo onlockVo  = new OnlockVo();
        onlockVo.setCmd("unlock");
        onlockVo.setCommunityId(useCommunityPo.getCommunityNo());
        onlockVo.setDeviceCode(po.getEquipmentNo());
        onlockVo.setPhone(po.getId());
        onlockVo.setId(id);
        onlockVo.setRoomNumber(useResidentPo.getUnitNo());  //房间号



        byte[] bs = JSON.toJSONString(onlockVo).getBytes();//要发的信息内容

        InetAddress desIp = InetAddress.getByName(portPo.getIps());
        DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, Integer.parseInt(portPo.getPort()));

        //创建数据报套接字，UDPA的端口设置为10086
        //DatagramSocket  = new DatagramSocket(9999);

        if(socket == null){
            respBody.add(RespCodeEnum.ERROR.getCode(),"该用户没有绑定");
           sentPort();
           return respBody;
        }
        //UDPA给UDPB发送数据报
        socket.send(p);

        System.out.println("发送udp"+desIp+":"+portPo.getPort()+""+p);


        UseLockUdpPo useLockUdpPo = new UseLockUdpPo();
        useLockUdpPo.setId(id);
        useLockUdpPo.setState("20");
        useLockUdpPo.setCreateTime(new Date());
        useLockUdpPo.setPhone(po.getId());
        useLockUdpPo.setRoomNumber(useResidentPo.getUnitNo());
        useLockUdpPo.setCommunityId(useCommunityPo.getCommunityNo());
        useLockUdpPo.setEquipmentId(po.getEquipmentId());
        useLockUdpMapper.insert(useLockUdpPo);




        respBody.add(RespCodeEnum.SUCCESS.getCode(),"用户数据获取成功",useLockUdpPo);

        return  respBody;
        //关闭socket_A套接字
        //Thread.sleep(1000);
        //socket.close();


    }


    /**
     * 获取开锁记录
     */

    @GetMapping("/getRecord")
    public RespBody getRecord(String  wxId){
        RespBody respBody = new RespBody();

        try {
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取开锁列表成功",useResidentUnlockRecordMapper.findAll(wxId));
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取开锁列表失败");
            LogUtils.error("获取开锁列表失败！",ex);
        }
        return respBody;
    }


    /**
     * 获取反馈状态
     * @param phone
     * @param equipmentId
     * @return
     */
    @GetMapping("/getEquipmentStatus")
    public RespBody getEquipmentStatus(String  phone,String equipmentId){
        RespBody respBody = new RespBody();


       UseResidentPo useResidentPo  =   useResidentMapper.findMobile(phone);

       UseLockUdpPo useLockUdpPo =    useLockUdpMapper.findId(equipmentId);

        respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户数据成功",useLockUdpPo);

        return  respBody;
    }




    @GetMapping("/getUser")
    public RespBody getUser(String  phone,String id){
        RespBody respBody = new RespBody();

       String str =  "{\"communityId\":\"123456\",\"phone\":\"18676487058\",\"roomNumber\":\"0111\",\"timeStamp\":1590820324858,\"unitId\":\"0011\"}";

      // UseResidentPo useResidentPo =   useResidentMapper.findMobile(phone);

        UseResidentPo useResidentPo =  useResidentMapper.findMobileId(phone,id);

       if(useResidentPo==null){
           respBody.add(RespCodeEnum.ERROR.getCode(), "该账号暂未绑定");
           return respBody;
       }

       if("20".equals(useResidentPo.getState())){
           respBody.add(RespCodeEnum.ERROR.getCode(), "该用户被禁用","");
           return respBody;
       }

        if("30".equals(useResidentPo.getState())){
            respBody.add(RespCodeEnum.ERROR.getCode(), "等待管理员审核","");
            return respBody;
        }


       //System.out.print(str);

       CodeVo codeVo = new CodeVo();
       UseEquipmentVo useEquipmentPo =   useEquipmentMapper.findId(useResidentPo.getEquipmentId());
       UseCommunityPo useCommunityPo =  useCommunityMapper.findOne(useEquipmentPo.getCommunityId());


       codeVo.setCommunityId(useCommunityPo.getCommunityNo());
       codeVo.setPhone(phone);
       codeVo.setRoomNumber(useResidentPo.getUnitNo());
       codeVo.setTimeStamp(new Date().getTime()+"");
       codeVo.setUnitId(useEquipmentPo.getEquCode());



    //    System.out.print(JSONObject.parse(str));
      //  Object obj = JSONObject.parse(codeVo);
        //str =  JSON.toJSONString(codeVo);

        str =  codeVo.getCommunityId()+","+codeVo.getPhone()+","+codeVo.getRoomNumber()+","+codeVo.getTimeStamp()+","+codeVo.getUnitId();
        //str = "123456,18676487058,0101,1540796022356,0011";
        //String obj1=   JSON.toJSONString(obj);

       byte[] data = str.getBytes();
        try {

            byte[] dataEnc = new byte[data.length];
            for (int i = 0; i < dataEnc.length; i++) {
                dataEnc[i] = (byte)(data[i]^0x31);
            }

            String s2 = new String(dataEnc);

            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户数据成功",s2);
        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "获取用户数据失败");
            LogUtils.error("获取用户数据失败！",ex);
        }
        return respBody;
    }


    @GetMapping("/getResidentList")
    public RespBody getResidentList(String  phone) {
        RespBody respBody = new RespBody();


       List<UseResidentVo> list =   useResidentMapper.findMobileList(phone);

//       for(UseResidentVo useResidentVo:list){
//           if(useResidentVo.getEquCode().length()>=8){
//               useResidentVo.setIdentityNo(useResidentVo.getCommunityName()+useResidentVo.getEquId());
//           }else{
//               useResidentVo.setIdentityNo(useResidentVo.getCommunityName()+useResidentVo.getEquId()+useResidentVo.getUnitNo());
//
//           }
//       }

       if(list.size()==0){
           respBody.add(RespCodeEnum.ERROR.getCode(), "用户暂未关联住址");
           return respBody;
       }

       respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户数据成功",list);

        return  respBody;
    }


    @GetMapping("/getResident1")
    public RespBody getResident1(String  id) {
        RespBody respBody = new RespBody();


        UseResidentVo useResidentVo =   useResidentMapper.findResidentId(id);


//        if(useResidentVo!=null){
//            if(useResidentVo.getEquCode().length()>=8){
//                useResidentVo.setIdentityNo(useResidentVo.getCommunityName()+useResidentVo.getEquId());
//            }else{
//                useResidentVo.setIdentityNo(useResidentVo.getCommunityName()+useResidentVo.getEquId()+useResidentVo.getUnitNo());
//
//            }
//            respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户数据成功",useResidentVo);
//
//        }else {
//            respBody.add(RespCodeEnum.ERROR.getCode(), "获取用户数据失败");
//
//        }

        respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取用户数据成功",useResidentVo);



        return  respBody;
    }


    @GetMapping("/getEquipmentList")
    public RespBody getEquipmentList(String  phone,String id){
        RespBody respBody = new RespBody();


       //UseResidentPo useResidentPo =    useResidentMapper.findMobile(phone);
        UseResidentPo useResidentPo =  useResidentMapper.findMobileId(phone,id);


       if(useResidentPo==null){
           respBody.add(RespCodeEnum.ERROR.getCode(), "该账号暂未绑定");
           return respBody;
       }


        if("20".equals(useResidentPo.getState())){
            respBody.add(RespCodeEnum.ERROR.getCode(), "该用户被禁用");
            return respBody;
        }


        if("30".equals(useResidentPo.getState())){
            respBody.add(RespCodeEnum.ERROR.getCode(), "等待管理员审核");
            return respBody;
        }


       List<EquipmentOnlineVo>  equlpmentOnlineList =   useEquipmentMapper.findOnlineAll(useResidentPo.getId(),useResidentPo.getCommunityId());


        if(equlpmentOnlineList.size()==0){
            respBody.add(RespCodeEnum.ERROR.getCode(), "该用户没有绑定设备");
            return respBody;
        }

        for(EquipmentOnlineVo vo:equlpmentOnlineList){
            if(vo.getStatus()!=null){
                if("30".equals(vo.getStatus())){
                    vo.setStatus("20");
                }else{
                    if((new Date().getTime()-vo.getOnlineCreateTime().getTime())/1000>60){
                        vo.setStatus("20");
                    }else{
                        vo.setStatus("10");
                    }

                }

            }
        }


       respBody.add(RespCodeEnum.SUCCESS.getCode(), "获取数据成功",equlpmentOnlineList);

        return  respBody;

    }


//
//    public void doData(String value){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                value
//            }
//        }).start();
//    }




    //获取微信assess_token
    private String getAccessToken() {


        String url="https://api.weixin.qq.com/cgi-bin/token?"+"grant_type=client_credential&appid="+appid+"&secret="+appsecret;
        try {

            String result = HttpClientManager.getUrlData(url);

            JSONObject pa=JSONObject.parseObject(result);
            if(pa  !=null){
                redisService.putString("access_token",pa.getString("access_token") , 7000).equalsIgnoreCase("ok");
            }else{

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    @PostMapping("/sentWeixinText")
    public RespBody sentWeixin(@RequestBody WxObjectVo vo) throws Exception {
        RespBody respBody = new RespBody();

        WxMessage wx = new WxMessage();

        WxContent content = new WxContent();
        content.setContent(vo.getContent());

        wx.setTouser(vo.getWxId());
        wx.setMsgtype("text");
        wx.setText(content);

        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+redisService.getString("access_token");
        System.out.println("test"+ JSON.toJSONString(wx)) ;
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "操作结果", HttpClientManager.postUrlData(url,JSON.toJSONString(wx)));
        return respBody;
    }



    @PostMapping("/sentWeixinNews")
    public RespBody sentWeixinNews(@RequestBody WxObjectVo vo) throws Exception {
        RespBody respBody = new RespBody();

        WxNews wx = new WxNews();

        WxNewsContents wxNewsContents = new WxNewsContents();
        wxNewsContents.setDescription(vo.getDescription());  //备注详情
        wxNewsContents.setPicurl(vo.getUrl());
        wxNewsContents.setUrl(vo.getUrl());
        wxNewsContents.setTitle(vo.getContent());

        WxNewsList list1 = new WxNewsList();
        List<WxNewsContents> newsContentsList = new ArrayList<WxNewsContents>();
        newsContentsList.add(wxNewsContents);

        list1.setArticles(newsContentsList);

        wx.setTouser(vo.getWxId());
        wx.setMsgtype("news");
        wx.setNews(list1);

        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+redisService.getString("access_token");
        System.out.println("test"+ JSON.toJSONString(wx)) ;
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "操作结果", HttpClientManager.postUrlData(url,JSON.toJSONString(wx)));
        return respBody;
    }








    //获取设备是否需要同步数据
    @GetMapping("/sentPort")
    public RespBody sentPort() throws  Exception{
        RespBody respBody = new RespBody();


        if(socket != null){
            return respBody;
        }


        //刷新assess_token
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//          @Override
//          public void run() {
//                System.out.println("assess_token:"+getAccessToken()) ;
//          }
//        }, 1000,7000000);// 设定指定的时间time,此处为2000毫秒



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                socket = new DatagramSocket(7000);
                byte[] buf = new byte[1024];

                while(true){
                    //建立udp的服务 ，并且要监听一个端口。
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                        System.out.print("udp recv start");
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
                        //调用udp的服务接收数据
                        socket.receive(datagramPacket); //receive是一个阻塞型的方法，没有接收到数据包之前会一直等待。 数据实际上就是存储到了byte的自己数组中了。
                        System.out.println("接收端接收到的数据："+ new String(buf,0,datagramPacket.getLength())); // getLength() 获取数据包存储了几个字节。
                        System.out.println("接收端接收到的数据："+datagramPacket.getData()); // getLength() 获取数据包存储了几个字节。








                        //String[] recordList = new String(buf,0,datagramPacket.getLength()).split(",",-1);


                        //{"deviceCode":"200311","cmd":"heart","communityId":"123456"}


                        if(!(new String(buf,0,datagramPacket.getLength()).indexOf("deviceCode")>0)){

                        }else{
                            System.out.println("receive阻塞了我，哈哈"+datagramPacket.getAddress()+":"+datagramPacket.getPort());
                            JSONObject jsStr = JSONObject.parseObject(new String(buf,0,datagramPacket.getLength()));



                            String  equNo1 = jsStr.getString("deviceCode");
                            String cmd1  = jsStr.getString("cmd");
                            String communityNo1 = jsStr.getString("communityId");


                            System.out.println("equNo1="+equNo1+",cmd1="+cmd1+",communityNo1="+communityNo1);

                            UseCommunityVo useCommunityVo =  useCommunityMapper.findCommunityNo(communityNo1);


                            if(useCommunityVo==null){
                                continue;
                            }

                            UseEquipmentVo useEquipmentVo =  useEquipmentMapper.selectEquipmentNo(equNo1,useCommunityVo.getId());
                            if(useEquipmentVo==null){
                                continue;
                            }

                            //doData(datagramPacket.getData());
                            //UseEquipmentPortPo portPo = new UseEquipmentPortPo();
                            useEquipmentPortMapper.update(String.valueOf(datagramPacket.getAddress()).substring(1,String.valueOf(datagramPacket.getAddress()).length()),datagramPacket.getPort()+"",useEquipmentVo.getId());



                            //修改开锁状态
                            if("unlockack".equals(cmd1)){
                                String phone = jsStr.getString("phone");
                                String roomNumber = jsStr.getString("roomNumber");
                                String id = jsStr.getString("id");

                                //useLockUdpMapper.updateStatus(phone,roomNumber);
                                useLockUdpMapper.updateStatusOne(id);
                            }


                            UseEquipmentNowStatePo nowStatePo =   useEquipmentNowStateMapper.findOne(equNo1,useEquipmentVo.getId());
                            if(nowStatePo == null){
                                respBody.add(RespCodeEnum.SUCCESS.getCode(), "error");
                                byte[] bs = "error".getBytes();//要发的信息内容
                                //UDPA与UDPB的ip均为本机ip，故设置不同的端口号
                                InetAddress desIp = InetAddress.getByName(datagramPacket.getAddress().toString().substring(1,datagramPacket.getAddress().toString().length()));

                                System.out.println("desIp"+desIp);
                                DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, datagramPacket.getPort());
                                socket.send(p);
                            }else{
                                respBody.add(RespCodeEnum.SUCCESS.getCode(), nowStatePo.getState());

                                nowStatePo.setUpdateTime(new Date());
                                nowStatePo.setState("10");
                                useEquipmentNowStateMapper.updateByPrimaryKey(nowStatePo);

                                UseEquipmentNowStateVo1 nowStateVo = new UseEquipmentNowStateVo1();
                                nowStateVo.setDeviceCode(nowStatePo.getEquipmentNo());
                                nowStateVo.setResult("0");
                                nowStateVo.setCmd("heartack");

                                byte[] bs = JSON.toJSONString(nowStateVo).getBytes();//要发的信息内容
                                //UDPA与UDPB的ip均为本机ip，故设置不同的端口号
                                InetAddress desIp = InetAddress.getByName(datagramPacket.getAddress().toString().substring(1,datagramPacket.getAddress().toString().length()));

                                System.out.println("desIp"+desIp);
                                DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, datagramPacket.getPort());
                                socket.send(p);
                            }




                        }




                        //关闭资源
                      //  socket.close();



                }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        return respBody;


    }

    @PostMapping("/sentUnlockSuccess")
    public RespBody sentUnlockSuccess(@RequestBody UseResidentUnlockRecordPo po){
        RespBody respBody = new RespBody();
        try {
            UseResidentUnlockRecordPo unlockRecordPo = new UseResidentUnlockRecordPo();

            unlockRecordPo.setId(ToolUtils.getUUID());
            unlockRecordPo.setWxId(po.getWxId());
            unlockRecordPo.setEquipmentNo(po.getEquipmentNo());
            unlockRecordPo.setState(po.getState());
            unlockRecordPo.setCreateTime(new Date());
            unlockRecordPo.setDeviceId(po.getDeviceId());
            useResidentUnlockRecordMapper.insert(unlockRecordPo);
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "添加开锁列表成功");

        } catch (Exception ex) {
            respBody.add(RespCodeEnum.ERROR.getCode(), "参数查找成功");
            LogUtils.error("参数查找成功！",ex);
        }
        return respBody;
    }


    @GetMapping("/getUnlockSuccess")
    public RespBody getUnlockSuccess(String deviceId ) throws  Exception{
        RespBody respBody = new RespBody();
        UseResidentUnlockRecordPo po = useResidentUnlockRecordMapper.deviceId(deviceId);
        if(po!=null){
            respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有住户房卡数据成功", po);
        }else{
            respBody.add(RespCodeEnum.ERROR.getCode(), "查找所有住户房卡数据成功", po);
        }
        return respBody;
    }


    @ResponseBody
    @RequestMapping(value = "/addUpload",method = RequestMethod.POST)
    public RespBody addUpload( HttpServletRequest request) throws IllegalStateException, IOException {
        RespBody respBody = new RespBody();

        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        String name = params.getParameter("name");
        System.out.println("name:" + name);

        String id = params.getParameter("id");
        System.out.println("id:" + id);

        Map<String, String> urlMap = new HashMap<>();

        MultipartFile file = null;
        BufferedOutputStream stream = null;
        String realPath=request.getSession().getServletContext().getRealPath("/");

        String urls = "";
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();

                    stream = new BufferedOutputStream(new FileOutputStream(
                            new File(imagepath+file.getOriginalFilename())));//存在项目根目录下
                    urls = imagepath+file.getOriginalFilename();


                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    stream = null;
                    return respBody;
                }
            } else {
                return respBody;
            }
        }

       // file.transferTo(new File(path));


        Runtime.getRuntime().exec("chmod 777 -R " +urls);
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有住户房卡数据成功", urls);


        return respBody;
    }
    //获取设备是否需要同步数据(弃用)
    @GetMapping("/sentUnlock")
    public RespBody sentUnlock() throws  Exception{
        RespBody respBody = new RespBody();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //建立udp的服务 ，并且要监听一个端口。
                    DatagramSocket  socket = null;
                    try {
                        socket = new DatagramSocket(6000);
                        byte[] buf = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
                        //调用udp的服务接收数据

                        socket.receive(datagramPacket); //receive是一个阻塞型的方法，没有接收到数据包之前会一直等待。 数据实际上就是存储到了byte的自己数组中了。
                        System.out.println("接收端接收到的数据："+ new String(buf,0,datagramPacket.getLength())); // getLength() 获取数据包存储了几个字节。



                        UseResidentUnlockRecordPo unlockRecordPo = new UseResidentUnlockRecordPo();
                        String[] recordList = new String(buf,0,datagramPacket.getLength()).split(",",-1);
                        unlockRecordPo.setId(ToolUtils.getUUID());
                        unlockRecordPo.setWxId(recordList[0]);
                        unlockRecordPo.setEquipmentNo(recordList[1]);
                        unlockRecordPo.setState("10");
                        unlockRecordPo.setCreateTime(new Date());
                        useResidentUnlockRecordMapper.insert(unlockRecordPo);




                        //UseEquipmentPortPo portPo = new UseEquipmentPortPo();
                        //useEquipmentPortMapper.update(String.valueOf(datagramPacket.getAddress()).substring(1,String.valueOf(datagramPacket.getAddress()).length()),datagramPacket.getPort()+"",new String(buf,0,datagramPacket.getLength()));



                        //	UseEquipmentNowStatePo nowStatePo =   useEquipmentNowStateMapper.findOne(new String(buf,0,datagramPacket.getLength()));
                        //
                        //	respBody.add(RespCodeEnum.SUCCESS.getCode(), nowStatePo.getState());
                        //


                        //关闭资源
                        socket.close();


                        byte[] bs = "10".getBytes();//要发的信息内容
                        //UDPA与UDPB的ip均为本机ip，故设置不同的端口号
                        InetAddress desIp = InetAddress.getByName(datagramPacket.getAddress().toString().substring(1,datagramPacket.getAddress().toString().length()));

                        //数据报包，UDPB的端口为10010
                        DatagramPacket p = new DatagramPacket(bs, bs.length, desIp, datagramPacket.getPort());
                        //创建数据报套接字，UDPA的端口设置为10086
                        DatagramSocket socket_A = new DatagramSocket(9999);
                        //UDPA给UDPB发送数据报
                        socket_A.send(p);
                        //关闭socket_A套接字
                        socket_A.close();

                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //准备空的数据包用于存放数据。

                }
            }
        }).start();



        return respBody;


    }


    /**
     * 查询用户
     * @param equipmentNo
     * @return
     * @throws Exception
     */

    @GetMapping("/getResident")
    public RespBody getResident(String equipmentNo ) throws  Exception{
        RespBody respBody = new RespBody();
        List<UseResidentVo> relist =  useResidentEquipmentMapper.findResidentId(equipmentNo);
        if(relist!=null){
            for(UseResidentVo vo1:relist){
                vo1.setCardPos(useResidentCardMapper.findResidentCardNoIds(vo1.getId()));
            }
        }
        respBody.add(RespCodeEnum.SUCCESS.getCode(), "查找所有住户房卡数据成功", relist);
        return respBody;
    }









}
