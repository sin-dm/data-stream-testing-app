package ru.sindm.data.stream.testing.domain.service;

import ru.sindm.data.stream.testing.domain.model.LoadResult;
import ru.sindm.data.stream.testing.domain.model.ResultCodeEnum;

public interface TransportInterface {

    String initLoad();

    ResultCodeEnum loadPartition(String loadingId, LoadResult data);

    LoadResult finish(String loadingId);
}
