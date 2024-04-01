# PdfViewerDemo
pdf阅读器，基于MuPDF开发，支持标注(高亮、下划线、删除线、手绘)
PdfViewerDemo is developed within the [mupdf](https://mupdf.com/downloads/) project.
# Screenshots
[效果预览](https://drive.google.com/file/d/1FrEztt9D0hCeGwGK61CYlsqeflTzgHY1/view?usp=sharing)
# Thanks
[PdfViewerDemo][2]

[2]: https://github.com/LonelyPluto/PdfViewerDemo

# 基于PdfViewerDemo的修改
* 重新编译mupdf,去除x86，新增arm64 so (2023-09-11)
* 修复画笔颜色无效问题(mupdf.c修改)
* 修复画笔size设置无效问题(mupdf.c修改)
* addMarkupAnnotation新增颜色入参，支持对下划线、高亮、删除线的颜色设置(mupdf.c修改)
* 去除不用的功能(设置链接颜色、搜索、复制文字、画笔size、outline)
* 画笔画完之后，Annotation点击不能删除问题
* add undo redo (2023-09-19)
* 新增文件选择长截图导出
