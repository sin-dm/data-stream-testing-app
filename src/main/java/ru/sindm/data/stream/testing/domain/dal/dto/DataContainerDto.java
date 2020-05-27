package ru.sindm.data.stream.testing.domain.dal.dto;

import ru.sindm.data.stream.testing.domain.model.OperationTypeEnum;

public class DataContainerDto {

    private String key;
    private String entryType;
    private OperationTypeEnum operation;
    private byte[] data;

    public DataContainerDto() {
    }

    public DataContainerDto(String key, String entryType, OperationTypeEnum operation, byte[] data) {
        this.key = key;
        this.entryType = entryType;
        this.operation = operation;
        this.data = data;
    }

    public static Builder newBuilder() {
        return new DataContainerDto().new Builder();
    }

    public String getKey() {
        return key;
    }

    public String getEntryType() {
        return entryType;
    }

    public OperationTypeEnum getOperation() {
        return operation;
    }

    public byte[] getData() {
        return data;
    }

    public class Builder {
        private Builder() {
        }

        public Builder setKey(String key) {
            DataContainerDto.this.key = key;
            return this;
        }

        public Builder setEntryType(String entryType) {
            DataContainerDto.this.entryType = entryType;
            return this;
        }

        public Builder setOperation(OperationTypeEnum operation) {
            DataContainerDto.this.operation = operation;
            return this;
        }

        public Builder setData(byte[] data) {
            DataContainerDto.this.data = data;
            return this;
        }

        public DataContainerDto build() {
            return DataContainerDto.this;
        }
    }
}