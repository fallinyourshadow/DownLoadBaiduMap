package com.ray.test;

import java.io.File;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 下载图片
 */
public class Hellow {
    public static void main(String[] args)
            throws Exception {
        BDTask.startDownload();
    }
}
 
/**
 * 线程池下载图片
 */
class BDTask implements Runnable {
//    正常百度地图
//    static String link = "http://online3.map.bdimg.com/onlinelabel/?qt=tile&x={x}&y={y}&z={z}&styles=pl&udt=20170712&scaler=1&p=1";
 //   static String link = "http://shangetu1.map.bdimg.com/it/u=x={x};y={y};z={z};v=009;type=sate&fm=46&udt=20170712";//卫星图
//static String link = "http://online2.map.bdimg.com/tile/?qt=tile&x={x}&y={y}&z={z}&styles=sl&udt=20170712";//道路图


static String link = "https://tile.openstreetmap.org/${z}/${x}/${y}.png";

    //午夜蓝版
//    static String link ="http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20180711&scale=1&ak=0F7691e465f5d7d161a4771f48ee38ff&styles=t%3Awater%7Ce%3Aall%7Cc%3A%23021019%2Ct%3Ahighway%7Ce%3Ag.f%7Cc%3A%23000000%2Ct%3Ahighway%7Ce%3Ag.s%7Cc%3A%23147a92%2Ct%3Aarterial%7Ce%3Ag.f%7Cc%3A%23000000%2Ct%3Aarterial%7Ce%3Ag.s%7Cc%3A%230b3d51%2Ct%3Alocal%7Ce%3Ag%7Cc%3A%23000000%2Ct%3Aland%7Ce%3Aall%7Cc%3A%2308304b%2Ct%3Arailway%7Ce%3Ag.f%7Cc%3A%23000000%2Ct%3Arailway%7Ce%3Ag.s%7Cc%3A%2308304b%2Ct%3Asubway%7Ce%3Ag%7Cl%3A-70%2Ct%3Abuilding%7Ce%3Ag.f%7Cc%3A%23000000%2Ct%3Aall%7Ce%3Al.t.f%7Cc%3A%23857f7f%2Ct%3Aall%7Ce%3Al.t.s%7Cc%3A%23000000%2Ct%3Abuilding%7Ce%3Ag%7Cc%3A%23022338%2Ct%3Agreen%7Ce%3Ag%7Cc%3A%23062032%2Ct%3Aboundary%7Ce%3Aall%7Cc%3A%231e1c1c%2Ct%3Amanmade%7Ce%3Ag%7Cc%3A%23022338%2Ct%3Apoi%7Ce%3Aall%7Cv%3Aoff%2Ct%3Aall%7Ce%3Al.i%7Cv%3Aoff%2Ct%3Aall%7Ce%3Al.t.f%7Cv%3Aon%7Cc%3A%232da0c6";
    static String rootDir = "f:/ganshuweixin";
 
    int i;  //x坐标
    int j;  //y坐标
    int z;  //缩放级别
 
    static volatile Integer c = 0;//成功数
    static volatile Integer fail = 0;//失败数量
 
    public BDTask(String link, int i, int j, int z) {
        this.link = link;
        this.i = i;
        this.j = j;
        this.z = z;
 
    }
 
    public static void startDownload() {
        ThreadPoolExecutor threadPoolExecutor = null;
        long start = 0L;
        for(Level c : Level.values()){
            int z = c.getLevel();
            int xmin = c.getX_min();
            int xmax = c.getX_max();
            int ymin = c.getY_min();
            int ymax = c.getY_max();
            start = System.currentTimeMillis();    //开始时间
            threadPoolExecutor = new ThreadPoolExecutor(2, 4, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            for (int i = xmin; i <= xmax; i++) {   //循环X
                for (int j = ymin; j <= ymax; j++) {    //循环Y
                    threadPoolExecutor.execute(new BDTask(link, i, j, z));  //下载图片
                    //new Thread(new BDTask(link,i,j,z)).start();    //此种方法会一直创建线程导致死机
                        /*try {
                            URL url = new URL(link.replace("{x}", i + "").replace("{y}", j + "").replace("{z}", z + ""));
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setConnectTimeout(100);
                            conn.connect();
                            InputStream in = conn.getInputStream();
                            File dir = new File("d:/mybaidumapdownload1/tiles/" + z + "/" + i);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File("d:/mybaidumapdownload1/tiles/" + z + "/" + i + "/" + j + ".jpg");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            OutputStream out = new FileOutputStream(file);
                            byte[] bytes = new byte[1024 * 20];
                            int len = 0;
                            while ((len = in.read(bytes)) != -1) {
                                out.write(bytes, 0, len);
                            }
                            out.close();
                            in.close();
                            //System.out.println("已成功下载:" + z + "_" + i + "_" + j + ".jpg");
                            c++;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            fail++;
                        }*/
                }    //循环Y结束
                System.out.println("x坐标已经下载到："+i);
            }   //循环X结束
        }
        System.out.println("第："+c+"缩放等级已经下载完毕---------------------------------------------------------------------------");
 
 
        threadPoolExecutor.shutdown();   //关闭线程池
        while (!threadPoolExecutor.isTerminated()) {
        }     //所有任务被执行完毕时继续往下执行
        System.out.println("-------用时-------:" + (System.currentTimeMillis() - start));
        System.out.println("共下载:   " + c + "   张");
        System.out.println("失败:   " + fail + "   张");
    }
 
    public void run() {
        try {
            URL url = new URL(link.replace("{x}", i + "").replace("{y}", j + "").replace("{z}", z + ""));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(500);
            conn.connect();
            InputStream in = conn.getInputStream();
 
            File file = new File(rootDir+"/tiles/" + z + "/" + i + "/" + j + ".png");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
 
            OutputStream out = new FileOutputStream(file);
            byte[] bytes = new byte[1024 * 20];
            int len = 0;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.close();
            in.close();
            synchronized (fail) {
                c++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("z:"+z+"x:"+i+"y:"+j+"下载失败！");
            synchronized (c) {
                fail++;
            }
        }
    }
}
 
/**
 * 枚举类型
 * 等级 x最小 x最大 y最小 y最大
 */
enum Level {
	Level_0(2, -4, 4, -4, 4);
//      Level_3(3, 0, 2, 0, 1);
 //   Level_4(4, 0, 4, 0, 3);
 //   Level_5(5, 0, 9, 0, 7);
  //  Level_6(6, 0, 19, 0, 14);
   // Level_7(7, 0, 38, 0, 29);
    //Level_8(8, 0, 77, 0, 59);
    //Level_9(9, 0, 160, 0, 120);
    //  Level_10(10, 0, 248, 0, 109);
//    Level_11(11, 370, 370, 116, 161);
//    Level_12(12, 670, 707, 42,431);
//    Level_13(13, 1476, 1477, 466, 642);
// 	Level_14(14, 2758, 2758, 932, 1283);
//    Level_15(15, 5897, 5897, 2361, 2361);
//    Level_16(16, 12939, 13036, 3680, 3850),
//    Level_17(17, 25878, 26073, 7360, 7670),
//    Level_18(18, 51757, 52146, 14720,15400),
//    Level_19(19, 103514, 104292, 29400,30700);
 
    private int level;
    private int x_min;
    private int x_max;
    private int y_min;
    private int y_max;
 
    Level(int level, int x_min, int x_max, int y_min, int y_max) {
        this.level = level;
        this.x_min = x_min;
        this.x_max = x_max;
        this.y_min = y_min;
        this.y_max = y_max;
    }
 
    public int getLevel() {
        return level;
    }
 
    public int getX_min() {
        return x_min;
    }
 
    public int getX_max() {
        return x_max;
    }
 
    public int getY_min() {
        return y_min;
    }
 
    public int getY_max() {
        return y_max;
    }
}