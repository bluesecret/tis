package io.wangk.peekaboo.webadmin.app.api;

import cn.dev33.satoken.annotation.SaIgnore;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import io.wangk.peekaboo.webadmin.app.dto.TisPatInfoApiDto;
import io.wangk.peekaboo.webadmin.app.dto.TisPatResultInfoApiDto;
import io.wangk.peekaboo.webadmin.app.model.TisPatInfo;
import io.wangk.peekaboo.webadmin.app.model.TisPatResult;
import io.wangk.peekaboo.webadmin.app.service.TisPatInfoService;
import io.wangk.peekaboo.webadmin.app.service.TisPatResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "患者信息管理接口")
@Slf4j
@RestController
@RequestMapping("/api/pat")
public class TisPatInfoApi {

    @Autowired
    private TisPatInfoService tisPatInfoService;

    @Autowired
    private TisPatResultService tisPatResultService;

    @Autowired
    private IdGeneratorWrapper idGeneratorWrapper;
    @SaIgnore
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<Void> save(
            @RequestBody TisPatInfoApiDto tisPatInfoApiDto) {
        TisPatInfo tisPatInfo = new TisPatInfo();
        tisPatInfo.setId(idGeneratorWrapper.nextLongId());
        tisPatInfo.setPatName(tisPatInfoApiDto.getPatname());
        tisPatInfo.setBatchNo(tisPatInfoApiDto.getBatchno());
        tisPatInfo.setAge(tisPatInfoApiDto.getAge());
        tisPatInfo.setProjectId(tisPatInfoApiDto.getProjectid());
        tisPatInfo.setSex(tisPatInfoApiDto.getSex());
        tisPatInfo.setSampleNo(tisPatInfoApiDto.getSampleno());
        tisPatInfo.setPatNo(tisPatInfoApiDto.getPatno());
        tisPatInfo.setSampleType(tisPatInfoApiDto.getSampletype());
        tisPatInfo.setOperator(tisPatInfoApiDto.getOperator());
        tisPatInfo.setCutoffVal(tisPatInfoApiDto.getCutoffval());
        tisPatInfo.setRangeVal(tisPatInfoApiDto.getRangeval());
        tisPatInfo.setPicPath(tisPatInfoApiDto.getPicpath());
        tisPatInfo.setFilePath(tisPatInfoApiDto.getFilepath());
        tisPatInfo.setTestTime(tisPatInfoApiDto.getTesttime());
        tisPatInfo.setTestUnit(tisPatInfoApiDto.getTestunit());
        tisPatInfo.setTestStat(tisPatInfoApiDto.getTeststat());
        tisPatInfoService.save(tisPatInfo);

        List<TisPatResultInfoApiDto> result = tisPatInfoApiDto.getResult();
        if(result != null && result.size() > 0) {
            List<TisPatResult> details = result.stream().map(tisPatResultInfoApiDto -> {
                TisPatResult tisPatResult = new TisPatResult();
                tisPatResult.setId(idGeneratorWrapper.nextLongId());
                tisPatResult.setPatId(tisPatInfo.getId());
                tisPatResult.setProjectName(tisPatResultInfoApiDto.getProjectname());
                tisPatResult.setResult(tisPatResultInfoApiDto.getResult());
                return tisPatResult;
            }).collect(Collectors.toList());
            tisPatResultService.saveBatch(details);
        }
        return ResponseResult.success();
    }
}
