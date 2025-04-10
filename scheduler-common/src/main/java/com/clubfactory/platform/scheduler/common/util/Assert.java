package com.clubfactory.platform.scheduler.common.util;

import java.util.Collection;

import com.clubfactory.platform.scheduler.common.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class Assert {
    private Assert() {
    }

    public static void notGreaterThan(int org, int to) {
        if (org > to) {
            throw new BizException(-1, String.format("%s  不能大于 %s", org, to));
        }
    }

    public static void notGreaterThan(int org, int to, String msg) {
        if (org > to) {
            throw new BizException(-1, String.format("%s  %s  不能大于 %s", msg, org, to));
        }
    }

    public static void notNull(Object obj) {
        if (obj == null) {
            throw new BizException(-1, " 对象 为null");
        } else if (obj instanceof String && StringUtils.isEmpty(obj.toString())) {
            throw new BizException(String.format("对象 不存在"));
        }
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BizException(String.format("参数 %s 不存在", message));
        }
    }

    public static void nonNull(Object obj, String message) {
        if (obj == null) {
            throw new BizException(message);
        }
    }

    public static void notBlank(String obj) {
        notBlank(obj, "");
    }

    public static void notBlank(String obj, String message) {
        if (StringUtils.isBlank(obj)) {
            throw new BizException(String.format("参数 %s 不能为空", message));
        }
    }

    public static void nonBlank(String obj, String message) {
        if (StringUtils.isBlank(obj)) {
            throw new BizException(message);
        }
    }

    public static void collectionNotEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BizException(String.format("param %s cannot empty", message));
        }
    }

    public static void collectionNonEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BizException(message);
        }
    }

    public static void isTrue(Boolean expression, String message) {
        if (!BooleanUtils.isTrue(expression)) {
            throw new BizException(message);
        }
    }

    public static void isFalse(Boolean expression, String message) {
        if (BooleanUtils.isTrue(expression)) {
            throw new BizException(message);
        }
    }

    public static void error(String message) {
        throw new BizException(-1, message);
    }
}
