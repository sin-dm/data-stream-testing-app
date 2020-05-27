package ru.sindm.data.stream.testing.test;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.sindm.data.stream.testing.DataStreamTestingApplication;
import ru.sindm.data.stream.testing.domain.dal.dto.DataContainerDto;
import ru.sindm.data.stream.testing.domain.model.LoadResult;
import ru.sindm.data.stream.testing.domain.model.OperationTypeEnum;
import ru.sindm.data.stream.testing.domain.model.ResultCodeEnum;
import ru.sindm.data.stream.testing.domain.service.TransportInterface;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.sindm.data.stream.testing.util.DataAdapterTestUtils.checkFileNotExist;
import static ru.sindm.data.stream.testing.util.DataAdapterTestUtils.prepareReferenceDataFile;
import static ru.sindm.data.stream.testing.util.DataAdapterTestUtils.readDataFile;

@SpringBootTest(classes = DataStreamTestingApplication.class)
class DataStreamTestingApplicationTests extends AbstractTestNGSpringContextTests {

    @Autowired
    TransportInterface dataService;

    @AfterMethod
    private void cleanup() throws IOException {
        for (File file : new File("data").listFiles()) {
            FileUtils.forceDelete(file);
        }
    }

    @DataProvider(name = "Data types and values")
    private Object[][] getDataTypesAndValues() {
        return new Object[][]{
                {String.class.getTypeName(), "Test data"},
                {Integer.class.getTypeName(), Integer.valueOf(123).toString()},
                {Long.class.getTypeName(), Long.valueOf(123L).toString()},
                {Object.class.getTypeName(), new Object().toString()}
        };
    }

    @Test(description = "Создание нового файла",
            dataProvider = "Data types and values")
    public void createNewDataFile(String typeName, String testData) {
        String dataKey = UUID.randomUUID().toString();
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                dataKey,
                                typeName,
                                OperationTypeEnum.CREATE,
                                testData.getBytes()
                        )));
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.OK));
        assertThat(readDataFile(dataKey, typeName), is(testData));
    }

    @Test(description = "Обновление файла",
            dataProvider = "Data types and values")
    public void updateExistDataFile(String typename, String testData) {
        String dataKey = prepareReferenceDataFile(typename);
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                dataKey,
                                typename,
                                OperationTypeEnum.UPDATE,
                                testData.getBytes()
                        )));
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.OK));
        assertThat(readDataFile(dataKey, typename), is(testData));
    }

    @Test(description = "Удаление файла",
            dataProvider = "Data types and values")
    public void deleteExistDataFile(String typeName, String testData) {
        String dataKey = prepareReferenceDataFile(typeName);
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        DataContainerDto.newBuilder()
                                .setKey(dataKey)
                                .setEntryType(typeName)
                                .setOperation(OperationTypeEnum.DELETE)
                                .build()
                )
        );
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.OK));
        assertThat(checkFileNotExist(dataKey, typeName), is(false));
    }

    @Test(description = "Создание нового файла с несколькими контейнерами",
            dataProvider = "Data types and values")
    public void createDataFilesWithMultipleContainer(String typeName, String testData) {
        String firstDataKey = UUID.randomUUID().toString();
        String secondDataKey = UUID.randomUUID().toString();
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                firstDataKey,
                                typeName,
                                OperationTypeEnum.CREATE,
                                testData.getBytes()
                        ),
                        new DataContainerDto(
                                secondDataKey,
                                typeName,
                                OperationTypeEnum.CREATE,
                                testData.getBytes()
                        )
                )
        );
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.OK));
        assertThat(readDataFile(firstDataKey, typeName), is(testData));
        assertThat(readDataFile(secondDataKey, typeName), is(testData));
    }

    @Test(description = "Передача дата объекта с разными операциями",
            dataProvider = "Data types and values")
    public void createDataFilesWithDifferentOperations(String typeName, String testData) {
        String dataKeyToCreate = UUID.randomUUID().toString();
        String dataKeyToUpdate = prepareReferenceDataFile(typeName);
        String dataKeyToDelete = prepareReferenceDataFile(typeName);
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                dataKeyToCreate,
                                typeName,
                                OperationTypeEnum.CREATE,
                                testData.getBytes()
                        ),
                        new DataContainerDto(
                                dataKeyToUpdate,
                                typeName,
                                OperationTypeEnum.UPDATE,
                                testData.getBytes()
                        ),
                        DataContainerDto.newBuilder()
                                .setKey(dataKeyToDelete)
                                .setEntryType(typeName)
                                .setOperation(OperationTypeEnum.DELETE)
                                .build()
                )
        );
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.OK));
        assertThat(readDataFile(dataKeyToCreate, typeName), is(testData));
        assertThat(readDataFile(dataKeyToUpdate, typeName), is(testData));
        assertThat(checkFileNotExist(dataKeyToDelete, typeName), is(false));
    }

    @Test(description = "Попытка завершить успешную транзакцию",
            dataProvider = "Data types and values")
    public void getFinishedTransaction(String typeName, String testData) {
        String dataKey = UUID.randomUUID().toString();
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                dataKey,
                                typeName,
                                OperationTypeEnum.CREATE,
                                testData.getBytes()
                        )));
        dataService.loadPartition(loadingId, loadResult);
        LoadResult finalResult = dataService.finish(loadingId);
        assertThat(finalResult.getResultCode(), is(ResultCodeEnum.OK));
        assertThat(finalResult.getDataContainers(), is(loadResult.getDataContainers()));
    }

    @Test(description = "Создание файла с неуникальным ключом (Negative)")
    public void createDataFileWithNonUniqueKey() {
        String dataKey = prepareReferenceDataFile(String.class.getTypeName());
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                dataKey,
                                String.class.getTypeName(),
                                OperationTypeEnum.CREATE,
                                "SomeData".getBytes()
                        )));
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.ERROR));
    }

    @Test(description = "Удаление несуществующего файла (Negative)")
    public void deleteDataFileThatNotExists() {
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                UUID.randomUUID().toString(),
                                String.class.getTypeName(),
                                OperationTypeEnum.DELETE,
                                "SomeData".getBytes()
                        )));
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.ERROR));
    }

    @Test(description = "Обновление несуществующего файла (Negative)")
    public void updateDataFileThatNotExists() {
        String loadingId = dataService.initLoad();
        LoadResult loadResult = new LoadResult();
        loadResult.setDataContainers(
                Arrays.asList(
                        new DataContainerDto(
                                UUID.randomUUID().toString(),
                                String.class.getTypeName(),
                                OperationTypeEnum.UPDATE,
                                "SomeData".getBytes()
                        )));
        ResultCodeEnum result = dataService.loadPartition(loadingId, loadResult);
        assertThat(result, is(ResultCodeEnum.ERROR));
    }

    @Test(description = "Попытка завершить не начатую транзакцию (Negative)")
    public void finishOpenTransaction() {
        String loadingId = dataService.initLoad();
        LoadResult finalResult = dataService.finish(loadingId);
        assertThat(finalResult.getResultCode(), is(ResultCodeEnum.ERROR));
    }
}