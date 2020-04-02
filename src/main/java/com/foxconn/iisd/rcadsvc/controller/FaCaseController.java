package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.domain.fa.FaCase;
import com.foxconn.iisd.rcadsvc.domain.fa.FaFile;
import com.foxconn.iisd.rcadsvc.domain.fa.FaSn;
import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.repo.*;
import com.foxconn.iisd.rcadsvc.service.FaCaseService;
import com.foxconn.iisd.rcadsvc.service.FileService;
import com.foxconn.iisd.rcadsvc.util.FormatUtils;
import joinery.DataFrame;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;


import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


@RestController
@RequestMapping("/fa")
public class FaCaseController {
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);




    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    @Autowired
    private FaCaseService faCaseService;

    @Autowired
    private FaCaseRepository faCaseRepository;

    @Autowired
    private FaSnRepository faSnRepository;

    @Autowired
    private FaFileRepository faFileRepository;

    @Autowired
    private SymptonRepository faSymptonRepository;

    @Autowired
    @Qualifier("minioFileService")
    private FileService fileService;

    @Value("${filePath}")
    private String filePath;
//    @Autowired
//    @Qualifier("minioFileService")
//    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    private static String BUCKET_CATEGORY = "fa_case";


    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "multipart/form-data")
    @RequiresAuthentication
    @RequiresPermissions(value={"fa_create:Create","fa_create:Update","fa_create:Delete","fa_create:View",
            "fa:Create","fa:Update","fa:Delete","fa:View"},logical= Logical.OR)
    public RestReturnMsg create(@ModelAttribute NewFaCaseMsg facMsg) {
        FaCase faCase = null;
        Integer code = null;
        String msg = null;
        try {
//            System.out.println((String) SecurityUtils.getSubject().getSession()
//                    .getAttribute(UserController.USERNAME_SESSION_KEY));

            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            FaCase _faCase = faCaseService.create(currentUser, facMsg.toFaCase());

            List<FaSn> faSnList = facMsg.generateFaSnList();
            List<FaFile> faFileList = facMsg.generateFaFileList();
            faSnList.forEach(faSn -> faSn.setFaCase(_faCase));
            faFileList.forEach(faFile -> {
                try {
                    fileService.save(_faCase.getId(), BUCKET_CATEGORY, faFile.getObjectName(), faFile.getFile());
                    faFile.setFaCase(_faCase);
                    faFile.clearId();
                } catch (Exception e) {
                    logger.error("save uploaded fa object failed: ", e);
                }
            });
            faSnRepository.saveAll(faSnList);
            faFileRepository.saveAll(faFileList);
            faCase = _faCase;

            code = 200;
            msg = "Query FA Tests successfully!!!";
        } catch (Exception e) {
            code = 500;
            msg = "Failing to query FA Tests!!!";
            logger.error(msg, e);
        }
        return new RestReturnMsg(code, msg, faCase);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = "multipart/form-data")
    @RequiresAuthentication
    public RestReturnMsg update(@ModelAttribute NewFaCaseMsg facMsg,@PathVariable Long id) {
        FaCase faCase = null;
        Integer code = null;
        String msg = null;
        try {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            facMsg.setId(id);
            FaCase _faCase = faCaseService.update(currentUser, facMsg.toFaCase());
            List<FaSn> faSnList = facMsg.generateFaSnList();
            List<FaFile> faFileList = facMsg.generateFaFileList();
            //faSn
            facMsg.getSnDel().forEach(idSN -> faSnRepository.deleteById(idSN));
            faSnList.forEach(faSn -> faSn.setFaCase(_faCase));
            faSnRepository.saveAll(faSnList);
            //faFile
            facMsg.getRefFileDel().forEach(idFile -> faFileRepository.deleteById(idFile));
            faFileList.forEach(faFile -> {
                try {
                    fileService.save(_faCase.getId(), BUCKET_CATEGORY, faFile.getObjectName(), faFile.getFile());
                    faFile.setFaCase(_faCase);
                    faFile.clearId();
                } catch (Exception e) {
                    logger.error("save uploaded fa object failed: ", e);
                }
            });
            faFileRepository.saveAll(faFileList);
            faCase = _faCase;

            code = 200;
            msg = "Query FA Tests successfully!!!";
        } catch (Exception e) {
            code = 500;
            msg = "Failing to query FA Tests!!!";
            logger.error(msg, e);
        }
        return new RestReturnMsg(code, msg, faCase);
    }

    @GetMapping("")
    @RequiresAuthentication
    @RequiresPermissions(value={"fa_list:Create","fa_list:Update","fa_list:Delete","fa_list:View",
            "fa:Create","fa:Update","fa:Delete","fa:View"},logical= Logical.OR)
    public RestReturnMsg queryFaCases(String product, String riskType,
                                      String startTimestamp, String stopTimestamp,
                                      String createUser, String failSymptom) {
        LocalDateTime startTime = null;
        LocalDateTime stopTime = null;
        if (startTimestamp != null) {
            startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(startTimestamp)), TimeZone
                    .getDefault().toZoneId());
        }
        if (stopTimestamp != null) {
            stopTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(stopTimestamp)), TimeZone
                    .getDefault().toZoneId());
        }
//        LocalDateTime startTime = startTimeStr.isEmpty()? null:
        List<FaCase> faCaseList = faCaseService.queryFaCases(product, riskType, startTime, stopTime, createUser, failSymptom);
        //copy to model
        Object[] responseSet = faCaseList.stream().map(faCase -> {
            NewFaCaseMsg model = new NewFaCaseMsg();
            try {
                BeanUtils.copyProperties(faCase, model);
                model.setTestStartTimeStr(FormatUtils.dateTimeToString(faCase.getTestStartTime()));
                if (faCase.getAnalyzeFinishTime() != null) {
                    model.setAnalyzeFinishTimeStr(FormatUtils.dateTimeToString(faCase.getAnalyzeFinishTime()));
                }
                model.setSymptomName(faCase.getSymptom().getName());
                model.setSymptomType(faCase.getSymptom().getType());
                model.setCreateTimeStr(FormatUtils.dateTimeToString(faCase.getCreateTime()));
                model.setUpdateTimeStr(FormatUtils.dateTimeToString(faCase.getUpdateTime()));
                model.setCreateUsername(faCase.getCreateUser().getUsername());
                model.setUpdateUsername(faCase.getUpdateUser().getUsername());
            } catch (Exception e) {
                logger.error("queryFaCase: convert from faCase to NewFaCaseMsg model error: ", e);
            }
            return model;
        }).toArray();

        return new RestReturnMsg(200, "Query Test Symptons successfully!!!",
                responseSet);
    }

    @RequiresAuthentication
    @GetMapping("/{id}/export")
    public @ResponseBody
    ResponseEntity<InputStreamResource> InputStreamResource(@PathVariable Long id) {
        List<String> srcFilesList = new ArrayList<>();
        List<FaCase> faCaseList = faCaseService.queryFaCaseById(id);

        Date now = new Date();
        int year = now.getYear() + 1900;
        int month = now.getMonth() + 1;
        int day = now.getDate();
        String path = filePath + "/fa_case" + year + month + day + id + "/";
        File fw = new File(path);
        if (!fw.isDirectory()) {
            fw.mkdirs();
        }

        //建立excel
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(id.toString());
        // 新增表頭行
        HSSFRow hssfRow = sheet.createRow(0);
        // 設定單元格格式居中
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 新增表頭內容
        String[] headArray = {"客戶", "產品", "線別", "測試工站", "分析徵狀", "徵狀描述", "徵狀發生時間", "分析完成時間", "案例建立時間"
                , "根因分析", "不良因子分類", "分析過程說明", "關鍵根因說明", "長短期方案", "最後行動"};
        for (int i = 0; i < headArray.length; i++) {
            HSSFCell headCell = hssfRow.createCell(i);
            headCell.setCellValue(headArray[i]);
            headCell.setCellStyle(cellStyle);
        }
        if (faCaseList.size() > 0) {
            // 新增資料內容
            FaCase facase = faCaseList.get(0);
            hssfRow = sheet.createRow(1);
            int i = 0;
            HSSFCell cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getCustomer());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getProduct());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getLine());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getStation());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getSymptom().getName());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getFailureDesc());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(FormatUtils.dateTimeToString(facase.getTestStartTime()));
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            if (facase.getAnalyzeFinishTime() != null) {
                cell.setCellValue(FormatUtils.dateTimeToString(facase.getAnalyzeFinishTime()));
            }
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(FormatUtils.dateTimeToString(facase.getCreateTime()) + "(" + facase.getCreateUser().getUsername() + ")");
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getRootCause());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getRiskType());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getAnalyzeDesc());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getRootCauseDesc());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getPlan());
            cell.setCellStyle(cellStyle);
            i++;
            cell = hssfRow.createCell(i);
            cell.setCellValue(facase.getAction());
            cell.setCellStyle(cellStyle);
            i++;
            try {
                OutputStream outputStream = new FileOutputStream(path + id + ".xls");
                workbook.write(outputStream);
                outputStream.close();
                srcFilesList.add(id + ".xls");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String sql = "select id from fa_file where fa_case_id = " + id;
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
        try {
            if (resultList.size() > 0) {
                for (Map<String, Object> row : resultList) {
                    FaFile faFile = faFileRepository.findById((Long) row.get("id")).get();
                    String name = faFile.getObjectName();
                    try {
                        InputStreamResource resource = new InputStreamResource(fileService.find(id, BUCKET_CATEGORY, name));

                        String filename = URLEncoder.encode(faFile.getOriginalFileName(), "UTF-8");

                        InputStream test = fileService.find(id, BUCKET_CATEGORY, name);

                        File targetFile = new File(path + faFile.getOriginalFileName());
                        OutputStream outStream = new FileOutputStream(targetFile);
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = test.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }
                        IOUtils.closeQuietly(test);
                        IOUtils.closeQuietly(outStream);
                        srcFilesList.add(faFile.getOriginalFileName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            byte[] buffer = new byte[8 * 1024];
            for (int i = 0; i < srcFilesList.size(); i++) {
                File srcFile = new File(path + srcFilesList.get(i));
                FileInputStream fis = new FileInputStream(srcFile);
                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                // close the InputStream
                fis.close();
            }
            // close the ZipOutputStream
            zos.close();

            File testfile = new File(path);
            if (testfile.isDirectory()) {
                for (File deleteMe : testfile.listFiles()) {
                    // recursive delete
                    System.out.println(deleteMe.delete());
                }
            }
            boolean find = testfile.delete();

            byte[] initialArray = baos.toByteArray();
            InputStream targetStream = new ByteArrayInputStream(initialArray);
            InputStreamResource resourcetest = new InputStreamResource(targetStream);
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                    "filename=" + id + ".zip" + "; " +
                    "filename*=utf-8''" + id + ".zip");
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resourcetest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{id}/references/defect")
    public RestReturnMsg queryFaSnByFaCase(@PathVariable(value = "id") long id) {
        return new RestReturnMsg(200, "Query Test Symptons successfully!!!",
                faSnRepository.findByFaCase(id));
    }

    @GetMapping("/{id}/references/objects")
    public RestReturnMsg queryFaFilesByFaCase(@PathVariable(value = "id") long id) {
        return new RestReturnMsg(200, "Query Test Symptons successfully!!!",
                faFileRepository.findByFaCase(id));
    }

    @GetMapping("/fa_case/{case_id}/references/objects/{file_id}")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable(value = "case_id") long caseId, @PathVariable(value = "file_id") long fileId) {
        FaFile faFile = faFileRepository.findById(fileId).get();

        try {
            String name = faFile.getObjectName();
            InputStreamResource resource = new InputStreamResource(fileService.find(caseId, BUCKET_CATEGORY, name));
            String filename = URLEncoder.encode(faFile.getOriginalFileName(), "UTF-8");

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                    "filename=" + filename + "; " +
                    "filename*=utf-8''" + filename);
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(faFile.getSizeInBytes().longValue())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (Exception e) {
            logger.error("get file object failed: ", e);
        }
        return null;
    }



    @GetMapping("/symptoms")
    public RestReturnMsg queryProductSymptoms() {
        Map<String, List<String>> productSymptoms = new HashMap<>();
        faCaseRepository.findProductSymptoms().stream().forEach(row -> {
            String key = row[0].toString().trim();
            String value = row[1].toString();
            if (!key.isEmpty()) {
                List<String> symptoms = (productSymptoms.get(key) == null) ? new ArrayList<>() : productSymptoms.get(key);
                symptoms.add(value);
                productSymptoms.put(key, symptoms);
            }
        });
        return new RestReturnMsg(200, "Query Products successfully!!!",
                productSymptoms);
    }

    @GetMapping("/creators")
    public RestReturnMsg queryCreateUsers() {
        return new RestReturnMsg(200, "Query Create Users successfully!!!",
                faCaseRepository.findCreateUser());
    }




}
