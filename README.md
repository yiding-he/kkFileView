# kkFileView - 修改版

项目基于 [kkFileView](https://github.com/kekingcn/kkFileView) 修改，主要修改的地方：

1. 去掉了项目内的 libreoffice 分发，目的是减少源码体积；
2. Java 升级到 17；
3. 专注于 docx 文件预览；
4. 去掉旧版本的 tiff 处理方式，因为 java 9 开始内置对其支持；
5. 去掉了容器脚本等内容，打包方式为普通方式和 Spring Boot 两种；
6. 将 100MB+ 的静态资源移到单独模块下，以减少 java 模块打包体积。