/* 
 * 文件名：UseRoomMapper.java  
 * 版权：Copyright 2016-2017 炎宝网络科技  All Rights Reserved by
 * 修改人：邱深友  
 * 创建时间：2017年7月6日
 * 版本号：v1.0
*/
package com.qzi.cms.server.mapper;

import com.qzi.cms.common.po.UseLockRecordPo;
import com.qzi.cms.common.po.UseLockUdpPo;
import com.qzi.cms.common.vo.UseLockRecordVo;
import com.qzi.cms.server.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 房间DAO
 * @author qsy
 * @version v1.0
 * @date 2020年5月日
 */
public interface UseLockUdpMapper extends BaseMapper<UseLockUdpPo>{



//	 List<UseLockRecordVo> findAll(@Param("model") UseLockRecordVo useLockRecordVo, @Param("startRow") int startRow, @Param("pageSize") int pageSize);
//
//	 long findCount(@Param("model") UseLockRecordVo useLockRecordVo);

	@Update("update use_lock_udp set state='10' where  phone=#{phone} and roomNumber=#{roomNumber}")
	public void updateStatus(@Param("phone") String phone,@Param("roomNumber") String roomNumber);


	@Update("update use_lock_udp set state='10' where  id=#{id}")
	public void updateStatusOne(@Param("id") String id);

	@Select("select * from  use_lock_udp where id=#{id}")
	public  UseLockUdpPo findId(@Param("id") String id);



	@Select("select * from  use_lock_udp where phone=#{phone} and roomNumber=#{roomNumber} and equipmentId=#{equipmentId} order by createTime desc limit 1")
	public  UseLockUdpPo findOne(@Param("phone") String phone,@Param("roomNumber") String roomNumber,@Param("equipmentId") String equipmentId);

}