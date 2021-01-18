package com.zeusas.dp;

import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;

public class TestGenId {

	public static void main(String[] args) throws Exception {  
        
        /** 
         * 项目：交易单分表 
         *  
         *  需求 
         *  查询需求： 1. userId维度 
         *           2. 产品维度 
         *           3. 商户维度 
         *           4. 时间区间维度 
         *   
         *  预计订单量： 
         *        一单平均10000， 一年交易额5000亿， 需要成功订单量 = 500000000000 / 10000 = 50000000 5000万订单 
         *       购买加回款应该是1亿订单量, 所以， 单表2000万， 一年需要5张表 
         *   
         *      最后扩展64库 + 64表， 共64*64 = 4096表， 4096 * 2000万 = 819亿订单够用了， 819亿 * 10000 = 8190000亿  819万亿，够用了 
         *   
         *  全局唯一主键： 
         *      15位时间戳 + 自增序号四位 + 机器后两段IP，6位 + 备用1位 + 分库信息两位 + 分表信息两位  共30位，  回款改造前 
         *      15位时间戳 + 自增序号四位 + 机器后两段IP，6位 + 备用3位 + 分库信息两位 + 分表信息两位  共32位，  回款改造后 
         *       
         *      单JVM支持最多1s  1000 * 9999 = 9999000, 999万9千笔订单，后续还可以扩展。 
         *   
         *  分库规则： 
         *      寻找到数据库  (userId/100) % 64 + 1 找到数据库    订单最多64个库    目前一个库   二分法裂变扩容 
         *  分表规则： 
         *      寻找到表信息  userId % 64 + 1 找到表信息    一个库最多64个表   目前分8张表    以后二分法裂变扩容 
         *   
         *  迁移规则： 
         *      迁移方案同步写， 目前用动态表名， 以后分表中间件稳定后， 迁移过去 
         *   
         *  查询改造： 
         *      原接口不变，对用户无感知， 底层钩子遍历 
         */  
  
        // 只获取本地局域网IP即可  
        String ip = InetAddress.getLocalHost().getHostAddress();  
        String[] ipArray = ip.split("\\.");  
  
        final String lastTwoPhaseIp = StringUtils.rightPad(ipArray[2], 3, '0')  
                + StringUtils.leftPad(ipArray[3], 3, '0');  
          
        for (int i = 0; i < 100000; i++) {  
            new Thread(new Runnable() {  
                  
                @Override  
                public void run() {  
                    // TSS commons工具类  
                    String tss = TSS.getTimeStampSequence();  
                    String id = tss + lastTwoPhaseIp + "000" + "01" + "08";  
                    System.out.println(id);  
                }  
            }).start();  
        }  
    }  
      
}
