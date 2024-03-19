package cn.keking.converters.impl;

import cn.keking.converters.FileFormatConverter;
import cn.keking.model.FileAttribute;
import cn.keking.model.FileType;


public class SofficeToPdf implements FileFormatConverter {

  @Override
  public FileType getSourceFileType() {
    return null;
  }

  @Override
  public FileType getTargetFileType() {
    return null;
  }

  @Override
  public void convert(FileAttribute fileAttribute, String outputPath) {

  }
}
