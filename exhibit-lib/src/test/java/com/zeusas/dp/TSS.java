package com.zeusas.dp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

public class TSS {
    // 默认1个大小  
    private static HashMap<String, AtomicInteger> tssCache  
        = new HashMap<String, AtomicInteger>(1);  
      
    private static final ReentrantLock lock = new ReentrantLock();  
  
    // 因为有锁，所以是变成了线程安全的，省去每次 new 的消耗，耗时降低约一半  
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");  
      
    public static String getTimeStampSequence() {  
          
        String timestamp = null;  
        String inc = null;  
          
        lock.lock();  
        try {  
              
            timestamp = sdf.format(new Date());  
            AtomicInteger value = tssCache.get(timestamp);  
            if(value == null) {  
                tssCache.clear();  
                int defaultStartValue = 0;  
                tssCache.put(timestamp, new AtomicInteger(defaultStartValue));  
                inc = String.valueOf(defaultStartValue);  
            } else {  
                inc = String.valueOf(value.addAndGet(1));  
            }  
        } finally {  
            lock.unlock();  
        }  
          
        return timestamp + StringUtils.leftPad(inc, 4, '0');  
    }  
  
      
    public static void main(String[] args) throws Exception {  
          
      for (int i = 0; i < 1000; i++) {  
          new Thread(new Runnable() {  
                
              @Override  
              public void run() {  
                  for (int j = 0; j < 10; j++) {  
                      System.out.println(TSS.getTimeStampSequence());  
                  }  
              }  
          }).start();  
      }  
  
          
        // 统计重复  
     /*   HashSet<String> set = new HashSet<String>();  
        BufferedReader br = new BufferedReader(  
                new InputStreamReader(new FileInputStream("C:/Users/IT_dp/Desktop/1.txt")));  
        String str = br.readLine();  
        while(str != null) {  
            if(set.contains(str)) {  
                System.out.println(str);  
            } else {  
                set.add(str);  
            }  
            str = br.readLine();  
        }  
  
        br.close();  
  */
    }  
}
