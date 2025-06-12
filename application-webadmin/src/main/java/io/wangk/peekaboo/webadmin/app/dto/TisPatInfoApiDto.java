package io.wangk.peekaboo.webadmin.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class TisPatInfoApiDto {
    private String patname;
    private String batchno;
    private String age;
    private String projectid;
    private String sex;
    private String sampleno;
    private String patno;
    private String sampletype;
    private String operator;
    private String cutoffval;
    private String rangeval;
    private String picpath;
    private String filepath;
    private String testtime;
    private String testunit;
    private String teststat;
    private String address;
    private String serno;
    private List<TisPatResultInfoApiDto> result;

}
