import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { TableData } from '@/common/types/table';
import { API_CONTEXT } from '../config';

export default class ReportOperationController extends BaseController {
  static listDataWithGroup(params: ANY_OBJECT, httpOptions?: RequestOption) {
    const url = API_CONTEXT + '/report/reportOperation/listData/' + params.pageCode;
    return super.post<TableData<ANY_OBJECT>>(url, params, httpOptions);
  }

  static previewDataWithGroup(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<ANY_OBJECT>(
      API_CONTEXT + '/report/reportOperation/previewData',
      params,
      httpOptions,
    );
  }
}
