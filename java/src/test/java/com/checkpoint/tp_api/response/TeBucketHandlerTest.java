package com.checkpoint.tp_api.response;

import com.checkpoint.tp_api.dir.FileToQuery;
import com.checkpoint.tp_api.dir.TeQueryBucket;
import com.checkpoint.tp_api.upload.TeHttpClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


import java.io.File;
import java.util.*;

import static org.mockito.Mockito.*;


/**
 * Created by edanha on 4/13/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TeBucketHandlerTest {
    private TeBucketHandler teBucketHandler;
    private List<FileToQuery> filesToQuery;
    private Map<String, Object> argMap;

    @Mock
    private File mockedFile;

    @Mock
    private TeHttpClient teHttpClient;

    @Mock
    private TeQueryBucket teQueryBucket;

    @Before
    public void init() throws Exception{
        MockitoAnnotations.initMocks(this);
        argMap = new HashMap<>();
        argMap.put("D", "");
        argMap.put("R", "");
        argMap.put("K", "key");
        argMap.put("debugFileName", null);
        argMap.put("d", true);
        argMap.put("p", true);
        argMap.put("x", true);
        argMap.put("r", true);
        argMap.put("withProxy", false);
        argMap.put("withTex", false);
        teBucketHandler = new TeBucketHandler(argMap);
        when(mockedFile.getName()).thenReturn("edan.pdf");
        filesToQuery = new ArrayList<>();
        filesToQuery.add(new FileToQuery("1", "2", mockedFile));
    }

    @Test
    public void testForResponseNotFound() throws Exception {
        when(teQueryBucket.getFilesToQueryList()).thenReturn(filesToQuery);
        when(teQueryBucket.getTeHttpClient()).thenReturn(teHttpClient);
        when(teQueryBucket.getFileByMD5(any())).thenReturn(mockedFile);

        when(teHttpClient.queryRequest(any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TE/responseNotFound.json")); //Query response

        when(teHttpClient.uploadRequest(any(), any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TE/uploadResponse.json")); //Upload response

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }

    @Test
    public void testForResponsePending() throws Exception {
        when(teQueryBucket.getFilesToQueryList()).thenReturn(filesToQuery);
        when(teQueryBucket.getTeHttpClient()).thenReturn(teHttpClient);

        when(teHttpClient.queryRequest(any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TE/responsePending.json"));

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }

    @Test
    public void testForMaliciousResponseFound() throws Exception{
        when(teQueryBucket.getFilesToQueryList()).thenReturn(filesToQuery);
        when(teQueryBucket.getTeHttpClient()).thenReturn(teHttpClient);

        when(teHttpClient.queryRequest(any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TE/maliciousResponseFound.json"));

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }

    @Test
    public void testForBenignResponseFound() throws Exception {
        when(teQueryBucket.getFilesToQueryList()).thenReturn(filesToQuery);
        when(teQueryBucket.getTeHttpClient()).thenReturn(teHttpClient);

        when(teHttpClient.queryRequest(any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TE/benignResponseFound.json"));

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }
}
