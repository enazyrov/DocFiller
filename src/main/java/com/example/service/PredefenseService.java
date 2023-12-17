package com.example.service;

import com.example.dao.PredefenseRepository;
import com.example.model.Predefense;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PredefenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredefenseService.class);
    @Value("${print.form.folder}")
    private String printFormsFolder;

    @Value("${font.folder}")
    private String fontFolder;

    private PdfOptions pdfOptions = null;

    private static HashMap<String, BaseFont> fonts = new HashMap<>();

    @Autowired
    private PredefenseRepository predefenseRepository;

    public void save(Predefense predefense) {
        predefenseRepository.save(predefense);
    }

    public List<Predefense> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(predefenseRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        predefenseRepository.deleteById(id);
    }

    public ResponseEntity<Resource> createGeneratedReportFileResponse(HashMap<String, String> data) {
        File file = new File(printFormsFolder + File.separatorChar + "Predzaschita.docx");
        File generatedFile = null;

        try {
            generatedFile = generateReport(file, data);
            return createResponseEntity(generatedFile, file.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File generateReport(File templateFile, HashMap<String, String> data) throws IOException {
        boolean success = false;
        File resultFile = null;
        XWPFDocument doc = null;
        try (FileInputStream fis = new FileInputStream(templateFile)) {
            resultFile = File.createTempFile("report", ".tmp");
            doc = new XWPFDocument(fis);
            // обработаем текстовые параграфы
            fillParagraphs(doc.getParagraphs(), data);
            // обработаем таблицы
            for (XWPFTable tbl : doc.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        fillParagraphs(cell.getParagraphs(), data);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Файл шаблона не найден!", e);
        } catch (IOException e) {
            LOGGER.error("Ошибка ввода/вывода!", e);
        }
        if (doc != null) {
            try (FileOutputStream fos = new FileOutputStream(resultFile)) {
                if (pdfOptions == null) {
                    pdfOptions = PdfOptions.create();
                    pdfOptions.fontProvider((familyName, encoding, size, style, color) -> {

                        if (familyName.equalsIgnoreCase("Times New Roman")) {
                            try {
                                BaseFont baseFont = fonts.get(familyName);
                                if (baseFont == null) {
                                    baseFont = BaseFont.createFont(fontFolder + "/times.ttf", encoding, BaseFont.EMBEDDED);
                                    fonts.put(familyName, baseFont);
                                }
                                if (baseFont != null) {
                                    return new Font(baseFont, size, style, color);
                                }
                            } catch (DocumentException | IOException e) {
                                LOGGER.error("Не удалось создать шрифт!", e);
                            }
                        }
                        return FontFactory.getFont(familyName, encoding, size, style, color);
                    });
                }
                PdfConverter.getInstance().convert(doc, fos, pdfOptions);
                success = true;
            } catch (FileNotFoundException e) {
                LOGGER.error("Временный файл не найден!", e);
            } catch (IOException e) {
                LOGGER.error("Ошибка ввода/вывода!", e);
            }
        }
        if (!success) {
            String errorMsg = "Не удалось создать ПФ по шаблону " + templateFile.getAbsolutePath();
            LOGGER.error(errorMsg);
            throw new IOException(errorMsg);
        }
        LOGGER.info("ПФ успешно создана по шаблону {} в файле {}", templateFile.getAbsolutePath(), resultFile.getAbsolutePath());
        return resultFile;
    }

    private void fillParagraphs(List<XWPFParagraph> paragraphs, HashMap<String, String> data) {
        List<Integer> runsForDelete = new ArrayList<>();
        boolean deleteRun;
        StringBuilder textForReplace = new StringBuilder();
        for (XWPFParagraph p : paragraphs) {
            String paragraphText = p.getText();
//            LOGGER.debug("Обработка параграфа:\n {}", paragraphText);
            if (paragraphText != null && paragraphText.contains("${")) {
                List<XWPFRun> runs = p.getRuns();
                runsForDelete.clear();
                deleteRun = false;
                textForReplace.setLength(0);
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
//                        LOGGER.debug("Обработка фрагмента:\n {}", text);
                        if (text != null) {
                            // найдем фрагмент с открывающим тегом
                            if (text.contains("${")) {
                                // пометим этот и последующие фрагменты для удаления
                                deleteRun = true;
                            }
                            // если в фрагменте есть закрывающий тег
                            if (text.contains("}")) {
                                // заменим текст в фрагменте
                                textForReplace.append(text);
                                /*int pos = textForReplace.indexOf(QR_CODE);
                                if (pos > 0) {
                                    r.setText("", 0);
                                    textForReplace.replace(pos, pos + QR_CODE.length(), "");
                                    addQRCode(r, calcExpression(textForReplace.toString(), data));
                                } else {
                                    r.setText(calcExpression(textForReplace.toString(), data), 0);
                                }*/
                                // и не будем помечать для удаления
                                deleteRun = false;
                            }
                        }
                        if (deleteRun) {
                            // если удаляем, то добавим текст фрагмента в билдер
                            textForReplace.append(text);
//                            LOGGER.debug("Удалим фрагмент:\n {}", text);
                            runsForDelete.add(runs.indexOf(r));
                        }
                    }
                    // удалим фрагменты в обратном порядке (начиная с самого последнего),
                    // так как после удаления позиция следующих фрагментов меняется
                    for (int i = runsForDelete.size() - 1; i >= 0 ; i--) {
                        int pos = runsForDelete.get(i);
                        p.removeRun(pos);
                    }
                }
            }
        }
    }

    private ResponseEntity<Resource> createResponseEntity(File file, String fileName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        MediaType mediaType = fileName.toLowerCase().contains(".pdf") ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }
}
