package com.example.service;

import com.example.dao.PrintFormRepository;
import com.example.model.PrintForm;
import com.example.utils.FileManager;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrintFormService {

    private static final String DATE_TAG = "var date = new Date();date.getDate()+'.'+(date.getMonth()+1)+'.'+date.getFullYear()";
    private static final String VALUE_V = "[\"V\"]";
    private static final String ERROR_FIELD_NAME = "ОШИБКА!";
    private static final String QR_CODE = "QRCODE:";
    private static final String OPEN_TAG_TERNARY_TEXT = "[\"@@";
    private static final String CLOSE_TAG_TERNARY_TEXT = "@@\"]";
    private static final String PERSONAL_ACCOUNT_REPORT_CODE = "ЛК";
    private static final String TAG_FOR_SHOW_FULL_BLOG_OPEN = "@@@";
    private static final String TAG_FOR_SHOW_FULL_BLOG_CLOSE = "/@@@";
    private static final String LINE_BREAK = "LINE_BREAK";
    private static final String FOR_TAG = "${for";
    private static final String COUNT_FOR_LOOP = "N";
    private static final String LINE_BREAK_FOR_LOOP = "line_break";
    private static final String CHECK_TABLE_TAG = "check_table${";
    private static final String PAGE_BREAK = "[PAGE_BREAK]";
    private static final String TABLE_FOR_OPEN = "tableFor${";
    private static final String TABLE_FOR_CLOSE = "}tableFor";
    private static final String CONDITION_TAG_FOR_LOOP_OPEN = "IF$";
    private static final String CONDITION_TAG_FOR_LOOP_CLOSE = "$IF";
    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");
    @Autowired
    private PrintFormRepository printFormRepository;

    private PdfOptions pdfOptions = null;

    private final String fontPath = "C:\\Users\\enazy\\IdeaProjects\\DocFiller_\\src\\main\\resources\\static\\fonts\\times.ttf";

    private static final HashMap<String, BaseFont> fonts = new HashMap<>();

    public List<PrintForm> findAll() {
        return printFormRepository.findAll();
    }

    public PrintForm findOne(Long typeId) {
        return printFormRepository.findByTypeId(typeId);
    }

    public void deleteById(Long pfId) {
        printFormRepository.deleteById(pfId);
    }

    public Optional<PrintForm> findById(Long pfId) {
        return printFormRepository.findById(pfId);
    }

    public PrintForm save(PrintForm printForm) {
        return printFormRepository.save(printForm);
    }

    public File getPrintFormFile(PrintForm printForm) {
        try {
            return FileManager.download(printForm.getUuid(), true).getFile();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    }


    public ResponseEntity<Resource> createGeneratedPrintFormFileResponse(PrintForm printForm, String fileName, String jsonString) throws IOException {
        final HashMap<String, String> data = getDataFromJSON(jsonString);

        File file = getPrintFormFile(printForm);
        File generatedFile = null;
        try {
            generatedFile = generatePrintForm(file, data);
            return createResponseEntity(generatedFile, fileName != null && !fileName.isEmpty() ? fileName : file.getName());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания ПФ: \n", e);
        } finally {
            if (generatedFile != null) {
                if (!generatedFile.delete()) {
                    log.error("Не удалось удалить временный файл: {}", file.getAbsolutePath());
                }
            }
        }
    }

    private void parseJSONObject(JSONObject jsonObject, HashMap<String, String> data) {
        for (Object key : jsonObject.keySet()) {
            Object obj = jsonObject.get((String) key);
            if (obj != null) {
                if (obj instanceof JSONArray) {
                    parseJSONArray((JSONArray) obj, data);
                } else if (obj instanceof JSONObject) {
                    parseJSONObject((JSONObject) obj, data);
                } else {
                    data.put((String) key, obj.toString());
                }
            }
        }
    }

    private void parseJSONArray(JSONArray jsonArray, HashMap<String, String> data) {
        for (int i = 0; i < jsonArray.length(); i++) {
            final Object obj = jsonArray.get(i);
            if (obj != null) {
                if (obj instanceof JSONArray) {
                    parseJSONArray((JSONArray) obj, data);
                } else if (obj instanceof JSONObject) {
                    parseJSONObject((JSONObject) obj, data);
                }
            }
        }
    }

    public HashMap<String, String> getDataFromJSON(String jsonString) {
        final HashMap<String, String> data = new HashMap<>();
        data.put(LINE_BREAK, "\n");
        if (jsonString != null && !jsonString.isEmpty()) {
            final JSONObject jsonObject = new JSONObject(jsonString);
            parseJSONObject(jsonObject, data);
        }
        return data;
    }

    private File generatePrintForm(File templateFile, HashMap<String, String> data) throws IOException, XmlException {
        boolean success = false;
        File resultFile = null;
        XWPFDocument doc = null;
        try (FileInputStream fis = new FileInputStream(templateFile)) {
            resultFile = File.createTempFile("report", ".tmp");
            doc = new XWPFDocument(fis);
            parseTables(doc, data);//удаляем таблицы не удоволетворяющие check_table и строки с check_table
            loopForTable(doc, data);
            cleanTable(doc.getTables());

            fillParagraphs(doc.getParagraphs(), data);

            doc.getTables().stream()
                    .flatMap(tbl -> tbl.getRows().stream())
                    .flatMap(row -> row.getTableCells().stream())
                    .forEach(cell -> fillParagraphs(cell.getParagraphs(), data));

            trimFinalDocument(doc);
        } catch (FileNotFoundException e) {
            log.error("Файл шаблона не найден!", e);
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода!", e);
        }
        if (doc != null) {
            try (FileOutputStream fos = new FileOutputStream(resultFile)) {
                if (pdfOptions == null) {
                    pdfOptions = PdfOptions.create();
                    pdfOptions.fontProvider((familyName, encoding, size, style, color) -> {
                        try {
                            BaseFont baseFont = fonts.get(familyName);
                            if (baseFont == null) {
                                baseFont = BaseFont.createFont(fontPath, encoding, BaseFont.EMBEDDED);
                                fonts.put(familyName, baseFont);
                            }
                            if (baseFont != null) {
                                return new Font(baseFont, size, style, color);
                            }
                        } catch (DocumentException | IOException e) {
                            log.error("Не удалось создать шрифт!", e);
                        }
                        return FontFactory.getFont(familyName, encoding, size, style, color);
                    });
                }
                PdfConverter.getInstance().convert(doc, fos, pdfOptions);
                success = true;
            } catch (FileNotFoundException e) {
                log.error("Временный файл не найден!", e);
            } catch (IOException e) {
                log.error("Ошибка ввода/вывода!", e);
            }
        }
        if (!success) {
            String errorMsg = "Не удалось создать ПФ по шаблону " + templateFile.getAbsolutePath();
            throw new IOException(errorMsg);
        }

        return resultFile;
    }


    private void fillParagraphs(List<XWPFParagraph> paragraphs, HashMap<String, String> data) {
        List<Integer> runsForDelete = new ArrayList<>();
        boolean deleteRun;
        StringBuilder textForReplace = new StringBuilder();
        for (XWPFParagraph p : paragraphs) {
            String paragraphText = p.getText();
//
            if (paragraphText.contains(TAG_FOR_SHOW_FULL_BLOG_OPEN)) {
                parseRunsByTagForShowFullBlog(p);
            }
            if (paragraphText.contains("${")) {
                if (p.getRuns() != null) {
                    parseRuns(p);
                }
                List<XWPFRun> runs = p.getRuns();
                runsForDelete.clear();
                deleteRun = false;
                textForReplace.setLength(0);
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        log.debug("Обработка фрагмента:\n {}", text);
                        if (text != null) {

                            if (text.contains("${")) {

                                deleteRun = true;
                            }

                            if (text.contains("}")) {

                                if (textForReplace.indexOf("${[") != -1 && textForReplace.indexOf("]}") != -1) {
                                    textForReplace.setLength(0);
                                }
                                textForReplace.append(text);
                                int pos = textForReplace.indexOf(QR_CODE);
                                if (pos > 0) {
                                    r.setText("", 0);
                                    textForReplace.replace(pos, pos + QR_CODE.length(), "");

                                } else {
                                    r.setText(calcExpression(textForReplace.toString(), data, p), 0);
                                    if (textForReplace.indexOf("${[") != -1 && textForReplace.indexOf("}") != -1) {
                                        textForReplace.setLength(0);
                                    }
                                }

                                deleteRun = false;
                            }
                        }
                        if (deleteRun) {

                            textForReplace.append(text);
//                            LOGGER.debug("Удалим фрагмент:\n {}", text);
                            runsForDelete.add(runs.indexOf(r));
                        }
                    }
                    // удалим фрагменты в обратном порядке (начиная с самого последнего),
                    // так как после удаления позиция следующих фрагментов меняется
                    for (int i = runsForDelete.size() - 1; i >= 0; i--) {
                        int pos = runsForDelete.get(i);
                        p.removeRun(pos);
                    }
                }
            }
        }
    }

    /**
     * Обрабатывает параграф. Вставляет текст параграфа в нулевой run.
     *
     * @param paragraph параграф для обработки
     */
    private void parseRuns(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        paragraph.getRuns().forEach(run -> run.setText("", 0));
        XWPFRun run = paragraph.getRuns().get(0);
        run.setText(text, 0);
    }

    /**
     * Обрабатывает параграф содержащий TAG_FOR_SHOW_FULL_BLOG_OPEN.
     *
     * @param paragraph параграф для обработки
     */
    private void parseRunsByTagForShowFullBlog(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            StringBuilder text = new StringBuilder(runs.get(i).getText(0));
            if (runs.get(i).getText(0) != null && text.toString().contains(TAG_FOR_SHOW_FULL_BLOG_OPEN) && !text.toString().contains(TAG_FOR_SHOW_FULL_BLOG_CLOSE)) {
                for (int j = i; j < runs.size() - 1; j++) {
                    if (text.toString().contains(TAG_FOR_SHOW_FULL_BLOG_OPEN) && text.toString().contains(TAG_FOR_SHOW_FULL_BLOG_CLOSE)) {
                        break;
                    }
                    text.append(runs.get(j + 1).getText(0));
                    paragraph.getRuns().get(j + 1).setText("", 0);
                }
                runs.get(i).setText(text.toString(), 0);
            }
        }
    }


    /**
     * Ищет в тексте поля вида [...], затем заменяет их значениями, найденными в data
     * Если в data значение по ключу не найдено, то поле заменяется словом "Ошибка!"
     *
     * @param text      строка, содержащая поля
     * @param data      значения полей в формате ключ-значение
     * @param paragraph параграф
     * @return строка с замененными полями на их значения
     */
    private String findAndReplaceFields(String text, HashMap<String, String> data, XWPFParagraph paragraph) {
        log.debug("Обработка текста, исходный текст: {} размер данных: {}", text, data.size());
        int pos;
        int pos2;
        int count = 0;
        while ((pos = text.indexOf("[")) != -1) {
            if (count == 5000) {
                log.warn("Бесконечный цикл в методе findAndReplaceFields! Значение: {}", text);
                return ERROR_FIELD_NAME;
            }
            if (paragraph != null && text.equals(PAGE_BREAK)) {
                paragraph.setPageBreak(true);
                text = "\"\"";
            }
            if (text.contains(TAG_FOR_SHOW_FULL_BLOG_OPEN)) {
                String personalAccount = data.get(PERSONAL_ACCOUNT_REPORT_CODE);
                return parseByPrivateOffice(personalAccount, text);
            }
            if (conditionTernaryText(text)) {
                String substring = text.substring(4, text.length() - 4);
                return substring.equals(" ") ? "\"\"" : substring;
            }
            if (VALUE_V.equals(text)) {
                return text;
            }
            if (text.equals(DATE_TAG)) {
                return text;
            }
            pos2 = text.indexOf("]", pos);
            if (pos2 > pos) {
                String fieldName = text.substring(pos + 1, pos2);
                log.debug("Найдено поле {}", fieldName);
                String fieldValue;
                if (fieldName.isEmpty()) {
                    fieldValue = ERROR_FIELD_NAME;
                    log.error("Ошибка в имени поля {}!", fieldName);
                } else {
                    fieldValue = data.get(fieldName);
                    log.debug("Найдено значение {} для поля {}", fieldValue, fieldName);
                }
                log.debug("Текст до замены: {}", text);
                if (fieldValue != null && isTernary(text)) {
                    String key;
                    int valuesPosition = text.indexOf(" ? ") + 3;// позиция начала значений.
                    // массив значений
                    String[] values = text.substring(valuesPosition)
                            .replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(" ", " ").split(" : ");
                    if (text.contains("&&") || text.contains("||")) {
                        boolean conditionValue = checkCondition(text.substring(0, text.indexOf("?")), data);
                        key = conditionValue ? values[0].trim() : values[1].trim();
                    } else {
                        String v = text.substring(text.indexOf("==") + 4);
                        String conditionValue = v.substring(0, v.indexOf("?") - 2); //получаем значение условия (например получаем B из [A] == "B" ? [x] : [y])
                        key = fieldValue.equals(conditionValue) ? values[0].trim() : values[1].trim();
                    }
                    text = "[" + key + "]";
                } else if (fieldValue == null) {
                    text = isTernary(text) ? "\"\"" : text.replace(text.substring(pos, pos2 + 1), "\"\"");
                } else {
                    text = text.replace("[" + fieldName + "]", "\"" + fieldValue + "\"");
                }
                log.debug("Текст после замены: {}", text);
            }
            count++;
        }
        log.debug("Результат обработки текста: {}", text);
        return text;
    }

    /**
     * Проверяет содержит ли текст тернарный оператор
     *
     * @param text строка, содержащая поля
     */
    private boolean isTernary(String text) {
        return text.contains("==") && text.contains("?") && text.contains(":");
    }

    private boolean conditionTernaryText(String text) {
        boolean isOpenTag = text.contains(OPEN_TAG_TERNARY_TEXT);
        boolean isCloseTag = text.contains(CLOSE_TAG_TERNARY_TEXT);
        boolean isDoubleEqual = text.contains("==");
        int indexOpenTag = text.indexOf(OPEN_TAG_TERNARY_TEXT);
        int indexDoubleEqual = text.indexOf("==");
        return isDoubleEqual && isOpenTag && isCloseTag ? indexOpenTag < indexDoubleEqual : isOpenTag && isCloseTag;
    }

    private String parseByPrivateOffice(String privateOfficeFromDeal, String text) { // PO - PrivateOffice
        if (privateOfficeFromDeal.equals(getPrivateOfficeFromText(text))) {
            int indexStartText = text.indexOf(TAG_FOR_SHOW_FULL_BLOG_OPEN) + TAG_FOR_SHOW_FULL_BLOG_OPEN.length();
            int indexEndText = text.lastIndexOf(TAG_FOR_SHOW_FULL_BLOG_CLOSE);
            return text.substring(indexStartText, indexEndText);
        }
        return " ";
    }

    private String getPrivateOfficeFromText(String text) {
        int startPositionNamePO = text.indexOf("] == ") + 6;
        int endPositionNamePO = startPositionNamePO + 1;
        return text.substring(startPositionNamePO, endPositionNamePO);
    }

    /**
     * Выполняет обработку выражения при помощи интерпретатора JavaScript
     *
     * @param text      текст выражения
     * @param data      данные в формате ключ-значение
     * @param paragraph параграф
     * @return результат выражения или сообщение об ошибке
     */
    private String calcExpression(String text, HashMap<String, String> data, XWPFParagraph paragraph) {
        log.info("(calc2) Обработка текста, исходный текст: {} ,size: {}", text, data.size());
        int pos;
        int pos2;
        int count = 0;
        if (text.contains(FOR_TAG)) {
            text = loopFor(text, data);
        }
        while ((pos = text.indexOf("${")) != -1) {
            if (count == 5000) {
                log.info("Бесконечный цикл в методе calcExpression! Значение: {}", text);
                return ERROR_FIELD_NAME;
            }
            pos2 = text.contains(TAG_FOR_SHOW_FULL_BLOG_OPEN) ? text.lastIndexOf("}") : text.indexOf("}", pos);
            log.info("(calc2) pos: {} pos2: {}", pos, pos2);
            if (pos2 != -1 && (pos + 2) < pos2) {

                String sourceExpression = text.substring(pos + 2, pos2);
                String expression = sourceExpression;
                log.info("(calc) Найдено выражение {}", expression);
                String resultExpression;
                if (expression.isEmpty()) {
                    resultExpression = ERROR_FIELD_NAME;
                    log.error("Ошибка в имени поля {}!", expression);
                } else {
                    // заменим кавычки
                    expression = expression.replace("«", "\"");
                    expression = expression.replace("»", "\"");
                    expression = expression.replace("“", "\"");
                    expression = expression.replace("„", "\"");
                    // заменим поля на строки
                    if (expression.contains("[")) {
                        expression = findAndReplaceFields(expression, data, paragraph);
                    }
                    try {
                        resultExpression = expression.equals(VALUE_V) ? "V" : removeQuotes(expression);
//                        resultExpression = (String) SCRIPT_ENGINE.eval(expression);
                        log.info("Найдено значение {} для выражения {}", resultExpression, expression);
                    } catch (Exception e) {
                        log.error("Ошибка вычисления выражения!", e);
                        resultExpression = ERROR_FIELD_NAME;
                    }
                }
//                LOGGER.debug("Текст до замены: {}", text);
                text = text.replace("${" + sourceExpression + "}", resultExpression != null ? resultExpression : "");
                log.debug("(calc) Текст после замены: {}", text);

            } else {
                log.warn("(calc) пропуск обработки pos: {} pos2: {}", pos, pos2);
            }


            if (validationTags(text, "${", "}")) {
                log.error("Ошибка в тексте {}!", text);
                return ERROR_FIELD_NAME;
            }
            count++;
        }
        log.debug("Результат обработки текста: {}", text);
        return text;
    }

    boolean validationTags(String text, String openTag, String closeTag) {
        return (text.contains(openTag) && !text.contains(closeTag)) || (!text.contains(openTag) && text.contains(closeTag));
    }

    public String removeQuotes(String field) throws ScriptException {
        if (field.equals(DATE_TAG)) {
            return setDate(field);
        }
        if (field.charAt(0) == 34 && field.charAt(field.length() - 1) == 34) {
            return field.substring(1, field.length() - 1);
        }
        return field;
    }

    private String setDate(String field) throws ScriptException {
        return (String) SCRIPT_ENGINE.eval(field);
    }

    /**
     * Обрабатываем цикл повторяющихся значений в тесте
     *
     * @param text текст содержащий ${for и }
     * @param data map репотд-код - значение
     * @return Объедененную строку обработаных значений
     */
    private String loopFor(String text, HashMap<String, String> data) {
        List<Integer> countRepeat = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        text = text.substring(FOR_TAG.length(), text.lastIndexOf("}"));
        String repeatValue = text.substring(text.indexOf("[") + 1, text.indexOf("]"));
        setRepeatCount(countRepeat, repeatValue, data);
        if (countRepeat.isEmpty()) {
            return "";
        }
        countRepeat = countRepeat.stream().distinct().collect(Collectors.toList());
        Collections.sort(countRepeat);
        for (Integer n : countRepeat) {
            int count = n + 1;
            String newText = text.replace(COUNT_FOR_LOOP + "+1", Integer.toString(count));
            newText = newText.replaceAll(COUNT_FOR_LOOP, n.toString());
            newText = conditionForLoop(newText, n, data);
            newText = findAndReplaceFields(newText, data, null).replaceAll("\"", "");
            result.append(newText.replaceAll(LINE_BREAK_FOR_LOOP, "\n").replaceAll("\"", ""));
        }
        log.info("Результат цикла для текста: " + text + "\n" + result);
        return result.toString();
    }

    /**
     * Удаляет таблицы не удоволетворяющие условию в CHECK_TABLE_TAG
     *
     * @param document документ
     */
    private void parseTables(XWPFDocument document, HashMap<String, String> data) {
        List<XWPFTable> tables = document.getTables();
        List<Integer> tablesPosition = new ArrayList<>();
        for (XWPFTable table : tables) {
            if (table.getText().contains(CHECK_TABLE_TAG)) {
                String text1 = table.getRows().get(0).getTableCells().get(0).getText();
                if (!checkCondition(text1, data)) {
                    tablesPosition.add(document.getPosOfTable(table));
                } else {
                    table.removeRow(0); //удаляем строку содержащую check_table
                }
            }
        }
        tablesPosition.sort(Collections.reverseOrder()); //удаляем таблицы с конца, т.к происходит смещение индексов таблиц
        log.info("Список таблиц для удаления: " + Arrays.toString(tablesPosition.toArray()));
        tablesPosition.forEach(document::removeBodyElement);
    }

    private String[] getPrintFormValueAndValue(String condition, HashMap<String, String> data) {
        String reportCode;
        String value;
        if (condition.contains(CHECK_TABLE_TAG)) {
            reportCode = condition.substring(condition.indexOf(CHECK_TABLE_TAG) + CHECK_TABLE_TAG.length(), condition.indexOf("]") + 1);
            value = condition.substring(condition.indexOf("«") + 1, condition.indexOf("»"));
        } else {
            reportCode = condition.substring(condition.indexOf("["), condition.indexOf("]") + 1);
            value = condition.substring(condition.indexOf("\"") + 1, condition.lastIndexOf("\""));
        }
        String reportValue = findAndReplaceFields(reportCode, data, null).replaceAll("\"", "");
        return new String[]{reportValue, value};
    }

    private boolean checkCondition(String conditions, HashMap<String, String> data) {
        log.info("Строка условия : " + conditions);
        try {
            if (!conditions.contains(CHECK_TABLE_TAG)) {
                while (conditions.contains("[")) {
                    String[] reportValueAndValue = getPrintFormValueAndValue(conditions.substring(conditions.indexOf("["), conditions.indexOf("\" ") + 1), data);
                    boolean resultCondition = reportValueAndValue[0].equals(reportValueAndValue[1]);
                    conditions = conditions.replace(conditions.substring(conditions.indexOf("["), conditions.indexOf("\" ") + 1), String.valueOf(resultCondition));
                }
            } else if (conditions.contains(CHECK_TABLE_TAG) && conditions.indexOf(CHECK_TABLE_TAG) == conditions.lastIndexOf(CHECK_TABLE_TAG)) {
                String[] reportValueAndValue = getPrintFormValueAndValue(conditions, data);
                return reportValueAndValue[0].equals(reportValueAndValue[1]);
            } else {
                while (conditions.contains(CHECK_TABLE_TAG)) {
                    String[] reportValueAndValue = getPrintFormValueAndValue(conditions, data);
                    boolean resultCondition = reportValueAndValue[0].equals(reportValueAndValue[1]);
                    conditions = conditions.replace(conditions.substring(conditions.indexOf(CHECK_TABLE_TAG), conditions.indexOf("}") + 1), String.valueOf(resultCondition));
                }
            }
            log.info("Выражение условия : " + conditions);
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");
            return (Boolean) engine.eval(conditions);
        } catch (Exception e) {
            log.error("Ошибка выражения условия : " + conditions, e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Обрабатываем цикл повторяющихся значений в таблицу
     *
     * @param document документ
     * @param data     map репорт-код - значение
     */
    private void loopForTable(XWPFDocument document, HashMap<String, String> data) throws IOException {
        List<Integer> countRepeat;
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            String tableText = table.getText();
            if (tableText.contains(TABLE_FOR_OPEN) && tableText.contains(TABLE_FOR_CLOSE)) {
                log.info("Цикл for для таблицы: " + table.getText());
                List<XWPFTableRow> rows = table.getRows();
                int rowSize = rows.size();
                int openRow = findRowByText(rows, TABLE_FOR_OPEN);
                int closeRow = findRowByText(rows, TABLE_FOR_CLOSE);
                countRepeat = setRepeatCountTable(rows, data, openRow, closeRow);
                int n = 0;
                for (int count : countRepeat) {
                    for (int i = (openRow + 1); i < rowSize; i++) {
                        XWPFTableRow row = rows.get(i);
                        XWPFTableRow copyRow = new XWPFTableRow((CTRow) row.getCtRow().copy(), table);
                        copyRow.getTableCells().stream().flatMap(cell -> cell.getParagraphs().stream())
                                .filter(paragraph -> !paragraph.getText().contains(TABLE_FOR_OPEN) || !paragraph.getText().contains(TABLE_FOR_CLOSE))
                                .flatMap(paragraph -> paragraph.getRuns().stream())
                                .forEach(run -> run.setText("", 0));
                        for (int k = 0; k < row.getTableCells().size(); k++) {
                            String textWithRepeatN = row.getTableCells().get(k).getText();
                            String result;
                            if (textWithRepeatN.isEmpty()) {
                                result = "";
                            } else if (textWithRepeatN.contains("[N=") && textWithRepeatN.contains("]")) {
                                int startValue = Integer.parseInt(textWithRepeatN.substring(textWithRepeatN.indexOf("=") + 1, textWithRepeatN.indexOf("]")));
                                result = n++ + startValue + textWithRepeatN.substring(textWithRepeatN.indexOf("]") + 1);
                            } else {
                                textWithRepeatN = conditionForLoop(textWithRepeatN, count, data);
                                String[] splitText = textWithRepeatN.replaceAll(COUNT_FOR_LOOP, String.valueOf(count))
                                        .split("]");
                                result = Arrays.stream(splitText).map(s -> s.contains("[") ? findAndReplaceFields(s + "]", data, null).replaceAll("\"", "") : s)
                                        .collect(Collectors.joining()).replaceAll(LINE_BREAK_FOR_LOOP, "\n");
                            }
                            List<XWPFTableCell> copyCells = copyRow.getTableCells();
                            if (CollectionUtils.isNotEmpty(copyCells.get(k).getParagraphs()) && CollectionUtils.isNotEmpty(copyCells.get(k).getParagraphs().get(0).getRuns())) {
                                copyCells.get(k).getParagraphs().get(0).getRuns().get(0).setText(result, 0);
                            } else {
                                XWPFParagraph paragraph = copyCells.get(k).getParagraphs().get(0) == null ? copyCells.get(k).addParagraph() : copyCells.get(k).getParagraphs().get(0);
                                XWPFRun run = paragraph.createRun();
                                run.setText(result, 0);
                            }
                        }
                        table.addRow(copyRow);
                    }
                }
            }
        }
    }

    private String conditionForLoop(String text, int count, HashMap<String, String> data) {
        while (text.contains(CONDITION_TAG_FOR_LOOP_OPEN) && text.contains(CONDITION_TAG_FOR_LOOP_CLOSE)) {
            int start = text.indexOf(CONDITION_TAG_FOR_LOOP_OPEN);
            int end = text.indexOf(CONDITION_TAG_FOR_LOOP_CLOSE);
            String conditionText = findAndReplaceFields(text.substring(start + CONDITION_TAG_FOR_LOOP_OPEN.length(), end)
                    .replace("N", String.valueOf(count)).replace("«@@", "\"@@")
                    .replace("@@»", "@@\""), data, null).replace("\"", "");
            text = text.substring(0, start - 1) + conditionText + text.substring(end + 4);
        }
        return text;
    }

    /**
     * Очищаем таблицы документа от строк содерщащих TABLE_FOR_OPEN, TABLE_FOR_CLOSE и шаблона между ними
     *
     * @param tables список таблиц в документе
     */
    private void cleanTable(List<XWPFTable> tables) {
        for (XWPFTable table : tables) {
            List<Integer> rowsForDelete = new ArrayList<>();
            List<XWPFTableRow> rows = table.getRows();
            for (int i = 0, rowsSize = rows.size(); i < rowsSize; i++) {
                XWPFTableRow row = rows.get(i);
                for (XWPFTableCell cell : row.getTableCells()) {
                    String text = cell.getText();
                    if (text.contains(TABLE_FOR_OPEN) || text.contains(TABLE_FOR_CLOSE)) {
                        rowsForDelete.add(i);
                        if (text.contains(TABLE_FOR_OPEN)) {
                            rowsForDelete.add(i + 1); //добавляем в список на удаление строку,содержащую repeatN и идущую после for${
                        }
                        break;
                    }
                }
            }
            for (int i : rowsForDelete.stream().distinct().sorted(Collections.reverseOrder()).collect(Collectors.toList())) {
                table.removeRow(i);
            }
        }
    }

    private int findRowByText(List<XWPFTableRow> rows, String text) {
        for (int i = 0, rowsSize = rows.size(); i < rowsSize; i++) {
            XWPFTableRow row = rows.get(i);
            for (XWPFTableCell cell : row.getTableCells()) {
                if (cell.getText().equals(text)) {
                    return i;
                }
            }
        }
        throw new RuntimeException("В таблице не найдена ячейка содержащая \"" + text + "\"");
    }

    /**
     * Возвращаем количество итераций для loopForTable()
     *
     * @param rows     список строк в таблице
     * @param data     map репотд-код - значение
     * @param openRow  индекс строки, содержащей открывающий тег
     * @param closeRow индекс строки, содержащей закрывающий тег
     */
    private List<Integer> setRepeatCountTable(List<XWPFTableRow> rows, HashMap<String, String> data, int openRow, int closeRow) {
        List<Integer> countRepeat = new ArrayList<>();
        for (int i = openRow + 1; i < closeRow; i++) {
            List<XWPFTableCell> tableCells = rows.get(i).getTableCells();
            for (XWPFTableCell cell : tableCells) {
                String cellText = cell.getText().replaceAll(" ", " ").split(" ")[0];
                // для исправления ошибки IndexOfBoundsException, по задаче 7764
                cellText = cellText.length() < 3 ? "" : cellText.substring(1, cellText.length() - 1);
                if (cellText.contains("repeatN") || cellText.equals(COUNT_FOR_LOOP)) {
                    setRepeatCount(countRepeat, cellText, data);
                    countRepeat = countRepeat.stream().distinct().collect(Collectors.toList());
                    Collections.sort(countRepeat);
                }
            }
        }
        return countRepeat.stream().distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    private void setRepeatCount(List<Integer> countRepeat, String text, HashMap<String, String> data) {
        String[] firstRepeat = text.split(COUNT_FOR_LOOP);
        for (String key : data.keySet()) {
            if (checkKeyAndText(key, firstRepeat)) {
                countRepeat.add(Integer.parseInt(key.substring(firstRepeat[0].length(), firstRepeat[0].length() + 1)));
            }
            if (firstRepeat.length == 1 && key.contains(firstRepeat[0])) {
                countRepeat.add(Integer.parseInt(key.substring(firstRepeat[0].length())));
            }
        }
    }

    private boolean checkKeyAndText(String key, String[] firstRepeat) {
        String f0 = firstRepeat[0];
        String f2 = firstRepeat.length > 1 ? firstRepeat[1] : null;
        return firstRepeat.length > 1
                && key.length() > f0.length() + f2.length()
                && key.startsWith(f0)
                && key.substring(f0.length() + 1, f0.length() + f2.length() + 1).equals(f2);
    }

    /**
     * Удаление пустых параграфов(переносов строк) в начале и конце обработанного документа
     *
     * @param document обработанный документ
     */
    private void trimFinalDocument(XWPFDocument document) {
        recursTrimStartDocument(document);
        recursTrimEndDocument(document);
    }

    private XWPFDocument recursTrimStartDocument(XWPFDocument document) {
        BodyElementType elementType = document.getBodyElements().get(0).getElementType();
        if (elementType.equals(BodyElementType.PARAGRAPH) && new XWPFWordExtractor(document).getText().startsWith("\n") && document.getParagraphs().get(0).getText().isEmpty()) {
            int posOfParagraphForRemove = document.getPosOfParagraph(document.getParagraphs().get(0));
            document.removeBodyElement(posOfParagraphForRemove);
            log.info("В начале документа удален параграф " + posOfParagraphForRemove);
            return recursTrimStartDocument(document);
        }
        return document;
    }

    private XWPFDocument recursTrimEndDocument(XWPFDocument document) {
        BodyElementType elementType = document.getBodyElements().get(document.getBodyElements().size() - 1).getElementType();
        if (elementType.equals(BodyElementType.PARAGRAPH) && new XWPFWordExtractor(document).getText().endsWith("\n") && document.getParagraphs().get(document.getParagraphs().size() - 1).getText().isEmpty()) {
            int posOfParagraphForRemove = document.getPosOfParagraph(document.getParagraphs().get(document.getParagraphs().size() - 1));
            log.info("В конце документа удален параграф " + posOfParagraphForRemove);
            document.removeBodyElement(posOfParagraphForRemove);
            return recursTrimEndDocument(document);
        }
        return document;
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


    public PrintForm upload(MultipartFile resource, String typeId) throws IOException {
        final PrintForm existingPrintForm = printFormRepository.findByTypeId(Long.valueOf(typeId));
        if (existingPrintForm != null) {
            printFormRepository.deleteById(existingPrintForm.getId());
        }

        PrintForm createdPrintForm = new PrintForm();

        createdPrintForm.setFileName(resource.getOriginalFilename());
        createdPrintForm.setName(Objects.requireNonNull(resource.getOriginalFilename()).substring(0, resource.getOriginalFilename().lastIndexOf('.')));
        createdPrintForm.setCreateDate(new Date());
        createdPrintForm.setTypeId(Long.valueOf(typeId));

        String uuid = FileManager.generateUuid(resource.getName());
        createdPrintForm.setUuid(uuid);
        createdPrintForm.setSize(resource.getSize());
        createdPrintForm = printFormRepository.save(createdPrintForm);
        FileManager.upload(resource.getBytes(), uuid, true);

        return createdPrintForm;
    }
}
