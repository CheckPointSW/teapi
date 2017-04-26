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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by edanha on 4/19/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TxBucketHandlerTest {
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
        argMap.put("withTex", true);
        argMap.put("method", "convert");
        argMap.put("tex_folder", "");
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
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TEX/TEXResponseNotFound.json"));

        when(teHttpClient.uploadRequest(any(), any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TEX/TEXUploadResponse.json"));

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }


    @Test
    public void testForResponsePending() throws Exception {
        when(teQueryBucket.getFilesToQueryList()).thenReturn(filesToQuery);
        when(teQueryBucket.getTeHttpClient()).thenReturn(teHttpClient);

        when(teHttpClient.queryRequest(any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TEX/TEXResponsePending.json"));

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }

    @Test
    public void testForResponseFound() throws Exception {
        when(teQueryBucket.getFilesToQueryList()).thenReturn(filesToQuery);
        when(teQueryBucket.getTeHttpClient()).thenReturn(teHttpClient);

        when(teHttpClient.queryRequest(any())).
                thenReturn(getClass().getClassLoader().getResourceAsStream("jsonExamples/TEX/TEXResponseFound.json"));

        teBucketHandler.handleFilesBucket(teQueryBucket);
    }
}
