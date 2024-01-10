package cn.keking.model;

import cn.keking.config.ConfigConstants;
import lombok.Data;

import java.util.Set;

import static cn.keking.service.FileHandlerService.getCacheFileName;

/**
 * Created by kl on 2018/1/17.
 * Content :
 */
@Data
public class FileAttribute {

  /**
   * 文件类型
   */
  private FileType type;

  /**
   * 文件后缀名，不带 "."
   */
  private String suffix;

  /**
   * 完整文件名
   */
  private String name;

  /**
   * 文件原始地址，如果有的话
   */
  private String url;

  private boolean isCompressFile = false;

  private String compressFileKey;

  private String filePassword;

  private boolean usePasswordCache;

  private String officePreviewType = ConfigConstants.getOfficePreviewType();

  private String tifPreviewType;

  private boolean skipDownLoad = false;

  private boolean forceUpdatedCache = false;

  private String cacheName;

  private String outFilePath;

  private String originFilePath;

  private String cacheListName;

  private boolean htmlView = false;

  /**
   * 代理请求到文件服务器的认证请求头，格式如下：
   * {“username”:"test","password":"test"}
   * 请求文件服务器时，会将 json 直接塞到请求头里
   */
  private String kkProxyAuthorization;

  public FileAttribute() {
  }

  public FileAttribute(FileType type, String suffix, String name, String url) {
    this.type = type;
    this.suffix = suffix;
    this.name = name;
    this.url = url;
  }

  public FileAttribute(FileType type, String suffix, String name, String url, String officePreviewType) {
    this.type = type;
    this.suffix = suffix;
    this.name = name;
    this.url = url;
    this.officePreviewType = officePreviewType;
  }

  ////////////////////////////////////////

  public static final Set<String> HTML_VIEW_SUFFIX = Set.of(
    "xls", "xlsx", "csv", "xlsm", "xlt", "xltm", "et", "ett", "xlam"
  );

  /**
   * 根据构造方法的参数自行组装，不是每种场景都要这么做，所以没有在构造方法中调用
   */
  public FileAttribute selfAssemble() {
    this.htmlView = HTML_VIEW_SUFFIX.contains(suffix.toLowerCase());
    String cacheFilePrefixName = name.substring(0, name.lastIndexOf(".")) + suffix + "."; //这里统一文件名处理 下面更具类型 各自添加后缀
    String cacheFileName = getCacheFileName(type, name, cacheFilePrefixName, htmlView, isCompressFile);
    String cacheListName = cacheFilePrefixName + "ListName";  //文件列表缓存文件名
    setCacheName(cacheFileName);
    setCacheListName(cacheListName);
    return this;
  }
}
