# kkFileView - 修改版

项目基于 [kkFileView](https://github.com/kekingcn/kkFileView) 修改，主要修改的地方：

1. 去掉了项目内的 libreoffice 分发，目的是减少源码体积；
2. Java 升级到 17；
3. 专注于 docx 文件预览；
4. 去掉旧版本的 tiff 处理方式，因为 java 9 开始内置对其支持；
5. 去掉了容器脚本等内容，打包方式为普通方式和 Spring Boot 两种；
6. 将 100MB+ 的静态资源移到单独模块下，以减少 java 模块打包体积。

#### 文档预览实现逻辑

1. 解析参数，判断来源渠道，生成 FileAttribute 对象：
   1. 如果是默认来源，则交给 FileHandlerService 的 getFileAttribute() 方法解析；
   2. 如果是其他渠道，则交给对应的 Channel 实现类来解析。
2. 通过 FilePreviewFactory 得到对应的 FilePreview 实现类，该类负责生成最终预览内容，并返回对应的模板页名称。
3. Spring WebMVC 框架负责渲染模板页，并返回给浏览器。

#### Word 转 PDF

1. 先由 OfficeToPdfService 将 Word 文档转为 PDF
2. 然后由 FileHandlerService.pdf2jpg() 方法将 PDF 转为图片
