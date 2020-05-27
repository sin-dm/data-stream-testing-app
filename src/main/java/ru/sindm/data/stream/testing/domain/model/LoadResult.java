package ru.sindm.data.stream.testing.domain.model;

import ru.sindm.data.stream.testing.domain.dal.dto.DataContainerDto;

import java.util.List;

public class LoadResult {

    ResultCodeEnum resultCode;
    List<DataContainerDto> dataContainers;

    public ResultCodeEnum getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCodeEnum resultCode) {
        this.resultCode = resultCode;
    }

    public List<DataContainerDto> getDataContainers() {
        return dataContainers;
    }

    public void setDataContainers(List<DataContainerDto> dataContainers) {
        this.dataContainers = dataContainers;
    }
}
