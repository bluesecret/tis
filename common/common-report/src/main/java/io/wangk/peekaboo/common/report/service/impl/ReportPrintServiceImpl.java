package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.*;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.cell.CellUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.FieldFilterType;
import io.wangk.peekaboo.common.core.constant.ObjectFieldType;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.upload.BaseUpDownloader;
import io.wangk.peekaboo.common.core.upload.UpDownloaderFactory;
import io.wangk.peekaboo.common.core.upload.UploadResponseInfo;
import io.wangk.peekaboo.common.core.upload.UploadStoreInfo;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.dbutil.object.DatasetFilter;
import io.wangk.peekaboo.common.dbutil.object.DatasetParam;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.constant.FilterValueType;
import io.wangk.peekaboo.common.report.constant.PrintRenderType;
import io.wangk.peekaboo.common.report.dao.ReportPrintMapper;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import io.wangk.peekaboo.common.report.model.ReportPrint;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import io.wangk.peekaboo.common.report.model.constant.DateShowFormat;
import io.wangk.peekaboo.common.report.model.constant.ReportRelationType;
import io.wangk.peekaboo.common.report.object.*;
import io.wangk.peekaboo.common.report.object.sheet.SpreadSheetData;
import io.wangk.peekaboo.common.report.service.ReportDatasetColumnService;
import io.wangk.peekaboo.common.report.service.ReportDatasetRelationService;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import io.wangk.peekaboo.common.report.service.ReportPrintService;
import io.wangk.peekaboo.common.report.util.ReportOperationHelper;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wiki.xsx.core.pdf.component.barcode.XEasyPdfBarCode;
import wiki.xsx.core.pdf.component.image.XEasyPdfImage;
import wiki.xsx.core.pdf.component.image.XEasyPdfImageType;
import wiki.xsx.core.pdf.component.table.XEasyPdfCell;
import wiki.xsx.core.pdf.component.table.XEasyPdfRow;
import wiki.xsx.core.pdf.component.table.XEasyPdfTable;
import wiki.xsx.core.pdf.component.text.XEasyPdfText;
import wiki.xsx.core.pdf.doc.*;
import wiki.xsx.core.pdf.handler.XEasyPdfHandler;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 报表打印数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportPrintService")
public class ReportPrintServiceImpl extends BaseService<ReportPrint, Long> implements ReportPrintService {

    @Autowired
    private ReportProperties reportProperties;
    @Autowired
    private ReportPrintMapper reportPrintMapper;
    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDatasetRelationService reportDatasetRelationService;
    @Autowired
    private ReportDatasetColumnService reportDatasetColumnService;
    @Autowired
    private ReportOperationHelper reportOperationHelper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;

    private static final float PDF_SCALE_DOWN = 0.72F;
    private static final float BARCODE_IMAGE_SCALE = 2.5F;
    private static final float EXCEL_COLUMN_WIDTH_FACTOR = 1023.0F;
    private static final float EXCEL_COLUMN_HEIGHT_FACTOR = 255.0F;
    private static final String DOWNLOAD_IMAGE_URL = "/admin/commonext/util/downloadSessionImage?filename=";

    /**
     * 表示查询不出任何数据的过滤对象。
     */
    private static final DatasetFilter NO_DATA_FILTER = new DatasetFilter();

    @Override
    protected BaseDaoMapper<ReportPrint> mapper() {
        return reportPrintMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportPrint saveNew(ReportPrint reportPrint) {
        if (StrUtil.isNotBlank(reportPrint.getSheetDataJson())) {
            reportPrint.setTemplateDataJson(this.doSpreadSheetParse(reportPrint, reportPrint.getSheetDataJson()));
        }
        reportPrintMapper.insert(this.buildDefaultValue(reportPrint));
        return reportPrint;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportPrint reportPrint, ReportPrint originalReportPrint) {
        TokenData tokenData = TokenData.takeFromRequest();
        reportPrint.setTenantId(tokenData.getTenantId());
        reportPrint.setAppCode(tokenData.getAppCode());
        reportPrint.setCreateUserId(originalReportPrint.getCreateUserId());
        reportPrint.setUpdateUserId(tokenData.getUserId());
        reportPrint.setCreateTime(originalReportPrint.getCreateTime());
        reportPrint.setUpdateTime(new Date());
        if (StrUtil.isNotBlank(reportPrint.getSheetDataJson())) {
            reportPrint.setTemplateDataJson(this.doSpreadSheetParse(reportPrint, reportPrint.getSheetDataJson()));
        }
        return reportPrintMapper.updateById(reportPrint) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long printId) {
        return reportPrintMapper.deleteById(printId) == 1;
    }

    @Override
    public List<ReportPrint> getReportPrintList(ReportPrint filter, String orderBy) {
        if (filter == null) {
            filter = new ReportPrint();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        filter.setAppCode(tokenData.getAppCode());
        filter.setTenantId(tokenData.getTenantId());
        return reportPrintMapper.getReportPrintList(filter, orderBy);
    }

    @Override
    public List<ReportPrint> getReportPrintListWithRelation(ReportPrint filter, String orderBy) {
        List<ReportPrint> resultList = this.getReportPrintList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    private ReportPrint buildDefaultValue(ReportPrint reportPrint) {
        reportPrint.setPrintId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportPrint.setTenantId(tokenData.getTenantId());
        reportPrint.setCreateUserId(tokenData.getUserId());
        reportPrint.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportPrint.setCreateTime(now);
        reportPrint.setUpdateTime(now);
        reportPrint.setAppCode(tokenData.getAppCode());
        return reportPrint;
    }

    @Override
    public CallResult render(String printVariable, List<ReportPrintParam> printParamList, int renderType) {
        LambdaQueryWrapper<ReportPrint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportPrint::getPrintVariable, printVariable);
        ReportPrint reportPrint = reportPrintMapper.selectOne(queryWrapper);
        if (reportPrint == null) {
            return CallResult.error("数据验证失败，打印模板标识 [" + printVariable + "] 不存在！");
        }
        return this.render(reportPrint, printParamList, renderType);
    }

    @Override
    public CallResult render(ReportPrint reportPrint, List<ReportPrintParam> printParamList, int renderType) {
        CallResult result = this.makeAndVerifyReportPrint(reportPrint);
        if (!result.isSuccess()) {
            return result;
        }
        // 验证每一个模板片段中配置的数据集及其关联数据，在当前的运行时是否正确。
        // 并在验证后，将模板片段所需的数据结构与其绑定，以备后用。
        result = this.makeAndVerifyFragments(reportPrint);
        if (!result.isSuccess()) {
            return result;
        }
        try {
            String conntentDispostionKey = "Content-Disposition";
            if (renderType == PrintRenderType.PDF) {
                XEasyPdfDocument doc = XEasyPdfHandler.Document.build();
                result = this.makePdfDoc(reportPrint, printParamList, doc);
                if (!result.isSuccess()) {
                    return result;
                }
                HttpServletResponse response = ContextUtil.getHttpResponse();
                response.setContentType("application/pdf;chartset=utf-8");
                response.setHeader(conntentDispostionKey, "attachment;filename=preview.pdf");
                doc.save(response.getOutputStream()).close();
            } else if (renderType == PrintRenderType.EXCEL) {
                ExcelWriter writer = ExcelUtil.getWriter();
                result = this.makeExcel(reportPrint, printParamList, writer);
                if (!result.isSuccess()) {
                    return result;
                }
                HttpServletResponse response = ContextUtil.getHttpResponse();
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
                response.setHeader(conntentDispostionKey, "attachment;filename=preview.xls");
                ServletOutputStream out = response.getOutputStream();
                writer.flush(out, true);
                writer.close();
                IoUtil.close(out);
            } else if (renderType == PrintRenderType.HTML) {
                result = this.bindDataToFragment(reportPrint, printParamList.get(0));
                if (!result.isSuccess()) {
                    return result;
                }
                JSONObject jsonData = new JSONObject();
                this.doRenderToJson(reportPrint, jsonData);
                HttpServletResponse response = ContextUtil.getHttpResponse();
                response.setContentType("application/json;chartset=utf-8");
                ServletOutputStream out = response.getOutputStream();
                out.write(JSON.toJSONString(jsonData).getBytes(StandardCharsets.UTF_8));
                out.flush();
                IoUtil.close(out);
            } else if (renderType == PrintRenderType.WORD) {
                result = this.bindDataToFragment(reportPrint, printParamList.get(0));
                if (!result.isSuccess()) {
                    return result;
                }
                HttpServletResponse response = ContextUtil.getHttpResponse();
                response.setContentType("application/octet-stream");
                response.setHeader(conntentDispostionKey, "attachment;filename=preview.docx");
                ServletOutputStream out = response.getOutputStream();
                XWPFTemplate template = this.renderToWord(reportPrint);
                template.writeAndClose(out);
            }
        } catch (IOException e) {
            throw new MyRuntimeException(e);
        }
        return CallResult.ok();
    }

    @Override
    public boolean existByPrintVariable(String printVariable) {
        ReportPrint filter = new ReportPrint();
        filter.setPrintVariable(printVariable);
        return CollUtil.isNotEmpty(this.getReportPrintList(filter, null));
    }

    private CallResult makeExcel(ReportPrint reportPrint, List<ReportPrintParam> printParamList, ExcelWriter writer)
            throws IOException {
        CallResult result = CallResult.ok();
        int rowOffset = 0;
        for (ReportPrintParam printParam : printParamList) {
            result = this.bindDataToFragment(reportPrint, printParam);
            if (!result.isSuccess()) {
                return result;
            }
            rowOffset += this.doRenderToExcel(reportPrint, writer, rowOffset) + 2;
        }
        return result;
    }

    private XWPFTemplate renderToWord(ReportPrint reportPrint) throws IOException {
        LoopRowTableRenderPolicy loopPolicy = new LoopRowTableRenderPolicy();
        ConfigureBuilder configBuilder = Configure.builder();
        Map<String, Object> m = new HashMap<>();
        for (ReportPrintFragment fragment : reportPrint.getFragmentList()) {
            if (BooleanUtil.isTrue(fragment.getLoop())) {
                configBuilder.bind(fragment.getVariableName(), loopPolicy);
                m.put(fragment.getVariableName(), fragment.getData());
            } else {
                if (CollUtil.isNotEmpty(fragment.getData())) {
                    m.put(fragment.getVariableName(), fragment.getData().get(0));
                }
            }
            if (CollUtil.isNotEmpty(fragment.getDatasetColumnInfo())) {
                for (ReportPrintFragment.DatasetColumnInfo c : fragment.getDatasetColumnInfo()) {
                    this.processWordImageData(c, fragment);
                    this.processWordDataFormat(c, fragment);
                }
            }
        }
        String fieldName = "wordTemplate";
        String fieldJsonData = (String) ReflectUtil.getFieldValue(reportPrint, fieldName);
        List<UploadResponseInfo> uploadInfos = JSON.parseArray(fieldJsonData, UploadResponseInfo.class);
        UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(ReportPrint.class, fieldName);
        BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
        InputStream in = upDownloader.getInputSream(reportProperties.getUploadFileBaseDir(),
                ReportPrint.class.getSimpleName(), fieldName, uploadInfos.get(0).getFilename(), false);
        Configure config = configBuilder.build();
        return XWPFTemplate.compile(in, config).render(m);
    }

    private void processWordDataFormat(ReportPrintFragment.DatasetColumnInfo c, ReportPrintFragment fragment) {
        List<Map<String, Object>> dataMapList = fragment.getData();
        if (BooleanUtil.isFalse(fragment.getLoop())) {
            dataMapList = CollUtil.sub(dataMapList, 0, 1);
        }
        for (Map<String, Object> dataMap : dataMapList) {
            if (c.getDateFormat() == null) {
                continue;
            }
            Object value = dataMap.get(c.getColumnName());
            if (ObjectUtil.equals(c.getDateFormat(), DateShowFormat.CN)) {
                String v = DateUtil.format((Date) value, MyDateUtil.COMMON_CN_SHORT_DATETIME_FORMAT);
                dataMap.put(c.getColumnName(), v);
            }
        }
    }

    private void processWordImageData(ReportPrintFragment.DatasetColumnInfo c, ReportPrintFragment fragment)
            throws IOException {
        if (!ObjectUtil.equals(c.getDataType(), ReportPrintFragment.IMAGE_DATA)
                && !ObjectUtil.equals(c.getDataType(), ReportPrintFragment.BARCODE_DATA)
                && !ObjectUtil.equals(c.getDataType(), ReportPrintFragment.QRCODE_DATA)) {
            return;
        }
        List<Map<String, Object>> dataMapList = fragment.getData();
        if (BooleanUtil.isFalse(fragment.getLoop())) {
            dataMapList = CollUtil.sub(dataMapList, 0, 1);
        }
        for (Map<String, Object> dataMap : dataMapList) {
            byte[] bytes;
            String dataValue = dataMap.get(c.getColumnName()).toString();
            if (c.getDataType().equals(ReportPrintFragment.IMAGE_DATA)) {
                List<UploadResponseInfo> infos = JSON.parseArray(dataValue, UploadResponseInfo.class);
                bytes = this.getImageDataBytes(infos.get(0));
            } else {
                bytes = this.createBarCodeBytes(
                        c.getImageWidth(), c.getImageHeight(), c.getBarCodeType(), c.getBarCodeShowWords(), dataValue);
            }
            if (BooleanUtil.isFalse(c.getAutoSize())) {
                dataMap.put(c.getColumnName(),
                        Pictures.ofStream(new ByteArrayInputStream(bytes)).size(c.getImageWidth(), c.getImageHeight()).create());
            } else {
                dataMap.put(c.getColumnName(), Pictures.ofStream(new ByteArrayInputStream(bytes)).fitSize().create());
            }
        }
    }

    private CallResult makePdfDoc(ReportPrint reportPrint, List<ReportPrintParam> printParamList, XEasyPdfDocument doc) {
        for (ReportPrintParam printParam : printParamList) {
            CallResult result = this.bindDataToFragment(reportPrint, printParam);
            if (!result.isSuccess()) {
                return result;
            }
            this.doRenderToPdf(reportPrint, doc);
        }
        return CallResult.ok();
    }

    private CallResult bindDataToFragment(ReportPrint reportPrint, ReportPrintParam printParam) {
        if (CollUtil.isEmpty(reportPrint.getFragmentList())) {
            return CallResult.ok();
        }
        for (ReportPrintFragment fragment : reportPrint.getFragmentList()) {
            ReportDataset dataset = fragment.getReportDataset();
            if (dataset.getDatasetType().equals(DatasetType.API)) {
                List<ReportFilterParam> filterParams = this.makeFragementFilterParams(fragment, printParam);
                MyPageData<Map<String, Object>> dataResult =
                        reportOperationHelper.getApiDataList(dataset, filterParams, null, null);
                List<Map<String, Object>> resultDataList = this.extractFragmentData(fragment, dataResult.getDataList());
                fragment.setData(resultDataList);
            } else {
                // 在获取模板片段数据的过程中，会验证查询数据集的参数，在运行时是否正确。同时与当前请求的参数值结合，
                // 构建数据集的查询SQL，然后再查询结果集中的数据、绑定的字典、一对一和一对多的关联数据。
                TypedCallResult<List<Map<String, Object>>> dataResult =
                        this.getFragmentDataList(reportPrint, fragment, printParam);
                if (!dataResult.isSuccess()) {
                    return CallResult.error(dataResult.getErrorMessage());
                }
                fragment.setData(dataResult.getData());
            }
        }
        return CallResult.ok();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractFragmentData(
            ReportPrintFragment fragment, List<Map<String, Object>> resultDataList) {
        if (CollUtil.isNotEmpty(fragment.getBindColumnPath()) && CollUtil.isNotEmpty(resultDataList)) {
            Map<String, Object> resultData = resultDataList.get(0);
            for (int i = 1; i < fragment.getBindColumnPath().size(); i++) {
                Object columnData = resultData.get(fragment.getBindColumnPath().get(i));
                if (i == fragment.getBindColumnPath().size() - 1) {
                    if (columnData instanceof List) {
                        return (List<Map<String, Object>>) columnData;
                    }
                    return CollUtil.newArrayList((Map<String, Object>) columnData);
                }
                resultData = (Map<String, Object>) columnData;
            }
        }
        return resultDataList;
    }

    private List<ReportFilterParam> makeFragementFilterParams(ReportPrintFragment fragment, ReportPrintParam printParam) {
        if (CollUtil.isEmpty(fragment.getDatasetFilterParams())) {
            return new LinkedList<>();
        }
        List<ReportFilterParam> filterParams = new LinkedList<>();
        for (ReportFilterParam filterParam : fragment.getDatasetFilterParams()) {
            if (filterParam.getFilterValueType().equals(FilterValueType.PRINT_INPUT_PARAM)) {
                ReportFilterParam clonedFilterParam = BeanUtil.copyProperties(filterParam, ReportFilterParam.class);
                clonedFilterParam.setParamValue(printParam.stream()
                        .filter(p -> p.getParamName().equals(filterParam.getParamValue()))
                        .map(p -> p.getParamValue() != null ? p.getParamValue() : CollUtil.join(p.getParamValueList(), ","))
                        .findFirst().orElse(null));
                filterParams.add(clonedFilterParam);
            }
        }
        return filterParams;
    }

    private CallResult makeAndVerifyReportPrint(ReportPrint reportPrint) {
        // 从JSON中解析FragmentList的数据。
        List<ReportPrintFragment> fragmentList =
                JSON.parseArray(reportPrint.getFragmentJson(), ReportPrintFragment.class);
        if (CollUtil.isEmpty(fragmentList)) {
            return CallResult.ok();
        }
        reportPrint.setFragmentList(fragmentList);
        String errorMessage;
        // 从FragmentList中提取出所有绑定的数据源集合。
        Set<Long> datasetIds = fragmentList.stream()
                .map(ReportPrintFragment::getDatasetId).collect(Collectors.toSet());
        // 这里取出了数据集的所有关联数据，以备后用。
        List<ReportDataset> datasetList =
                reportDatasetService.getInListWithRelation(datasetIds, MyRelationParam.full());
        reportPrint.setDatasetMap(datasetList.stream()
                .collect(Collectors.toMap(ReportDataset::getDatasetId, c -> c)));
        // 如果查询datasetIds的数量和结果集返回的数据集列表数量不一致，就需要尽可能提前的定位错误，并返回。
        // 这里后面的处理逻辑，可以更加简单流畅。
        if (datasetList.size() != datasetIds.size()) {
            for (ReportPrintFragment fragment : fragmentList) {
                if (!reportPrint.getDatasetMap().containsKey(fragment.getDatasetId())) {
                    errorMessage = "数据验证失败，模板片段 [{}] 所使用的数据集Id [{}] 并不存在!";
                    return CallResult.error(StrFormatter.format(
                            errorMessage, fragment.getShowName(), fragment.getDatasetId()));
                }
            }
        }
        // 如果数据集中包含关联，这里需要提前把数据集关联所依赖的从数据集，也一起保存到当前打印模板对象的数据集Map中，以备后用。
        Set<Long> relationSlaveDatasetIds = new HashSet<>();
        datasetList.stream().filter(c -> CollUtil.isNotEmpty(c.getRelationList())).forEach(c -> {
            reportDatasetRelationService.buildRelationForDataList(c.getRelationList(), MyRelationParam.dictOnly());
            Set<Long> slaveDatasetIdSet = c.getRelationList().stream()
                    .map(ReportDatasetRelation::getSlaveDatasetId)
                    .filter(r -> !reportPrint.getDatasetMap().containsKey(r)).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(slaveDatasetIdSet)) {
                relationSlaveDatasetIds.addAll(slaveDatasetIdSet);
            }
        });
        if (CollUtil.isNotEmpty(relationSlaveDatasetIds)) {
            List<ReportDataset> slaveDatasetList =
                    reportDatasetService.getInListWithRelation(relationSlaveDatasetIds, MyRelationParam.full());
            reportPrint.getDatasetMap().putAll(slaveDatasetList.stream()
                    .collect(Collectors.toMap(ReportDataset::getDatasetId, c -> c)));
            // 因为批量查询relationSlaveDatasetIds所关联的数据集列表效率会更高。
            // 所以这里对错误定位的处理逻辑会相对比较复杂，毕竟错误场景是极少数的。
            // 我们还是要尽可能保证极大概率场景的运行时效率。
            if (relationSlaveDatasetIds.size() != slaveDatasetList.size()) {
                return CallResult.error(this.nonexistSlaveDatasetIdError(reportPrint, relationSlaveDatasetIds));
            }
        }
        for (ReportDataset d : reportPrint.getDatasetMap().values()) {
            d.setColumnMap(d.getColumnList().stream()
                    .collect(Collectors.toMap(ReportDatasetColumn::getColumnName, c -> c)));
        }
        return CallResult.ok();
    }

    private CallResult makeAndVerifyFragments(ReportPrint reportPrint) {
        if (CollUtil.isEmpty(reportPrint.getFragmentList())) {
            return CallResult.ok();
        }
        for (ReportPrintFragment fragment : reportPrint.getFragmentList()) {
            ReportDataset dataset = reportPrint.getDatasetMap().get(fragment.getDatasetId());
            fragment.setReportDataset(dataset);
            CallResult result = this.verifyAndFillFragmentRelationMap(fragment, dataset);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return CallResult.ok();
    }

    private CallResult verifyAndFillFragmentRelationMap(ReportPrintFragment fragment, ReportDataset dataset) {
        Map<Long, ReportDatasetRelation> datasetRelationMap = null;
        if (CollUtil.isNotEmpty(dataset.getRelationList())) {
            datasetRelationMap = dataset.getRelationList()
                    .stream().collect(Collectors.toMap(ReportDatasetRelation::getRelationId, c -> c));
        }
        String errorMessage;
        // 如果不为空，通常就表示为有且只有一个的一对多的关联了。
        if (fragment.getRelationId() != null) {
            fragment.setRelationMap(new HashMap<>(1));
            ReportDatasetRelation r =
                    MapUtil.get(datasetRelationMap, fragment.getRelationId(), ReportDatasetRelation.class);
            if (r == null) {
                errorMessage = "数据验证失败，模板片段 [{}] 所使用的数据集 [{}] 中，并不存在数据集关联Id [{}]！";
                return CallResult.error(StrFormatter.format(errorMessage,
                        fragment.getShowName(), dataset.getDatasetName(), fragment.getRelationId()));
            }
            if (!r.getRelationType().equals(ReportRelationType.ONE_TO_MANY)) {
                errorMessage = "数据验证失败，模板片段 [{}] 所使用的数据集 [{}] 关联 [{}]，只能是一对多类型的关联！";
                return CallResult.error(StrFormatter.format(errorMessage,
                        fragment.getShowName(), dataset.getDatasetName(), r.getVariableName()));
            }
            fragment.getRelationMap().put(r.getVariableName(), r);
        } else {
            if (CollUtil.isNotEmpty(dataset.getRelationList())) {
                List<ReportDatasetRelation> oneToOneRelationList = dataset.getRelationList().stream()
                        .filter(r -> r.getRelationType().equals(ReportRelationType.ONE_TO_ONE))
                        .collect(Collectors.toList());
                if (CollUtil.isNotEmpty(oneToOneRelationList)) {
                    fragment.setRelationMap(oneToOneRelationList.stream()
                            .collect(Collectors.toMap(ReportDatasetRelation::getVariableName, c -> c)));
                }
            }
        }
        return CallResult.ok();
    }

    private TypedCallResult<List<Map<String, Object>>> getFragmentDataList(
            ReportPrint print, ReportPrintFragment fragment, ReportPrintParam printParam) {
        TypedCallResult<DatasetFilter> filterResult = this.makeFragmentFilter(print, fragment, printParam);
        if (!filterResult.isSuccess()) {
            return TypedCallResult.errorFrom(filterResult);
        }
        DatasetFilter filter = filterResult.getData();
        // 该过滤对象表示不会查询出任何数据了。比如查询从表的数据时，主表没有查询到数据，从表也就不会存在关联数据了。
        if (filter != null && filter.equals(NO_DATA_FILTER)) {
            return TypedCallResult.ok(null);
        }
        DatasetParam param = new DatasetParam();
        param.setFilter(filter);
        param.setOrderBy(fragment.getOrderBy());
        boolean fromSlaveDataset = fragment.getRelationId() != null;
        if (fromSlaveDataset) {
            // Fragment要从一对多从数据集取数据，取出的数据只是包含从表数据和关联的字典数据。
            ReportDatasetRelation r = fragment.getRelationMap().values().iterator().next();
            ReportDataset slaveDataset = print.getDatasetMap().get(r.getSlaveDatasetId());
            ReportResultSet<Map<String, Object>> result =
                    reportDatasetService.getDataListWithRelation(slaveDataset, param);
            return TypedCallResult.ok(result.getDataList());
        }
        Map<String, ReportPrintParam.FilterInfo> printFilterInfoMap = null;
        if (CollUtil.isNotEmpty(printParam)) {
            printFilterInfoMap = printParam.stream()
                    .collect(Collectors.toMap(ReportPrintParam.FilterInfo::getParamName, c -> c));
        }
        param.setSqlFilter(this.makeMasterDatasetFilter(fragment, printFilterInfoMap));
        // Fragment要从主数据集取数据，在取出主数据集数据的同时，还要取出与其关联的一对一从数据集的数据。
        ReportResultSet<Map<String, Object>> result =
                reportDatasetService.getDataListWithRelation(fragment.getReportDataset(), param);
        if (!ReportResultSet.isEmpty(result) && MapUtil.isNotEmpty(fragment.getRelationMap())) {
            for (Map.Entry<String, ReportDatasetRelation> entry : fragment.getRelationMap().entrySet()) {
                reportDatasetService.buildDataListWithRelation(result.getDataList(), entry.getValue());
            }
        }
        return TypedCallResult.ok(result.getDataList());
    }

    private TypedCallResult<DatasetFilter> makeFragmentFilter(
            ReportPrint print, ReportPrintFragment fragment, ReportPrintParam printParam) {
        // 如果当前打印模板片段中没有过滤参数，就直接返回了。
        if (CollUtil.isEmpty(fragment.getFilterParams())) {
            return TypedCallResult.ok(null);
        }
        boolean fromSlaveDataset = fragment.getRelationId() != null;
        DatasetFilter filter = new DatasetFilter();
        // 这里是外部调用传入的参数值对象。
        Map<String, ReportPrintParam.FilterInfo> printFilterInfoMap = null;
        if (CollUtil.isNotEmpty(printParam)) {
            printFilterInfoMap = printParam.stream()
                    .collect(Collectors.toMap(ReportPrintParam.FilterInfo::getParamName, c -> c));
        }
        for (ReportFilterParam filterParam : fragment.getFilterParams()) {
            TypedCallResult<DatasetFilter.FilterInfo> result =
                    this.makeAndVerifyFilterParam(print, fragment, filterParam, printFilterInfoMap);
            if (!result.isSuccess()) {
                return TypedCallResult.errorFrom(result);
            }
            // 如果返回的参数中没有过滤对象，则视为可以忽略的过滤。
            DatasetFilter.FilterInfo filterInfo = result.getData();
            if (filterInfo == null) {
                continue;
            }
            // 如果当前Fragment的relationId不等于NULL，说明该Fragment的数据集是主数据集的一对多从数据集。
            // 同时，如果当前过滤参数依赖的datasetId是Fragment主数据集Id，那么需要基于该参数，去主数据集
            // 中先行过滤，然后再根据主从数据集的关联关系，计算出绑定一对多relationId真正需要的过滤参数对象。
            if (fromSlaveDataset && filterInfo.getDatasetId().equals(fragment.getDatasetId())) {
                // 构建主表的查询对象。通常只是返回一条数据。
                DatasetParam datasetParam = new DatasetParam();
                DatasetFilter masterFilter = new DatasetFilter();
                masterFilter.add(filterInfo);
                datasetParam.setFilter(masterFilter);
                datasetParam.setSqlFilter(this.makeMasterDatasetFilter(fragment, printFilterInfoMap));
                // 获取当前relationId和主数据集的关联关系，再通过这些关系，同于基于主数据集的查询结果，
                // 为Fragment所依赖的从数据集计算关联参数。
                ReportDatasetRelation r = fragment.getRelationMap().entrySet().iterator().next().getValue();
                String masterColumnName = (String) r.getMasterColumnIdDictMap().get("name");
                String slaveColumnName = (String) r.getSlaveColumnIdDictMap().get("name");
                Object masterData = reportDatasetService.getDataByColumnName(
                        fragment.getReportDataset(), datasetParam, masterColumnName);
                // 如果主数据集中的关联字段为空值，这样是没法去与从数据集的字段进行关联的，所以也返回空结果集。
                if (masterData == null) {
                    return TypedCallResult.ok(NO_DATA_FILTER);
                }
                filterInfo = new DatasetFilter.FilterInfo();
                filterInfo.setParamName(slaveColumnName);
                filterInfo.setFilterType(FieldFilterType.EQUAL);
                filterInfo.setParamValue(masterData);
            }
            filter.add(filterInfo);
        }
        return TypedCallResult.ok(filter);
    }

    private DatasetFilter makeMasterDatasetFilter(
            ReportPrintFragment fragment, Map<String, ReportPrintParam.FilterInfo> printFilterInfoMap) {
        if (CollUtil.isEmpty(fragment.getDatasetFilterParams())) {
            return null;
        }
        List<ReportFilterParam> masterDatasetFilterParams = fragment.getDatasetFilterParams()
                .stream().filter(f -> f.getRelationId() == null).collect(Collectors.toList());
        if (CollUtil.isEmpty(masterDatasetFilterParams)) {
            return null;
        }
        DatasetFilter masterDatasetFilter = new DatasetFilter();
        for (ReportFilterParam p : masterDatasetFilterParams) {
            Assert.isTrue(p.getFilterValueType().equals(FilterValueType.INPUT_DATA)
                    || p.getFilterValueType().equals(FilterValueType.PRINT_INPUT_PARAM));
            String paramValue = p.getParamValue();
            if (p.getFilterValueType().equals(FilterValueType.PRINT_INPUT_PARAM)) {
                ReportPrintParam.FilterInfo printInputFilterInfo =
                        MapUtil.get(printFilterInfoMap, p.getParamValue(), ReportPrintParam.FilterInfo.class);
                if (printInputFilterInfo == null) {
                    continue;
                }
                paramValue = printInputFilterInfo.getParamValue();
            }
            DatasetFilter.FilterInfo masterFilterInfo = new DatasetFilter.FilterInfo();
            masterFilterInfo.setParamName(p.getParamName());
            masterFilterInfo.setParamValue(paramValue);
            masterDatasetFilter.add(masterFilterInfo);
        }
        return masterDatasetFilter;
    }

    private TypedCallResult<DatasetFilter.FilterInfo> makeAndVerifyFilterParam(
            ReportPrint reportPrint,
            ReportPrintFragment fragment,
            ReportFilterParam filterParam,
            Map<String, ReportPrintParam.FilterInfo> variableMap) {
        String errorMessage;
        ReportDataset dataset = fragment.getReportDataset();
        // 如果当前过滤参数的relationId是NULL，说明该参数为主数据集的过滤字段。
        // 否则当前的fragment的数据，就是基于一对多从数据集的数据进行过滤和渲染。
        if (filterParam.getRelationId() != null) {
            // 在此场景下，一定只有一个一对多的从关联。
            ReportDatasetRelation oneToManyRelation = fragment.getRelationMap().values().iterator().next();
            dataset = reportPrint.getDatasetMap().get(oneToManyRelation.getSlaveDatasetId());
        }
        String paramStringValue = filterParam.getParamValue();
        // 如果过滤参数值是打印模板的变量，就需要从调用参数中获取具体的变量值。
        if (filterParam.getFilterValueType().equals(FilterValueType.PRINT_INPUT_PARAM)) {
            if (StrUtil.isBlank(filterParam.getParamValue())) {
                errorMessage = "数据验证失败，模板片段 [{}] 参数 [{}] 引用的打印模板参数变量没有被设置！";
                return TypedCallResult.error(StrFormatter.format(
                        errorMessage, fragment.getShowName(), filterParam.getParamName()));
            }
            ReportPrintParam.FilterInfo variable = variableMap.get(filterParam.getParamValue());
            if (variable == null) {
                if (BooleanUtil.isTrue(filterParam.getRequired())) {
                    errorMessage = "数据验证失败，模板片段 [{}] 必填参数 [{}] 引用的打印模板参数变量 [{}] 不存在！";
                    return TypedCallResult.error(StrFormatter.format(errorMessage,
                            fragment.getShowName(), filterParam.getParamName(), filterParam.getParamValue()));
                }
                // 如果依赖的是打印模板变量，而当前的外部调用中，没有传入该变量的值，对于非必填过滤，就可以直接跳过了。
                return TypedCallResult.ok(null);
            }
            // 将外部传入的实际变量值，赋值给过滤参数值字段。
            paramStringValue = variable.getParamValue();
            if (paramStringValue == null) {
                paramStringValue = JSON.toJSONString(variable.getParamValueList());
            }
        }
        return this.verifyAndMakeFilterInfo(fragment, filterParam, dataset, paramStringValue);
    }

    private TypedCallResult<DatasetFilter.FilterInfo> verifyAndMakeFilterInfo(
            ReportPrintFragment fragment,
            ReportFilterParam filterParam,
            ReportDataset dataset,
            String paramStringValue) {
        String errorMessage;
        ReportDatasetColumn column = dataset.getColumnMap().get(filterParam.getParamName());
        if (column == null) {
            errorMessage = "数据验证失败，模板片段 [{}] 参数 [{}] 不是数据集 [{}] 的任何字段！";
            return TypedCallResult.error(StrFormatter.format(errorMessage,
                    fragment.getShowName(), filterParam.getParamName(), dataset.getDatasetName()));
        }
        DatasetFilter.FilterInfo filterInfo = new DatasetFilter.FilterInfo();
        filterInfo.setDatasetId(dataset.getDatasetId());
        filterInfo.setParamName(filterParam.getParamName());
        filterInfo.setFilterType(filterParam.getFilterType());
        // 这里需要根据字段的类型，动态转换过滤参数值，这样可以在后面的查询中，直接使用占位符变量了。
        // 使用占位符变量，不仅效率略高，最重要的是可以避免SQL注入。
        if (FieldFilterType.supportMultiValueFilterType(filterInfo.getFilterType())) {
            List<String> paramStringValueList = JSON.parseArray(paramStringValue, String.class);
            List<Serializable> paramValueList =
                    reportDatasetColumnService.convertToColumnValueList(column, paramStringValueList);
            for (int i = 0; i < paramValueList.size() - 1; i++) {
                Serializable convertedValue = paramValueList.get(i);
                if (convertedValue == null && paramStringValueList.get(i) != null) {
                    errorMessage = "数据验证失败，模板片段 [{}] 必填参数 [{}] 的参数元素 [{}] 类型转换失败！";
                    return TypedCallResult.error(StrFormatter.format(errorMessage,
                            fragment.getShowName(), filterParam.getParamName(), filterParam.getParamValue()));
                }
            }
            filterInfo.setParamValueList(paramValueList);
        } else {
            Object paramValue = reportDatasetColumnService.convertToColumnValue(column, paramStringValue);
            if (paramValue == null && paramStringValue != null) {
                errorMessage = "数据验证失败，模板片段 [{}] 必填参数 [{}] 的参数 [{}] 类型转换失败！";
                return TypedCallResult.error(StrFormatter.format(errorMessage,
                        fragment.getShowName(), filterParam.getParamName(), filterParam.getParamValue()));
            }
            filterInfo.setParamValue(paramValue);
        }
        filterInfo.setDateValueFilter(StrUtil.equals(ObjectFieldType.DATE, column.getFieldType()));
        if (BooleanUtil.isTrue(filterInfo.getDateValueFilter())
                && FieldFilterType.unsupportDateFilterType(filterInfo.getFilterType())) {
            errorMessage = "数据验证失败，模板片段 [{}] 日期参数 [{}] 不支持当前过滤类型 [{}]！";
            return TypedCallResult.error(StrFormatter.format(errorMessage, fragment.getShowName(),
                    filterParam.getParamName(), FieldFilterType.getName(filterInfo.getFilterType())));
        }
        return TypedCallResult.ok(filterInfo);
    }

    private String nonexistSlaveDatasetIdError(ReportPrint reportPrint, Set<Long> relationSlaveDatasetIds) {
        String errorMessage = null;
        for (Long relationSlaveDatasetId : relationSlaveDatasetIds) {
            if (reportPrint.getDatasetMap().containsKey(relationSlaveDatasetId)) {
                continue;
            }
            // 这里会根据relationSlaveDatasetId反向定位导致运行时错误的Fragment
            for (ReportPrintFragment fragment : reportPrint.getFragmentList()) {
                ReportDataset dataset = reportPrint.getDatasetMap().get(fragment.getDatasetId());
                if (CollUtil.isEmpty(dataset.getRelationList())) {
                    ReportDatasetRelation relation = dataset.getRelationList().stream()
                            .filter(r -> r.getSlaveDatasetId().equals(relationSlaveDatasetId))
                            .findFirst().orElse(null);
                    if (relation != null) {
                        errorMessage = "数据验证失败，模板片段 [{}] 所使用的数据集 [{}] 关联 [{}] 的从数据集Id [{}] 并不存在!";
                        return StrFormatter.format(errorMessage,
                                fragment.getShowName(),
                                dataset.getDatasetName(),
                                relation.getVariableName(),
                                relationSlaveDatasetId);
                    }
                }
            }
        }
        return errorMessage;
    }

    private String doSpreadSheetParse(ReportPrint print, String sheetJson) {
        SpreadSheetData sheetData =
                JSON.parseArray(sheetJson).getJSONObject(0).toJavaObject(SpreadSheetData.class);
        sheetData.setRowCount(sheetData.getRows().getInteger("len"));
        sheetData.setColCount(sheetData.getCols().getInteger("len"));
        this.processDataRows(sheetData);
        this.processDataCols(sheetData);
        // 解析luckysheet中的单元格数据信息，并转换为内部统一的格式。
        List<ReportSheetRow> reportRowList = this.parseCellData(sheetData);
        this.processMergeCells(reportRowList);
        // 迭代luckysheet中的边框信息，并以重放的方式，填充给每个单元格。
        this.fillReportBorderInfo(reportRowList, sheetData);
        // 计算出哪些是空行。
        this.parseEmptyRow(reportRowList);
        ReportSheet reportSheet = new ReportSheet();
        reportSheet.setVisibleDataRow(sheetData.getRowHeights());
        reportSheet.setVisibleDataColumn(sheetData.getColWidths());
        // 规格化电子表格的行和列数据，裁剪掉没用的行列，以提升运行时打印渲染的性能。
        this.normalizeReportSheet(reportSheet, reportRowList);
        // 填充电子表格中的fragment相关信息。
        this.fillReportSheetFragmentInfo(reportSheet, print);
        return JSON.toJSONString(reportSheet);
    }

    private void processMergeCells(List<ReportSheetRow> reportRowList) {
        List<ReportSheetCell> mergeCells = new LinkedList<>();
        reportRowList.forEach(r ->
                r.getCells().stream().filter(c -> BooleanUtil.isTrue(c.isMergeCellTopLeft())).forEach(mergeCells::add));
        if (CollUtil.isEmpty(mergeCells)) {
            return;
        }
        for (ReportSheetCell c : mergeCells) {
            List<ReportSheetRow> mergedRows =
                    reportRowList.subList(c.getRowIndex(), c.getRowIndex() + c.getMergeCellRowSpan());
            int endCol = c.getColIndex() + c.getMergeCellColSpan();
            mergedRows.forEach(mr -> mr.getCells().subList(c.getColIndex(), endCol).forEach(mc -> {
                mc.setMergeCell(true);
                mc.setMergeCellTopRow(c.getRowIndex());
                mc.setMergeCellLeftCol(c.getColIndex());
            }));
        }
    }

    private void fillCellWithCellValueData(
            SpreadSheetData sheetData, SpreadSheetData.Cell cellData, ReportSheetCell cell) {
        if (cellData.getStyle() != null) {
            SpreadSheetData.Style style = sheetData.getStyles()[cellData.getStyle()];
            cell.setStyleIndex(cellData.getStyle());
            cell.setUnderline(BooleanUtil.isTrue(style.getUnderline()));
            cell.setCancelLine(BooleanUtil.isTrue(style.getStrike()));
            cell.setHorizontalType(style.getAlign());
            cell.setVerticalType(style.getValign());
            cell.setFontColor(style.getColor());
            cell.setBackgroundColor(style.getBgcolor());
            if (StrUtil.equals("number", style.getFormat())) {
                cell.setCustomFormatType("n");
                cell.setCustomFormat("General");
            } else {
                cell.setCustomFormat(style.getFormat());
            }
            SpreadSheetData.Font font = style.getFont();
            if (font != null) {
                cell.setBold(BooleanUtil.isTrue(font.getBold()));
                cell.setItalic(BooleanUtil.isTrue(font.getItalic()));
                cell.setFontSize(font.getSize());
            }
        }
        if (StrUtil.isNotBlank(cellData.getV())) {
            cell.setData(cellData.getV());
        } else {
            cell.setData(cellData.getText());
        }
        cell.setMergeCell(cellData.getMerge() != null);
        cell.setMergeCellTopLeft(cellData.getMerge() != null);
        if (cell.isMergeCellTopLeft()) {
            cell.setMergeCellRowSpan(cellData.getMerge()[0] + 1);
            cell.setMergeCellColSpan(cellData.getMerge()[1] + 1);
        }
    }

    private List<ReportSheetRow> parseCellData(SpreadSheetData sheetData) {
        List<ReportSheetRow> reportRowList = new ArrayList<>(sheetData.getRowCount());
        for (int i = 0; i < sheetData.getRowCount(); i++) {
            ReportSheetRow reportRow = new ReportSheetRow();
            reportRow.setRowIndex(i);
            reportRow.setRowHeight(sheetData.getRowHeights()[i]);
            reportRow.setCells(new ArrayList<>(sheetData.getColCount()));
            JSONObject rowData = sheetData.getRowMap().get(i);
            JSONObject cells = null;
            if (rowData != null) {
                cells = rowData.getJSONObject("cells");
            }
            for (Integer j = 0; j < sheetData.getColCount(); j++) {
                ReportSheetCell reportCell = new ReportSheetCell();
                reportCell.setRowIndex(i);
                reportCell.setColIndex(j);
                reportCell.setColWidth(sheetData.getColWidths()[j]);
                reportRow.getCells().add(reportCell);
                if (cells != null) {
                    JSONObject cell = cells.getJSONObject(j.toString());
                    reportCell.setEmptyCell(cell == null);
                    if (cell != null) {
                        this.fillCellWithCellValueData(
                                sheetData, JSON.toJavaObject(cell, SpreadSheetData.Cell.class), reportCell);
                    }
                }
            }
            reportRowList.add(reportRow);
        }
        return reportRowList;
    }

    private void processDataRows(SpreadSheetData sheetData) {
        Map<Integer, JSONObject> rowMap = new TreeMap<>();
        for (Map.Entry<String, Object> row : sheetData.getRows().entrySet()) {
            if (NumberUtil.isNumber(row.getKey())) {
                rowMap.put(Convert.toInt(row.getKey()), sheetData.getRows().getJSONObject(row.getKey()));
            }
        }
        sheetData.setRowMap(rowMap);
        Integer[] rowHeights = new Integer[sheetData.getRowCount()];
        Arrays.fill(rowHeights, 25);
        for (int i = 0; i < sheetData.getRowCount(); i++) {
            JSONObject rowData = sheetData.getRowMap().get(i);
            if (rowData != null) {
                rowHeights[i] = (Integer) rowData.getOrDefault("height", 25);
            }
        }
        sheetData.setRowHeights(rowHeights);
    }

    private void processDataCols(SpreadSheetData sheetData) {
        Map<Integer, JSONObject> colMap = new TreeMap<>();
        for (Map.Entry<String, Object> col : sheetData.getCols().entrySet()) {
            if (NumberUtil.isNumber(col.getKey())) {
                colMap.put(Convert.toInt(col.getKey()), sheetData.getCols().getJSONObject(col.getKey()));
            }
        }
        sheetData.setColMap(colMap);
        Integer[] colWidths = new Integer[sheetData.getColCount()];
        Arrays.fill(colWidths, 100);
        for (int i = 0; i < sheetData.getColCount(); i++) {
            JSONObject colData = sheetData.getColMap().get(i);
            if (colData != null) {
                colWidths[i] = (Integer) colData.getOrDefault("width", 100);
            }
        }
        sheetData.setColWidths(colWidths);
    }

    private void fillReportBorderInfo(List<ReportSheetRow> rows, SpreadSheetData sheetData) {
        for (ReportSheetRow row : rows) {
            for (ReportSheetCell cell : row.getCells()) {
                if (cell.getStyleIndex() != null) {
                    SpreadSheetData.Style style = sheetData.getStyles()[cell.getStyleIndex()];
                    if (style.getBorder() != null) {
                        this.fillReportCellBorderInfo(cell, style.getBorder());
                    }
                }
            }
        }
    }

    private void fillReportCellBorderInfo(ReportSheetCell cell, SpreadSheetData.Border border) {
        if (border.getTop() != null) {
            cell.setHasTopBorder(true);
            cell.setTopBorderColor(border.getTop()[1]);
        }
        if (border.getBottom() != null) {
            cell.setHasBottomBorder(true);
            cell.setBottomBorderColor(border.getBottom()[1]);
        }
        if (border.getLeft() != null) {
            cell.setHasLeftBorder(true);
            cell.setLeftBorderColor(border.getLeft()[1]);
        }
        if (border.getRight() != null) {
            cell.setHasRightBorder(true);
            cell.setRightBorderColor(border.getRight()[1]);
        }
    }

    private void parseEmptyRow(List<ReportSheetRow> rows) {
        for (ReportSheetRow row : rows) {
            for (ReportSheetCell cell : row.getCells()) {
                if (StrUtil.isNotBlank(cell.getData()) || cell.hasBorder() || cell.isMergeCell()) {
                    row.setEmptyRow(false);
                    break;
                }
            }
        }
    }

    private void normalizeReportSheet(ReportSheet reportSheet, List<ReportSheetRow> rows) {
        int maxCol = 0;
        int maxRow = 0;
        // 计算每行实际可用的cell数量，以及实际有用的行数量。
        for (ReportSheetRow row : rows) {
            if (row.isEmptyRow()) {
                continue;
            }
            maxRow = row.getRowIndex();
            // 计算出每一行的最大可用cell的列坐标。
            int rowMaxCol = 0;
            for (ReportSheetCell cell : row.getCells()) {
                if (StrUtil.isNotBlank(cell.getData()) || cell.hasBorder() || cell.isMergeCell()) {
                    rowMaxCol = cell.getColIndex();
                }
            }
            maxCol = Math.max(maxCol, rowMaxCol);
        }
        // 裁剪掉没用的行列区域的数据。
        List<ReportSheetRow> resultSheetRows = new ArrayList<>(rows.subList(0, maxRow + 1));
        for (ReportSheetRow r : resultSheetRows) {
            if (CollUtil.isNotEmpty(r.getCells())) {
                List<ReportSheetCell> c = new ArrayList<>(r.getCells().subList(0, maxCol + 1));
                r.setCells(c);
            }
        }
        reportSheet.setMaxCol(maxCol);
        reportSheet.setMaxRow(maxRow);
        reportSheet.setRows(resultSheetRows);
    }

    private void fillReportSheetFragmentInfo(ReportSheet reportSheet, ReportPrint print) {
        boolean hasLoopFragment = false;
        List<ReportPrintFragment> fragmentList =
                JSON.parseArray(print.getFragmentJson(), ReportPrintFragment.class);
        if (CollUtil.isEmpty(fragmentList)) {
            reportSheet.setHasLoopFragment(hasLoopFragment);
            return;
        }
        for (ReportPrintFragment fragment : fragmentList) {
            if (BooleanUtil.isTrue(fragment.getLoop())) {
                hasLoopFragment = true;
            }
            fragment.setEndRow(fragment.getStartRow() + fragment.getRowSpan());
            int i = 0;
            for (ReportSheetRow r : reportSheet.getRows()) {
                if (++i >= fragment.getStartRow()) {
                    if (i == fragment.getEndRow()) {
                        break;
                    }
                    r.setFragmentId(fragment.getFragmentId());
                }
            }
        }
        reportSheet.setHasLoopFragment(hasLoopFragment);
    }

    private void doRenderToJson(ReportPrint print, JSONObject jsonData) throws IOException {
        ReportSheet sheet = JSON.parseObject(print.getTemplateDataJson(), ReportSheet.class);
        this.bindRowDataFromFragment(print, sheet);
        for (ReportSheetRow reportRow : sheet.getRows()) {
            reportRow.setRowIndex(reportRow.getRowIndex());
            for (ReportSheetCell reportCell : reportRow.getCells()) {
                reportCell.setRowIndex(reportRow.getRowIndex());
                if (reportCell.isMergeCellTopLeft()) {
                    reportCell.setMergeCellTopRow(reportCell.getMergeCellTopRow());
                }
                ReportSheetCellValue cellValue = this.normalizeCellData(print, reportRow, reportCell);
                if (cellValue.getValue() != null) {
                    reportCell.setData(cellValue.getValue().toString());
                    this.makeImageCellData(cellValue, sheet, reportCell);
                }
            }
        }
        jsonData.put("reportSheet", sheet);
    }

    private void makeImageCellData(ReportSheetCellValue value, ReportSheet sheet, ReportSheetCell cell) throws IOException {
        if (value == null || value.getCellType() == null) {
            return;
        }
        if (!value.getCellType().equals(ReportSheetCellValue.IMAGE_CELL)
                && !value.getCellType().equals(ReportSheetCellValue.BARCODE_CELL)
                && !value.getCellType().equals(ReportSheetCellValue.QRCODE_CELL)) {
            return;
        }
        cell.setImageCell(true);
        byte[] imageBytes;
        if (value.getCellType().equals(ReportSheetCellValue.QRCODE_CELL)
                || value.getCellType().equals(ReportSheetCellValue.BARCODE_CELL)) {
            imageBytes = this.createBarCodeBytes(sheet, cell, value);
        } else {
            List<UploadResponseInfo> infos = JSON.parseArray(value.getValue().toString(), UploadResponseInfo.class);
            if (CollUtil.isEmpty(infos)) {
                return;
            }
            UploadResponseInfo info = infos.get(0);
            imageBytes = this.getImageDataBytes(info);
        }
        String filename = MyCommonUtil.generateUuid();
        RBinaryStream stream = redissonClient.getBinaryStream(TokenData.takeFromRequest().getSessionId() + filename);
        stream.set(imageBytes, 2, TimeUnit.HOURS);
        cell.setData(DOWNLOAD_IMAGE_URL + filename);
    }

    private int doRenderToExcel(ReportPrint print, ExcelWriter writer, int rowOffset) throws IOException {
        ReportSheet sheet = JSON.parseObject(print.getTemplateDataJson(), ReportSheet.class);
        this.bindRowDataFromFragment(print, sheet);
        if (rowOffset > 0) {
            Integer[] rowHeightsArray = new Integer[rowOffset + sheet.getVisibleDataRow().length];
            ArrayUtil.copy(sheet.getVisibleDataRow(), 0, rowHeightsArray, rowOffset, sheet.getVisibleDataRow().length);
            sheet.setVisibleDataRow(rowHeightsArray);
        }
        HSSFWorkbook workbook = (HSSFWorkbook) writer.getWorkbook();
        for (int i = rowOffset; i <= sheet.getMaxRow() + rowOffset; i++) {
            Row r = writer.getSheet().createRow(i);
            r.setHeightInPoints(Float.parseFloat(sheet.getVisibleDataRow()[i] + ""));
        }
        for (int j = 0; j <= sheet.getMaxCol(); j++) {
            writer.getSheet().setColumnWidth(j, sheet.getVisibleDataColumn()[j] * 42);
        }
        for (ReportSheetRow reportRow : sheet.getRows()) {
            reportRow.setRowIndex(reportRow.getRowIndex() + rowOffset);
            for (ReportSheetCell reportCell : reportRow.getCells()) {
                reportCell.setRowIndex(reportRow.getRowIndex());
                if (reportCell.isMergeCellTopLeft()) {
                    reportCell.setMergeCellTopRow(reportCell.getMergeCellTopRow() + rowOffset);
                }
                ReportSheetCellValue cellValue = this.normalizeCellData(print, reportRow, reportCell);
                CellStyle cellStyle = writer.createCellStyle();
                this.fillCellDataFormat(reportCell, cellValue, cellStyle);
                this.fillCellStyleInfo(workbook, reportCell, cellStyle);
                this.fillCellBorderInfo(workbook, reportCell, cellStyle);
                this.writeCellToExcel(sheet, reportCell, cellValue, writer, cellStyle);
            }
        }
        return sheet.getMaxRow();
    }

    private void fillCellDataFormat(ReportSheetCell reportCell, ReportSheetCellValue cellValue, CellStyle cellStyle) {
        if (cellValue.getValue() instanceof Double || cellValue.getValue() instanceof BigDecimal) {
            cellStyle.setDataFormat((short) 2);
        } else if (cellValue.getValue() instanceof Long || cellValue.getValue() instanceof Integer) {
            cellStyle.setDataFormat((short) 1);
        } else if (cellValue.getValue() instanceof String) {
            cellStyle.setWrapText(true);
        }
        if (StrUtil.equals(reportCell.getCustomFormatType(), "n")
                && StrUtil.isNotBlank(reportCell.getCustomFormat())) {
            cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat(reportCell.getCustomFormat()));
        }
    }

    private void writeCellToExcel(
            ReportSheet sheet,
            ReportSheetCell reportCell,
            ReportSheetCellValue cellValue,
            ExcelWriter writer,
            CellStyle cellStyle) throws IOException {
        if (!reportCell.isMergeCell()) {
            if (ObjectUtil.equals(cellValue.getCellType(), ReportSheetCellValue.IMAGE_CELL)) {
                this.writeImageCellToExcel(writer, sheet, reportCell, cellValue);
            } else if (MyCommonUtil.equalsAny(cellValue.getCellType(),
                    ReportSheetCellValue.BARCODE_CELL, ReportSheetCellValue.QRCODE_CELL)) {
                byte[] imageBytes = this.createBarCodeBytes(sheet, reportCell, cellValue);
                this.writeImageCellToExcel(writer, sheet, reportCell, imageBytes, Workbook.PICTURE_TYPE_PNG, true);
            } else {
                writer.writeCellValue(reportCell.getColIndex(), reportCell.getRowIndex(), cellValue.getValue());
            }
            writer.setStyle(cellStyle, reportCell.getColIndex(), reportCell.getRowIndex());
            return;
        }
        if (reportCell.isMergeCellTopLeft()) {
            if (ObjectUtil.equals(cellValue.getCellType(), ReportSheetCellValue.IMAGE_CELL)) {
                this.writeImageCellToExcel(writer, sheet, reportCell, cellValue);
                CellUtil.mergingCells(writer.getSheet(),
                        reportCell.getMergeCellTopRow(),
                        reportCell.getMergeCellTopRow() + reportCell.getMergeCellRowSpan() - 1,
                        reportCell.getMergeCellLeftCol(),
                        reportCell.getMergeCellLeftCol() + reportCell.getMergeCellColSpan() - 1,
                        cellStyle);
            } else if (MyCommonUtil.equalsAny(cellValue.getCellType(),
                    ReportSheetCellValue.BARCODE_CELL, ReportSheetCellValue.QRCODE_CELL)) {
                byte[] imageBytes = this.createBarCodeBytes(sheet, reportCell, cellValue);
                this.writeImageCellToExcel(writer, sheet, reportCell, imageBytes, Workbook.PICTURE_TYPE_PNG, true);
                CellUtil.mergingCells(writer.getSheet(),
                        reportCell.getMergeCellTopRow(),
                        reportCell.getMergeCellTopRow() + reportCell.getMergeCellRowSpan() - 1,
                        reportCell.getMergeCellLeftCol(),
                        reportCell.getMergeCellLeftCol() + reportCell.getMergeCellColSpan() - 1,
                        cellStyle);
            } else {
                writer.merge(reportCell.getMergeCellTopRow(),
                        reportCell.getMergeCellTopRow() + reportCell.getMergeCellRowSpan() - 1,
                        reportCell.getMergeCellLeftCol(),
                        reportCell.getMergeCellLeftCol() + reportCell.getMergeCellColSpan() - 1,
                        cellValue.getValue(), cellStyle);
            }
        }
    }

    private void writeImageCellToExcel(
            ExcelWriter writer, ReportSheet sheet, ReportSheetCell reportCell, byte[] imageBytes, int imageType, Boolean scale) {
        int startColIndex = reportCell.getColIndex();
        int startRowIndex = reportCell.getRowIndex();
        int endColIndex = startColIndex;
        int endRowIndex = startRowIndex;
        int height = sheet.getVisibleDataRow()[reportCell.getRowIndex()];
        if (reportCell.isMergeCellTopLeft()) {
            endColIndex = reportCell.getMergeCellLeftCol() + reportCell.getMergeCellColSpan() - 1;
            endRowIndex = reportCell.getMergeCellTopRow() + reportCell.getMergeCellRowSpan() - 1;
            height = 0;
            for (int i = startRowIndex; i <= endRowIndex; i++) {
                height += sheet.getVisibleDataRow()[i];
            }
        }
        if (BooleanUtil.isFalse(scale)) {
            BufferedImage inputImage = ImgUtil.read(new ByteArrayInputStream(imageBytes));
            int imageWidth = inputImage.getWidth();
            int imageHeight = inputImage.getHeight();
            BufferedImage outputImage = new BufferedImage(reportCell.getColWidth(), height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();
            int x = (reportCell.getColWidth() - imageWidth) / 2;
            int y = (height - imageHeight) / 2;
            g2d.drawImage(inputImage, x, y, imageWidth, imageHeight, null);
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream(imageBytes.length * 2)) {
                ImgUtil.write(outputImage, "png", bout);
                imageBytes = bout.toByteArray();
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new MyRuntimeException(e);
            }
            g2d.dispose();
        }
        org.springframework.util.Assert.isTrue(height != 0, "Merge cell height can'be 0.");
        int dx1 = (int) ((2 * (EXCEL_COLUMN_WIDTH_FACTOR / reportCell.getColWidth())) + 1);
        int dy1 = (int) ((2 * (EXCEL_COLUMN_HEIGHT_FACTOR / height)) + 1);
        writer.writeImg(imageBytes, imageType, dx1, dy1,
                1023, 255, startColIndex, startRowIndex, endColIndex, endRowIndex);
    }

    private byte[] getImageDataBytes(UploadResponseInfo info) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(reportProperties.getImageDownloadUrl())
                .append("?uriPath=").append(info.getUploadPath())
                .append("&filename=").append(info.getFilename());
        byte[] bytes = HttpUtil.downloadBytes(sb.toString());
        if (ObjectUtil.isEmpty(bytes)) {
            log.warn("When export to excel, no image data from url [{}]", sb.toString());
        }
        return bytes;
    }

    private void writeImageCellToExcel(
            ExcelWriter writer, ReportSheet sheet, ReportSheetCell reportCell, ReportSheetCellValue cellValue) {
        List<UploadResponseInfo> infos = JSON.parseArray(cellValue.getValue().toString(), UploadResponseInfo.class);
        if (CollUtil.isEmpty(infos)) {
            return;
        }
        byte[] bytes = this.getImageDataBytes(infos.get(0));
        if (ObjectUtil.isEmpty(bytes)) {
            return;
        }
        int imageType = Workbook.PICTURE_TYPE_PNG;
        // 目前先仅仅支持这两种图片格式，更多可以自己手动修改代码扩充。
        if (StrUtil.endWithIgnoreCase(infos.get(0).getFilename(), "jpg")) {
            imageType = Workbook.PICTURE_TYPE_JPEG;
        }
        this.writeImageCellToExcel(writer, sheet, reportCell, bytes, imageType, cellValue.getImageScale());
    }

    private float autoCalculcateMarginRight(ReportSheet sheet, float pageWidth) {
        int totalWidth = 0;
        for (int i = 0; i <= sheet.getMaxCol(); i++) {
            totalWidth += sheet.getVisibleDataColumn()[i];
        }
        totalWidth *= PDF_SCALE_DOWN;
        return totalWidth > pageWidth ? 0 : (pageWidth - totalWidth) / 2;
    }

    private void doRenderToPdf(ReportPrint print, XEasyPdfDocument doc) {
        ReportPrintInfo printInfo = JSON.parseObject(print.getPrintJson(), ReportPrintInfo.class);
        ReportSheet sheet = JSON.parseObject(print.getTemplateDataJson(), ReportSheet.class);
        this.bindRowDataFromFragment(print, sheet);
        XEasyPdfPage page = this.createPage(printInfo);
        if (page == null) {
            throw new MyRuntimeException("Invalid PrintInfo data for creating page!");
        }
        doc.addPage(page);
        XEasyPdfTable pdfTable = XEasyPdfHandler.Table.build();
        pdfTable.setMarginLeft(this.autoCalculcateMarginRight(sheet, (int) page.getOriginalWidth()))
                .setMarginTop(printInfo.getTopMargin())
                .setMarginBottom(printInfo.getBottomMargin())
                .disableAutoSplitRow();
        page.addComponent(pdfTable);
        for (ReportSheetRow reportRow : sheet.getRows()) {
            XEasyPdfRow row = XEasyPdfHandler.Table.Row.build();
            row.setHeight(reportRow.getRowHeight() * PDF_SCALE_DOWN);
            pdfTable.addRow(row);
            for (ReportSheetCell reportCell : reportRow.getCells()) {
                float cellWidth = reportCell.getColWidth() * PDF_SCALE_DOWN;
                float cellHeight = reportRow.getRowHeight() * PDF_SCALE_DOWN;
                if (!reportCell.isMergeCell()) {
                    ReportSheetCellValue cellValue = this.normalizeCellData(print, reportRow, reportCell);
                    row.addCell(this.makePdfCell(reportCell, cellValue, cellWidth, cellHeight));
                    continue;
                }
                if (reportCell.isMergeCellTopLeft()) {
                    cellWidth = this.calculateMergeCellWidth(sheet, reportCell);
                    cellHeight = this.calculateMergeCellHeight(sheet, reportCell);
                    ReportSheetCellValue cellValue = this.normalizeCellData(print, reportRow, reportCell);
                    row.addCell(this.makePdfCell(reportCell, cellValue, cellWidth, cellHeight));
                } else {
                    // 当前单元格为被合并单元格，如果和左上角的行坐标相同，就可直接忽略。
                    // 如果行坐标不相同，可以调用enableVerticalMerge方法顺延垂直合并。
                    if (reportCell.getRowIndex() != reportCell.getMergeCellTopRow()) {
                        row.addCell(XEasyPdfHandler.Table.Row.Cell.build(cellWidth, cellHeight).enableVerticalMerge());
                    }
                }
            }
        }
    }

    private XEasyPdfPage createPage(ReportPrintInfo printInfo) {
        if (printInfo.isCustomSize()) {
            return new XEasyPdfPage(XEasyPdfPageRectangle.create(
                    printInfo.getCustomWidth(), printInfo.getCustomHeight()));
        }
        XEasyPdfPage page = null;
        if (printInfo.isLandscape()) {
            if (printInfo.getPaperType().equals(ReportPrintInfo.A4)) {
                page = new XEasyPdfPage(XEasyPdfPageRectangle.create(
                        XEasyPdfPageRectangle.A4.getHeight(), XEasyPdfPageRectangle.A4.getWidth()));
            } else if (printInfo.getPaperType().equals(ReportPrintInfo.A5)) {
                page = new XEasyPdfPage(XEasyPdfPageRectangle.create(
                        XEasyPdfPageRectangle.A5.getHeight(), XEasyPdfPageRectangle.A5.getWidth()));
            }
        } else {
            if (printInfo.getPaperType().equals(ReportPrintInfo.A4)) {
                page = XEasyPdfHandler.Page.build();
            } else if (printInfo.getPaperType().equals(ReportPrintInfo.A5)) {
                page = new XEasyPdfPage(XEasyPdfPageRectangle.A5);
            }
        }
        return page;
    }

    private void bindRowDataFromFragment(ReportPrint print, ReportSheet sheet) {
        Map<Long, ReportPrintFragment> fragmentMap = null;
        if (CollUtil.isNotEmpty(print.getFragmentList())) {
            fragmentMap = print.getFragmentList().stream()
                    .collect(Collectors.toMap(ReportPrintFragment::getFragmentId, c -> c));
        }
        List<ReportSheetRow> resultRows = new LinkedList<>();
        Long currentLoopFragment = null;
        for (ReportSheetRow row : sheet.getRows()) {
            boolean ok = this.checkAndBindNonloopFragmentRowData(row, fragmentMap);
            if (ok) {
                resultRows.add(row);
                continue;
            }
            ReportPrintFragment fragment = fragmentMap.get(row.getFragmentId());
            if (CollUtil.isEmpty(fragment.getData())) {
                resultRows.add(row);
            } else if (fragment.getData().size() == 1) {
                row.setDataObject(fragment.getData().get(0));
                resultRows.add(row);
            } else {
                if (ObjectUtil.notEqual(currentLoopFragment, row.getFragmentId())) {
                    currentLoopFragment = row.getFragmentId();
                    resultRows.addAll(this.bindLoopFragmentRowData(fragment, sheet, row));
                }
            }
        }
        int i = 0;
        for (ReportSheetRow row : resultRows) {
            row.setRowIndex(i++);
            for (ReportSheetCell cell : row.getCells()) {
                cell.setRowIndex(row.getRowIndex());
            }
        }
        this.recalculateMergeCellRowIndex(resultRows);
        sheet.setRows(resultRows);
        sheet.setMaxRow(resultRows.size() - 1);
        List<Integer> resultRowHeights =
                resultRows.stream().map(ReportSheetRow::getRowHeight).collect(Collectors.toList());
        sheet.setVisibleDataRow(resultRowHeights.toArray(new Integer[]{}));
    }

    private void recalculateMergeCellRowIndex(List<ReportSheetRow> resultRows) {
        for (int i = 0; i < resultRows.size(); i++) {
            ReportSheetRow row = resultRows.get(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                ReportSheetCell cell = row.getCells().get(j);
                if (cell.isMergeCell() && cell.isMergeCellTopLeft()) {
                    cell.setMergeCellTopRow(row.getRowIndex());
                    List<ReportSheetRow> subMergedRows = resultRows.subList(i, i + cell.getMergeCellRowSpan());
                    for (ReportSheetRow subRow : subMergedRows) {
                        List<ReportSheetCell> subMergedCells = subRow.getCells().subList(j, j + cell.getMergeCellColSpan());
                        subMergedCells.forEach(c -> c.setMergeCellTopRow(row.getRowIndex()));
                    }
                }
            }
        }
    }

    private boolean checkAndBindNonloopFragmentRowData(
            ReportSheetRow row, Map<Long, ReportPrintFragment> fragmentMap) {
        if (row.getFragmentId() == null) {
            return true;
        }
        ReportPrintFragment fragment = fragmentMap.get(row.getFragmentId());
        if (BooleanUtil.isTrue(fragment.getLoop())) {
            return false;
        }
        if (CollUtil.isNotEmpty(fragment.getData())) {
            row.setDataObject(fragment.getData().get(0));
        }
        return true;
    }

    private List<ReportSheetRow> bindLoopFragmentRowData(
            ReportPrintFragment fragment, ReportSheet sheet, ReportSheetRow row) {
        List<ReportSheetRow> resultRows = new LinkedList<>();
        List<ReportSheetRow> fragmentRows = sheet.getRows().stream()
                .filter(r -> ObjectUtil.equals(r.getFragmentId(), row.getFragmentId()))
                .collect(Collectors.toList());
        int startRowIndex = row.getRowIndex();
        int fragmentDataIndex = 0;
        for (Map<String, Object> dataObject : fragment.getData()) {
            int fragmentRowOffset = 0;
            for (ReportSheetRow fragmentRow : fragmentRows) {
                int rowOffset = fragmentRows.size() * fragmentDataIndex + fragmentRowOffset++;
                ReportSheetRow clonedRow = new ReportSheetRow(fragmentRow, startRowIndex, rowOffset);
                clonedRow.setDataObject(dataObject);
                resultRows.add(clonedRow);
            }
            fragmentDataIndex++;
        }
        return resultRows;
    }

    @SuppressWarnings("unchecked")
    private ReportSheetCellValue normalizeCellData(ReportPrint print, ReportSheetRow row, ReportSheetCell cell) {
        Object cellData = cell.getData() == null ? "" : cell.getData();
        if (!StrUtil.startWith(cellData.toString(), "{\"")) {
            ReportSheetCellValue cellValue = new ReportSheetCellValue();
            cellValue.setValue(cellData);
            return cellValue;
        }
        ReportSheetCellValue cellValue = JSON.parseObject(cellData.toString(), ReportSheetCellValue.class);
        if (!this.fromDatasetField(cellValue)) {
            cellValue.setValue(cellData);
            return cellValue;
        }
        if (row.getDataObject() != null) {
            if (cellValue.getRelationId() != null) {
                ReportDataset dataset = print.getDatasetMap().get(cellValue.getDatasetId());
                String variableName = this.getRelationVariableNameById(dataset, cellValue.getRelationId());
                Map<String, Object> relationData = (Map<String, Object>) row.getDataObject().get(variableName);
                if (relationData != null) {
                    cellData = relationData.get(cellValue.getColumnName());
                } else {
                    cellData = row.getDataObject().get(cellValue.getColumnName());
                }
            } else {
                cellData = this.extractCellData(row, cellValue, cellValue.getColumnName());
            }
            Map<String, Object> dictMap =
                    (Map<String, Object>) row.getDataObject().get(cellValue.getColumnName() + "__DictMap");
            if (dictMap != null) {
                cellData = dictMap.get("name");
            }
        } else {
            cellData = "";
        }
        cellValue.setValue(cellData == null ? "" : cellData);
        this.processDataFormat(cellValue);
        return cellValue;
    }

    private void processDataFormat(ReportSheetCellValue cellValue) {
        if (ObjectUtil.equals(cellValue.getDateFormat(), DateShowFormat.CN)
                && cellValue.getValue() != null && cellValue.getValue() instanceof Date) {
            String v = DateUtil.format((Date) cellValue.getValue(), MyDateUtil.COMMON_CN_SHORT_DATETIME_FORMAT);
            cellValue.setValue(v);
        }
        if (StrUtil.equals("date", cellValue.getCustomFormat())) {
            cellValue.setValue(DateUtil.format((Date) cellValue.getValue(), MyDateUtil.COMMON_DATE_FORMAT));
        } else if (StrUtil.equals("datetime", cellValue.getCustomFormat())) {
            cellValue.setValue(DateUtil.format((Date) cellValue.getValue(), MyDateUtil.COMMON_SHORT_DATETIME_FORMAT));
        }
    }

    @SuppressWarnings("unchecked")
    private Object extractCellData(ReportSheetRow row, ReportSheetCellValue cellValue, String columnName) {
        if (cellValue.getDatasetType() == null || !cellValue.getDatasetType().equals(DatasetType.API)) {
            return row.getDataObject().get(columnName);
        }
        List<String> columnPaths = JSON.parseArray(columnName, String.class);
        Map<String, Object> dataObject = row.getDataObject();
        for (int i = 1; i < columnPaths.size(); i++) {
            Object columnData = dataObject.get(columnPaths.get(i));
            if (i == columnPaths.size() - 1) {
                return columnData;
            }
            dataObject = (Map<String, Object>) columnData;
        }
        return null;
    }

    private boolean fromDatasetField(ReportSheetCellValue sheetCellValue) {
        if (MyCommonUtil.equalsAny(sheetCellValue.getCellType(),
                ReportSheetCellValue.DATASET_FIELD,
                ReportSheetCellValue.BARCODE_CELL,
                ReportSheetCellValue.QRCODE_CELL)) {
            return true;
        }
        return ObjectUtil.equals(sheetCellValue.getImageSourceType(), ReportSheetCellValue.IMAGE_SOURCE_DATASET_FIELD);
    }

    private String getRelationVariableNameById(ReportDataset dataset, Long relationId) {
        if (CollUtil.isNotEmpty(dataset.getRelationList())) {
            for (ReportDatasetRelation r : dataset.getRelationList()) {
                if (r.getRelationId().equals(relationId)) {
                    return r.getVariableName();
                }
            }
        }
        return null;
    }

    private XEasyPdfCell makePdfCell(
            ReportSheetCell reportCell, ReportSheetCellValue cellValue, float cellWidth, float cellHeight) {
        XEasyPdfCell cell;
        if (ObjectUtil.equals(cellValue.getCellType(), ReportSheetCellValue.IMAGE_CELL)) {
            cell = this.makePdfImageCell(cellValue, cellWidth, cellHeight);
        } else if (ObjectUtil.equals(cellValue.getCellType(), ReportSheetCellValue.BARCODE_CELL)
                || ObjectUtil.equal(cellValue.getCellType(), ReportSheetCellValue.QRCODE_CELL)) {
            cell = this.makePdfBarcodeCell(cellValue, cellWidth, cellHeight);
        } else {
            if (StrUtil.equals(reportCell.getCustomFormatType(), "n")
                    && StrUtil.isNotBlank(reportCell.getCustomFormat())
                    && !StrUtil.equals(reportCell.getCustomFormat(), "General")) {
                cellValue.setValue(NumberUtil.decimalFormat(reportCell.getCustomFormat(), cellValue.getValue()));
            }
            XEasyPdfText text = this.makePdfTextCell(cellValue.getValue().toString());
            cell = XEasyPdfHandler.Table.Row.Cell.build(cellWidth, cellHeight)
                    .addContent(text).setFontSize(reportCell.getFontSize());
            if (reportCell.isUnderline()) {
                text.enableUnderline();
            }
            if (reportCell.isCancelLine()) {
                text.enableDeleteLine();
            }
            if (reportCell.getHorizontalType().equals(1)) {
                text.setMarginLeft(4F);
            } else if (reportCell.getHorizontalType().equals(2)) {
                text.setMarginRight(4F);
            }
        }
        this.fillCellStyleInfo(reportCell, cell);
        this.fillCellBorderInfo(reportCell, cell);
        return cell;
    }

    private XEasyPdfText makePdfTextCell(String cellValue) {
        XEasyPdfText text;
        if (StrUtil.containsAny(cellValue, '\r', '\n')) {
            List<String> valueList = StrUtil.splitTrim(cellValue, '\r');
            List<String> resultList = new LinkedList<>();
            for (String value : valueList) {
                resultList.addAll(StrUtil.splitTrim(value, '\n'));
            }
            text = XEasyPdfHandler.Text.build(resultList);
        } else {
            text = XEasyPdfHandler.Text.build(cellValue);
        }
        return text;
    }

    private XEasyPdfCell makePdfImageCell(ReportSheetCellValue cellValue, float cellWidth, float cellHeight) {
        XEasyPdfCell cell;
        List<UploadResponseInfo> infos = JSON.parseArray(cellValue.getValue().toString(), UploadResponseInfo.class);
        if (CollUtil.isNotEmpty(infos)) {
            try {
                UploadResponseInfo info = infos.get(0);
                byte[] bytes = this.getImageDataBytes(info);
                XEasyPdfImageType imageType = XEasyPdfImageType.PNG;
                // 目前先仅仅支持这两种图片格式，更多可以自己手动修改代码扩充。
                if (StrUtil.endWithIgnoreCase(info.getFilename(), "jpg")) {
                    imageType = XEasyPdfImageType.JPEG;
                }
                cell = this.createImageCell(bytes, imageType, cellWidth, cellHeight, cellValue.getImageScale());
            } catch (HttpException e) {
                throw new MyRuntimeException(e);
            }
        } else {
            // 如果没有图片数据，就用空文本内容替代即可。
            XEasyPdfText text = XEasyPdfHandler.Text.build("");
            cell = XEasyPdfHandler.Table.Row.Cell.build(cellWidth, cellHeight).addContent(text);
        }
        return cell;
    }

    private byte[] createBarCodeBytes(
            ReportSheet sheet, ReportSheetCell reportCell, ReportSheetCellValue cellValue) throws IOException {
        float cellWidth = reportCell.getColWidth() * PDF_SCALE_DOWN;
        float cellHeight = sheet.getVisibleDataRow()[reportCell.getRowIndex()] * PDF_SCALE_DOWN;
        if (reportCell.isMergeCellTopLeft()) {
            cellWidth = this.calculateMergeCellWidth(sheet, reportCell);
            cellHeight = this.calculateMergeCellHeight(sheet, reportCell);
        }
        return this.createBarCodeBytes(cellWidth, cellHeight,
                cellValue.getBarCodeType(), cellValue.getBarCodeShowWords(), cellValue.getValue().toString());
    }

    private byte[] createBarCodeBytes(
            float width, float height, String barCodeType, Boolean barCodeShowWords, String value) throws IOException {
        XEasyPdfBarCode barCode = XEasyPdfHandler.BarCode.build(
                XEasyPdfBarCode.CodeType.valueOf(barCodeType), value);
        barCode.setWidth(width - 2);
        barCode.setHeight(height - 2);
        if (BooleanUtil.isTrue(barCodeShowWords)) {
            barCode.enableShowWords();
            barCode.setMaxWidth(width * BARCODE_IMAGE_SCALE);
            barCode.setMaxHeight(height * BARCODE_IMAGE_SCALE);
        }
        barCode.init();
        BufferedImage image = barCode.getBarCodeImage(barCode.createBitMatrix());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }

    private XEasyPdfCell makePdfBarcodeCell(ReportSheetCellValue cellValue, float cellWidth, float cellHeight) {
        XEasyPdfCell cell;
        if (ObjectUtil.isNotEmpty(cellValue.getValue())) {
            XEasyPdfBarCode barCode = XEasyPdfHandler.BarCode.build(
                    XEasyPdfBarCode.CodeType.valueOf(cellValue.getBarCodeType()), cellValue.getValue().toString());
            barCode.setWidth(cellWidth - 2);
            barCode.setHeight(cellHeight - 2);
            if (BooleanUtil.isTrue(cellValue.getBarCodeShowWords())) {
                barCode.enableShowWords();
                barCode.setMaxWidth(cellWidth * BARCODE_IMAGE_SCALE);
                barCode.setMaxHeight(cellHeight * BARCODE_IMAGE_SCALE);
            }
            cell = XEasyPdfHandler.Table.Row.Cell.build(cellWidth, cellHeight).addContent(barCode);
        } else {
            // 如果没有图片数据，就用空文本内容替代即可。
            XEasyPdfText text = XEasyPdfHandler.Text.build("");
            cell = XEasyPdfHandler.Table.Row.Cell.build(cellWidth, cellHeight).addContent(text);
        }
        return cell;
    }

    private void fillCellBorderInfo(ReportSheetCell reportCell, XEasyPdfCell cell) {
        if (!reportCell.isHasTopBorder()) {
            cell.disableTopBorder();
        }
        if (!reportCell.isHasBottomBorder()) {
            cell.disableBottomBorder();
        }
        if (!reportCell.isHasLeftBorder()) {
            cell.disableLeftBorder();
        }
        if (!reportCell.isHasRightBorder()) {
            cell.disableRightBorder();
        }
        if (StrUtil.isNotBlank(reportCell.getTopBorderColor())) {
            cell.setTopBorderColor(this.decodeColor(reportCell.getTopBorderColor()));
        }
        if (StrUtil.isNotBlank(reportCell.getBottomBorderColor())) {
            cell.setBottomBorderColor(this.decodeColor(reportCell.getBottomBorderColor()));
        }
        if (StrUtil.isNotBlank(reportCell.getLeftBorderColor())) {
            cell.setLeftBorderColor(this.decodeColor(reportCell.getLeftBorderColor()));
        }
        if (StrUtil.isNotBlank(reportCell.getRightBorderColor())) {
            cell.setRightBorderColor(this.decodeColor(reportCell.getRightBorderColor()));
        }
    }

    private void fillCellStyleInfo(ReportSheetCell reportCell, XEasyPdfCell cell) {
        if (StrUtil.isNotBlank(reportCell.getFontColor())) {
            cell.setFontColor(this.decodeColor(reportCell.getFontColor()));
        }
        if (StrUtil.isNotBlank(reportCell.getBackgroundColor())) {
            cell.setBackgroundColor(this.decodeColor(reportCell.getBackgroundColor()));
        }
        if (reportCell.isBold()) {
            cell.setDefaultFontStyle(XEasyPdfDefaultFontStyle.BOLD);
        }
        if (reportCell.getVerticalType().equals(0)) {
            cell.setVerticalStyle(XEasyPdfPositionStyle.CENTER);
        } else if (reportCell.getVerticalType().equals(1)) {
            cell.setVerticalStyle(XEasyPdfPositionStyle.TOP);
        } else {
            cell.setVerticalStyle(XEasyPdfPositionStyle.BOTTOM);
        }
        if (reportCell.getHorizontalType().equals(0)) {
            cell.setHorizontalStyle(XEasyPdfPositionStyle.CENTER);
        } else if (reportCell.getHorizontalType().equals(1)) {
            cell.setHorizontalStyle(XEasyPdfPositionStyle.LEFT);
        } else {
            cell.setHorizontalStyle(XEasyPdfPositionStyle.RIGHT);
        }
    }

    private Color decodeColor(String colorData) {
        if (colorData.startsWith("rgb(")) {
            colorData = colorData.substring(4, colorData.length() - 1);
            List<Integer> colorEl = StrUtil.splitTrim(colorData, ",")
                    .stream().map(Integer::valueOf).collect(Collectors.toList());
            return new Color(colorEl.get(0), colorEl.get(1), colorEl.get(2));
        }
        return Color.decode(colorData);
    }

    private void fillCellStyleInfo(HSSFWorkbook workbook, ReportSheetCell reportCell, CellStyle cellStyle) {
        HSSFFont font = workbook.createFont();
        if (reportCell.getFontSize() != null) {
            font.setFontHeightInPoints((short) reportCell.getFontSize().intValue());
        }
        font.setFontName("微软雅黑");
        font.setBold(reportCell.isBold());
        font.setItalic(reportCell.isItalic());
        font.setStrikeout(reportCell.isCancelLine());
        cellStyle.setFont(font);
        if (reportCell.isUnderline()) {
            font.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
        }
        font.setColor(this.decodeExcelCellColor(workbook, reportCell.getFontColor()));
        if (reportCell.getVerticalType() == 0) {
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        } else if (reportCell.getVerticalType() == 1) {
            cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        } else {
            cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        }
        if (reportCell.getHorizontalType() == 0) {
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
        } else if (reportCell.getHorizontalType() == 1) {
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
        } else {
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        }
        if (StrUtil.isNotBlank(reportCell.getBackgroundColor())) {
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(
                    this.decodeExcelCellColor(workbook, reportCell.getBackgroundColor()));
        }
    }

    private void fillCellBorderInfo(HSSFWorkbook workbook, ReportSheetCell reportCell, CellStyle cellStyle) {
        if (!reportCell.isHasTopBorder()) {
            cellStyle.setBorderTop(BorderStyle.NONE);
        } else {
            cellStyle.setBorderTop(BorderStyle.THIN);
        }
        if (StrUtil.isNotBlank(reportCell.getTopBorderColor())) {
            cellStyle.setTopBorderColor(
                    this.decodeExcelCellColor(workbook, reportCell.getTopBorderColor()));
        }
        if (!reportCell.isHasBottomBorder()) {
            cellStyle.setBorderBottom(BorderStyle.NONE);
        } else {
            cellStyle.setBorderBottom(BorderStyle.THIN);
        }
        if (StrUtil.isNotBlank(reportCell.getBottomBorderColor())) {
            cellStyle.setBottomBorderColor(
                    this.decodeExcelCellColor(workbook, reportCell.getBottomBorderColor()));
        }
        if (!reportCell.isHasLeftBorder()) {
            cellStyle.setBorderLeft(BorderStyle.NONE);
        } else {
            cellStyle.setBorderLeft(BorderStyle.THIN);
        }
        if (StrUtil.isNotBlank(reportCell.getLeftBorderColor())) {
            cellStyle.setLeftBorderColor(
                    this.decodeExcelCellColor(workbook, reportCell.getLeftBorderColor()));
        }
        if (!reportCell.isHasRightBorder()) {
            cellStyle.setBorderRight(BorderStyle.NONE);
        } else {
            cellStyle.setBorderRight(BorderStyle.THIN);
        }
        if (StrUtil.isNotBlank(reportCell.getRightBorderColor())) {
            cellStyle.setRightBorderColor(
                    this.decodeExcelCellColor(workbook, reportCell.getRightBorderColor()));
        }
    }

    private short decodeExcelCellColor(HSSFWorkbook workbook, String c) {
        if (StrUtil.isBlank(c)) {
            return HSSFColor.HSSFColorPredefined.BLACK.getIndex();
        }
        int[] color = new int[3];
        if (c.length() == 7) {
            color[0] = Integer.parseInt(c.substring(1, 3), 16);
            color[1] = Integer.parseInt(c.substring(3, 5), 16);
            color[2] = Integer.parseInt(c.substring(5, 7), 16);
        } else {
            color[0] = Integer.parseInt(c.substring(1, 2), 16);
            color[1] = Integer.parseInt(c.substring(2, 3), 16);
            color[2] = Integer.parseInt(c.substring(3, 4), 16);
        }
        HSSFPalette palette = workbook.getCustomPalette();
        return palette.findSimilarColor(color[0], color[1], color[2]).getIndex();
    }

    private XEasyPdfCell createImageCell(
            byte[] bytes, XEasyPdfImageType imageType, float cellWidth, float cellHeight, Boolean scale) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            int imageWidth = (int) cellWidth;
            int imageHeight = (int) cellHeight;
            if (BooleanUtil.isFalse(scale)) {
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
                imageWidth = bufferedImage.getWidth();
                imageHeight = bufferedImage.getHeight();
            }
            XEasyPdfImage image = XEasyPdfHandler.Image.build(is, imageType, imageWidth, imageHeight);
            return XEasyPdfHandler.Table.Row.Cell.build(cellWidth, cellHeight).addContent(image);
        } catch (IOException e) {
            throw new MyRuntimeException(e);
        }
    }

    private float calculateMergeCellWidth(ReportSheet sheet, ReportSheetCell cell) {
        float cellWidth = 0;
        for (int i = 0; i < cell.getMergeCellColSpan(); i++) {
            cellWidth += sheet.getVisibleDataColumn()[i + cell.getMergeCellLeftCol()];
        }
        return cellWidth * PDF_SCALE_DOWN;
    }

    private float calculateMergeCellHeight(ReportSheet sheet, ReportSheetCell cell) {
        float cellHeight = 0;
        for (int i = 0; i < cell.getMergeCellRowSpan(); i++) {
            cellHeight += sheet.getVisibleDataRow()[i + cell.getMergeCellTopRow()];
        }
        return cellHeight * PDF_SCALE_DOWN;
    }
}
