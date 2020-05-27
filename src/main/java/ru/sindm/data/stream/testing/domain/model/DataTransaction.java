package ru.sindm.data.stream.testing.domain.model;

public class DataTransaction {

    private TransactionStateEnum state;
    private String loadingId;
    private LoadResult loadResult;

    public DataTransaction(String loadingId) {
        this.loadingId = loadingId;
        this.state = TransactionStateEnum.OPEN;
    }

    public TransactionStateEnum getState() {
        return state;
    }

    public void changeState() {
        switch (this.state) {
            case OPEN: this.state = TransactionStateEnum.IN_PROGRESS;
            break;
            case IN_PROGRESS: this.state = TransactionStateEnum.FINISHED;
            break;
        }
    }

    public String getLoadingId() {
        return loadingId;
    }

    public LoadResult getLoadResult() {
        return loadResult;
    }

    public void setLoadResult(LoadResult loadResult) {
        this.loadResult = loadResult;
    }
}
