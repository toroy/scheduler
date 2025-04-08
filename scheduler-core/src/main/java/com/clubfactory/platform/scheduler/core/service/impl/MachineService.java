package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.scheduler.core.vo.SlotMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.core.vo.MachineVO;
import com.clubfactory.platform.scheduler.dal.dao.MachineMapper;
import com.clubfactory.platform.scheduler.dal.enums.CommonStatus;
import com.clubfactory.platform.scheduler.dal.enums.MachineTypeEnum;
import com.clubfactory.platform.scheduler.dal.po.Machine;
import com.google.common.collect.Lists;

@Service
@Slf4j
public class MachineService extends BaseNewService<MachineVO,Machine> {

    @Resource
    MachineMapper machineMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(machineMapper);
    }
    
    public Map<Long, String> getMap(List<Long> ids) {
    	Assert.collectionNotEmpty(ids, "ids");
    	Machine machine = new Machine();
    	machine.setIds(ids);
    	machine.setIsDeleted(false);
    	machine.setStatus(CommonStatus.ENABLED);
    	machine.setType(MachineTypeEnum.WORKER);
    	List<MachineVO> vos = this.list(machine);
    	if (CollectionUtils.isEmpty(vos)) {
    		return Maps.newHashMap();
    	}
    	return vos.stream().collect(Collectors.toMap(MachineVO::getId, MachineVO::getIp));
    }
    
    public Map<Long, String> getAllMap() {
    	Machine machine = new Machine();
    	machine.setIsDeleted(false);
    	machine.setStatus(CommonStatus.ENABLED);
    	machine.setType(MachineTypeEnum.WORKER);
    	List<MachineVO> vos = this.list(machine);
    	if (CollectionUtils.isEmpty(vos)) {
    		return Maps.newHashMap();
    	}
    	return vos.stream().collect(Collectors.toMap(MachineVO::getId, MachineVO::getIp));
    }
    
	/**
	 * 获取类型对应的ip类别map
	 * 
	 * @return key: 类型
	 *         value: ip列表
	 */
    public Map<String, List<String>> getIpsByJobTypeMap() {
    	Machine machine = new Machine();
		machine.setIsDeleted(false);
		machine.setType(MachineTypeEnum.WORKER);
		machine.setStatus(CommonStatus.ENABLED);
		machine.setIsSelf(false);
		List<MachineVO> machineVos = this.list(machine);
		return getIpsMap(machineVos);
    }

	public Map<String, List<String>> getIpsMap(List<MachineVO> machineVos) {
		if (CollectionUtils.isEmpty(machineVos)) {
			return Maps.newHashMap();
		}
		Map<String, List<String>> jobTypeMaps = Maps.newHashMap();
		for (MachineVO machineVO : machineVos) {
			if (machineVO.getFunctions() == null || StringUtils.isEmpty(machineVO.getIp())) {
				continue;
			}
			String key = machineVO.getFunctions();
			List<String> list = jobTypeMaps.get(key);
			if (CollectionUtils.isNotEmpty(list)) {
				list.add(machineVO.getIp());
				jobTypeMaps.put(key, list);
			} else {
				jobTypeMaps.put(key, Lists.newArrayList(machineVO.getIp()));
			}
		}
		return jobTypeMaps;
	}
    
    public List<MachineVO> listSlaves() {
    	Machine machine = new Machine();
		machine.setIsDeleted(false);
		machine.setStatus(CommonStatus.ENABLED);
		machine.setType(MachineTypeEnum.WORKER);
		return this.list(machine);
    }
    
    public List<MachineVO> listDisableSlaves() {
    	Machine machine = new Machine();
		machine.setIsDeleted(false);
		machine.setStatus(CommonStatus.DISABLED);
		machine.setType(MachineTypeEnum.WORKER);
		return this.list(machine);
    }
    

    public Boolean isMaster(String ip) {
    	Machine machine = new Machine();
    	machine.setIsDeleted(false);
    	machine.setType(MachineTypeEnum.MASTER);
    	machine.setIp(ip);
    	Machine res = this.get(machine);
    	
    	if (res != null) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public void disable(List<Long> ids) {
    	Assert.collectionNotEmpty(ids, "id列表");

    	log.info("disable, ids: {}", JSON.toJSONString(ids));
    	Machine machine = new Machine();
    	machine.setIds(ids);
    	Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status",CommonStatus.DISABLED);
		machine.setUpdateParam(updateParam);
		this.edit(machine);
    }
    
    public void disableByIp(String ip) {
    	Assert.notBlank(ip);

		log.info("disableByIp, ip: {}", ip);
    	Machine machine = new Machine();
    	machine.setIp(ip);
    	Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status",CommonStatus.DISABLED);
		machine.setUpdateParam(updateParam);
		this.edit(machine);
    }
    
    public void enableByIp(String ip) {
    	Assert.notBlank(ip);
    	Machine machine = new Machine();
    	machine.setIp(ip);
    	Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status",CommonStatus.ENABLED);
		machine.setUpdateParam(updateParam);
		this.edit(machine);
    }
    
    public void enable(List<Long> ids) {
    	Assert.collectionNotEmpty(ids, "id列表");
    	Machine machine = new Machine();
    	machine.setIds(ids);
    	Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status",CommonStatus.ENABLED);
		machine.setUpdateParam(updateParam);
		this.edit(machine);
    }

    public void changeServerStatus(Machine machine){
		Assert.notNull(machine.getIp());
		Assert.notNull(machine.getStatus());

		log.info("changeServerStatus, machine: {}", JSON.toJSONString(machine));
		Machine newMachine = new Machine();
		newMachine.setIp(machine.getIp());
		newMachine.setType(machine.getType());

		Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status",machine.getStatus());
		newMachine.setUpdateParam(updateParam);
		this.edit(newMachine);
	}

	/**
	 * 根据IP统计当前worker的slot数
	 * @param ipAddress
	 * @return
	 */
	public Integer countSlotsByIp(String ipAddress){
		Assert.notNull(ipAddress);
		return machineMapper.countSlotsByIp(ipAddress);
	}

	/**
	 * 根据调度机IP 按类型获取slots
	 * @param ip
	 * @return
	 */
	public List<SlotMap> listSlotMapByIp(String ip){
		Assert.notNull(ip,"调度机IP");
		Machine machine = new Machine();
		machine.setIsDeleted(false);
		machine.setStatus(CommonStatus.ENABLED);
		machine.setType(MachineTypeEnum.WORKER);
		machine.setIp(ip);
		return this.list(machine).stream()
				.map(po -> SlotMap.builder()
						.feature(po.getFunctions())
						.slot(po.getSlots())
						.build())
				.collect(Collectors.toList());
	}
    
}
