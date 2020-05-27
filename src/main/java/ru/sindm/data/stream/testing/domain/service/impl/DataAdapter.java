package ru.sindm.data.stream.testing.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sindm.data.stream.testing.domain.dal.dto.DataContainerDto;
import ru.sindm.data.stream.testing.domain.model.DataTransaction;
import ru.sindm.data.stream.testing.domain.model.LoadResult;
import ru.sindm.data.stream.testing.domain.model.ResultCodeEnum;
import ru.sindm.data.stream.testing.domain.model.TransactionStateEnum;
import ru.sindm.data.stream.testing.domain.service.FileService;
import ru.sindm.data.stream.testing.domain.service.TransportInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class DataAdapter implements TransportInterface {

    private final FileService fileService;

    private static final Map<String, DataTransaction> transactionMap = new HashMap<>();

    public DataAdapter(@Autowired FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public String initLoad() {
        String loadingUid = UUID.randomUUID().toString();
        transactionMap.put(loadingUid, new DataTransaction(loadingUid));
        return loadingUid;
    }

    @Override
    public ResultCodeEnum loadPartition(String loadingId, LoadResult data) {
        if (transactionMap.get(loadingId).getState().equals(TransactionStateEnum.IN_PROGRESS) &&
                transactionMap.get(loadingId).getState().equals(TransactionStateEnum.FINISHED)) {
            return ResultCodeEnum.ERROR;
        }
        Set<ResultCodeEnum> setOfCodes = new HashSet<>();
        transactionMap.get(loadingId).changeState();
        data.getDataContainers().forEach(dataContainer -> setOfCodes.add(process(dataContainer)));
        if (setOfCodes.contains(ResultCodeEnum.ERROR)) {
            data.setResultCode(ResultCodeEnum.ERROR);
            transactionMap.get(loadingId).setLoadResult(data);
            return ResultCodeEnum.ERROR;
        } else {
            data.setResultCode(ResultCodeEnum.OK);
            transactionMap.get(loadingId).setLoadResult(data);
            return ResultCodeEnum.OK;
        }
    }

    @Override
    public LoadResult finish(String loadingId) {
        DataTransaction dataTransaction = transactionMap.get(loadingId);
        if (dataTransaction.getState().equals(TransactionStateEnum.FINISHED)) {
            return dataTransaction.getLoadResult();
        } else if (dataTransaction.getState().equals(TransactionStateEnum.OPEN)) {
            dataTransaction.setLoadResult(new LoadResult());
            dataTransaction.getLoadResult().setResultCode(ResultCodeEnum.ERROR);
            return dataTransaction.getLoadResult();
        } else {
            transactionMap.get(loadingId).changeState();
            return transactionMap.get(loadingId).getLoadResult();
        }
    }

    private ResultCodeEnum process(DataContainerDto dataContainer) {
        switch (dataContainer.getOperation()) {
            case CREATE:
                return createDataFile(dataContainer);
            case UPDATE:
                return updateDataFile(dataContainer);
            case DELETE:
                return deleteDataFile(dataContainer);
            default:
                throw new UnsupportedOperationException("Operation not supported");
        }
    }

    private ResultCodeEnum createDataFile(DataContainerDto dataContainer) {
        String folderName = fileService.createFolder(dataContainer.getEntryType());
        try {
            fileService.createFile(folderName, dataContainer.getKey(), dataContainer.getData());
            return ResultCodeEnum.OK;
        } catch (Exception e) {
            return ResultCodeEnum.ERROR;
        }
    }

    private ResultCodeEnum updateDataFile(DataContainerDto dataContainer) {
        String folderName = fileService.createFolder(dataContainer.getEntryType());
        try {
            fileService.updateFile(folderName, dataContainer.getKey(), dataContainer.getData());
            return ResultCodeEnum.OK;
        } catch (Exception e) {
            return ResultCodeEnum.ERROR;
        }
    }

    private ResultCodeEnum deleteDataFile(DataContainerDto dataContainer) {
        String folderName = fileService.createFolder(dataContainer.getEntryType());
        try {
            fileService.deleteFile(folderName, dataContainer.getKey());
            return ResultCodeEnum.OK;
        } catch (Exception e) {
            return ResultCodeEnum.ERROR;
        }
    }
}