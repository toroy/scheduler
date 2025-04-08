package com.clubfactory.platform.scheduler.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author xiejiajun
 */
public class CliParamSplitter {

    /**
     * 将命令行参数按空格切分成List
     * @param paramText
     * @return
     */
    public static List<String> splitParams(String paramText) {
        if (StringUtils.isBlank(paramText)) {
            return Lists.newArrayList();
        }

        List<String> params = Lists.newArrayList();
        int lastIdx = StringUtils.isNotBlank(paramText) ? paramText.length() - 1 : 0;
        StringBuilder psb = new StringBuilder();

        boolean singleQuoteString = false;
        boolean doubleQuoteString = false;

        for (int idx = 0; idx <= lastIdx; idx++) {
            char ch = paramText.charAt(idx);
            String chText = paramText.substring(idx, idx + 1);

            // 单引号模式切换
            if (ch == '\'') {
                if (singleQuoteString) {
                    singleQuoteString = false;
                } else if (!doubleQuoteString) {
                    singleQuoteString = true;
                }
            }

            // 双引号模式切换
            if (ch == '"') {
                if (doubleQuoteString && idx > 0) {
                    doubleQuoteString = false;
                } else if (!singleQuoteString) {
                    doubleQuoteString = true;
                }
            }

            if (StringUtils.isBlank(chText) && !singleQuoteString && !doubleQuoteString) {
                String param = psb.toString().trim();
                if (StringUtils.isNotBlank(param)) {
                    params.add(param);
                    psb = new StringBuilder();
                }
            } else if (idx == lastIdx) {
                psb.append(ch);
                String param = psb.toString().trim();
                if (StringUtils.isNotBlank(param)) {
                    params.add(param);
                    psb = new StringBuilder();
                }
            } else {
                psb.append(ch);
            }
        }

        return params;

    }

    public static void main(String[] args) {
        String paramText = "";
        List<String> params = splitParams(paramText);
        Objects.requireNonNull(params).forEach(System.out::println);
    }
}
