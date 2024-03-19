package cn.keking.converters;

import cn.keking.model.FileAttribute;
import cn.keking.model.FileType;

public interface FileFormatConverter {

  FileType getSourceFileType();

  FileType getTargetFileType();

  void convert(FileAttribute fileAttribute, String outputPath);
}
