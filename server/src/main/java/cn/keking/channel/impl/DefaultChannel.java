package cn.keking.channel.impl;


import cn.keking.FileTypeNotSupportedException;
import cn.keking.channel.Channel;
import cn.keking.model.ChannelParameters;
import cn.keking.model.FileAttribute;
import cn.keking.service.FileHandlerService;
import cn.keking.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static cn.keking.web.controller.OnlinePreviewController.BASE64_DECODE_ERROR_MSG;

/**
 * 默认的 Channel 实现
 */
@Component
public class DefaultChannel implements Channel {

  @Autowired
  private FileHandlerService fileHandlerService;

  @Override
  public FileAttribute resolve(ChannelParameters parameters, Model model, HttpServletRequest req) {
    String fileUrl = parameters.get("fileUrl");
    try {
      fileUrl = WebUtils.decodeUrl(fileUrl);
    } catch (Exception ex) {
      String errorMsg = String.format(BASE64_DECODE_ERROR_MSG, "url");
      throw new FileTypeNotSupportedException(errorMsg, ex);
    }
    return fileHandlerService.getFileAttribute(fileUrl, req);
  }
}
