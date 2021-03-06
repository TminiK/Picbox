package ml.timik.picbox.configs;

/**
 * Created by PureDark on 2016/9/27.
 */

public class UrlConfig {
    //public final static String updateUrl = "https://api.github.com/repos/PureDark/H-Viewer/releases/latest";
    public final static String updateUrl = "https://api.github.com/repos/tminik/picbox/releases/latest";
    public final static String siteSourceUrl = "https://raw.githubusercontent.com/TminiK/Index/master/source.json";
    //public final static String bingApiUrl = "https://bing.ioliu.cn/v1/rand?type=json";
    //public final static String bingApiUrl = "http://api.btstu.cn/sjbz/api.php?lx=m_meizi&format=json";
    //public final static String bingApiUrl = "https://3650000.xyz/api/?type=json&mode=8";
    public final static String bingApiUrl = "https://api.uomg.com/api/rand.img1?format=json";
    //    public final static String libijkffmpegUrl = "https://raw.githubusercontent.com/PureDark/GSYVideoPlayer/master/gsyVideoPlayer/libs/<ABIS>/libijkffmpeg.so";
//    public final static String libijkplayerUrl = "https://raw.githubusercontent.com/PureDark/GSYVideoPlayer/master/gsyVideoPlayer/libs/<ABIS>/libijkplayer.so";
//    public final static String libijksdlUrl = "https://raw.githubusercontent.com/PureDark/GSYVideoPlayer/master/gsyVideoPlayer/libs/<ABIS>/libijksdl.so";
    public final static String libijkffmpegUrl = "";
    public final static String libijkplayerUrl = "";
    public final static String libijksdlUrl = "";

    public static String getBingAPIUrl() {
//        Random random = new Random();
//        int id = random.nextInt(8);
//        return "http://www.bing.com/HPImageArchive.aspx?format=js&idx=" + id + "&n=1";
        return bingApiUrl;
    }

    public static String[] getIjkLibUrl(String abi){
        String[] urls = new String[3];
        urls[0] = libijkffmpegUrl.replace("<ABIS>", abi);
        urls[1] = libijkplayerUrl.replace("<ABIS>", abi);
        urls[2] = libijksdlUrl.replace("<ABIS>", abi);
        return urls;
    }


}
