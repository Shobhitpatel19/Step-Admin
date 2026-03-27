package com.top.talent.management.dto;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class BenchHistoryResponse {

    private int total;
    private List<Result> results;


    @Data
    public static class Result {

        @JsonProperty("_key")
        private String key;

        @JsonProperty("_partition")
        private int partition;

        @JsonProperty("_offset")
        private int offset;

        @JsonProperty("_operation")
        private String operation;

        private Entity entity;


        @Data
        public static class Entity {

            @JsonProperty("person_id")
            private String personId;

            @JsonProperty("bench_records")
            private List<BenchRecord> benchRecords;

        }

        @Data
        public static class BenchRecord {

            @JsonProperty("start_date")
            private String startDate;

            @JsonProperty("end_date")
            private String endDate;

            private String status;
        }


    }}

