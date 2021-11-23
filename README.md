# SmartBluetoothDemo
1.说明
 1.1 本文档用于说明妙联蓝牙WIFI配网SDK Android版本接口之间的关系以及接口调用顺序，对蓝牙WIFI 配网SDK Android版本各接口都有详细的说明。
2.名词解释
kindId	设备品类ID目前使用于妙联APP、或者接入妙联平台APP
modelId	设备型号ID目前使用于妙联APP、或者接入妙联平台APP
deviceName	蓝牙设备名称
macAddress	蓝牙设备MAC地址
mVersion	蓝牙模组版本号
mCode	蓝牙模组配网方式，目前使用于妙联APP
rssi	蓝牙信号强度
ssid	路由器名称
password	路由器密码
delayMillis	配网超时时间

3.功能介绍
蓝牙WIFI搜索	用于搜索带有妙联标志的蓝牙设备
蓝牙WIFI配网	手机端将蓝牙设备连接上路由器并连接上平台
蓝牙串口通讯	可实现手机端和蓝牙设备的数据通讯

4.开发指南
4.1工程配置
  环境准备
支持 Android Studio 3.0 以上 支持 JDK 7.0 以上版本

支持 Android 手机系统 4.4 以上版本
4.2获得SDK
Demo下载地址
https://github.com/Miotlink/SmartBluetoothDemo.git

Gradle依赖地址  
dependencies {

   implementation 'com.miotlink.bluetooth:ISmartBluetoothSDK:1.0.11'

}

配置build.gradle
allprojects {
    repositories {
        maven {
            url 'https://maven.aliyun.com/repository/public'
        }
        maven {
            credentials {
                username '5f0c1af14c4e70fdbf9e26c6'
                password 'd7I)7(NdDOcw'
            }
            url 'https://packages.aliyun.com/maven/repository/2016367-release-GHmELg/'
        }
        maven {
            credentials {
                username '5f0c1af14c4e70fdbf9e26c6'
                password 'd7I)7(NdDOcw'
            }
            url 'https://packages.aliyun.com/maven/repository/2016367-snapshot-yufnfx/'
        }
    }
}


4.3配置权限
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permissionandroid:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

4.4SDK初始化
一般建议在application中初始化

MiotSmartBluetoothSDK.setDebug(true);//true日志打印//false 不打印

MiotSmartBluetoothSDK.getInstance().init(this, "", new SmartListener() {
    @Override
    public void onSmartListener(int i, String s, String s1) throws Exception {

    }
});

4.5调用方法
4.5.1蓝牙设备搜索
调用方法
MiotSmartBluetoothSDK.getInstance().startScan(scanCallBack);
回调函数
ILinkBlueScanCallBack scanCallBack=new ILinkBlueScanCallBack() {
    @Override
    public void onScanDevice(BleModelDevice bleModelDevice) throws Exception {
        
    }
};


4.5.2停止搜索
调用方法
MiotSmartBluetoothSDK.getInstance().onStopScan();

4.5.3蓝牙设备配网
调用方法	
MiotSmartBluetoothSDK.getInstance().startSmartConfig(macCode,ssid,password,60,smartConfigListener);
macCode	蓝牙设备MAC地址
ssid	路由器热点名称
password	路由器热点密码
delayMillis	配网超时时间 delayMillis>60
回调函数	
ILinkSmartConfigListener smartConfigListener=new ILinkSmartConfigListener() {
    @Override
    public void onLinkSmartConfigListener(int i, String s, String s1) throws Exception {
        
    }

    @Override
    public void onLinkSmartConfigTimeOut(int i, String s) throws Exception {

    }
};


4.5.4停止配网
MiotSmartBluetoothSDK.getInstance().onDisConnect();




4.5.5蓝牙串口通讯

MiotSmartBluetoothSDK.getInstance().send(macCode,byte[],smartNotifyListener)

SmartNotifyListener smartNotifyListener=new SmartNotifyListener() {
    @Override
    public void onSmartNotifyListener(int i, String s, BleEntityData bleEntityData) throws Exception {
        
    }
};

4.5.6销毁
App应用销毁调用该方法

MiotSmartBluetoothSDK.getInstance().onDestory();


4.6错误码
7001	设备开始配网
7002	设备配网中
7003	设备已连上路由器,未连接到平台 
7015	连接平台、配置成功
7255	配置失败、检查路由器账户、密码
7010	设备与手机断开连接，请确保设备与手机距离靠近

4.7注意事项
4.7.1Android操作系统8.0以上需打开开启位置权限
4.7.2Android操作系统8.1以上需打开位置服务
4.7.3仅支持Android 4.4以上的操作系统
5.联系我们
电话：0571-86797400
邮箱：qiaozhuang@miotlink.com
地址：浙江省杭州市滨江区伟业路1号高新软件园5号楼
网址：www.miotlink.com
