package com.clubfactory.platform.scheduler.core.service.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clubfactory.platform.scheduler.common.bean.PageUtils;
import com.clubfactory.platform.scheduler.common.util.BeanUtil;
import com.clubfactory.platform.scheduler.dal.dao.BaseMapper;
import com.clubfactory.platform.scheduler.dal.po.BasePO;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;



/**
 * @param <V>
 * @param <P>
 */
public class BaseNewService<V extends P, P extends BasePO> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected BaseMapper<P> baseMapper;

    protected Class<V> voClass;

    protected Class<P> poClass;
    
    protected final static Integer BATCH_NUM = 1_000;

    public BaseMapper<P> getBaseMapper() {
        return baseMapper;
    }

    public void setBaseMapper(BaseMapper<P> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public BaseNewService() {
        Class<?> c = getClass();

        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.voClass = (Class<V>) p[0];
            this.poClass = (Class<P>) p[1];
        }
    }

    /**
     * 成功返回插入的对象，否则返回null
     * 有时候需要把插入的值返回给前端展示，返回int没有任何用
     *
     * @param vo
     * @return
     */
    public P save(P po) {
        if (po == null) {
            return null;
        }
        //初始化插入时间，更新时间
        po.initCreate();
        int result = baseMapper.save(po);
        if (result > 0) {
            return po;
        }
        return null;
    }
    
    public Boolean saveResult(P po) {
    		P p = save(po);
    		if (p != null) {
    			return true;
    		} else {
    			return false;
    		}
    }

    /**
     * 更新成功，返回原对象，失败返回null
     * 有时候需要把更新的对象返回给前端展示，返回int没有任何用
     *
     * @param vo
     * @return
     */
    public P edit(P po) {
        if (po == null) {
            return null;
        }
        //初始化插入时间，更新时间
        po.initUpdate();
        int result = baseMapper.edit(po);
        if (result > 0) {
            return po;
        }
        return null;
    }

    public Boolean editResult(P po) {
    		if (po == null) {
            return false;
        }
    		P res = edit(po);
    		if (res != null) {
    			return true;
    		} else {
    			return false;
    		}
    }

    public int saveBatch(List<P> list) {
        if (CollectionUtils.isEmpty(list)) {
            return -1;
        }
        List<P> poList = Lists.newArrayListWithCapacity(list.size());
        for (P po : list) {
            //初始化插入时间，更新时间
        	po.initCreate();
            poList.add(po);
        }
        // 服务端对传输的数据大小有限制
        while (true) {
        	if (poList.size() <= BATCH_NUM) {
        		if (CollectionUtils.isEmpty(poList)) {
        			break;
        		}
        		baseMapper.saveBatch(poList);
        		break;
        	}
        	List<P> listSub = poList.subList(0, BATCH_NUM);
        	baseMapper.saveBatch(listSub);
        	
        	poList = poList.subList(BATCH_NUM, poList.size());
        }
        return 1;
    }

    public int count(P po) {
        if (po == null) {
            return 0;
        }
        return baseMapper.count(po);
    }

    public PageUtils<P> pageList(P po) {
        if (null == po) {
            return new PageUtils(new ArrayList<P>(),0, po.getPageSize(), po.getPageNo());
        }
        try {
            int totalCount = baseMapper.count(po);
            if (totalCount <= 0) {
                return new PageUtils(new ArrayList<P>(), 0, po.getPageSize(), po.getPageNo());
            }
            po.setTotalCount(totalCount);//设置总记录数，获取总页数
            //vo.initPage();//分页保护，防止参数传入过大
            //mybatis插件，分页前要先调用下这个语句
            PageHelper.startPage(po.getPageNo(), po.getPageSize(), false);
            List<P> list = baseMapper.list(po);
            return new PageUtils(list, po.getTotalCount(), po.getPageSize(), po.getPageNo());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("mybatis分页查询出错:" + ex);
        }
        return new PageUtils(new ArrayList<P>(), 0, po.getPageSize(), po.getPageNo());
    }



    public PageUtils<V> pageVoList(P po) {
    	PageUtils<P> pageUI = pageList(po);
        List<P> poList = pageUI.getRows();
        if (CollectionUtils.isEmpty(poList)) {
            return new PageUtils(new ArrayList<V>(),0, po.getPageSize(), po.getPageNo());
        }
        List<V> list = Lists.newArrayListWithCapacity(poList.size());
        for (P po2 : poList) {
            list.add(fromPoToVo(po2));
        }
        return new PageUtils(list, po.getTotalCount(), po.getPageSize(), po.getPageNo());
    }


    public List<V> fromPoListToVoList(List<P> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return Lists.newArrayList();
        }
        List<V> voList = new ArrayList<V>(poList.size());
        for (P po : poList) {
            V vo = fromPoToVo(po);
            if (vo != null) {
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * po层到vo层的转换，一般面对用户是vo，面向数据库是po,如果po和vo字段基本差不多，则直接用，不用改任何东西
     * 如果po和vo字段相差很多，则子类service可以覆盖这个方法，自己写转换函数
     *
     * @param po
     * @return
     */
    public V fromPoToVo(P po) {
        try {
            if (null == po) {
                return null;
            }
            V vo = voClass.newInstance();
            BeanUtil.copyBeanNotNull2Bean(po, vo);
            return vo;
        } catch (InstantiationException e) {
            logger.error("InstantiationException!", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException!", e);
        }
        return null;
    }


    /**
     * 个性化定制查询，返回一个对象
     *
     * @param vo
     * @return
     */
    public V get(P po) {
        return fromPoToVo(baseMapper.get(po));
    }

    /**
     * 根据某个特定字段返回一个List
     *
     * @param ids         数据库根据此list作 in查询
     * @param dbFieldName 字段名称，与数据库字段对应
     * @return
     */
    public List<V> listVoByField(String dbFieldName, List<Long> ids) {
        return fromPoListToVoList(listPoByField(dbFieldName, ids));
    }

    /**
     * 根据某个特定字段返回一个List
     *
     * @param ids         数据库根据此list作 in查询
     * @param dbFieldName 字段名称，与数据库字段对应
     * @return
     */
    public List<P> listPoByField(String dbFieldName, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        try {
            P po = voClass.newInstance();
            po.setIds(ids);
            po.setQueryListFieldName(dbFieldName);
            return baseMapper.list(po);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    /**
     * 个性化定制查询，返回一个对象list
     *
     * @param vo
     * @return
     */
    public List<V> list(P po) {
        if (po == null) {
            return Lists.newArrayList();
        }
        return fromPoListToVoList(baseMapper.list(po));
    }


    /**
     * 根据某个字段作in 查询，并返回一个以此字段为key的map
     *
     * @param dbFieldName db字段名称
     * @param poFiledName Java对象属性名称
     * @param ids         字段值列表
     * @return
     */
    public Map<String, P> getAsMapByIds(String dbFieldName, String poFiledName, List<Long> ids) {
        List<P> list = listPoByField(dbFieldName, ids);
        return getAsMap(list, poFiledName);
    }

    /**
     * 返回一个以此字段为key的map
     *
     * @param poFiledName Java对象属性名称,如果为空，默认用pkId作为map的key
     * @param list        对象列表
     * @return
     */
    public Map<String, P> getAsMap(List<P> list, String poFiledName) {
        Map<String, P> map = Maps.newHashMap();
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        for (P po : list) {
            try {
                if (StringUtils.isBlank(poFiledName)) {
                    map.put(po.getId().toString(), po);
                } else {
                    map.put(BeanUtil.getProperty(po, poFiledName), po);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return map;
    }

    /**
     * 根据某个字段作in 查询，并返回一个以此字段为key的map
     *
     * @param dbFieldName db字段名称
     * @param poFiledName Java对象属性名称
     * @param ids         字段值列表
     * @return
     */
    public Map<String, List<P>> getListAsMapByIds(String dbFieldName, String poFiledName, List<Long> ids) {
        List<P> list = listPoByField(dbFieldName, ids);
        return getListAsMap(list, poFiledName);
    }

    /**
     * 返回一个以此字段为key的map
     *
     * @param poFiledName Java对象属性名称
     * @param list        对象列表
     * @return
     */
    public Map<String, List<P>> getListAsMap(List<P> list, String poFiledName) {
        Map<String, List<P>> map = Maps.newHashMap();
        if (StringUtils.isBlank(poFiledName)) {
            return map;
        }
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        for (P po : list) {
            try {
                String key = BeanUtil.getProperty(po, poFiledName);
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                List<P> tempList = map.get(key);
                if (CollectionUtils.isEmpty(tempList)) {
                    tempList = Lists.newArrayList();
                    map.put(key, tempList);
                }
                tempList.add(po);
            } catch (Exception e) {
                continue;
            }
        }
        return map;
    }

    /**
     * 根据某个特定字段 拼接in语句删除
     *
     * @param ids         数据库根据此list作 in查询
     * @param dbFieldName 字段名称，与数据库字段对应
     * @return
     */
    public int removeByIds(String dbFieldName, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return -1;
        }
        try {
            P po = poClass.newInstance();
            po.setIds(ids);
            po.setQueryListFieldName(dbFieldName);
            return remove(po);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 个性化定制删除
     *
     * @param
     * @return
     */
    public int remove(P po) {
        if (po == null) {
            return -1;
        }
        return baseMapper.remove(po);
    }


    /**
     * 个性化定制逻辑删除，可以根据字段拼接where逻辑删除
     *
     * @param
     * @return
     */
    public int logicRemove(P po) {
        if (po == null) {
            return -1;
        }
        //初始化插入时间，更新时间
        po.initDelete();
        return baseMapper.logicRemove(po);
    }
}
