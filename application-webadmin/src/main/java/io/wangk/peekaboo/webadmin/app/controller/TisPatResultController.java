package io.wangk.peekaboo.webadmin.app.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.webadmin.app.vo.*;
import io.wangk.peekaboo.webadmin.app.dto.*;
import io.wangk.peekaboo.webadmin.app.model.*;
import io.wangk.peekaboo.webadmin.app.service.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.webadmin.config.ApplicationConfig;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 检查结果操作控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "检查结果管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/tisPatResult")
public class TisPatResultController {

    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private TisPatResultService tisPatResultService;

    /**
     * 新增检查结果数据。
     *
     * @param tisPatResultDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"tisPatResultDto.id"})
    @SaCheckPermission("tisPatResult.add")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody TisPatResultDto tisPatResultDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(tisPatResultDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TisPatResult tisPatResult = MyModelUtil.copyTo(tisPatResultDto, TisPatResult.class);
        tisPatResult = tisPatResultService.saveNew(tisPatResult);
        return ResponseResult.success(tisPatResult.getId());
    }

    /**
     * 更新检查结果数据。
     *
     * @param tisPatResultDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.update")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody TisPatResultDto tisPatResultDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(tisPatResultDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TisPatResult tisPatResult = MyModelUtil.copyTo(tisPatResultDto, TisPatResult.class);
        TisPatResult originalTisPatResult = tisPatResultService.getById(tisPatResult.getId());
        if (originalTisPatResult == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!tisPatResultService.update(tisPatResult, originalTisPatResult)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除检查结果数据。
     *
     * @param id 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.delete")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long id) {
        if (MyCommonUtil.existBlankArgument(id)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return this.doDelete(id);
    }

    /**
     * 批量删除检查结果数据。
     *
     * @param idList 待删除对象的主键Id列表。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.delete")
    @OperationLog(type = SysOperationLogType.DELETE_BATCH)
    @PostMapping("/deleteBatch")
    public ResponseResult<Void> deleteBatch(@MyRequestBody List<Long> idList) {
        if (MyCommonUtil.existBlankArgument(idList)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        for (Long id : idList) {
            ResponseResult<Void> responseResult = this.doDelete(id);
            if (!responseResult.isSuccess()) {
                return responseResult;
            }
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的检查结果列表。
     *
     * @param tisPatResultDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("tisPatResult.view")
    @PostMapping("/list")
    public ResponseResult<MyPageData<TisPatResultVo>> list(
            @MyRequestBody TisPatResultDto tisPatResultDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
        }
        TisPatResult tisPatResultFilter = MyModelUtil.copyTo(tisPatResultDtoFilter, TisPatResult.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, TisPatResult.class);
        List<TisPatResult> tisPatResultList =
                tisPatResultService.getTisPatResultListWithRelation(tisPatResultFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(tisPatResultList, TisPatResultVo.class));
    }

    /**
     * 导入主表数据列表。
     *
     * @param importFile 上传的文件，目前仅仅支持xlsx和xls两种格式。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.import")
    @OperationLog(type = SysOperationLogType.IMPORT)
    @PostMapping("/import")
    public ResponseResult<Void> importBatch(
            @RequestParam Boolean skipHeader,
            @RequestParam("importFile") MultipartFile importFile) throws IOException {
        String filename = ImportUtil.saveImportFile(appConfig.getUploadFileBaseDir(), null, importFile);
        // 这里可以指定需要忽略导入的字段集合。如创建时间、创建人、更新时间、更新人、主键Id和逻辑删除，
        // 以及一些存在缺省值且无需导入的字段。其中主键字段和逻辑删除字段不需要在这里设置，批量插入逻辑会自动处理的。
        Set<String> ignoreFieldSet = new HashSet<>();
        ignoreFieldSet.add("createTime");
        ignoreFieldSet.add("createUserId");
        ignoreFieldSet.add("updateTime");
        ignoreFieldSet.add("updateUserId");
        List<ImportUtil.ImportHeaderInfo> headerInfoList = ImportUtil.makeHeaderInfoList(TisPatResult.class, ignoreFieldSet);
        // 下面是导入时需要注意的地方，如果我们缺省生成的代码，与实际情况存在差异，请手动修改。
        // 1. 头信息数据字段，我们只是根据当前的主表实体对象生成了缺省数组，开发者可根据实际情况，对headerInfoList进行修改。
        ImportUtil.ImportHeaderInfo[] headerInfos = headerInfoList.toArray(new ImportUtil.ImportHeaderInfo[]{});
        // 2. 这里需要根据实际情况决定，导入文件中第一行是否为中文头信息，如果是可以跳过。这里我们默认为true。
        // 这里根据自己的实际需求，为doImport的最后一个参数，传递需要进行字典转换的字段集合。
        // 注意，集合中包含需要翻译的Java字段名，如: gradeId。
        Set<String> translatedDictFieldSet = new HashSet<>();
        List<TisPatResult> dataList =
                ImportUtil.doImport(headerInfos, skipHeader, filename, TisPatResult.class, translatedDictFieldSet);
        tisPatResultService.saveNewBatch(dataList, -1);
        return ResponseResult.success();
    }

    /**
     * 导出符合过滤条件的检查结果列表。
     *
     * @param tisPatResultDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @throws IOException 文件读写失败。
     */
    @SaCheckPermission("tisPatResult.export")
    @OperationLog(type = SysOperationLogType.EXPORT, saveResponse = false)
    @PostMapping("/export")
    public void export(
            @MyRequestBody TisPatResultDto tisPatResultDtoFilter,
            @MyRequestBody MyOrderParam orderParam) throws IOException {
        TisPatResult tisPatResultFilter = MyModelUtil.copyTo(tisPatResultDtoFilter, TisPatResult.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, TisPatResult.class);
        List<TisPatResult> resultList =
                tisPatResultService.getTisPatResultListWithRelation(tisPatResultFilter, orderBy);
        // 导出文件的标题数组
        // NOTE: 下面的代码中仅仅导出了主表数据，主表聚合计算数据和主表关联字典的数据。
        // 一对一从表数据的导出，可根据需要自行添加。如：headerMap.put("slaveFieldName.xxxField", "标题名称")
        Map<String, String> headerMap = new LinkedHashMap<>(11);
        headerMap.put("id", "主键Id");
        headerMap.put("patId", "患者ID");
        headerMap.put("projectName", "检测项目");
        headerMap.put("result", "检测结果");
        headerMap.put("remark1", "备用字段1");
        headerMap.put("remark2", "备用字段2");
        headerMap.put("remark3", "备用字段3");
        headerMap.put("createTime", "创建时间");
        headerMap.put("createUserId", "创建用户");
        headerMap.put("updateTime", "修改时间");
        headerMap.put("updateUserId", "修改用户");
        ExportUtil.doExport(resultList, headerMap, "tisPatResult.xlsx");
    }

    /**
     * 查看指定检查结果对象详情。
     *
     * @param id 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("tisPatResult.view")
    @GetMapping("/view")
    public ResponseResult<TisPatResultVo> view(@RequestParam Long id) {
        TisPatResult tisPatResult = tisPatResultService.getByIdWithRelation(id, MyRelationParam.full());
        if (tisPatResult == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TisPatResultVo tisPatResultVo = MyModelUtil.copyTo(tisPatResult, TisPatResultVo.class);
        return ResponseResult.success(tisPatResultVo);
    }

    private ResponseResult<Void> doDelete(Long id) {
        String errorMessage;
        // 验证关联Id的数据合法性
        TisPatResult originalTisPatResult = tisPatResultService.getById(id);
        if (originalTisPatResult == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!tisPatResultService.remove(id)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }
}
