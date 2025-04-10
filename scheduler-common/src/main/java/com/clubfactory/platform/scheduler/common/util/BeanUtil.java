package com.clubfactory.platform.scheduler.common.util;

import com.clubfactory.platform.scheduler.common.bean.Pager;
import com.google.common.base.CaseFormat;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;

public class BeanUtil extends BeanUtils {
    public BeanUtil() {
    }

    public static void copyBeanNotNull2Bean(Object databean, Object tobean) {
        if (databean instanceof Map) {
            copyMap2Bean(tobean, (Map)databean);
        } else {
            PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(databean);

            for(int i = 0; i < origDescriptors.length; ++i) {
                String name = origDescriptors[i].getName();
                if (!"class".equals(name) && PropertyUtils.isReadable(databean, name) && PropertyUtils.isWriteable(tobean, name)) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty(databean, name);
                        Object toType = PropertyUtils.getPropertyType(tobean, name);
                        if (value != null) {
                            if (value instanceof String) {
                                value = ((String)value).trim();

                                try {
                                    Class clazz = Class.forName(toType.toString().substring(6, toType.toString().length()));
                                    if (clazz.isEnum()) {
                                        Enum enumInfo = Enum.valueOf(clazz, (String)value);
                                        value = enumInfo;
                                    }
                                } catch (Exception var9) {
                                }
                            }

                            copyProperty(tobean, name, value);
                        }
                    } catch (IllegalArgumentException var10) {
                    } catch (Exception var11) {
                    }
                }
            }
        }

    }

    public static Map<String, Object> copyBean2Map(Object bean, Map<String, Object> map) {
        if (null == map) {
            map = new HashMap();
        }

        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);

        for(int i = 0; i < pds.length; ++i) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();

            try {
                if (!"class".equals(propname)) {
                    Object propvalue = PropertyUtils.getSimpleProperty(bean, propname);
                    if (null != propvalue) {
                        ((Map)map).put(propname, propvalue);
                    }
                }
            } catch (IllegalAccessException var7) {
            } catch (InvocationTargetException var8) {
            } catch (NoSuchMethodException var9) {
            }
        }

        return (Map)map;
    }

    public static Map<String, Object> copyBeanCamel2MapUnder(Object bean, Map<String, Object> map) {
        return copyBeanCamel2MapUnder(bean, map, (List)null);
    }

    public static Map<String, Object> copyBeanCamel2MapUnder(Object bean, Map<String, Object> map, List<String> excludeFields) {
        if (null == map) {
            map = new HashMap();
        }

        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);

        for(int i = 0; i < pds.length; ++i) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();

            try {
                if (!"class".equals(propname) && (!CollectionUtils.isNotEmpty(excludeFields) || !excludeFields.contains(propname))) {
                    Object propvalue = PropertyUtils.getSimpleProperty(bean, propname);
                    if (null != propvalue) {
                        ((Map)map).put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propname), propvalue);
                    }
                }
            } catch (IllegalAccessException var8) {
            } catch (InvocationTargetException var9) {
            } catch (NoSuchMethodException var10) {
            }
        }

        return (Map)map;
    }

    public static void copyMap2Bean(Object bean, Map<String, Object> properties) {
        if (bean != null && properties != null) {
            Iterator entrys = properties.entrySet().iterator();

            while(true) {
                Entry entry;
                String name;
                do {
                    if (!entrys.hasNext()) {
                        return;
                    }

                    entry = (Entry)entrys.next();
                    name = (String)entry.getKey();
                } while(name == null);

                Object value = entry.getValue();

                try {
                    Class<?> clazz = PropertyUtils.getPropertyType(bean, name);
                    if (null != clazz) {
                        String className = clazz.getName();
                        if (!className.equalsIgnoreCase("java.sql.Timestamp") || value != null && !value.equals("")) {
                            setProperty(bean, name, value);
                        }
                    }
                } catch (NoSuchMethodException var8) {
                } catch (Exception var9) {
                }
            }
        }
    }

    public static void copyMap2Bean(Object bean, Map properties, String defaultValue) {
        if (bean != null && properties != null) {
            Iterator entrys = properties.entrySet().iterator();

            while(true) {
                Entry entry;
                String name;
                do {
                    if (!entrys.hasNext()) {
                        return;
                    }

                    entry = (Entry)entrys.next();
                    name = (String)entry.getKey();
                } while(name == null);

                Object value = entry.getValue();

                try {
                    Class clazz = PropertyUtils.getPropertyType(bean, name);
                    if (null != clazz) {
                        String className = clazz.getName();
                        if (!className.equalsIgnoreCase("java.sql.Timestamp") || value != null && !value.equals("")) {
                            if (className.equalsIgnoreCase("java.lang.String") && value == null) {
                                value = defaultValue;
                            }

                            setProperty(bean, name, value);
                        }
                    }
                } catch (NoSuchMethodException var9) {
                } catch (Exception var10) {
                }
            }
        }
    }

    public static void main(String[] args) {
        Pager pager = new Pager();
        pager.setPageNo(1);
        pager.setPageSize(10);
        Map<String, Object> map = new HashMap();
        copyBeanCamel2MapUnder(pager, map);
        Iterator var3 = map.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<String, Object> entry = (Entry)var3.next();
            System.out.println((String)entry.getKey() + " " + entry.getValue());
        }

    }
}
