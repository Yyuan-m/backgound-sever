package com.car.rental.module.system.util;

import com.car.rental.entity.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志导出工具：支持 Excel(xlsx) / PDF / Markdown 三种格式。
 * 字段顺序：ID、操作人、模块、操作类型、描述、IP、状态、操作时间。
 * <p>
 * PDF 采用表格样式：画行列边框，长文本（如描述列）按列宽自动换行不截断，行高根据内容自适应。
 * 中文字体优先使用系统字体（Windows SimSun/Microsoft YaHei，Linux NotoSansCJK），
 * 找不到时回退到 PDFBox 内置 Helvetica（不支持中文，会把非 ASCII 替换为 "?"）。
 */
@Slf4j
@Component
public class OperationLogExporter {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] HEADERS = {"ID", "操作人", "模块", "操作类型", "描述", "IP", "状态", "操作时间"};
    /** 各列相对宽度（用于按比例分配 PDF/Excel 列宽）；描述列较宽，容纳长文本 */
    private static final int[] COL_WIDTHS = {50, 100, 90, 80, 260, 110, 50, 130};

    /** 导出格式枚举 */
    public enum Format {
        EXCEL, PDF, MARKDOWN
    }

    public static Format parseFormat(String format) {
        if (format == null) {
            return Format.EXCEL;
        }
        return switch (format.toLowerCase()) {
            case "pdf" -> Format.PDF;
            case "markdown", "md" -> Format.MARKDOWN;
            default -> Format.EXCEL;
        };
    }

    /** 根据格式分发到具体的写入方法 */
    public void write(Format format, List<OperationLog> logs, OutputStream out) throws IOException {
        switch (format) {
            case EXCEL -> writeExcel(logs, out);
            case PDF -> writePdf(logs, out);
            case MARKDOWN -> writeMarkdown(logs, out);
        }
    }

    // ======================== Excel ========================

    private void writeExcel(List<OperationLog> logs, OutputStream out) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("操作日志");

            // 表头样式：加粗居中
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 写表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
                sheet.setColumnWidth(i, COL_WIDTHS[i] * 36);
            }

            // 写数据行
            if (logs != null) {
                for (int i = 0; i < logs.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    OperationLog log = logs.get(i);
                    row.createCell(0).setCellValue(log.getId() != null ? log.getId() : 0);
                    row.createCell(1).setCellValue(safe(log.getOperatorName() != null ? log.getOperatorName() : log.getOperator()));
                    row.createCell(2).setCellValue(safe(log.getModule()));
                    row.createCell(3).setCellValue(safe(log.getAction()));
                    row.createCell(4).setCellValue(safe(log.getDescription()));
                    row.createCell(5).setCellValue(safe(log.getIp()));
                    row.createCell(6).setCellValue(log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败");
                    row.createCell(7).setCellValue(log.getCreatedAt() != null ? log.getCreatedAt().format(DT_FMT) : "");
                }
            }
            workbook.write(out);
        }
    }

    // ======================== PDF ========================

    /**
     * PDF 表格写入器：自动分页，每页带表头，画行列边框，长文本自动换行，行高随内容自适应。
     * 内部类封装分页状态（当前页、当前 y 坐标、当前 content stream）。
     */
    private static class PdfTableWriter {
        private final PDDocument document;
        private final PDRectangle pageSize;
        private final PDFont font;
        private final float fontSize;
        private final float margin;
        private final float yTop;
        private final float yBottom;
        private final float lineHeight;
        private final float cellPaddingX;
        private final float cellPaddingY;
        private final float[] colWidths;
        private final float[] colXStarts;
        private final float totalWidth;
        /** 字体是否支持非 ASCII 字符（中文）。Helvetica 不支持，需要替换为 "?" */
        private final boolean fontSupportsCJK;

        private PDPageContentStream cs;
        private float y;

        PdfTableWriter(PDDocument document, PDRectangle pageSize, PDFont font, float fontSize,
                       float margin, float lineHeight, float[] colWidths) throws IOException {
            this.document = document;
            this.pageSize = pageSize;
            this.font = font;
            this.fontSize = fontSize;
            this.margin = margin;
            this.yTop = pageSize.getHeight() - margin;
            this.yBottom = margin;
            this.lineHeight = lineHeight;
            this.cellPaddingX = 3f;
            this.cellPaddingY = 4f;
            this.colWidths = colWidths;
            this.fontSupportsCJK = checkCJKSupport(font);
            // 计算总宽度和每列起始 x
            float total = 0;
            for (float w : colWidths) total += w;
            this.totalWidth = total;
            this.colXStarts = new float[colWidths.length];
            float x = margin;
            for (int i = 0; i < colWidths.length; i++) {
                colXStarts[i] = x;
                x += colWidths[i];
            }
            newPage();
        }

        /** 检测字体是否支持中文字符：尝试测量"中"字宽度，不抛异常则视为支持 */
        private boolean checkCJKSupport(PDFont font) {
            try {
                font.getStringWidth("中");
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private void newPage() throws IOException {
            if (cs != null) {
                cs.close();
            }
            PDPage page = new PDPage(pageSize);
            document.addPage(page);
            cs = new PDPageContentStream(document, page);
            cs.setFont(font, fontSize);
            y = yTop;
        }

        /**
         * 写一行（自动分页）：
         * 1. 把每个单元格的文本按列宽拆成多行；
         * 2. 行高 = 最大行数 * lineHeight + 上下 padding；
         * 3. 空间不足时新建一页并重画表头；
         * 4. 画该行外框 + 列分隔线 + 单元格文本。
         */
        void writeRow(String[] cells, boolean isHeader) throws IOException {
            // 1. 拆分每个单元格为多行
            List<List<String>> wrappedCells = new ArrayList<>();
            int maxLines = 1;
            for (int i = 0; i < cells.length && i < colWidths.length; i++) {
                List<String> lines = wrapText(cells[i], colWidths[i] - 2 * cellPaddingX);
                wrappedCells.add(lines);
                if (lines.size() > maxLines) {
                    maxLines = lines.size();
                }
            }
            float rowHeight = maxLines * lineHeight + 2 * cellPaddingY;

            // 2. 空间不足时换页 + 重画表头
            if (y - rowHeight < yBottom) {
                newPage();
                writeRow(HEADERS, true);
            }

            // 3. 画行边框（矩形）
            cs.addRect(margin, y - rowHeight, totalWidth, rowHeight);
            cs.stroke();
            // 4. 画列分隔线（除最右列外，每列右边一条竖线）
            for (int i = 0; i < colWidths.length - 1; i++) {
                float lineX = colXStarts[i] + colWidths[i];
                cs.moveTo(lineX, y);
                cs.lineTo(lineX, y - rowHeight);
                cs.stroke();
            }

            // 5. 在每个单元格内画文本（垂直居中）
            cs.setFont(font, isHeader ? fontSize + 0.5f : fontSize);
            for (int i = 0; i < wrappedCells.size(); i++) {
                List<String> lines = wrappedCells.get(i);
                float textBlockHeight = lines.size() * lineHeight;
                float startY = y - cellPaddingY - (rowHeight - 2 * cellPaddingY - textBlockHeight) / 2 - fontSize;
                for (int li = 0; li < lines.size(); li++) {
                    String display = sanitize(lines.get(li));
                    cs.beginText();
                    cs.newLineAtOffset(colXStarts[i] + cellPaddingX, startY - li * lineHeight);
                    cs.showText(display);
                    cs.endText();
                }
            }
            y -= rowHeight;
        }

        /**
         * 按列宽把文本拆成多行（贪心切分，支持中文/英文混排）。
         * 算法：逐字符累加宽度，超过列宽就换行；同时优先在空格/标点处断行。
         */
        private List<String> wrapText(String text, float maxWidth) {
            List<String> lines = new ArrayList<>();
            if (text == null || text.isEmpty()) {
                lines.add("");
                return lines;
            }
            // 若字体不支持中文，直接按字符数粗略切分（避免 getStringWidth 抛异常）
            if (!fontSupportsCJK) {
                int maxChars = Math.max(1, (int) (maxWidth / (fontSize * 0.6)));
                for (int i = 0; i < text.length(); i += maxChars) {
                    lines.add(text.substring(i, Math.min(i + maxChars, text.length())));
                }
                return lines;
            }
            try {
                StringBuilder cur = new StringBuilder();
                float curWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    String ch = String.valueOf(text.charAt(i));
                    float chWidth = font.getStringWidth(ch) / 1000 * fontSize;
                    if (curWidth + chWidth > maxWidth && cur.length() > 0) {
                        lines.add(cur.toString());
                        cur.setLength(0);
                        curWidth = 0;
                    }
                    cur.append(ch);
                    curWidth += chWidth;
                }
                if (cur.length() > 0) {
                    lines.add(cur.toString());
                }
            } catch (Exception e) {
                // 异常时回退：按字符数粗略切分
                int maxChars = Math.max(1, (int) (maxWidth / (fontSize * 0.6)));
                for (int i = 0; i < text.length(); i += maxChars) {
                    lines.add(text.substring(i, Math.min(i + maxChars, text.length())));
                }
            }
            return lines;
        }

        /** 当字体不支持中文时，把所有非 ASCII 字符替换为 "?"，避免 showText 抛异常 */
        private String sanitize(String text) {
            if (text == null) return "";
            if (fontSupportsCJK) return text;
            return text.replaceAll("[^\\x00-\\x7F]", "?");
        }

        void close() throws IOException {
            if (cs != null) {
                cs.close();
            }
        }
    }

    private void writePdf(List<OperationLog> logs, OutputStream out) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDRectangle pageSize = PDRectangle.A4;
            PDFont font = loadChineseFont(document);
            float fontSize = 9f;
            float margin = 36f;
            float lineHeight = 12f;

            // 按页面宽度等比分配列宽
            float totalWidth = pageSize.getWidth() - 2 * margin;
            int widthSum = 0;
            for (int w : COL_WIDTHS) widthSum += w;
            float[] colWidths = new float[HEADERS.length];
            for (int i = 0; i < HEADERS.length; i++) {
                colWidths[i] = totalWidth * ((float) COL_WIDTHS[i] / widthSum);
            }

            PdfTableWriter writer = new PdfTableWriter(document, pageSize, font, fontSize, margin, lineHeight, colWidths);
            // 表头
            writer.writeRow(HEADERS, true);
            // 数据行
            if (logs != null) {
                for (OperationLog log : logs) {
                    String[] cells = {
                            log.getId() != null ? String.valueOf(log.getId()) : "",
                            safe(log.getOperatorName() != null ? log.getOperatorName() : log.getOperator()),
                            safe(log.getModule()),
                            safe(log.getAction()),
                            safe(log.getDescription()),
                            safe(log.getIp()),
                            log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败",
                            log.getCreatedAt() != null ? log.getCreatedAt().format(DT_FMT) : ""
                    };
                    writer.writeRow(cells, false);
                }
            }
            writer.close();
            document.save(out);
        }
    }

    /**
     * 加载支持中文的字体：依次尝试常见系统字体路径，全部失败时返回内置 Helvetica。
     * 注意：Helvetica 不支持中文字符，中文将显示为方框/?，但能保证 PDF 正常生成。
     */
    private PDFont loadChineseFont(PDDocument document) {
        // Windows / Linux 常见中文字体路径
        String[] candidates = {
                "C:/Windows/Fonts/simsun.ttc",
                "C:/Windows/Fonts/msyh.ttc",
                "C:/Windows/Fonts/msyhbd.ttc",
                "C:/Windows/Fonts/simhei.ttf",
                "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc",
                "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
                "/usr/share/fonts/wqy-zenhei/wqy-zenhei.ttc",
                "/usr/share/fonts/wqy-microhei/wqy-microhei.ttc"
        };
        for (String path : candidates) {
            File f = new File(path);
            if (f.exists() && f.canRead()) {
                try {
                    return PDType0Font.load(document, f);
                } catch (Exception e) {
                    log.warn("加载中文字体失败 {}: {}", path, e.getMessage());
                }
            }
        }
        log.warn("未找到可用的中文字体，PDF 中的中文将显示为 ?");
        // 兜底：内置 Helvetica（不支持中文，但保证不抛异常）
        return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    }

    // ======================== Markdown ========================

    private void writeMarkdown(List<OperationLog> logs, OutputStream out) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# 操作日志\n\n");
        sb.append("导出时间：").append(LocalDateTime.now().format(DT_FMT))
                .append("  \n总条数：").append(logs != null ? logs.size() : 0).append("\n\n");

        // 表头
        sb.append("| ");
        sb.append(String.join(" | ", HEADERS));
        sb.append(" |\n");

        // 分隔行
        sb.append("| ");
        for (int i = 0; i < HEADERS.length; i++) {
            sb.append("--- | ");
        }
        sb.append("\n");

        // 数据行
        if (logs != null) {
            for (OperationLog log : logs) {
                sb.append("| ");
                sb.append(log.getId() != null ? log.getId() : "").append(" | ");
                sb.append(escapeMd(safe(log.getOperatorName() != null ? log.getOperatorName() : log.getOperator()))).append(" | ");
                sb.append(escapeMd(safe(log.getModule()))).append(" | ");
                sb.append(escapeMd(safe(log.getAction()))).append(" | ");
                sb.append(escapeMd(safe(log.getDescription()))).append(" | ");
                sb.append(escapeMd(safe(log.getIp()))).append(" | ");
                sb.append(log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败").append(" | ");
                sb.append(log.getCreatedAt() != null ? log.getCreatedAt().format(DT_FMT) : "").append(" |\n");
            }
        }
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** 转义 Markdown 表格中的特殊字符 */
    private String escapeMd(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").replace("\n", " ").replace("\r", "");
    }

    private String safe(String s) {
        return s != null ? s : "";
    }
}
