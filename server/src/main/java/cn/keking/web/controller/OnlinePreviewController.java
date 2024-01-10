package cn.keking.web.controller;

import cn.keking.channel.ChannelManager;
import cn.keking.model.ChannelParameters;
import cn.keking.model.FileAttribute;
import cn.keking.service.FileHandlerService;
import cn.keking.service.FilePreview;
import cn.keking.service.FilePreviewFactory;
import cn.keking.service.cache.CacheService;
import cn.keking.service.impl.OtherFilePreviewImpl;
import cn.keking.utils.Jackson;
import cn.keking.utils.KkFileUtils;
import cn.keking.utils.WebUtils;
import fr.opensagres.xdocreport.core.io.IOUtils;
import io.mola.galimatias.GalimatiasParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static cn.keking.service.FilePreview.PICTURE_FILE_PREVIEW_PAGE;

/**
 * 预览功能的主入口
 *
 * @author yudian-it
 */
@Controller
@Slf4j
public class OnlinePreviewController {

  public static final String BASE64_DECODE_ERROR_MSG = "Base64解码失败，请检查你的 %s 是否采用 Base64 + urlEncode 双重编码了！";

  private final FilePreviewFactory previewFactory;

  private final CacheService cacheService;

  private final FileHandlerService fileHandlerService;

  private final OtherFilePreviewImpl otherFilePreview;

  private final ChannelManager channelManager;

  public OnlinePreviewController(
    FilePreviewFactory filePreviewFactory,
    FileHandlerService fileHandlerService,
    CacheService cacheService,
    OtherFilePreviewImpl otherFilePreview,
    ChannelManager channelManager
  ) {
    this.previewFactory = filePreviewFactory;
    this.fileHandlerService = fileHandlerService;
    this.cacheService = cacheService;
    this.otherFilePreview = otherFilePreview;
    this.channelManager = channelManager;
  }

  /**
   * 预览文档
   *
   * @param url 文档地址
   */
  @GetMapping("/onlinePreview")
  public String onlinePreview(String url, Model model, HttpServletRequest req) {

    String fileUrl;
    try {
      fileUrl = WebUtils.decodeUrl(url);
    } catch (Exception ex) {
      String errorMsg = String.format(BASE64_DECODE_ERROR_MSG, "url");
      return otherFilePreview.notSupportedFile(model, errorMsg);
    }
    FileAttribute fileAttribute = fileHandlerService.getFileAttribute(fileUrl, req);
    model.addAttribute("file", fileAttribute);
    FilePreview filePreview = previewFactory.get(fileAttribute);
    log.info("预览文件, filePreview={}, url={}", filePreview, fileUrl);
    var template = filePreview.filePreviewHandle(fileUrl, model, fileAttribute);
    log.info("预览方式 template={}", template);
    return template;
  }

  /**
   * 区分不同渠道的文档预览
   *
   * @param parameters 渠道参数，内容为符合 ChannelParameters 类的 JSON 内容做 Base64 编码
   */
  @GetMapping("/onlinePreviewChannel")
  public String onlinePreviewPdf(String parameters, Model model, HttpServletRequest req) {
    // 1. 解析请求参数
    var json = new String(Base64.decodeBase64(parameters), StandardCharsets.UTF_8);
    var channelParameters = Jackson.deserializeStandardJson(json, ChannelParameters.class);

    // 2. 解析渠道文档来源，得到 FileAttribute 对象
    var channel = channelManager.resolve(channelParameters);
    if (channel == null) {
      return otherFilePreview.notSupportedFile(model, "不支持的文档来源渠道");
    }

    try {
      var fileAttribute = channel.resolve(channelParameters, model, req);
      var fileUrl = fileAttribute.getUrl();

      // 3. 生成预览内容
      model.addAttribute("file", fileAttribute);
      FilePreview filePreview = previewFactory.get(fileAttribute);
      log.info("预览文件, filePreview={}, url={}", filePreview, fileUrl);
      var previewTemplateName = filePreview.filePreviewHandle(fileUrl, model, fileAttribute);
      log.info("预览方式 template={}", previewTemplateName);
      return previewTemplateName;

    } catch (Exception e) {
      log.error("", e);
      return otherFilePreview.notSupportedFile(model, e.getMessage());
    }
  }

  /**
   * 预览图片
   *
   * @param urls 图片地址，多个地址用 "|" 分割
   */
  @GetMapping("/picturesPreview")
  public String picturesPreview(String urls, Model model, HttpServletRequest req) {
    String fileUrls;
    try {
      fileUrls = WebUtils.decodeUrl(urls);
      // 防止XSS攻击
      fileUrls = KkFileUtils.htmlEscape(fileUrls);
    } catch (Exception ex) {
      String errorMsg = String.format(BASE64_DECODE_ERROR_MSG, "urls");
      return otherFilePreview.notSupportedFile(model, errorMsg);
    }
    log.info("预览文件url：{}，urls：{}", fileUrls, urls);
    // 抽取文件并返回文件列表
    String[] images = fileUrls.split("\\|");
    List<String> imgUrls = Arrays.asList(images);
    model.addAttribute("imgUrls", imgUrls);
    String currentUrl = req.getParameter("currentUrl");
    if (StringUtils.hasText(currentUrl)) {
      String decodedCurrentUrl = new String(Base64.decodeBase64(currentUrl));
      decodedCurrentUrl = KkFileUtils.htmlEscape(decodedCurrentUrl);   // 防止XSS攻击
      model.addAttribute("currentUrl", decodedCurrentUrl);
    } else {
      model.addAttribute("currentUrl", imgUrls.get(0));
    }
    return PICTURE_FILE_PREVIEW_PAGE;
  }

  /**
   * 根据url获取文件内容
   * 当pdfjs读取存在跨域问题的文件时将通过此接口读取
   *
   * @param urlPath  url
   * @param response response
   */
  @GetMapping("/getCorsFile")
  public void getCorsFile(String urlPath, HttpServletResponse response) throws IOException {
    try {
      urlPath = WebUtils.decodeUrl(urlPath);
    } catch (Exception ex) {
      log.error(String.format(BASE64_DECODE_ERROR_MSG, urlPath), ex);
      return;
    }
    HttpURLConnection urlcon = null;
    InputStream inputStream = null;
    String urlStr;
    assert urlPath != null;
    if (!urlPath.toLowerCase().startsWith("http") && !urlPath.toLowerCase().startsWith("https") && !urlPath.toLowerCase().startsWith("ftp")) {
      log.info("读取跨域文件异常，可能存在非法访问，urlPath：{}", urlPath);
      return;
    }
    log.info("下载跨域pdf文件url：{}", urlPath);
    if (!urlPath.toLowerCase().startsWith("ftp:")) {
      try {
        URL url = WebUtils.normalizedURL(urlPath);
        urlcon = (HttpURLConnection) url.openConnection();
        urlcon.setConnectTimeout(30000);
        urlcon.setReadTimeout(30000);
        urlcon.setInstanceFollowRedirects(false);
        int responseCode = urlcon.getResponseCode();
        if (responseCode == 403 || responseCode == 500) { //403  500
          log.error("读取跨域文件异常，url：{}，错误：{}", urlPath, responseCode);
          return;
        }
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) { //301 302
          url = new URL(urlcon.getHeaderField("Location"));
          urlcon = (HttpURLConnection) url.openConnection();
        }
        if (responseCode == 404) {  //404
          try {
            urlStr = URLDecoder.decode(urlPath, StandardCharsets.UTF_8);
            urlStr = URLDecoder.decode(urlStr, StandardCharsets.UTF_8);
            url = WebUtils.normalizedURL(urlStr);
            urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setConnectTimeout(30000);
            urlcon.setReadTimeout(30000);
            urlcon.setInstanceFollowRedirects(false);
            responseCode = urlcon.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) { //301 302
              url = new URL(urlcon.getHeaderField("Location"));
            }
            if (responseCode == 404 || responseCode == 403 || responseCode == 500) {
              log.error("读取跨域文件异常，url：{}，错误：{}", urlPath, responseCode);
              return;
            }
          } catch (UnsupportedEncodingException e) {
            log.error("", e);
          } finally {
            assert urlcon != null;
            urlcon.disconnect();
          }
        }
        if (urlPath.contains(".svg")) {
          response.setContentType("image/svg+xml");
        }
        inputStream = (url).openStream();
        IOUtils.copy(inputStream, response.getOutputStream());

      } catch (IOException | GalimatiasParseException e) {
        log.error("读取跨域文件异常，url：{}", urlPath);
      } finally {
        assert urlcon != null;
        urlcon.disconnect();
        IOUtils.closeQuietly(inputStream);
      }
    } else {
      try {
        URL url = WebUtils.normalizedURL(urlPath);
        if (urlPath.contains(".svg")) {
          response.setContentType("image/svg+xml");
        }
        inputStream = (url).openStream();
        IOUtils.copy(inputStream, response.getOutputStream());
      } catch (IOException | GalimatiasParseException e) {
        log.error("读取跨域文件异常，url：{}", urlPath);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
    }
  }

  /**
   * 通过api接口入队
   *
   * @param url 请编码后在入队
   */
  @GetMapping("/addTask")
  @ResponseBody
  public String addQueueTask(String url) {
    log.info("添加转码队列url：{}", url);
    cacheService.addQueueTask(url);
    return "success";
  }
}
