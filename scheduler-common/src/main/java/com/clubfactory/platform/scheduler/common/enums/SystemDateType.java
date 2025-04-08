package com.clubfactory.platform.scheduler.common.enums;

/**
 * @author xiejiajun
 */

public enum  SystemDateType {

    /**
     * 1. NF_N : 基准时间加N分钟，格式：yyyy-MM-dd HH:mm:ss
     * 2. NH_N : 基准时间加N小时，格式：yyyy-MM-dd HH:mm:ss
     * 3. ND_N : 基准时间加N天，格式：yyyy-MM-dd HH:mm:ss
     * 4. NW_N : 基准时间加N周，格式：yyyy-MM-dd HH:mm:ss
     * 5. NM_N : 基准时间加N个月，格式：yyyy-MM-dd HH:mm:ss
     * 6. WEEK: WEEK_N，基准时间所在周的周N，格式：yyyy-MM-dd HH:mm:ss
     * 7. RND_N: 基准时间减N天，格式：yyyy-MM-dd HH:mm:ss
     * 8. RNH_N: 基准时间减N小时，格式：yyyy-MM-dd HH:mm:ss
     * 9. RNM_N: 基准时间减N月，格式：yyyy-MM-dd HH:mm:ss
     * 10. NYMD_N: 基准时间加N天，格式：yyyy-MM-dd
     * 11. RNYMD_N: 基准时间减N天，格式：yyyy-MM-dd
     * 12. RNF_N: 基准时间减N分钟，格式：yyyy-MM-dd HH:mm:ss
     */
    NF,NH,ND,NW,NM,WEEK,RNF,RNH,RND,RNM,NYMD,RNYMD,UNKNOWN;

    public static SystemDateType keyOf(String value){
        for (SystemDateType type:SystemDateType.values()){
            if(type.name().equals(value)){
                return type;
            }
        }
        return UNKNOWN;
    }
}
