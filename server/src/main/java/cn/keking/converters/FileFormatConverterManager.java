package cn.keking.converters;

import cn.keking.model.FileAttribute;
import cn.keking.model.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FileFormatConverterManager {

  @Autowired
  private List<FileFormatConverter> converters;

  public Optional<FileFormatConverter> findConverter(FileAttribute fileAttribute, FileType targetFileType) {
    return converters.stream()
      .filter(
        converter -> converter.getSourceFileType().equals(fileAttribute.getType())
          && converter.getTargetFileType().equals(targetFileType)
      )
      .findFirst();
  }
}
