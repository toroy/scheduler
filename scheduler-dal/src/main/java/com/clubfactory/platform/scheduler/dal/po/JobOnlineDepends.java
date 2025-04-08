package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.DependTypeEnum;

import lombok.Data;

@Data
public class JobOnlineDepends extends BasePO {

	/**
	 * 父节点id
	 */
	private Long parentId;

	/**
	 * 任务id
	 */
	private Long jobId;

	/**
	 * 依赖类型
	 */
	private DependTypeEnum type;
	
	@Override
    public int hashCode()  {
        int PRIME = 59;
        int result = 1;
        Object $parentId = getParentId();
        result = result * 59 + ($parentId != null ? $parentId.hashCode() : 43);
        Object $jobId = getJobId();
        result = result * 59 + ($jobId != null ? $jobId.hashCode() : 43);
        Object $type = getType();
        result = result * 59 + ($type != null ? $type.hashCode() : 43);
        return result;
    }

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof JobOnlineDepends))
			return false;
		JobOnlineDepends other = (JobOnlineDepends) o;
		if (!other.canEqual(this))
			return false;
		Object this$parentId = getParentId();
		Object other$parentId = other.getParentId();
		if (this$parentId != null ? !this$parentId.equals(other$parentId) : other$parentId != null)
			return false;
		Object this$jobId = getJobId();
		Object other$jobId = other.getJobId();
		if (this$jobId != null ? !this$jobId.equals(other$jobId) : other$jobId != null)
			return false;
		Object this$type = getType();
		Object other$type = other.getType();
		return this$type != null ? this$type.equals(other$type) : other$type == null;
	}

	@Override
	public String toString() {
		return (new StringBuilder("JobOnlineDepends(parentId=")).append(getParentId()).append(", jobId=")
				.append(getJobId()).append(", type=").append(getType()).append(")").toString();
	}

}
