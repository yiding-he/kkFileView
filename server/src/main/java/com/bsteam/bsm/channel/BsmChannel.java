package com.bsteam.bsm.channel;

import cn.keking.FileTypeNotSupportedException;
import cn.keking.channel.Channel;
import cn.keking.config.ConfigConstants;
import cn.keking.model.ChannelParameters;
import cn.keking.model.FileAttribute;
import cn.keking.model.FileType;
import cn.keking.utils.Jackson;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@Component
public class BsmChannel implements Channel {

  @Override
  public FileAttribute resolve(ChannelParameters parameters, Model model, HttpServletRequest req) {
    var env = parameters.get("env");  // api/prod_api
    var api = parameters.getConfig().get(env);

    var client = new RestTemplate();
    var uri = UriComponentsBuilder.fromUriString(api)
      .path("/check-building/get-building-report")
      .queryParam("from", "report-generator")
      .queryParam("project_id", parameters.get("project_id"))
      .queryParam("check_building_id", parameters.get("check_building_id"))
      .queryParam("report_type", parameters.get("report_type"))
      .build().toUri();

    var responseJson = client.getForObject(uri, String.class);
    if (responseJson == null || !responseJson.startsWith("{")) {
      throw new FileTypeNotSupportedException("无法获取文件地址: " + uri);
    }

    var node = Jackson.deserializeNode(responseJson);

    if (node.get("code").asInt() != HttpStatus.OK.value()) {
      var message = node.get("msg").asText();
      throw new FileTypeNotSupportedException(message);
    }

    var fileUrl = node.get("url").asText();
    var fileType = FileType.typeFromUrl(fileUrl);
    var fileName = FilenameUtils.getName(fileUrl);
    var extension = FilenameUtils.getExtension(fileName);

    var fixedFileUrl = UriComponentsBuilder.fromHttpUrl(fileUrl)
      .replaceQueryParam("from", "report-generator")
      .build().toUriString();

    var fileAttribute = new FileAttribute(fileType, extension, fileName, fixedFileUrl).selfAssemble();
    fileAttribute.setOfficePreviewType("image");
    fileAttribute.setHtmlView(false);
    fileAttribute.setOutFilePath(ConfigConstants.getFileDir() + fileAttribute.getCacheName());
    fileAttribute.setOriginFilePath(ConfigConstants.getFileDir() + fileAttribute.getName());
    return fileAttribute;
  }
}
