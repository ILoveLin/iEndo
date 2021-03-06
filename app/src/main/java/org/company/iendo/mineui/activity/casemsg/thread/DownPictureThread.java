package org.company.iendo.mineui.activity.casemsg.thread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.company.iendo.bean.beandb.image.DimImageBean;
import org.company.iendo.bean.beandb.image.ImageListDownDBBean;
import org.company.iendo.bean.beandb.image.ReallyImageBean;
import org.company.iendo.mineui.activity.casemsg.CaseDetailMsgActivity;
import org.company.iendo.util.LogUtils;
import org.company.iendo.util.SDFileUtil;
import org.company.iendo.util.SharePreferenceUtil;
import org.company.iendo.util.db.ImageDBUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;

/**
 * LoveLin
 * <p>
 * Describe  下载图片的线程
 *
 * http格式的url
 * http://192.168.66.42:8008/Images/3915/thumb/001.jpg
 *
 * http://192.168.66.42:8008/Videos/3874/444420210318164613550.mp4
 */
public class DownPictureThread implements Runnable {
    public HashMap<String, String> mPicMap = new HashMap<>();
    public ArrayList<String> mDataList = new ArrayList<>();
    private ImageListDownDBBean mImageListBean;
    private ArrayList<String> dimImageList;
    private ArrayList<String> reallyPathList;
    private SmbFile mRootFolder;
    private File toLocalFile;
    private Context mContext;
    private String itemID;
    private Boolean exists;
    private String downTag;
    private String ip;

    public DownPictureThread(Context mContext, String itemID, String downTag, boolean exists) {
        this.mContext = mContext;
        this.itemID = itemID;
        this.downTag = downTag;
        //file.exits 如果是false表示,不存在,说明没有进入过03界面,本地没有缓存,所以需要开启线程下载,
        //如果是true，则直接读取本地文件写入数据库
        this.exists = exists;
    }

    @Override
    public void run() {
        LogUtils.e("缓存=本地SD卡里面是否有缓存==exists===" + exists);
        if (!exists) {
            LogUtils.e("缓存===本地SD卡bu存在=== 开始下载===" + exists);
            ip = (String) SharePreferenceUtil.get(mContext, SharePreferenceUtil.Current_IP, "");
            String path = "smb://" + ip + "/ImageData/Images/" + CaseDetailMsgActivity.getCurrentID() + "/thumb/";
            try {
                initSmb(path);
                if (mRootFolder.exists()) {  //SMB服务器存在图片
                    SmbFile[] smbFiles = mRootFolder.listFiles(); // smb://192.168.128.146/ImageData/Images/4033/thumb/
                    LogUtils.e("缓存===本地SD卡bu存在=== smb服务器====smbFiles.length===" + smbFiles.length);
                    for (int i = 0; i < smbFiles.length; i++) {
                        //添加每个图片名字的前缀
                        mDataList.add(smbFiles[i].getName().split("\\.")[0]);
                        toLocalFile = new File(Environment.getExternalStorageDirectory() +
                                "/MyData/Images/" + CaseDetailMsgActivity.getCurrentID());
                        File toFile = new File(Environment.getExternalStorageDirectory() +
                                "/MyData/Images/" + CaseDetailMsgActivity.getCurrentID() + "/thumb/");
                        String remoteUrl = "smb://cmeftproot:lzjdzh19861207@" + ip + "/";

                        if (!toFile.exists()) {   //不存在创建
                            toFile.mkdirs();
                            SDFileUtil.downLoadFileToFolder(remoteUrl, "ImageData/Images/" + CaseDetailMsgActivity.getCurrentID() + "/thumb/",
                                    smbFiles[i].getName(), toFile.getAbsolutePath());

                        } else {
                            if (toFile.listFiles().length==smbFiles.length) { //SMB服务器图片数量和本地不同再次下载
                                SDFileUtil.downLoadFileToFolder(remoteUrl, "ImageData/Images/" + CaseDetailMsgActivity.getCurrentID() + "/thumb/",
                                        smbFiles[i].getName(), toFile.getAbsolutePath());
                            }
                        }
                    }
                    if (mListener != null) {
                        mListener.onDimPictureDownOK();
                    }
                    //获取到下载好的模糊图本地路径list
                    getLocalImagePathList();
                    //开始下载原图到本地
                    startGetReallyPic();
                    if (mListener != null) {
                        mListener.onReallyPictureDownOK();
                    }
                    //存入数据库
                    insertFilePathToDB();
                    if (mListener != null) {
                        mListener.onOverDownOK();
                    }
                } else {      //SMB服务器bu存在图片
                    if (mListener != null) {
                        mListener.onSmbEmptyPicture();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //获取本地模糊图
            ArrayList<String> localImagePathList = getLocalImagePathList();
            for (int i = 0; i < localImagePathList.size(); i++) {
                LogUtils.e("缓存===存在载=缓存里面拿=====localImagePathList====" + localImagePathList.size());
            }
            //获取本地原图
            getLocalReallyPathList();
            LogUtils.e("缓存===存在载=缓存里面拿==" + exists);
            //存入数据库
            insertFilePathToDB();
            if (mListener != null) {
                mListener.onOverDownOK();
            }
        }
    }

    @SuppressLint("NewApi")
    private ArrayList<String> getLocalReallyPathList() {
        ArrayList<String> mLocalReallyPathList = new ArrayList<String>();   //存和模糊图名字相同的原图集合
        HashMap<String, String> mPicMap = new HashMap<>();             //所有原图的map key是名字,value是path
        ArrayList<String> list = new ArrayList<>();                    //存模糊图的前缀名字
        // /storage/emulated/0/MyData/Images/4012/2020110317581095.jpg
        dimImageList.stream().forEach((String str) -> {
            String[] split = str.split("/");
            String s = split[split.length - 1];
            String s1 = s.split("\\.")[0];
            //此时添加图片名字 .前面的名字
            list.add(s1);

        });
        File localReallyFile = new File(Environment.getExternalStorageDirectory() +
                "/MyData/Images/" + itemID);
        File[] files = localReallyFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {//不是文件夹就是图片了
                String PicName = files[i].getName();
                File toFile = new File(Environment.getExternalStorageDirectory() +
                        "/MyData/Images/" + CaseDetailMsgActivity.getCurrentID());
                //截取20000236.bmp   的20000236部分,到时候需要对比模糊图相同的名字才显示
                String mPicKeyName = PicName.split("\\.")[0];
                String mPicValuePath = toFile + "/" + PicName;
                mPicMap.put(mPicKeyName, mPicValuePath);
            }
        }

        if (mPicMap != null && mPicMap.size() != 0 || list != null && list.size() != 0) {
            Set<Map.Entry<String, String>> entriesList = mPicMap.entrySet();
            entriesList.stream().forEach((entry) -> {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(entry.getKey())) {
                        mLocalReallyPathList.add(entry.getValue() + "");
                        Log.e("========root=====", "local==entry.getValue()==" + "" + entry.getValue());
                    }
                }
            });
            return mLocalReallyPathList;
        }
        return mLocalReallyPathList;
    }

    @SuppressLint("NewApi")
    public ArrayList<String> getLocalImagePathList() {
        dimImageList = new ArrayList<>();
        File localFile = new File(Environment.getExternalStorageDirectory() +
                "/MyData/Images/" + itemID + "/thumb");
        if (!localFile.exists()) {
            localFile.mkdirs();
        }
        File[] files = localFile.listFiles();
        Stream.of(files).forEach(f -> dimImageList.add(f.getAbsolutePath()));
        for (int i = 0; i < dimImageList.size(); i++) {

        }

        return dimImageList;
    }

    @SuppressLint("NewApi")
    public ArrayList getViewPagerPicturePath() {
        reallyPathList = new ArrayList<String>();
        if (mPicMap != null && mPicMap.size() != 0 || mDataList != null && mDataList.size() != 0) {
            Set<Map.Entry<String, String>> entriesList = mPicMap.entrySet();
            entriesList.stream().forEach((entry) -> {
                for (int i = 0; i < mDataList.size(); i++) {
                    if (mDataList.get(i).equals(entry.getKey())) {
                        reallyPathList.add(entry.getValue() + "");
                        Log.e("========root=====", "local==entry.getValue()==" + "" + entry.getValue());
                    }
                }
            });
            return reallyPathList;
        }
        return reallyPathList;

    }

    private void insertFilePathToDB() {
        //创建数据库Bean对象
        mImageListBean = new ImageListDownDBBean();
        //设置存入图片对象的 用户ID
        LogUtils.e("====DP==DimPath==开启DB数据库线程=====");
        LogUtils.e("====DP==DimPath==模糊===之前=用户ID=" + CaseDetailMsgActivity.getCurrentID());
        mImageListBean.setItemID(CaseDetailMsgActivity.getCurrentID());
        mImageListBean.setTag(CaseDetailMsgActivity.getCurrentID());
        mImageListBean.setDownTag("1");  //1表示在线用户下载到本地存入到数据库
        ArrayList<DimImageBean> list00 = new ArrayList<>();
        for (int i = 0; i < dimImageList.size(); i++) {
            DimImageBean dimBean = new DimImageBean();
            dimBean.setDimFilePath(dimImageList.get(i));
            LogUtils.e("====DP==DimPath==模糊===之前==" + dimImageList.get(i));
            list00.add(dimBean);
        }
        //存入模糊图片路径的list
        mImageListBean.setMDimImageList(list00);

        //然后开始存原图
        ArrayList ReallyPathList = null;
        if (!exists) {   //exists ==false表示没有进入03界面,需要开启线程下载
            ReallyPathList = getViewPagerPicturePath();
        } else {        //exists ==true 表示进入过03界面,下载过了,直接本地SD卡取
            ReallyPathList = getLocalReallyPathList();
        }
        ArrayList<ReallyImageBean> list01 = new ArrayList<>();
        LogUtils.e("====DP==ReallyPath=====ReallyPathList==" + ReallyPathList.size());
        for (int i = 0; i < ReallyPathList.size(); i++) {
            ReallyImageBean reallyImageBean = new ReallyImageBean();
            reallyImageBean.setReallyFilePath((String) ReallyPathList.get(i));
            LogUtils.e("====DP==ReallyPath==原图===之前==" + (String) ReallyPathList.get(i));
            list01.add(reallyImageBean);
        }
        //存入原图片路径的list
        mImageListBean.setMReallyImageList(list01);

        //存入数据库
        ImageDBUtils.insertOrReplaceData(mImageListBean);
        boolean isExist = ImageDBUtils.queryListIsExist(CaseDetailMsgActivity.getCurrentID());
        LogUtils.e("====DP==ReallyPath==DB===是否存在==" + isExist);
    }


    //开始下载原图
    private void startGetReallyPic() throws
            MalformedURLException, UnknownHostException, SmbException {
        ip = (String) SharePreferenceUtil.get(mContext, SharePreferenceUtil.Current_IP, "");
        String picRootPath = "smb://" + ip + "/ImageData/Images/" + CaseDetailMsgActivity.getCurrentID() + File.separator;
        LogUtils.e("====picture======length=" + picRootPath);
        initSmb(picRootPath);
        SmbFile[] smbFiles = mRootFolder.listFiles();
        LogUtils.e("====DP==DimPath==开始下载=====");
        for (int i = 0; i < smbFiles.length; i++) {
            if (!smbFiles[i].isDirectory()) {   //不是文件夹,是原图,才读取
                String PicName = smbFiles[i].getName();
                File toFile = new File(Environment.getExternalStorageDirectory() +
                        "/MyData/Images/" + CaseDetailMsgActivity.getCurrentID());
                String mPicKeyName = PicName.split("\\.")[0];  //截取20000236.bmp   的20000236部分,到时候需要对比模糊图相同的名字才显示
                String mPicValuePath = toFile + "/" + PicName;
                mPicMap.put(mPicKeyName, mPicValuePath);
                String remoteUrl = "smb://cmeftproot:lzjdzh19861207@" + ip + "/";
                SDFileUtil.downLoadReallyFileToFolder(remoteUrl, "ImageData/Images/" + CaseDetailMsgActivity.getCurrentID() + "/",
                        PicName, toFile.getAbsolutePath());
                LogUtils.e("====DP==DimPath==下载一条===PicName==" + PicName);
            }
        }
        if (mListener != null) {
            mListener.onReallyPictureDownOK();
            mListener.onOverDownOK();
        }

    }


    private void initSmb(String rootPath) throws
            UnknownHostException, SmbException, MalformedURLException {
        ip = (String) SharePreferenceUtil.get(mContext, SharePreferenceUtil.Current_IP, "");
        System.setProperty("jcifs.smb.client.dfs.disabled", "true"); //true false
        System.setProperty("jcifs.smb.client.soTimeout", "1000000");
        System.setProperty("jcifs.smb.client.responseTimeout", "30000");
        String username = "cmeftproot";
        String password = "lzjdzh19861207";
        Log.e("========root==rootPath=", rootPath);
        UniAddress mDomain = UniAddress.getByName(ip);
        NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(ip, username, password);
        SmbSession.logon(mDomain, mAuthentication);
        mRootFolder = new SmbFile(rootPath, mAuthentication);
    }


    public onThreadStatueListener mListener;

    public void setOnThreadStatueListener(onThreadStatueListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 现在下载状态的接口回调
     */
    public interface onThreadStatueListener {

        /**
         * SMB服务器图片数据为空
         */
        void onSmbEmptyPicture();

        /**
         * 模糊图下载完毕
         */
        void onDimPictureDownOK();

        /**
         * 原图下载完毕
         */
        void onReallyPictureDownOK();

        /**
         * 所有下载完毕
         */
        void onOverDownOK();

    }

}
