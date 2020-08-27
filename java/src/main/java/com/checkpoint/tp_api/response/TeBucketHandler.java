package com.checkpoint.tp_api.response;

import com.checkpoint.tp_api.dir.TeQueryBucket;
import com.checkpoint.tp_api.logger.TeLogger;
import com.checkpoint.tp_api.query.*;
import com.checkpoint.tp_api.response.TeUploadResponse.*;
import com.checkpoint.tp_api.upload.TeUploadRequest.TeUploadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by edanha on 3/28/2017.
 */
public class TeBucketHandler {
    private TeQueryResponse teQueryResponse;
    private TeQueryBucket teQueryBucket;
    private ObjectMapper mapper;
    private final List<String> reports;
    private final List<String> features;
    private static Logger logger;
    private Map<String, Object> argMap;

    private final static class StatusCodes {
        private final static int FOUND = 1001;
        private final static int UPLOAD_SUCCESS = 1002;
        private final static int PENDING = 1003;
        private final static int NOT_FOUND = 1004;
        private final static int NO_QUOTA = 1005;
        private final static int PARTIALLY_FOUND = 1006;
        private final static int FILE_TYPE_NOT_SUPPORTED = 1007;
        private final static int BAD_REQUEST = 1008;
        private final static int INTERNAL_ERROR = 1009;
        private final static int FORBIDDEN = 1010;
    }

    public TeBucketHandler(Map<String, Object> argMap) throws Exception{
        mapper = new ObjectMapper(); //Json mapper
        logger = TeLogger.init(TeBucketHandler.class, argMap);
        reports = new ArrayList<>(); //For upload request object
        features = new ArrayList<>(); //For upload request object
        this.argMap = argMap;
        if ((boolean) argMap.get("p")) {
            reports.add("pdf");
        }
        if ((boolean) argMap.get("x")) {
            reports.add("xml");
        }
        if ((boolean) argMap.get("s")) {
            reports.add("summary");
        }
        if ((boolean) argMap.get("withTex")) {
            features.add("extraction");
        }
        features.add("te");
    }

    public void handleFilesBucket(TeQueryBucket teQueryBucket) throws Exception {
        this.teQueryBucket = teQueryBucket; //Holds the list of all the files and Cookie for POD
        queryAndGetResponses(); //query til found cycle
        for (Response response : teQueryResponse.getResponse()) {
            int responseCode = response.getStatus().getCode();

            if (responseCode == StatusCodes.FOUND) {
                downloadFiles(response); //Looks for reports/extracted files
                teQueryBucket.removeFileByMD5(response.getMd5()); //removing from list after found
                continue;
            }
            if (responseCode == StatusCodes.NOT_FOUND || responseCode == StatusCodes.PARTIALLY_FOUND) {
                uploadFile(response); //Same for TE or Extraction
                continue;
            }
            if (responseCode == StatusCodes.PENDING) {
                // no need to do anything
                continue;
            }
            if (responseCode == StatusCodes.FILE_TYPE_NOT_SUPPORTED) {
                // nothing to do here
                logger.error(teQueryBucket.getFileByMD5(response.getMd5()) + " is not supported for emulation");
                continue;
            }
            handleErrors(responseCode);
        }
    }

    private void queryAndGetResponses() throws Exception{
        TeQueryRequest request;
        if ((boolean) argMap.get("withTex")) {
            request = new TeQueryRequest(teQueryBucket.getFilesToQueryList(),
                    reports, features, (String) argMap.get("method")); //Creating Request using all md5's
        }
        else {
            request = new TeQueryRequest(teQueryBucket.getFilesToQueryList(),
                    reports, features);
        }
        String requestString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request); //Generating string from it
        logger.debug(requestString);
        teQueryResponse = mapper.readValue(teQueryBucket.getTeHttpClient().queryRequest(requestString), TeQueryResponse.class);
        logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(teQueryResponse));
    }

    private void uploadFile(Response response) throws Exception{
        File file = teQueryBucket.getFileByMD5(response.getMd5()); //Fetching the file to upload
        String uploadRequestString;
        if ((boolean) argMap.get("withTex")) {
            uploadRequestString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new TeUploadRequest(
                    FilenameUtils.getExtension(file.getName()), file.getName(),
                    reports, features, response.getMd5(), (String) argMap.get("method")));
        }
        else {
            uploadRequestString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new TeUploadRequest(
                    FilenameUtils.getExtension(file.getName()), file.getName(),
                    reports, features, response.getMd5()));
        }
        logger.debug(uploadRequestString);
        TexUploadResponse texUploadResponse = mapper.readValue(teQueryBucket.getTeHttpClient().uploadRequest(
                uploadRequestString, file), TexUploadResponse.class);
        logger.debug(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(texUploadResponse));
        int code = texUploadResponse.getResponse().getStatus().getCode();
        if (code != StatusCodes.UPLOAD_SUCCESS && code != StatusCodes.FOUND && code != StatusCodes.PENDING) {
            logger.error("Problem with " +
                    teQueryBucket.getFileByMD5(texUploadResponse.getResponse().getMd5()).getName());
        }
    }

    private void downloadFiles(Response response) throws Exception{
        downloadReports(response);
        downloadCleanedFiles(response);
    }

    private void downloadCleanedFiles(Response response) throws Exception {
        if (features.contains("extraction")) {
            String extractedFileDownloadId = response.getExtraction().getExtracted_file_download_id();
            if (extractedFileDownloadId != null) {
                teQueryBucket.getTeHttpClient().downloadFile(extractedFileDownloadId, (String) argMap.get("tex_folder"));
            }
            else {
                logger.info(response.getFile_name() + " could not be downloaded. extract_result: " +
                        response.getExtraction().getExtract_result());
            }
        }
    }

    private void downloadReports(Response response) throws Exception {
        for (Image image : response.getTe().getImages()) {
            String pdfReportId = image.getReport().getPdf_report();
            String xmlReportId = image.getReport().getXml_report();
            if (pdfReportId != null && reports.contains("pdf")) {
                teQueryBucket.getTeHttpClient().downloadFile(pdfReportId, (String) argMap.get("R"));
            }
            if (xmlReportId != null && reports.contains("xml")) {
                teQueryBucket.getTeHttpClient().downloadFile(xmlReportId, (String) argMap.get("R"));
            }
        }

        String summaryReportId = response.getTe().getSummary_report();
        if (summaryReportId != null && reports.contains("summary")) {
            teQueryBucket.getTeHttpClient().downloadFile(summaryReportId, (String) argMap.get("R"));
        }
    }

    private void handleErrors(int responseCode) {
        if (responseCode == StatusCodes.NO_QUOTA) {
            logger.error("No quota");
            return;
        }
        if (responseCode == StatusCodes.BAD_REQUEST) {
            logger.error("Request string is wrongly formatted");
            return;
        }
        if (responseCode == StatusCodes.FORBIDDEN) {
            logger.error("API_KEY not allowed");
            return;
        }
        if (responseCode == StatusCodes.INTERNAL_ERROR) {
            logger.error("An internal error has occurred");
            return;
        }
    }
}
